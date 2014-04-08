package pt.utl.ist.repox.task;

import pt.utl.ist.repox.util.CompareUtil;

import java.util.Calendar;


public class DataSourceExportTask extends Task implements DataSourceTask {
	@Override
	protected int getNumberParameters() {
		return 5;
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

	public String getExportDirectory() {
		return getParameter(2);
	}
	
	public void setExportDirectory(String exportDirectory) {
		setParameter(2, exportDirectory);
	}
	
	public String getRecordsPerFile() {
		return getParameter(3);
	}
	
	public void setRecordsPerFile(String recordsPerFile) {
		setParameter(3, recordsPerFile);
	}

    public String getExportFormat() {
		return getParameter(4);
	}

	public void setExportFormat(String metadataExportFormat) {
		setParameter(4, metadataExportFormat);
	}

	public DataSourceExportTask() {
		super();
	}

	public DataSourceExportTask(String taskId, String dataSourceId, String exportDirectory, String recordsPerFile, String metadataExportFormat) throws SecurityException, NoSuchMethodException {
		super(ExportToFilesystem.class, new String[]{taskId, dataSourceId, exportDirectory, recordsPerFile, metadataExportFormat});
	}

	public DataSourceExportTask(String taskId, String dataSourceId, String exportDirectory, String recordsPerFile, String metadataExportFormat,
					Calendar startTime, Calendar finishTime, Status status, int maxRetries, int retries, long retryDelay)
			throws SecurityException, NoSuchMethodException {
		super(ExportToFilesystem.class, new String[]{taskId, dataSourceId, exportDirectory, recordsPerFile, metadataExportFormat},
				startTime, finishTime, status, maxRetries, retries, retryDelay);
	}

	@Override
	public boolean equalActionParameters(Task otherTask) {
		boolean equal = true;

		if(otherTask == null
				|| !(otherTask instanceof DataSourceExportTask)
				|| !CompareUtil.compareObjectsAndNull(getDataSourceId(), ((DataSourceExportTask) otherTask).getDataSourceId())){
//				|| !CompareDataUtil.compareObjectsAndNull(getExportDirectory(), ((DataSourceExportTask) otherTask).getExportDirectory())) {
//				|| !CompareDataUtil.compareObjectsAndNull(getRecordsPerFile(), ((DataSourceExportTask) otherTask).getRecordsPerFile())) {
			equal = false;
		}

		return equal;
	}
}