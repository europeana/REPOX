package pt.utl.ist.repox.task;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.util.CompareUtil;

import java.io.IOException;
import java.util.Calendar;

/**
 */
public class DataSourceIngestTask extends Task implements DataSourceTask {
    @Override
    protected int getNumberParameters() {
        return 3;
    }

    public String getTaskId() {
        return getParameter(0);
    }

    public void setTaskId(String taskId) {
        setParameter(0, taskId);
    }

    public String getDataSourceId() {
        return getParameter(1);
    }

    public void setDataSourceId(String dataSourceId) {
        setParameter(1, dataSourceId);
    }

    public boolean getFullIngest() {
        return (getParameter(2) != null && Boolean.parseBoolean(getParameter(2)));
    }

    public void setFullIngest(boolean fullIngest) {
        setParameter(2, (Boolean.valueOf(fullIngest)).toString());
    }

    /**
     * Creates a new instance of this class.
     */
    public DataSourceIngestTask() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * @param taskId
     * @param dataSourceId
     * @param fullIngest
     * @param maxRecord4Sample
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws DocumentException
     * @throws IOException
     */
    public DataSourceIngestTask(String taskId, String dataSourceId, String fullIngest, String maxRecord4Sample) throws SecurityException, NoSuchMethodException, DocumentException, IOException {
        super(IngestDataSource.class, new String[] { taskId, dataSourceId, fullIngest, maxRecord4Sample });
    }

    /**
     * Creates a new instance of this class.
     * @param taskId
     * @param dataSourceId
     * @param fullIngest
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws DocumentException
     * @throws IOException
     */
    public DataSourceIngestTask(String taskId, String dataSourceId, String fullIngest) throws SecurityException, NoSuchMethodException, DocumentException, IOException {
        super(IngestDataSource.class, new String[] { taskId, dataSourceId, fullIngest });
    }

    /**
     * Creates a new instance of this class.
     * @param taskId
     * @param dataSourceId
     * @param fullIngest
     * @param startTime
     * @param finishTime
     * @param status
     * @param maxRetries
     * @param retries
     * @param retryDelay
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws DocumentException
     * @throws IOException
     */
    public DataSourceIngestTask(String taskId, String dataSourceId, String fullIngest, Calendar startTime, Calendar finishTime, Status status, int maxRetries, int retries, long retryDelay) throws SecurityException, NoSuchMethodException, DocumentException, IOException {
        super(IngestDataSource.class, new String[] { taskId, dataSourceId, fullIngest }, startTime, finishTime, status, maxRetries, retries, retryDelay);
    }

    @Override
    public boolean equalActionParameters(Task otherTask) {
        if (otherTask == null || !(otherTask instanceof DataSourceIngestTask)) { return false; }

        DataSourceIngestTask otherDSITask = (DataSourceIngestTask)otherTask;

        return !(!CompareUtil.compareObjectsAndNull(getDataSourceId(), otherDSITask.getDataSourceId())
        /*|| getFullIngest() != otherDSITask.getFullIngest()*/);

    }
}
