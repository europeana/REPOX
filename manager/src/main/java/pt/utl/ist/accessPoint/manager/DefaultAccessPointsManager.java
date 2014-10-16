package pt.utl.ist.accessPoint.manager;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import pt.utl.ist.accessPoint.AccessPoint;
import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.util.TimeUtil;
import pt.utl.ist.util.Urn;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Implementations of this class manage the indexes for the AccessPoints. Know
 * implementation are: AccessPointsManagerSql
 * 
 * @author Nuno Freire
 */
public abstract class DefaultAccessPointsManager implements AccessPointsManager {
    private static final Logger log = Logger.getLogger(DefaultAccessPointsManager.class);

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

    @Override
    public void processRecords(DataSource dataSource, List<RecordRepox> records, File logFile) throws IOException, SQLException {
        if (records == null || records.isEmpty()) { return; }
        TimeUtil.getTimeSinceLastTimerArray(5);
        if (dataSource.getAccessPoints().isEmpty()) {
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

    @Override
    public boolean deleteRecord(Urn recordUrn) throws IOException, DocumentException, SQLException {
        boolean result = true;
        DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(recordUrn.getDataSourceId()).getDataSource();
        for (AccessPoint accessPoint : dataSource.getAccessPoints().values()) {
            result = result & deleteFromIndex(recordUrn, accessPoint);
        }
        return result;
    }

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
