package pt.utl.ist.repox.dataSource;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.utl.ist.repox.accessPoint.manager.DefaultAccessPointsManager;
import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.repox.dataProvider.DefaultDataManager;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.util.exceptions.AlreadyExistsException;
import pt.utl.ist.repox.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.repox.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.repox.util.exceptions.task.IllegalFileFormatException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

public class HarvestFtpTest {
    private final String DATA_PROVIDER_ID = "DP_FTP";
    private final String DATA_SOURCE_ID = "DS_FTP";
    private final String DATA_SOURCE_DESCRIPTION = "DS_description";

    private final String SOURCE_SCHEMA = "http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd";
    private final String SOURCE_NAMESPACE = "http://www.europeana.eu/schemas/ese/";
    private final String SOURCE_METADATA_FORMAT = MetadataFormat.ese.name();

    private final String SOURCE_SERVER = "bd1.inesc-id.pt";
    private final String SOURCE_USER = "ftp";
    private final String SOURCE_PASSWORD = "pmath2010.";
    private final String SOURCE_ftpPATH= "Lizbeth";
    private final String SOURCE_CHARSET = "";
    private final String SOURCE_ID_POLICYCLASS = "IdGenerated";
    private final String SOURCE_ISO_FORMAT = " ";

    private final String SOURCE_IDXPATH = "";
    private final String SOURCE_NAMESPACEURI ="";
    private final String SOURCE_NAMESPACESPREFIX = "namespacePrefix";
    private final String SOURCE_RECORDXPATH = "record";


    private final int RECORD_COUNT =1849;

    DefaultDataManager dataManager;
    private DataProvider provider;
    private DataSource dataSourceFtp;

    @Before
    public void setUp() {
        try {
            ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
            dataManager = (DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager();

            provider = dataManager.createDataProvider(DATA_PROVIDER_ID, "pt", "DP_description");

            Map<String, String> namespaces = new TreeMap<String, String>();
            namespaces.put(SOURCE_NAMESPACESPREFIX, SOURCE_NAMESPACEURI);

            dataSourceFtp = dataManager.createDataSourceFtp(provider.getId(),  DATA_SOURCE_ID, DATA_SOURCE_DESCRIPTION,
                    SOURCE_SCHEMA, SOURCE_NAMESPACE, SOURCE_METADATA_FORMAT,SOURCE_ISO_FORMAT, SOURCE_CHARSET,
                    SOURCE_ID_POLICYCLASS, SOURCE_IDXPATH, namespaces,SOURCE_RECORDXPATH, SOURCE_SERVER, SOURCE_USER, SOURCE_PASSWORD, SOURCE_ftpPATH,
                    null,null, null);
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
        try {
            File logFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getTempDir() + "/log.txt");

            dataSourceFtp.ingestRecords(logFile, true);
            DefaultAccessPointsManager accessPointsManager = (DefaultAccessPointsManager)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getAccessPointsManager();
            int[] recordCountLastrowPair = accessPointsManager.getRecordCountLastrowPair(dataSourceFtp, null, null, null);
            int recordCount = recordCountLastrowPair[0];
            Assert.assertEquals(RECORD_COUNT, recordCount);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
/*
	@Test
	public void testRunFromFile() {
		Assert.assertTrue(false);
	}
*/
}
