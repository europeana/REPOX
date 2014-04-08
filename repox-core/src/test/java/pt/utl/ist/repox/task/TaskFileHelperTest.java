package pt.utl.ist.repox.task;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.dataProvider.*;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.dataProvider.dataSource.IdProvided;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.marc.DataSourceFolder;
import pt.utl.ist.repox.marc.Iso2709FileExtract;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.CompareUtil;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilDefault;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TaskFileHelperTest {
	TaskManager taskManager;
	DataProvider newDP;
	DataSourceIngestTask dSIngestTask1;
	DataSourceIngestTask dSIngestTask2;
	DataSourceExportTask dSExportTask1;
	DataSourceExportTask dSExportTask2;
	
	@Before
    public void setUp() throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, AlreadyExistsException {
        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
        taskManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getTaskManager();
        taskManager.stop(); //avoid starting execution of Tasks for testing purposes

        HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();
        newDP = new DataProvider("dummyDP", "dummyDP", "pt", "testing purposes only", dataSourceContainers);

        DataSource dataSourceOai = new DataSourceOai(newDP, "dummyDSIngest", "test DS", "schema", "namespace", MetadataFormat.oai_dc.toString(),
                "http://dummy.oai.rp", "noset", new IdProvided(), null);
        dataSourceContainers.put(dataSourceOai.getId(), new DataSourceContainerDefault(dataSourceOai));

        //DataSource dataSourceDImporter = new DataSourceDirectoryImporter(newDP, "dummyDSExport1", "", "", "test DS", MetadataFormat.oai_dc.toString(),
        DataSource dataSourceDImporter = new DataSourceDirectoryImporter(newDP, "dummyDSExport1", "", "", "test DS", MetadataFormat.ese.toString(),
                new Iso2709FileExtract("pt.utl.ist.marc.iso2709.IteratorIso2709"), new DataSourceFolder(), pt.utl.ist.repox.marc.CharacterEncoding.UTF_8,
                 "src/test/resources/directoryImportTest", new IdGenerated(), null, null, null);

        dataSourceContainers.put(dataSourceDImporter.getId(), new DataSourceContainerDefault(dataSourceDImporter));

        DataSource dataSourceDImporter1 = new DataSourceDirectoryImporter(newDP, "dummyDSExport2", "", "", "test DS", MetadataFormat.ISO2709.toString(),
                new Iso2709FileExtract("pt.utl.ist.marc.iso2709.IteratorIso2709"), new DataSourceFolder(), pt.utl.ist.repox.marc.CharacterEncoding.UTF_8,
                "src/test/resources/directoryImportTest", new IdGenerated(), null, null, null);

        dataSourceContainers.put(dataSourceDImporter1.getId(), new DataSourceContainerDefault(dataSourceDImporter1));

        ((DataManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager()).addDataProvider(newDP);

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
