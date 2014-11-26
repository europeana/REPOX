package pt.utl.ist.task;

import pt.utl.ist.util.CompareUtil;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 */
@XmlRootElement(name = "DataSourceExportTask")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "A DataSourceExportTask")
public class DataSourceExportTask extends Task implements DataSourceTask {
    protected int getNumberParameters() {
        return 5;
    }

    @XmlElement
    @ApiModelProperty
    @Override
    public String getTaskId() {
        return getParameter(0);
    }

    @Override
    public void setTaskId(String taskId) {
        setParameter(0, taskId);
    }

    @XmlElement
    @ApiModelProperty
    @Override
    public String getDataSourceId() {
        return getParameter(1);
    }

    @Override
    public void setDataSourceId(String dataSourceId) {
        setParameter(1, dataSourceId);
    }

    @XmlElement
    @ApiModelProperty
    public String getExportDirectory() {
        return getParameter(2);
    }

    public void setExportDirectory(String exportDirectory) {
        setParameter(2, exportDirectory);
    }

    @XmlElement
    @ApiModelProperty
    public String getRecordsPerFile() {
        return getParameter(3);
    }

    public void setRecordsPerFile(String recordsPerFile) {
        setParameter(3, recordsPerFile);
    }

    @XmlElement
    @ApiModelProperty
    public String getExportFormat() {
        return getParameter(4);
    }

    public void setExportFormat(String metadataExportFormat) {
        setParameter(4, metadataExportFormat);
    }

    /**
     * Creates a new instance of this class.
     */
    public DataSourceExportTask() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * @param taskId
     * @param dataSourceId
     * @param exportDirectory
     * @param recordsPerFile
     * @param metadataExportFormat
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public DataSourceExportTask(String taskId, String dataSourceId, String exportDirectory, String recordsPerFile, String metadataExportFormat) throws SecurityException, NoSuchMethodException {
        super(ExportToFilesystem.class, new String[] { taskId, dataSourceId, exportDirectory, recordsPerFile, metadataExportFormat });
    }

    /**
     * Creates a new instance of this class.
     * @param taskId
     * @param dataSourceId
     * @param exportDirectory
     * @param recordsPerFile
     * @param metadataExportFormat
     * @param startTime
     * @param finishTime
     * @param status
     * @param maxRetries
     * @param retries
     * @param retryDelay
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public DataSourceExportTask(String taskId, String dataSourceId, String exportDirectory, String recordsPerFile, String metadataExportFormat, Calendar startTime, Calendar finishTime, Status status, int maxRetries, int retries, long retryDelay) throws SecurityException, NoSuchMethodException {
        super(ExportToFilesystem.class, new String[] { taskId, dataSourceId, exportDirectory, recordsPerFile, metadataExportFormat }, startTime, finishTime, status, maxRetries, retries, retryDelay);
    }

    @Override
    public boolean equalActionParameters(Task otherTask) {
        boolean equal = true;

        if (otherTask == null || !(otherTask instanceof DataSourceExportTask) || !CompareUtil.compareObjectsAndNull(getDataSourceId(), ((DataSourceExportTask)otherTask).getDataSourceId())) {
            //				|| !CompareDataUtil.compareObjectsAndNull(getExportDirectory(), ((DataSourceExportTask) otherTask).getExportDirectory())) {
            //				|| !CompareDataUtil.compareObjectsAndNull(getRecordsPerFile(), ((DataSourceExportTask) otherTask).getRecordsPerFile())) {
            equal = false;
        }

        return equal;
    }
}