package harvesterUI.server.projects;

import harvesterUI.server.RepoxServiceImpl;
import harvesterUI.server.dataManagement.DataType;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.dataManagement.dataSets.DataSetOperationsServiceImpl;
import harvesterUI.server.dataManagement.filters.FilterManagementUtil;
import harvesterUI.server.dataManagement.filters.FilteredDataResponse;
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
import harvesterUI.shared.search.DefaultBaseSearchResult;
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
import java.util.UUID;

import javax.mail.AuthenticationFailedException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.dom4j.DocumentException;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxConfiguration;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.configuration.DefaultRepoxManager;
import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.dataProvider.Countries;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.rest.statistics.DefaultRepoxStatistics;
import pt.utl.ist.rest.statistics.DefaultStatisticsManager;
import pt.utl.ist.task.OldTask;
import pt.utl.ist.task.OldTaskReviewer;
import pt.utl.ist.util.Country;
import pt.utl.ist.util.DefaultEmailUtil;
import pt.utl.ist.util.FileUtil;
import pt.utl.ist.util.PropertyUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import com.extjs.gxt.ui.client.data.ModelData;

/**
 * @author Edmundo
 * @since Apr 30, 2012
 */
public class DefaultProjectManager extends ProjectManager {

  private int filteredDataSize;

  public DefaultProjectManager() {
    ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
  }

  @Override
  public RepoxStatisticsUI getStatisticsInfo(StatisticsType statisticsType, String username)
      throws ServerSideException {
    try {
      DefaultStatisticsManager manager =
          (DefaultStatisticsManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager()
              .getStatisticsManager();
      User user = UserManagementServiceImpl.getInstance().getUser(username);
      List<String> dpIds;
      if (user instanceof DataProviderUser)
        dpIds = ((DataProviderUser) user).getAllowedDataProviderIds();
      else
        dpIds = null;

      DefaultRepoxStatistics statistics =
          (DefaultRepoxStatistics) manager.generateStatistics(dpIds);

      NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
      String totalRecords = numberFormat.format(statistics.getRecordsTotal());

      int recordsAvgDataSource = (int) statistics.getRecordsAvgDataSource();
      int recordsAvgDataProvider = (int) statistics.getRecordsAvgDataProvider();

      RepoxStatisticsUI repoxStatisticsUI =
          new RepoxStatisticsUI(statistics.getGenerationDate(),
              statistics.getDataSourcesIdExtracted(), statistics.getDataSourcesIdGenerated(),
              statistics.getDataSourcesIdProvided(), statistics.getDataProviders(),
              statistics.getDataSourcesOai() + statistics.getDataSourcesZ3950()
                  + statistics.getDataSourcesDirectoryImporter(), statistics.getDataSourcesOai(),
              statistics.getDataSourcesZ3950(), statistics.getDataSourcesDirectoryImporter(),
              StatisticsUtil.getMetadataFormatStatistics(
                  statistics.getDataSourcesMetadataFormats(), false),
              StatisticsUtil.getMetadataFormatStatistics(
                  statistics.getDataSourcesMetadataFormats(), true), recordsAvgDataSource,
              recordsAvgDataProvider, statistics.getCountriesRecords(), totalRecords);
      repoxStatisticsUI.setAggregators(statistics.getAggregators());
      return repoxStatisticsUI;

    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public Map<String, String> getFullCountryList() throws ServerSideException {
    try {
      boolean useCountriesFile =
          RepoxServiceImpl.getRepoxManager().getConfiguration().getUseCountriesTxt();
      if (useCountriesFile)
        return Countries.getCountries();
      else
        return createDefaultCountriesMap();
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  private Map<String, String> createDefaultCountriesMap() {
    Map<String, String> results = new HashMap<String, String>();

    Country[] countries = Country.values();

    for (int index = 0; index < countries.length; index++) {
      String countryName = countries[index].name();

      Iterator<Map.Entry<String, String>> iterator = Countries.getCountries().entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<String, String> mapEntry = iterator.next();
        if (mapEntry.getValue().equals(countryName)) {
          results.put(mapEntry.getKey(), mapEntry.getValue());
        }
      }
    }
    return results;
  }

  @Override
  public AdminInfo loadAdminFormInfo() throws ServerSideException {
    try {
      DefaultRepoxConfiguration configuration =
          (DefaultRepoxConfiguration) RepoxServiceImpl.getRepoxManager().getConfiguration();
      AdminInfo adminInfo = new AdminInfo();
      adminInfo.set("repositoryFolder", configuration.getRepositoryPath());
      adminInfo.set("configFilesFolder", configuration.getXmlConfigPath());
      adminInfo.set("oaiRequestFolder", configuration.getOaiRequestPath());
      adminInfo.set("baseUrn", configuration.getBaseUrn());

      PropertiesConfigurationLayout propertiesConfigrationLayout =
          PropertyUtil.loadCorrectedConfiguration("oaicat.properties");
      PropertiesConfiguration properties = propertiesConfigrationLayout.getConfiguration();
      adminInfo.set("oaiRepoName",
          properties.getProperty("Identify.repositoryName") == null ? "undefined" : properties
              .getProperty("Identify.repositoryName").toString());
      adminInfo.set("oaiMaxList",
          properties.getProperty("DataSourceOAICatalog.maxListSize") == null ? "undefined"
              : properties.getProperty("DataSourceOAICatalog.maxListSize").toString());

      adminInfo.set("defaultExportFolder", configuration.getExportDefaultFolder());
      adminInfo.set("adminEmail", configuration.getAdministratorEmail());
      adminInfo.set("smtpServer", configuration.getSmtpServer());
      adminInfo.set("smtpPort", configuration.getSmtpPort());
      adminInfo.set("repoxDefaultEmailSender", configuration.getDefaultEmail());
      adminInfo.set("httpRequestFolder", configuration.getHttpRequestPath());
      adminInfo.set("ftpRequestFolder", configuration.getFtpRequestPath());
      adminInfo.set("sampleRecords", configuration.getSampleRecords());
      adminInfo.set("useCountriesTxt", configuration.getUseCountriesTxt());
      adminInfo.set("sendEmailAfterIngest", configuration.getSendEmailAfterIngest());
      // adminInfo.set("useMailSSLAuthentication",configuration.isUseMailSSLAuthentication());
      adminInfo.set("useOAINamespace", configuration.isUseOAINamespace());

      // optional fields
      if (configuration.getLdapHost() != null)
        adminInfo.set("ldapHost", configuration.getLdapHost());
      if (configuration.getLdapRootDN() != null)
        adminInfo.set("ldapRootDN", configuration.getLdapRootDN());
      if (configuration.getLdapRootPassword() != null)
        adminInfo.set("ldapRootPassword", configuration.getLdapRootPassword());
      if (configuration.getLdapBasePath() != null)
        adminInfo.set("ldapBasePath", configuration.getLdapBasePath());
      if (configuration.getRepoxDefaultEmailPass() != null)
        adminInfo.set("repoxDefaultEmailPass", configuration.getRepoxDefaultEmailPass());

      return adminInfo;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public void saveAdminFormInfo(AdminInfo results) throws ServerSideException {
    try {

      PropertiesConfigurationLayout propertiesConfigrationLayout =
          PropertyUtil.loadCorrectedConfiguration(DefaultRepoxContextUtil.CONFIG_FILE);
      PropertiesConfiguration properties = propertiesConfigrationLayout.getConfiguration();
      properties.setProperty("repository.dir", (String) results.get("repositoryFolder"));
      properties.setProperty("xmlConfig.dir", (String) results.get("configFilesFolder"));
      properties.setProperty("oairequests.dir", (String) results.get("oaiRequestFolder"));
      properties.setProperty("baseurn", (String) results.get("baseUrn"));
      properties.setProperty("exportDefaultFolder", (String) results.get("defaultExportFolder"));
      properties.setProperty("administrator.email", (String) results.get("adminEmail"));
      properties.setProperty("smtp.server", (String) results.get("smtpServer"));
      properties.setProperty("smtp.port", (String) results.get("smtpPort"));
      properties.setProperty("default.email", (String) results.get("repoxDefaultEmailSender"));
      properties.setProperty("httprequests.dir", (String) results.get("httpRequestFolder"));
      properties.setProperty("ftprequests.dir", (String) results.get("ftpRequestFolder"));
      properties.setProperty("sample.records", (String) results.get("sampleRecords"));
      properties
          .setProperty("userCountriesTxtFile", String.valueOf(results.get("useCountriesTxt")));
      properties.setProperty("sendEmailAfterIngest",
          String.valueOf(results.get("sendEmailAfterIngest")));
      // properties.setProperty("useMailSSLAuthentication",String.valueOf(results.get("useMailSSLAuthentication")));
      properties.setProperty("useOAINamespace", String.valueOf(results.get("useOAINamespace")));

      // optional fields
      if (results.get("repoxDefaultEmailPass") != null)
        properties.setProperty("default.email.pass", (String) results.get("repoxDefaultEmailPass"));
      if (results.get("ldapHost") != null)
        properties.setProperty("ldapHost", (String) results.get("ldapHost"));
      if (results.get("ldapRootDN") != null)
        properties.setProperty("ldapRootDN", ((String) results.get("ldapRootDN")).replace(",", "\\,"));
      if (results.get("ldapRootPassword") != null)
        properties.setProperty("ldapRootPassword", (String) results.get("ldapRootPassword"));
      if (results.get("ldapBasePath") != null)
        properties.setProperty("ldapBasePath", ((String) results.get("ldapBasePath")).replace(",", "\\,"));

      PropertiesConfigurationLayout oaiPropertiesConfigrationLayout =
          PropertyUtil.loadCorrectedConfiguration("oaicat.properties");
      PropertiesConfiguration oaiConfiguration = oaiPropertiesConfigrationLayout.getConfiguration();
      if (results.get("oaiRepoName") != null)
        oaiConfiguration
            .setProperty("Identify.repositoryName", (String) results.get("oaiRepoName"));
      if (results.get("oaiMaxList") != null)
        oaiConfiguration.setProperty("DataSourceOAICatalog.maxListSize",
            (String) results.get("oaiMaxList"));

      PropertyUtil.saveProperties(oaiPropertiesConfigrationLayout, "oaicat.properties");
      reloadOAIProperties(results.getReloadOAIPropertiesUrl());
      PropertyUtil
          .saveProperties(propertiesConfigrationLayout, DefaultRepoxContextUtil.CONFIG_FILE);
      ConfigSingleton.getRepoxContextUtil().reloadProperties();
      // System.out.println("Done save admin");
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }


  /*********************************************************
   * Paging Europeana Functions
   **********************************************************/

  @Override
  public List<DataContainer> getParsedData(int offSet, int limit) throws ServerSideException {
    List<DataContainer> mainData = new ArrayList<DataContainer>();
    try {
      List<Object> allDataList =
          RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
      int realLimit = allDataList.size();
      if (realLimit > 0) {
        DataProviderUI currentDataProvider = null;
        AggregatorUI currentAggregator = null;
        for (int i = offSet; i < (limit > realLimit ? realLimit : limit); i++) {
          if (allDataList.get(i) instanceof Aggregator) {
            currentAggregator = parseAggregator((Aggregator) allDataList.get(i));
            mainData.add(currentAggregator);
          } else if (allDataList.get(i) instanceof DataProvider) {
            if (currentAggregator == null) {
              currentAggregator =
                  parseAggregator(((DefaultDataManager) RepoxServiceImpl.getRepoxManager()
                      .getDataManager()).getAggregatorParent(((DataProvider) allDataList.get(i))
                      .getId()));
              mainData.add(currentAggregator);
            }
            currentDataProvider =
                parseDataProvider((DataProvider) allDataList.get(i), currentAggregator);
            currentAggregator.add(currentDataProvider);
            currentAggregator.addDataProvider(currentDataProvider);
          } else if (allDataList.get(i) instanceof DataSourceContainer) {
            if (currentAggregator == null) {
              String parentDataProviderId =
                  RepoxServiceImpl
                      .getRepoxManager()
                      .getDataManager()
                      .getDataProviderParent(
                          ((DataSourceContainer) allDataList.get(i)).getDataSource().getId())
                      .getId();
              currentAggregator =
                  parseAggregator(((DefaultDataManager) RepoxServiceImpl.getRepoxManager()
                      .getDataManager()).getAggregatorParent(parentDataProviderId));
              mainData.add(currentAggregator);
            }
            if (currentDataProvider == null) {
              currentDataProvider =
                  parseDataProvider(
                      RepoxServiceImpl
                          .getRepoxManager()
                          .getDataManager()
                          .getDataProviderParent(
                              ((DataSourceContainer) allDataList.get(i)).getDataSource().getId()),
                      currentAggregator);
              currentAggregator.add(currentDataProvider);
              currentAggregator.addDataProvider(currentDataProvider);
            }

            DataSourceUI dataSourceUI =
                parseDataSource((DataSourceContainer) allDataList.get(i), currentDataProvider);
            currentDataProvider.add(dataSourceUI);
            currentDataProvider.addDataSource(dataSourceUI);
          }
        }
      }

      return mainData;
    } catch (IndexOutOfBoundsException e) {
      // return mainData;
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public DataContainer getSearchResult(ModelData data) throws ServerSideException {
    try {
      DataContainer dataContainer = new DataContainer(UUID.randomUUID().toString());
      String id = data.get("id");

      if (data.get("dataType").equals(DataType.AGGREGATOR.name())) {
        DefaultDataManager dataManagerEuropeana =
            (DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager();
        Aggregator aggregator = dataManagerEuropeana.getAggregator(id);
        AggregatorUI aggregatorUI = parseAggregator(aggregator);
        dataContainer.add(aggregatorUI);
        for (DataProvider dataProvider : aggregator.getDataProviders()) {
          DataProviderUI dataProviderUI = parseDataProvider(dataProvider, aggregatorUI);
          aggregatorUI.add(dataProviderUI);
          aggregatorUI.addDataProvider(dataProviderUI);

          for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers()
              .values()) {
            DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer, dataProviderUI);
            dataProviderUI.add(dataSourceUI);
            dataProviderUI.addDataSource(dataSourceUI);
          }
        }
      } else if (data.get("dataType").equals(DataType.DATA_PROVIDER.name())) {
        DataProvider dataProvider =
            RepoxServiceImpl.getRepoxManager().getDataManager().getDataProvider(id);
        AggregatorUI aggregatorUI =
            parseAggregator(((DefaultDataManager) RepoxServiceImpl.getRepoxManager()
                .getDataManager()).getAggregatorParent(dataProvider.getId()));
        dataContainer.add(aggregatorUI);
        DataProviderUI dataProviderUI = parseDataProvider(dataProvider, aggregatorUI);
        aggregatorUI.add(dataProviderUI);
        aggregatorUI.addDataProvider(dataProviderUI);

        for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers()
            .values()) {
          DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer, dataProviderUI);
          dataProviderUI.add(dataSourceUI);
          dataProviderUI.addDataSource(dataSourceUI);
        }
      } else if (data.get("dataType").equals(DataType.DATA_SET.name())) {
        String parentDataProviderId =
            RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(id).getId();
        AggregatorUI aggregatorUI =
            parseAggregator(((DefaultDataManager) RepoxServiceImpl.getRepoxManager()
                .getDataManager()).getAggregatorParent(parentDataProviderId));
        dataContainer.add(aggregatorUI);
        DataProviderUI dataProviderUI =
            parseDataProvider(RepoxServiceImpl.getRepoxManager().getDataManager()
                .getDataProviderParent(id), aggregatorUI);
        aggregatorUI.add(dataProviderUI);
        aggregatorUI.addDataProvider(dataProviderUI);

        DataSourceContainer dataSourceContainer =
            RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(id);
        DataSourceUI dataSourceUI = parseDataSource(dataSourceContainer, dataProviderUI);
        dataProviderUI.add(dataSourceUI);
        dataProviderUI.addDataSource(dataSourceUI);
      }
      return dataContainer;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public List<DataContainer> getViewResult(int offset, int limit, String type)
      throws ServerSideException {
    List<DataContainer> mainData = new ArrayList<DataContainer>();
    try {
      if (type.equals("AGGREAGATORS")) {
        DefaultDataManager dataManagerEuropeana =
            (DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager();
        List<Aggregator> aggregatorEuropeanaList = dataManagerEuropeana.getAggregators();
        for (int i = offset; i < limit && i < aggregatorEuropeanaList.size(); i++) {
          mainData.add(parseAggregator(aggregatorEuropeanaList.get(i)));
        }
      } else if (type.equals("DATA_PROVIDERS")) {
        List<DataProvider> dpList =
            RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviders();
        for (int i = offset; i < limit && i < dpList.size(); i++) {
          AggregatorUI currentAggregator =
              parseAggregator(((DefaultDataManager) RepoxServiceImpl.getRepoxManager()
                  .getDataManager()).getAggregatorParent(dpList.get(i).getId()));
          mainData.add(parseDataProvider(dpList.get(i), currentAggregator));
        }
      } else if (type.equals("DATA_SETS")) {
        DefaultDataManager dataManagerEuropeana =
            (DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager();
        List<Aggregator> aggregatorEuropeanaList = dataManagerEuropeana.getAggregators();
        for (Aggregator aggregatorEuropeana : aggregatorEuropeanaList) {
          for (DataProvider dataProvider : aggregatorEuropeana.getDataProviders()) {
            if (dataProvider.getDataSourceContainers() != null) {
              for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers()
                  .values()) {
                DataProviderUI currentDataProvider =
                    RepoxDataExchangeManager.parseDataProvider(RepoxServiceImpl.getRepoxManager()
                        .getDataManager()
                        .getDataProviderParent(dataSourceContainer.getDataSource().getId()));
                mainData.add(parseDataSource(dataSourceContainer, currentDataProvider));
              }
            }
          }
        }

      }
      return mainData;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public List<FilterAttribute> getDPAttributes(FilterType filterType,
      List<FilterQuery> filterQueries) throws ServerSideException {
    Map<String, String> countryMap = getFullCountryList();

    List<FilterAttribute> values = new ArrayList<FilterAttribute>();
    List<Object> allDataList;
    // if(filterType == FilterType.DATA_PROVIDER_USER)
    allDataList =
        FilterManagementUtil.getInstance().getRawFilteredData(filterQueries).getFilteredData();
    // else
    // allDataList =
    // ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getAllDataList();


    for (Object object : allDataList) {
      if (object instanceof DataProvider) {
        DataProvider dataProvider = (DataProvider) object;
        if (filterType.equals(FilterType.COUNTRY)) {
          String showName =
              "<img src=\"resources/images/countries/" + dataProvider.getCountryCode()
                  + ".png\" alt=\"" + countryMap.get(dataProvider.getCountryCode()) + "\" title=\""
                  + countryMap.get(dataProvider.getCountryCode()) + "\"/> "
                  + countryMap.get(dataProvider.getCountryCode());
          values.add(new FilterAttribute(showName, dataProvider.getCountryCode()));
        } else if (filterType.equals(FilterType.DP_TYPE)) {
          DataProvider dataProviderEuropeana = dataProvider;
          values.add(new FilterAttribute(dataProviderEuropeana.getProviderType().name(),
              dataProviderEuropeana.getProviderType().name()));
        }
      } else if (object instanceof DataSourceContainer) {
        DataProvider parent =
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()
                .getDataProviderParent(((DataSourceContainer) object).getDataSource().getId());
        if (filterType.equals(FilterType.COUNTRY)) {
          String showName =
              "<img src=\"resources/images/countries/" + parent.getCountryCode() + ".png\" alt=\""
                  + countryMap.get(parent.getCountryCode()) + "\" title=\""
                  + countryMap.get(parent.getCountryCode()) + "\"/> "
                  + countryMap.get(parent.getCountryCode());
          values.add(new FilterAttribute(showName, parent.getCountryCode()));
        } else if (filterType.equals(FilterType.DP_TYPE)) {
          values.add(new FilterAttribute(parent.getProviderType().name(), parent.getProviderType()
              .name()));
        }
      }
    }
    return values;
  }

  @Override
  public DataContainer getFilteredData(List<FilterQuery> filterQueries, int offset, int limit)
      throws ServerSideException {
    try {
      FilteredDataResponse filteredDataResponse =
          FilterManagementUtil.getInstance().getRawFilteredData(filterQueries);
      List<DataContainer> resultData;
      if (filteredDataResponse.isDataWasFiltered()) {
        resultData =
            getFilteredDataTreeResult(offset, limit, filteredDataResponse.getFilteredData());
        filteredDataSize = filteredDataResponse.getFilteredData().size();
      } else
        resultData = getParsedData(offset, limit);

      DataContainer dataContainer = new DataContainer(UUID.randomUUID().toString());
      for (DataContainer model : resultData)
        dataContainer.add(model);
      return dataContainer;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  private List<DataContainer> getFilteredDataTreeResult(int offSet, int limit,
      List<Object> filteredDataList) throws ServerSideException {
    List<DataContainer> mainData = new ArrayList<DataContainer>();
    try {
      int filteredListSize = filteredDataList.size();
      if (filteredListSize > 0) {
        DataProviderUI currentDataProvider = null;
        AggregatorUI currentAggregator = null;
        int realLimit = (limit > filteredListSize ? filteredListSize : limit);
        for (int i = offSet; i < realLimit; i++) {
          if (filteredDataList.get(i) instanceof Aggregator) {
            currentAggregator = parseAggregator((Aggregator) filteredDataList.get(i));
            mainData.add(currentAggregator);
          } else if (filteredDataList.get(i) instanceof DataProvider) {
            if (currentAggregator == null
                || isDifferentAggregator(currentAggregator, (DataProvider) filteredDataList.get(i))) {
              currentAggregator =
                  parseAggregator(((DefaultDataManager) RepoxServiceImpl.getRepoxManager()
                      .getDataManager()).getAggregatorParent(((DataProvider) filteredDataList
                      .get(i)).getId()));
              mainData.add(currentAggregator);
              if (limit <= filteredDataList.size())
                realLimit--;
            }
            currentDataProvider =
                parseDataProvider((DataProvider) filteredDataList.get(i), currentAggregator);
            currentAggregator.add(currentDataProvider);
            currentAggregator.addDataProvider(currentDataProvider);
          } else if (filteredDataList.get(i) instanceof DataSourceContainer) {
            if (currentAggregator == null
                || isDifferentAggregator(currentAggregator,
                    (DataSourceContainer) filteredDataList.get(i))) {
              String parentDataProviderId =
                  RepoxServiceImpl
                      .getRepoxManager()
                      .getDataManager()
                      .getDataProviderParent(
                          ((DataSourceContainer) filteredDataList.get(i)).getDataSource().getId())
                      .getId();
              currentAggregator =
                  parseAggregator(((DefaultDataManager) RepoxServiceImpl.getRepoxManager()
                      .getDataManager()).getAggregatorParent(parentDataProviderId));
              mainData.add(currentAggregator);

              if (limit <= filteredDataList.size())
                realLimit--;
            }
            if (currentDataProvider == null
                || isDifferentDataProvider(currentDataProvider,
                    (DataSourceContainer) filteredDataList.get(i))) {
              currentDataProvider =
                  parseDataProvider(
                      RepoxServiceImpl
                          .getRepoxManager()
                          .getDataManager()
                          .getDataProviderParent(
                              ((DataSourceContainer) filteredDataList.get(i)).getDataSource()
                                  .getId()), currentAggregator);
              currentAggregator.add(currentDataProvider);
              currentAggregator.addDataProvider(currentDataProvider);
              if (limit <= filteredDataList.size())
                realLimit--;
            }
            DataSourceUI dataSourceUI =
                parseDataSource((DataSourceContainer) filteredDataList.get(i), currentDataProvider);
            currentDataProvider.add(dataSourceUI);
            currentDataProvider.addDataSource(dataSourceUI);
          }
        }
      }
      return mainData;
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public int getFilteredDataSize() {
    return filteredDataSize;
  }

  private boolean isDifferentAggregator(AggregatorUI aggregatorUI, DataProvider dataProvider)
      throws ServerSideException {
    if (aggregatorUI == null)
      return false;

    String currentAggregatorID = aggregatorUI.getId();
    String dpAggregatorId =
        ((DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager())
            .getAggregatorParent(dataProvider.getId()).getId();

    return !currentAggregatorID.equals(dpAggregatorId);
  }

  private boolean isDifferentAggregator(AggregatorUI aggregatorUI,
      DataSourceContainer dataSourceContainer) throws ServerSideException {
    if (aggregatorUI == null)
      return false;

    String currentAggregatorID = aggregatorUI.getId();
    DataProvider dataProvider =
        RepoxServiceImpl.getRepoxManager().getDataManager()
            .getDataProviderParent(dataSourceContainer.getDataSource().getId());
    String dpAggregatorId =
        ((DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager())
            .getAggregatorParent(dataProvider.getId()).getId();

    return !currentAggregatorID.equals(dpAggregatorId);
  }

  /*********************************************************
   * Save Europeana Functions
   **********************************************************/

  @Override
  public SaveDataResponse saveDataProvider(boolean update, DataProviderUI dataProviderUI,
      int pageSize, String username) throws ServerSideException {
    try {
      return DefaultSaveData.saveDataProvider(update, dataProviderUI, pageSize, username);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public SaveDataResponse moveDataProvider(List<DataProviderUI> dataProviders,
      ModelData aggregatorUI, int pageSize) throws ServerSideException {
    SaveDataResponse saveDataResponse = new SaveDataResponse();
    try {
      DefaultDataManager europeanaManager =
          (DefaultDataManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager()
              .getDataManager();
      for (DataProviderUI dataProvider : dataProviders) {
        europeanaManager.moveDataProvider((String) aggregatorUI.get("id"), dataProvider.getId());
      }
      // Jump to the page of the FIRST data provider moved on the list
      saveDataResponse.setPage(PagingUtil.getDataPage(dataProviders.get(0).getId(), pageSize));
      saveDataResponse.setResponseState(ResponseState.SUCCESS);
      return saveDataResponse;
    } catch (IOException e) {
      saveDataResponse.setResponseState(ResponseState.ERROR);
      return saveDataResponse;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public String deleteDataProviders(List<DataProviderUI> dataProviderUIs)
      throws ServerSideException {
    try {
      return DefaultSaveData.deleteDataProviders(dataProviderUIs);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public SaveDataResponse saveDataSource(boolean update, DatasetType type, String originalDSset,
      DataSourceUI dataSourceUI, int pageSize) throws ServerSideException {
    try {
      return DefaultSaveData.saveDataSource(update, type, originalDSset, dataSourceUI, pageSize);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public String addAllOAIURL(String url, String dataProviderID, String dsSchema,
      String dsNamespace, String dsMTDFormat, String name, String nameCode, String exportPath,
      DataSetOperationsServiceImpl dataSetOperationsService) throws ServerSideException {
    try {
      // Check http URLs
      String checkUrlResult = DataSetOperationsServiceImpl.checkURL(url);
      if (checkUrlResult.equals("URL_MALFORMED"))
        return "URL_MALFORMED";
      else if (checkUrlResult.equals("URL_NOT_EXISTS"))
        return "URL_NOT_EXISTS";

      DefaultSaveData.addAllOAIURL(url.trim(), dataProviderID, dsSchema, dsNamespace, dsMTDFormat,
          dataSetOperationsService.checkOAIURL(url.trim()), name, nameCode, exportPath);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
    return "SUCCESS";
  }

  @Override
  public SaveDataResponse moveDataSources(List<DataSourceUI> dataSourceUIs,
      ModelData dataProviderUI, int pageSize) throws ServerSideException {
    SaveDataResponse saveDataResponse = new SaveDataResponse();
    try {
      DefaultDataManager europeanaManager =
          (DefaultDataManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager()
              .getDataManager();
      for (DataSourceUI dataSourceUI : dataSourceUIs) {
        europeanaManager.moveDataSource((String) dataProviderUI.get("id"),
            dataSourceUI.getDataSourceSet());
      }

      // Jump to the page of the FIRST data source moved on the list
      saveDataResponse.setPage(PagingUtil.getDataPage(dataSourceUIs.get(0).getDataSourceSet(),
          pageSize));
      saveDataResponse.setResponseState(ResponseState.SUCCESS);
      return saveDataResponse;
    } catch (IOException e) {
      saveDataResponse.setResponseState(ResponseState.ERROR);
    } catch (DocumentException e) {
      saveDataResponse.setResponseState(ResponseState.ERROR);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
    return saveDataResponse;
  }

  @Override
  public String deleteDataSources(List<DataSourceUI> dataSourceUIs) throws ServerSideException {
    try {
      return DefaultSaveData.deleteDataSources(dataSourceUIs);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public Boolean dataSourceExport(DataSourceUI dataSourceUI) throws ServerSideException {
    try {
      DefaultRepoxManager repoxManagerEuropeana =
          (DefaultRepoxManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager();
      DataSourceContainer dataSourceContainer =
          repoxManagerEuropeana.getDataManager().getDataSourceContainer(
              dataSourceUI.getDataSourceSet());

      DataSource dataSource = dataSourceContainer.getDataSource();
      dataSource.setExportDir(dataSourceUI.getExportDirectory());

      String recordsPerFile;
      if (dataSourceUI.getRecordsPerFile().equals("All"))
        recordsPerFile = "-1";
      else
        recordsPerFile = dataSourceUI.getRecordsPerFile();

      ConfigSingleton
          .getRepoxContextUtil()
          .getRepoxManager()
          .getDataManager()
          .startExportDataSource(dataSourceUI.getDataSourceSet(), recordsPerFile,
              dataSourceUI.getExportFormat());
    } catch (Exception e) {
      throw new ServerSideException(Util.stackTraceToString(e));
    }
    return true;
  }

  @Override
  public List<OldTaskUI> getParsedOldTasks(List<FilterQuery> filterQueries)
      throws ServerSideException {
    try {
      List<OldTaskUI> oldTaskUIs = new ArrayList<OldTaskUI>();
      List<Object> allData =
          FilterManagementUtil.getInstance().getRawFilteredData(filterQueries).getFilteredData();
      for (Object model : allData) {
        if (model instanceof DataProvider) {
          for (DataSourceContainer dataSourceContainer : ((DataProvider) model)
              .getDataSourceContainers().values()) {
            for (OldTask oldTask : dataSourceContainer.getDataSource().getOldTasksList()) {
              OldTaskUI oldTaskUI =
                  new OldTaskUI(dataSourceContainer.getDataSource().getId(), oldTask.getId(),
                      oldTask.getLogName(), oldTask.getIngestType(), oldTask.getStatus(),
                      oldTask.getRetries(), oldTask.getRetryMax(), oldTask.getDelay(),
                      oldTask.getDateString(), oldTask.getRecords());
              oldTaskUIs.add(oldTaskUI);
            }
          }
        }
      }
      return oldTaskUIs;
    } catch (Exception e) {
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  private DataSourceUI parseDataSource(DataSourceContainer dataSourceContainer,
      DataProviderUI dataProviderUI) throws ServerSideException {
    DataSource dataSource = dataSourceContainer.getDataSource();
    new OldTaskReviewer().addNotListedOldTasks(dataSource.getId());
    String oaiSchemas = dataSource.getMetadataFormat();
    if (dataSource.getMetadataTransformations() != null) {
      for (MetadataTransformation metadataTransformation : dataSource.getMetadataTransformations()
          .values()) {
        oaiSchemas += " | " + metadataTransformation.getDestinationSchemaId();
      }
    }

    String recordPolicy;
    if (dataSource.getRecordIdPolicy() instanceof IdExtractedRecordIdPolicy)
      recordPolicy = IdExtractedRecordIdPolicy.IDEXTRACTED;
    else
      recordPolicy = IdGeneratedRecordIdPolicy.IDGENERATED;

    DataSourceUI newDataSourceUI =
        new DataSourceUI(dataProviderUI, dataSource.getDescription(), dataSource.getId(),
            oaiSchemas, "TODO", "", dataSource.getDescription(), "", "", "", "", recordPolicy,
            dataSource.getMetadataFormat());

    newDataSourceUI.setSchema(dataSource.getSchema());
    newDataSourceUI.setIsSample(dataSource.isSample());
    newDataSourceUI.setMetadataNamespace(dataSource.getNamespace());

    // External Services Run Type
    if (dataSource.getExternalServicesRunType() != null)
      newDataSourceUI.setExternalServicesRunType(dataSource.getExternalServicesRunType().name());

    DefaultDataSourceContainer dsEurop = (DefaultDataSourceContainer) dataSourceContainer;
    newDataSourceUI.setName(dsEurop.getName());
    newDataSourceUI.setNameCode(dsEurop.getNameCode());

    newDataSourceUI.setType(dataProviderUI.getType());

    newDataSourceUI.setExportDirectory(dataSource.getExportDir() != null ? dataSource
        .getExportDir() : "");

    String marcFormat = dataSource.getMarcFormat();
    if (marcFormat != null && !marcFormat.isEmpty())
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

  @Override
  public String sendFeedbackEmail(String userEmail, String title, String message, String messageType)
      throws ServerSideException {
    try {
      // String fromEmail =
      // ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getDefaultEmail();
      String developTeamMail =
          ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()
              .getDefaultEmail();
      String[] recipientsEmail = new String[] {developTeamMail};
      String messageTitle = "[" + messageType + "] - " + title + " - Sent by user: " + userEmail;
      File[] attachments = null;
      // Europeana
      HashMap map = new HashMap<String, String>();
      map.put("mailType", "sendFeedback");
      map.put("message", message);
      ((DefaultEmailUtil) ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient())
          .sendEmail(developTeamMail, recipientsEmail, messageTitle, "", null, map);

      return "SUCCESS";
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public ResponseState sendUserDataEmail(String username, String email, String password)
      throws ServerSideException {
    try {
      String fromEmail =
          ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration()
              .getDefaultEmail();
      String subject = "REPOX User Account Data";

      HashMap map = new HashMap<String, String>();
      map.put("user", username);
      map.put("password", password);
      map.put("mailType", "userAccount");
      ((DefaultEmailUtil) ConfigSingleton.getRepoxContextUtil().getRepoxManager().getEmailClient())
          .sendEmail(fromEmail, new String[] {email}, subject, "", null, map);

      return ResponseState.SUCCESS;
    } catch (AuthenticationFailedException e) {
      return ResponseState.EMAIL_AUTHENTICATION_ERROR;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public boolean isCorrectAggregator(String dataSetId, String aggregatorId)
      throws ServerSideException {
    String parentDataProviderId =
        RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(dataSetId)
            .getId();
    return ((DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager())
        .getAggregatorParent(parentDataProviderId).getId().equals(aggregatorId);
  }

  private AggregatorUI parseAggregator(Aggregator aggregatorEuropeana) throws ServerSideException {
    String url;
    if (aggregatorEuropeana.getHomepage() != null)
      url = aggregatorEuropeana.getHomepage().toString();
    else
      url = "";
    return new AggregatorUI(aggregatorEuropeana.getId(), aggregatorEuropeana.getName(),
        aggregatorEuropeana.getNameCode(), url);
  }

  private DataProviderUI parseDataProvider(DataProvider dataProvider, AggregatorUI aggregatorUI)
      throws ServerSideException {
    DataProvider provider = dataProvider;
    String country;
    if (provider.getCountryCode() != null)
      country = provider.getCountryCode();
    else
      country = "none";

    DataProviderUI newDataProviderUI =
        new DataProviderUI(provider.getId(), provider.getName(), country,
            (country != null && !country.equals("")) ? Countries.getCountries().get(country) : "");
    newDataProviderUI.setName(provider.getName());
    newDataProviderUI.setNameCode(provider.getNameCode());
    newDataProviderUI.setDescription(provider.getDescription());
    if (provider.getHomepage() != null)
      newDataProviderUI.setHomepage(provider.getHomepage().toString());
    newDataProviderUI.setType(provider.getProviderType().name());
    newDataProviderUI.setParentAggregatorID(aggregatorUI.getId());
    return newDataProviderUI;
  }

  @Override
  public DataSourceUI getDataSetInfo(String dataSetId) throws ServerSideException {
    try {
      DataProvider dataProvider =
          RepoxServiceImpl.getRepoxManager().getDataManager().getDataProviderParent(dataSetId);
      if (dataProvider == null)
        return null;
      DataProviderUI currentDataProvider = RepoxDataExchangeManager.parseDataProvider(dataProvider);
      DataSourceContainer container =
          RepoxServiceImpl.getRepoxManager().getDataManager().getDataSourceContainer(dataSetId);
      if (container == null)
        return null;
      return parseDataSource(container, currentDataProvider);
    } catch (Exception e) {
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public List<ModelData> getAllAggregators() throws ServerSideException {
    List<ModelData> aggregators = new ArrayList<ModelData>();
    List<Object> allDataList = RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
    try {
      for (Object data : allDataList) {
        if (data instanceof Aggregator) {
          aggregators.add(createModel(((Aggregator) data).getId(), ((Aggregator) data).getName()));
        }
      }
      return aggregators;
    } catch (Exception e) {
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public SaveDataResponse saveAggregator(boolean update, AggregatorUI aggregatorUI, int pageSize)
      throws ServerSideException {
    SaveDataResponse saveDataResponse = new SaveDataResponse();
    try {
      String homepage = aggregatorUI.getHomepage();

      URL url;
      if (homepage != null && !homepage.isEmpty())
        url = new URL(aggregatorUI.getHomepage());
      else
        url = null;

      // Url doesn't exist
      if (url != null && !FileUtil.checkUrl(aggregatorUI.getHomepage())) {
        saveDataResponse.setResponseState(ResponseState.URL_NOT_EXISTS);
        return saveDataResponse;
      }

      if (update) {
        DefaultDataManager defaultDataManager =
            (DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager();

        Aggregator aggregator = defaultDataManager.getAggregator(aggregatorUI.getId());
        aggregator =
            defaultDataManager.updateAggregator(aggregator.getId(), null, aggregatorUI.getName(),
                aggregatorUI.getNameCode(), aggregatorUI.getHomepage());

        saveDataResponse.setPage(PagingUtil.getDataPage(aggregator.getId(), pageSize));
        saveDataResponse.setResponseState(ResponseState.SUCCESS);
        return saveDataResponse;
      } else {
        DefaultDataManager europeanaManager =
            (DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager();
        Aggregator aggregatorEuropeana =
            europeanaManager.createAggregator(null, aggregatorUI.getName(),
                aggregatorUI.getNameCode(), aggregatorUI.getHomepage());

        saveDataResponse.setPage(PagingUtil.getDataPage(aggregatorEuropeana.getId(), pageSize));
        saveDataResponse.setResponseState(ResponseState.SUCCESS);
        return saveDataResponse;
      }
    } catch (AlreadyExistsException e) {
      saveDataResponse.setResponseState(ResponseState.ALREADY_EXISTS);
      return saveDataResponse;
    } catch (MalformedURLException e) {
      saveDataResponse.setResponseState(ResponseState.URL_MALFORMED);
      return saveDataResponse;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public String deleteAggregators(List<AggregatorUI> aggregatorUIs) throws ServerSideException {
    try {
      for (AggregatorUI aggregatorUI : aggregatorUIs) {
        DefaultDataManager europeanaManager =
            (DefaultDataManager) RepoxServiceImpl.getRepoxManager().getDataManager();
        try {
          europeanaManager.deleteAggregator(aggregatorUI.getId());
          // System.out.println("Done aggres removed");
        } catch (ObjectNotFoundException e) {
          e.printStackTrace();
        }
      }
      // System.out.println("Done aggres removed");
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerSideException(Util.stackTraceToString(e));
    }
  }

  @Override
  public List<BaseSearchResult> getMainGridSearchResults(String searchValue,
      List<FilterQuery> filterQueries) throws ServerSideException {
    List<BaseSearchResult> searchData = new ArrayList<BaseSearchResult>();
    try {
      List<Object> allDataList =
          FilterManagementUtil.getInstance().getRawFilteredData(filterQueries).getFilteredData();
      for (Object data : allDataList) {
        if (data instanceof Aggregator) {
          String id = ((Aggregator) data).getId();
          String name = ((Aggregator) data).getName();
          String nameCode = ((Aggregator) data).getNameCode();
          if (Util.compareStrings(searchValue, name) || Util.compareStrings(searchValue, nameCode)) {
            DefaultBaseSearchResult agg =
                createModelEuropeana(id, name, nameCode, "", "", DataType.AGGREGATOR);
            searchData.add(agg);
          }
        } else if (data instanceof DataProvider) {
          String id = ((DataProvider) data).getId();
          String name = ((DataProvider) data).getName();
          String nameCode = ((DataProvider) data).getNameCode();
          String description = ((DataProvider) data).getDescription();
          if (Util.compareStrings(searchValue, description)
              || Util.compareStrings(searchValue, name)
              || Util.compareStrings(searchValue, nameCode)) {
            DefaultBaseSearchResult dp =
                createModelEuropeana(id, name, nameCode, description, "", DataType.DATA_PROVIDER);
            searchData.add(dp);
          }
        } else if (data instanceof DataSourceContainer) {
          String id = ((DataSourceContainer) data).getDataSource().getId();
          String name = ((DefaultDataSourceContainer) data).getName();
          String nameCode = ((DefaultDataSourceContainer) data).getNameCode();
          String description = ((DataSourceContainer) data).getDataSource().getDescription();
          String records = ((DataSourceContainer) data).getDataSource().getNumberRecords()[2];
          if (Util.compareStrings(searchValue, id) || Util.compareStrings(searchValue, description)
              || Util.compareStrings(searchValue, name)
              || Util.compareStrings(searchValue, nameCode)
              || Util.compareStrings(searchValue, records)) {
            DefaultBaseSearchResult ds =
                createModelEuropeana(id, name, nameCode, description, id, DataType.DATA_SET);
            ds.set("records", records);
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

  @Override
  public int getDataPage(String id, int pageSize) {
    try {
      List<Object> allDataList =
          RepoxServiceImpl.getRepoxManager().getDataManager().getAllDataList();
      int showSize = RepoxServiceImpl.getRepoxManager().getDataManager().getShowSize();
      int extra = 0;
      for (int i = 0; i < showSize + extra; i += pageSize) {
        for (int j = i; j < pageSize + i && j < showSize + extra; j++) {
          String modelId = null;
          if (allDataList.get(j) instanceof Aggregator) {
            modelId = ((Aggregator) allDataList.get(j)).getId();
          } else if (allDataList.get(j) instanceof DataProvider) {
            DataProvider dataProvider = ((DataProvider) allDataList.get(j));
            modelId = dataProvider.getId();
            if (dataProvider.getDataSourceContainers().values().size() == 1)
              extra++;
          } else if (allDataList.get(j) instanceof DataSourceContainer) {
            modelId = ((DataSourceContainer) allDataList.get(j)).getDataSource().getId();
          }

          if (modelId != null && modelId.equals(id)) {
            return (i / pageSize) + 1;
          }
        }
      }

    } catch (ServerSideException e) {
      e.printStackTrace(); // To change body of catch statement use File | Settings | File
                           // Templates.
    }
    return -1;
  }

  private DataSourceContainer getFirstElementOfCollection(Collection<DataSourceContainer> collection) {
    for (DataSourceContainer dataSourceContainer : collection) {
      return dataSourceContainer;
    }
    return null;
  }
}
