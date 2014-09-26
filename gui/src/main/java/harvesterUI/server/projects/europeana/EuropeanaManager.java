package harvesterUI.server.projects.europeana;

import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.DataType;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
import harvesterUI.server.dataManagement.filters.FilterManagementUtil;
import harvesterUI.server.dataManagement.filters.FilteredDataResponse;
import harvesterUI.server.projects.ProjectManager;
import harvesterUI.server.userManagement.UserManagementServiceImpl;
import harvesterUI.server.util.PagingUtil;
import harvesterUI.server.util.StatisticsUtil;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.dataTypes.admin.AdminInfo;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;
import harvesterUI.shared.filters.FilterAttribute;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.filters.FilterType;
import harvesterUI.shared.search.BaseSearchResult;
import harvesterUI.shared.search.EuropeanaSearchResult;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.statistics.RepoxStatisticsUI;
import harvesterUI.shared.statistics.StatisticsType;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.users.DataProviderUser;
import harvesterUI.shared.users.User;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.AuthenticationFailedException;

import org.dom4j.DocumentException;

import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.configuration.EuropeanaRepoxConfiguration;
import pt.utl.ist.repox.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.repox.configuration.EuropeanaRepoxContextUtil;
import pt.utl.ist.repox.configuration.EuropeanaRepoxManager;
import pt.utl.ist.repox.dataProvider.Countries;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.rest.dataProvider.Aggregator;
import pt.utl.ist.repox.rest.dataProvider.DefaultDataManager;
import pt.utl.ist.repox.rest.dataProvider.DefualtDataProvider;
import pt.utl.ist.repox.rest.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.repox.rest.statistics.DefaultRepoxStatistics;
import pt.utl.ist.repox.rest.statistics.DefaultStatisticsManager;
import pt.utl.ist.repox.rest.util.DefaultEmailUtil;
import pt.utl.ist.repox.task.OldTask;
import pt.utl.ist.repox.task.OldTaskReviewer;
import pt.utl.ist.repox.util.FileUtilSecond;
import pt.utl.ist.repox.util.PropertyUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import com.extjs.gxt.ui.client.data.ModelData;

import eu.europeana.definitions.domain.Country;

//import com.google.common.collect.Iterables;
//import pt.utl.ist.repox.RepoxManagerEuDml;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 30-04-2012
 * Time: 11:29
 */
public class EuropeanaManager extends ProjectManager {

    private int filteredDataSize;

    public EuropeanaManager() {
        ConfigSingleton.setRepoxContextUtil(new EuropeanaRepoxContextUtil());
    }

    public RepoxStatisticsUI getStatisticsInfo(StatisticsType statisticsType, String username) throws ServerSideException {
        try {
            DefaultStatisticsManager manager = (DefaultStatisticsManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getStatisticsManager();
            User user = UserManagementServiceImpl.getInstance().getUser(username);
            List<String> dpIds;
            if(user instanceof DataProviderUser)
                dpIds = ((DataProviderUser) user).getAllowedDataProviderIds();
            else
                dpIds = null;

            DefaultRepoxStatistics statistics = (DefaultRepoxStatistics)manager.generateStatistics(dpIds);

            NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
            String totalRecords = numberFormat.format(statistics.getRecordsTotal());

            int recordsAvgDataSource = (int)statistics.getRecordsAvgDataSource();
            int recordsAvgDataProvider = (int)statistics.getRecordsAvgDataProvider();

            RepoxStatisticsUI repoxStatisticsUI = new RepoxStatisticsUI(statistics.getGenerationDate(),
                    statistics.getDataSourcesIdExtracted(),
                    statistics.getDataSourcesIdGenerated(),statistics.getDataSourcesIdProvided(),
                    statistics.getDataProviders(),
                    statistics.getDataSourcesOai() + statistics.getDataSourcesZ3950() + statistics.getDataSourcesDirectoryImporter(),
                    statistics.getDataSourcesOai(),statistics.getDataSourcesZ3950(),
                    statistics.getDataSourcesDirectoryImporter(),
                    StatisticsUtil.getMetadataFormatStatistics(statistics.getDataSourcesMetadataFormats(),false),
                    StatisticsUtil.getMetadataFormatStatistics(statistics.getDataSourcesMetadataFormats(),true),
                    recordsAvgDataSource,recordsAvgDataProvider,
                    statistics.getCountriesRecords(),totalRecords);
            repoxStatisticsUI.setAggregators(statistics.getAggregators());
            return repoxStatisticsUI;

        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Map<String,String> getFullCountryList() throws ServerSideException{
        try{
            boolean useCountriesFile = RepoxServiceImpl.getRepoxManager().getConfiguration().getUseCountriesTxt();
            if(useCountriesFile)
                return Countries.getCountries();
            else
                return createEuropeanaCountriesMap();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private Map<String,String> createEuropeanaCountriesMap() {
        Map<String,String> results = new HashMap<String,String>();

        Country[] countries = Country.values();

        for(int index=0; index < countries.length; index++){
            String countryName = countries[index].name();

            Iterator iterator = Countries.getCountries().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry mapEntry=(Map.Entry)iterator.next();
                if(mapEntry.getValue().equals(countryName)) {
                    results.put((String)mapEntry.getKey(),(String)mapEntry.getValue());
                }
            }
        }
        return results;
    }

    public AdminInfo loadAdminFormInfo() throws ServerSideException{
        try{
            EuropeanaRepoxConfiguration configuration = (EuropeanaRepoxConfiguration)RepoxServiceImpl.getRepoxManager().getConfiguration();
            AdminInfo adminInfo = new AdminInfo();
            adminInfo.set("repositoryFolder",configuration.getRepositoryPath());
            adminInfo.set("configFilesFolder",configuration.getXmlConfigPath());
            adminInfo.set("oaiRequestFolder", configuration.getOaiRequestPath());
            adminInfo.set("derbyDbFolder",configuration.getDatabasePath());
            adminInfo.set("baseUrn",configuration.getBaseUrn());

            Properties properties = PropertyUtil.loadCorrectedConfiguration("oaicat.properties");
            adminInfo.set("oaiRepoName",properties.getProperty("Identify.repositoryName","undefined"));
            adminInfo.set("oaiMaxList",properties.getProperty("DataSourceOAICatalog.maxListSize","undefined"));

            adminInfo.set("defaultExportFolder",configuration.getExportDefaultFolder());
            adminInfo.set("adminEmail",configuration.getAdministratorEmail());
            adminInfo.set("smtpServer",configuration.getSmtpServer());
            adminInfo.set("smtpPort",configuration.getSmtpPort());
            adminInfo.set("repoxDefualtEmailSender",configuration.getDefaultEmail());
            adminInfo.set("httpRequestFolder",configuration.getHttpRequestPath());
            adminInfo.set("ftpRequestFolder",configuration.getFtpRequestPath());
            adminInfo.set("sampleRecords",configuration.getSampleRecords());
            adminInfo.set("useCountriesTxt",configuration.getUseCountriesTxt());
            adminInfo.set("sendEmailAfterIngest",configuration.getSendEmailAfterIngest());
            adminInfo.set("useMailSSLAuthentication",configuration.isUseMailSSLAuthentication());
            adminInfo.set("useOAINamespace",configuration.isUseOAINamespace());

            // optional fields
            if(configuration.getLdapHost() != null)
                adminInfo.set("ldapHost",configuration.getLdapHost());
            if(configuration.getLdapUserPrefix() != null)
                adminInfo.set("ldapUserPrefix",configuration.getLdapUserPrefix());
            if(configuration.getLdapLoginDN() != null)
                adminInfo.set("ldapLoginDN",configuration.getLdapLoginDN());
            if(configuration.getAdminEmailPass() != null)
                adminInfo.set("adminPass",configuration.getAdminEmailPass());

            return adminInfo;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public void saveAdminFormInfo(AdminInfo results) throws ServerSideException{
        try{
            Properties properties = PropertyUtil.loadCorrectedConfiguration(DefaultRepoxContextUtil.CONFIG_FILE);
            properties.setProperty("repository.dir",(String)results.get("repositoryFolder"));
            properties.setProperty("xmlConfig.dir",(String)results.get("configFilesFolder"));
            properties.setProperty("oairequests.dir",(String)results.get("oaiRequestFolder"));
            properties.setProperty("database.dir",(String)results.get("derbyDbFolder"));
            properties.setProperty("baseurn",(String)results.get("baseUrn"));
            properties.setProperty("exportDefaultFolder",(String)results.get("defaultExportFolder"));
            properties.setProperty("administrator.email",(String)results.get("adminEmail"));
            properties.setProperty("smtp.server",(String)results.get("smtpServer"));
            properties.setProperty("smtp.port",(String)results.get("smtpPort"));
            properties.setProperty("default.email",(String)results.get("repoxDefaultEmailSender"));
            properties.setProperty("httprequests.dir",(String)results.get("httpRequestFolder"));
            properties.setProperty("ftprequests.dir",(String)results.get("ftpRequestFolder"));
            properties.setProperty("sample.records",(String)results.get("sampleRecords"));
            properties.setProperty("userCountriesTxtFile",String.valueOf(results.get("useCountriesTxt")));
            properties.setProperty("sendEmailAfterIngest",String.valueOf(results.get("sendEmailAfterIngest")));
            properties.setProperty("useMailSSLAuthentication",String.valueOf(results.get("useMailSSLAuthentication")));
            properties.setProperty("useOAINamespace",String.valueOf(results.get("useOAINamespace")));

            // optional fields
            if(results.get("adminPass") != null)
                properties.setProperty("administrator.email.pass",(String)results.get("adminPass"));
            if(results.get("ldapHost") != null)
                properties.setProperty("ldapHost",(String)results.get("ldapHost"));
            if(results.get("ldapUserPrefix") != null)
                properties.setProperty("ldapUserPrefix",(String)results.get("ldapUserPrefix"));
            if(results.get("ldapLoginDN") != null)
                properties.setProperty("ldapLoginDN",(String)results.get("ldapLoginDN"));

            Properties oaiProperties = PropertyUtil.loadCorrectedConfiguration("oaicat.properties");
            if(results.get("oaiRepoName") != null)
                oaiProperties.setProperty("Identify.repositoryName",(String)results.get("oaiRepoName"));
            if(results.get("oaiMaxList") != null)
                oaiProperties.setProperty("DataSourceOAICatalog.maxListSize",(String)results.get("oaiMaxList"));

            PropertyUtil.saveProperties(oaiProperties, "oaicat.properties");
            reloadOAIProperties(results.getReloadOAIPropertiesUrl());
            PropertyUtil.saveProperties(properties, DefaultRepoxContextUtil.CONFIG_FILE);
            ConfigSingleton.getRepoxContextUtil().reloadProperties();
//            System.out.println("Done save admin");
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }


    /*********************************************************
     Paging Europeana Functions
     **********************************************************/

    public List<DataContainer> getParsedData(int offSet, int limit) throws ServerSideException{
        List<DataContainer> mainData = new ArrayList<DataContainer>();
        try{
            List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
            int realLimit = allDataList.size();
            if(realLimit > 0){
                DataProviderUI currentDataProvider = null;
                AggregatorUI currentAggregator = null;
                for (int i = offSet; i<(limit>realLimit ? realLimit : limit); i++){
                    if(allDataList.get(i) instanceof Aggregator){
                        currentAggregator = parseAggregatorEuropeana((Aggregator) allDataList.get(i));
                        mainData.add(currentAggregator);
                    }else if(allDataList.get(i) instanceof DataProvider){
                        if(currentAggregator == null){
                            currentAggregator = parseAggregatorEuropeana(((DefaultDataManager) RepoxServiceImpl.
                                    getRepoxManager().getDataManager()).getAggregatorParent(((DataProvider) allDataList.get(i)).getId()));
                            mainData.add(currentAggregator);
                        }
                        currentDataProvider = parseDataProviderEuropeana((DataProvider) allDataList.get(i), currentAggregator);
                        currentAggregator.add(currentDataProvider);
                        currentAggregator.addDataProvider(currentDataProvider);
                    } else if(allDataList.get(i) instanceof DataSourceContainer){
                        if(currentAggregator == null){
                            String parentDataProviderId = RepoxServiceImpl.
                                    getRepoxManager().getDataManager().getDataProviderParent(((DataSourceContainer) allDataList.get(i)).getDataSource().getId()).getId();
                            currentAggregator = parseAggregatorEuropeana(((DefaultDataManager) RepoxServiceImpl.
                                    getRepoxManager().getDataManager()).getAggregatorParent(parentDataProviderId));
                            mainData.add(currentAggregator);
                        }
                        if(currentDataProvider == null){
                            currentDataProvider = parseDataProviderEuropeana(RepoxServiceImpl.getRepoxManager().getDataManager().
                                    getDataProviderParent(((DataSourceContainer) allDataList.get(i)).getDataSource().getId()),
                                    currentAggregator);
                            currentAggregator.add(currentDataProvider);
                            currentAggregator.addDataProvider(currentDataProvider);
                        }

                        DataSourceUI dataSourceUI = parseDataSource((DataSourceContainer) allDataList.get(i), currentDataProvider);
                        currentDataProvider.add(dataSourceUI);
                        currentDataProvider.addDataSource(dataSourceUI);
                    }
                }
            }

            return mainData;
        } catch (IndexOutOfBoundsException e){
//            return mainData;
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public DataContainer getSearchResult(ModelData data) throws ServerSideException{
        try{
            DataContainer dataContainer = new DataContainer(UUID.randomUUID().toString());
            String id = data.get("id");

            if(data.get("dataType").equals(DataType.AGGREGATOR.name())){
                DefaultDataManager dataManagerEuropeana = (DefaultDataManager)RepoxServiceImpl.getRepoxManager().getDataManager();
                Aggregator aggregator = dataManagerEuropeana.getAggregator(id);
                AggregatorUI aggregatorUI = parseAggregatorEuropeana(aggregator);
                dataContainer.add(aggregatorUI);
                for(DataProvider dataProvider : aggregator.getDataProviders()){
                    DataProviderUI dataProviderUI = parseDataProviderEuropeana(dataProvider,aggregatorUI);
                    aggregatorUI.add(dataProviderUI);
                    aggregatorUI.addDataProvider(dataProviderUI);

                    for(DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()){
                        DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,dataProviderUI);
                        dataProviderUI.add(dataSourceUI);
                        dataProviderUI.addDataSource(dataSourceUI);
                    }
                }
            }else if(data.get("dataType").equals(DataType.DATA_PROVIDER.name())){
                DataProvider dataProvider = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProvider(id);
                AggregatorUI aggregatorUI = parseAggregatorEuropeana(((DefaultDataManager)RepoxServiceImpl.
                        getRepoxManager().getDataManager()).getAggregatorParent(dataProvider.getId()));
                dataContainer.add(aggregatorUI);
                DataProviderUI dataProviderUI = parseDataProviderEuropeana(dataProvider,aggregatorUI);
                aggregatorUI.add(dataProviderUI);
                aggregatorUI.addDataProvider(dataProviderUI);

                for(DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()){
                    DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,dataProviderUI);
                    dataProviderUI.add(dataSourceUI);
                    dataProviderUI.addDataSource(dataSourceUI);
                }
            }else if(data.get("dataType").equals(DataType.DATA_SET.name())){
                String parentDataProviderId = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(id).getId();
                AggregatorUI aggregatorUI = parseAggregatorEuropeana(((DefaultDataManager)RepoxServiceImpl.
                        getRepoxManager().getDataManager()).getAggregatorParent(parentDataProviderId));
                dataContainer.add(aggregatorUI);
                DataProviderUI dataProviderUI = parseDataProviderEuropeana(RepoxServiceImpl.getRepoxManager().getDataManager().
                        getDataProviderParent(id), aggregatorUI);
                aggregatorUI.add(dataProviderUI);
                aggregatorUI.addDataProvider(dataProviderUI);

                DataSourceContainer dataSourceContainer = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(id);
                DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer,dataProviderUI);
                dataProviderUI.add(dataSourceUI);
                dataProviderUI.addDataSource(dataSourceUI);
            }
            return dataContainer;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<DataContainer> getViewResult(int offset, int limit, String type) throws ServerSideException{
        List<DataContainer> mainData = new ArrayList<DataContainer>();
        try{
            if(type.equals("AGGREAGATORS")){
                DefaultDataManager dataManagerEuropeana = (DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager();
                List<Aggregator> aggregatorEuropeanaList = dataManagerEuropeana.getAggregators();
                for (int i = offset; i<limit && i<aggregatorEuropeanaList.size(); i++){
                    mainData.add(parseAggregatorEuropeana(aggregatorEuropeanaList.get(i)));
                }
            }else if(type.equals("DATA_PROVIDERS")){
                List<DataProvider> dpList = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviders();
                for (int i = offset; i<limit && i<dpList.size(); i++){
                    AggregatorUI currentAggregator = parseAggregatorEuropeana(((DefaultDataManager)RepoxServiceImpl.
                            getRepoxManager().getDataManager()).getAggregatorParent(dpList.get(i).getId()));
                    mainData.add(parseDataProviderEuropeana(dpList.get(i), currentAggregator));
                }
            }else if(type.equals("DATA_SETS")){
                DefaultDataManager dataManagerEuropeana = (DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager();
                List<Aggregator> aggregatorEuropeanaList = dataManagerEuropeana.getAggregators();
                for(Aggregator aggregatorEuropeana : aggregatorEuropeanaList){
                    for(DataProvider dataProvider : aggregatorEuropeana.getDataProviders()){
                        if(dataProvider.getDataSourceContainers() != null) {
                            for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()) {
                                DataProviderUI currentDataProvider = RepoxDataExchangeManager.parseDataProvider(RepoxServiceImpl.getRepoxManager().getDataManager().
                                        getDataProviderParent(dataSourceContainer.getDataSource().getId()));
                                mainData.add(parseDataSource(dataSourceContainer, currentDataProvider));
                            }
                        }
                    }
                }

            }
            return mainData;
        }catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<FilterAttribute> getDPAttributes(FilterType filterType,List<FilterQuery> filterQueries) throws ServerSideException {
        Map<String,String> countryMap = getFullCountryList();

        List<FilterAttribute> values = new ArrayList<FilterAttribute>();
        List<Object> allDataList;
//        if(filterType == FilterType.DATA_PROVIDER_USER)
            allDataList = FilterManagementUtil.getInstance().getRawFilteredData(filterQueries).getFilteredData();
//        else
//            allDataList = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getAllDataList();


        for(Object object : allDataList){
            if(object instanceof DataProvider){
                DataProvider dataProvider = (DataProvider)object;
                if(filterType.equals(FilterType.COUNTRY)){
                    String showName = "<img src=\"resources/images/countries/" +
                            dataProvider.getCountry() + ".png\" alt=\"" + countryMap.get(dataProvider.getCountry()) + "\" title=\"" +
                            countryMap.get(dataProvider.getCountry()) + "\"/> " + countryMap.get(dataProvider.getCountry());
                    values.add(new FilterAttribute(showName,dataProvider.getCountry()));
                }else if(filterType.equals(FilterType.DP_TYPE)){
                    DefualtDataProvider dataProviderEuropeana = (DefualtDataProvider)dataProvider;
                    values.add(new FilterAttribute(dataProviderEuropeana.getDataSetType().name(),dataProviderEuropeana.getDataSetType().name()));
                }
            }
            else if(object instanceof DataSourceContainer){
                DefualtDataProvider parent = (DefualtDataProvider)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                        getDataProviderParent(((DataSourceContainer) object).getDataSource().getId());
                if(filterType.equals(FilterType.COUNTRY)){
                    String showName = "<img src=\"resources/images/countries/" +
                            parent.getCountry() + ".png\" alt=\"" + countryMap.get(parent.getCountry()) + "\" title=\"" +
                            countryMap.get(parent.getCountry()) + "\"/> " + countryMap.get(parent.getCountry());
                    values.add(new FilterAttribute(showName,parent.getCountry()));
                }else if(filterType.equals(FilterType.DP_TYPE)){
                    values.add(new FilterAttribute(parent.getDataSetType().name(),parent.getDataSetType().name()));
                }
            }
        }
        return values;
    }

    public DataContainer getFilteredData(List<FilterQuery> filterQueries,int offset, int limit)throws ServerSideException{
        try{
            FilteredDataResponse filteredDataResponse = FilterManagementUtil.getInstance().getRawFilteredData(filterQueries);
            List<DataContainer> resultData;
            if(filteredDataResponse.isDataWasFiltered()){
                resultData = getFilteredDataTreeResult(offset, limit, filteredDataResponse.getFilteredData());
                filteredDataSize = filteredDataResponse.getFilteredData().size();
            }else
                resultData = getParsedData(offset,limit);

            DataContainer dataContainer = new DataContainer(UUID.randomUUID().toString());
            for(DataContainer model : resultData)
                dataContainer.add(model);
            return dataContainer;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private List<DataContainer> getFilteredDataTreeResult(int offSet, int limit, List<Object> filteredDataList) throws ServerSideException{
        List<DataContainer> mainData = new ArrayList<DataContainer>();
        try{
            int filteredListSize = filteredDataList.size();
            if(filteredListSize > 0){
                DataProviderUI currentDataProvider = null;
                AggregatorUI currentAggregator = null;
                int realLimit = (limit>filteredListSize ? filteredListSize : limit);
                for (int i = offSet; i<realLimit; i++){
                    if(filteredDataList.get(i) instanceof Aggregator){
                        currentAggregator = parseAggregatorEuropeana((Aggregator) filteredDataList.get(i));
                        mainData.add(currentAggregator);
                    }else if(filteredDataList.get(i) instanceof DataProvider){
                        if(currentAggregator == null || isDifferentAggregator(currentAggregator,(DataProvider)filteredDataList.get(i))){
                            currentAggregator = parseAggregatorEuropeana(((DefaultDataManager) RepoxServiceImpl.
                                    getRepoxManager().getDataManager()).getAggregatorParent(((DataProvider) filteredDataList.get(i)).getId()));
                            mainData.add(currentAggregator);
                            if(limit <= filteredDataList.size())
                                realLimit--;
                        }
                        currentDataProvider = parseDataProviderEuropeana((DataProvider) filteredDataList.get(i), currentAggregator);
                        currentAggregator.add(currentDataProvider);
                        currentAggregator.addDataProvider(currentDataProvider);
                    } else if(filteredDataList.get(i) instanceof DataSourceContainer){
                        if(currentAggregator == null || isDifferentAggregator(currentAggregator,(DataSourceContainer)filteredDataList.get(i))){
                            String parentDataProviderId = RepoxServiceImpl.
                                    getRepoxManager().getDataManager().getDataProviderParent(((DataSourceContainer) filteredDataList.get(i)).getDataSource().getId()).getId();
                            currentAggregator = parseAggregatorEuropeana(((DefaultDataManager) RepoxServiceImpl.
                                    getRepoxManager().getDataManager()).getAggregatorParent(parentDataProviderId));
                            mainData.add(currentAggregator);

                            if(limit <= filteredDataList.size())
                                realLimit--;
                        }
                        if(currentDataProvider == null || isDifferentDataProvider(currentDataProvider,(DataSourceContainer)filteredDataList.get(i))){
                            currentDataProvider = parseDataProviderEuropeana(RepoxServiceImpl.getRepoxManager().getDataManager().
                                    getDataProviderParent(((DataSourceContainer) filteredDataList.get(i)).getDataSource().getId()),
                                    currentAggregator);
                            currentAggregator.add(currentDataProvider);
                            currentAggregator.addDataProvider(currentDataProvider);
                            if(limit <= filteredDataList.size())
                                realLimit--;
                        }
                        DataSourceUI dataSourceUI = parseDataSource((DataSourceContainer) filteredDataList.get(i), currentDataProvider);
                        currentDataProvider.add(dataSourceUI);
                        currentDataProvider.addDataSource(dataSourceUI);
                    }
                }
            }
            return mainData;
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public int getFilteredDataSize(){
        return filteredDataSize;
    }

    private boolean isDifferentAggregator(AggregatorUI aggregatorUI, DataProvider dataProvider) throws ServerSideException{
        if(aggregatorUI == null)
            return false;

        String currentAggregatorID = aggregatorUI.getId();
        String dpAggregatorId = ((DefaultDataManager)RepoxServiceImpl.getRepoxManager().getDataManager()).
                getAggregatorParent(dataProvider.getId()).getId();

        return !currentAggregatorID.equals(dpAggregatorId);
    }

    private boolean isDifferentAggregator(AggregatorUI aggregatorUI, DataSourceContainer dataSourceContainer) throws ServerSideException{
        if(aggregatorUI == null)
            return false;

        String currentAggregatorID = aggregatorUI.getId();
        DataProvider dataProvider = RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(dataSourceContainer.getDataSource().getId());
        String dpAggregatorId = ((DefaultDataManager)RepoxServiceImpl.getRepoxManager().getDataManager()).
                getAggregatorParent(dataProvider.getId()).getId();

        return !currentAggregatorID.equals(dpAggregatorId);
    }

    /*********************************************************
     Save Europeana Functions
     **********************************************************/

    public SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI, int pageSize, String username) throws ServerSideException {
        try{
            return EuropeanaSaveData.saveDataProvider(update, dataProviderUI,pageSize,username);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public SaveDataResponse moveDataProvider(List<DataProviderUI> dataProviders, ModelData aggregatorUI, int pageSize) throws ServerSideException{
        SaveDataResponse saveDataResponse = new SaveDataResponse();
        try {
            DefaultDataManager europeanaManager = (DefaultDataManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager();
            for(DataProviderUI dataProvider : dataProviders) {
                europeanaManager.moveDataProvider((String)aggregatorUI.get("id"),dataProvider.getId());
            }
            // Jump to the page of the FIRST data provider moved on the list
            saveDataResponse.setPage(PagingUtil.getDataPage(dataProviders.get(0).getId(), pageSize));
            saveDataResponse.setResponseState(ResponseState.SUCCESS);
            return saveDataResponse;
        } catch (IOException e) {
            saveDataResponse.setResponseState(ResponseState.ERROR);
            return saveDataResponse;
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String deleteDataProviders(List<DataProviderUI> dataProviderUIs) throws ServerSideException{
        try{
            return EuropeanaSaveData.deleteDataProviders(dataProviderUIs);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public SaveDataResponse saveDataSource(boolean update, DatasetType type, String originalDSset, DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {
        try{
            return EuropeanaSaveData.saveDataSource(update, type, originalDSset, dataSourceUI,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String addAllOAIURL(String url,String dataProviderID,String dsSchema,String dsNamespace,
                               String dsMTDFormat, String name, String nameCode, String exportPath,DataSetOperationsServiceImpl dataSetOperationsService) throws ServerSideException{
        try{
            // Check http URLs
            String checkUrlResult = DataSetOperationsServiceImpl.checkURL(url);
            if(checkUrlResult.equals("URL_MALFORMED"))
                return "URL_MALFORMED";
            else if(checkUrlResult.equals("URL_NOT_EXISTS"))
                return "URL_NOT_EXISTS";

            EuropeanaSaveData.addAllOAIURL(url.trim(),dataProviderID,dsSchema,dsNamespace,dsMTDFormat,dataSetOperationsService.checkOAIURL(url.trim()),
                    name,nameCode,exportPath);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return "SUCCESS";
    }

    public SaveDataResponse moveDataSources(List<DataSourceUI> dataSourceUIs, ModelData dataProviderUI, int pageSize) throws ServerSideException{
        SaveDataResponse saveDataResponse = new SaveDataResponse();
        try {
            DefaultDataManager europeanaManager = (DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager();
            for(DataSourceUI dataSourceUI : dataSourceUIs) {
                europeanaManager.moveDataSource((String)dataProviderUI.get("id"), dataSourceUI.getDataSourceSet());
            }

            // Jump to the page of the FIRST data source moved on the list
            saveDataResponse.setPage(PagingUtil.getDataPage(dataSourceUIs.get(0).getDataSourceSet(), pageSize));
            saveDataResponse.setResponseState(ResponseState.SUCCESS);
            return saveDataResponse;
        } catch (IOException e) {
            saveDataResponse.setResponseState(ResponseState.ERROR);
        } catch (DocumentException e) {
            saveDataResponse.setResponseState(ResponseState.ERROR);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return saveDataResponse;
    }

    public String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException{
        try{
            return EuropeanaSaveData.deleteDataSources(dataSourceUIs);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException{
        try {
            EuropeanaRepoxManager repoxManagerEuropeana = (EuropeanaRepoxManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager();
            DataSourceContainer dataSourceContainer = repoxManagerEuropeana.getDataManager().getDataSourceContainer(dataSourceUI.getDataSourceSet());

            DataSource dataSource = dataSourceContainer.getDataSource();
            dataSource.setExportDir(dataSourceUI.getExportDirectory());

            String recordsPerFile;
            if(dataSourceUI.getRecordsPerFile().equals("All"))
                recordsPerFile = "-1";
            else
                recordsPerFile = dataSourceUI.getRecordsPerFile();

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().
                    startExportDataSource(dataSourceUI.getDataSourceSet(), recordsPerFile, dataSourceUI.getExportFormat());
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        return true;
    }

    public List<OldTaskUI> getParsedOldTasks(List<FilterQuery> filterQueries) throws ServerSideException{
        try{
            List<OldTaskUI> oldTaskUIs = new ArrayList<OldTaskUI>();
            List<Object> allData = FilterManagementUtil.getInstance().getRawFilteredData(filterQueries).getFilteredData();
            for(Object model : allData){
                if(model instanceof DataProvider){
                    for(DataSourceContainer dataSourceContainer : ((DataProvider)model).getDataSourceContainers().values()){
                        for(OldTask oldTask: dataSourceContainer.getDataSource().getOldTasksList()) {
                            OldTaskUI oldTaskUI = new OldTaskUI(dataSourceContainer.getDataSource().getId(),oldTask.getId(),oldTask.getLogName(),
                                    oldTask.getIngestType(),oldTask.getStatus(),oldTask.getRetries(),
                                    oldTask.getRetryMax(),oldTask.getDelay(),oldTask.getDateString(),oldTask.getRecords());
                            oldTaskUIs.add(oldTaskUI);
                        }
                    }
                }
            }
            return oldTaskUIs;
        } catch (Exception e){
            throw  new ServerSideException(Util.stackTraceToString(e));
        }
    }

    private DataSourceUI parseDataSource(DataSourceContainer dataSourceContainer,DataProviderUI dataProviderUI) throws ServerSideException{
        DataSource dataSource = dataSourceContainer.getDataSource();
        new OldTaskReviewer().addNotListedOldTasks(dataSource.getId());
        String oaiSchemas = dataSource.getMetadataFormat();
        if(dataSource.getMetadataTransformations() != null) {
            for(MetadataTransformation metadataTransformation : dataSource.getMetadataTransformations().values()){
                oaiSchemas += " | " + metadataTransformation.getDestinationFormat();
            }
        }

        String recordPolicy;
        if(dataSource.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy)
            recordPolicy = "IdExtracted";
        else
            recordPolicy = "IdGenerated";

        DataSourceUI newDataSourceUI = new DataSourceUI(dataProviderUI,dataSource.getDescription(),
                dataSource.getId(),oaiSchemas,"TODO","",
                dataSource.getDescription(),"","","","",
                recordPolicy,dataSource.getMetadataFormat());

        newDataSourceUI.setSchema(dataSource.getSchema());
        newDataSourceUI.setIsSample(dataSource.isSample());
        newDataSourceUI.setMetadataNamespace(dataSource.getNamespace());

        // External Services Run Type
        if(dataSource.getExternalServicesRunType() != null)
            newDataSourceUI.setExternalServicesRunType(dataSource.getExternalServicesRunType().name());

        DefaultDataSourceContainer dsEurop = (DefaultDataSourceContainer)dataSourceContainer;
        newDataSourceUI.setName(dsEurop.getName());
        newDataSourceUI.setNameCode(dsEurop.getNameCode());

        newDataSourceUI.setType(dataProviderUI.getType());

        newDataSourceUI.setExportDirectory(dataSource.getExportDir() != null ? dataSource.getExportDir().getAbsolutePath() : "");

        String marcFormat = dataSource.getMarcFormat();
        if(marcFormat != null && !marcFormat.isEmpty())
            newDataSourceUI.setMarcFormat(marcFormat);

        RepoxDataExchangeManager.parseDataSourceSubType(newDataSourceUI, dataSource);
        RepoxDataExchangeManager.getOldTasks(dataSource, newDataSourceUI);
        RepoxDataExchangeManager.getScheduledTasks(newDataSourceUI);

        RepoxDataExchangeManager.getMetadataTransformations(dataSource, newDataSourceUI);
        RepoxDataExchangeManager.getDataSetInfo(dataSource, newDataSourceUI);
        RepoxDataExchangeManager.getExternalServices(dataSource, newDataSourceUI);
        RepoxDataExchangeManager.getTags(dataSource, newDataSourceUI);
        return newDataSourceUI;
    }

    public String sendFeedbackEmail(String userEmail, String title, String message, String messageType) throws ServerSideException {
        try {
//            String fromEmail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
            String developTeamMail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
            String[] recipientsEmail = new String[]{developTeamMail};
            String messageTitle = "[" + messageType + "] - " + title + " - Sent by user: " + userEmail;
            File[] attachments = null;
            // Europeana
            HashMap map = new HashMap<String, String>();
            map.put("mailType","sendFeedback");
            map.put("message",message);
            ((DefaultEmailUtil)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient()).
                    sendEmail(developTeamMail, recipientsEmail, messageTitle, "", null, map);

            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ResponseState sendUserDataEmail(String username, String email, String password) throws ServerSideException {
        try {
            // Europeana
            String fromEmail = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
            String subject = "REPOX User Account Data";

            HashMap map = new HashMap<String, String>();
            map.put("user", username);
            map.put("password", password);
            map.put("mailType","userAccount");
            ((DefaultEmailUtil)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient()).
                    sendEmail(fromEmail, new String[] {email}, subject, "", null, map);

            return ResponseState.SUCCESS;
        }catch (AuthenticationFailedException e){
            return ResponseState.EMAIL_AUTHENTICATION_ERROR;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public boolean isCorrectAggregator(String dataSetId, String aggregatorId) throws ServerSideException{
        String parentDataProviderId = RepoxServiceImpl.
                getRepoxManager().getDataManager().getDataProviderParent(dataSetId).getId();
        return ((DefaultDataManager)RepoxServiceImpl.getRepoxManager().getDataManager()).getAggregatorParent(parentDataProviderId).getId().equals(aggregatorId);
    }

    private AggregatorUI parseAggregatorEuropeana(Aggregator aggregatorEuropeana)  throws ServerSideException{
        String url;
        if(aggregatorEuropeana.getHomePage() != null)
            url = aggregatorEuropeana.getHomePage().toString();
        else
            url = "";
        return new AggregatorUI(aggregatorEuropeana.getId(),
                aggregatorEuropeana.getName(),aggregatorEuropeana.getNameCode(),url);
    }

    private DataProviderUI parseDataProviderEuropeana(DataProvider dataProvider, AggregatorUI aggregatorUI)  throws ServerSideException{
        DefualtDataProvider dataProviderEuropeana = (DefualtDataProvider) dataProvider;
        String country;
        if(dataProviderEuropeana.getCountry() != null)
            country = dataProviderEuropeana.getCountry();
        else
            country = "none";

        DataProviderUI newDataProviderUI = new DataProviderUI(dataProviderEuropeana.getId(),dataProviderEuropeana.getName(),
                country, (country != null && !country.equals("")) ? Countries.getCountries().get(country) : "");
        newDataProviderUI.setName(dataProviderEuropeana.getName());
        newDataProviderUI.setNameCode(dataProviderEuropeana.getNameCode());
        newDataProviderUI.setDescription(dataProviderEuropeana.getDescription());
        if(dataProviderEuropeana.getHomePage() != null)
            newDataProviderUI.setHomepage(dataProviderEuropeana.getHomePage().toString());
        newDataProviderUI.setType(dataProviderEuropeana.getDataSetType().name());
        newDataProviderUI.setParentAggregatorID(aggregatorUI.getId());
        return newDataProviderUI;
    }

    public DataSourceUI getDataSetInfo(String dataSetId) throws ServerSideException{
        try{
            DataProvider dataProvider = RepoxServiceImpl.getRepoxManager().getDataManager().
                    getDataProviderParent(dataSetId);
            if(dataProvider == null)
                return null;
            DataProviderUI currentDataProvider = RepoxDataExchangeManager.parseDataProvider(dataProvider);
            DataSourceContainer container = RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSetId);
            if(container == null)
                return null;
            return parseDataSource(container,currentDataProvider);
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<ModelData> getAllAggregators() throws ServerSideException{
        List<ModelData> aggregators = new ArrayList<ModelData>();
        List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
        try{
            for (Object data : allDataList){
                if(data instanceof Aggregator){
                    aggregators.add(createModel(((Aggregator) data).getId(),((Aggregator) data).getName()));
                }
            }
            return aggregators;
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public SaveDataResponse saveAggregator(boolean update, AggregatorUI aggregatorUI, int pageSize) throws ServerSideException{
        SaveDataResponse saveDataResponse = new SaveDataResponse();
        try {
            String homepage = aggregatorUI.getHomepage();

            URL url;
            if(homepage != null && !homepage.isEmpty())
                url = new URL(aggregatorUI.getHomepage());
            else
                url = null;

            // Url doesn't exist
            if(url != null && !FileUtilSecond.checkUrl(aggregatorUI.getHomepage())){
                saveDataResponse.setResponseState(ResponseState.URL_NOT_EXISTS);
                return saveDataResponse;
            }

            if(update) {
                DefaultDataManager europeanaManager = (DefaultDataManager)RepoxServiceImpl.getRepoxManager().getDataManager();

                Aggregator aggregatorEuropeana = europeanaManager.getAggregator(aggregatorUI.getId());
                aggregatorEuropeana = europeanaManager.updateAggregator(aggregatorEuropeana.getId(), aggregatorUI.getName(),
                        aggregatorUI.getNameCode(), aggregatorUI.getHomepage());

                saveDataResponse.setPage(PagingUtil.getDataPage(aggregatorEuropeana.getId(), pageSize));
                saveDataResponse.setResponseState(ResponseState.SUCCESS);
                return saveDataResponse;
            }
            else {
                DefaultDataManager europeanaManager = (DefaultDataManager)RepoxServiceImpl.getRepoxManager().getDataManager();
                Aggregator aggregatorEuropeana = europeanaManager.createAggregator(aggregatorUI.getName(),
                        aggregatorUI.getNameCode(), aggregatorUI.getHomepage());

                saveDataResponse.setPage(PagingUtil.getDataPage(aggregatorEuropeana.getId(),pageSize));
                saveDataResponse.setResponseState(ResponseState.SUCCESS);
                return saveDataResponse;
            }
        } catch (AlreadyExistsException e){
            saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
            return saveDataResponse;
        }catch (MalformedURLException e) {
            saveDataResponse.setResponseState(ResponseState.URL_MALFORMED);
            return saveDataResponse;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String deleteAggregators(List<AggregatorUI> aggregatorUIs) throws ServerSideException{
        try {
            for (AggregatorUI aggregatorUI : aggregatorUIs) {
                DefaultDataManager europeanaManager = (DefaultDataManager)RepoxServiceImpl.getRepoxManager().getDataManager();
                try {
                    europeanaManager.deleteAggregator(aggregatorUI.getId());
//                    System.out.println("Done aggres removed");
                } catch (ObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println("Done aggres removed");
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<BaseSearchResult> getMainGridSearchResults(String searchValue, List<FilterQuery> filterQueries) throws ServerSideException{
        List<BaseSearchResult> searchData = new ArrayList<BaseSearchResult>();
        try{
            List<Object> allDataList = FilterManagementUtil.getInstance().getRawFilteredData(filterQueries).getFilteredData();
            for (Object data : allDataList){
                if(data instanceof Aggregator){
                    String id = ((Aggregator) data).getId();
                    String name = ((Aggregator) data).getName();
                    String nameCode = ((Aggregator) data).getNameCode();
                    if(Util.compareStrings(searchValue,name) || Util.compareStrings(searchValue, nameCode)){
                        EuropeanaSearchResult agg = createModelEuropeana(id, name, nameCode, "", "", DataType.AGGREGATOR);
                        searchData.add(agg);
                    }
                }else if(data instanceof DataProvider){
                    String id = ((DataProvider) data).getId();
                    String name = ((DataProvider) data).getName();
                    String nameCode = ((DefualtDataProvider) data).getNameCode();
                    String description = ((DataProvider) data).getDescription();
                    if(Util.compareStrings(searchValue, description) ||
                            Util.compareStrings(searchValue, name) || Util.compareStrings(searchValue, nameCode)){
                        EuropeanaSearchResult dp = createModelEuropeana(id, name, nameCode, description, "", DataType.DATA_PROVIDER);
                        searchData.add(dp);
                    }
                }else if(data instanceof DataSourceContainer){
                    String id = ((DataSourceContainer) data).getDataSource().getId();
                    String name = ((DefaultDataSourceContainer) data).getName();
                    String nameCode = ((DefaultDataSourceContainer) data).getNameCode();
                    String description = ((DataSourceContainer) data).getDataSource().getDescription();
                    String records = ((DataSourceContainer) data).getDataSource().getNumberRecords()[2];
                    if(Util.compareStrings(searchValue, id) || Util.compareStrings(searchValue, description) ||
                            Util.compareStrings(searchValue, name) || Util.compareStrings(searchValue, nameCode)
                            || Util.compareStrings(searchValue, records)){
                        EuropeanaSearchResult ds = createModelEuropeana(id, name, nameCode, description, id, DataType.DATA_SET);
                        ds.set("records",records);
                        searchData.add(ds);
                    }
                }
            }
            return searchData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public int getDataPage(String id, int pageSize){
        try{
            List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
            int showSize = RepoxServiceImpl.getRepoxManager().getDataManager().getShowSize();
            int extra = 0;
            for(int i = 0; i<showSize+extra; i+=pageSize){
                for(int j = i; j<pageSize+i && j<showSize+extra; j++){
                    String modelId = null;
                    if(allDataList.get(j) instanceof Aggregator){
                        modelId = ((Aggregator) allDataList.get(j)).getId();
                    }else if(allDataList.get(j) instanceof DataProvider){
                        DataProvider dataProvider = ((DataProvider) allDataList.get(j));
                        modelId = dataProvider.getId();
                        if(dataProvider.getDataSourceContainers().values().size() == 1)
                            extra++;
                    } else if(allDataList.get(j) instanceof DataSourceContainer){
                        modelId = ((DataSourceContainer) allDataList.get(j)).getDataSource().getId();
                    }

                    if(modelId != null && modelId.equals(id)){
                        return (i/pageSize)+1;
                    }
                }
            }

        } catch (ServerSideException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return -1;
    }

    private DataSourceContainer getFirstElementOfCollection(Collection<DataSourceContainer> collection){
        for(DataSourceContainer dataSourceContainer : collection){
            return dataSourceContainer;
        }
        return null;
    }
}
