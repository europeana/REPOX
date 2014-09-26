package pt.utl.ist.task;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.repox.util.exceptions.task.IllegalFileFormatException;
import pt.utl.ist.task.DataSourceIngestTask;
import pt.utl.ist.task.ScheduledTask;
import pt.utl.ist.task.TaskManager;
import pt.utl.ist.task.ScheduledTask.Frequency;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class TaskManagerTest {
	TaskManager taskManager;
	private ScheduledTask task1;
	private ScheduledTask task2;
	
	@Before
    public void setUp() throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());

        taskManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getTaskManager();
        String dataSourceId = "1";
        Calendar firstRun = new GregorianCalendar(2009, 0, 1, 0, 0);
        task1 = new ScheduledTask(dataSourceId + "_1", firstRun, Frequency.ONCE, null, new DataSourceIngestTask(dataSourceId, "1", "false"));
        task2 = new ScheduledTask(dataSourceId + "_2", firstRun, Frequency.ONCE, null, new DataSourceIngestTask(dataSourceId, "1", "false"));
    }

	@Test
	public void testSaveAndLoadScheduledTask() throws IOException {
		taskManager.saveTask(task1);
		Assert.assertNotNull(taskManager.getTask(task1.getId()));
	}

	@Test
	public void testDeleteScheduledTask() throws IOException {
		taskManager.saveTask(task1);
		taskManager.saveTask(task2);
		Assert.assertTrue(taskManager.deleteTask(task1.getId()));
		Assert.assertTrue(taskManager.deleteTask(task2.getId()));
	}
	
	@After
	public void tearDown() {
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
