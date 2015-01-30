package pt.utl.ist.configuration;

import java.io.IOException;
import java.util.Properties;

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


    public DefaultRepoxConfiguration(Properties configurationProperties) throws IOException {
        super(configurationProperties);
        this.exportDefaultFolder = configurationProperties.getProperty(PROPERTY_EXPORT_DEFAULT_FOLDER);
        this.repoxDefaultEmailPass = configurationProperties.getProperty(PROPERTY_EMAIL_PASS);
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

