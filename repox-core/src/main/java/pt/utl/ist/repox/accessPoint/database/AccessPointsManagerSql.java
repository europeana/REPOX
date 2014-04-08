package pt.utl.ist.repox.accessPoint.database;

import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.Urn;
import pt.utl.ist.repox.accessPoint.AccessPoint;
import pt.utl.ist.repox.accessPoint.AccessPointRecordRepoxFull;
import pt.utl.ist.repox.accessPoint.AccessPointsManagerDefault;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.dataProvider.dataSource.IdExtracted;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.oai.OaiListResponse;
import pt.utl.ist.repox.oai.OaiListResponse.OaiItem;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.TimeUtil;
import pt.utl.ist.repox.util.ZipUtil;
import pt.utl.ist.util.sql.SqlUtil;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Implementation of an AccessPointsManagerDefault based on a Sql database
 * @author Nuno Freire
 *
 */
public interface AccessPointsManagerSql {
    public void initialize(HashMap<String, DataSourceContainer> dataSourceContainers) throws SQLException;

    public void updateDataSourceAccessPoint(DataSource dataSource, Class typeOfIndex, String oldAccessPointId,
                                            String newAccessPointId) throws SQLException;

    public void emptyIndex(DataSource dataSource, AccessPoint accessPoint) throws SQLException;

    public void deleteIndex(AccessPoint accessPoint) throws SQLException;

    public int[] getRecordCountLastrowPair(DataSource dataSource, Integer fromRow, String fromDate, String toDate) throws SQLException;

    public Collection getIdsFromDataSource(DataSource dataSource, String fromDate, String toDate, Integer offset,
                                           Integer numberResults) throws SQLException;

    public OaiItem getRecord(Urn urnOfRecord) throws IOException, DocumentException, SQLException;

    public String getRecordTimestamp(Urn urnOfRecord) throws IOException, DocumentException, SQLException;

    public OaiListResponse getOaiRecordsFromDataSource(DataSource dataSource, String fromDate, String toDate,
                                                       Integer offset, int numberResults, boolean headersOnly) throws SQLException, IOException;
}
