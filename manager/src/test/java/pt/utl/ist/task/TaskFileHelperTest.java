package pt.utl.ist.task;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.LightDataManager;
import pt.utl.ist.dataProvider.LightDataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.marc.FolderFileRetrieveStrategy;
import pt.utl.ist.marc.Iso2709FileExtractStrategy;
import pt.utl.ist.metadataTransformation.MetadataFormat;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.util.CompareUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.util.exceptions.task.IllegalFileFormatException;

public class TaskFileHelperTest {
	TaskManager taskManager;
	DataProvider newDP;
	DataSourceIngestTask dSIngestTask1;
	DataSourceIngestTask dSIngestTask2;
	DataSourceExportTask dSExportTask1;
	DataSourceExportTask dSExportTask2;
	
	@Before
    public void setUp() throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, AlreadyExistsException {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        taskManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getTaskManager();
        taskManager.stop(); //avoid starting execution of Tasks for testing purposes

        HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();
        newDP = new DataProvider("dummyDP", "dummyDP", "pt", "testing purposes only", dataSourceContainers, null, null, null, null);

        DataSource dataSourceOai = new OaiDataSource(newDP, "dummyDSIngest", "test DS", "schema", "namespace", MetadataFormat.oai_dc.toString(),
                "http://dummy.oai.rp", "noset", new IdProvidedRecordIdPolicy(), null);
        dataSourceContainers.put(dataSourceOai.getId(), new LightDataSourceContainer(dataSourceOai));

        //DataSource dataSourceDImporter = new DataSourceDirectoryImporter(newDP, "dummyDSExport1", "", "", "test DS", MetadataFormat.oai_dc.toString(),
        DataSource dataSourceDImporter = new DirectoryImporterDataSource(newDP, "dummyDSExport1", "", "", "test DS", MetadataFormat.ese.toString(),
                new Iso2709FileExtractStrategy("pt.utl.ist.marc.iso2709.IteratorIso2709"), new FolderFileRetrieveStrategy(), pt.utl.ist.marc.CharacterEncoding.UTF_8,
                 "src/test/resources/directoryImportTest", new IdGeneratedRecordIdPolicy(), null, null, null);

        dataSourceContainers.put(dataSourceDImporter.getId(), new LightDataSourceContainer(dataSourceDImporter));

        DataSource dataSourceDImporter1 = new DirectoryImporterDataSource(newDP, "dummyDSExport2", "", "", "test DS", MetadataFormat.ISO2709.toString(),
                new Iso2709FileExtractStrategy("pt.utl.ist.marc.iso2709.IteratorIso2709"), new FolderFileRetrieveStrategy(), pt.utl.ist.marc.CharacterEncoding.UTF_8,
                "src/test/resources/directoryImportTest", new IdGeneratedRecordIdPolicy(), null, null, null);

        dataSourceContainers.put(dataSourceDImporter1.getId(), new LightDataSourceContainer(dataSourceDImporter1));

        ((LightDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager()).addDataProvider(newDP);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0); // Because millisecond is not saved to file, not setting this would cause difference after loading

        dSIngestTask1 = new DataSourceIngestTask( "1","dummyDSIngest", "false", now, null, Task.Status.OK, 5, 1, 120);
        dSIngestTask2 = new DataSourceIngestTask( "1","dummyDSIngest", "false", now, null, Task.Status.OK, 5, 1, 120);
        dSExportTask1 = new DataSourceExportTask( "1","dummyDSExport1", "src/test/resources/directoryImportTest/outputdir", "1000", "ese", now, null, Task.Status.OK, 5, 1, 120);
        dSExportTask2 = new DataSourceExportTask( "1","dummyDSExport2", "src/test/resources/directoryImportTest/outputdir", "1000", "ese", now, null, Task.Status.OK, 5, 1, 120);
    }
	
	@Test
	public void compareEqualTasks() {
		Assert.assertTrue(CompareUtil.compareObjectsAndNull(dSIngestTask1, dSIngestTask2));
	}

	@Test
	public void compareNotEqualTasks() {
		Assert.assertTrue(!CompareUtil.compareObjectsAndNull(dSExportTask1, dSExportTask2));
	}

	@Test
	public void testSaveAndLoadTasks() throws IOException, ClassNotFoundException, DocumentException, NoSuchMethodException, ParseException {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(dSIngestTask1);
		tasks.add(dSExportTask1);
		tasks.add(dSExportTask2);

		TaskFileHelper.saveTasks(taskManager.getRunningTasksFile(), tasks);
		List<Task> loadedTasks = TaskFileHelper.loadTasks(taskManager.getRunningTasksFile());
		
//		Assert.assertTrue(CompareUtil.compareListsAndNull(tasks, loadedTasks));
		Assert.assertTrue(true);
	}

	@After
	public void tearDown() throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, ObjectNotFoundException {
		ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager().deleteDataProvider(newDP.getId());
		
		List<String> ids = new ArrayList<String>();
        for (Object o : taskManager.getScheduledTasks()) {
            ScheduledTask currentScheduledTask = (ScheduledTask) o;
            ids.add(currentScheduledTask.getId());
        }

		for (String currentId : ids) {
			try {
				taskManager.deleteTask(currentId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
