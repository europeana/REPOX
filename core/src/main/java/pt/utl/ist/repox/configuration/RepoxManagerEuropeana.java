package pt.utl.ist.repox.configuration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import pt.utl.ist.repox.accessPoint.manager.AccessPointManagerFactory;
import pt.utl.ist.repox.accessPoint.manager.AccessPointsManager;
import pt.utl.ist.repox.dataProvider.dataSource.TagsManager;
import pt.utl.ist.repox.externalServices.ExternalRestServicesManager;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.statistics.StatisticsManager;
import pt.utl.ist.repox.task.TaskManager;
import pt.utl.ist.repox.util.FileUtilSecond;
import pt.utl.ist.rest.dataProvider.DataManagerEuropeana;
import pt.utl.ist.rest.statistics.StatisticsManagerEuropeana;
import pt.utl.ist.rest.util.EmailUtilEuropeana;
import pt.utl.ist.util.exceptions.task.IllegalFileFormatException;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 30-03-2011
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class RepoxManagerEuropeana implements RepoxManager {
    private static final Logger           log = Logger.getLogger(RepoxManagerEuropeana.class);
    private static String                 baseUrn;

    private RepoxConfigurationEuropeana   configuration;
    private AccessPointsManager           accessPointsManager;
    private DataManagerEuropeana          dataManager;
    private StatisticsManager             statisticsManager;
    private RecordCountManager            recordCountManager;
    private TaskManager                   taskManager;
    private MetadataTransformationManager metadataTransformationManager;
    private ExternalRestServicesManager   externalRestServicesManager;
    private MetadataSchemaManager         metadataSchemaManager;
    private TagsManager                   tagsManager;
    private Thread                        taskManagerThread;
    private EmailUtilEuropeana            emailClient;

    public RepoxConfigurationEuropeana getConfiguration() {
        return configuration;
    }

    public AccessPointsManager getAccessPointsManager() {
        return accessPointsManager;
    }

    public DataManagerEuropeana getDataManager() {
        return dataManager;
    }

    public RecordCountManager getRecordCountManager() {
        return recordCountManager;
    }

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public MetadataTransformationManager getMetadataTransformationManager() {
        return metadataTransformationManager;
    }

    public ExternalRestServicesManager getExternalRestServicesManager() {
        return externalRestServicesManager;
    }

    public MetadataSchemaManager getMetadataSchemaManager() {
        return metadataSchemaManager;
    }

    public TagsManager getTagsManager() {
        return tagsManager;
    }

    public Thread getTaskManagerThread() {
        return taskManagerThread;
    }

    public EmailUtilEuropeana getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(EmailUtilEuropeana emailClient) {
        this.emailClient = emailClient;
    }

    public RepoxManagerEuropeana(RepoxConfigurationEuropeana configuration, String dataProvidersFilename, String statisticsFilename, String recordCountsFilename, String schedulerFilename, String ongoingTasksFilename, String metadataTransformationsFilename, String oldTasksFileName,
                                 String externalServicesFilename, String metadataSchemasFilename, String tagsFilename) throws DocumentException, ParseException, SQLException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException {
        this.configuration = configuration;

        File countries = new File(configuration.getXmlConfigPath() + "/" + RepoxContextUtilDefault.COUNTRIES_FILENAME);
        if (!countries.exists()) {
            FileUtilSecond.createFile("/" + RepoxContextUtilDefault.COUNTRIES_FILENAME, countries);
        }

        File metadataTransformation = new File(configuration.getXmlConfigPath() + "/" + RepoxContextUtilEuropeana.METADATA_TRANSFORMATIONS_FILENAME);
        if (!countries.exists()) {
            FileUtilSecond.createFile("/" + RepoxContextUtilEuropeana.METADATA_TRANSFORMATIONS_FILENAME, metadataTransformation);
        }

        File statisticsFile = new File(configuration.getXmlConfigPath(), statisticsFilename);
        this.statisticsManager = new StatisticsManagerEuropeana(statisticsFile);

        File countsFile = new File(configuration.getXmlConfigPath(), recordCountsFilename);
        this.recordCountManager = new RecordCountManager(countsFile);

        File metadataTransformationsFile = new File(configuration.getXmlConfigPath(), metadataTransformationsFilename);
        File xsltDir = new File(configuration.getXmlConfigPath(), RepoxConfigurationEuropeana.METADATA_TRANSFORMATIONS_DIRNAME);
        this.metadataTransformationManager = new MetadataTransformationManager(metadataTransformationsFile, xsltDir);

        File externalServicesFile = new File(configuration.getXmlConfigPath(), externalServicesFilename);
        this.externalRestServicesManager = new ExternalRestServicesManager(externalServicesFile);

        File metadataSchemasFile = new File(configuration.getXmlConfigPath(), metadataSchemasFilename);
        this.metadataSchemaManager = new MetadataSchemaManager(metadataSchemasFile);

        File tagsFile = new File(configuration.getXmlConfigPath(), tagsFilename);
        this.tagsManager = new TagsManager(tagsFile);

        File dataProvidersFile = new File(configuration.getXmlConfigPath(), dataProvidersFilename);
        File repositoryPath = new File(configuration.getRepositoryPath());

        File oldTasksFile = new File(configuration.getXmlConfigPath(), oldTasksFileName);
        File defaultExportDir = new File(configuration.getExportDefaultFolder());
        this.dataManager = new DataManagerEuropeana(dataProvidersFile, this.metadataTransformationManager, this.metadataSchemaManager, repositoryPath, oldTasksFile, defaultExportDir, configuration);

        RepoxManagerEuropeana.baseUrn = configuration.getBaseUrn();

        this.accessPointsManager = AccessPointManagerFactory.getInstance(configuration);
        accessPointsManager.initialize(dataManager.loadDataSourceContainers());

        this.emailClient = new EmailUtilEuropeana();

        File scheduledTasksFile = new File(configuration.getXmlConfigPath(), schedulerFilename);
        File ongoingTasksFile = new File(configuration.getXmlConfigPath(), ongoingTasksFilename);
        taskManager = new TaskManager(scheduledTasksFile, ongoingTasksFile);
        taskManagerThread = new Thread(taskManager);
        taskManagerThread.start();
    }

    /**
     * Gets the base URN of this Repox instance. Ex: urn:bn:repox:
     * @return the base URN of this Repox instance. Ex: urn:bn:repox:
     */
    public String getBaseUrn() {
        return baseUrn;
    }
}
