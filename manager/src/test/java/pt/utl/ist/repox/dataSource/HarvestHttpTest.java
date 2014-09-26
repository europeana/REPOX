package pt.utl.ist.repox.dataSource;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.util.exceptions.AlreadyExistsException;
import pt.utl.ist.repox.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.repox.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.repox.util.exceptions.task.IllegalFileFormatException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

public class HarvestHttpTest {
    private final String DATA_PROVIDER_ID = "DP_HTTP";
    private final String DATA_SOURCE_ID = "DS_HTTP";
    private final String DATA_SOURCE_DESCRIPTION = "DS_description";

    private final String SOURCE_SCHEMA = "http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd";
    private final String SOURCE_NAMESPACE = "http://www.europeana.eu/schemas/ese/";
    private final String SOURCE_METADATA_FORMAT = MetadataFormat.ese.name();

    private final String SOURCE_CHARSET = "";
    private final String SOURCE_ID_POLICYCLASS = "IdGenerated";
    private final String SOURCE_ISO_FORMAT = " ";

    private final String SOURCE_IDXPATH = "";
    private final String SOURCE_NAMESPACEURI ="";
    private final String SOURCE_NAMESPACESPREFIX = "";
    private final String SOURCE_RECORDXPATH = "record";
    private final String SOURCE_URLHTTP = "http://digmap2.ist.utl.pt:8080/index_digital/contente/09428_Ag_DE_ELocal.zip";


    private final int RECORD_COUNT = 2203;

    DefaultDataManager dataManager;
    private DataProvider provider;
    private DataSource dataSourceHttp;

    @Before
    public void setUp() {
        try {
            ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
            dataManager = (DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager();

            provider = dataManager.createDataProvider(DATA_PROVIDER_ID, "pt", "DP_description");

            Map<String, String> namespaces = new TreeMap<String, String>();
            namespaces.put(SOURCE_NAMESPACESPREFIX, SOURCE_NAMESPACEURI);

            dataSourceHttp = dataManager.createDataSourceHttp(provider.getId(),  DATA_SOURCE_ID, DATA_SOURCE_DESCRIPTION,
                    SOURCE_SCHEMA, SOURCE_NAMESPACE, SOURCE_METADATA_FORMAT,SOURCE_ISO_FORMAT, SOURCE_CHARSET,
                    SOURCE_ID_POLICYCLASS, SOURCE_IDXPATH,namespaces,SOURCE_RECORDXPATH,SOURCE_URLHTTP,
                    null, null, null);
        }
        catch (AlreadyExistsException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidArgumentsException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, ObjectNotFoundException {
        dataManager.deleteDataProvider(provider.getId());
    }

    @Test
    public void testRun() {
        //todo: the http_url response should be replaced with a stub to prevent server offline cases...
//        try {
//            File logFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getTempDir() + "/log.txt");
//
//            dataSourceHttp.ingestRecords(logFile, true);
//            AccessPointsManagerDefault accessPointsManager = (AccessPointsManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getAccessPointsManager();
//            int[] recordCountLastrowPair = accessPointsManager.getRecordCountLastrowPair(dataSourceHttp, null, null, null);
//            int recordCount = recordCountLastrowPair[0];
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assert.assertTrue(false);
//        }
        Assert.assertTrue(true);
    }
/*
	@Test
	public void testRunFromFile() {
		Assert.assertTrue(false);
	}
*/
}
