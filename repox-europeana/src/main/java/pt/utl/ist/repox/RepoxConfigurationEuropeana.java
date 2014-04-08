package pt.utl.ist.repox;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 30-03-2011
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public class RepoxConfigurationEuropeana extends RepoxConfigurationDefault {
    private static final String PROPERTY_EXPORT_DEFAULT_FOLDER = "exportDefaultFolder";
    private static final String PROPERTY_ADMIN_EMAIL_PASS = "administrator.email.pass";

    private String exportDefaultFolder;
    private String adminEmailPass;


    public RepoxConfigurationEuropeana(Properties configurationProperties) throws IOException {
        super(configurationProperties);
        this.exportDefaultFolder = configurationProperties.getProperty(PROPERTY_EXPORT_DEFAULT_FOLDER);
        this.adminEmailPass = configurationProperties.getProperty(PROPERTY_ADMIN_EMAIL_PASS);
    }

    public String getExportDefaultFolder() {
        return exportDefaultFolder;
    }

    public String getAdminEmailPass() {
        return adminEmailPass;
    }

    public void setExportDefaultFolder(String exportDefaultFolder) {
        this.exportDefaultFolder = exportDefaultFolder;
    }


}

