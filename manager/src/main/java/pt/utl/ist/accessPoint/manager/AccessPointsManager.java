package pt.utl.ist.accessPoint.manager;

import org.dom4j.DocumentException;

import pt.utl.ist.accessPoint.AccessPoint;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.oai.OaiListResponse;
import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.util.Urn;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import pt.utl.ist.oai.OaiListResponse.OaiItem;

/**
 * This interface defines access to the actual data.
 *
 * @author Gilberto Pedrosa
 * @author Nuno Freire
 */
public interface AccessPointsManager {

    /**
     * Do some initialization tasks if necessary. This is called from the
     * RepoxManagerDefault when initializing
     *
     * @param dataSourceContainers
     * @throws SQLException
     */
    void initialize(HashMap<String, DataSourceContainer> dataSourceContainers) throws SQLException;

    /**
     * Release and cleanup of resources
     *
     * @throws SQLException
     */
    void shutdown() throws SQLException;

    /**
     * Update a Data Source Access Point after the Data Source Id is changed
     *
     * @param dataSource
     * @param typeOfIndex
     * @param oldAccessPointId
     * @param newAccessPointId
     * @throws SQLException
     */
//    void update(DataSource dataSource, Class typeOfIndex, String oldAccessPointId, String newAccessPointId) throws AccessPointException;
    void updateDataSourceAccessPoint(DataSource dataSource, Class typeOfIndex,
            String oldAccessPointId, String newAccessPointId) throws SQLException;

    /**
     * Removes the storage for this access point
     *
     * @param accessPoint The AccessPoint definition
     * @throws SQLException
     */
//    void delete(AccessPoint accessPoint) throws AccessPointException;
    void deleteIndex(AccessPoint accessPoint) throws SQLException;

    /**
     * Removes all the entries from the storage for this access point
     *
     * @param dataSource
     * @param accessPoint the AccessPoint
     * @throws SQLException
     */
//    void truncate(DataSource dataSource, AccessPoint accessPoint) throws AccessPointException;
    void emptyIndex(DataSource dataSource, AccessPoint accessPoint) throws SQLException;

    /**
     * Processes and saves the values for the AccessPoints of a RecordRepox
     *
     * @param dataSource
     * @param record
     * @param logFile
     * @throws IOException
     * @throws SQLException
     */
    public void processRecord(DataSource dataSource, RecordRepox record, File logFile) throws IOException, SQLException;
//    void processRecord(DataSource dataSource, RecordRepox record, File logFile) throws AccessPointException;

    /**
     * Processes and saves the values for the AccessPoints of a List of
     * RecordRepox records
     *
     * @param dataSource
     * @param logFile
     * @param records
     * @throws IOException
     * @throws SQLException
     */
//    void processRecords(DataSource dataSource, File logFile, RecordRepox... records) throws AccessPointException;
    void processRecords(DataSource dataSource, List<RecordRepox> records, File logFile) throws IOException, SQLException;

    /**
     * Marks a Record as deleted in all indexes
     *
     * @param recordUrn
     * @return successful?
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
//    boolean deleteRecords(Urn... recordUrn) throws AccessPointException;
    boolean deleteRecord(Urn recordUrn) throws IOException, DocumentException, SQLException;

    /**
     * Removes a Record from all indexes
     *
     * @param recordUrn
     * @return successful?
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
//    boolean removeRecords(Urn... recordUrn) throws AccessPointException;
    boolean removeRecord(Urn recordUrn) throws IOException, DocumentException, SQLException;

    /**
     * Gets all Record IDs of a given DataSource starting in offset with size
     * numberResults
     *
     * @param dataSource a DataSource
     * @param fromDate start date for search
     * @param toDate end date for search
     * @param offset the starting result (to start with the normal result use 0,
     * to get the second result use 1). If this value is null or a negative
     * value, it's considered 0
     * @param numberResults number of results returned. If this value is null,
     * less than 0 or 0, all results are returned
     * @return A collection of IDs
     * @throws SQLException
     */
//    Collection<Object> getRecordIds(DataSource dataSource, String fromDate, String toDate, Integer offset, Integer numberResults) throws AccessPointException;
    Collection getIdsFromDataSource(DataSource dataSource, String fromDate, String toDate, Integer offset, Integer numberResults) throws SQLException;

    /**
     * Gets an OaiItem corresponding to a RecordRepox URN
     *
     * @param urnOfRecords a URN identifying a Record
     * @return records
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     * @throws ObjectNotFoundException 
     */
    Collection<OaiItem> getRecords(Urn... urnOfRecords) throws IOException, DocumentException, SQLException, ObjectNotFoundException;

    /**
     * Gets an OaiItem corresponding to a RecordRepox URN
     *
     * @param urnOfRecord a URN identifying a Record
     * @return records
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     * @throws ObjectNotFoundException 
     */
    OaiItem getRecord(Urn urnOfRecord) throws IOException, DocumentException, SQLException, ObjectNotFoundException;

    /**
     * Gets a Map of Records and respective update Dates of a given DataSource
     * starting in offset with size numberResults
     *
     * @param dataSource a DataSource
     * @param fromDate start date for search
     * @param toDate end date for search
     * @param offset the starting result (to start with the normal result use 0,
     * to get the second result use 1). If this value is null or a negative
     * value, it's considered 0
     * @param numberResults maximum number of results returned. //@param
     * headersOnly return only the headers of the records
     * @param headersOnly
     *
     * @return A collection of Records
     * @throws SQLException
     * @throws IOException
     */
//    OaiListResponse getRecords(DataSource dataSource, String fromDate, String toDate, Integer offset, Integer numberResults, boolean headersOnly) throws AccessPointException;
    OaiListResponse getOaiRecordsFromDataSource(DataSource dataSource, String fromDate, String toDate,
            Integer offset, int numberResults, boolean headersOnly) throws SQLException, IOException;

    /**
     * Gets a Record Timestamp String corresponding to a URN
     *
     * @param urnOfRecord a URN identifying a Record
     * @return A Record Timestamp String
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
//    String getRecordTimestamp(Urn urnOfRecord) throws AccessPointException;
    String getRecordTimestamp(Urn urnOfRecord) throws SQLException, IOException, DocumentException;

    /**
     * Get a pair with count of records from a DataSource and the row number of
     * the last record
     *
     * @param dataSource a DataSource
     * @param fromRow starting row for count
     * @param fromDate start date for count
     * @param toDate end date for count
     * @return The number of Records
     * @throws SQLException
     */
    int[] getRecordCountLastrowPair(DataSource dataSource, Integer fromRow, String fromDate, String toDate) throws SQLException;
}
