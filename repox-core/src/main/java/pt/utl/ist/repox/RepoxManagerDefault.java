package pt.utl.ist.repox;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import pt.utl.ist.repox.accessPoint.AccessPointsManagerDefault;
import pt.utl.ist.repox.accessPoint.database.AccessPointManagerFactory;
import pt.utl.ist.repox.dataProvider.DataManagerDefault;
import pt.utl.ist.repox.dataProvider.dataSource.TagsManager;
import pt.utl.ist.repox.externalServices.ExternalRestServicesManager;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.statistics.RecordCountManager;
import pt.utl.ist.repox.statistics.StatisticsManager;
import pt.utl.ist.repox.statistics.StatisticsManagerDefault;
import pt.utl.ist.repox.task.TaskManager;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.EmailUtil;
import pt.utl.ist.repox.util.EmailUtilDefault;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * The main class of REPOX. It manages all other components.
 * 
 * @author Nuno Freire
 */
public class RepoxManagerDefault implements RepoxManager {
    private static final Logger           log = Logger.getLogger(RepoxManagerDefault.class);
    private static String                 baseUrn;

    private RepoxConfiguration            configuration;
    private AccessPointsManagerDefault    accessPointsManager;
    private DataManagerDefault            dataManager;
    private StatisticsManager             statisticsManager;
    private RecordCountManager            recordCountManager;
    private TaskManager                   taskManager;
    private MetadataTransformationManager metadataTransformationManager;
    private ExternalRestServicesManager   externalRestServicesManager;
    private MetadataSchemaManager         metadataSchemaManager;
    private TagsManager                   tagsManager;
    private Thread                        taskManagerThread;
    private EmailUtil                     emailClient;

    @Override
    public RepoxConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public AccessPointsManagerDefault getAccessPointsManager() {
        return accessPointsManager;
    }

    @Override
    public DataManagerDefault getDataManager() {
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
    public EmailUtil getEmailClient() {
        return emailClient;
    }

    /**
     * @param emailClient
     */
    public void setEmailClient(EmailUtil emailClient) {
        this.emailClient = emailClient;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param configuration
     * @param dataProvidersFilename
     * @param statisticsFilename
     * @param recordCountsFilename
     * @param schedulerFilename
     * @param ongoingTasksFilename
     * @param metadataTransformationsFilename
     * @param oldTasksFileName
     * @param externalServicesFilename
     * @param metadataSchemasFilename
     * @param tagsFilename
     * @throws DocumentException
     * @throws ParseException
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalFileFormatException
     */
    public RepoxManagerDefault(RepoxConfigurationDefault configuration, String dataProvidersFilename, String statisticsFilename, String recordCountsFilename, String schedulerFilename, String ongoingTasksFilename, String metadataTransformationsFilename, String oldTasksFileName,
                               String externalServicesFilename, String metadataSchemasFilename, String tagsFilename) throws DocumentException, ParseException, SQLException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException {
        this.configuration = configuration;

        File statisticsFile = new File(configuration.getXmlConfigPath(), statisticsFilename);
        this.statisticsManager = new StatisticsManagerDefault(statisticsFile);

        File countsFile = new File(configuration.getXmlConfigPath(), recordCountsFilename);
        this.recordCountManager = new RecordCountManager(countsFile);

        File metadataTransformationsFile = new File(configuration.getXmlConfigPath(), metadataTransformationsFilename);
        File xsltDir = new File(configuration.getXmlConfigPath(), RepoxConfigurationDefault.METADATA_TRANSFORMATIONS_DIRNAME);
        this.metadataTransformationManager = new MetadataTransformationManager(metadataTransformationsFile, xsltDir);

        File externalServicesFile = new File(configuration.getXmlConfigPath(), externalServicesFilename);
        this.externalRestServicesManager = new ExternalRestServicesManager(externalServicesFile);

        File metadataSchemasFile = new File(configuration.getXmlConfigPath(), metadataSchemasFilename);
        this.metadataSchemaManager = new MetadataSchemaManager(metadataSchemasFile);

        File tagsFile = new File(configuration.getXmlConfigPath(), tagsFilename);
        this.tagsManager = new TagsManager(tagsFile);

        File dataProvidersFile = new File(configuration.getXmlConfigPath(), dataProvidersFilename);
        File oldTasksFile = new File(configuration.getXmlConfigPath(), oldTasksFileName);
        File repositoryPath = new File(configuration.getRepositoryPath());
        this.dataManager = new DataManagerDefault(dataProvidersFile, this.metadataTransformationManager, this.metadataSchemaManager, repositoryPath, oldTasksFile, configuration);

        RepoxManagerDefault.baseUrn = configuration.getBaseUrn();

        this.accessPointsManager = (AccessPointsManagerDefault)AccessPointManagerFactory.getInstance(configuration);
        accessPointsManager.initialize(dataManager.loadDataSourceContainers());

        this.emailClient = new EmailUtilDefault();

        File scheduledTasksFile = new File(configuration.getXmlConfigPath(), schedulerFilename);
        File ongoingTasksFile = new File(configuration.getXmlConfigPath(), ongoingTasksFilename);
        taskManager = new TaskManager(scheduledTasksFile, ongoingTasksFile);
        taskManagerThread = new Thread(taskManager);
        taskManagerThread.start();
    }

    /**
     * Gets the base URN of this Repox instance. Ex: urn:bn:repox:
     * 
     * @return the base URN of this Repox instance. Ex: urn:bn:repox:
     */
    public String getBaseUrn() {
        return baseUrn;
    }
}
