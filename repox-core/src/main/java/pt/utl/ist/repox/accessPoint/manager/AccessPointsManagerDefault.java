/*
 * Created on 23/Mar/2006
 *
 */
package pt.utl.ist.repox.accessPoint.manager;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.oai.OaiListResponse;
import pt.utl.ist.repox.oai.OaiListResponse.OaiItem;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.Urn;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Implementations of this class manage the indexes for the AccessPoints. Know
 * implementation are: AccessPointsManagerSql
 * 
 * @author Nuno Freire
 */
public abstract class AccessPointsManagerDefault implements AccessPointsManager {
    private static final Logger log = Logger.getLogger(AccessPointsManagerDefault.class);

    /**
     * processes and saves the values for the AccessPoints of a RecordRepox
     * 
     * @param dataSource
     * @param record
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    @Override
    public void processRecord(DataSource dataSource, RecordRepox record, File logFile) throws IOException, SQLException {
        TimeUtil.getTimeSinceLastTimerArray(7);
        Urn recordNodeUrn = new Urn(dataSource.getId(), record.getId());
        log.debug("AP.processRecordNode.getUrn(): " + TimeUtil.getTimeSinceLastTimerArray(7));
        log.debug("AP.processRecordNode.getDataSource(): " + TimeUtil.getTimeSinceLastTimerArray(7));
        for (AccessPoint accessPoint : dataSource.getAccessPoints().values()) {
            Collection vals = accessPoint.index(record);
            log.debug("AP.processRecordNode...index(recordPackage): " + TimeUtil.getTimeSinceLastTimerArray(7));
            updateIndex(record, vals, accessPoint, logFile);
            log.debug("AP.processRecordNode...updateIndex(recordPackage, vals, aP): " + TimeUtil.getTimeSinceLastTimerArray(7));
        }
    }

    /**
     * processes and saves the values for the AccessPoints of a List of
     * RecordRepox records
     * 
     * @param dataSource
     * @param records
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    @Override
    public void processRecords(DataSource dataSource, List<RecordRepox> records, File logFile) throws IOException, SQLException {
        if (records == null || records.isEmpty()) { return; }
        TimeUtil.getTimeSinceLastTimerArray(5);
        if (dataSource.getAccessPoints().size() == 0) {
            log.error("AP.processRecords: does not store records in the DB - getAccessPoints().size()=0");
            //System.out.println("AP.processRecords: does not store records in the DB - getAccessPoints().size()=0");
        }
        for (AccessPoint accessPoint : dataSource.getAccessPoints().values()) {
            List vals = accessPoint.index(records);
            log.debug("AP.processRecords...index(recordPackage): " + TimeUtil.getTimeSinceLastTimerArray(7));

            updateIndex(dataSource, records, vals, accessPoint, logFile);

            log.debug("AP.processRecords...updateIndex(recordPackage, vals, aP): " + TimeUtil.getTimeSinceLastTimerArray(7));
        }
        log.debug("AP.processRecords time: " + TimeUtil.getTimeSinceLastTimerArray(5));
    }

    /**
     * Marks a Record as deleted in all indexes
     * 
     * @param recordUrn
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws org.dom4j.DocumentException
     */
    @Override
    public boolean deleteRecord(Urn recordUrn) throws IOException, DocumentException, SQLException {
        boolean result = true;
        DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(recordUrn.getDataSourceId()).getDataSource();
        for (AccessPoint accessPoint : dataSource.getAccessPoints().values()) {
            result = result & deleteFromIndex(recordUrn, accessPoint);
        }
        return result;
    }

    /**
     * Removes a Record from all indexes
     * 
     * @param recordUrn
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws org.dom4j.DocumentException
     */
    @Override
    public boolean removeRecord(Urn recordUrn) throws IOException, DocumentException, SQLException {
        boolean result = true;
        DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(recordUrn.getDataSourceId()).getDataSource();
        for (AccessPoint accessPoint : dataSource.getAccessPoints().values()) {
            result = result & removeFromIndex(recordUrn, accessPoint);
        }
        return result;
    }

    /**
     * @param classOfValue
     * @return boolean
     */
    protected boolean getIsValueIndexable(Class classOfValue) {
        return !classOfValue.equals(byte[].class);
    }

    /**
     * do some initialization tasks if necessary. This is called from the
     * RepoxManagerDefault when initializing
     * 
     * @param dataSourceContainers
     * @throws java.sql.SQLException
     */
    @Override
    public abstract void initialize(HashMap<String, DataSourceContainer> dataSourceContainers) throws SQLException;

    /**
     * Update a Data Source Access Point after the Data Source Id is changed
     * 
     * @param dataSource
     * @param typeOfIndex
     * @param oldAccessPointId
     * @param newAccessPointId
     * @throws java.sql.SQLException
     */
    @Override
    public abstract void updateDataSourceAccessPoint(DataSource dataSource, Class typeOfIndex, String oldAccessPointId, String newAccessPointId) throws SQLException;

    /**
     * Removes the index
     * 
     * @param accessPoint
     *            The AccessPoint definition
     * @throws SQLException
     */
    @Override
    public abstract void deleteIndex(AccessPoint accessPoint) throws SQLException;

    /**
     * Removes all the entries from the index
     * 
     * @param dataSource
     *            the DataSource from where the index originates
     * @param accessPoint
     *            the AccessPoint
     * @throws SQLException
     */
    @Override
    public abstract void emptyIndex(DataSource dataSource, AccessPoint accessPoint) throws SQLException;

    /**
     * Gets all Record IDs of a given DataSource starting in offset with size
     * numberResults
     * 
     * @param dataSource
     *            a DataSource
     * @param fromDate
     *            start date for search
     * @param toDate
     *            end date for search
     * @param offset
     *            the starting result (to start with the normal result use 0, to
     *            get the second result use 1). If this value is null or a
     *            negative value, it's considered 0
     * @param numberResults
     *            number of results returned. If this value is null, less than 0
     *            or 0, all results are returned
     * @return A collection of IDs
     * @throws SQLException
     */
    @Override
    public abstract Collection getIdsFromDataSource(DataSource dataSource, String fromDate, String toDate, Integer offset, Integer numberResults) throws SQLException;

    /**
     * Gets an OaiItem corresponding to a RecordRepox URN
     * 
     * @param urnOfRecord
     *            a URN identifying a Record
     * @return A Record String
     * @throws IOException
     * @throws java.sql.SQLException
     * @throws org.dom4j.DocumentException
     */
    @Override
    public abstract OaiItem getRecord(Urn urnOfRecord) throws IOException, DocumentException, SQLException;

    /**
     * Gets a Record Timestamp String corresponding to a URN
     * 
     * @param urnOfRecord
     *            a URN identifying a Record
     * @return A Record Timestamp String
     * @throws SQLException
     * @throws java.io.IOException
     * @throws org.dom4j.DocumentException
     */
    @Override
    public abstract String getRecordTimestamp(Urn urnOfRecord) throws SQLException, IOException, DocumentException;

    /**
     * Gets a Map of Records and respective update Dates of a given DataSource
     * starting in offset with size numberResults
     * 
     * @param dataSource
     *            a DataSource
     * @param fromDate
     *            start date for search
     * @param toDate
     *            end date for search
     * @param offset
     *            the starting result (to start with the normal result use 0, to
     *            get the second result use 1). If this value is null or a
     *            negative value, it's considered 0
     * @param numberResults
     *            maximum number of results returned.
     * @param headersOnly
     *            return only the headers of the records
     * 
     * @return A collection of Records
     * @throws SQLException
     * @throws java.io.IOException
     */
    @Override
    public abstract OaiListResponse getOaiRecordsFromDataSource(DataSource dataSource, String fromDate, String toDate, Integer offset, int numberResults, boolean headersOnly) throws SQLException, IOException;

    /**
     * Get a pair with count of records from a DataSource and the row number of
     * the last record
     * 
     * @param dataSource
     *            a DataSource
     * @param fromRow
     *            starting row for count
     * @param fromDate
     *            start date for count
     * @param toDate
     *            end date for count
     * @return The number of Records
     * @throws java.sql.SQLException
     */
    @Override
    public abstract int[] getRecordCountLastrowPair(DataSource dataSource, Integer fromRow, String fromDate, String toDate) throws SQLException;

    /**
     * Updates the index of record on an accessPoint
     * 
     * @param record
     * @param values
     *            the values to index
     * @param accessPoint
     *            The AccessPoint definition
     * @param logFile
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    protected abstract void updateIndex(RecordRepox record, Collection values, AccessPoint accessPoint, File logFile) throws SQLException, IOException;

    /**
     * Updates the index of a list of records on an accessPoint
     * 
     * @param dataSource
     *            a DataSource
     * @param records
     *            An URN identifying a RecordPackage
     * @param values
     *            the values to index
     * @param accessPoint
     *            The AccessPoint definition
     * @param logFile
     * @throws SQLException
     * @throws java.io.IOException
     * @throws java.io.IOException
     */
    protected abstract void updateIndex(DataSource dataSource, List<RecordRepox> records, List values, AccessPoint accessPoint, File logFile) throws SQLException, IOException;

    /**
     * Mark a record as deleted in an index
     * 
     * @param recordUrn
     *            An URN identifying a Record
     * @param accessPoint
     *            The AccessPoint definition
     * @return boolean
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws org.dom4j.DocumentException
     */
    protected abstract boolean deleteFromIndex(Urn recordUrn, AccessPoint accessPoint) throws DocumentException, SQLException, IOException;

    /**
     * Remove a record from an index
     * 
     * @param recordUrn
     *            An URN identifying a Record
     * @param accessPoint
     *            The AccessPoint definition
     * @return boolean
     * @throws java.io.IOException
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws org.dom4j.DocumentException
     */
    protected abstract boolean removeFromIndex(Urn recordUrn, AccessPoint accessPoint) throws SQLException, IOException, DocumentException;

}
