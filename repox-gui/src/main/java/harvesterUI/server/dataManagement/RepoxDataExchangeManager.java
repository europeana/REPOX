package harvesterUI.server.dataManagement;

import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.dataSets.TagsStatisticsManager;
import harvesterUI.server.transformations.MdrStatisticsManager;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.externalServices.ExternalServiceUI;
import harvesterUI.shared.externalServices.ServiceParameterUI;
import harvesterUI.shared.mdr.*;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;
import pt.utl.ist.repox.dataProvider.Countries;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.dataSource.DataSourceTag;
import pt.utl.ist.repox.dataProvider.dataSource.IdExtracted;
import pt.utl.ist.repox.externalServices.ExternalRestService;
import pt.utl.ist.repox.externalServices.ServiceParameter;
import pt.utl.ist.repox.ftp.DataSourceFtp;
import pt.utl.ist.repox.http.DataSourceHttp;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.marc.DataSourceFolder;
import pt.utl.ist.repox.metadataSchemas.MetadataSchema;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.sru.DataSourceSruRecordUpdate;
import pt.utl.ist.repox.task.OldTask;
import pt.utl.ist.repox.task.ScheduledTask;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.z3950.DataSourceZ3950;
import pt.utl.ist.repox.z3950.IdListHarvester;
import pt.utl.ist.repox.z3950.IdSequenceHarvester;
import pt.utl.ist.repox.z3950.TimestampHarvester;

import java.util.*;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 02-05-2011
 * Time: 17:25
 */
public class RepoxDataExchangeManager {

    public static void parseDataSourceSubType(DataSourceUI dataSourceUI, DataSource dataSource) {
        if(dataSource instanceof DataSourceOai) {
            dataSourceUI.setIngest("OAI-PMH " + dataSourceUI.getSourceMDFormat());
            DataSourceOai dataSourceOai = (DataSourceOai) dataSource;
            dataSourceUI.setOaiSource(dataSourceOai.getOaiSourceURL());
            dataSourceUI.setOaiSet(dataSourceOai.getOaiSet());
        }
        else if(dataSource instanceof DataSourceSruRecordUpdate) {
            dataSourceUI.setIngest("SRU " + dataSourceUI.getSourceMDFormat());
        }else if(dataSource instanceof DataSourceDirectoryImporter) {
            dataSourceUI.setIngest("Folder " + dataSourceUI.getSourceMDFormat());
            DataSourceDirectoryImporter dataSourceDirectoryImporter = (DataSourceDirectoryImporter) dataSource;
            if(dataSourceDirectoryImporter.getRetrieveStrategy() instanceof DataSourceFolder) {
                loadIdExtractedInfo(dataSourceDirectoryImporter,dataSource,dataSourceUI);
                dataSourceUI.setRetrieveStartegy("pt.utl.ist.repox.marc.DataSourceFolder");
            }
            else if(dataSourceDirectoryImporter.getRetrieveStrategy() instanceof DataSourceFtp) {
                DataSourceFtp dataSourceFtp = (DataSourceFtp) dataSourceDirectoryImporter.getRetrieveStrategy();
                dataSourceUI.setServer(dataSourceFtp.getServer());
                dataSourceUI.setUser(dataSourceFtp.getUser());
                dataSourceUI.setPassword(dataSourceFtp.getPassword());
                dataSourceUI.setFolderPath(dataSourceFtp.getFtpPath());
                dataSourceUI.setRetrieveStartegy("pt.utl.ist.repox.ftp.DataSourceFtp");
                loadIdExtractedInfo(dataSourceDirectoryImporter,dataSource,dataSourceUI);
            }
            else if(dataSourceDirectoryImporter.getRetrieveStrategy() instanceof DataSourceHttp) {
                DataSourceHttp dataSourceHttp = (DataSourceHttp) dataSourceDirectoryImporter.getRetrieveStrategy();
                dataSourceUI.setHttpURL(dataSourceHttp.getUrl());
                dataSourceUI.setRetrieveStartegy("pt.utl.ist.repox.ftp.DataSourceHttp");
                loadIdExtractedInfo(dataSourceDirectoryImporter,dataSource,dataSourceUI);
            }

            // Common attributes for both retrieve strategies
            dataSourceUI.setDirPath(dataSourceDirectoryImporter.getSourcesDirPath());
            if(dataSourceDirectoryImporter.getCharacterEncoding() != null)
                dataSourceUI.setCharacterEncoding(dataSourceDirectoryImporter.getCharacterEncoding().toString());
            dataSourceUI.setRecordRootName(dataSourceDirectoryImporter.getRecordXPath());

        }
        else if(dataSource instanceof DataSourceZ3950) {
            dataSourceUI.setIngest("Z3950 " + dataSourceUI.getSourceMDFormat());
            DataSourceZ3950 dataSourceZ3950 = (DataSourceZ3950) dataSource;
            if(dataSourceZ3950.getRecordIdPolicy() instanceof IdExtracted) {
                IdExtracted idExtracted = (IdExtracted) dataSource.getRecordIdPolicy();
                dataSourceUI.setIdXPath(idExtracted.getIdentifierXpath());
                Map<String,String> namespaces = idExtracted.getNamespaces();
                Iterator iterator=namespaces.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry mapEntry=(Map.Entry)iterator.next();
                    dataSourceUI.getNamespaceList().add("" + mapEntry.getKey());
                    dataSourceUI.getNamespaceList().add("" +mapEntry.getValue());
                }
            }

            dataSourceUI.setZ39Address(dataSourceZ3950.getHarvestMethod().getTarget().getAddress());
            dataSourceUI.setZ39Port("" + dataSourceZ3950.getHarvestMethod().getTarget().getPort());
            dataSourceUI.setZ39Database(dataSourceZ3950.getHarvestMethod().getTarget().getDatabase());
            dataSourceUI.setZ39User(dataSourceZ3950.getHarvestMethod().getTarget().getUser());
            dataSourceUI.setZ39Password(dataSourceZ3950.getHarvestMethod().getTarget().getPassword());
            if(dataSourceZ3950.getHarvestMethod().getTarget().getCharacterEncoding() != null)
                dataSourceUI.setCharacterEncoding(dataSourceZ3950.getHarvestMethod().getTarget().getCharacterEncoding().toString());

            dataSourceUI.setZ39RecordSyntax(dataSourceZ3950.getHarvestMethod().getTarget().getRecordSyntax());

            // Harvest Method differences
            if(dataSourceZ3950.getHarvestMethod() instanceof IdSequenceHarvester) {
                IdSequenceHarvester idSequenceHarvester = (IdSequenceHarvester) dataSourceZ3950.getHarvestMethod();
                dataSourceUI.setZ39HarvestMethod("IdSequenceHarvester");
                dataSourceUI.setZ39MaximumId(""+idSequenceHarvester.getMaximumId());
            }
            else if(dataSourceZ3950.getHarvestMethod() instanceof IdListHarvester) {
                IdListHarvester idListHarvester = (IdListHarvester) dataSourceZ3950.getHarvestMethod();
                dataSourceUI.setZ39HarvestMethod("IdListHarvester");
                dataSourceUI.setZ39IdListFile(idListHarvester.getIdListFile().getPath());
            }
            else if(dataSourceZ3950.getHarvestMethod() instanceof TimestampHarvester) {
                TimestampHarvester timestampHarvester = (TimestampHarvester) dataSourceZ3950.getHarvestMethod();
                dataSourceUI.setZ39HarvestMethod("TimestampHarvester");
                dataSourceUI.setZ39EarliestDate(timestampHarvester.getEarliestTimestamp());
            }
        }
    }

    public static void getOldTasks(DataSource dataSource,DataSourceUI dataSourceUI) {
        for(OldTask oldTask: dataSource.getOldTasksList()) {
            OldTaskUI oldTaskUI = new OldTaskUI(dataSourceUI.getDataSourceSet(),oldTask.getId(),oldTask.getLogName(),
                    oldTask.getIngestType(),oldTask.getStatus(),oldTask.getRetries(),
                    oldTask.getRetryMax(),oldTask.getDelay(),oldTask.getDateString(),oldTask.getRecords());
            dataSourceUI.getOldTasks().add(oldTaskUI);
        }
        dataSourceUI.setLastIngest(dataSource.getLastUpdate());
//        if(dataSourceUI.getOldTasks().size() > 0) {
//            dataSourceUI.setLastIngest(dataSourceUI.getOldTasks().get(dataSourceUI.getOldTasks().size()-1).getDate());
////            dataSourceUI.setRecords(dataSourceUI.getOldTasks().get(dataSourceUI.getOldTasks().size()-1).getRecords());
//        }
    }

    public static void getScheduledTasks(DataSourceUI dataSourceUI) {
        for (ScheduledTask task : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getScheduledTasks()) {
            // Load only tasks after today that aren't frequency ONCE
            if(task.getFirstRun().getTime().after(Calendar.getInstance().getTime()) || !task.getFrequency().name().equals("ONCE")) {
                if(task.getParameters()[1].equals(dataSourceUI.getDataSourceSet())) {
                    if(task.getTaskClass().getSimpleName().equals("IngestDataSource")) {
                        String taskId = task.getId();
                        String firstRunStr = task.getFirstRunString();
                        String freq = task.getFrequency().name();
                        Integer xmonths = task.getXmonths();
                        String fullIngest = task.getParameters()[2];
                        ScheduledTaskUI scheduledTaskUI = new ScheduledTaskUI(dataSourceUI.getDataSourceSet(),taskId,firstRunStr,freq,xmonths,fullIngest);
                        scheduledTaskUI.setScheduleType(0);
                        dataSourceUI.getScheduledTasks().add(scheduledTaskUI);
                        dataSourceUI.setNextIngest(task.getNextIngestDate());
                    } else if(task.getTaskClass().getSimpleName().equals("ExportToFilesystem")) {
                        String taskId = task.getId();
                        String firstRunStr = task.getFirstRunString();
                        String freq = task.getFrequency().name();
                        Integer xmonths = task.getXmonths();
                        String recordsPerFile = task.getParameters()[3];
                        String exportDirectory = task.getParameters()[2];
                        ScheduledTaskUI scheduledTaskUI = new ScheduledTaskUI(dataSourceUI.getDataSourceSet(),taskId,firstRunStr,freq,xmonths,"");
                        scheduledTaskUI.setScheduleType(1);
                        scheduledTaskUI.createDateString(1);
                        scheduledTaskUI.setScheduleType("Data Set Export");
                        scheduledTaskUI.setParameters("Data Set: " + scheduledTaskUI.getDataSetId() + " -- Folder: " +
                                dataSourceUI.getExportDirectory());
                        scheduledTaskUI.setRecordsPerFile(recordsPerFile);
                        scheduledTaskUI.setExportDirectory(exportDirectory);
                        dataSourceUI.getScheduledTasks().add(scheduledTaskUI);
                        dataSourceUI.setNextIngest(task.getNextIngestDate());
                    }
                }
            }
        }
    }

    public static void getDataSetInfo(DataSource dataSource, DataSourceUI newDataSourceUI) throws ServerSideException{
        try{
//            RepoxServiceImpl.getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getId(), true);

            int maxSample = dataSource.getMaxRecord4Sample();
            if(maxSample == -1)
                newDataSourceUI.setStatus(dataSource.getStatusString());
            else
                newDataSourceUI.setStatus(dataSource.getStatusString()+"_SAMPLE");

            // Check if has a retrying the task
            if(Util.hasRunningTask(dataSource.getId()))
                newDataSourceUI.setHasRunningTask(true);
            else
                newDataSourceUI.setHasRunningTask(false);

            String[] counts = dataSource.getNumberRecords();
            newDataSourceUI.setRecords(counts[2]);
            newDataSourceUI.setDeletedRecords(counts[1]);
            if((dataSource instanceof DataSourceOai || dataSource instanceof DataSourceDirectoryImporter)
                    && dataSource.getStatusString().equals("RUNNING")) {
                try{
                    newDataSourceUI.setTotalRecords(dataSource.getTotalRecords2Harvest());
                    newDataSourceUI.setTotalRecordsStr(dataSource.getNumberOfRecords2HarvestStr());
                    newDataSourceUI.setIngestPercentage(dataSource.getPercentage());
                    newDataSourceUI.setIngestTimeLeft(dataSource.getTimeLeft());
                }catch (NullPointerException e){
                    newDataSourceUI.setTotalRecords(-1);
                    newDataSourceUI.setTotalRecordsStr(null);
                    newDataSourceUI.setIngestPercentage(-1);
                    newDataSourceUI.setIngestTimeLeft(-1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    // Transformations for every data source type
    public static void getMetadataTransformations(DataSource dataSource, DataSourceUI newDataSourceUI) {
        Map<String,MetadataTransformation> transformations = dataSource.getMetadataTransformations();
        for(MetadataTransformation metadataTransformation:transformations.values()){
            newDataSourceUI.getMetadataTransformations().add(new TransformationUI(metadataTransformation.getId(),
                    metadataTransformation.getDescription(),metadataTransformation.getSourceFormat(),
                    metadataTransformation.getDestinationFormat(),metadataTransformation.getDestSchema(),
                    metadataTransformation.getDestNamespace(),metadataTransformation.getStylesheet(),
                    metadataTransformation.isVersionTwo()));
        }
    }

    // External Services for every data source type
    public static void getExternalServices(DataSource dataSource, DataSourceUI newDataSourceUI) {
        List<ExternalRestService> externalRestServices = dataSource.getExternalRestServices();
        for(ExternalRestService externalRestService : externalRestServices){
            List<ServiceParameterUI> serviceParameters = new ArrayList<ServiceParameterUI>();
            for(ServiceParameter serviceParameter : externalRestService.getServiceParameters()){
                ServiceParameterUI serviceParameterUI = new ServiceParameterUI(serviceParameter.getName(),
                        serviceParameter.getType(),serviceParameter.getRequired(),serviceParameter.getExample(),
                        serviceParameter.getSemantics());
                if(serviceParameter.getType().equals("COMBO_FIELD"))
                    serviceParameterUI.setComboValues(serviceParameter.getComboValues());

                serviceParameterUI.setValue(serviceParameter.getValue());
                serviceParameters.add(serviceParameterUI);
            }

            ExternalServiceUI externalServiceUI = new ExternalServiceUI(externalRestService.getId(),
                    externalRestService.getName(),externalRestService.getUri(),
                    externalRestService.getStatusUri(),
                    externalRestService.getType(),serviceParameters,externalRestService.getExternalServiceType().name());
            externalServiceUI.setEnabled(externalRestService.isEnabled());
            externalServiceUI.setExternalResultUI(externalRestService.getExternalResultsUri());
            newDataSourceUI.getRestServiceUIList().add(externalServiceUI);
        }
    }

    public static void getTags(DataSource dataSource, DataSourceUI newDataSourceUI) {
        List<DataSourceTag> tags = dataSource.getTags();
        for(DataSourceTag tag : tags){
            String name = tag.getName();
            newDataSourceUI.getTags().add(new DataSetTagUI(name));
        }
    }

    /**
     *
     * Data Lists
     *
     */

    public List<String> getFullCharacterEncodingList() {
        List<String> results = new ArrayList<String>();
        for(int i=0; i<CharacterEncoding.getValues().length; i++) {
            results.add(CharacterEncoding.getValues()[i].toString());
        }
        return results;
    }

    private static void loadIdExtractedInfo(DataSourceDirectoryImporter dataSourceDirectoryImporter, DataSource dataSource, DataSourceUI dataSourceUI){
        if(dataSourceDirectoryImporter.getRecordIdPolicy() instanceof IdExtracted) {
            IdExtracted idExtracted = (IdExtracted) dataSource.getRecordIdPolicy();
            dataSourceUI.setIdXPath(idExtracted.getIdentifierXpath());
            dataSourceUI.setRecordIdPolicy("IdExtracted");
            Map<String,String> namespaces = idExtracted.getNamespaces();
            Iterator iterator=namespaces.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry mapEntry=(Map.Entry)iterator.next();
                dataSourceUI.getNamespaceList().add("" + mapEntry.getKey());
                dataSourceUI.getNamespaceList().add("" +mapEntry.getValue());
            }
        } else
            dataSourceUI.setRecordIdPolicy("IdGenerated");
    }

    public static DataProviderUI parseDataProvider(DataProvider dataProvider)  throws ServerSideException{
        String country;
        if(dataProvider.getCountry() != null)
            country = dataProvider.getCountry();
        else
            country = "none";

        DataProviderUI newDataProviderUI = new DataProviderUI(dataProvider.getId(),dataProvider.getName(),
                country, (country != null && !country.equals("")) ? Countries.getCountries().get(country) : "");
        newDataProviderUI.setDescription(dataProvider.getDescription());
        newDataProviderUI.setEmail(dataProvider.getEmail());

        // Eudml
//        if(dataProvider instanceof DataProviderEuDML){
//            newDataProviderUI.setIpAddress(((DataProviderEuDML) dataProvider).getIpAddress());
//        }

        return newDataProviderUI;
    }

    public static List<SchemaUI> convertRepoxSchemas(MdrStatisticsManager mdrStatisticsManager) throws ServerSideException{
        List<SchemaUI> schemaUIs = new ArrayList<SchemaUI>();
        List<MetadataSchema> schemaList = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().getMetadataSchemas();
        mdrStatisticsManager.processSchemaStatistics();

        for(MetadataSchema metadataSchema : schemaList){

            SchemaUI schemaUI = new SchemaUI(metadataSchema.getDesignation(),metadataSchema.getShortDesignation(),
                    metadataSchema.getDescription(),metadataSchema.getNamespace(),
                    metadataSchema.getNotes());
            schemaUI.setOAIAvailable(metadataSchema.isOAIAvailable());

            for(MetadataSchemaVersion metadataSchemaVersion : metadataSchema.getMetadataSchemaVersions()){
                SchemaMdrDataStatistics mdrDataStatistics = mdrStatisticsManager.getSchemaVersionsStatisticsMap().get(schemaUI.getShortDesignation() + metadataSchemaVersion.getVersion());
                SchemaVersionUI schemaVersionUI;
                if(mdrDataStatistics != null)
                    schemaVersionUI = new SchemaVersionUI(metadataSchemaVersion.getVersion(),metadataSchemaVersion.getXsdLink(),mdrDataStatistics);
                else
                    schemaVersionUI = new SchemaVersionUI(metadataSchemaVersion.getVersion(),metadataSchemaVersion.getXsdLink(),new SchemaMdrDataStatistics(0,null, 0, null));

                schemaUI.getSchemaVersions().add(schemaVersionUI);
            }

            schemaUIs.add(schemaUI);
        }

        Collections.sort(schemaUIs, new Comparator<SchemaUI>() {
            public int compare(SchemaUI p1, SchemaUI p2) {
                return p1.getShortDesignation().compareTo(p2.getShortDesignation());
            }
        });

        return schemaUIs;
    }

    public static List<TransformationUI> getFullTransformationsList(MdrStatisticsManager mdrStatisticsManager) throws ServerSideException{
        List<TransformationUI> results = new ArrayList<TransformationUI>();
        mdrStatisticsManager.processMatches();

        try {
            Map<String,List<MetadataTransformation>> transformations =
                    RepoxServiceImpl.getRepoxManager().getMetadataTransformationManager().getMetadataTransformations();
            Iterator iterator=transformations.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry mapEntry=(Map.Entry)iterator.next();
                for (MetadataTransformation metadataTransformation : (List<MetadataTransformation>)mapEntry.getValue()) {
                    MdrDataStatistics mdrDataStatistics = mdrStatisticsManager.getTransformationStatisticsMap().get(metadataTransformation.getId());
                    TransformationUI transformationUI;
                    if(mdrDataStatistics != null){
                        transformationUI = new TransformationUI(metadataTransformation.getId(),
                                metadataTransformation.getDescription(),metadataTransformation.getSourceFormat(),
                                metadataTransformation.getDestinationFormat(),metadataTransformation.getDestSchema(),
                                metadataTransformation.getDestNamespace(),metadataTransformation.getStylesheet(),
                                metadataTransformation.isVersionTwo(), mdrDataStatistics);
                    }else{
                        transformationUI = new TransformationUI(metadataTransformation.getId(),
                                metadataTransformation.getDescription(),metadataTransformation.getSourceFormat(),
                                metadataTransformation.getDestinationFormat(),metadataTransformation.getDestSchema(),
                                metadataTransformation.getDestNamespace(),metadataTransformation.getStylesheet(),
                                metadataTransformation.isVersionTwo());
                    }
                    transformationUI.setSourceSchema(metadataTransformation.getSourceSchema());
                    transformationUI.setMDRCompliant(metadataTransformation.isMDRCompliant());
                    transformationUI.setEditable(metadataTransformation.isEditable());
                    results.add(transformationUI);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return results;
    }

    public static List<DataSetTagUI> getAllParsedTags(TagsStatisticsManager tagsStatisticsManager) throws ServerSideException{
        List<DataSetTagUI> results = new ArrayList<DataSetTagUI>();
        tagsStatisticsManager.processMatches();

        try {
            List<DataSourceTag> repoxTags = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTagsManager().getTags();
            for(DataSourceTag dataSourceTag : repoxTags){
                MdrDataStatistics mdrDataStatistics = tagsStatisticsManager.getTagsStatisticsMap().get(dataSourceTag.getName());
                String name = dataSourceTag.getName();
                DataSetTagUI dataSetTagUI;
                if(mdrDataStatistics != null){
                    dataSetTagUI = new DataSetTagUI(name,mdrDataStatistics);
                }else
                    dataSetTagUI = new DataSetTagUI(name);
                results.add(dataSetTagUI);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return results;
    }
}
