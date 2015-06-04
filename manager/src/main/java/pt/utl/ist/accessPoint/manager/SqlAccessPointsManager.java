package pt.utl.ist.accessPoint.manager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import pt.utl.ist.accessPoint.AccessPoint;
import pt.utl.ist.accessPoint.RecordRepoxFullAccessPoint;
import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.database.DatabaseAccess;
import pt.utl.ist.oai.OaiListResponse;
import pt.utl.ist.oai.OaiListResponse.OaiItem;
import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.reports.LogUtil;
import pt.utl.ist.util.TimeUtil;
import pt.utl.ist.util.Urn;
import pt.utl.ist.util.ZipUtil;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.util.sql.SqlUtil;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * Implementation of an AccessPointsManagerDefault based on a Sql database
 *
 * @author Nuno Freire
 *
 */
public class SqlAccessPointsManager extends DefaultAccessPointsManager {

    private static final Logger log = Logger.getLogger(SqlAccessPointsManager.class);

    protected DatabaseAccess    databaseAccess;

    /**
     * Creates a new instance of this class.
     *
     * @param databaseAccess
     */
    public SqlAccessPointsManager(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    private void initDataSource(Connection con, DataSource dataSource, boolean isUpdate) {
        String idType = databaseAccess.getVarType(dataSource.getClassOfLocalId());

        for (AccessPoint ap : dataSource.getAccessPoints().values()) {
            String table = ap.getId().toLowerCase();

            if (!tableExists(table, con)) {
                // test if the table exists with the old name (without REPOX internal prefix)
                if (tableExists(table.substring(AccessPoint.PREFIX_INTERNAL_BD.length()), con)) {
                    // rename the access point
                    AccessPoint defaultTimestampAP = dataSource.getAccessPoints().get(AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD);
                    AccessPoint defaultRecordAP = dataSource.getAccessPoints().get(AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_RECORD_INTERNAL_BD);

                    try {
                        updateDataSourceAccessPoint(dataSource, defaultTimestampAP.typeOfIndex(), dataSource.getId() + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD,
                                AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD);
                        updateDataSourceAccessPoint(dataSource, defaultRecordAP.typeOfIndex(), dataSource.getId() + AccessPoint.SUFIX_RECORD_INTERNAL_BD,
                                AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_RECORD_INTERNAL_BD);
                    } catch (SQLException e) {
                        log.error(this.getClass() + ": " + e.getMessage());
                        e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
                    }
                } else {
                    String valueType = databaseAccess.getVarType(ap.typeOfIndex());
                    boolean indexValue = getIsValueIndexable(ap.typeOfIndex());

                    databaseAccess.createTableIndexes(con, idType, table, valueType, indexValue);
                }
            }
        }
    }

    private void renameTableIndexes(Connection con, boolean indexValue, String oldTableName, String newTableName) {
        String renameTableQuery = databaseAccess.renameTableString(oldTableName, newTableName);
        log.info(renameTableQuery);
        SqlUtil.runUpdate(renameTableQuery, con);

        databaseAccess.renameIndexString(con, newTableName, oldTableName, indexValue);
    }

    private boolean tableExists(String table, Connection con) {
        return databaseAccess.checkTableExists(table, con);
    }

    private void setStatementParameter(PreparedStatement insStatement, Object value, int paramCounter, Class classOfParameter) throws SQLException {
        if (classOfParameter.equals(String.class)) {
            String valString = (String)value;
            insStatement.setString(paramCounter, (valString.length() > 255 ? valString.substring(0, 255) : valString));
        } else if (classOfParameter.equals(Integer.class)) {
            insStatement.setInt(paramCounter, (Integer)value);
        } else if (classOfParameter.equals(Date.class)) {
            insStatement.setDate(paramCounter, new java.sql.Date((((Date)value).getTime())));
        } else if (classOfParameter.equals(byte[].class)) {
            insStatement.setBytes(paramCounter, (byte[])value);
        } else {
            throw new RuntimeException("Index type not implemented");
        }
    }

    private List<Object[]> getTripleArraysToInsert(String identifier, boolean isDeleted, Object value) {
        Integer isDeletedInteger = (isDeleted ? 1 : 0);
        List<Object[]> allValues = new ArrayList<Object[]>();
        if (value instanceof List) {
            List valueList = (List)value;
            for (Object aValueList : valueList) {
                allValues.add(new Object[] { identifier, isDeletedInteger, aValueList });
            }
        } else {
            allValues.add(new Object[] { identifier, isDeletedInteger, value });
        }

        return allValues;
    }

    private Collection getFieldFromDataSource(DataSource dataSource, String fromDateString, String toDateString, Integer offset, Integer numberResults, String field, Class itemClass)
            throws SQLException {
        if (offset == null || offset < 0) {
            offset = 0;
        }
        boolean noResultLimit = (numberResults == null || numberResults <= 0);

        Connection con = databaseAccess.openDbConnection();
        try {
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            try {
                if (!noResultLimit) {
                    stmt.setMaxRows(numberResults + offset);
                }
                String query = databaseAccess.getFieldQuery(dataSource, fromDateString, toDateString, offset, numberResults, field);

                log.debug(query);

                ResultSet rs = stmt.executeQuery(query);
                try {
                    rs.absolute(offset);
                    ArrayList allResults = new ArrayList();
                    int counter = 0;

                    while (rs.next()) {
                        if (noResultLimit || counter <= numberResults) {
                            if (itemClass.equals(String.class)) {
                                allResults.add(rs.getString(1));
                            } else if (itemClass.equals(byte[].class)) {
                                allResults.add(rs.getBytes(1));
                            } else if (itemClass.equals(Date.class)) {
                                allResults.add(rs.getDate(1));
                            } else {
                                throw new SQLException("Unsupported class: " + itemClass.getName());
                            }
                        }
                        counter++;
                    }
                    return allResults;
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            con.close();
        }
    }

    private String getField(Urn urnOfRecord, String tableSuffix, Class classofField) throws SQLException, IOException, DocumentException {
        Connection con = databaseAccess.openDbConnection();

        try {
            Statement stmt = con.createStatement();
            try {
                String recordTable = (urnOfRecord.getDataSourceId() + tableSuffix).toLowerCase();
                String query = "select " + recordTable.toLowerCase() + ".value from " + recordTable.toLowerCase() + " where " + recordTable.toLowerCase() + ".nc = '" + urnOfRecord.getRecordId() + "'";

                log.debug(query);
                ResultSet rs = stmt.executeQuery(query);
                try {
                    String returnString = null;
                    if (rs.next()) {
                        if (classofField.equals(Date.class)) {
                            returnString = new SimpleDateFormat(TimeUtil.SHORT_DATE_FORMAT).format(new Date(rs.getDate(1).getTime()));
                        } else if (classofField.equals(byte[].class)) {
                            returnString = new String(rs.getBytes(1), "UTF-8");
                        }
                    }
                    return returnString;
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            con.close();
        }
    }

    private OaiListResponse getHeaderAndRecordFromDataSource(DataSource dataSource, String fromDateString, String toDateString, Integer offset, Integer numberResults, boolean retrieveFullRecord,
            Class itemClass) throws SQLException, IOException {
        OaiListResponse oaiListResponse = new OaiListResponse();

        boolean noResultLimit = (numberResults == null || numberResults <= 0);

        Connection con = databaseAccess.openDbConnection();
        try {
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            try {
                if (!noResultLimit) {
                    stmt.setMaxRows(numberResults);
                }

                String query = databaseAccess.getHeaderAndRecordQuery(dataSource, fromDateString, toDateString, offset, numberResults, retrieveFullRecord);

                log.info(query);

                ResultSet rs = stmt.executeQuery(query);
                try {
                    oaiListResponse.setOaiItems(new ArrayList<OaiItem>());
                    while (rs.next()) {
                        byte[] recordBytes = (retrieveFullRecord && rs.getBytes(5) != null ? ZipUtil.unzip(rs.getBytes(5)) : null);

                        boolean recordDeleted = (rs.getInt(2) == 1);
                        oaiListResponse.getOaiItems().add(
                                oaiListResponse.new OaiItem(rs.getObject(1).toString(), DateFormatUtils.format(rs.getDate(3), TimeUtil.SHORT_DATE_FORMAT), dataSource.getId(), recordDeleted,
                                        recordBytes));
                    }

                    if (rs.previous() && rs.previous()) { // get the result before last
                        int lastIdentifier = rs.getInt(4);
                        oaiListResponse.setLastRequestedIdentifier(lastIdentifier);
                    }
                    return oaiListResponse;
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return oaiListResponse;
        } finally {
            con.close();
        }
    }

    @Override
    public void initialize(HashMap<String, DataSourceContainer> dataSourceContainers) throws SQLException {
        Connection con = databaseAccess.openDbConnection();
        if (con == null) {
            return;
        }

        try {
            for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
                initDataSource(con, dataSourceContainer.getDataSource(), false);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            con.close();
        }
    }

    @Override
    public void updateDataSourceAccessPoint(DataSource dataSource, Class typeOfIndex, String oldAccessPointId, String newAccessPointId) throws SQLException {
        Connection con = databaseAccess.openDbConnection();
        if (con == null) {
            return;
        }

        try {
            String idType = databaseAccess.getVarType(dataSource.getClassOfLocalId());
            String valueType = databaseAccess.getVarType(typeOfIndex);
            boolean indexValue = getIsValueIndexable(typeOfIndex);

            String oldTableName = oldAccessPointId.toLowerCase();
            String newTableName = newAccessPointId.toLowerCase();

            if (!tableExists(oldTableName, con)) {
                databaseAccess.createTableIndexes(con, idType, newTableName, valueType, indexValue);
            } else {
                renameTableIndexes(con, indexValue, oldTableName, newTableName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            con.close();
        }
    }

    @Override
    public void emptyIndex(DataSource dataSource, AccessPoint accessPoint) throws SQLException {
        Connection con = databaseAccess.openDbConnection();
        if (con == null) {
            return;
        }

        try {
            String table = accessPoint.getId().toLowerCase();

            if (tableExists(table, con)) {
                deleteIndex(accessPoint);
            }

            String idType = databaseAccess.getVarType(dataSource.getClassOfLocalId());
            String valueType = databaseAccess.getVarType(accessPoint.typeOfIndex());
            boolean indexValue = getIsValueIndexable(accessPoint.typeOfIndex());

            databaseAccess.createTableIndexes(con, idType, table, valueType, indexValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            con.close();
        }
    }

    @Override
    public void deleteIndex(AccessPoint accessPoint) throws SQLException {
        Connection con = databaseAccess.openDbConnection();
        if (con == null) {
            return;
        }

        try {
            String table = accessPoint.getId().toLowerCase();
            boolean tableExists = tableExists(table, con);

            if (tableExists) {
                databaseAccess.deleteTable(con, table);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            con.close();
        }
    }

    @Override
    protected void updateIndex(RecordRepox record, Collection values, AccessPoint accessPoint, File logFile) throws SQLException, IOException {
        Connection con = databaseAccess.openDbConnection();
        if (con == null) {
            return;
        }

        String dataSourceId = getDataSourceIdFromAccessPoint(accessPoint.getId().toLowerCase());

        TimeUtil.getTimeSinceLastTimerArray(8);
        try {
            String delim = ((record.getId() instanceof Integer || record.getId() instanceof Long) ? "" : "'");
            String recId = delim + record.getId() + delim;
            String table = accessPoint.getId().toLowerCase();

            String query = "delete from " + table + " where nc = " + recId;

            PreparedStatement delPs = con.prepareStatement(query);
            try {
                boolean marcAsDeleted = false;
                if (accessPoint instanceof RecordRepoxFullAccessPoint) {
                    // check if record exists and if it marked as deleted
                    marcAsDeleted = isRecordMarkedAsDeleted(con, table, (String)record.getId());
                }
                int result = delPs.executeUpdate();

                if (accessPoint instanceof RecordRepoxFullAccessPoint && result > 0) {
                    // update the number of records
                    LogUtil.addDuplicateRecordCount((String)record.getId(), logFile);
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().updateEraseRecordsCount(dataSourceId, 1, marcAsDeleted ? 1 : 0);
                }
            } finally {
                delPs.close();
            }

            if (values.size() > 0) {
                int deletedInt = (record.isDeleted() ? 1 : 0);
                String statementString = "insert into " + table + "(nc, deleted, value) values (" + recId + ", " + deletedInt + ", ?)";
                PreparedStatement insPs = con.prepareStatement(statementString);

                try {
                    for (Object val : values) {
                        log.debug(statementString + "; value = " + val);

                        insPs.clearParameters();
                        if (accessPoint.typeOfIndex().equals(String.class)) {
                            if (((String)val).length() > 255) {
                                insPs.setString(1, ((String)val).substring(0, 255));
                            } else {
                                insPs.setString(1, (String)val);
                            }
                        } else if (accessPoint.typeOfIndex().equals(Integer.class)) {
                            insPs.setInt(1, (Integer)val);
                        } else if (accessPoint.typeOfIndex().equals(Date.class)) {
                            insPs.setDate(1, new java.sql.Date(((Date)val).getTime()));
                        } else if (accessPoint.typeOfIndex().equals(byte[].class)) {
                            byte[] valueToInsert = ZipUtil.zip((byte[])val);
                            insPs.setBytes(1, valueToInsert);
                        } else {
                            throw new RuntimeException("Index type not implemented");
                        }

                        try {
                            TimeUtil.getTimeSinceLastTimerArray(9);
                            insPs.executeUpdate();
                            insPs.clearParameters();
                        } catch (SQLException e) {
                            log.error(e.getMessage() + "rec: " + recId + " table: " + table + " value:" + val.toString(), e);
                            throw e;
                        }
                    }
                    try {
                        // update the number of records
                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSourceId, true);
                    } catch (DocumentException e) {
                        log.error(e.getMessage());
                    }
                } finally {
                    insPs.close();
                }
            }
        } finally {
            con.close();
        }
    }

    private String getDataSourceIdFromAccessPoint(String accessPointId) {
        if (accessPointId.endsWith(AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD)) {
            return accessPointId.substring(AccessPoint.PREFIX_INTERNAL_BD.length(), accessPointId.length() - AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD.length());
        } else {
            return accessPointId.substring(AccessPoint.PREFIX_INTERNAL_BD.length(), accessPointId.length() - AccessPoint.SUFIX_RECORD_INTERNAL_BD.length());
        }
    }

    @Override
    protected void updateIndex(DataSource dataSource, List<RecordRepox> records, List values, AccessPoint accessPoint, File logFile) throws SQLException, IOException {
        Connection con = databaseAccess.openDbConnection();
        if (con == null) {
            return;
        }

        if (records == null || records.isEmpty()) {
            return;
        }

        try {
            TimeUtil.getTimeSinceLastTimerArray(5);
            //String delim = ((records.get(0).getId() instanceof Integer || records.get(0).getId() instanceof Long) ? "" : "'");
            String table = accessPoint.getId().toLowerCase();

            /* DELETE old indexes */
            // If Data Source has ids extracted or is IdProvided, the ids are extracted from the records and need
            // to be deleted before being inserted again (for performance: it's faster than updating)
            if (dataSource.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy || dataSource.getRecordIdPolicy() instanceof IdProvidedRecordIdPolicy/*
                                                                                                                                                         * ||
                                                                                                                                                         * dataSource
                                                                                                                                                         * instanceof
                                                                                                                                                         * DataSourceOai
                                                                                                                                                         */) {
                String deleteQuery = "delete from " + table + " where nc = ?";

                PreparedStatement delStatement = con.prepareStatement(deleteQuery);

                try {
                    for (RecordRepox record : records) {
                        boolean marcAsDeleted = false;
                        if (accessPoint instanceof RecordRepoxFullAccessPoint) {
                            // check if record exists and if it marked as deleted
                            marcAsDeleted = isRecordMarkedAsDeleted(con, table, (String)record.getId());
                        }

                        delStatement.setString(1, (String)record.getId());
                        int result = delStatement.executeUpdate();
                        delStatement.clearParameters();

                        if (accessPoint instanceof RecordRepoxFullAccessPoint && result > 0) {
                            //                            LogUtil.addEmptyRecordCount(recordId, logFile);
                            LogUtil.addDuplicateRecordCount((String)record.getId(), logFile);
                            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().updateEraseRecordsCount(dataSource.getId(), 1, marcAsDeleted ? 1 : 0);
                        }
                    }
                } finally {
                    delStatement.close();
                }
            }

            /* INSERT new indexes */
            List<Object[]> pairsForInsertion = new ArrayList<Object[]>();
            for (int i = 0; i < records.size(); i++) {
                Object valueToInsert;

                if (values.get(i) != null && values.get(i).getClass().equals(byte[].class)) {
                    valueToInsert = ZipUtil.zip((byte[])values.get(i));
                } else {
                    valueToInsert = values.get(i);
                }
                pairsForInsertion.addAll(getTripleArraysToInsert((String)records.get(i).getId(), records.get(i).isDeleted(), valueToInsert));
            }

            String pairsValues = "(?, ?, ?)";
            for (int i = 1; i < pairsForInsertion.size(); i++) {
                pairsValues += ", (?, ?, ?)";
            }

            String insStatementString = "insert into " + table + "(nc, deleted, value) values " + pairsValues;

            PreparedStatement insStatement = con.prepareStatement(insStatementString);
            try {
                int paramCounter = 1;
                for (Object[] aPairsForInsertion : pairsForInsertion) {
                    setStatementParameter(insStatement, aPairsForInsertion[0], paramCounter, String.class);
                    paramCounter++;
                    setStatementParameter(insStatement, aPairsForInsertion[1], paramCounter, Integer.class);
                    paramCounter++;
                    setStatementParameter(insStatement, aPairsForInsertion[2], paramCounter, accessPoint.typeOfIndex());
                    paramCounter++;
                }

                double prepareTime = TimeUtil.getTimeSinceLastTimerArray(5) / 1000.0;
                insStatement.executeUpdate();
                insStatement.clearParameters();
                double executeTime = TimeUtil.getTimeSinceLastTimerArray(5) / 1000.0;
                double totalTime = prepareTime + executeTime;
                log.info(accessPoint.getId() + " DB statement prepared/executed/total (s) " + prepareTime + "/" + executeTime + "/" + totalTime);
            } finally {
                insStatement.close();
            }
        } finally {
            con.close();
        }
    }

    private boolean isRecordMarkedAsDeleted(Connection con, String table, String id) {
        String query = "select " + table + ".deleted from " + table + " where " + table + ".nc = '" + id + "'";

        try {
            Statement stmt = con.createStatement();
            try {
                ResultSet rs = stmt.executeQuery(query);
                try {
                    if (rs.next()) {
                        if (rs.getInt(1) == 1) {
                            return true;
                        }
                    }
                    return false;
                } catch (SQLException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    return false;
                } finally {
                    rs.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    protected boolean deleteFromIndex(Urn recordUrn, AccessPoint accessPoint) throws DocumentException, SQLException, IOException, ObjectNotFoundException {
        Connection con = databaseAccess.openDbConnection();
        if (con == null) {
            return false;
        }
        try {
            String delim = ((recordUrn.getRecordId() instanceof Integer || recordUrn.getRecordId() instanceof Long) ? "" : "'");
            String recId = delim + recordUrn.getRecordId() + delim;
            String table = accessPoint.getId().toLowerCase();

            // test if record is already marked as deleted
            String query = "select deleted from " + table + " where nc=" + recId;
            log.debug("select query: " + query);

            PreparedStatement selectPs = con.prepareStatement(query);
            try {
                ResultSet rsMaxRow = selectPs.executeQuery();
                try {
                    if (rsMaxRow.next()) {
                        if (rsMaxRow.getInt(1) == 1) {
                            return false;
                        }
                    }
                    else
                        throw new ObjectNotFoundException("Record with id: " + recordUrn.getRecordId() + " does NOT exist!");
                } finally {
                    rsMaxRow.close();
                }
            } finally {
                selectPs.close();
            }

            // mark record as deleted
            query = "update " + table + " set deleted = 1 where nc = " + recId;
            log.debug("delete query: " + query);

            PreparedStatement delPs = con.prepareStatement(query);
            try {
                delPs.executeUpdate();

                return true;
            } finally {
                delPs.close();
            }
        } finally {
            con.close();
        }
    }

    @Override
    protected boolean removeFromIndex(Urn recordUrn, AccessPoint accessPoint) throws SQLException, IOException, DocumentException, ObjectNotFoundException {
        Connection con = databaseAccess.openDbConnection();
        if (con == null) {
            return false;
        }
        try {
            String delim = ((recordUrn.getRecordId() instanceof Integer || recordUrn.getRecordId() instanceof Long) ? "" : "'");
            String recId = delim + recordUrn.getRecordId() + delim;
            String table = accessPoint.getId().toLowerCase();

            // test if record is already marked as deleted
            String query = "select deleted from " + table + " where nc=" + recId;
            log.debug("select query: " + query);

            PreparedStatement selectPs = con.prepareStatement(query);
            try {
                ResultSet rsMaxRow = selectPs.executeQuery();
                try {
                    if (!rsMaxRow.next()) {
                        throw new ObjectNotFoundException("Record with id: " + recordUrn.getRecordId() + " does NOT exist!");
                    }

                    query = "delete from " + table + " where nc = " + recId;
                    log.debug("remove query: " + query);

                    PreparedStatement delPs = con.prepareStatement(query);
                    try {
                        delPs.executeUpdate();
                        return true;
                    } finally {
                        delPs.close();
                    }
                } finally {
                    rsMaxRow.close();
                }
            } finally {
                selectPs.close();
            }
        } finally {
            con.close();
        }
    }

    @Override
    public int[] getRecordCountLastrowPair(DataSource dataSource, Integer fromRow, String fromDate, String toDate) throws SQLException {
        int[] countLastrowPair = { 0, 0, 0 };
        // todo check ... stops here a lot during REPOX execution
        String recordTable = AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_RECORD_INTERNAL_BD;
        String timestampTable = AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD;
        Connection con = databaseAccess.openDbConnection();

        if(!tableExists(AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_RECORD_INTERNAL_BD, con) || !tableExists(AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD, con))
        	return new int[] { 0, 0, 0};
        	
	        try {
	            TimeUtil.startTimers();
	            TimeUtil.getTimeSinceLastTimerArray(1);
	
	            String maxRowQuery = "select max(" + recordTable.toLowerCase() + ".id) from " + recordTable.toLowerCase();
	            String countQuery = "select count(" + recordTable.toLowerCase() + ".nc) from " + recordTable.toLowerCase();
	            String deletedQuery = "select count(" + recordTable.toLowerCase() + ".id) from " + recordTable.toLowerCase() + " where deleted > 0";
	
	            String fromQueryHelper = "";
	            if (fromDate != null || toDate != null) {
	                fromQueryHelper += ", " + timestampTable.toLowerCase() + " where " + recordTable.toLowerCase() + ".nc = " + timestampTable.toLowerCase() + ".nc";
	                if (fromDate != null) {
	                    fromQueryHelper += " and " + timestampTable.toLowerCase() + ".value >= '" + fromDate + "'";
	                }
	                if (toDate != null) {
	                    fromQueryHelper += " and " + timestampTable.toLowerCase() + ".value <= '" + toDate + "'";
	                }
	            }
	
	            maxRowQuery += fromQueryHelper;
	            if (fromQueryHelper.contains("where")) {
	                deletedQuery = deletedQuery.replace(" where deleted > 0", "");
	                deletedQuery += fromQueryHelper + " and " + recordTable.toLowerCase() + ".deleted > 0";
	            } else {
	                deletedQuery += fromQueryHelper;
	            }
	
	            //			countLastrowPair[1] = SqlUtil.getCount(maxQuery, con);
	            Statement statementMaxRow = con.createStatement();
	            try {
	                ResultSet rsMaxRow = statementMaxRow.executeQuery(maxRowQuery);
	                try {
	                    if (rsMaxRow.next()) {
	                        countLastrowPair[1] = rsMaxRow.getInt(1);
	                    }
	                    log.debug(maxRowQuery + ": " + TimeUtil.getTimeSinceLastTimerArray(1) + "ms");
	                } finally {
	                    rsMaxRow.close();
	                }
	            } finally {
	                statementMaxRow.close();
	            }
	
	            Statement deletedRow = con.createStatement();
	            try {
	                ResultSet rsDeletedRow = deletedRow.executeQuery(deletedQuery);
	                try {
	                    if (rsDeletedRow.next()) {
	                        countLastrowPair[2] = rsDeletedRow.getInt(1);
	                    }
	
	                    if (fromRow == null || countLastrowPair[1] > fromRow) {
	                        countQuery += fromQueryHelper;
	                        countQuery += (fromQueryHelper.length() > 0 ? " and " : " where ") + (fromRow != null ? recordTable.toLowerCase() + ".id > " + fromRow + " and " : "") + recordTable
	                                .toLowerCase() + ".id <= " + countLastrowPair[1];
	
	                        Statement statementCount = con.createStatement();
	                        ResultSet rsCount = statementCount.executeQuery(countQuery);
	                        if (rsCount.next()) {
	                            countLastrowPair[0] = rsCount.getInt(1);
	                        }
	                        log.info(countQuery + ": " + TimeUtil.getTimeSinceLastTimerArray(1) + "ms");
	                    }
	
	                    return countLastrowPair;
	                } finally {
	                    rsDeletedRow.close();
	                }
	            } finally {
	                deletedRow.close();
	            }
	        } catch (Exception e) {
	            log.error("ERROR Counting records", e);
	            return new int[] { 0, 0, 0};
	        } finally {
	            con.close();
	        }
    }

    @Override
    public Collection getIdsFromDataSource(DataSource dataSource, String fromDate, String toDate, Integer offset, Integer numberResults) throws SQLException {
        return getFieldFromDataSource(dataSource, fromDate, toDate, offset, numberResults, "nc", String.class);
    }

    @Override
    public OaiItem getRecord(Urn urnOfRecord) throws IOException, DocumentException, SQLException, ObjectNotFoundException {
        DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(urnOfRecord.getDataSourceId());
        if (dataSourceContainer == null)
            throw new ObjectNotFoundException("DataSource with id " + urnOfRecord.getDataSourceId() + " NOT found!");
        DataSource dataSource = dataSourceContainer.getDataSource();

        String recordTable = AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_RECORD_INTERNAL_BD;
        String timestampTable = AccessPoint.PREFIX_INTERNAL_BD + dataSource.getId() + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD;

        Connection con = databaseAccess.openDbConnection();
        try {
            String query = "select " + recordTable.toLowerCase() + ".nc, " + timestampTable.toLowerCase() + ".deleted" + ", " + timestampTable.toLowerCase() + ".value" + ", " + recordTable
                    .toLowerCase() + ".value";

            query += " from " + recordTable.toLowerCase() + ", " + timestampTable.toLowerCase() + " where " + recordTable.toLowerCase() + ".nc = ?" + " and " + recordTable.toLowerCase() + ".nc = " + timestampTable
                    .toLowerCase() + ".nc";

            PreparedStatement preparedStatement = con.prepareStatement(query);
            try {
                preparedStatement.setString(1, urnOfRecord.getRecordId().toString());
                log.info(preparedStatement);

                ResultSet rs = preparedStatement.executeQuery();
                try {
                    OaiItem item = null;
                    if (rs.next()) {
                        byte[] recordBytes = (rs.getBytes(4) != null ? ZipUtil.unzip(rs.getBytes(4)) : null);
                        boolean recordDeleted = (rs.getInt(2) == 1);
                        OaiListResponse oaiListResponse = new OaiListResponse();
                        item = oaiListResponse.new OaiItem(rs.getObject(1).toString(), DateFormatUtils.format(rs.getDate(3), TimeUtil.SHORT_DATE_FORMAT), dataSource.getId(), recordDeleted,
                                recordBytes);
                    }
                    return item;
                } finally {
                    rs.close();
                }
            } finally {
                preparedStatement.close();
            }
        } finally {
            con.close();
        }
    }

    @Override
    public String getRecordTimestamp(Urn urnOfRecord) throws IOException, DocumentException, SQLException {
        return getField(urnOfRecord, AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD, Date.class);
    }

    @Override
    public OaiListResponse getOaiRecordsFromDataSource(DataSource dataSource, String fromDate, String toDate, Integer offset, int numberResults, boolean headersOnly) throws SQLException, IOException {
        if (headersOnly) {
            return getHeaderAndRecordFromDataSource(dataSource, fromDate, toDate, offset, numberResults, false, String.class);
        } else {
            return getHeaderAndRecordFromDataSource(dataSource, fromDate, toDate, offset, numberResults, true, byte[].class);

        }
    }

    @Override
    public void shutdown() throws SQLException {
        /// nothing to do here
    }

    @Override
    public Collection<OaiItem> getRecords(Urn... urnOfRecords) throws IOException, DocumentException, SQLException, ObjectNotFoundException {
        List<OaiItem> items = new ArrayList<>();
        for (Urn urnOfRecord : urnOfRecords) {
            items.add(getRecord(urnOfRecord));
        }
        return items;
    }
}
