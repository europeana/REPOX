package harvesterUI.shared.tasks;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 26-03-2011
 * Time: 15:34
 */
public class RunningTask extends HarvestTask {

    public RunningTask() {}

    public RunningTask(String dataSet, String type,String runnableClass, String id, String fullIngest,
                       String status, String retries, String retryMax, String delay, String startTime){
        set("dataSet", dataSet);
        set("type",type);
        set("runnableClass",runnableClass);
        set("id",id);
        set("fullIngest",fullIngest);
        set("status",status);
        set("retries",retries);
        set("retryMax",retryMax);
        set("delay",delay);
        set("startTime",startTime);

        createTaskListStrings();
    }

    public String getDataSet() {return (String) get("dataSet");}
    public void setIngestStatus(String ingestStatus){set("ingestStatus",ingestStatus);}
    public String getIngestStatus() {return (String) get("ingestStatus");}

    public void setExportDirectory(String exportDirectory){set("exportDirectory",exportDirectory);}
    public String getExportDirectory() {return (String) get("exportDirectory");}

    public void setRecordsPerFile(String recordsPerFile){set("recordsPerFile",recordsPerFile);}
    public String getRecordsPerFile() {return (String) get("recordsPerFile");}

    public String getRunnableClass() {return (String) get("runnableClass");}
    public String getStatus() {return (String) get("status");}
    public String getRetries() {return (String) get("retries");}
    public String getRetryMax() {return (String) get("retryMax");}
    public String getDelay() {return (String) get("delay");}
    public String getStartTime() {return (String) get("startTime");}
    public String getType() {return (String) get("type");}

    public void createTaskListStrings() {
        if(getType().equals("DATA_SOURCE_INGEST"))
            set("listType","Data Set Importer");
        else
            set("listType","Data Set Export");
        set("parameters","Data Set: " + getDataSet() +" -- Full Ingest: " + getFullIngest());
        set("statusListString", getStatus() + " (" + getRetries() + " of " + getRetryMax() + ")");
    }
}
