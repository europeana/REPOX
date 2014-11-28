package pt.utl.ist.configuration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import pt.utl.ist.accessPoint.manager.AccessPointManagerFactory;
import pt.utl.ist.accessPoint.manager.AccessPointsManager;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.dataSource.TagsManager;
import pt.utl.ist.externalServices.ExternalRestServicesManager;
import pt.utl.ist.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.rest.statistics.DefaultStatisticsManager;
import pt.utl.ist.rest.util.DefaultEmailUtil;
import pt.utl.ist.statistics.RecordCountManager;
import pt.utl.ist.statistics.StatisticsManager;
import pt.utl.ist.task.TaskManager;
import pt.utl.ist.util.FileUtilSecond;
import pt.utl.ist.util.exceptions.task.IllegalFileFormatException;

/**
 * Default implementation of the RepoxManager.
 * 
 * @author GPedrosa
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 24, 2014
 */
public class DefaultRepoxManager implements RepoxManager {
    private static final Logger           log = Logger.getLogger(DefaultRepoxManager.class);
    private static String                 baseUrn;

    private DefaultRepoxConfiguration   configuration;
    private AccessPointsManager           accessPointsManager;
    private DefaultDataManager          dataManager;
    private StatisticsManager             statisticsManager;
    private RecordCountManager            recordCountManager;
    private TaskManager                   taskManager;
    private MetadataTransformationManager metadataTransformationManager;
    private ExternalRestServicesManager   externalRestServicesManager;
    private MetadataSchemaManager         metadataSchemaManager;
    private TagsManager                   tagsManager;
    private Thread                        taskManagerThread;
    private DefaultEmailUtil            emailClient;

    @Override
    public DefaultRepoxConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public AccessPointsManager getAccessPointsManager() {
        return accessPointsManager;
    }

    @Override
    public DefaultDataManager getDataManager() {
        return dataManager;
    }

    @Override
    public RecordCountManager getRecordCountManager() {
        return recordCountManager;
    }

    @Override
    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    @Override
    public TaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public MetadataTransformationManager getMetadataTransformationManager() {
        return metadataTransformationManager;
    }

    @Override
    public ExternalRestServicesManager getExternalRestServicesManager() {
        return externalRestServicesManager;
    }

    @Override
    public MetadataSchemaManager getMetadataSchemaManager() {
        return metadataSchemaManager;
    }

    @Override
    public TagsManager getTagsManager() {
        return tagsManager;
    }

    @Override
    public Thread getTaskManagerThread() {
        return taskManagerThread;
    }

    @Override
    public DefaultEmailUtil getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(DefaultEmailUtil emailClient) {
        this.emailClient = emailClient;
    }

    public DefaultRepoxManager(DefaultRepoxConfiguration configuration, String dataProvidersFilename, String statisticsFilename, String recordCountsFilename, String schedulerFilename, String ongoingTasksFilename, String metadataTransformationsFilename, String oldTasksFileName,
                                 String externalServicesFilename, String metadataSchemasFilename, String tagsFilename) throws DocumentException, ParseException, SQLException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException {
        this.configuration = configuration;

        File countries = new File(configuration.getXmlConfigPath() + "/" + DefaultRepoxContextUtil.COUNTRIES_FILENAME);
        if (!countries.exists()) {
            FileUtilSecond.createFile("/" + DefaultRepoxContextUtil.COUNTRIES_FILENAME, countries);
        }

        File metadataTransformation = new File(configuration.getXmlConfigPath() + "/" + DefaultRepoxContextUtil.METADATA_TRANSFORMATIONS_FILENAME);
        if (!countries.exists()) {
            FileUtilSecond.createFile("/" + DefaultRepoxContextUtil.METADATA_TRANSFORMATIONS_FILENAME, metadataTransformation);
        }

        File statisticsFile = new File(configuration.getXmlConfigPath(), statisticsFilename);
        this.statisticsManager = new DefaultStatisticsManager(statisticsFile);

        File countsFile = new File(configuration.getXmlConfigPath(), recordCountsFilename);
        this.recordCountManager = new RecordCountManager(countsFile);

        File metadataTransformationsFile = new File(configuration.getXmlConfigPath(), metadataTransformationsFilename);
        File xsltDir = new File(configuration.getXmlConfigPath(), DefaultRepoxConfiguration.METADATA_TRANSFORMATIONS_DIRNAME);
        this.metadataTransformationManager = new MetadataTransformationManager(metadataTransformationsFile, xsltDir);

        File externalServicesFile = new File(configuration.getXmlConfigPath(), externalServicesFilename);
        this.externalRestServicesManager = new ExternalRestServicesManager(externalServicesFile);

        File metadataSchemasFile = new File(configuration.getXmlConfigPath(), metadataSchemasFilename);
        this.metadataSchemaManager = new MetadataSchemaManager(metadataSchemasFile);
        this.metadataTransformationManager.updateSchemaVersions(metadataSchemaManager);

        File tagsFile = new File(configuration.getXmlConfigPath(), tagsFilename);
        this.tagsManager = new TagsManager(tagsFile);

        File dataProvidersFile = new File(configuration.getXmlConfigPath(), dataProvidersFilename);
        File repositoryPath = new File(configuration.getRepositoryPath());

        File oldTasksFile = new File(configuration.getXmlConfigPath(), oldTasksFileName);
        File defaultExportDir = new File(configuration.getExportDefaultFolder());
        this.dataManager = new DefaultDataManager(dataProvidersFile, this.metadataTransformationManager, this.metadataSchemaManager, repositoryPath, oldTasksFile, defaultExportDir, configuration);

        DefaultRepoxManager.baseUrn = configuration.getBaseUrn();

        this.accessPointsManager = AccessPointManagerFactory.getInstance(configuration);
        accessPointsManager.initialize(dataManager.loadDataSourceContainers());

        this.emailClient = new DefaultEmailUtil();

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
