package pt.utl.ist.repox.task;


import org.apache.log4j.Logger;
import pt.utl.ist.repox.RunnableStoppable;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.util.ConfigSingleton;

/**
 * Ingest records from Data Source to REPOX
 *
 * @author dreis
 *
 */
public class IngestDataSource implements RunnableStoppable {
	private static final Logger log = Logger.getLogger(IngestDataSource.class);
	private DataSource dataSource;

	private String taskId;
	private String dataSourceId;
	private boolean fullIngest;
	private Task.Status exitStatus;
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public boolean isFullIngest() {
		return fullIngest;
	}

	public void setFullIngest(boolean fullIngest) {
		this.fullIngest = fullIngest;
	}
	
	public Task.Status getExitStatus() {
		return exitStatus;
	}

    public void setExitStatus(Task.Status exitStatus) {
        this.exitStatus = exitStatus;
    }

    public IngestDataSource() {
		super();
	}


    public IngestDataSource(String taskId, String dataSourceId, String fullIngest) {
		this();
		this.taskId = taskId;
		this.dataSourceId = dataSourceId;
		this.fullIngest = Boolean.parseBoolean(fullIngest);
	}
   
	public void run() {
		try {
			dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId).getDataSource();
			ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true); //force updated count before ingesting
			exitStatus = dataSource.startIngest(taskId, fullIngest);
		}
		catch (Exception e) {
			log.error("Unable to start dataSource with id " + dataSource.getId() + " of Data Provider "
					+ ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSource.getId()).getName(), e);
		}
	}

	public void stop() {
		if(dataSource == null) {
			log.error("Unable to stop dataSource (not running) with id " + dataSource.getId() + " of Data Provider "
					+ ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSource.getId()).getName());
		}
		
		log.warn("Received stop signal for execution of Data Source " + dataSource.getId() + " of Data Provider "
				+ ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSource.getId()).getName());
		dataSource.stopIngest(getExitStatus().isForceEmpty());
	}

}
