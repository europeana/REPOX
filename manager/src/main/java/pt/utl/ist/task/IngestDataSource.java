package pt.utl.ist.task;

import org.apache.log4j.Logger;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.util.RunnableStoppable;

/**
 * Ingest records from Data Source to REPOX
 *
 * @author dreis
 *
 */
public class IngestDataSource implements RunnableStoppable {
    private static final Logger log = Logger.getLogger(IngestDataSource.class);
    private DataSource          dataSource;

    private String              taskId;
    private String              dataSourceId;
    private boolean             fullIngest;
    private Task.Status         exitStatus;

    @SuppressWarnings("javadoc")
    public String getTaskId() {
        return taskId;
    }

    @SuppressWarnings("javadoc")
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @SuppressWarnings("javadoc")
    public String getDataSourceId() {
        return dataSourceId;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @SuppressWarnings("javadoc")
    public boolean isFullIngest() {
        return fullIngest;
    }

    @SuppressWarnings("javadoc")
    public void setFullIngest(boolean fullIngest) {
        this.fullIngest = fullIngest;
    }

    @SuppressWarnings("javadoc")
    public Task.Status getExitStatus() {
        return exitStatus;
    }

    @SuppressWarnings("javadoc")
    public void setExitStatus(Task.Status exitStatus) {
        this.exitStatus = exitStatus;
    }

    /**
     * Creates a new instance of this class.
     */
    public IngestDataSource() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * @param taskId
     * @param dataSourceId
     * @param fullIngest
     */
    public IngestDataSource(String taskId, String dataSourceId, String fullIngest) {
        this();
        this.taskId = taskId;
        this.dataSourceId = dataSourceId;
        this.fullIngest = Boolean.parseBoolean(fullIngest);
    }

    @Override
    public void run() {
        try {
            dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSourceId).getDataSource();
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true); //force updated count before ingesting
            exitStatus = dataSource.startIngest(taskId, fullIngest);
        } catch (Exception e) {
            log.error("Unable to start dataSource with id " + dataSource.getId() + " of Data Provider " + ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSource.getId()).getName(), e);
        }
    }

    @Override
    public void stop() {
        if (dataSource == null) {
            log.error("Unable to stop dataSource (not running) with id " + dataSource.getId() + " of Data Provider " + ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSource.getId()).getName());
        }

        log.warn("Received stop signal for execution of Data Source " + dataSource.getId() + " of Data Provider " + ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSource.getId()).getName());
        dataSource.stopIngest(getExitStatus().isForceEmpty());
    }

}
