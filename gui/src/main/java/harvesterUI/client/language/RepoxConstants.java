package harvesterUI.client.language;

import com.google.gwt.i18n.client.Constants;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 13/02/12
 * Time: 13:31
 */
public interface RepoxConstants extends Constants{

    @DefaultStringValue("Yes")
    String yes();
    @DefaultStringValue("No")
    String no();
    @DefaultStringValue("Confirm")
    String confirm();

    /*---------------------- Top Toolbar ----------------------*/
    @DefaultStringValue("Welcome")
    String welcome();

    @DefaultStringValue("---")
    String refresh();

    @DefaultStringValue("Id")
    String id();

    @DefaultStringValue("Home")
    String home();

    @DefaultStringValue("Statistics")
    String statistics();

    @DefaultStringValue("Schema Mapper")
    String schemaMapper();

    @DefaultStringValue("OAI-PMH Tests")
    String oaiPmhTests();

    @DefaultStringValue("Harvesting")
    String harvesting();

    @DefaultStringValue("Scheduled Tasks (Calendar)")
    String scheduledTasksCalendar();

    @DefaultStringValue("Scheduled Tasks (List)")
    String scheduledTasksList();

    @DefaultStringValue("Running Tasks")
    String runningTasks();

    @DefaultStringValue("Administration")
    String administration();

    @DefaultStringValue("Configuration Settings")
    String configurationSettings();

    @DefaultStringValue("User Management")
    String userManagement();

    @DefaultStringValue("External Services Manager")
    String externalServicesManager();

    @DefaultStringValue("Rest Services")
    String restServices();

    @DefaultStringValue("Help")
    String help();

    @DefaultStringValue("View helpful documentation about the framework")
    String helpTooltipInfo();

    @DefaultStringValue("Anonymous")
    String anonymous();

    @DefaultStringValue("---")
    String logoutButton();
    @DefaultStringValue("---")
    String logoutButtonTooltip();
    @DefaultStringValue("---")
    String sendFeedbackButton();
    @DefaultStringValue("---")
    String sendFeedbackButtonTooltip();
    @DefaultStringValue("---")
    String rssFeedButton();
    @DefaultStringValue("---")
    String rssFeedButtonTooltip();
    @DefaultStringValue("---")
    String editAccountButton();
    @DefaultStringValue("---")
    String editAccountButtonTooltip();

    /*---------------------- Aggregator ----------------------*/

    @DefaultStringValue("---")
    String createDataProvider();
    @DefaultStringValue("---")
    String manageAggregator();
    @DefaultStringValue("---")
    String editAggregator();
    @DefaultStringValue("---")
    String removeAggregator();
    @DefaultStringValue("---")
    String deleteAggregators();
    @DefaultStringValue("---")
    String aggregatorsDeleted();
    @DefaultStringValue("---")
    String aggregatorDeleteMessage();
    @DefaultStringValue("---")
    String deleteAggregatorsMask();
    @DefaultStringValue("---")
    String aggregatorAlreadyExists();

    /*---------------------- Data Provider ----------------------*/

    @DefaultStringValue("---")
    String createDataSet();
    @DefaultStringValue("---")
    String importDataProviders();
    @DefaultStringValue("---")
    String manageDataProvider();
    @DefaultStringValue("---")
    String editDataProvider();
    @DefaultStringValue("---")
    String removeDataProvider();
    @DefaultStringValue("---")
    String moveDataProvider();
    @DefaultStringValue("---")
    String deleteDataProviders();
    @DefaultStringValue("---")
    String dataProvidersDeleted();
    @DefaultStringValue("---")
    String deletingDataProvidersMask();
    @DefaultStringValue("---")
    String deleteDataProvidersMessage();
    @DefaultStringValue("---")
    String deleteDataProvidersError();

    /*---------------------- Data Set ----------------------*/

    @DefaultStringValue("---")
    String deleteDataSets();
    @DefaultStringValue("---")
    String dataSetDeleted();
    @DefaultStringValue("---")
    String deleteDataSetMask();
    @DefaultStringValue("---")
    String harvest();
    @DefaultStringValue("---")
    String emptyDataSet();
    @DefaultStringValue("---")
    String emptyDataSetMessage();
    @DefaultStringValue("---")
    String ingestNow();
    @DefaultStringValue("---")
    String ingestSample();
    @DefaultStringValue("---")
    String scheduleIngest();
    @DefaultStringValue("---")
    String scheduleExport();
    @DefaultStringValue("---")
    String exportNow();
    @DefaultStringValue("---")
    String viewInfo();
    @DefaultStringValue("---")
    String testOaiPMH();
    @DefaultStringValue("---")
    String manageDataSet();
    @DefaultStringValue("---")
    String moveDataSet();
    @DefaultStringValue("---")
    String editDataSet();
    @DefaultStringValue("---")
    String removeDataSet();
    @DefaultStringValue("---")
    String removeDataSetMessage();
    @DefaultStringValue("---")
    String emptyingDataSetMask();
    @DefaultStringValue("---")
    String emptySuccessful();
    @DefaultStringValue("---")
    String removeDataSetMask();
    @DefaultStringValue("---")
    String dataSetNotFound();
    @DefaultStringValue("---")
    String taskAlreadyExecuting();
    @DefaultStringValue("---")
    String harvestWillStart();
    @DefaultStringValue("---")
    String calendar();
    @DefaultStringValue("---")
    String newDSTitle();
    @DefaultStringValue("---")
    String collapseAll();

    /*---------------------- Schedule Tasks ----------------------*/

    @DefaultStringValue("---")
    String firstRun();
    @DefaultStringValue("---")
    String recordsPerFile();
    @DefaultStringValue("---")
    String noAvailableTransformation();
    @DefaultStringValue("---")
    String exportFormat();
    @DefaultStringValue("---")
    String fullPath();
    @DefaultStringValue("---")
    String once();
    @DefaultStringValue("---")
    String daily();
    @DefaultStringValue("---")
    String weekly();
    @DefaultStringValue("---")
    String every();
    @DefaultStringValue("---")
    String frequency();
    @DefaultStringValue("---")
    String months();
    @DefaultStringValue("---")
    String viewDataSet();
    @DefaultStringValue("---")
    String deleteScheduledTask();
    @DefaultStringValue("---")
    String failedDeleteScheduledTask();
    @DefaultStringValue("---")
    String deleteScheduledTaskMessage();
    @DefaultStringValue("---")
    String deleteScheduledTaskSuccess();
    @DefaultStringValue("---")
    String delete();
    @DefaultStringValue("---")
    String schedule();
    @DefaultStringValue("---")
    String scheduleTaskUpdate();
    @DefaultStringValue("---")
    String failedScheduleUpdate();
    @DefaultStringValue("---")
    String scheduleUpdateSuccess();
    @DefaultStringValue("---")
    String exportSchedule();
    @DefaultStringValue("---")
    String exportScheduleError();
    @DefaultStringValue("---")
    String exportAlreadyExists();
    @DefaultStringValue("---")
    String exportScheduleSuccess();

    @DefaultStringValue("---")
    String incrementalIngest();
    @DefaultStringValue("---")
    String fullIngest();
    @DefaultStringValue("---")
    String typeIngest();
    @DefaultStringValue("---")
    String scheduleIngestError();
    @DefaultStringValue("---")
    String scheduleIngestAlreadyExists();
    @DefaultStringValue("---")
    String scheduleIngestSuccess();

    /*---------------------- Forms ----------------------*/
    @DefaultStringValue("---")
    String cancel();
    @DefaultStringValue("---")
    String save();
    @DefaultStringValue("---")
    String name();
    @DefaultStringValue("---")
    String nameCode();
    @DefaultStringValue("---")
    String homepage();
    @DefaultStringValue("---")
    String createAggregator();
    @DefaultStringValue("---")
    String saveAggregator();
    @DefaultStringValue("---")
    String homepageUrlMalformed();
    @DefaultStringValue("---")
    String homepageUrlNotExists();
    @DefaultStringValue("---")
    String homepageAlreadyExists();
    @DefaultStringValue("---")
    String saveAggregatorSuccess();

    @DefaultStringValue("---")
    String country();
    @DefaultStringValue("---")
    String providerName();
    @DefaultStringValue("---")
    String type();
    @DefaultStringValue("---")
    String description();
    @DefaultStringValue("---")
    String saveDataProvider();
    @DefaultStringValue("---")
    String dataProviderAlreadyExists();
    @DefaultStringValue("---")
    String dataProviderSaveError();
    @DefaultStringValue("---")
    String dataProviderSaveSuccess();

    @DefaultStringValue("---")
    String namespaces();
    @DefaultStringValue("---")
    String output();
    @DefaultStringValue("---")
    String add();
    @DefaultStringValue("---")
    String newStr();
    @DefaultStringValue("---")
    String recordSet();
    @DefaultStringValue("---")
    String transformations();
    @DefaultStringValue("---")
    String transformation();
    @DefaultStringValue("---")
    String saveDataSetMask();
    @DefaultStringValue("---")
    String saveDataSet();
    @DefaultStringValue("---")
    String invalidArguments();
    @DefaultStringValue("---")
    String oaiUrlMalformed();
    @DefaultStringValue("---")
    String oaiUrlNotExists();
    @DefaultStringValue("---")
    String httpUrlMalformed();
    @DefaultStringValue("---")
    String httpUrlNotExists();
    @DefaultStringValue("---")
    String dataSetAlreadyExists();
    @DefaultStringValue("---")
    String incompatibleType();
    @DefaultStringValue("---")
    String errorAccessDatabase();
    @DefaultStringValue("---")
    String ftpConnectionFailed();
    @DefaultStringValue("---")
    String errorSaveDataSet();
    @DefaultStringValue("---")
    String saveDataSetSuccess();
    @DefaultStringValue("---")
    String dataSetIngesting();

    /*---------------------- Forms Fields ----------------------*/
    @DefaultStringValue("---")
    String dataSet();
    @DefaultStringValue("---")
    String oaiUrl();
    @DefaultStringValue("---")
    String check();
    @DefaultStringValue("---")
    String checkUrl();
    @DefaultStringValue("---")
    String invalidUrl();
    @DefaultStringValue("---")
    String pleaseInsertUrl();
    @DefaultStringValue("---")
    String addAll();
    @DefaultStringValue("---")
    String pleaseFillNameAndNamecode();
    @DefaultStringValue("---")
    String allDataSetsAddedSuccess();
    @DefaultStringValue("---")
    String addAllDataSetsMask();
    @DefaultStringValue("---")
    String oaiSet();
    @DefaultStringValue("---")
    String metadataFormat();
    @DefaultStringValue("---")
    String other();
    @DefaultStringValue("---")
    String schema();
    @DefaultStringValue("---")
    String metadataNamespace();
    @DefaultStringValue("---")
    String exportPath();

    @DefaultStringValue("---")
    String recordRootName();
    @DefaultStringValue("---")
    String iso2709Variant();
    @DefaultStringValue("---")
    String characterEncoding();
    @DefaultStringValue("---")
    String idPolicy();
    @DefaultStringValue("---")
    String identifierXpath();
    @DefaultStringValue("---")
    String idExtracted();
    @DefaultStringValue("---")
    String retrieveVariant();
    @DefaultStringValue("---")
    String server();
    @DefaultStringValue("---")
    String user();
    @DefaultStringValue("---")
    String password();
    @DefaultStringValue("---")
    String httpUrl();
    @DefaultStringValue("---")
    String folderFtp();
    @DefaultStringValue("---")
    String folderPath();
    @DefaultStringValue("---")
    String authentication();

    @DefaultStringValue("---")
    String address();
    @DefaultStringValue("---")
    String port();
    @DefaultStringValue("---")
    String database();
    @DefaultStringValue("---")
    String recordSyntax();
    @DefaultStringValue("---")
    String harvestMethod();
    @DefaultStringValue("---")
    String maximumID();
    @DefaultStringValue("---")
    String fileOnePerID();
    @DefaultStringValue("---")
    String earliestDate();

    @DefaultStringValue("---")
    String info();
    @DefaultStringValue("---")
    String localMetadataFormat();
    @DefaultStringValue("---")
    String numberOfRecords();
    @DefaultStringValue("---")
    String namespacePrefixUri();

    @DefaultStringValue("---")
    String addNamespace();
    @DefaultStringValue("---")
    String namespacePrefix();
    @DefaultStringValue("---")
    String namespaceUri();

    /*---------------------- REPOX Configurations ----------------------*/

    @DefaultStringValue("---")
    String repositoryFolder();
    @DefaultStringValue("---")
    String repositoryFolderExample();
    @DefaultStringValue("---")
    String configurationFilesFolder();
    @DefaultStringValue("---")
    String configurationFilesFolderExample();
    @DefaultStringValue("---")
    String oaiPmhRequestsFolder();
    @DefaultStringValue("---")
    String oaiPmhRequestsFolderExample();
    @DefaultStringValue("---")
    String ftpRequestsFolder();
    @DefaultStringValue("---")
    String ftpRequestsFolderExample();
    @DefaultStringValue("---")
    String httpRequestsFolder();
    @DefaultStringValue("---")
    String httpRequestsFolderExample();
//    @DefaultStringValue("---")
//    String derbyDatabaseFolder();
//    @DefaultStringValue("---")
//    String derbyDatabaseFolderExample();
    @DefaultStringValue("---")
    String sampleRecords();
    @DefaultStringValue("---")
    String sampleRecordsExample();
    @DefaultStringValue("---")
    String baseUrn();
    @DefaultStringValue("---")
    String baseUrnExample();
    @DefaultStringValue("---")
    String defaultExportFolder();
    @DefaultStringValue("---")
    String defaultExportFolderExample();
    @DefaultStringValue("---")
    String administratorEmail();
    @DefaultStringValue("---")
    String administratorEmailInfo();
    @DefaultStringValue("---")
    String repoxDefaultEmailSender();
    @DefaultStringValue("---")
    String repoxDefaultEmailSenderInfo();
    @DefaultStringValue("---")
    String repoxDefaultEmailPassword();
    @DefaultStringValue("---")
    String smtpServer();
    @DefaultStringValue("---")
    String smtpPort();

    /*---------------------- User Management ----------------------*/

    @DefaultStringValue("---")
    String noUsersAvailable();
    @DefaultStringValue("---")
    String addUser();
    @DefaultStringValue("---")
    String updateUser();
    @DefaultStringValue("---")
    String removeUsers();
    @DefaultStringValue("---")
    String deleteUsers();
    @DefaultStringValue("---")
    String usersDeleted();
    @DefaultStringValue("---")
    String deleteUserConfirmMessage();
    @DefaultStringValue("---")
    String cannotRemoveSelf();
    @DefaultStringValue("---")
    String username();
    @DefaultStringValue("---")
    String email();
    @DefaultStringValue("---")
    String role();
    @DefaultStringValue("---")
    String saveUserMask();
    @DefaultStringValue("---")
    String updateUserMask();
    @DefaultStringValue("---")
    String newUser();
    @DefaultStringValue("---")
    String usernameAlreadyExists();
    @DefaultStringValue("---")
    String saveNewUserSuccess();
    @DefaultStringValue("---")
    String updateUserSuccess();
    @DefaultStringValue("---")
    String usernameValidateMessage();
    @DefaultStringValue("---")
    String emailValidateMessage();
    @DefaultStringValue("---")
    String samePasswordValidateMessage();
    @DefaultStringValue("---")
    String newPassword();
    @DefaultStringValue("---")
    String confirmPassword();
    @DefaultStringValue("---")
    String editAccount();
    @DefaultStringValue("---")
    String editAccountSuccess();
    @DefaultStringValue("---")
    String editAccountSettings();
    @DefaultStringValue("---")
    String resetPasswordMask();
    @DefaultStringValue("---")
    String passwordReset();
    @DefaultStringValue("---")
    String passwordResetSuccess();
    @DefaultStringValue("---")
    String userEmailMatchNotFound();
    @DefaultStringValue("---")
    String dontAskPassword();
    @DefaultStringValue("---")
    String recoverPassword();
    @DefaultStringValue("---")
    String login();
    @DefaultStringValue("---")
    String verifyingLogin();
    @DefaultStringValue("---")
    String alert();
    @DefaultStringValue("---")
    String usersFileCorrupted();
    @DefaultStringValue("---")
    String userNameOrPasswordIncorrect();
    @DefaultStringValue("---")
    String send();

    /*---------------------- External Services ----------------------*/
    @DefaultStringValue("---")
    String noServicesAvailable();
    @DefaultStringValue("---")
    String editExternalService();
    @DefaultStringValue("---")
    String loadingServiceData();
    @DefaultStringValue("---")
    String removeExternalService();
    @DefaultStringValue("---")
    String deleteServices();
    @DefaultStringValue("---")
    String deleteServiceSuccess();
    @DefaultStringValue("---")
    String deleteServiceConfirmMessage();
    @DefaultStringValue("---")
    String addExternalService();
    @DefaultStringValue("---")
    String externalRestServices();
    @DefaultStringValue("---")
    String loadingRestServicesMask();
    @DefaultStringValue("---")
    String restService();
    @DefaultStringValue("---")
    String executionType();
    @DefaultStringValue("---")
    String uriType();
    @DefaultStringValue("---")
    String generalInfo();
    @DefaultStringValue("---")
    String serviceName();
    @DefaultStringValue("---")
    String statusUri();
    @DefaultStringValue("---")
    String addField();
    @DefaultStringValue("---")
    String saveExternalService();
    @DefaultStringValue("---")
    String errorSavingExternalService();
    @DefaultStringValue("---")
    String saveExternalServiceSuccess();
    @DefaultStringValue("---")
    String saveExternalServiceMask();
    @DefaultStringValue("---")
    String serviceFields();
    @DefaultStringValue("---")
    String field();
    @DefaultStringValue("---")
    String parameterName();
    @DefaultStringValue("---")
    String restExample();
    @DefaultStringValue("---")
    String semantics();
    @DefaultStringValue("---")
    String fieldRequired();
    @DefaultStringValue("---")
    String comboValue();
    @DefaultStringValue("---")
    String addComboValue();
    @DefaultStringValue("---")
    String uri();
    @DefaultStringValue("---")
    String externalServiceResult();
    @DefaultStringValue("---")
    String wasSuccessful();
    @DefaultStringValue("---")
    String wasNotSuccessful();
    @DefaultStringValue("---")
    String service();
    @DefaultStringValue("---")
    String running();

    /*---------------------- Oai test Panel ----------------------*/
    @DefaultStringValue("---")
    String set();
    @DefaultStringValue("---")
    String from();
    @DefaultStringValue("---")
    String until();
    @DefaultStringValue("---")
    String identifier();
    @DefaultStringValue("---")
    String resumptionToken();
    @DefaultStringValue("---")
    String noSetsFound();
    @DefaultStringValue("---")
    String noMetadataPrefixesFound();
    @DefaultStringValue("---")
    String noUrlFound();
    @DefaultStringValue("---")
    String others();
    @DefaultStringValue("---")
    String parameters();
    @DefaultStringValue("---")
    String response();
    @DefaultStringValue("---")
    String restOperations();
    @DefaultStringValue("---")
    String metadataPrefix();


    /*---------------------- Repox Rest Services ----------------------*/
    @DefaultStringValue("---")
    String operationsList();
    @DefaultStringValue("---")
    String aggregatorsOperations();
    @DefaultStringValue("---")
    String dataProvidersOperations();
    @DefaultStringValue("---")
    String dataSetOperations();
    @DefaultStringValue("---")
    String recordsOperations();
    @DefaultStringValue("---")
    String commonRetrievalOperations();
    @DefaultStringValue("---")
    String recordUrn();
    @DefaultStringValue("---")
    String operations();
    @DefaultStringValue("---")
    String recordFillUrn();
    @DefaultStringValue("---")
    String pleaseInsertUrn();
    @DefaultStringValue("---")
    String listAggregators();
    @DefaultStringValue("---")
    String listdataProviders();
    @DefaultStringValue("---")
    String listDataSets();
    @DefaultStringValue("---")
    String modificationOperations();
    @DefaultStringValue("---")
    String operation();
    @DefaultStringValue("---")
    String selectOperation();
    @DefaultStringValue("---")
    String saveRecord();
    @DefaultStringValue("---")
    String deleteRecord();
    @DefaultStringValue("---")
    String eraseRecord();
    @DefaultStringValue("---")
    String dataSetId();
    @DefaultStringValue("---")
    String recordId();
    @DefaultStringValue("---")
    String recordXML();
    @DefaultStringValue("---")
    String submit();
    @DefaultStringValue("---")
    String urlRetrievalOperations();

    /*---------------------- Calendar ----------------------*/
    @DefaultStringValue("---")
    String deleteScheduledTaskConfirmMessage();
    @DefaultStringValue("---")
    String moveOldTasks();
    @DefaultStringValue("---")
    String moveOldTaskError();
    @DefaultStringValue("---")
    String moveScheduledTask();
    @DefaultStringValue("---")
    String moveScheduledTaskError();
    @DefaultStringValue("---")
    String moveScheduledTaskPrevDateError();
    @DefaultStringValue("---")
    String scheduleTaskMoved();
    @DefaultStringValue("---")
    String oneDay();
    @DefaultStringValue("---")
    String threeDay();
    @DefaultStringValue("---")
    String week();
    @DefaultStringValue("---")
    String month();
    @DefaultStringValue("---")
    String datePicker();
    @DefaultStringValue("---")
    String ingestResume();
    @DefaultStringValue("---")
    String ingestDate();
    @DefaultStringValue("---")
    String dataProvider();
    @DefaultStringValue("---")
    String status();
    @DefaultStringValue("---")
    String ingestType();
    @DefaultStringValue("---")
    String numberRetries();
    @DefaultStringValue("---")
    String numberRecordsIngested();
    @DefaultStringValue("---")
    String logLink();
    @DefaultStringValue("---")
    String today();

    /*---------------------- Send Feedback Dialog ----------------------*/
    @DefaultStringValue("---")
    String feedback();
    @DefaultStringValue("---")
    String idea();
    @DefaultStringValue("---")
    String ideaExample();
    @DefaultStringValue("---")
    String problem();
    @DefaultStringValue("---")
    String problemExample();
    @DefaultStringValue("---")
    String question();
    @DefaultStringValue("---")
    String questionExample();
    @DefaultStringValue("---")
    String feedbackTitle();
    @DefaultStringValue("---")
    String sendMessage();
    @DefaultStringValue("---")
    String sendEmailMask();
    @DefaultStringValue("---")
    String repoxMessage();

    /*---------------------- Rss Feed Repox ----------------------*/
    @DefaultStringValue("---")
    String rssPanelTitle();
    @DefaultStringValue("---")
    String date();
    @DefaultStringValue("---")
    String title();
    @DefaultStringValue("---")
    String link();
    @DefaultStringValue("---")
    String noFeedsAvailable();
    @DefaultStringValue("---")
    String loadingFeedsMask();

    @DefaultStringValue("---")
    String noScheduledTasks();
    @DefaultStringValue("---")
    String noTasksRunning();
    @DefaultStringValue("---")
    String cancelHarvest();
    @DefaultStringValue("---")
    String taskOfDataSet();

    /*---------------------- Main Grid ----------------------*/
    @DefaultStringValue("---")
    String nameCodeHeader();
    @DefaultStringValue("---")
    String dataSetHeader();
    @DefaultStringValue("---")
    String oaiSchemasHeader();
    @DefaultStringValue("---")
    String ingestTypeHeader();
    @DefaultStringValue("---")
    String lastIngestHeader();
    @DefaultStringValue("---")
    String nextIngestHeader();
    @DefaultStringValue("---")
    String ingestStatusHeader();
    @DefaultStringValue("---")
    String loadingMainData();
    @DefaultStringValue("---")
    String records();
    @DefaultStringValue("---")
    String loadingFilter();
    @DefaultStringValue("---")
    String noDataAvailable();
    @DefaultStringValue("---")
    String view();
    @DefaultStringValue("---")
    String aggregators();
    @DefaultStringValue("---")
    String dataProviders();
    @DefaultStringValue("---")
    String dataSets();
    @DefaultStringValue("---")
    String autoRefresh();
    @DefaultStringValue("---")
    String goTo();

    @DefaultStringValue("---")
    String viewLog();
    @DefaultStringValue("---")
    String taskFailedRetry();
    @DefaultStringValue("---")
    String of();
    @DefaultStringValue("---")
    String warning();
    @DefaultStringValue("---")
    String viewRunningTasks();
    @DefaultStringValue("---")
    String error();
    @DefaultStringValue("---")
    String taskCanceled();
    @DefaultStringValue("---")
    String ingestSuccessful();
    @DefaultStringValue("---")
    String preProcessing();
    @DefaultStringValue("---")
    String postProcessing();
    @DefaultStringValue("---")
    String preProcessError();
    @DefaultStringValue("---")
    String postProcessError();
    @DefaultStringValue("---")
    String ingestingSample();
    @DefaultStringValue("---")
    String estimate();
    @DefaultStringValue("---")
    String export();
    @DefaultStringValue("---")
    String exportFailed();
    @DefaultStringValue("---")
    String exportWillStart();
    @DefaultStringValue("---")
    String moveDataSets();
    @DefaultStringValue("---")
    String noOtherDataProvidersAvailable();
    @DefaultStringValue("---")
    String move();
    @DefaultStringValue("---")
    String moveDataSetSuccessful();
    @DefaultStringValue("---")
    String importFile();
    @DefaultStringValue("---")
    String importName();
    @DefaultStringValue("---")
    String importingDPs();
    @DefaultStringValue("---")
    String notFound();
    @DefaultStringValue("---")
    String cancelTaskWarning();
    @DefaultStringValue("---")
    String loadingSearchResults();
    @DefaultStringValue("---")
    String loadingInterface();

    /*---------------------- View Data Set Info ----------------------*/
    @DefaultStringValue("---")
    String informationHistory();
    @DefaultStringValue("---")
    String lastExportData();
    @DefaultStringValue("---")
    String lastExportZipArchive();
    @DefaultStringValue("---")
    String validationResult();
    @DefaultStringValue("---")
    String validationSuccess();
    @DefaultStringValue("---")
    String validationNotSuccess();
    @DefaultStringValue("---")
    String viewValidationReport();
    @DefaultStringValue("---")
    String fullTextResult();
    @DefaultStringValue("---")
    String fullTextSuccess();
    @DefaultStringValue("---")
    String fullTextNotSuccess();
    @DefaultStringValue("---")
    String viewFullTextReport();
    @DefaultStringValue("---")
    String oldTasks();
    @DefaultStringValue("---")
    String logFile();
    @DefaultStringValue("---")
    String showAll();
    @DefaultStringValue("---")
    String hideAll();
    @DefaultStringValue("---")
    String noDataSetSelected();
    @DefaultStringValue("---")
    String noDataSetFound();
    @DefaultStringValue("---")
    String viewDataSetInformation();
    @DefaultStringValue("---")
    String scheduledTasks();

    /*---------------------- Filters ----------------------*/
    @DefaultStringValue("---")
    String metadata();
    @DefaultStringValue("---")
    String recent();
    @DefaultStringValue("---")
    String lastIngest();
    @DefaultStringValue("---")
    String filters();
    @DefaultStringValue("---")
    String startFilter();
    @DefaultStringValue("---")
    String resetFilter();
    @DefaultStringValue("---")
    String data();
    @DefaultStringValue("---")
    String range();
    @DefaultStringValue("---")
    String filter();
    @DefaultStringValue("---")
    String reset();
    @DefaultStringValue("---")
    String ingestTypes();
    @DefaultStringValue("---")
    String countries();
    @DefaultStringValue("---")
    String types();
    @DefaultStringValue("---")
    String sourceMetadataFormat();
    @DefaultStringValue("---")
    String time();
    @DefaultStringValue("---")
    String hours();
    @DefaultStringValue("---")
    String invalidHour();
    @DefaultStringValue("---")
    String minutes();
    @DefaultStringValue("---")
    String invalidMinute();

    /*---------------------- Schema Mapper ----------------------*/
    @DefaultStringValue("---")
    String sourceFormat();
    @DefaultStringValue("---")
    String destinationFormat();
    @DefaultStringValue("---")
    String xsl();
    @DefaultStringValue("---")
    String download();
    @DefaultStringValue("---")
    String addTransformation();
    @DefaultStringValue("---")
    String noTransformationsAvailable();
    @DefaultStringValue("---")
    String xslVersion2();
    @DefaultStringValue("---")
    String transformationFile();
    @DefaultStringValue("---")
    String saveTransformation();
    @DefaultStringValue("---")
    String saveTransformationSuccess();
    @DefaultStringValue("---")
    String xslFile();
    @DefaultStringValue("---")
    String editTransformation();
    @DefaultStringValue("---")
    String removeTransformation();
    @DefaultStringValue("---")
    String deleteTransformation();
    @DefaultStringValue("---")
    String deleteTransformationConfirm();
    @DefaultStringValue("---")
    String deleteTransformationSuccess();

    /*---------------------- Statistics ----------------------*/
    @DefaultStringValue("---")
    String idGenerated();
    @DefaultStringValue("---")
    String averages();
    @DefaultStringValue("---")
    String averageRecordsDP();
    @DefaultStringValue("---")
    String averageRecordsDS();
    @DefaultStringValue("---")
    String generationDate();
    @DefaultStringValue("---")
    String statisticsPanelMask();

    @DefaultStringValue("---")
    String rowsPerPage();

    /*---------------------- MDR Integration ----------------------*/
    @DefaultStringValue("---")
    String transformationsUsageList();
    @DefaultStringValue("---")
    String selection();
    @DefaultStringValue("---")
    String selectionEmptyWarning();
    @DefaultStringValue("---")
    String dataSetUsageTootip();
    @DefaultStringValue("---")
    String mdrMappings();
    @DefaultStringValue("---")
    String services();
    @DefaultStringValue("---")
    String information();
    @DefaultStringValue("---")
    String openMdrInfo();
    @DefaultStringValue("---")
    String addSchema();
    @DefaultStringValue("---")
    String addMapping();
    @DefaultStringValue("---")
    String importStr();
    @DefaultStringValue("---")
    String mdrGUIAccess();
    @DefaultStringValue("---")
    String importingMappingsMask();
    @DefaultStringValue("---")
    String importMdrMapping();
    @DefaultStringValue("---")
    String imported();
    @DefaultStringValue("---")
    String errorImports();
    @DefaultStringValue("---")
    String alreadyExisted();
    @DefaultStringValue("---")
    String reloadingMappings();
    @DefaultStringValue("---")
    String updatingMappings();
    @DefaultStringValue("---")
    String updateMappings();
    @DefaultStringValue("---")
    String thereAre();
    @DefaultStringValue("---")
    String mappingsInRepox();
    @DefaultStringValue("---")
    String updateMappingsQuestion();

    @DefaultStringValue("---")
    String schemas();
    @DefaultStringValue("---")
    String mappings();
    @DefaultStringValue("---")
    String editSchema();
    @DefaultStringValue("---")
    String designation();
    @DefaultStringValue("---")
    String shortDesignation();
    @DefaultStringValue("---")
    String xsdLink();
    @DefaultStringValue("---")
    String namespace();
    @DefaultStringValue("---")
    String notes();
    @DefaultStringValue("---")
    String saveMetadataSchema();
    @DefaultStringValue("---")
    String saveMetadataSchemaSuccess();
    @DefaultStringValue("---")
    String edit();
    @DefaultStringValue("---")
    String deleteMetadataSchema();
    @DefaultStringValue("---")
    String deleteMetadataSchemaSuccess();
    @DefaultStringValue("---")
    String deleteMetadataSchemaQuestion();

    @DefaultStringValue("---")
    String storeInYadda();

}
