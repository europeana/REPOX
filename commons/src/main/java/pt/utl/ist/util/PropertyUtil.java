package pt.utl.ist.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;

/**
 */
public class PropertyUtil {
    private static String configsDirVar = "repox.conf.dir";
    /**
     * @param configurationFilename
     * @return Properties
     */
    public static PropertiesConfigurationLayout loadCorrectedConfiguration(String configurationFilename) {
        try {
            // Use -Drepox.conf.dir=/path/to/configuration/dir in the VM system variables in order to pass an already existing configuration.properties and gui.properties
            String configsDir = System.getProperty(configsDirVar);
            String configurationFile;
            if (configDataDirAlreadyExists(configsDir, configurationFilename))
                configurationFile = URLDecoder.decode(configsDir + File.separator + configurationFilename, "ISO-8859-1");
            else {
                URL configurationURL = Thread.currentThread().getContextClassLoader().getResource(configurationFilename);
                configurationFile = URLDecoder.decode(configurationURL.getFile(), "ISO-8859-1");
            }

            PropertiesConfiguration propertiesConfigration = new PropertiesConfiguration();
            PropertiesConfigurationLayout propertiesConfigrationLayout = new PropertiesConfigurationLayout(propertiesConfigration);
            propertiesConfigrationLayout.load(new FileReader(configurationFile));

            //            Properties properties = new Properties();
            //            properties.load(new FileInputStream(configurationFile));

            return propertiesConfigrationLayout;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load [" + configurationFilename + "]");
        }
    }

    /**
     * @param configsDataDir
     * @param configurationFilename
     * @return boolean indicating if the configuration Directory already exists
     */
    public static boolean configDataDirAlreadyExists(String configsDataDir, String configurationFilename) {
        if (configsDataDir == null || configsDataDir.isEmpty())
            return false;

        File configFile = new File(configsDataDir + File.separator + configurationFilename);
        if (!configFile.exists()) {
            System.out.println("Load Config files from Java Variables -- " + configurationFilename + " file doesn't exist on the specified directory");
            return false;
        }

        return true;
    }

    /**
     * Have a separate configuration load for gui, cause we don't want it be accessible in another location than the classpath.
     * 
     * @param configurationFilename
     * @return Properties
     */
    public static PropertiesConfigurationLayout loadGuiConfiguration(String configurationFilename) {
        try {
            URL configurationURL = Thread.currentThread().getContextClassLoader().getResource(configurationFilename);
            String configurationFile = URLDecoder.decode(configurationURL.getFile(), "ISO-8859-1");

            PropertiesConfiguration propertiesConfigration = new PropertiesConfiguration();
            PropertiesConfigurationLayout propertiesConfigrationLayout = new PropertiesConfigurationLayout(propertiesConfigration);
            propertiesConfigrationLayout.load(new FileReader(configurationFile));

            //            Properties properties = new Properties();
            //            properties.load(new FileInputStream(configurationFile));
            return propertiesConfigrationLayout;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not load [" + configurationFilename + "]");
        }
    }

    /**
     * @param propertiesConfigrationLayout 
     * @param name
     */
    public static void saveProperties(PropertiesConfigurationLayout propertiesConfigrationLayout, String name) {
        try {
            //Use -Drepox.conf.dir=/path/to/configuration/dir in the VM system variables in order to pass an already existing configuration.properties and gui.properties
            String configsDir = System.getProperty(configsDirVar);
            String configurationFile;
            if (configDataDirAlreadyExists(configsDir, name))
                configurationFile = URLDecoder.decode(configsDir + File.separator + name, "ISO-8859-1");
            else {
                URL configurationURL = Thread.currentThread().getContextClassLoader().getResource(name);
                configurationFile = URLDecoder.decode(configurationURL.getFile(), "ISO-8859-1");
            }

            propertiesConfigrationLayout.save(new FileWriter(configurationFile));
            //            properties.store(new FileOutputStream(configurationFile), null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            throw new RuntimeException("Caused by ConfigurationException", e);
        }
    }
}
