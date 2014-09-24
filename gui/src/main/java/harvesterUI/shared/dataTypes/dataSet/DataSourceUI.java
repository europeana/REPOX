package harvesterUI.shared.dataTypes.dataSet;

import com.google.gwt.i18n.client.DateTimeFormat;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.externalServices.ExternalServiceUI;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 11-04-2011
 * Time: 13:08
 */
public class DataSourceUI extends DataContainer {

    private List<String> namespaceList;
    private List<TransformationUI> metadataTransformations;
    private List<ScheduledTaskUI> scheduledTasksListUI;
    private List<ExternalServiceUI> restServiceUIs;
    private List<OldTaskUI> oldTaskUIList;

    private String marcFormat;
    private boolean useLastUpdateDate;
    private boolean isSample;
    private String deletedRecords;
    private List<DataSetTagUI> tags;

    // Eudml Only
    private boolean storeInYadda;
    private String yaddaCollection;

    public DataSourceUI() {}

    public DataSourceUI(DataProviderUI parent, String name, String dataSourceSet, String mtdFormat, String ingest, String country,
                        String description, String nameCode, String oaiSource, String oaiSet, String language,
                        String recordIdPolicy, String sourceMDFormat) {
        super(dataSourceSet);
        set("parent",parent);
        set("name",name);
        set("dataSourceSet", dataSourceSet);
        set("metadataFormat", mtdFormat);
        set("ingest", ingest);
        set("country",country);
        set("description",description);
        set("nameCode",nameCode);
        set("oaiSource",oaiSource);
        set("oaiSet",oaiSet);
        set("language",language);
        set("recordIdPolicy",recordIdPolicy);
        set("sourceMDFormat",sourceMDFormat);

        namespaceList = new ArrayList<String>();
        metadataTransformations = new ArrayList<TransformationUI>();
        scheduledTasksListUI = new ArrayList<ScheduledTaskUI>();
        oldTaskUIList = new ArrayList<OldTaskUI>();
        restServiceUIs = new ArrayList<ExternalServiceUI>();

        useLastUpdateDate = false;

        setFiltered(false);
    }

    public void setUsed(Date used){
        set("usedDate", used);
        DateTimeFormat formatter = DateTimeFormat.getFormat("dd/MM/yyyy -- HH:mm:ss");
        String result = formatter.format(used) + "   " + getDataSourceSet();

        set("used",result);
    }
    public Date getUsedDate(){return (Date) get("usedDate");}
//    public String getUsedString(){return (String) get("usedString");}

    public void setType(String type){set("type", type);}

    public void setLastIngest(Date lastIngest){
        set("lastIngestDate",lastIngest);
//        DateTimeFormat formatter = DateTimeFormat.getFormat("dd/MM/yyyy -- HH:mm");
//        String result = formatter.format(lastIngest);
//        set("lastIngest", result);
//        getDataSetParent().set("lastIngest",result);
    }
//    public void setLastIngest(String lastIngest){
//        set("lastIngest", lastIngest);
//        getDataSetParent().set("lastIngest",lastIngest);
//
//        DateTimeFormat formatter = DateTimeFormat.getFormat("dd/MM/yyyy -- HH:mm");
//        Date result = formatter.parse(lastIngest);
//        set("lastIngestDate",result);
//    }
    public Date getLastIngest(){return (Date) get("lastIngestDate");}

    public void setFiltered(boolean filtered){set("filtered", filtered);}
    public boolean getFiltered() {return (Boolean) get("filtered");}

    public DataProviderUI getDataSetParent(){return (DataProviderUI) get("parent");}

    public void setNextIngest(String nextIngest){set("nextIngest", nextIngest);}
    public String getNextIngest(){return (String) get("nextIngest");}

    public void setRecords(String records){set("records", records);}
    public String getRecords(){return (String) get("records");}

//    public void setIntRecords(Integer recordsInt){set("recordsInt", recordsInt);}
//    public Integer getIntRecords(){return (Integer) get("recordsInt");}

    public void setTotalRecords(int totalRecords){set("totalRecords", totalRecords);}
    public Integer getTotalRecords() {return (Integer) get("totalRecords");}

    public void setTotalRecordsStr(String totalRecordsStr){set("totalRecordsStr", totalRecordsStr);}
    public String getTotalRecordsStr() {return (String) get("totalRecordsStr");}

    public void setIngestPercentage(float percentage){set("percentage", percentage);}
    public float getIngestPercentage() {return (Float) get("percentage");}

    public void setIngestTimeLeft(long timeLeft){set("timeLeft", timeLeft);}
    public long getIngestTimeLeft() {return (Long) get("timeLeft");}
    
//    public void setRecordsPerResponse(int recordsPerResponse){set("recordsPerResponse", recordsPerResponse);}
//    public Integer getRecordsPerResponse() {return (Integer) get("recordsPerResponse");}

//    public void setAverageIngestTime(long averageIngestTime){set("averageIngestTime", averageIngestTime);}
//    public Long getAverageIngestTime() {return (Long) get("averageIngestTime");}

    public void setURLSourcesPath(String urlSourcesPath){set("urlSourcesPath", urlSourcesPath);}
    public String getURLSourcesPath(){return (String) get("urlSourcesPath");}

    // Parallel execution or Sequential
    public void setExternalServicesRunType(String externalServicesRunType){set("externalServicesRunType", externalServicesRunType);}
    public String getExternalServicesRunType() {return (String) get("externalServicesRunType");}

    public void setName(String name){set("name", name);}
    public String getName(){return (String) get("name");}

    public String getCountry(){return (String) get("country");}

    public void setMetadataFormat(String mtdFormat){set("metadataFormat", mtdFormat);}
    public String getMetadataFormat() {return (String) get("metadataFormat");}

    public void setSourceMDFormat(String sourceMDFormat){set("sourceMDFormat", sourceMDFormat);}
    public String getSourceMDFormat() {return (String) get("sourceMDFormat");}

    public void setHasRunningTask(boolean hasRunningTask){set("hasRunningTask", hasRunningTask);}
    public boolean getHasRunningTask() {return (Boolean) get("hasRunningTask");}

    public void setDataSourceSet(String dsSet){set("dataSourceSet", dsSet);}
    public String getDataSourceSet(){return (String) get("dataSourceSet");}

    public void setIngest(String ingest){set("ingest", ingest);}
    public String getIngest(){return (String) get("ingest");}

    public void setDescription(String description){set("description", description);}
    public String getDescription(){return (String) get("description");}

    public void setIsoVariant(String isoVariant){set("isoVariant", isoVariant);}
    public String getIsoVariant(){return (String) get("isoVariant");}

    public void setNameCode(String nameCode){set("nameCode", nameCode);}
    public String getNameCode(){return (String) get("nameCode");}

    public void setCharacterEncoding(String characterEncoding){set("characterEncoding", characterEncoding);}
    public String getCharacterEncoding() {return (String) get("characterEncoding");}

    public void setRecordIdPolicy(String recordIdPolicy){set("recordIdPolicy", recordIdPolicy);}
    public String getRecordIdPolicy(){return (String) get("recordIdPolicy");}

    public List<TransformationUI> getMetadataTransformations(){return metadataTransformations;}
    public void setMetadataTransformations(List<TransformationUI> transformations){metadataTransformations = transformations;}

    public void setSchema(String schema){set("schema", schema);}
    public String getSchema(){return (String) get("schema");}

    public void setMetadataNamespace(String metadataNamespace){set("metadataNamespace", metadataNamespace);}
    public String getMetadataNamespace(){return (String) get("metadataNamespace");}

    public void setExportDirectory(String exportDirectory){set("exportDirectory", exportDirectory);}
    public String getExportDirectory(){return (String) get("exportDirectory");}

    public void setRecordsPerFile(String recordsPerFile){set("recordsPerFile", recordsPerFile);}
    public String getRecordsPerFile(){return (String) get("recordsPerFile");}

    public void setExportFormat(String exportFormat){set("exportFormat", exportFormat);}
    public String getExportFormat(){return (String) get("exportFormat");}

    public void setStatus(String status){set("status", status);}
    public String getStatus(){return (String) get("status");}

    /*
    *Old Tasks
    */
    public List<OldTaskUI> getOldTasks(){return oldTaskUIList;}
    public List<ExternalServiceUI> getRestServiceUIList(){return restServiceUIs;}
    public void setOldTasks(List<OldTaskUI> oldTaskUIs){
        oldTaskUIList = oldTaskUIs;}

    /*
    *Running Tasks
    */

    public OldTaskUI getOldTask(String id)
    {
        for(OldTaskUI taskUI : oldTaskUIList)
        {
            if(taskUI.getId().equals(id))
                return taskUI;
        }
        return null;
    }

    /*
    *Scheduled Tasks
    */
    public List<ScheduledTaskUI> getScheduledTasks(){return scheduledTasksListUI;}
    public void setScheduledTasks(List<ScheduledTaskUI> taskUIList){
        scheduledTasksListUI = taskUIList;}

    public void removeScheduledTask(String id) {
        ScheduledTaskUI taskUIToRemove = null;

        for(ScheduledTaskUI taskUI : scheduledTasksListUI)
        {
            if(taskUI.getId().equals(id))
                taskUIToRemove = taskUI;
        }
        scheduledTasksListUI.remove(taskUIToRemove);
    }

    public String getMarcFormat() {
        return marcFormat;
    }

    public void setMarcFormat(String marcFormat) {
        this.marcFormat = marcFormat;
    }

    /*
    *DS Type Folder Attributes only
    */
    public void setRetrieveStartegy(String strategy){set("retrieveStrategy", strategy);}
    public String getRetrieveStartegy(){return (String) get("retrieveStrategy");}

    public List<String> getNamespaceList() {return namespaceList;}
    public void setNamespaceList(List<String> namespaces){namespaceList = namespaces;}

    public void setOaiSource(String src){set("oaiSource", src);}
    public String getOaiSource(){return (String) get("oaiSource");}

    public void setOaiSet(String set){set("oaiSet", set);}
    public String getOaiSet(){return (String) get("oaiSet");}

    public void setIdXPath(String path){set("idXPath", path);}
    public String getIdXPath(){return (String) get("idXPath");}

    public void setDirPath(String dirPath){set("dirPath", dirPath);}
    public String getDirPath(){return (String) get("dirPath");}

    public void setHttpURL(String httpURL){set("httpURL", httpURL);}
    public String getHttpURL(){return (String) get("httpURL");}

    public void setFileExtract(String fileExtract){set("fileExtract", fileExtract);}
    public String getFileExtract(){return (String) get("fileExtract");}

    public void setRecordRootName(String rootName){set("recordRootName", rootName);}
    public String getRecordRootName(){return (String) get("recordRootName");}

    public void setServer(String server){set("server", server);}
    public String getServer(){return (String) get("server");}

    public void setUser(String user){set("user", user);}
    public String getUser(){return (String) get("user");}

    public void setPassword(String password){set("password", password);}
    public String getPassword(){return (String) get("password");}

    public void setFolderPath(String folderPath){set("folderPath", folderPath);}
    public String getFolderPath(){return (String) get("folderPath");}

    /*
     *DS Type Z39 Attributes only
     */
    public void setZ39Address(String z39Address){set("z39Address", z39Address);}
    public String getZ39Address(){return (String) get("z39Address");}

    public void setZ39Port(String port){set("z39Port", port);}
    public String getZ39Port(){return (String) get("z39Port");}

    public void setZ39Database(String database){set("z39Database", database);}
    public String getZ39Database(){return (String) get("z39Database");}

    public void setZ39User(String user){set("z39User", user);}
    public String getZ39User(){return (String) get("z39User");}

    public void setZ39Password(String password){set("z39Password", password);}
    public String getZ39Password(){return (String) get("z39Password");}

    public void setZ39RecordSyntax(String recordSyntax){set("z39RecordSyntax", recordSyntax);}
    public String getZ39RecordSyntax(){return (String) get("z39RecordSyntax");}

    public void setZ39HarvestMethod(String harvestMethod){set("z39HarvestMethod", harvestMethod);}
    public String getZ39HarvestMethod(){return (String) get("z39HarvestMethod");}

    public void setZ39MaximumId(String maximumId){set("z39MaximumId", maximumId);}
    public String getZ39MaximumId(){return (String) get("z39MaximumId");}

    public void setZ39IdListFile(String idListFile){set("z39IdListFile", idListFile);}
    public String getZ39IdListFile(){return (String) get("z39IdListFile");}

    public void setZ39EarliestDate(Date date){set("z39EarliestDate", date);}
    public Date getZ39EarlistDate(){return (Date) get("z39EarliestDate");}

    public String toString() {
        return getName();
    }

    public boolean isStoreInYadda() {
        return storeInYadda;
    }

    public void setStoreInYadda(boolean storeInYadda) {
        this.storeInYadda = storeInYadda;
    }

    public boolean isUseLastUpdateDate() {
        return useLastUpdateDate;
    }

    public void setUseLastUpdateDate(boolean useLastUpdateDate) {
        this.useLastUpdateDate = useLastUpdateDate;
    }

    public boolean isSample() {
        return isSample;
    }

    public void setIsSample(boolean sample) {
        isSample = sample;
    }

    public String getDeletedRecords() {
        return deletedRecords;
    }

    public void setDeletedRecords(String deletedRecords) {
        this.deletedRecords = deletedRecords;
    }

    public String getYaddaCollection() {
        return yaddaCollection;
    }

    public void setYaddaCollection(String yaddaCollection) {
        this.yaddaCollection = yaddaCollection;
    }

    public List<DataSetTagUI> getTags() {
        if(tags == null)
            tags = new ArrayList<DataSetTagUI>();
        return tags;
    }

    public void setTags(List<DataSetTagUI> tags) {
        this.tags = tags;
    }
}
