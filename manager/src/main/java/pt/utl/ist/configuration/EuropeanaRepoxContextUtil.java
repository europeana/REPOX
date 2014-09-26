package pt.utl.ist.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import pt.utl.ist.util.PropertyUtil;
import pt.utl.ist.util.exceptions.task.IllegalFileFormatException;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 31-03-2011
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
public class EuropeanaRepoxContextUtil implements RepoxContextUtil {
    private static final Logger          log                               = Logger.getLogger(EuropeanaRepoxContextUtil.class);
    public static final String           COUNTRIES_FILENAME                = "countries.txt";
    public static final String           DATA_PROVIDERS_FILENAME           = "dataProviders.xml";
    public static final String           OLD_TASKS_FILENAME                = "oldTasks.xml";
    public static final String           STATISTICS_FILENAME               = "repoxStatistics.xml";
    public static final String           RECORD_COUNTS_FILENAME            = "recordCounts.xml";
    public static final String           DATA_SOURCES_STATE_FILENAME       = "dataSourcesStates.xml";
    private static final String          SCHEDULED_TASKS_FILENAME          = "scheduledTasks.xml";
    private static final String          RECOVERABLE_TASKS_FILENAME        = "recoverableTasks.xml";
    public static final String           METADATA_TRANSFORMATIONS_FILENAME = "metadataTransformations.xml";
    private static final String          EXTERNAL_SERVICES_FILENAME        = "externalServices.xml";
    private static final String          METADATA_SCHEMAS_FILENAME         = "metadataSchemas.xml";
    private static final String          TAGS_FILENAME                     = "dataSetTags.xml";

    private static EuropeanaRepoxManager repoxManager;

    public EuropeanaRepoxManager getRepoxManager() {
        try {
            if (repoxManager == null) {
                Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration(CONFIG_FILE);
                EuropeanaRepoxConfiguration configuration = new EuropeanaRepoxConfiguration(configurationProperties);

                String configurationPath = configuration.getXmlConfigPath();
                File countriesFile = new File(configurationPath + File.separator + "countries.txt");
                if (!countriesFile.exists()) {
                    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("countries.txt");
                    OutputStream os = new FileOutputStream(countriesFile);
                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    os.close();
                    inputStream.close();
                }

                log.warn("Using DEFAULT configuration properties file: " + CONFIG_FILE);
                repoxManager = new EuropeanaRepoxManager(configuration, DATA_PROVIDERS_FILENAME, STATISTICS_FILENAME, RECORD_COUNTS_FILENAME, SCHEDULED_TASKS_FILENAME, RECOVERABLE_TASKS_FILENAME, METADATA_TRANSFORMATIONS_FILENAME, OLD_TASKS_FILENAME, EXTERNAL_SERVICES_FILENAME, METADATA_SCHEMAS_FILENAME, TAGS_FILENAME);
            }

            return repoxManager;
        } catch (Exception e) {
            log.fatal("Unable to load RepoxManagerEuropeana", e);
            return null;
        }
    }

    public EuropeanaRepoxManager getRepoxManagerTest() {
        try {
            Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration(CONFIG_FILE);
            EuropeanaRepoxConfiguration configuration = new EuropeanaRepoxConfiguration(configurationProperties);
            log.warn("Using TEST configuration properties file: " + TEST_CONFIG_FILE);
            repoxManager = new EuropeanaRepoxManager(configuration, DATA_PROVIDERS_FILENAME, STATISTICS_FILENAME, RECORD_COUNTS_FILENAME, SCHEDULED_TASKS_FILENAME, RECOVERABLE_TASKS_FILENAME, METADATA_TRANSFORMATIONS_FILENAME, OLD_TASKS_FILENAME, EXTERNAL_SERVICES_FILENAME, METADATA_SCHEMAS_FILENAME, TAGS_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IllegalFileFormatException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return repoxManager;
    }

    public void reloadProperties() {
        try {
            Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration(CONFIG_FILE);
            EuropeanaRepoxConfiguration configuration = new EuropeanaRepoxConfiguration(configurationProperties);
            log.warn("Using DEFAULT configuration properties file: " + CONFIG_FILE);
            repoxManager = new EuropeanaRepoxManager(configuration, DATA_PROVIDERS_FILENAME, STATISTICS_FILENAME, RECORD_COUNTS_FILENAME, SCHEDULED_TASKS_FILENAME, RECOVERABLE_TASKS_FILENAME, METADATA_TRANSFORMATIONS_FILENAME, OLD_TASKS_FILENAME, EXTERNAL_SERVICES_FILENAME, METADATA_SCHEMAS_FILENAME, TAGS_FILENAME);
        } catch (Exception e) {
            log.fatal("Unable to load RepoxManager", e);
        }
    }
}
