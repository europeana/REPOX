package pt.utl.ist.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import pt.utl.ist.statistics.RecordCountManager;
import pt.utl.ist.statistics.StatisticsManager;
import pt.utl.ist.task.TaskManager;
import pt.utl.ist.util.DefaultEmailUtil;
import pt.utl.ist.util.FileUtil;
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

    private DefaultRepoxConfiguration     configuration;
    private AccessPointsManager           accessPointsManager;
    private DefaultDataManager            dataManager;
    private StatisticsManager             statisticsManager;
    private RecordCountManager            recordCountManager;
    private TaskManager                   taskManager;
    private MetadataTransformationManager metadataTransformationManager;
    private ExternalRestServicesManager   externalRestServicesManager;
    private MetadataSchemaManager         metadataSchemaManager;
    private TagsManager                   tagsManager;
    private Thread                        taskManagerThread;
    private DefaultEmailUtil              emailClient;

    @Override
    public DefaultRepoxConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DefaultRepoxConfiguration configuration) {
      this.configuration = configuration;
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

    public DefaultRepoxManager(DefaultRepoxConfiguration configuration, String dataProvidersFilename, String statisticsFilename, String recordCountsFilename, String schedulerFilename,
                               String ongoingTasksFilename, String metadataTransformationsFilename, String oldTasksFileName,
                               String externalServicesFilename, String metadataSchemasFilename, String tagsFilename) throws DocumentException, ParseException, SQLException, IOException,
                                                                                                                    ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException {
        log.info("DefaultRepoxManager creating.");
        long startInitializing = System.currentTimeMillis();
        this.configuration = configuration;

        long start = System.currentTimeMillis();
        File countries = new File(configuration.getXmlConfigPath() + File.separator + DefaultRepoxContextUtil.COUNTRIES_FILENAME);
        if (!countries.exists()) {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DefaultRepoxContextUtil.COUNTRIES_FILENAME);
            OutputStream os = new FileOutputStream(countries);
            FileUtil.transferData(inputStream, os);
            os.close();
            inputStream.close();
        }
        long end = System.currentTimeMillis();

        start = System.currentTimeMillis();
        File metadataTransformation = new File(configuration.getXmlConfigPath() + File.separator + DefaultRepoxContextUtil.METADATA_TRANSFORMATIONS_FILENAME);
        if (!metadataTransformation.exists()) {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DefaultRepoxContextUtil.METADATA_TRANSFORMATIONS_FILENAME);
            OutputStream os = new FileOutputStream(metadataTransformation);
            FileUtil.transferData(inputStream, os);
            os.close();
            inputStream.close();
        }
        end = System.currentTimeMillis();

        start = System.currentTimeMillis();
        log.info("DefaultStatisticsManager creating.");
        //This file is never created, everything is created on the fly
        File statisticsFile = new File(configuration.getXmlConfigPath(), statisticsFilename);
        this.statisticsManager = new DefaultStatisticsManager(statisticsFile);
        end = System.currentTimeMillis();
        log.info("DefaultStatisticsManager created in : " + (end - start)/(60*1000F) + " mins");

        start = System.currentTimeMillis();
        log.info("RecordCounntManager creating.");
        File countsFile = new File(configuration.getXmlConfigPath(), recordCountsFilename);
        this.recordCountManager = new RecordCountManager(countsFile);
        end = System.currentTimeMillis();
        log.info("RecordCounntManager created in : " + (end - start)/(60*1000F) + " mins");

        start = System.currentTimeMillis();
        log.info("MetadataTransformationManager creating.");
        File metadataTransformationsFile = new File(configuration.getXmlConfigPath(), metadataTransformationsFilename);
        File xsltDir = new File(configuration.getXmlConfigPath(), DefaultRepoxConfiguration.METADATA_TRANSFORMATIONS_DIRNAME);
        this.metadataTransformationManager = new MetadataTransformationManager(metadataTransformationsFile, xsltDir);
        end = System.currentTimeMillis();
        log.info("MetadataTransformationManager created in : " + (end - start)/(60*1000F) + " mins");

        start = System.currentTimeMillis();
        log.info("ExternalRestServicesManager creating.");
        File externalServicesFile = new File(configuration.getXmlConfigPath(), externalServicesFilename);
        this.externalRestServicesManager = new ExternalRestServicesManager(externalServicesFile);
        end = System.currentTimeMillis();
        log.info("ExternalRestServicesManager created in : " + (end - start)/(60*1000F) + " mins");

        start = System.currentTimeMillis();
        log.info("MetadataSchemaManager creating.");
        File metadataSchemasFile = new File(configuration.getXmlConfigPath(), metadataSchemasFilename);
        this.metadataSchemaManager = new MetadataSchemaManager(metadataSchemasFile);
        this.metadataTransformationManager.updateSchemaVersions(metadataSchemaManager);
        end = System.currentTimeMillis();
        log.info("MetadataSchemaManager created in : " + (end - start)/(60*1000F) + " mins");

        start = System.currentTimeMillis();
        log.info("TagsManager creating.");
        File tagsFile = new File(configuration.getXmlConfigPath(), tagsFilename);
        this.tagsManager = new TagsManager(tagsFile);
        end = System.currentTimeMillis();
        log.info("TagsManager created in : " + (end - start)/(60*1000F) + " mins");

        File dataProvidersFile = new File(configuration.getXmlConfigPath(), dataProvidersFilename);
        File repositoryPath = new File(configuration.getRepositoryPath());

        File oldTasksFile = new File(configuration.getXmlConfigPath(), oldTasksFileName);
        File defaultExportDir = new File(configuration.getExportDefaultFolder());
        
        start = System.currentTimeMillis();
        log.info("DefaultDataManager creating.");
        this.dataManager = new DefaultDataManager(dataProvidersFile, this.metadataTransformationManager, this.metadataSchemaManager, repositoryPath, oldTasksFile, defaultExportDir, configuration);
        end = System.currentTimeMillis();
        log.info("DefaultDataManager created in : " + (end - start)/(60*1000F) + " mins");

//        DefaultRepoxManager.baseUrn = configuration.getBaseUrn();

        start = System.currentTimeMillis();
        log.info("AccessPointManager initializing.");
        this.accessPointsManager = AccessPointManagerFactory.getInstance(configuration);
        accessPointsManager.initialize(dataManager.loadDataSourceContainers());
        end = System.currentTimeMillis();
        log.info("AccessPointManager initialized in : " + (end - start)/(60*1000F) + " mins");

        this.emailClient = new DefaultEmailUtil();

        start = System.currentTimeMillis();
        log.info("TaskManager creating.");
        File scheduledTasksFile = new File(configuration.getXmlConfigPath(), schedulerFilename);
        File ongoingTasksFile = new File(configuration.getXmlConfigPath(), ongoingTasksFilename);
        taskManager = new TaskManager(scheduledTasksFile, ongoingTasksFile);
        taskManagerThread = new Thread(taskManager);
        taskManagerThread.start();
        end = System.currentTimeMillis();
        log.info("TaskManager created in : " + (end - start)/(60*1000F) + " mins");

        long endInitializing = System.nanoTime();
        log.info("DefaultRepoxManager created in : " + (endInitializing - startInitializing)/(60*1000F) + " mins");
    }
}
