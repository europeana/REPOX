package pt.utl.ist.repox.dataSource;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.utl.ist.accessPoint.manager.DefaultAccessPointsManager;
import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.util.exceptions.task.IllegalFileFormatException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

public class HarvestIdSequenceTest {
    private final String DATA_PROVIDER_ID = "DP_Z3950IdSequence";
    private final String DATA_SOURCE_ID = "DS_Z3950IdSequence";
    private final String DATA_SOURCE_DESCRIPTION = "DS_description";

    private final String SOURCE_SCHEMA = "info:lc/xmlns/marcxchange-v1.xsd";
    private final String SOURCE_NAMESPACE = "info:lc/xmlns/marcxchange-v1";

    private final String SOURCE_ADDRESS = "aleph.lbfl.li";
    private final String SOURCE_PORT = "9909";
    private final String SOURCE_DATABASE = "LLB_IDS";
    private final String SOURCE_USER = "";
    private final String SOURCE_PASSWORD= "";

    private final String SOURCE_CHARSET = "UTF-8";
    private final String SOURCE_SYNTAX = "usmarc";
    private final String SOURCE_MAXIMUMID = "10";
    private final String SOURCE_ID_POLICYCLASS = "IdGenerated";

    private final String SOURCE_IDXPATH = "";
    private final String SOURCE_NAMESPACEURI ="";
    private final String SOURCE_NAMESPACESPREFIX = "";



    private final int RECORD_COUNT = 10;

    DefaultDataManager dataManager;
    private DataProvider provider;
    private DataSource dataSourceZ3950;

    @Before
    public void setUp() {
        try {
            ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
            dataManager = (DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager();

            provider = dataManager.createDataProvider(DATA_PROVIDER_ID, "pt", "DP_description");

            Map<String, String> namespaces = new TreeMap<String, String>();
            namespaces.put(SOURCE_NAMESPACESPREFIX, SOURCE_NAMESPACEURI);

            dataSourceZ3950 = dataManager.createDataSourceZ3950IdSequence(provider.getId(), DATA_SOURCE_ID, DATA_SOURCE_DESCRIPTION, SOURCE_SCHEMA, SOURCE_NAMESPACE,
                    SOURCE_ADDRESS, SOURCE_PORT, SOURCE_DATABASE, SOURCE_USER, SOURCE_PASSWORD,
                    SOURCE_SYNTAX, SOURCE_CHARSET, SOURCE_MAXIMUMID,
                    SOURCE_ID_POLICYCLASS, SOURCE_IDXPATH, namespaces, null, null);

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
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

            dataSourceZ3950.ingestRecords(logFile, true);
            DefaultAccessPointsManager accessPointsManager = (DefaultAccessPointsManager)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getAccessPointsManager();
            int[] recordCountLastrowPair = accessPointsManager.getRecordCountLastrowPair(dataSourceZ3950, null, null, null);
            int recordCount = recordCountLastrowPair[0];
            Assert.assertEquals(RECORD_COUNT, recordCount);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

}
