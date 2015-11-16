package pt.utl.ist.task;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import pt.utl.ist.util.CompareUtil;
import pt.utl.ist.util.RunnableStoppable;
import pt.utl.ist.util.TimeUtil;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 */
@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "taskType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ScheduledTask.class, name = "SCHEDULED")
})
@XmlSeeAlso({ ScheduledTask.class, DataSourceIngestTask.class, DataSourceExportTask.class })
@ApiModel(value = "A Task", discriminator = "taskType", subTypes = { ScheduledTask.class, DataSourceIngestTask.class, DataSourceExportTask.class })
public abstract class Task {
    private static final Logger log = Logger.getLogger(Task.class);

    /** The possible status of the Task: OK, WARNINGS, ERRORS, CANCELED, FAILED */
    public enum Status {
        OK, WARNINGS, ERRORS, CANCELED, FAILED, FORCE_EMPTY;

        public boolean isSuccessful() {
            return (equals(OK));
        }
        
        public boolean isWarnings() {
          return (equals(WARNINGS));
        }

        public boolean isCanceled() {
            return (equals(CANCELED));
        }

        public boolean isForceEmpty() {
            return (equals(FORCE_EMPTY));
        }
    }

    @ApiModelProperty(hidden = true)
    RunnableStoppable                            runnableTask;          // RunnableStoppable instance that will run the task
    @ApiModelProperty(hidden = true)
    protected Class<? extends RunnableStoppable> taskClass;             // class assignable to RunnableStoppable that will be instantiated to run
    @ApiModelProperty(hidden = true)
    protected String[]                           parameters;            // Parameters to instantiate object of class taskClass
    @ApiModelProperty(hidden = true)
    protected Thread                             taskThread;
    @ApiModelProperty(hidden = true)
    protected Calendar                           startTime;
    @ApiModelProperty(hidden = true)
    protected Calendar                           finishTime;
    @ApiModelProperty(hidden = true)
    protected Status                             status     = Status.OK;
    @ApiModelProperty(hidden = true)
    protected int                                maxRetries = 3;
    @ApiModelProperty(hidden = true)
    protected int                                retries    = 0;        // Retries already performed, ceiling at maxRetries
    @ApiModelProperty(hidden = true)
    protected long                               retryDelay = 300;      // Delay sleep time in seconds (5 minutes) 300
    @ApiModelProperty(hidden = true)
    protected Calendar                           failTime;              // Time of failure, required to calculate next retry

    public Class<? extends RunnableStoppable> getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(Class<? extends RunnableStoppable> taskClass) {
        this.taskClass = taskClass;
    }

    protected abstract int getNumberParameters();

    protected Boolean getParameterBoolean(int index) {
        String parameter = getParameter(index);
        if (parameter != null) {
            return Boolean.parseBoolean(parameter);
        } else {
            return null;
        }
    }

    /**
     * @param index
     * @return String of the parameter at index
     */
    protected String getParameter(int index) {
        if (parameters != null && parameters.length > 0) { return parameters[index]; }

        return null;
    }

    /**
     * @param index
     * @param parameter
     */
    protected void setParameter(int index, String parameter) {
        if (parameters == null) {
            parameters = new String[getNumberParameters()];
        }

        parameters[index] = parameter;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    public Thread getTaskThread() {
        return taskThread;
    }

    public void setTaskThread(Thread taskThread) {
        this.taskThread = taskThread;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Calendar finishTime) {
        this.finishTime = finishTime;
    }

    public Status getStatus() {
        if (runnableTask != null && IngestDataSource.class.isAssignableFrom(taskClass)) {
            IngestDataSource ingestDataSource = (IngestDataSource)runnableTask;
            status = ingestDataSource.getExitStatus();
        }

        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public long getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }

    public Calendar getFailTime() {
        return failTime;
    }

    public void setFailTime(Calendar failTime) {
        this.failTime = failTime;
    }

    /**
     * Creates a new instance of this class.
     */
    public Task() {
    }

    /**
     * Creates a new instance of this class.
     * @param taskClass
     * @param parameters
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public Task(Class<? extends RunnableStoppable> taskClass, String[] parameters) throws SecurityException, NoSuchMethodException {
        super();

        this.taskClass = taskClass;
        this.parameters = parameters;

        Class[] paramTypes = null;
        if (parameters != null) {
            paramTypes = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                paramTypes[i] = String.class;
            }
        }
        taskClass.getConstructor(paramTypes);
    }

    /**
     * Creates a new instance of this class.
     * @param taskClass
     * @param parameters
     * @param startTime
     * @param finishTime
     * @param status
     * @param maxRetries
     * @param retries
     * @param retryDelay
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public Task(Class<? extends RunnableStoppable> taskClass, String[] parameters, Calendar startTime, Calendar finishTime, Status status, int maxRetries, int retries, long retryDelay) throws SecurityException, NoSuchMethodException {
        this(taskClass, parameters);
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.status = status;
        this.maxRetries = maxRetries;
        this.retries = retries;
        this.retryDelay = retryDelay;
    }

    @ApiModelProperty(hidden = true)
    public boolean isRunning() {
        return (taskThread != null && taskThread.isAlive());
    }

    protected boolean isTimeToRetry(Calendar calendar) {
        long minRetryDelayInMillis = retryDelay * 1000;
        long failTimeDiffInMillis = calendar.getTimeInMillis() - failTime.getTimeInMillis();
        return retries < maxRetries && failTimeDiffInMillis > minRetryDelayInMillis;
    }

    public boolean isTimeToRun(Calendar calendar) {
        return !isRunning() && (failTime == null || isTimeToRetry(calendar));
    }

    /**
     * @param status
     */
    public void stop(Status status) {
        if (isRunning()) {
            finishTime = Calendar.getInstance();
            this.status = status;

            if (runnableTask instanceof IngestDataSource) {
                ((IngestDataSource)runnableTask).setExitStatus(status);
            }
            runnableTask.stop();
        }
    }

    /**
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public void startTask() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        Class[] paramTypes = null;
        if (parameters != null) {
            paramTypes = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                paramTypes[i] = String.class;
            }
        }

        runnableTask = taskClass.getConstructor(paramTypes).newInstance((Object[])parameters);
        taskThread = new Thread(runnableTask);
        startTime = Calendar.getInstance();
        taskThread.start();
    }

    /**
     * Save the configuration of this object to XML Element taskElement
     * @param taskElement 
     */
    public void setXml(Element taskElement) {
        taskElement.addAttribute("type", TaskFactory.getType(this.getClass()).toString());

        taskElement.addElement("runnableClass").setText(taskClass.getName());
        for (String currentParameter : parameters) {
            taskElement.addElement("parameter").setText(currentParameter);
        }
        if (status != null) {
            taskElement.addElement("status").setText(status.toString());
        } else {
            taskElement.addElement("status").setText(Status.ERRORS.toString());
        }
        Element retriesElement = taskElement.addElement("retries");
        retriesElement.addAttribute("max", String.valueOf(maxRetries));
        retriesElement.addAttribute("delay", String.valueOf(retryDelay));
        retriesElement.setText(String.valueOf(retries));
        if (startTime != null) {
            Element startTimeElement = taskElement.addElement("startTime");
            startTimeElement.setText((new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT)).format(startTime.getTime()));
        }
        if (finishTime != null) {
            Element finishTimeElement = taskElement.addElement("finishTime");
            finishTimeElement.setText((new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT)).format(finishTime.getTime()));
        }
    }

    /**
     * Load the configuration of this object from XML Element taskElement
     * @param taskElement 
     * @throws ClassNotFoundException 
     */
    public void getXml(Element taskElement) throws ClassNotFoundException {
        List<Element> parameterElements = taskElement.elements("parameter");

        taskClass = (Class<RunnableStoppable>)Class.forName(taskElement.elementText("runnableClass"));
        if (parameterElements != null) {
            parameters = new String[parameterElements.size()];
            for (int i = 0; i < parameterElements.size(); i++) {
                parameters[i] = (((Element)parameterElements.get(i))).getText();
            }
        }

        status = Task.Status.valueOf(taskElement.elementText("status"));

        if (taskElement.element("retries") != null) {
            maxRetries = Integer.parseInt(taskElement.element("retries").attributeValue("max"));
            retryDelay = Integer.parseInt(taskElement.element("retries").attributeValue("delay"));
            retries = Integer.parseInt(taskElement.elementText("retries"));
        }

        if (taskElement.elementText("startTime") != null) {
            try {
                startTime = Calendar.getInstance();
                startTime.setTime(new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT).parse(taskElement.elementText("startTime")));
            } catch (ParseException e) {
                log.error("Unable to parse startTime", e);
            }
        }

        if (taskElement.elementText("finishTime") != null) {
            try {
                finishTime = Calendar.getInstance();
                finishTime.setTime(new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT).parse(taskElement.elementText("finishTime")));
            } catch (ParseException e) {
                log.error("Unable to parse finishTime", e);
            }
        }
    }

    /**
     * Tests if this and otherTask have the same taskClass and parameters (which means the executed action will be
     * the same, though they may have been created separately).
     *
     * @param otherTask
     * @return boolean indicating if there is a difference
     */
    public boolean equalsAction(Task otherTask) {
        return CompareUtil.compareObjectsAndNull(taskClass, otherTask.getTaskClass()) && equalActionParameters(otherTask);
    }

    /**
     * @param otherTask
     * @return boolean indicating if there is a difference
     */
    public abstract boolean equalActionParameters(Task otherTask);

    @Override
    public boolean equals(Object obj) {
        Task otherTask = (Task)obj;

        return CompareUtil.compareObjectsAndNull(taskClass, otherTask.getTaskClass()) && CompareUtil.compareArraysAndNull(parameters, otherTask.getParameters()) && CompareUtil.compareObjectsAndNull(startTime, otherTask.getStartTime()) && CompareUtil.compareObjectsAndNull(finishTime, otherTask.getFinishTime()) && CompareUtil.compareObjectsAndNull(status, otherTask.getStatus()) && CompareUtil.compareObjectsAndNull(maxRetries, otherTask.getMaxRetries()) && CompareUtil.compareObjectsAndNull(retries, otherTask.getRetries()) && CompareUtil.compareObjectsAndNull(retryDelay, otherTask.getRetryDelay());
    }

    @Override
    public String toString() {
        String output = "{taskClass: " + taskClass;
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                output += "; parameter[" + i + "]: " + parameters[i];
            }
        }
        output += "; status: " + status.toString();
        output += "; maxRetries: " + maxRetries;
        output += "; retryDelay: " + retryDelay;
        output += "; retries: " + retries;

        if (startTime != null) {
            output += "; startTime: " + new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT).format(startTime.getTime());
        }

        if (finishTime != null) {
            output += "; finishTime: " + new SimpleDateFormat(TimeUtil.LONG_DATE_FORMAT).format(finishTime.getTime());
        }

        output += "}";
        return output;
    }

    /**
     * Return true if the task can be removed from execution list and false otherwise, independently of having been finished.
     */
    @ApiModelProperty(hidden = true)
    public boolean isTimeToRemove() {
        return true;
    }

}
