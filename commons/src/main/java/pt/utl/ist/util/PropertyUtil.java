package pt.utl.ist.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 */
public class PropertyUtil {
    /**
     * @param configurationFilename
     * @return Properties
     */
    public static Properties loadCorrectedConfiguration(String configurationFilename) {
        try {
            // Use -Drepox.data.dir=D:\Projectos in the VM parameters in order to pass an already existing configuration.properties and gui.properties
            String configsDataDir = System.getProperty("repox.data.dir");
            String configurationFile;
            if (configDataDirAlreadyExists(configsDataDir, configurationFilename))
                configurationFile = URLDecoder.decode(configsDataDir + File.separator + configurationFilename, "ISO-8859-1");
            else {
                URL configurationURL = Thread.currentThread().getContextClassLoader().getResource(configurationFilename);
                configurationFile = URLDecoder.decode(configurationURL.getFile(), "ISO-8859-1");
            }

            Properties properties = new Properties();
            properties.load(new FileInputStream(configurationFile));

            return properties;
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
        if (configsDataDir == null || configsDataDir.isEmpty()) return false;

        File configFile = new File(configsDataDir + File.separator + configurationFilename);
        if (!configFile.exists()) {
            System.out.println("Load Config files from Java Variables -- " + configurationFilename + " file doesn't exist on the specified directory");
            return false;
        }

        return true;
    }

    /**
     * @param configurationFilename
     * @return Properties
     */
    public static Properties loadGuiConfiguration(String configurationFilename) {
        try {
            URL configurationURL = Thread.currentThread().getContextClassLoader().getResource(configurationFilename);
            String configurationFile = URLDecoder.decode(configurationURL.getFile(), "ISO-8859-1");
            Properties properties = new Properties();
            properties.load(new FileInputStream(configurationFile));
            return properties;
        } catch (Exception e) {
            throw new IllegalArgumentException("could not load [" + configurationFilename + "]");
        }
    }

    /**
     * @param properties
     * @param name
     */
    public static void saveProperties(Properties properties, String name) {
        try {
            // Use -Drepox.data.dir=D:\Projectos in the VM parameters in order to pass an already existing configuration.properties and gui.properties
            String configsDataDir = System.getProperty("repox.data.dir");
            String configurationFile;
            if (configDataDirAlreadyExists(configsDataDir, name))
                configurationFile = URLDecoder.decode(configsDataDir + File.separator + name, "ISO-8859-1");
            else {
                URL configurationURL = Thread.currentThread().getContextClassLoader().getResource(name);
                configurationFile = URLDecoder.decode(configurationURL.getFile(), "ISO-8859-1");
            }
            properties.store(new FileOutputStream(configurationFile), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
