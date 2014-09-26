package pt.utl.ist.accessPoint.manager;

import org.dom4j.DocumentException;

import pt.utl.ist.accessPoint.AccessPoint;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.oai.OaiListResponse;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.util.Urn;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 31-03-2011
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */

public interface AccessPointsManager {

    public void processRecord(DataSource dataSource, RecordRepox record,File logFile) throws IOException, SQLException;

    public void processRecords(DataSource dataSource, List<RecordRepox> records, File logFile) throws IOException, SQLException;

    public boolean deleteRecord(Urn recordUrn) throws IOException, DocumentException, SQLException;

    public boolean removeRecord(Urn recordUrn) throws IOException, DocumentException, SQLException;

    public abstract void initialize(HashMap<String, DataSourceContainer> dataSourceContainers) throws SQLException;

    public abstract void updateDataSourceAccessPoint(DataSource dataSource, Class typeOfIndex,
                                                     String oldAccessPointId, String newAccessPointId) throws SQLException;

    public abstract void deleteIndex(AccessPoint accessPoint) throws SQLException;

    public abstract void emptyIndex(DataSource dataSource, AccessPoint accessPoint) throws SQLException;

    public abstract Collection getIdsFromDataSource(DataSource dataSource, String fromDate, String toDate, Integer offset, Integer numberResults) throws SQLException;

    public abstract OaiListResponse.OaiItem getRecord(Urn urnOfRecord) throws IOException, DocumentException, SQLException;

    public abstract String getRecordTimestamp(Urn urnOfRecord) throws SQLException, IOException, DocumentException;

    public abstract OaiListResponse getOaiRecordsFromDataSource(DataSource dataSource, String fromDate, String toDate,
                                                                Integer offset, int numberResults, boolean headersOnly) throws SQLException, IOException;

    public abstract int[] getRecordCountLastrowPair(DataSource dataSource, Integer fromRow, String fromDate, String toDate) throws SQLException;
}
