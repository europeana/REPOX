package pt.utl.ist.configuration;

import java.io.IOException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 30-03-2011
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public class DefaultRepoxConfiguration extends RepoxConfiguration {
    private static final String PROPERTY_EXPORT_DEFAULT_FOLDER = "exportDefaultFolder";
    private static final String PROPERTY_EMAIL_PASS = "default.email.pass";

    private String exportDefaultFolder;
    private String repoxDefaultEmailPass;


    public DefaultRepoxConfiguration(PropertiesConfigurationLayout configurationPropertiesLayout) throws IOException {
        super(configurationPropertiesLayout);
        PropertiesConfiguration configuration = configurationPropertiesLayout.getConfiguration();
        this.exportDefaultFolder = configuration.getProperty(PROPERTY_EXPORT_DEFAULT_FOLDER).toString();
        this.repoxDefaultEmailPass = configuration.getProperty(PROPERTY_EMAIL_PASS).toString();
    }

    public String getExportDefaultFolder() {
        return exportDefaultFolder;
    }

    public String getRepoxDefaultEmailPass() {
        return repoxDefaultEmailPass;
    }

    public void setExportDefaultFolder(String exportDefaultFolder) {
        this.exportDefaultFolder = exportDefaultFolder;
    }


}

