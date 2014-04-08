package pt.utl.ist.repox.task;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.util.CompareUtil;

import java.io.IOException;
import java.util.Calendar;

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


	public DataSourceIngestTask() {
		super();
	}
    public DataSourceIngestTask(String taskId, String dataSourceId, String fullIngest, String maxRecord4Sample) throws SecurityException, NoSuchMethodException, DocumentException, IOException {
		super(IngestDataSource.class, new String[]{taskId, dataSourceId, fullIngest, maxRecord4Sample});
	}

	public DataSourceIngestTask(String taskId, String dataSourceId, String fullIngest) throws SecurityException, NoSuchMethodException, DocumentException, IOException {
		super(IngestDataSource.class, new String[]{taskId, dataSourceId, fullIngest});
	}

	public DataSourceIngestTask(String taskId, String dataSourceId, String fullIngest, Calendar startTime,
			Calendar finishTime, Status status, int maxRetries, int retries, long retryDelay)
			throws SecurityException, NoSuchMethodException, DocumentException, IOException {
		super(IngestDataSource.class, new String[]{taskId, dataSourceId, fullIngest}, startTime, finishTime, status, maxRetries, retries, retryDelay);
	}

	@Override
	public boolean equalActionParameters(Task otherTask) {
		if(otherTask == null || !(otherTask instanceof DataSourceIngestTask)) {
			return false;
		}

		DataSourceIngestTask otherDSITask = (DataSourceIngestTask) otherTask;

        return !(!CompareUtil.compareObjectsAndNull(getDataSourceId(), otherDSITask.getDataSourceId())
                /*|| getFullIngest() != otherDSITask.getFullIngest()*/);

    }
}
