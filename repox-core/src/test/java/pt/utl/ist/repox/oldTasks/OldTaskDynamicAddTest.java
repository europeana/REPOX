package pt.utl.ist.repox.oldTasks;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.dataProvider.DataManagerDefault;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.task.oldTasks.OldTaskReviewer;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilDefault;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class OldTaskDynamicAddTest {
    private final String DATA_PROVIDER_ID = "DP_OAI_TEST";
    private final String DATA_SOURCE_ID = "DS_OAI_TEST";
    private final String DATA_SOURCE_DESCRIPTION = "DS_description";
    private final String SOURCE_URL = "http://bd1.inesc-id.pt:8080/repoxel/OAIHandler";
    private final String SOURCE_SET = "bmfinancas";
    private final String SOURCE_SCHEMA = "http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd";
    private final String SOURCE_NAMESPACE = "http://www.europeana.eu/schemas/ese/";
    private final String SOURCE_METADATA_FORMAT = MetadataFormat.ese.name();
    private final int RECORD_COUNT = 37;

    DataManagerDefault dataManager;
    private DataProvider provider;
    private DataSource dataSourceOai;



    @Before
    public void setUp() {
        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
        dataManager = (DataManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager();
//        addDataSet();
    }

    @After
    public void tearDown() throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, ObjectNotFoundException {
//        dataManager.deleteDataProvider(provider.getId());
    }

    @Test
    public void testRun() {
        new OldTaskReviewer().addNotListedOldTasks(DATA_SOURCE_ID);
    }

    private void addDataSet(){
        try {
            provider = dataManager.createDataProvider(DATA_PROVIDER_ID, "pt", "DP_description");
            dataSourceOai = dataManager.createDataSourceOai(provider.getId(), DATA_SOURCE_ID, DATA_SOURCE_DESCRIPTION,
                    SOURCE_SCHEMA, SOURCE_NAMESPACE, SOURCE_METADATA_FORMAT, SOURCE_URL, SOURCE_SET, null, null, null);

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().startIngestDataSource(DATA_SOURCE_ID, true);
            while(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getOnetimeTasks().size() > 0){
                Thread.sleep(7000);
            }
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


}
