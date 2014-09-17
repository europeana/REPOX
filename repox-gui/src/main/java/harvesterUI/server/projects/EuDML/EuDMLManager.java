//package harvesterUI.server.projects.EuDML;
//
//import com.extjs.gxt.ui.client.data.BaseModel;
//import harvesterUI.server.RepoxServiceImpl;
//import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
//import harvesterUI.server.projects.EuDMLAndLightManager;
//import harvesterUI.server.userManagement.UserManagementServiceImpl;
//import harvesterUI.server.util.StatisticsUtil;
//import harvesterUI.server.util.Util;
//import pt.utl.ist.repox.util.ServerSideException;
//import harvesterUI.shared.dataTypes.DataProviderUI;
//import harvesterUI.shared.dataTypes.SaveDataResponse;
//import harvesterUI.shared.dataTypes.admin.AdminInfo;
//import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
//import harvesterUI.shared.dataTypes.dataSet.DatasetType;
//import harvesterUI.shared.statistics.RepoxEudmlStatisticsUI;
//import harvesterUI.shared.statistics.StatisticsType;
//import harvesterUI.shared.users.DataProviderUser;
//import harvesterUI.shared.users.User;
//import pt.utl.ist.repox.RepoxConfigurationEuDml;
//import pt.utl.ist.repox.RepoxManagerEuDml;
//import pt.utl.ist.repox.dataProvider.DataSource;
//import pt.utl.ist.repox.dataProvider.DataSourceContainer;
//import pt.utl.ist.repox.statistics.RepoxStatisticsEudml;
//import pt.utl.ist.repox.statistics.StatisticsManagerEudml;
//import pt.utl.ist.repox.util.ConfigSingleton;
//import pt.utl.ist.repox.util.PropertyUtil;
//import pt.utl.ist.repox.util.RepoxContextUtilDefault;
//import pt.utl.ist.repox.util.RepoxContextUtilEuDml;
//
//import java.text.NumberFormat;
//import java.util.List;
//import java.util.Locale;
//import java.util.Properties;
//
///**
// * Created to Project REPOX
// * User: Edmundo
// * Date: 30-04-2012
// * Time: 11:29
// */
//public class EuDMLManager extends EuDMLAndLightManager {
//
//    public EuDMLManager() {
//        super();
//        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilEuDml());
//    }
//
//    public SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI, int pageSize, String username) throws ServerSideException {
//        try{
//            return EuDMLSaveData.saveDataProvider(update, dataProviderUI, pageSize, username);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException{
//        try{
//            return EuDMLSaveData.deleteDataProviders(dataProviderUIs);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public SaveDataResponse saveDataSource(boolean update, DatasetType type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {
//        try{
//            return EuDMLSaveData.saveDataSource(update, type, originalDSset, dataSourceUI,pageSize);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public String addAllOAIURL(String url,String dataProviderID,String dsSchema,String dsNamespace,
//                               String dsMTDFormat, String name, String nameCode, String exportPath,DataSetOperationsServiceImpl dataSetOperationsService) throws ServerSideException{
//        try{
//            // Check http URLs
//            String checkUrlResult = DataSetOperationsServiceImpl.checkURL(url);
//            if(checkUrlResult.equals("URL_MALFORMED"))
//                return "URL_MALFORMED";
//            else if(checkUrlResult.equals("URL_NOT_EXISTS"))
//                return "URL_NOT_EXISTS";
//
//            EuDMLSaveData.addAllOAIURL(url.trim(), dataProviderID, dsSchema, dsNamespace, dsMTDFormat, dataSetOperationsService.checkOAIURL(url.trim()));
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//        return "SUCCESS";
//    }
//
//
//    public String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException{
//        try{
//            return EuDMLSaveData.deleteDataSources(dataSourceUIs);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException{
//        try {
//            RepoxManagerEuDml repoxManagerEuDml = (RepoxManagerEuDml)ConfigSingleton.getRepoxContextUtil().getRepoxManager();
//            DataSourceContainer dataSourceContainer = repoxManagerEuDml.getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet());
//
//            DataSource dataSource = dataSourceContainer.getDataSource();
//            dataSource.setExportDir(dataSourceUI.getExportDirectory());
//
//            String recordsPerFile;
//            if(dataSourceUI.getRecordsPerFile().equals("All"))
//                recordsPerFile = "-1";
//            else
//                recordsPerFile = dataSourceUI.getRecordsPerFile();
//
//            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
//                    startExportDataSource(dataSourceUI.getDataSourceSet(), recordsPerFile, dataSourceUI.getExportFormat());
//        } catch (Exception e) {
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//        return true;
//    }
//
//    public AdminInfo loadAdminFormInfo() throws ServerSideException{
//        try{
//            RepoxConfigurationEuDml configuration = (RepoxConfigurationEuDml)RepoxServiceImpl.getRepoxManager().getConfiguration();
//            AdminInfo adminInfo = new AdminInfo();
//            adminInfo.set("repositoryFolder",configuration.getRepositoryPath());
//            adminInfo.set("configFilesFolder",configuration.getXmlConfigPath());
//            adminInfo.set("oaiRequestFolder", configuration.getOaiRequestPath());
//            adminInfo.set("derbyDbFolder",configuration.getDatabasePath());
//            adminInfo.set("baseUrn",configuration.getBaseUrn());
////            adminInfo.set("defaultExportFolder",configuration.getRepositoryPath());
//            adminInfo.set("adminEmail",configuration.getAdministratorEmail());
//            adminInfo.set("smtpServer",configuration.getSmtpServer());
//            adminInfo.set("smtpPort",configuration.getSmtpPort());
//            adminInfo.set("repoxDefualtEmailSender",configuration.getDefaultEmail());
//            adminInfo.set("httpRequestFolder",configuration.getHttpRequestPath());
//            adminInfo.set("ftpRequestFolder",configuration.getFtpRequestPath());
//            adminInfo.set("sampleRecords",configuration.getSampleRecords());
//            adminInfo.set("useCountriesTxt",configuration.getUseCountriesTxt());
//            adminInfo.set("sendEmailAfterIngest",configuration.getSendEmailAfterIngest());
//            adminInfo.set("useYaddaStorage",configuration.isUseYaddaStorage());
//            adminInfo.set("backendUrl",configuration.getBackendUrl());
//            adminInfo.set("useMailSSLAuthentication",configuration.isUseMailSSLAuthentication());
//
//            // optional fields
//            if(configuration.getMailPassword() != null)
//                adminInfo.set("adminPass",configuration.getMailPassword());
//            if(configuration.getLdapHost() != null)
//                adminInfo.set("ldapHost",configuration.getLdapHost());
//            if(configuration.getLdapUserPrefix() != null)
//                adminInfo.set("ldapUserPrefix",configuration.getLdapUserPrefix());
//            if(configuration.getLdapLoginDN() != null)
//                adminInfo.set("ldapLoginDN",configuration.getLdapLoginDN());
//
//            return adminInfo;
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public void saveAdminFormInfo(BaseModel results) throws ServerSideException{
//        try{
//            Properties properties = PropertyUtil.loadCorrectedConfiguration(RepoxContextUtilDefault.CONFIG_FILE);
//            properties.setProperty("repository.dir",(String)results.get("repositoryFolder"));
//            properties.setProperty("xmlConfig.dir",(String)results.get("configFilesFolder"));
//            properties.setProperty("oairequests.dir",(String)results.get("oaiRequestFolder"));
//            properties.setProperty("database.dir",(String)results.get("derbyDbFolder"));
//            properties.setProperty("baseurn",(String)results.get("baseUrn"));
//            properties.setProperty("administrator.email",(String)results.get("adminEmail"));
//            properties.setProperty("smtp.server",(String)results.get("smtpServer"));
//            properties.setProperty("smtp.port",(String)results.get("smtpPort"));
//            properties.setProperty("default.email",(String)results.get("repoxDefaultEmailSender"));
//            properties.setProperty("httprequests.dir",(String)results.get("httpRequestFolder"));
//            properties.setProperty("ftprequests.dir",(String)results.get("ftpRequestFolder"));
//            properties.setProperty("sample.records",(String)results.get("sampleRecords"));
//            properties.setProperty("userCountriesTxtFile",String.valueOf(results.get("useCountriesTxt")));
//            properties.setProperty("sendEmailAfterIngest",String.valueOf(results.get("sendEmailAfterIngest")));
//            properties.setProperty("yaddaStorage",String.valueOf(results.get("useYaddaStorage")));
//            properties.setProperty("backendRootUrl",String.valueOf(results.get("backendUrl")));
//            properties.setProperty("useMailSSLAuthentication",String.valueOf(results.get("useMailSSLAuthentication")));
//
//            // optional fields
//            if(results.get("adminPass") != null)
//                properties.setProperty("administrator.email.pass",(String)results.get("adminPass"));
//            if(results.get("ldapHost") != null)
//                properties.setProperty("ldapHost",(String)results.get("ldapHost"));
//            if(results.get("ldapUserPrefix") != null)
//                properties.setProperty("ldapUserPrefix",(String)results.get("ldapUserPrefix"));
//            if(results.get("ldapLoginDN") != null)
//                properties.setProperty("ldapLoginDN",(String)results.get("ldapLoginDN"));
//
//            PropertyUtil.saveProperties(properties, RepoxContextUtilDefault.CONFIG_FILE);
//            ConfigSingleton.getRepoxContextUtil().reloadProperties();
////            System.out.println("Done save admin");
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//
//    public RepoxEudmlStatisticsUI getStatisticsInfo(StatisticsType statisticsType, String username) throws ServerSideException {
//        try {
//            StatisticsManagerEudml manager = (StatisticsManagerEudml) ConfigSingleton.getRepoxContextUtil().getRepoxManager().getStatisticsManager();
//            User user = UserManagementServiceImpl.getInstance().getUser(username);
//            List<String> dpIds;
//            if(user instanceof DataProviderUser)
//                dpIds = ((DataProviderUser) user).getAllowedDataProviderIds();
//            else
//                dpIds = null;
//
//            RepoxStatisticsEudml statistics;
//            if(statisticsType == StatisticsType.EXTERNAL){
//                statistics = manager.generateEudmlExternalStatistics(dpIds);
//            }else if(statisticsType == StatisticsType.YADDA){
//                statistics = manager.generateEudmlYaddaStatistics(dpIds);
//            }else if(statisticsType == StatisticsType.INTERNAL){
//                statistics = manager.generateEudmlInternalStatistics(dpIds);
//            }else {
//                // Default case is statistics of ALL data
//                statistics = manager.generateStatistics(dpIds);
//            }
//
//            NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
//            String totalRecords = numberFormat.format(statistics.getRecordsTotal());
//
//            int recordsAvgDataSource = (int)statistics.getRecordsAvgDataSource();
//            int recordsAvgDataProvider = (int)statistics.getRecordsAvgDataProvider();
//
//            return new RepoxEudmlStatisticsUI(statistics.getGenerationDate(),statistics.getDataSourcesIdExtracted(),
//                    statistics.getDataSourcesIdGenerated(),statistics.getDataSourcesIdProvided(),
//                    statistics.getDataProviders(), statistics.getDataSourcesOai(), statistics.getDataSourcesZ3950(),
//                    statistics.getDataSourcesDirectoryImporter(),statistics.getDataSourcesYadda(),
//                    StatisticsUtil.getMetadataFormatStatistics(statistics.getDataSourcesMetadataFormats(),false),
//                    StatisticsUtil.getMetadataFormatStatistics(statistics.getDataSourcesMetadataFormats(),true),
//                    recordsAvgDataSource,recordsAvgDataProvider,
//                    statistics.getCountriesRecords(),totalRecords);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerSideException(Util.stackTraceToString(e));
//        }
//    }
//}
