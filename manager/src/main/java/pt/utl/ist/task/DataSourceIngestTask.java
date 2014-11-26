package pt.utl.ist.task;

import org.dom4j.DocumentException;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import pt.utl.ist.util.CompareUtil;

import java.io.IOException;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 */
@XmlRootElement(name = "DataSourceIngestTask")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "A DataSourceIngestTask")
public class DataSourceIngestTask extends Task implements DataSourceTask {
    @Override
    protected int getNumberParameters() {
        return 3;
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
