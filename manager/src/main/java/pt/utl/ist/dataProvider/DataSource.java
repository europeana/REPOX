/*
 * Created on 23/Mar/2006
 *
 */
package pt.utl.ist.dataProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.MessagingException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pt.utl.ist.accessPoint.AccessPoint;
import pt.utl.ist.accessPoint.RecordRepoxFullAccessPoint;
import pt.utl.ist.accessPoint.TimestampAccessPoint;
import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.RepoxConfiguration;
import pt.utl.ist.dataProvider.dataSource.DataSourceTag;
import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.externalServices.ExternalNoMonitorServiceThread;
import pt.utl.ist.externalServices.ExternalRestService;
import pt.utl.ist.externalServices.ExternalRestServiceContainer;
import pt.utl.ist.externalServices.ExternalRestServiceThread;
import pt.utl.ist.externalServices.ExternalServiceNoMonitor;
import pt.utl.ist.externalServices.ExternalServiceStates;
import pt.utl.ist.externalServices.ServiceParameter;
import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.reports.LogUtil;
import pt.utl.ist.statistics.RecordCount;
import pt.utl.ist.task.DataSourceIngestTask;
import pt.utl.ist.task.OldTask;
import pt.utl.ist.task.ScheduledTask;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.CompareUtil;
import pt.utl.ist.util.FileUtil;
import pt.utl.ist.util.StringUtil;
import pt.utl.ist.util.TimeUtil;
import pt.utl.ist.util.XmlUtil;
import pt.utl.ist.util.date.DateUtil;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ibm.icu.util.Calendar;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import freemarker.template.TemplateException;

/**
 * Represents a Data Source in REPOX. It will be used to harvest records and
 * ingest them. 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 01, 2014
 */
@XmlRootElement(name = "dataset")
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataSetType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OaiDataSource.class, name = "OAI"),
        @JsonSubTypes.Type(value = DirectoryImporterDataSource.class, name = "DIR")
//        @JsonSubTypes.Type(value = DataSourceZ3950.class, name = "Z3950"),
//        @JsonSubTypes.Type(value = SruRecordUpdateDataSource.class, name = "SRU")
})
@XmlSeeAlso({ OaiDataSource.class, DirectoryImporterDataSource.class })
@ApiModel(value = "A Dataset", discriminator = "dataSourceType", subTypes = { OaiDataSource.class, DirectoryImporterDataSource.class })
public abstract class DataSource {
    @XmlEnum(String.class)
    public enum StatusDS {
        CREATED, RUNNING, ERROR, OK, WARNING, CANCELED, PRE_PROCESSING, POST_PROCESSING, PRE_PROCESS_ERROR, POST_PROCESS_ERROR
    }

    private static final Logger                   log                        = Logger.getLogger(DataSource.class);

    /** DataSource MAX_ID_SIZE */
    public static final int                       MAX_ID_SIZE                = 32;
    /** DataSource RECORDS_BATCH_SIZE */
    protected static final int                    RECORDS_BATCH_SIZE         = 1000;
    /** DataSource LAST_TASK_FILENAME */
    protected static final String                 LAST_TASK_FILENAME         = "lastTask.txt";

    @XmlElement
    @ApiModelProperty
    protected String                              id;
    @XmlElement
    @ApiModelProperty(required = true)
    protected String                              schema;
    @XmlElement
    @ApiModelProperty(required = true)
    protected String                              namespace;
    @XmlElement
    @ApiModelProperty
    protected String                              description;
    @XmlElement
    @ApiModelProperty(required = true)
    protected String                              metadataFormat;
    @XmlElement
    @ApiModelProperty
    protected boolean                             isSample                   = false;
    @XmlElement
    @ApiModelProperty
    protected String                              exportDir;
    @XmlElement
    @ApiModelProperty
    protected String                              marcFormat;

    @JsonSubTypes({
            @JsonSubTypes.Type(value = IdGeneratedRecordIdPolicy.class, name = "IdGenerated"),
            @JsonSubTypes.Type(value = IdExtractedRecordIdPolicy.class, name = "IdExtracted"),
            @JsonSubTypes.Type(value = IdProvidedRecordIdPolicy.class, name = "IdProvided")
    })
    @XmlElementRefs({
            @XmlElementRef(type = IdGeneratedRecordIdPolicy.class),
            @XmlElementRef(type = IdExtractedRecordIdPolicy.class),
            @XmlElementRef(type = IdProvidedRecordIdPolicy.class)
    })
    @ApiModelProperty
    protected RecordIdPolicy                      recordIdPolicy;

    @ApiModelProperty(hidden = true)
    protected StatusDS                            status;
    @ApiModelProperty(hidden = true)
    protected StatusDS                            previousStatus;
    @ApiModelProperty(hidden = true)
    protected String                              lastRunResult;
    @ApiModelProperty(hidden = true)
    protected int                                 maxRecord4Sample           = -1;
    @ApiModelProperty(hidden = true)
    protected int                                 numberOfRecords2Harvest    = -1;
    @ApiModelProperty(hidden = true)
    protected int                                 numberOfRecordsPerResponse = -1;
    @ApiModelProperty(hidden = true)
    protected Date                                lastUpdate;
    @ApiModelProperty(hidden = true)
    protected boolean                             stopExecution              = false;
    @ApiModelProperty(hidden = true)
    protected boolean                             forceStopExecution         = false;
    @ApiModelProperty(hidden = true)
    protected int                                 lastIngestCount, lastIngestDeletedCount;
    @ApiModelProperty(hidden = true)
    protected List<DataSourceTag>                 tags;
    @ApiModelProperty(hidden = true)
    protected HashMap<String, AccessPoint>        accessPoints               = new HashMap<String, AccessPoint>(); // AccessPoints for this Data Source
    @ApiModelProperty(hidden = true)
    protected Map<String, MetadataTransformation> metadataTransformations;                                        // Map of source -> destination MetadataFormat
    @ApiModelProperty(hidden = true)
    protected List<ExternalRestService>           externalRestServices;
    @ApiModelProperty(hidden = true)
    protected List<OldTask>                       oldTasksList               = new ArrayList<OldTask>();
    @ApiModelProperty(hidden = true)
    protected ExternalServiceStates.ContainerType externalServicesRunType;
    @ApiModelProperty(hidden = true)
    protected ArrayList<Long>                     statisticsHarvester        = new ArrayList<Long>();

    public int getMaxRecord4Sample() {
        return maxRecord4Sample;
    }

    public void setMaxRecord4Sample(int maxRecord4Sample) {
        this.maxRecord4Sample = maxRecord4Sample;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public HashMap<String, AccessPoint> getAccessPoints() {
        return accessPoints;
    }

    public void setAccessPoints(HashMap<String, AccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
    }

    public Map<String, MetadataTransformation> getMetadataTransformations() {
        return metadataTransformations;
    }

    public void setMetadataTransformations(Map<String, MetadataTransformation> metadataTransformations) {
        this.metadataTransformations = metadataTransformations;
    }

    public List<ExternalRestService> getExternalRestServices() {
        if (externalRestServices == null)
            externalRestServices = new ArrayList<ExternalRestService>();
        return externalRestServices;
    }

    public void setExternalRestServices(List<ExternalRestService> externalRestServices) {
        this.externalRestServices = externalRestServices;
    }

    public ExternalServiceStates.ContainerType getExternalServicesRunType() {
        return externalServicesRunType;
    }

    public void setExternalServicesRunType(ExternalServiceStates.ContainerType externalServicesRunType) {
        this.externalServicesRunType = externalServicesRunType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecordIdPolicy getRecordIdPolicy() {
        return recordIdPolicy;
    }

    public void setRecordIdPolicy(RecordIdPolicy recordIdPolicy) {
        this.recordIdPolicy = recordIdPolicy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusDS getStatus() {
        return status;
        //return StatusDS.OK;
    }

    public StatusDS getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(StatusDS previousStatus) {
        this.previousStatus = previousStatus;
    }

    public List<OldTask> getOldTasksList() {
        return oldTasksList;
    }

    public void setOldTasksList(List<OldTask> oldTasksList) {
        this.oldTasksList = oldTasksList;
    }

    public List<DataSourceTag> getTags() {
        if (tags == null)
            tags = new ArrayList<DataSourceTag>();
        return tags;
    }

    public void setTags(List<DataSourceTag> tags) {
        this.tags = tags;
    }

    /**
     * Obtains the current status of this DataSource
     */
    @ApiModelProperty(hidden = true)
    public String getStatusString() throws IOException {
        if (status != null) {
            return status.toString();
        }
        return "";
    }

    public void setStatus(StatusDS status) {
        this.status = status;
    }

    public String getLastRunResult() {
        return lastRunResult;
    }

    public void setLastRunResult(String lastRunResult) {
        this.lastRunResult = lastRunResult;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getMetadataFormat() {
        return metadataFormat;
    }

    public void setMetadataFormat(String metadataFormat) {
        this.metadataFormat = metadataFormat;
    }

    public String getMarcFormat() {
        return marcFormat;
    }

    public void setMarcFormat(String marcFormat) {
        this.marcFormat = marcFormat;
    }

    public boolean isSample() {
        return isSample;
    }

    public void setIsSample(boolean sample) {
        isSample = sample;
    }

    public int getLastIngestCount() {
        return lastIngestCount;
    }

    public void setLastIngestCount(int lastIngestCount) {
        this.lastIngestCount = lastIngestCount;
    }

    /**
     * Creates a new instance of this class.
     */
    public DataSource() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param dataProvider
     * @param id
     * @param description
     * @param schema
     * @param namespace
     * @param metadataFormat
     * @param recordIdPolicy
     * @param metadataTransformations
     */
    public DataSource(DataProvider dataProvider, String id, String description, String schema, String namespace, String metadataFormat, RecordIdPolicy recordIdPolicy,
                      Map<String, MetadataTransformation> metadataTransformations) {
        this();
        this.id = id;
        this.description = description;
        this.schema = schema;
        this.namespace = namespace;
        this.metadataFormat = metadataFormat;
        this.recordIdPolicy = recordIdPolicy;

        if (metadataTransformations == null) {
            this.metadataTransformations = new TreeMap<String, MetadataTransformation>();
        } else {
            this.metadataTransformations = metadataTransformations;
        }

        this.status = StatusDS.CREATED;

        initAccessPoints();
    }

//    private void sendEmail(Task.Status exitStatus, File logFile) throws FileNotFoundException, MessagingException {
//        String smtpServer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSmtpServer();
//        if (smtpServer == null || smtpServer.isEmpty()) {
//            return;
//        }
//
//        String fromEmail = "repox@noreply.eu";
//        String subject = "REPOX Data Source ingesting finished. Exit status: " + exitStatus.toString();
//        String[] recipientsEmail = new String[] { ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getAdministratorEmail() };
//        File[] attachments = new File[] { logFile };
//
//        String message = "Data Source " + id + " finished ingesting." + " Exit status: " + exitStatus.toString() + "\nLog file is attached to this email." + "\n\n--------------------------------------------------------------------------------\n" + "This email is sent automatically by REPOX. Do not reply to this message.";
//
//    }

    /**
     * @param taskId
     * @param fullIngest
     * @return Task.Status
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
    public Task.Status startIngest(String taskId, boolean fullIngest) throws IOException, DocumentException, SQLException {
        Task.Status exitStatus = Task.Status.OK;
        stopExecution = false;
        forceStopExecution = false;
        lastIngestCount = 0;
        lastIngestDeletedCount = 0;

        File logFile = getLogFile(taskId);
        Date startIngestTime = new Date();

        try {
            // Run Pre-Process Services
            ExternalServiceStates.ServiceExitState esState = runExternalServices("PRE_PROCESS", logFile);

            status = checkProcessingState(esState, "PRE_PROCESS");
            if (status == StatusDS.PRE_PROCESS_ERROR) {
                LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.PRE_PROCESS_ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
                return Task.Status.ERRORS;
            }

            //Status - running ingest
            status = StatusDS.RUNNING;
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();

            StringUtil.simpleLog("Starting to import from Data Source with id " + id, this.getClass(), logFile);
            Date now = new Date();

            exitStatus = ingestRecords(logFile, fullIngest);

            if (exitStatus.isSuccessful()) {
                status = StatusDS.OK;
                //                if(full)
                //                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().setDataSetSampleState(true,dataSource);
            } else if (exitStatus.isCanceled()) {
                status = StatusDS.CANCELED;
                if (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getTask(taskId) != null)
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getTask(taskId).setFailTime(new GregorianCalendar());
            } else if (exitStatus.isForceEmpty()) {
                status = null;
                // update record's count
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id, true);
                // update dataProviders.xml
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
                LogUtil.removeLogFile(logFile);
                return exitStatus;
            } else {
                status = StatusDS.ERROR;
                if (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getTask(taskId) != null)
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getTask(taskId).setFailTime(new GregorianCalendar());
            }
            previousStatus = status;

            // update record's count
            RecordCount dataSourceCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id, true);

            lastUpdate = now;

            // update dataProviders.xml
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();

            createIngestOldTask(taskId);

            StringUtil.simpleLog("Finished importing from Data Source with id " + id + ". Exit status: " + exitStatus.toString(), this.getClass(), logFile);
            LogUtil.endLogInfo(logFile, startIngestTime, new Date(), exitStatus.toString(), id, lastIngestCount, lastIngestDeletedCount);

            // Run Post-Process Services
            ExternalServiceStates.ServiceExitState esStatus = runExternalServices("POST_PROCESS", logFile);

            status = checkProcessingState(esStatus, "POST_PROCESS");
            if (status == StatusDS.POST_PROCESS_ERROR) {
                LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.POST_PROCESS_ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
                sendEmailAfterIngest(exitStatus, logFile);
                return Task.Status.ERRORS;
            }

            if (exitStatus.isSuccessful()) {
                status = StatusDS.OK;
            } else if (exitStatus.isCanceled()) {
                status = StatusDS.CANCELED;
            } else if (exitStatus.isForceEmpty()) {
                status = null;
            } else {
                status = StatusDS.ERROR;
            }

            sendEmailAfterIngest(exitStatus, logFile);

        } catch (MessagingException e) {
            log.warn(e.getMessage(), e);
            if (status != null && status == StatusDS.OK)
                status = StatusDS.WARNING;
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (DocumentException e1) {
                e1.printStackTrace();
            }
            StringUtil.simpleLog("WARN - Could not send email notification: " + e.getMessage(), e, this.getClass(), logFile);
            LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.WARNING.name(), id, lastIngestCount, lastIngestDeletedCount);
        } catch (Exception e) {
            status = StatusDS.ERROR;
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (DocumentException e1) {
                e1.printStackTrace();
            }
            log.error(e.getMessage(), e);
            StringUtil.simpleLog("ERROR importing from Data Source with id " + id + ": " + e.getMessage(), e, this.getClass(), logFile);
            LogUtil.endLogInfo(logFile, startIngestTime, new Date(), StatusDS.ERROR.name(), id, lastIngestCount, lastIngestDeletedCount);
        }

        LogUtil.removeLogFile(logFile);
        return exitStatus;
    }

    private void sendEmailAfterIngest(Task.Status exitStatus, File logFile) throws IOException, DocumentException, SQLException, MessagingException, TemplateException {
        if (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getSendEmailAfterIngest()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            String fromEmail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
            String[] recipientsEmail = new String[] { ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getAdministratorEmail() };
            String subject = "REPOX Data Source ingesting finished. Exit status: " + exitStatus.toString();
            String messageText = "Data Source " + id + " finished ingesting." + "\nExit status: " + exitStatus.toString() + "\nRepox Address: " + host + "\nLog file is attached to this email." + "\n\n--------------------------------------------------------------------------------\n" + "This email is sent automatically by REPOX. Do not reply to this message.";

            File zipFile = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient().createZipFile(logFile);
            File[] attachments = new File[] { zipFile };

            HashMap map = new HashMap<String, String>();
            map.put("exitStatus", exitStatus.toString());
            map.put("id", id);
            map.put("mailType", "ingest");
            map.put("repoxAddress", host);

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient().sendEmail(fromEmail, recipientsEmail, subject, messageText, attachments, map);
        }
    }

    private void createIngestOldTask(String taskId) throws IOException, DocumentException {
        Task task = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getRunningTask(taskId);
        if (task instanceof DataSourceIngestTask) {
            DataSourceIngestTask dSTask = (DataSourceIngestTask)task;
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dSTask.getDataSourceId());

            if (dataSourceContainer != null) {
                DataSource dataSource = dataSourceContainer.getDataSource();
                if (dataSource.getLogFilenames().size() > 0) {
                    createOldTask(dataSource, dSTask.getFullIngest(), dSTask.getTaskId(), dSTask.getRetries(), dSTask.getRetryDelay(), dSTask.getMaxRetries());
                }
            }
        } else if (task instanceof ScheduledTask) {
            ScheduledTask scheduledTask = (ScheduledTask)task;
            String[] params = scheduledTask.getParameters();
            String dsID = params[1];
            String fullIngestStr = params[2];
            DataSourceContainer dataSourceContainer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dsID);

            if (dataSourceContainer != null) {
                DataSource dataSource = dataSourceContainer.getDataSource();
                if (dataSource.getLogFilenames().size() > 0) {
                    createOldTask(dataSource, Boolean.getBoolean(fullIngestStr), scheduledTask.getId(), scheduledTask.getRetries(), scheduledTask.getRetryDelay(), scheduledTask.getMaxRetries());
                }
            }
        }
    }

    private void createOldTask(DataSource dataSource, boolean fullIngest, String taskID, int retries, long retryDelay, int maxRetries) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            int m = cal.get(GregorianCalendar.MONTH) + 1;
            int d = cal.get(GregorianCalendar.DATE);
            String mm = Integer.toString(m);
            String dd = Integer.toString(d);
            String dateString = "" + cal.get(GregorianCalendar.YEAR) + "-" + (m < 10 ? "0" + mm : mm) + "-" + (d < 10 ? "0" + dd : dd) + " " + cal.get(GregorianCalendar.HOUR_OF_DAY) + ":" + cal
                    .get(GregorianCalendar.MINUTE);

            String logName = dataSource.getLogFilenames().get(0);
            String ingestType;
            if (fullIngest)
                ingestType = "fullIngest";
            else
                ingestType = "incrementalIngest";

            OldTask newOldTask = new OldTask(dataSource, taskID, logName, ingestType, dataSource.getStatusString(), String.valueOf(retries), String.valueOf(maxRetries), String.valueOf(retryDelay),
                    dateString, dataSource.getNumberRecords()[0]);

            dataSource.getOldTasksList().add(newOldTask);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveOldTask(newOldTask);
        } catch (SQLException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        } catch (DocumentException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * @param taskId
     * @return Log File
     */
    public File getLogFile(String taskId) {
        String yearMonthString = Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1);
        File logsMonthDir = new File(getLogsDir(), yearMonthString);
        logsMonthDir.mkdir();
        File logFile = new File(logsMonthDir, taskId + "_" + DateFormatUtils.format(new Date(), TimeUtil.LONG_DATE_FORMAT_COMPACT) + ".log");

        return logFile;
    }

    @ApiModelProperty(hidden = true)
    public String getLastLogDataSource() throws ObjectNotFoundException, IOException {
        if (getLogFilenames().size() > 0) {
            Element logElement;
            File logFile = new File(getLogsDir(), getLogFilenames().get(0));
            try {
                SAXReader reader = new SAXReader();
                org.dom4j.Document document = reader.read(logFile);
                logElement = document.getRootElement();
            } catch (DocumentException e) {
                ArrayList<String> logFileContent = FileUtil.readFile(new File(getLogsDir(), getLogFilenames().get(0)));

                logElement = DocumentHelper.createElement("log");

                for (String line : logFileContent) {
                    logElement.addElement("line").addText(line);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XmlUtil.writePrettyPrint(baos, logElement);
            return baos.toString();
        }
        else {
            throw new ObjectNotFoundException("Log file NOT found!");
        }
    }

    /**
     * @param forceStop
     */
    public void stopIngest(boolean forceStop) {
        if (forceStop) {
            forceStopExecution = true;
        }
        log.warn("Received stop signal for execution of Data Source " + id + " of Data Provider " + ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(id));
        stopExecution = true;
    }

    /**
     * Function to show the number of records according #NumberFormat
     * 
     * @return
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
    @ApiModelProperty(hidden = true)
    public String[] getNumberRecords() throws IOException, DocumentException, SQLException {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
        RecordCount dataSourceCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id, getStatus() == StatusDS.RUNNING);
        String[] counts = new String[3];
        counts[0] = (dataSourceCount == null ? "0" : numberFormat.format(dataSourceCount.getCount()));
        counts[1] = (dataSourceCount == null ? "0" : numberFormat.format(dataSourceCount.getDeleted()));
        counts[2] = (dataSourceCount == null ? "0" : numberFormat.format(dataSourceCount.getCount() - dataSourceCount.getDeleted()));
        return counts;
    }

    /**
     * @return number of records
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
    @ApiModelProperty(hidden = true)
    public int getIntNumberRecords() throws IOException, DocumentException, SQLException {
        RecordCount dataSourceCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id, getStatus() == StatusDS.RUNNING);
        return (dataSourceCount == null ? 0 : dataSourceCount.getCount());
    }

    /**
     * @return number of deleted records
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     */
    @ApiModelProperty(hidden = true)
    public int getIntNumberDeletedRecords() throws IOException, DocumentException, SQLException {
        RecordCount dataSourceCount = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(id, getStatus() == StatusDS.RUNNING);
        return (dataSourceCount == null ? 0 : dataSourceCount.getDeleted());
    }

    /**
     * @return List of log file names
     */
    @ApiModelProperty(hidden = true)
    public List<String> getLogFilenames() {
        File logDir = getLogsDir();
        List<File> logDirnames = Arrays.asList(logDir.listFiles());
        List<String> logFilenames = new ArrayList<String>();

        for (File logMonthDir : logDirnames) {
            if (logMonthDir.isDirectory() && logMonthDir.listFiles().length > 0) {
                for (File logFile : logMonthDir.listFiles()) {
                    if (logFile.getName().endsWith(".log")) {
                        logFilenames.add(logMonthDir.getName() + File.separator + logFile.getName());
                    }
                }
            }
        }

        Collections.sort(logFilenames, new LogFilenameComparator());
        Collections.reverse(logFilenames);

        return logFilenames;
    }

    /**
     * @return File
     */
    @ApiModelProperty(hidden = true)
    public File getOutputDir() {
        File outputDir = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath(), id);
        outputDir.mkdir();
        return outputDir;
    }

    /**
     * @return File of logs dircetory
     */
    @ApiModelProperty(hidden = true)
    public File getLogsDir() {
        File logDir = new File(getOutputDir(), "logs");
        logDir.mkdir();
        return logDir;
    }

    /**
     * @return File of tasks directory
     */
    @ApiModelProperty(hidden = true)
    public File getTasksDir() {
        File tasksDir = new File(getOutputDir(), "tasks");
        tasksDir.mkdir();
        return tasksDir;
    }

    /**
     * @return File of export directory
     */
    public String getExportDir() {
        return exportDir;
    }

    /**
     * @param exportDirPath
     */
    public void setExportDir(String exportDirPath) {
        File file = new File(exportDirPath);
        exportDir = exportDirPath;
        //        exportDir.mkdir();
    }

    /**
     * @return String of new task id
     * @throws IOException
     */
    @ApiModelProperty(hidden = true)
    public String getNewTaskId() throws IOException {
        int nextId = getLastTaskId() + 1;
        setLastTaskId(nextId);
        return getId() + "_" + nextId;
    }

    /**
     * @return last task id
     * @throws IOException
     */
    protected int getLastTaskId() throws IOException {
        int lastId = 0;

        File lastTaskFile = new File(getTasksDir(), LAST_TASK_FILENAME);
        if (lastTaskFile.exists()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(lastTaskFile)));
            String currentLine;
            if ((currentLine = reader.readLine()) != null) {
                try {
                    lastId = Integer.parseInt(currentLine);
                } catch (NumberFormatException e) {
                    log.error("Trying to parse as int: " + currentLine, e);
                }
            }

        }

        return lastId;
    }

    /**
     * @param taskId
     * @throws IOException
     */
    protected void setLastTaskId(int taskId) throws IOException {
        File lastTaskFile = new File(getTasksDir(), LAST_TASK_FILENAME);
        if (lastTaskFile.exists()) {
            File backupFile = new File(lastTaskFile.getParent(), lastTaskFile.getName() + ".bkp");
            FileUtil.copyFile(lastTaskFile, backupFile);
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lastTaskFile)));
            writer.write(Integer.toString(taskId));
            writer.newLine();
        } catch (IOException e) {
            log.error("Error writing last task file", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * @param startDate
     * @return File
     */
    public File getNewLogFile(Date startDate) {
        String currentTime = new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT_COMPACT).format(startDate);
        return new File(getLogsDir(), currentTime + ".log");
    }

    /**
     * 
     */
    public void initAccessPoints() {
        // initialization of the default AccessPoints used internally by REPOX
        AccessPoint defaultTimestampAP = new TimestampAccessPoint(AccessPoint.PREFIX_INTERNAL_BD + id + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD);
        defaultTimestampAP.setIndexDeletedRecords(false);
        defaultTimestampAP.setRepoxInternal(true);
        this.accessPoints.put(defaultTimestampAP.getId(), defaultTimestampAP);

        AccessPoint defaultRecordAP = new RecordRepoxFullAccessPoint(AccessPoint.PREFIX_INTERNAL_BD + id + AccessPoint.SUFIX_RECORD_INTERNAL_BD);
        defaultRecordAP.setRepoxInternal(true);
        this.accessPoints.put(defaultRecordAP.getId(), defaultRecordAP);
    }

    private boolean equalsAccessPoints(HashMap<String, AccessPoint> accessPoints) {
        if (this.accessPoints == null && accessPoints == null) {
            return true;
        } else if (this.accessPoints == null || accessPoints == null || this.accessPoints.size() != accessPoints.size()) {
            return false;
        }

        Set<String> localAccessPointsIds = this.accessPoints.keySet();
        Set<String> otherAccessPointsIds = accessPoints.keySet();

        return localAccessPointsIds.containsAll(otherAccessPointsIds);
    }

    /**
     * @param dataSource
     * @return boolean
     */
    protected boolean equalsBaseProperties(DataSource dataSource) {
        return CompareUtil.compareObjectsAndNull(this.id, dataSource.getId()) && CompareUtil.compareObjectsAndNull(this.status, dataSource.getStatus()) && CompareUtil.compareObjectsAndNull(
                this.lastRunResult, dataSource.getLastRunResult()) && CompareUtil.compareObjectsAndNull(this.lastUpdate, dataSource.getLastUpdate()) && this.equalsAccessPoints(dataSource
                .getAccessPoints());
    }

    /**
     * @return boolean
     * @throws IOException
     */
    protected boolean emptyRecords() throws IOException {
        boolean successfulDeletion = true;

        //Delete records indexes
        for (AccessPoint accessPoint : this.getAccessPoints().values()) {
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().emptyIndex(this, accessPoint);
                log.info("Emptied AccessPoint with id " + accessPoint.getId());
            } catch (Exception e) {
                log.error("Unable to empty Table from Database: " + accessPoint.getId(), e);
                successfulDeletion = false;
            }
        }

        //Remove from Record Counts cache
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().removeDataSourceCounts(id);

        return successfulDeletion;
    }

    /**
     * @param logFile
     * @param fullIngest
     * @return Tasj.Status
     * @throws Exception
     */
    public abstract Task.Status ingestRecords(File logFile, boolean fullIngest) throws Exception;

    /**
     * @return boolean
     */
    @ApiModelProperty(hidden = true)
    public abstract boolean isWorking();

    /**
     * @return The class of the local IDs of this Data Source.
     */
    @ApiModelProperty(hidden = true)
    public Class getClassOfLocalId() {
        return String.class;
    }

    /**
     * Obtains the date of the last synchronization of this DataSource
     * 
     * @param syncDateFile
     * @return String of the synchronization date
     */
    @ApiModelProperty(hidden = true)
    public String getSynchronizationDate(File syncDateFile) {
        if (!syncDateFile.exists()) {
            return "";
        }
        String dateString = FileUtil.readFile(syncDateFile, 0);
        try {
            return dateString;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @return String of the synchronization date
     * @throws IOException
     */
    @ApiModelProperty(hidden = true)
    public String getSynchronizationDateString() throws IOException {
        return DateUtil.date2String(lastUpdate, TimeUtil.LONG_DATE_FORMAT);
    }

    /**
     * @return Get a sample number
     */
    @ApiModelProperty(hidden = true)
    public int getSampleNumber() {
        File syncDateFile = getSyncDateFile();
        if (!syncDateFile.exists()) {
            return -1;
        }
        String sample = FileUtil.readFile(syncDateFile, 1);
        return Integer.valueOf(sample);
    }

    /**
     * @param syncDateFile
     * @return Get a sample number
     */
    public int getSampleNumber(File syncDateFile) {
        if (!syncDateFile.exists()) {
            return -1;
        }
        String sample = FileUtil.readFile(syncDateFile, 1);
        try {
            return Integer.valueOf(sample);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * @return File
     */
    @ApiModelProperty(hidden = true)
    public File getSyncDateFile() {
        return new File(getOutputDir(), "synchronization-date.txt");
    }

    /**
     * @param oldDataSourceId
     * @param newDataSourceId
     * @throws IOException
     */
    public void renameDataSourceDir(String oldDataSourceId, String newDataSourceId) throws IOException {
        File oldDataSourceDir = getDataSourceDir(oldDataSourceId);

        RepoxConfiguration configuration = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration();
        File newSourceDir = new File(configuration.getRepositoryPath(), newDataSourceId);

        if (oldDataSourceDir.exists()) {
            oldDataSourceDir.renameTo(newSourceDir);
        }
    }

    /**
     * @return File
     */
    @ApiModelProperty(hidden = true)
    public File getDataSourceDir() {
        return getDataSourceDir(id);
    }

    /**
     * @param dataSourceId
     * @return File
     */
    public File getDataSourceDir(String dataSourceId) {
        RepoxConfiguration configuration = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration();
        File dataSourceDir = new File(configuration.getRepositoryPath(), dataSourceId);
        dataSourceDir.mkdir();
        return dataSourceDir;
    }

    /**
     * @param dataSourceId
     * @return File
     */
    @ApiModelProperty(hidden = true)
    public File getDataSourceLogsDir(String dataSourceId) {
        File logDir = new File(getDataSourceDir(dataSourceId), "logs");
        logDir.mkdir();
        return logDir;
    }

    /**
     * @param dataSourceId
     * @param startDate
     * @return File
     */
    @ApiModelProperty(hidden = true)
    public File getNewDataSourceLogFile(String dataSourceId, Date startDate) {
        String currentTime = new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT_COMPACT).format(startDate);
        return new File(getDataSourceLogsDir(dataSourceId), currentTime + ".log");
    }

    /**
     * Empty repository directory, record count and specific Data Source
     * temporary files and directories.
     * 
     * @throws IOException
     * @throws DocumentException
     */
    public void cleanUp() throws IOException, DocumentException {
        try {
            // stop the ingestion task
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().stopIngestDataSource(id, Task.Status.FORCE_EMPTY);
        } catch (NoSuchMethodException e) {
        } catch (ObjectNotFoundException e) {
        } catch (ClassNotFoundException e) {
        } catch (ParseException e) {
        }

        //Delete records indexes
        for (AccessPoint accessPoint : getAccessPoints().values()) {
            try {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().emptyIndex(this, accessPoint);
                log.info("Emptied AccessPoint with id " + accessPoint.getId());
            } catch (Exception e) {
                log.error("Unable to empty Table from Database: " + accessPoint.getId(), e);
            }
        }
        if (getMaxRecord4Sample() != -1) {
            setMaxRecord4Sample(-1);
        }

        File dataSourceDir = getDataSourceDir();
        if (dataSourceDir.exists()) {

            try {
                File tasksDir = new File(dataSourceDir, "tasks");
                FileUtils.deleteDirectory(tasksDir);
                log.info("Deleted Data Source task's dir with success from Data Source with id " + id);
                //                dataSourceDir.mkdir();

            } catch (IOException e) {
                log.error("Unable to delete Data Source dir from Data Source with id " + id);
            }
        }

        //clear the status
        setStatus(null);

        //Clear the last ingest date
        setLastUpdate(null);

        //Clear Old tasks
        //        oldTasksList.clear();

        //Delete Record Counts cache
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().removeDataSourceCounts(id);

        //Update XML file
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        //        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().removeOldTasks(id);
    }

    /**
     * @param sourceElement
     * @return Element
     */
    public abstract Element addSpecificInfo(Element sourceElement);

    /**
     * @return Element
     */
    public Element createElement() {
        try {
            Element sourceElement = DocumentHelper.createElement("source");
            sourceElement.addAttribute("id", getId());
            sourceElement.addAttribute("metadataFormat", getMetadataFormat());

            if (getMarcFormat() != null && !getMarcFormat().isEmpty())
                sourceElement.addAttribute("marcFormat", getMarcFormat());

            sourceElement.addAttribute("schema", getSchema());
            sourceElement.addAttribute("namespace", getNamespace());
            sourceElement.addAttribute("lastIngest", getLastUpdate() != null ? getSynchronizationDateString() : "");
            sourceElement.addAttribute("isSample", String.valueOf(isSample));
            sourceElement.addAttribute("sample", String.valueOf(getMaxRecord4Sample()));
            sourceElement.addAttribute("status", getStatus() != null ? getStatus().toString() : "");
            sourceElement.addElement("description").setText(getDescription());
            sourceElement
                    .addElement("exportDirPath")
                    .setText(
                            getExportDir() != null ? getExportDir() : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath() + "/" + getId() + "/" + "export");

            Element recordIdPolicyNode = sourceElement.addElement("recordIdPolicy");
            recordIdPolicyNode.addAttribute("type", getRecordIdPolicy().getClass().getSimpleName());
            if (getRecordIdPolicy() instanceof IdGeneratedRecordIdPolicy) {
            } else if (getRecordIdPolicy() instanceof IdProvidedRecordIdPolicy) {
            } else if (getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy) {
                IdExtractedRecordIdPolicy idExtracted = (IdExtractedRecordIdPolicy)getRecordIdPolicy();
                recordIdPolicyNode.addElement("idXpath").setText(idExtracted.getIdentifierXpath());
                if (idExtracted.getNamespaces() != null && !idExtracted.getNamespaces().isEmpty()) {
                    Element namespacesElement = recordIdPolicyNode.addElement("namespaces");
                    for (String currentKey : idExtracted.getNamespaces().keySet()) {
                        Element namespaceElement = namespacesElement.addElement("namespace");
                        namespaceElement.addElement("namespacePrefix").setText(currentKey);
                        namespaceElement.addElement("namespaceUri").setText(idExtracted.getNamespaces().get(currentKey));
                    }
                }
            } else {
                throw new RuntimeException("Invalid RecordIdPolicy of class " + getRecordIdPolicy().getClass().getName());
            }

            // fill with specific information according to the data source type (OAI, Folder, Z39.50)
            sourceElement = addSpecificInfo(sourceElement);

            //Add MetadataTransformations
            Element metadataTransformationsNode = sourceElement.addElement("metadataTransformations");
            if (getMetadataTransformations() != null && !getMetadataTransformations().isEmpty()) {
                for (MetadataTransformation metadataTransformation : getMetadataTransformations().values()) {
                    if (metadataTransformation != null) {
                        Element metadataTransformationNode = metadataTransformationsNode.addElement("metadataTransformation");
                        metadataTransformationNode.setText(metadataTransformation.getId());
                    }
                }
            }

            //Add ExternalServices
            Element externalServicesNode = sourceElement.addElement("restServices");
            if (externalServicesRunType != null && getExternalRestServices().size() > 0)
                externalServicesNode.addAttribute("executeType", externalServicesRunType.name());
            if (getExternalRestServices().size() > 0) {
                for (ExternalRestService externalRestService : getExternalRestServices()) {
                    if (externalRestService != null) {
                        Element externalServiceNode = externalServicesNode.addElement("restService");
                        externalServiceNode.addAttribute("id", externalRestService.getId());
                        externalServiceNode.addAttribute("uri", externalRestService.getUri());
                        externalServiceNode.addAttribute("statusUri", externalRestService.getStatusUri());
                        externalServiceNode.addAttribute("externalResultsUri", externalRestService.getExternalResultsUri() == null ? "" : externalRestService.getExternalResultsUri());
                        externalServiceNode.addAttribute("name", externalRestService.getName());
                        externalServiceNode.addAttribute("type", externalRestService.getType());
                        externalServiceNode.addAttribute("isEnabled", String.valueOf(externalRestService.isEnabled()));
                        externalServiceNode.addAttribute("externalServiceType", externalRestService.getExternalServiceType().name());
                        Element serviceParameters = externalServiceNode.addElement("parameters");
                        for (ServiceParameter serviceParameter : externalRestService.getServiceParameters()) {
                            if (serviceParameter.getValue() != null) {
                                Element serviceParameterNode = serviceParameters.addElement("parameter");
                                serviceParameterNode.addAttribute("name", serviceParameter.getName());
                                serviceParameterNode.addAttribute("value", serviceParameter.getValue());
                                serviceParameterNode.addAttribute("type", serviceParameter.getType());
                                serviceParameterNode.addAttribute("semantics", serviceParameter.getSemantics());
                                serviceParameterNode.addAttribute("required", String.valueOf(serviceParameter.getRequired()));
                            }
                        }
                    }
                }
            }

            addTagsXML(sourceElement);

            return sourceElement;
        } catch (IOException e) {
            return null;
        }
    }

    private void addTagsXML(Element sourceElement) {
        if (getTags().size() > 0) {
            Element tagsNode = sourceElement.addElement("tags");
            for (DataSourceTag dataSourceTag : getTags()) {
                if (dataSourceTag != null) {
                    Element tagNode = tagsNode.addElement("tag");
                    tagNode.addAttribute("name", dataSourceTag.getName());
                }
            }
        }
    }

    public abstract int getNumberOfRecords2Harvest();

    @ApiModelProperty(hidden = true)
    public abstract String getNumberOfRecords2HarvestStr();

    public abstract int getNumberOfRecordsPerResponse();

    public abstract List<Long> getStatisticsHarvester();

    /**
     * @return percentage
     */
    @ApiModelProperty(hidden = true)
    public float getPercentage() {
        try {
            if (getNumberOfRecords2Harvest() == 0)
                return 0;
            else
                return (getIntNumberRecords() * 100) / getNumberOfRecords2Harvest();
        } catch (Exception e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
            return -1;
        }
    }

    /**
     * @return time left
     */
    @ApiModelProperty(hidden = true)
    public long getTimeLeft() {
        try {
            int recordsLeft = getNumberOfRecords2Harvest() - getIntNumberRecords();
            int segmentsLeft = recordsLeft / getNumberOfRecordsPerResponse();
            long value = segmentsLeft * getAverageOfSecondsPerIngest();
            return value > 0 ? value : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private long getAverageOfSecondsPerIngest() {
        if (getStatisticsHarvester().size() > 0) {
            long sum = 0;
            for (long time : getStatisticsHarvester()) {
                sum += time;
            }
            return (long)sum / getStatisticsHarvester().size();
        }
        return -1;
    }

    /**
     * @param type
     * @param logFile
     * @return ExternalServiceStates.ServiceExitState
     */
    protected ExternalServiceStates.ServiceExitState runExternalServices(String type, File logFile) {
        try {
            if (type.equals("PRE_PROCESS"))
                status = StatusDS.PRE_PROCESSING;
            else
                status = StatusDS.POST_PROCESSING;

            // Special case for only 1 post_process no monitor process
            if (type.equals("POST_PROCESS") && runNoMonitorProcesses(logFile))
                return ExternalServiceStates.ServiceExitState.SUCCESS;

            if (containsExternalServicesOfType(type)) {
                ExternalRestServiceContainer externalRestServiceContainer = new ExternalRestServiceContainer(externalServicesRunType);

                for (ExternalRestService externalRestService : externalRestServices) {
                    if (externalRestService.isEnabled() && externalRestService.getType().equals(type)) {
                        externalRestServiceContainer.addExternalService(new ExternalRestServiceThread(externalRestService, externalRestServiceContainer, logFile));
                    }
                }
                while (!externalRestServiceContainer.getContainerRunningState().equals(ExternalServiceStates.ServiceRunningState.FINISHED) && externalRestServiceContainer.getServiceThreads().size() > 0) {
                    Thread.sleep(3000);
                }
                return externalRestServiceContainer.getContainerExitState();
            } else
                return ExternalServiceStates.ServiceExitState.NONE;
        } catch (InterruptedException e) {
            return ExternalServiceStates.ServiceExitState.ERROR;
        }
    }

    private boolean runNoMonitorProcesses(File logFile) {
        boolean hasNoMonitorProcesses = false;
        for (ExternalRestService externalRestService : externalRestServices) {
            if (externalRestService instanceof ExternalServiceNoMonitor) {
                new ExternalNoMonitorServiceThread((ExternalServiceNoMonitor)externalRestService, logFile).start();
                hasNoMonitorProcesses = true;
            }
        }
        return hasNoMonitorProcesses;
    }

    /**
     * @param type
     * @return boolean
     */
    protected boolean containsExternalServicesOfType(String type) {
        boolean contains = false;
        for (ExternalRestService externalRestService : externalRestServices) {
            if (externalRestService.getType().equals(type)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * @param exitState
     * @param type
     * @return StatusDS
     */
    protected StatusDS checkProcessingState(ExternalServiceStates.ServiceExitState exitState, String type) {
        StatusDS status = StatusDS.OK;
        if (exitState.equals(ExternalServiceStates.ServiceExitState.ERROR) && type.equals("PRE_PROCESS")) {
            status = StatusDS.PRE_PROCESS_ERROR;
        } else if (exitState.equals(ExternalServiceStates.ServiceExitState.ERROR) && type.equals("POST_PROCESS")) {
            status = StatusDS.POST_PROCESS_ERROR;
        }
        return status;
    }

    /**
     * @param format
     * @return boolean
     */
    public boolean hasTransformation(String format) {
        for (MetadataTransformation metadataTransformation : getMetadataTransformations().values()) {
            if (metadataTransformation.getDestinationSchemaId().equals(format)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param batchRecords
     */
    protected void addDeletedRecords(List<RecordRepox> batchRecords) {
        for (RecordRepox recordRepox : batchRecords) {
            if (recordRepox.isDeleted())
                lastIngestDeletedCount++;
        }
    }

    public static String generateId(String name) {
        String generatedIdPrefix = "";

        for (int i = 0; (i < name.length() && i < 32); i++) {
            if ((name.charAt(i) >= 'a' && name.charAt(i) <= 'z') || (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') || (name.charAt(i) >= '0' && name.charAt(i) <= '9')) {
                generatedIdPrefix += name.charAt(i);
            }
        }
        generatedIdPrefix += "r";

        String fullId = generatedIdPrefix + generateNumberSufix(generatedIdPrefix);

        return fullId;
    }

    private static int generateNumberSufix(String basename) {
        int currentNumber = 0;
        String currentFullId = basename + currentNumber;

        try {
            while (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(currentFullId) != null) {
                currentNumber++;
                currentFullId = basename + currentNumber;
            }
        } catch (DocumentException e) {
            throw new RuntimeException("Caused by DocumentException", e);
        } catch (IOException e) {
            throw new RuntimeException("Caused by IOException", e);
        }

        return currentNumber;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        /*
         * VelocityEngine ve = new VelocityEngine(); ve.init(); Template t =
         * ve.getTemplate( "src/main/resources/emailExitStatus.vm" );
         * VelocityContext context = new VelocityContext(); context.put("id",
         * "44"); context.put("exitStatus","exitStatus.toString()");
         * StringWriter writer = new StringWriter(); t.merge( context, writer );
         * String message = writer.toString(); System.out.println(message);
         */
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            factory.setSchema(schemaFactory.newSchema(new Source[] { new StreamSource("http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd") }));

            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new InputSource("C:\\Users\\GPedrosa\\Desktop\\testeValidate\\teste.xml"));

        } catch (ParserConfigurationException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
