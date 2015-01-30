package pt.utl.ist.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA. User: GPedrosa Date: 06-04-2011 Time: 11:42 To
 * change this template use File | Settings | File Templates.
 */
public abstract class RepoxConfiguration {
    /** RepoxConfiguration TEMP_DIRNAME */
    public static final String  TEMP_DIRNAME                         = "temp";
    /** RepoxConfiguration METADATA_TRANSFORMATIONS_DIRNAME */
    public static final String  METADATA_TRANSFORMATIONS_DIRNAME     = "xslt";
    /** RepoxConfiguration METADATA_TRANSFORMATIONS_XMAP_SUBDIR */
    public static final String  METADATA_TRANSFORMATIONS_XMAP_SUBDIR = "xmap";
    /** RepoxConfiguration LOGOS_DIRNAME */
    public static final String  LOGOS_DIRNAME                        = "logos";

    private static final String PROPERTY_BASE_URN                    = "baseurn";
    private static final String PROPERTY_REPOSITORY_DIR              = "repository.dir";
    private static final String PROPERTY_XML_CONFIG_DIR              = "xmlConfig.dir";
    private static final String PROPERTY_FTP_REQUEST_DIR             = "ftprequests.dir";
    private static final String PROPERTY_HTTP_REQUEST_DIR            = "httprequests.dir";
    private static final String PROPERTY_OAI_REQUEST_DIR             = "oairequests.dir";

    private static final String PROPERTY_ADMINISTRATOR_EMAIL         = "administrator.email";
    private static final String PROPERTY_SMTP_SERVER                 = "smtp.server";
    private static final String PROPERTY_DEFAULT_EMAIL               = "default.email";
    private static final String PROPERTY_MAIL_PASS                   = "default.email.pass";
    private static final String PROPERTY_SMTP_PORT                   = "smtp.port";

    private static final String PROPERTY_SAMPLE_RECORDS              = "sample.records";
    private static final String PROPERTY_DB_DIR                      = "database.dir";
    private static final String PROPERTY_DB_DRIVER_CLASSNAME         = "database.driverClassName";
    private static final String PROPERTY_DB_EMBEDDED_DRIVER          = "database.embeddedDriver";
    private static final String PROPERTY_DB_URL                      = "database.url";
    private static final String PROPERTY_DB_CREATE                   = "database.create";
    private static final String PROPERTY_DB_USER                     = "database.user";
    private static final String PROPERTY_DB_PASSWORD                 = "database.password";

    private static final String PROPERTY_LDAP_HOST                   = "ldapHost";
    private static final String PROPERTY_LDAP_ROOT_DN                = "ldapRootDN";
    private static final String PROPERTY_LDAP_ROOT_PASSWORD          = "ldapRootPassword";
    private static final String PROPERTY_LDAP_BASE_PATH              = "ldapBasePath";
    private static final String PROPERTY_LDAP_USER_PREFIX            = "ldapUserPrefix";
    private static final String PROPERTY_LDAP_LOGIN_DN               = "ldapLoginDN";
    private static final String PROPERTY_USE_COUNTRIES_TXT           = "userCountriesTxtFile";
    private static final String PROPERTY_SEND_EMAIL_AFTER_INGESTION  = "sendEmailAfterIngest";
    private static final String PROPERTY_SERVER_OAI_URL              = "currentServerOAIUrl";
    private static final String PROPERTY_USE_MAIL_AUTHENTICATION     = "useMailSSLAuthentication";
    private static final String PROPERTY_USE_OAI_NAMESPACE           = "useOAINamespace";

    private String              baseUrn;
    private String              repositoryPath;
    private String              xmlConfigPath;
    private String              oaiRequestPath;
    private String              ftpRequestPath;
    private String              httpRequestPath;

    private String              administratorEmail;
    private String              smtpServer;
    private String              defaultEmail;
    private String              mailPassword;
    private String              smtpPort;

    private int                 sampleRecords;

    private String              databasePath;
    private String              databaseDriverClassName;
    private boolean             databaseEmbeddedDriver;
    private String              databaseUrl;
    private boolean             databaseCreate;
    private String              databaseUser;
    private String              databasePassword;

    //    private String mdrUrl;
    private String              ldapHost;
    private String              ldapRootDN;
    private String              ldapRootPassword;
    private String              ldapBasePath;
    private String              ldapUserPrefix;
    private String              ldapLoginDN;
    private Boolean             useCountriesTxt;
    private Boolean             sendEmailAfterIngest;
    private String              currentServerOAIUrl;
    private boolean             useMailSSLAuthentication;
    private boolean             useOAINamespace;

    /**
     * Creates a new instance of this class.
     * 
     * @param configurationProperties
     * @throws IOException
     */
    public RepoxConfiguration(Properties configurationProperties) throws IOException {
        super();

        this.baseUrn = configurationProperties.getProperty(PROPERTY_BASE_URN);
        this.repositoryPath = configurationProperties.getProperty(PROPERTY_REPOSITORY_DIR);
        File repositoryFile = new File(repositoryPath);
        if (!repositoryFile.exists()) {
            repositoryFile.mkdirs();
        }
        this.xmlConfigPath = configurationProperties.getProperty(PROPERTY_XML_CONFIG_DIR);
        File xmlConfigFile = new File(xmlConfigPath);
        if (!xmlConfigFile.exists()) {
            xmlConfigFile.mkdirs();
        }
        this.oaiRequestPath = configurationProperties.getProperty(PROPERTY_OAI_REQUEST_DIR);
        File oaiRequestFile = new File(oaiRequestPath);
        if (!oaiRequestFile.exists()) {
            oaiRequestFile.mkdirs();
        }

        this.ftpRequestPath = configurationProperties.getProperty(PROPERTY_FTP_REQUEST_DIR);
        File ftpRequestFile = new File(ftpRequestPath);
        if (!ftpRequestFile.exists()) {
            ftpRequestFile.mkdirs();
        }

        this.httpRequestPath = configurationProperties.getProperty(PROPERTY_HTTP_REQUEST_DIR);
        File httpRequestFile = new File(httpRequestPath);
        if (!httpRequestFile.exists()) {
            httpRequestFile.mkdirs();
        }

        File tempDir = getTempDir();
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        if (configurationProperties.getProperty(PROPERTY_SAMPLE_RECORDS) != null) {
            try {
                this.sampleRecords = Integer.valueOf(configurationProperties.getProperty(PROPERTY_SAMPLE_RECORDS));
            } catch (Exception e) {
                this.sampleRecords = 1000;
            }
        } else {
            this.sampleRecords = 1000;
        }
        this.smtpPort = configurationProperties.getProperty(PROPERTY_SMTP_PORT);
        this.mailPassword = configurationProperties.getProperty(PROPERTY_MAIL_PASS);
        this.defaultEmail = configurationProperties.getProperty(PROPERTY_DEFAULT_EMAIL);
        this.administratorEmail = configurationProperties.getProperty(PROPERTY_ADMINISTRATOR_EMAIL);
        this.smtpServer = configurationProperties.getProperty(PROPERTY_SMTP_SERVER);
        this.databasePath = configurationProperties.getProperty(PROPERTY_DB_DIR);
        this.databaseDriverClassName = configurationProperties.getProperty(PROPERTY_DB_DRIVER_CLASSNAME);
        this.databaseEmbeddedDriver = Boolean.parseBoolean(configurationProperties.getProperty(PROPERTY_DB_EMBEDDED_DRIVER));
        this.databaseUrl = configurationProperties.getProperty(PROPERTY_DB_URL);
        this.databaseCreate = Boolean.parseBoolean(configurationProperties.getProperty(PROPERTY_DB_CREATE));
        this.databaseUser = configurationProperties.getProperty(PROPERTY_DB_USER);
        this.databasePassword = configurationProperties.getProperty(PROPERTY_DB_PASSWORD);

        this.useCountriesTxt = Boolean.valueOf(configurationProperties.getProperty(PROPERTY_USE_COUNTRIES_TXT) == null ? "true" : configurationProperties.getProperty(PROPERTY_USE_COUNTRIES_TXT));
        this.sendEmailAfterIngest = Boolean.valueOf(configurationProperties.getProperty(PROPERTY_SEND_EMAIL_AFTER_INGESTION) == null ? "true" : configurationProperties
                .getProperty(PROPERTY_SEND_EMAIL_AFTER_INGESTION));
        this.ldapHost = configurationProperties.getProperty(PROPERTY_LDAP_HOST);
        this.ldapRootDN = configurationProperties.getProperty(PROPERTY_LDAP_ROOT_DN);
        this.ldapRootPassword = configurationProperties.getProperty(PROPERTY_LDAP_ROOT_PASSWORD);
        this.ldapBasePath = configurationProperties.getProperty(PROPERTY_LDAP_BASE_PATH);

        this.ldapUserPrefix = configurationProperties.getProperty(PROPERTY_LDAP_USER_PREFIX);
        this.ldapLoginDN = configurationProperties.getProperty(PROPERTY_LDAP_LOGIN_DN);
        this.currentServerOAIUrl = configurationProperties.getProperty(PROPERTY_SERVER_OAI_URL);
        this.useMailSSLAuthentication = Boolean.valueOf(configurationProperties.getProperty(PROPERTY_USE_MAIL_AUTHENTICATION) == null ? "true" : configurationProperties
                .getProperty(PROPERTY_USE_MAIL_AUTHENTICATION));
        this.useOAINamespace = Boolean.valueOf(configurationProperties.getProperty(PROPERTY_USE_OAI_NAMESPACE) == null ? "false" : configurationProperties.getProperty(PROPERTY_USE_OAI_NAMESPACE));
    }

    public String getBaseUrn() {
        return baseUrn;
    }

    public void setBaseUrn(String baseUrn) {
        this.baseUrn = baseUrn;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public String getXmlConfigPath() {
        return xmlConfigPath;
    }

    public void setXmlConfigPath(String xmlConfigPath) {
        this.xmlConfigPath = xmlConfigPath;
    }

    public String getOaiRequestPath() {
        return oaiRequestPath;
    }

    public void setOaiRequestPath(String oaiRequestPath) {
        this.oaiRequestPath = oaiRequestPath;
    }

    public String getFtpRequestPath() {
        return ftpRequestPath;
    }

    public void setFtpRequestPath(String ftpRequestPath) {
        this.ftpRequestPath = ftpRequestPath;
    }

    public String getHttpRequestPath() {
        return httpRequestPath;
    }

    public void setHttpRequestPath(String httpRequestPath) {
        this.httpRequestPath = httpRequestPath;
    }

    public String getAdministratorEmail() {
        return administratorEmail;
    }

    public void setAdministratorEmail(String administratorEmail) {
        this.administratorEmail = administratorEmail;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getDatabaseDriverClassName() {
        return databaseDriverClassName;
    }

    public void setDatabaseDriverClassName(String databaseDriverClassName) {
        this.databaseDriverClassName = databaseDriverClassName;
    }

    public boolean isDatabaseEmbeddedDriver() {
        return databaseEmbeddedDriver;
    }

    public void setDatabaseEmbeddedDriver(boolean databaseEmbeddedDriver) {
        this.databaseEmbeddedDriver = databaseEmbeddedDriver;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public boolean isDatabaseCreate() {
        return databaseCreate;
    }

    public void setDatabaseCreate(boolean databaseCreate) {
        this.databaseCreate = databaseCreate;
    }

    public File getTempDir() {
        return new File(xmlConfigPath, TEMP_DIRNAME);
    }

    public File getLogosDir() {
        return new File(repositoryPath, LOGOS_DIRNAME);
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public int getSampleRecords() {
        return sampleRecords;
    }

    public void setSampleRecords(int sampleRecords) {
        this.sampleRecords = sampleRecords;
    }

    public String getDefaultEmail() {
        return defaultEmail;
    }

    public void setDefaultEmail(String defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public Boolean getUseCountriesTxt() {
        return useCountriesTxt;
    }

    public Boolean getSendEmailAfterIngest() {
        return sendEmailAfterIngest;
    }

    public String getLdapHost() {
        return ldapHost;
    }

    public String getLdapUserPrefix() {
        return ldapUserPrefix;
    }

    public String getLdapLoginDN() {
        return ldapLoginDN;
    }

    public String getCurrentServerOAIUrl() {
        return currentServerOAIUrl;
    }

    public boolean isUseMailSSLAuthentication() {
        return useMailSSLAuthentication;
    }

    public boolean isUseOAINamespace() {
        return useOAINamespace;
    }

    /**
     * Returns the ldapRootUser.
     * @return the ldapRootUser
     */
    public String getLdapRootDN() {
        return ldapRootDN;
    }

    /**
     * Sets the ldapRootUser to the given value.
     * @param ldapRootUser the ldapRootUser to set
     */
    public void setLdapRootDN(String ldapRootDN) {
        this.ldapRootDN = ldapRootDN;
    }

    /**
     * Returns the ldapRootPassword.
     * @return the ldapRootPassword
     */
    public String getLdapRootPassword() {
        return ldapRootPassword;
    }

    /**
     * Sets the ldapRootPassword to the given value.
     * @param ldapRootPassword the ldapRootPassword to set
     */
    public void setLdapRootPassword(String ldapRootPassword) {
        this.ldapRootPassword = ldapRootPassword;
    }

    /**
     * Returns the ldapBasePath.
     * @return the ldapBasePath
     */
    public String getLdapBasePath() {
        return ldapBasePath;
    }

    /**
     * Sets the ldapBasePath to the given value.
     * @param ldapBasePath the ldapBasePath to set
     */
    public void setLdapBasePath(String ldapBasePath) {
        this.ldapBasePath = ldapBasePath;
    }
}
