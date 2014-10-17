package pt.utl.ist.dataProvider;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import pt.utl.ist.accessPoint.AccessPoint;
import pt.utl.ist.accessPoint.manager.DefaultAccessPointsManager;
import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxConfiguration;
import pt.utl.ist.dataProvider.dataSource.*;
import pt.utl.ist.externalServices.*;
import pt.utl.ist.ftp.FtpFileRetrieveStrategy;
import pt.utl.ist.http.HttpFileRetrieveStrategy;
import pt.utl.ist.marc.*;
import pt.utl.ist.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.rest.util.ExternalServiceUtil;
import pt.utl.ist.sru.SruRecordUpdateDataSource;
import pt.utl.ist.task.*;
import pt.utl.ist.util.CompareDataUtil;
import pt.utl.ist.util.FileUtilSecond;
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.TimeUtil;
import pt.utl.ist.util.Urn;
import pt.utl.ist.util.XmlUtil;
import pt.utl.ist.util.date.DateUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.IncompatibleInstanceException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.z3950.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

public class DefaultDataManager implements DataManager {
    private static final Logger             log                   = Logger.getLogger(DefaultDataManager.class);
    private static final String             ID_REGULAR_EXPRESSION = "[a-zA-Z_0-9]*";
    protected static final int              ID_MAX_SIZE           = 160;

    protected File                          dataFile;
    protected MetadataTransformationManager metadataTransformationManager;
    protected MetadataSchemaManager         metadataSchemaManager;
    protected List<Aggregator>              aggregators;
    protected File                          oldTasksFile;

    private int                             showSize              = 0;
    protected List<Object>                  allDataList;
    private DefaultRepoxConfiguration       configuration;

    public DefaultDataManager(File dataFile, MetadataTransformationManager metadataTransformationManager, MetadataSchemaManager metadataSchemaManager, File repositoryPath, File oldTasksFile, File defaultExportDir, DefaultRepoxConfiguration configuration) throws DocumentException, IOException,
                                                                                                                                                                                                                                                              ParseException {
        super();
        this.configuration = configuration;
        this.dataFile = dataFile;
        this.metadataTransformationManager = metadataTransformationManager;
        this.metadataSchemaManager = metadataSchemaManager;
        this.oldTasksFile = oldTasksFile;
        loadAggregators(repositoryPath, defaultExportDir);
        loadAllDataList();
        if (!this.dataFile.exists()) {
            try {
                saveData();
            } catch (IOException e) {
                log.error("Can't create DataProviders configuration file", e);
            }
        }
    }

    private void loadAllDataList() {
        showSize = 0;
        allDataList = new ArrayList<Object>();
        Collections.sort(aggregators, new AggregatorComparator());
        for (Aggregator aggregator : aggregators) {
            allDataList.add(aggregator);
            showSize++;
            if (aggregator.getDataProviders() != null) {
                Collections.sort(aggregator.getDataProviders(), new CompareDataUtil.DPComparator());
                for (DataProvider dataProvider : aggregator.getDataProviders()) {
                    allDataList.add(dataProvider);
                    showSize++;
                    if (dataProvider.getDataSourceContainers() != null) {
                        List<DataSourceContainer> containers = CompareDataUtil.convertHashToList(dataProvider.getDataSourceContainers());
                        Collections.sort(containers, new CompareDataUtil.DSComparator());
                        for (DataSourceContainer dataSourceContainer : containers) {
                            allDataList.add(dataSourceContainer);
                            showSize++;
                        }
                        //                        if(dataProvider.getDataSourceContainers().size() == 1)
                        //                            showSize--;
                    }
                }
            }
        }
    }

    private class AggregatorComparator implements java.util.Comparator<Aggregator> {
        public int compare(Aggregator agg1, Aggregator agg2) {
            String str1 = agg1.getName().toUpperCase();
            String str2 = agg2.getName().toUpperCase();

            if (str1.compareTo(str2) < 0)
                return -1;
            else {
                if (str1.compareTo(str2) > 0)
                    return 1;
            }
            return 0;
        }
    }

    public List<Object> getAllDataList() {
        return allDataList;
    }

    public int getShowSize() {
        return showSize;
    }

    /******************************************************************************************************************/
    /** READ/WRITE XML file *******************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Save all information in a XML file
     * @throws IOException
     */
    public synchronized void saveData() throws IOException {
        loadAllDataList();
        Document document = DocumentHelper.createDocument();
        Element rootNode = document.addElement("repox-data");

        for (Aggregator aggregator : aggregators) {
            rootNode.add(aggregator.createElement(true));
        }

        // backup file
        File backup = new File(dataFile.getParentFile().getAbsoluteFile() + File.separator + dataFile.getName() + ".bak");
        if (backup.exists())
            backup.delete();
        dataFile.renameTo(backup);

        XmlUtil.writePrettyPrint(dataFile, document);
    }

    /**
     * Load all aggregators from XML file
     * @throws DocumentException
     * @throws IOException
     */
    protected synchronized void loadAggregators(File repositoryPath, File defaultExportDir) throws DocumentException, IOException, ParseException {
        aggregators = new ArrayList<Aggregator>();
        if (!dataFile.exists()) {
            return;
        }
        aggregators = loadAggregators(dataFile, repositoryPath, defaultExportDir);
    }

    /**
     * Load all aggregators from XML file
     * @param file2Read
     * @param repositoryPath
     * @throws DocumentException
     * @throws IOException
     * @return List<Aggregator>
     */
    public synchronized List<Aggregator> loadAggregators(File file2Read, File repositoryPath, File defaultExportDir) throws DocumentException, IOException, ParseException {
        List<Aggregator> aggregatorsLoaded = new ArrayList<Aggregator>();

        SAXReader reader = new SAXReader();
        Document document = reader.read(file2Read);

        if (configuration != null && configuration.getCurrentServerOAIUrl() != null) {
            ExternalServiceUtil.replaceAllExternalServices(document, configuration.getCurrentServerOAIUrl());
            XmlUtil.writePrettyPrint(dataFile, document);
        }

        Element root = document.getRootElement();

        for (Iterator aggIterator = root.elementIterator("aggregator"); aggIterator.hasNext();) {
            // read aggregator from XML file
            Element currentElementAgg = (Element)aggIterator.next();

            Aggregator aggregator = new Aggregator();
            aggregator.setId(currentElementAgg.attributeValue("id"));

            if (currentElementAgg.element("url") != null) {
                aggregator.setHomePage(new URL(currentElementAgg.elementText("url")));
            }
            if (currentElementAgg.element("name") != null) {
                aggregator.setName(currentElementAgg.elementText("name"));
            }
            if (currentElementAgg.element("nameCode") != null) {
                aggregator.setNameCode(currentElementAgg.elementText("nameCode"));
            }

            for (Iterator provIterator = currentElementAgg.elementIterator("provider"); provIterator.hasNext();) {
                // read providers inside the aggregator
                Element currentElementProv = (Element)provIterator.next();

                String providerId = currentElementProv.attributeValue("id");

                String providerName = null;
                if (currentElementProv.elementText("name") != null) {
                    providerName = currentElementProv.elementText("name");
                }
                String providerCountry = null;
                if (currentElementProv.elementText("country") != null) {
                    providerCountry = currentElementProv.elementText("country");
                }
                String providerDescription = null;
                if (currentElementProv.elementText("description") != null) {
                    providerDescription = currentElementProv.elementText("description");
                }
                String providerNameCode = null;
                if (currentElementProv.elementText("nameCode") != null) {
                    providerNameCode = currentElementProv.elementText("nameCode");
                }
                String providerType = null;
                if (currentElementProv.elementText("type") != null) {
                    providerType = currentElementProv.elementText("type");
                }
                URL providerHomePage = null;
                if (currentElementProv.elementText("url") != null) {
                    providerHomePage = new URL(currentElementProv.elementText("url"));
                }

                HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();

                DefaultDataProvider dataProvider = new DefaultDataProvider(providerId, providerName, providerCountry, providerDescription, dataSourceContainers, providerNameCode, providerHomePage, ProviderType.get(providerType));

                for (Iterator dataSIterator = currentElementProv.elementIterator("source"); dataSIterator.hasNext();) {
                    // read data sources inside the aggregator
                    Element currentDataSourceElement = (Element)dataSIterator.next();

                    String id = currentDataSourceElement.attributeValue("id");
                    String description = currentDataSourceElement.elementText("description");
                    String metadataFormat = currentDataSourceElement.attributeValue("metadataFormat");
                    String schema = currentDataSourceElement.attributeValue("schema");
                    String namespace = currentDataSourceElement.attributeValue("namespace");
                    String lastIngest = currentDataSourceElement.attributeValue("lastIngest");
                    String sample = currentDataSourceElement.attributeValue("sample");
                    String status = currentDataSourceElement.attributeValue("status");

                    if (schema == null && namespace == null) {
                        if (metadataFormat.equals("ese")) {
                            namespace = "http://www.europeana.eu/schemas/ese/";
                            schema = "http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd";
                        } else if (metadataFormat.equals("MarcXchange") || (metadataFormat.equals("ISO2709"))) {
                            namespace = "info:lc/xmlns/marcxchange-v1";
                            schema = "info:lc/xmlns/marcxchange-v1.xsd";
                        } else if (metadataFormat.equals("tel")) {
                            namespace = "http://krait.kb.nl/coop/tel/handbook/telterms.html";
                            schema = "http://krait.kb.nl/coop/tel/handbook/telterms.html";
                        } else if (metadataFormat.equals("oai_dc")) {
                            namespace = "http://www.openarchives.org/OAI/2.0/";
                            schema = "http://www.openarchives.org/OAI/2.0/oai_dc.xsd";
                        }
                    }

                    // DataSourceContainer attributes
                    String dataSourceContainerName = null;
                    if (currentDataSourceElement.attributeValue("name") != null) {
                        dataSourceContainerName = currentDataSourceElement.attributeValue("name");
                    }

                    String dataSourceContainerNameCode = null;
                    if (currentDataSourceElement.attributeValue("nameCode") != null) {
                        dataSourceContainerNameCode = currentDataSourceElement.attributeValue("nameCode");
                    }

                    String isSampleStr = currentDataSourceElement.attributeValue("isSample");
                    if (isSampleStr == null)
                        isSampleStr = "false";
                    boolean isSample = Boolean.valueOf(isSampleStr);

                    //                    String dataSourceContainerExportPath = null;
                    //                    if(currentDataSourceElement.attributeValue("exportPath") != null){
                    //                        dataSourceContainerExportPath = currentDataSourceElement.attributeValue("exportPath");
                    //                    }

                    // Create RecordIdPolicy
                    Element recordIdPolicyNode = currentDataSourceElement.element("recordIdPolicy");
                    String recordIdPolicyClass = recordIdPolicyNode.attributeValue("type");
                    RecordIdPolicy recordIdPolicy;
                    if (recordIdPolicyClass.equals(IdGeneratedRecordIdPolicy.class.getSimpleName())) {
                        recordIdPolicy = new IdGeneratedRecordIdPolicy();
                    } else if (recordIdPolicyClass.equals(IdProvidedRecordIdPolicy.class.getSimpleName())) {
                        recordIdPolicy = new IdProvidedRecordIdPolicy();
                    } else if (recordIdPolicyClass.equals(IdExtractedRecordIdPolicy.class.getSimpleName())) {
                        String identifierXpath = recordIdPolicyNode.element("idXpath").getText();
                        Map<String, String> namespaces = new TreeMap<String, String>();
                        Element namespacesElement = recordIdPolicyNode.element("namespaces");
                        if (namespacesElement != null) {
                            List<Element> namespaceElement = namespacesElement.elements("namespace");
                            for (Element currentNamespace : namespaceElement) {
                                namespaces.put(currentNamespace.elementText("namespacePrefix"), currentNamespace.elementText("namespaceUri"));
                            }
                        }
                        recordIdPolicy = new IdExtractedRecordIdPolicy(identifierXpath, namespaces);
                    } else {
                        throw new RuntimeException("Invalid RecordIdPolicy of class " + recordIdPolicyClass);
                    }

                    //Create MetadataTransformations
                    Map<String, MetadataTransformation> metadataTransformations = new HashMap<String, MetadataTransformation>();
                    for (Element metadataTransformationElement : (List<Element>)currentDataSourceElement.element("metadataTransformations").elements("metadataTransformation")) {
                        String transformationId = metadataTransformationElement.getText();
                        MetadataTransformation metadataTransformation = metadataTransformationManager.loadMetadataTransformation(transformationId);
                        if (metadataTransformation != null)
                            metadataTransformations.put(metadataTransformation.getDestinationFormat(), metadataTransformation);
                    }

                    //Create DataSource
                    DataSource dataSource = null;

                    String dataSourceType = currentDataSourceElement.attribute("type").getText();
                    if (dataSourceType.equals("DataSourceOai")) {
                        String oaiSource = currentDataSourceElement.elementText("oai-source");
                        String oaiSet = (currentDataSourceElement.element("oai-set") != null ? currentDataSourceElement.elementText("oai-set") : null);
                        dataSource = new OaiDataSource(dataProvider, id, description, schema, namespace, metadataFormat, oaiSource, oaiSet, new IdProvidedRecordIdPolicy(), metadataTransformations);
                    } else if (dataSourceType.equals("DataSourceSruRecordUpdate")) {
                        dataSource = new SruRecordUpdateDataSource(dataProvider, id, description, schema, namespace, metadataFormat, recordIdPolicy, metadataTransformations);
                    } else if (dataSourceType.equals("DataSourceDirectoryImporter")) {
                        String sourcesDirPath = currentDataSourceElement.elementText("sourcesDirPath");
                        String extractStrategyString = currentDataSourceElement.elementText("fileExtract");
                        Element currentRetrieveStrategy = currentDataSourceElement.element("retrieveStrategy");

                        String retrieveStrategyString = null;
                        if (currentRetrieveStrategy != null) {
                            retrieveStrategyString = currentRetrieveStrategy.attributeValue("type");
                        }

                        FileRetrieveStrategy retrieveStrategy;
                        //FTP
                        if (retrieveStrategyString != null && retrieveStrategyString.equals(FtpFileRetrieveStrategy.class.getName())) {

                            String server = currentRetrieveStrategy.elementText("server");

                            String user = currentRetrieveStrategy.elementText("user");
                            String password = currentRetrieveStrategy.elementText("password");

                            String idType;
                            if (user != null && password != null) {
                                idType = FtpFileRetrieveStrategy.NORMAL;
                            } else {
                                idType = FtpFileRetrieveStrategy.ANONYMOUS;
                            }
                            String ftpPath = currentRetrieveStrategy.elementText("folderPath");
                            retrieveStrategy = new FtpFileRetrieveStrategy(server, user, password, idType, ftpPath);
                        }
                        //HTTP
                        else if (retrieveStrategyString != null && retrieveStrategyString.equals(HttpFileRetrieveStrategy.class.getName())) {

                            String url = currentRetrieveStrategy.elementText("url");
                            retrieveStrategy = new HttpFileRetrieveStrategy(url);
                        } else {
                            //FOLDER
                            retrieveStrategy = new FolderFileRetrieveStrategy();
                        }

                        CharacterEncoding characterEncoding = null;
                        String recordXPath = null;
                        Map<String, String> namespaces = null;
                        FileExtractStrategy extractStrategy = null;

                        if (extractStrategyString.equals(Iso2709FileExtractStrategy.class.getSimpleName())) {
                            characterEncoding = CharacterEncoding.get(currentDataSourceElement.attributeValue("characterEncoding"));
                            String isoImplementationClass = currentDataSourceElement.attributeValue("isoImplementationClass");
                            extractStrategy = new Iso2709FileExtractStrategy(isoImplementationClass);
                        } else if (extractStrategyString.equals(MarcXchangeFileExtractStrategy.class.getSimpleName())) {
                            extractStrategy = new MarcXchangeFileExtractStrategy();
                        } else if (extractStrategyString.equals(SimpleFileExtractStrategy.class.getSimpleName())) {
                            extractStrategy = new SimpleFileExtractStrategy();

                            Element splitRecordsElement = currentDataSourceElement.element("splitRecords");
                            if (splitRecordsElement != null) {
                                recordXPath = splitRecordsElement.elementText("recordXPath");

                                namespaces = new TreeMap<String, String>();
                                Element namespacesElement = splitRecordsElement.element("namespaces");
                                if (namespacesElement != null) {
                                    List<Element> namespaceElement = namespacesElement.elements("namespace");
                                    for (Element currentNamespace : namespaceElement) {
                                        namespaces.put(currentNamespace.elementText("namespacePrefix"), currentNamespace.elementText("namespaceUri"));
                                    }
                                }
                            }
                        }
                        dataSource = new DirectoryImporterDataSource(dataProvider, id, description, schema, namespace, metadataFormat, extractStrategy, retrieveStrategy, characterEncoding, sourcesDirPath, recordIdPolicy, metadataTransformations, recordXPath, namespaces);
                    } else if (dataSourceType.equals("DataSourceZ3950")) {
                        Element targetElement = currentDataSourceElement.element("target");
                        String targetAddress = targetElement.elementText("address");
                        int targetPort = Integer.parseInt(targetElement.elementText("port"));
                        String targetDatabase = targetElement.elementText("database");
                        String targetUser = targetElement.elementText("user");
                        String targetPassword = targetElement.elementText("password");
                        String targetRecordSyntax = targetElement.elementText("recordSyntax");
                        CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(targetElement.elementText("charset"));

                        Target target = new Target(targetAddress, targetPort, targetDatabase, targetUser, targetPassword, targetCharacterEncoding, targetRecordSyntax);

                        Harvester harvestMethod = null;

                        String harvestMethodString = currentDataSourceElement.elementText("harvestMethod");

                        if (harvestMethodString.equals(TimestampHarvester.class.getSimpleName())) {
                            String timestampString = currentDataSourceElement.elementText("earliestTimestamp");
                            try {
                                Date earliestTimestamp = DateUtil.string2Date(timestampString, "yyyyMMdd");

                                harvestMethod = new TimestampHarvester(target, earliestTimestamp);
                            } catch (ParseException e) {
                                log.error("Error parsing date: " + timestampString, e);
                                break;
                            }
                        } else if (harvestMethodString.equals(IdListHarvester.class.getSimpleName())) {
                            String filePath = currentDataSourceElement.elementText("idListFile");
                            File idListFile = new File(filePath);

                            harvestMethod = new IdListHarvester(target, idListFile);

                        } else if (harvestMethodString.equals(IdSequenceHarvester.class.getSimpleName())) {
                            String maximumIdString = currentDataSourceElement.elementText("maximumId");
                            Long maximumId = (maximumIdString != null && !maximumIdString.isEmpty() ? Long.valueOf(maximumIdString) : null);

                            harvestMethod = new IdSequenceHarvester(target, maximumId);
                        }

                        dataSource = new DataSourceZ3950(dataProvider, id, description, schema, namespace, harvestMethod, recordIdPolicy, metadataTransformations);
                    } else {
                        throw new RuntimeException("Loading configuration from Data Source of type " + dataSourceType + " not implemented");
                    }

                    parseOldTasks(dataSource);

                    boolean removeSynFile = false;
                    if (dataSource != null) {
                        // Add generic Data Source Data
                        if (status != null) {
                            if (status.equals(DataSource.StatusDS.OK.toString())) {
                                dataSource.setStatus(DataSource.StatusDS.OK);
                            } else if (status.equals(DataSource.StatusDS.ERROR.toString())) {
                                dataSource.setStatus(DataSource.StatusDS.ERROR);
                            } else if (status.equals(DataSource.StatusDS.WARNING.toString())) {
                                dataSource.setStatus(DataSource.StatusDS.WARNING);
                            } else if (status.equals(DataSource.StatusDS.RUNNING.toString())) {
                                dataSource.setStatus(DataSource.StatusDS.RUNNING);
                            } else if (status.equals(DataSource.StatusDS.CANCELED.toString())) {
                                dataSource.setStatus(DataSource.StatusDS.CANCELED);
                            } else if (status.equals(DataSource.StatusDS.PRE_PROCESS_ERROR.toString())) {
                                dataSource.setStatus(DataSource.StatusDS.PRE_PROCESS_ERROR);
                            } else if (status.equals(DataSource.StatusDS.POST_PROCESS_ERROR.toString())) {
                                dataSource.setStatus(DataSource.StatusDS.POST_PROCESS_ERROR);
                            }
                        }

                        if (lastIngest == null) {
                            // dataProviders.xml old version
                            lastIngest = dataSource.getSynchronizationDate(new File(new File(repositoryPath, id), "synchronization-date.txt"));
                            removeSynFile = true;
                        }
                        if (!lastIngest.equals("")) {
                            try {
                                dataSource.setLastUpdate(DateUtil.string2Date(lastIngest, TimeUtil.LONG_DATE_FORMAT));
                            } catch (ParseException e) {
                                dataSource.setLastUpdate(DateUtil.string2Date(lastIngest, TimeUtil.SHORT_DATE_FORMAT));
                            }
                        }

                        if (sample == null) {
                            // dataProviders.xml old version
                            sample = String.valueOf(dataSource.getSampleNumber(new File(new File(repositoryPath, id), "synchronization-date.txt")));
                            removeSynFile = true;
                        }
                        if (!sample.equals("")) {
                            dataSource.setMaxRecord4Sample(Integer.valueOf(sample));
                        }

                        if (removeSynFile) {
                            // remove file synchronization-date.txt file
                            File dataSourceSyncDate = new File(new File(repositoryPath, dataSource.getId()), "synchronization-date.txt");
                            if (dataSourceSyncDate.exists()) {
                                boolean result = dataSourceSyncDate.delete();
                                if (result)
                                    log.info("Deleted synchronization-date.txt file with success from Data Source with id " + id);
                                else
                                    log.error("Unable to delete synchronization-date.txt file from Data Source with id " + id);
                            }
                        }

                        String marcFormat = currentDataSourceElement.attributeValue("marcFormat");
                        if (marcFormat != null && !marcFormat.isEmpty())
                            dataSource.setMarcFormat(marcFormat);

                        // Load external services data
                        List servicesList = currentDataSourceElement.selectNodes("restServices/restService");
                        String executeType = currentDataSourceElement.valueOf("restServices/@executeType");
                        if (executeType != null && !executeType.isEmpty())
                            dataSource.setExternalServicesRunType(ExternalServiceStates.ContainerType.valueOf(executeType));

                        for (Object node : servicesList) {
                            Node n = (Node)node;
                            String serviceId = n.valueOf("@id");
                            String serviceType = n.valueOf("@type");
                            String serviceUri = n.valueOf("@uri");
                            String serviceStatusUri = n.valueOf("@statusUri");
                            String externalResultsUri = n.valueOf("@externalResultsUri");
                            String serviceName = n.valueOf("@name");
                            String externalServiceType = n.valueOf("@externalServiceType");
                            String enabled = n.valueOf("@isEnabled");
                            boolean isEnabled = Boolean.valueOf((enabled == null || enabled.isEmpty()) ? "true" : enabled);
                            if (externalServiceType == null || externalServiceType.isEmpty())
                                externalServiceType = ExternalServiceType.MONITORED.name();

                            ExternalRestService externalRestService;
                            if (externalServiceType.equals(ExternalServiceType.NO_MONITOR.name())) {
                                externalRestService = new ExternalServiceNoMonitor(serviceId, serviceName, serviceUri, dataSource);
                            } else {
                                externalRestService = new ExternalRestService(serviceId, serviceName, serviceUri, serviceStatusUri, serviceType, ExternalServiceType.valueOf(externalServiceType));
                            }
                            externalRestService.setEnabled(isEnabled);
                            if (externalResultsUri != null && !externalResultsUri.isEmpty())
                                externalRestService.setExternalResultsUri(externalResultsUri);

                            List parametersList = n.selectNodes("parameters/parameter");
                            for (Object nodeP : parametersList) {
                                Node parameterNode = (Node)nodeP;
                                String parameterName = parameterNode.valueOf("@name");
                                String parameterValue = parameterNode.valueOf("@value");
                                String parameterType = parameterNode.valueOf("@type");
                                boolean parameterRequired = Boolean.parseBoolean(parameterNode.valueOf("@required"));
                                String exampleStr = parameterNode.valueOf("@example");
                                String semantics = parameterNode.valueOf("@semantics");
                                ServiceParameter serviceParameter = new ServiceParameter(parameterName, parameterType, parameterRequired, exampleStr, semantics);
                                serviceParameter.setValue(parameterValue);
                                externalRestService.getServiceParameters().add(serviceParameter);
                            }
                            dataSource.getExternalRestServices().add(externalRestService);
                        }

                        loadDataSourceTags(currentDataSourceElement, dataSource);

                        // Create DataSourceContainer
                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(dataSource, dataSourceContainerNameCode, dataSourceContainerName, "");

                        // export path
                        if (currentDataSourceElement.elementText("exportDirPath") != null && !currentDataSourceElement.elementText("exportDirPath").isEmpty())
                            dataSource.setExportDir(currentDataSourceElement.elementText("exportDirPath"));
                        else {
                            File newExportDir = new File(defaultExportDir.getAbsolutePath() + File.separator + dataSource.getId() + File.separator + "export");
                            //                            FileUtils.forceMkdir(newExportDir);
                            dataSource.setExportDir(newExportDir.getAbsolutePath());
                        }

                        // Create new MDR schema if it doesn't exist
                        boolean exists = metadataSchemaManager.schemaExists(metadataFormat);

                        if (!exists) {
                            List<MetadataSchemaVersion> metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
                            metadataSchemaVersions.add(new MetadataSchemaVersion(1.0, schema));
                            metadataSchemaManager.saveMetadataSchema(null, metadataFormat, null, namespace, null, null, metadataSchemaVersions, true);
                        }

                        dataSource.setIsSample(isSample);

                        dataProvider.getDataSourceContainers().put(dataSource.getId(), dataSourceContainer);
                    }
                }
                aggregator.addDataProvider(dataProvider);
            }
            aggregatorsLoaded.add(aggregator);
        }
        //save new dataProviders.xml format
        if (aggregators != null && aggregators.size() > 0) {
            aggregators.addAll(aggregatorsLoaded);
        } else {
            aggregators = aggregatorsLoaded;
        }
        saveData();

        return aggregatorsLoaded;
    }

    protected void loadDataSourceTags(Element currentDataSourceElement, DataSource dataSource) {
        List<Node> servicesList = currentDataSourceElement.selectNodes("tags/tag");
        for (Node tagNode : servicesList) {
            String name = tagNode.valueOf("@name");
            dataSource.getTags().add(new DataSourceTag(name));
        }
    }

    private void parseOldTasks(DataSource dataSource) {
        if (oldTasksFile.exists()) {
            try {
                SAXReader reader = new SAXReader();
                Document recordsDocument = reader.read(oldTasksFile);
                List list = recordsDocument.selectNodes("//old-tasks/oldTask");

                for (Object node : list) {
                    Node n = (Node)node;
                    String dataSet = n.valueOf("dataSourceSet");
                    if (dataSet.equals(dataSource.getId())) {
                        String id = n.valueOf("@id");
                        String logName = n.valueOf("logName");
                        String ingestType = n.valueOf("ingestType");
                        String status = n.valueOf("status");
                        String retries = n.valueOf("retries");
                        String maxRetries = n.valueOf("retries/@max");
                        String retriesDelay = n.valueOf("retries/@delay");
                        String time = n.valueOf("time");
                        String records = n.valueOf("records");
                        OldTask oldTask = new OldTask(dataSource, id, logName, ingestType, status, retries, maxRetries, retriesDelay, time, records);

                        dataSource.getOldTasksList().add(oldTask);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.error("The old taks file does not exist.");
        }
    }

    public void removeOldTasks(String dataSourceId) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(oldTasksFile);
            List list = document.selectNodes("//old-tasks/oldTask");

            for (Object node : list) {
                Node n = (Node)node;
                String dataSet = n.valueOf("dataSourceSet");
                if (dataSet.equals(dataSourceId))
                    n.detach();
            }

            XMLWriter output = new XMLWriter(new FileWriter(new File(oldTasksFile.getPath())));
            output.write(document);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /******************************************************************************************************************/
    /** AGGREGATOR ****************************************************************************************************/
    /******************************************************************************************************************/
    /**
     * Create new Aggregator
     * @param name
     * @param nameCode
     * @param homepageUrl
     * @throws IOException
     * @throws DocumentException
     * @return MessageType
     * @throws InvalidArgumentsException 
     * @throws AlreadyExistsException 
     */
    public Aggregator createAggregator(String name, String nameCode, String homepageUrl) throws DocumentException, IOException, InvalidArgumentsException, AlreadyExistsException {
        Aggregator newAggregator = new Aggregator();
        if (homepageUrl != null && !homepageUrl.equals("")) {
            try {
                String generatedHomepageUrl = null;
                if (!homepageUrl.startsWith("http://") && !homepageUrl.startsWith("https://")) {
                    generatedHomepageUrl = "http://" + homepageUrl;
                }
                // test if URL is valid
                if (!FileUtilSecond.checkUrl(generatedHomepageUrl)) {
                    throw new Exception();
                }
                newAggregator.setHomePage(new URL(generatedHomepageUrl));
            } catch (Exception e) {
                throw new InvalidArgumentsException(homepageUrl);
            }
        }
        newAggregator.setName(name);
        newAggregator.setNameCode(nameCode);
        newAggregator.setId(Aggregator.generateId(newAggregator.getName()));

        if (!checkIfAggregatorExists(aggregators, newAggregator)) {
            aggregators.add(newAggregator);
            saveData();
            return newAggregator;
        } else //This basically happens if and aggregator already exists with both name and nameCode the same as the one provided
            throw new AlreadyExistsException(newAggregator.getName());
    }

    /**
     * Update Aggregator
     * @param oldAggregatorId
     * @param name
     * @param nameCode
     * @param homepageUrl
     * @throws IOException
     * @throws DocumentException
     * @return MessageType
     * @throws ObjectNotFoundException 
     * @throws InvalidArgumentsException 
     */
    public Aggregator updateAggregator(String oldAggregatorId, String name, String nameCode, String homepageUrl) throws IOException, DocumentException, ObjectNotFoundException, InvalidArgumentsException {
        Aggregator aggregator = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).getAggregator(oldAggregatorId);

        if (aggregator != null) {
            // only not null fields are updated

            if (name != null)
                aggregator.setName(name);

            if (nameCode != null)
                aggregator.setNameCode(nameCode);

            if (homepageUrl != null && !homepageUrl.equals("")) {
                try {
                    if (!homepageUrl.startsWith("http://") && !homepageUrl.startsWith("https://")) {
                        homepageUrl = "http://" + homepageUrl;
                    }
                    // test if URL is valid
                    if (!FileUtilSecond.checkUrl(homepageUrl)) {
                        throw new Exception();
                    }
                    aggregator.setHomePage(new URL(homepageUrl));
                } catch (Exception e) {
                    throw new InvalidArgumentsException(homepageUrl);
                }
            }
            /*else{
                aggregatorEuropeana.setHomePage(null);
            }*/

            for (Aggregator actualAggregator : aggregators) {
                if (actualAggregator.getId().equals(oldAggregatorId)) {
                    aggregators.remove(actualAggregator);
                    break;
                }
            }
            aggregators.add(aggregator);
            saveData();
            return aggregator;
        } else {
            throw new ObjectNotFoundException(oldAggregatorId);
        }
    }

    /**
     * Delete aggregator from REPOX
     * @param aggregatorId
     * @throws IOException
     * @throws DocumentException
     * @throws ObjectNotFoundException 
     */
    public void deleteAggregator(String aggregatorId) throws IOException, DocumentException, ObjectNotFoundException {
        Aggregator aggregator = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).getAggregator(aggregatorId);

        if (aggregator != null) {
            for (Aggregator actualAggregator : aggregators) {
                if (actualAggregator.getId().equals(aggregatorId)) {

                    Iterator iteratorDP = actualAggregator.getDataProviders().iterator();
                    while (iteratorDP.hasNext()) {
                        DataProvider dataProvider = (DataProvider)iteratorDP.next();

                        //                        // remove all data sources container from data provider
                        //                        for (DataSourceContainer dataSourceContainer : dataProvider.getDataSourceContainers().values()) {
                        //                            deleteDataSource(dataSourceContainer.getDataSource().getId());
                        //                        }
                        //                        dataProvider.getDataSourceContainers().clear();
                        Object[] list = dataProvider.getDataSourceContainers().keySet().toArray();
                        for (int i = list.length - 1; i > -1; i--) {
                            deleteDataSource((String)list[i]);
                        }

                        // remove dataProvider from dataProvider's list
                        iteratorDP.remove();
                    }
                    aggregators.remove(actualAggregator);
                    saveData();
                    return;
                }
            }
        }
        throw new ObjectNotFoundException(aggregatorId);
    }

    /**
     *
     * @param aggregatorId
     * @return Gets the Aggregator with aggregatorId from the configuration file if it exists or null otherwise.
     * @throws DocumentException
     * @throws IOException
     */
    public Aggregator getAggregator(String aggregatorId) {
        for (Aggregator currentAggregator : aggregators) {
            if (currentAggregator.getId().equals(aggregatorId)) {
                return currentAggregator;
            }
        }
        return null;
    }

    /**
     * Check if a specific aggregator already exists in REPOX (use name and nameCode attributes)
     * @param aggregators
     * @param aggregatorToCheck
     * @return boolean
     */
    private boolean checkIfAggregatorExists(List<Aggregator> aggregators, Aggregator aggregatorToCheck) {
        for (Aggregator aggregator : aggregators) {
            if (aggregatorToCheck.getName().equalsIgnoreCase(aggregator.getName()))
                if (aggregatorToCheck.getNameCode() != null && aggregator.getNameCode() != null)
                    if (aggregatorToCheck.getNameCode().equalsIgnoreCase(aggregator.getNameCode()))
                        return true;
        }
        return false;
    }

    public synchronized List<Aggregator> getAggregators() throws DocumentException, IOException {
        return Collections.unmodifiableList(aggregators);
    }

    /******************************************************************************************************************/
    /** DATA PROVIDER's ***********************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Add a new Data Provider
     * @param aggregatorId
     * @param name
     * @param country
     * @param description
     * @param nameCode
     * @param url
     * @param dataSetType
     * @throws IOException
     * @throws DocumentException
     * @return MessageType
     */
    public DataProvider createDataProvider(String aggregatorId, String name, String country, String description, String nameCode, String url, String dataSetType) throws ObjectNotFoundException, AlreadyExistsException, IOException, InvalidArgumentsException {
        Aggregator aggregator = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).getAggregator(aggregatorId);

        if (aggregator != null) {
            DefaultDataProvider newDataProvider = new DefaultDataProvider();
            newDataProvider.setName(name);
            newDataProvider.setCountry(country);
            newDataProvider.setDescription(description);
            newDataProvider.setNameCode(nameCode);

            if (url != null && !url.equals("")) {
                try {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    // test if URL is valid
                    if (!FileUtilSecond.checkUrl(url)) {
                        throw new Exception();
                    }
                    newDataProvider.setHomePage(new URL(url));

                } catch (Exception e) {
                    throw new InvalidArgumentsException(url);
                }
            }
            try {
                newDataProvider.setProviderType(ProviderType.get(dataSetType));
            } catch (Exception e) {
                throw new InvalidArgumentsException(dataSetType);
            }
            newDataProvider.setDataSourceContainers(new HashMap<String, DataSourceContainer>());

            if (nameCode != null && (DefaultDataProvider)getDataProvider(nameCode) == null) {
                // asked by TEL (but first checks if the dataProvider with that specific ID does not exist
                newDataProvider.setId(nameCode);
            } else {
                newDataProvider.setId(DataProvider.generateId(newDataProvider.getName()));
            }

            for (Aggregator currentAggregator : aggregators) {
                if (currentAggregator.getId().equals(aggregatorId)) {
                    if (checkIfDataProviderExists(aggregatorId, newDataProvider)) {
                        throw new AlreadyExistsException(aggregatorId);
                    }
                    currentAggregator.addDataProvider(newDataProvider);
                    break;
                }
            }
            saveData();
            return newDataProvider;
        } else {
            throw new ObjectNotFoundException(aggregatorId);
        }
    }

    /**
     * Add a new Data Provider (used by REST)
     * @param aggregatorId
     * @param id
     * @param name
     * @param country
     * @param description
     * @param nameCode
     * @param url
     * @param dataSetType
     * @return
     * @throws ObjectNotFoundException
     * @throws AlreadyExistsException
     * @throws IOException
     * @throws InvalidArgumentsException
     */
    public DataProvider createDataProvider(String aggregatorId, String id, String name, String country, String description, String nameCode, String url, String dataSetType) throws ObjectNotFoundException, AlreadyExistsException, IOException, InvalidArgumentsException {
        Aggregator aggregator = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).getAggregator(aggregatorId);

        if (aggregator != null) {
            DefaultDataProvider newDataProvider = new DefaultDataProvider();
            newDataProvider.setName(name);
            newDataProvider.setCountry(country);
            newDataProvider.setDescription(description);
            newDataProvider.setNameCode(nameCode);

            if (url != null && !url.equals("")) {
                try {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    // test if URL is valid
                    if (!FileUtilSecond.checkUrl(url)) {
                        throw new Exception();
                    }
                    newDataProvider.setHomePage(new URL(url));

                } catch (Exception e) {
                    throw new InvalidArgumentsException(url);
                }
            }
            try {
                newDataProvider.setProviderType(ProviderType.get(dataSetType));
            } catch (Exception e) {
                throw new InvalidArgumentsException(dataSetType);
            }
            newDataProvider.setDataSourceContainers(new HashMap<String, DataSourceContainer>());
            newDataProvider.setId(id);

            for (Aggregator currentAggregator : aggregators) {
                if (currentAggregator.getId().equals(aggregatorId)) {
                    if (checkIfDataProviderExists(aggregatorId, newDataProvider)) {
                        throw new AlreadyExistsException(aggregatorId);
                    }
                    currentAggregator.addDataProvider(newDataProvider);
                    break;
                }
            }
            saveData();
            return newDataProvider;
        } else {
            throw new ObjectNotFoundException(aggregatorId);
        }
    }

    public boolean moveDataProvider(String newAggregatorId, String idDataProvider2Move) throws IOException {
        DefaultDataProvider dataProvider = (DefaultDataProvider)getDataProvider(idDataProvider2Move);
        Aggregator aggregatorParent = getAggregatorParent(dataProvider.getId());

        if (aggregatorParent.getId().equals(newAggregatorId)) {
            return false;
        }

        for (Aggregator currentAggregator : aggregators) {
            if (currentAggregator.getId().equals(newAggregatorId)) {
                currentAggregator.addDataProvider(dataProvider);
                aggregatorParent.getDataProviders().remove(dataProvider);

                saveData();

                return true;
            }
        }
        return false;
    }

    public boolean moveDataSource(String newDataProviderID, String idDataSource2Move) throws IOException, DocumentException {
        DataSourceContainer dataSourceContainer = getDataSourceContainer(idDataSource2Move);
        DefaultDataProvider dataProviderParent = (DefaultDataProvider)getDataProviderParent(dataSourceContainer.getDataSource().getId());

        if (dataProviderParent.getId().equals(newDataProviderID)) {
            return false;
        }

        for (Aggregator currentAggregator : aggregators) {
            for (DefaultDataProvider currentDataProvider : currentAggregator.getDataProviders()) {
                if (currentDataProvider.getId().equals(newDataProviderID)) {
                    currentDataProvider.getDataSourceContainers().put(dataSourceContainer.getDataSource().getId(), dataSourceContainer);
                    dataProviderParent.getDataSourceContainers().remove(dataSourceContainer.getDataSource().getId());

                    saveData();

                    return true;
                }
            }
        }
        return false;
    }

    public void setDataSetSampleState(boolean isSample, DataSource dataSource) {
        dataSource.setIsSample(isSample);
        try {
            saveData();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public synchronized MessageType removeTransformationFromDataSource(String transformationId) {
        try {
            HashMap<String, DataSourceContainer> dataSourceContainers = loadDataSourceContainers();

            for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
                DataSource currentDataSource = dataSourceContainer.getDataSource();
                MetadataTransformation metadataTransformation = currentDataSource.getMetadataTransformations().get(transformationId);
                if (metadataTransformation != null) {
                    currentDataSource.getMetadataTransformations().remove(metadataTransformation.getId());
                }
            }
            saveData();
            return MessageType.OK;
        } catch (DocumentException e) {
            return MessageType.OTHER;
        } catch (IOException e) {
            return MessageType.OTHER;
        }
    }

    @Deprecated
    public DataProvider createDataProvider(String aggregatorId, String name, String country) {
        return null;
    }

    @Deprecated
    public DataProvider updateDataProvider(String id, String name, String country, String description) {
        return null;
    }

    /**
     * Update Data Provider
     * @param id
     * @param name
     * @param country
     * @param description
     * @param nameCode
     * @param url
     * @param dataSetType
     * @throws IOException
     * @throws DocumentException
     * @return MessageType
     */
    public DataProvider updateDataProvider(String id, String name, String country, String description, String nameCode, String url, String dataSetType) throws ObjectNotFoundException, InvalidArgumentsException, IOException {
        DefaultDataProvider dataProvider = (DefaultDataProvider)getDataProvider(id);

        if (dataProvider != null) {
            // only not null fields are updated

            if (name != null)
                dataProvider.setName(name);
            if (country != null)
                dataProvider.setCountry(country);
            if (description != null)
                dataProvider.setDescription(description);
            if (nameCode != null)
                dataProvider.setNameCode(nameCode);

            if (url != null && !url.equals("")) {
                try {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    // test if URL is valid
                    if (!FileUtilSecond.checkUrl(url)) {
                        throw new InvalidArgumentsException(url);
                    }
                    dataProvider.setHomePage(new URL(url));
                } catch (Exception e) {
                    throw new InvalidArgumentsException(url);
                }
            }
            /*else{
                dataProvider.setHomePage(null);
            }*/

            if (dataSetType != null) {
                try {
                    dataProvider.setProviderType(ProviderType.get(dataSetType));
                } catch (Exception e) {
                    throw new InvalidArgumentsException(dataSetType);
                }
            }
            return updateDataProvider(dataProvider, dataProvider.getId());
        } else {
            throw new ObjectNotFoundException(id);
        }
    }

    /**
     * Delete data provider from REPOX
     * @param dataProviderId
     * @return MessageType
     */
    public synchronized void deleteDataProvider(String dataProviderId) throws ObjectNotFoundException, IOException {
        DefaultDataProvider dataProvider2Delete = (DefaultDataProvider)getDataProvider(dataProviderId);

        if (dataProvider2Delete != null) {

            for (Aggregator currentAggregator : aggregators) {

                Iterator iteratorDP = currentAggregator.getDataProviders().iterator();
                while (iteratorDP.hasNext()) {
                    DataProvider dataProvider = (DataProvider)iteratorDP.next();

                    if (dataProvider.getId().equals(dataProviderId)) {
                        // remove all data sources container from data provider

                        Object[] list = dataProvider.getDataSourceContainers().keySet().toArray();
                        for (int i = list.length - 1; i > -1; i--) {
                            deleteDataSource((String)list[i]);
                        }

                        //                        Iterator iteratorDS = dataProvider.getDataSourceContainers().values().iterator();
                        //                        while (iteratorDS.hasNext()) {
                        //                            DataSourceContainer dataSourceContainer = (DataSourceContainer)iteratorDS.next();
                        //                            deleteDataSource(dataSourceContainer.getDataSource().getId());
                        //                        }
                        //                        dataProvider.getDataSourceContainers().clear();

                        // remove dataProvider from dataProvider's list
                        iteratorDP.remove();

                        saveData();
                        return;
                    }
                }
            }
        }
        throw new ObjectNotFoundException(dataProviderId);
    }

    public DataProvider updateDataProvider(DataProvider dataProvider, String oldDataProviderId) throws IOException, ObjectNotFoundException {
        DefaultDataProvider defaultDataProvider = (DefaultDataProvider)dataProvider;
        Aggregator aggregatorParent = getAggregatorParent(defaultDataProvider.getId());

        for (Aggregator currentAggregator : aggregators) {
            if (currentAggregator.getId().equals(aggregatorParent.getId())) {
                Iterator<DefaultDataProvider> iteratorDataProvider = currentAggregator.getDataProviders().iterator();
                while (iteratorDataProvider.hasNext()) {
                    DataProvider currentDataProvider = iteratorDataProvider.next();
                    if (currentDataProvider.getId().equals(oldDataProviderId)) {
                        iteratorDataProvider.remove();
                        currentAggregator.getDataProviders().add(defaultDataProvider);
                        saveData();
                        return dataProvider;
                    }
                }

            }
        }
        throw new ObjectNotFoundException(oldDataProviderId);
    }

    /**
     * Check if data provider exists inside a aggregator
     * @param aggregatorId
     * @param dataProvider
     * @return boolean
     */
    private boolean checkIfDataProviderExists(String aggregatorId, DataProvider dataProvider) {
        for (Aggregator currentAggregator : aggregators) {
            if (currentAggregator.getId().equals(aggregatorId)) {
                for (DefaultDataProvider currentDataProvider : currentAggregator.getDataProviders()) {
                    if (currentDataProvider.getId().equals(dataProvider.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<DataProvider> getDataProviders() throws DocumentException, IOException {
        List<DataProvider> dataProvidersList = new ArrayList<DataProvider>();
        for (Aggregator aggregator : aggregators) {
            dataProvidersList.addAll(aggregator.getDataProviders());
        }
        return dataProvidersList;
    }

    public DataProvider getDataProvider(String dataProviderId) {
        for (Aggregator aggregator : aggregators) {
            for (DefaultDataProvider dataProvider : aggregator.getDataProviders()) {
                if (dataProvider.getId().equals(dataProviderId)) {
                    return dataProvider;
                }
            }
        }
        return null;
    }

    /**
     * Used by REST service and REPOX GUI
     * @param aggregatorId
     * @param name
     * @return DataProvider
     */
    public DataProvider getDataProvider(String aggregatorId, String name) {
        for (Aggregator aggregator : aggregators) {
            if (aggregator.getId().equals(aggregatorId)) {
                for (DefaultDataProvider dataProvider : aggregator.getDataProviders()) {
                    if (dataProvider.getName().equals(name))
                        return dataProvider;
                }
            }
        }
        return null;
    }

    public DataProvider getDataProviderParent(String dataSourceId) {
        for (Aggregator aggregator : aggregators) {
            for (DefaultDataProvider dataProvider : aggregator.getDataProviders()) {
                DataSourceContainer dataSourceContainer = dataProvider.getDataSourceContainers().get(dataSourceId);
                if (dataSourceContainer != null) {
                    return dataProvider;
                }
            }
        }
        return null;
    }

    public Aggregator getAggregatorParent(String dataProviderId) {
        for (Aggregator aggregator : aggregators) {
            for (DefaultDataProvider dataProvider : aggregator.getDataProviders()) {
                if (dataProvider.getId().equals(dataProviderId)) {
                    return aggregator;
                }
            }
        }
        return null;
    }

    /******************************************************************************************************************/
    /** DATA SOURCE CONTAINER *****************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Add Data Source Container
     * @param dataSourceContainer
     * @param dataProviderId
     * @return MessageType
     * @throws IOException
     * @throws DocumentException
     */
    public MessageType addDataSourceContainer(DataSourceContainer dataSourceContainer, String dataProviderId) {
        try {
            for (Aggregator currentAggregator : aggregators) {
                for (DataProvider dataProvider : currentAggregator.getDataProviders()) {
                    if (dataProvider.getId().equals(dataProviderId)) {
                        dataProvider.getDataSourceContainers().put(dataSourceContainer.getDataSource().getId(), dataSourceContainer);
                        // todo add dataSource to databases
                        break;
                    }
                }
            }
            saveData();
            return MessageType.OK;
        } catch (IOException e) {
            return MessageType.OTHER;
        }
    }

    public synchronized MessageType deleteDataSource(String dataSourceId) throws ObjectNotFoundException {
        DataProvider dataProviderParent = getDataProviderParent(dataSourceId);

        if (dataProviderParent == null)
            throw new ObjectNotFoundException(dataSourceId);

        DefaultDataSourceContainer dataSourceContainer = (DefaultDataSourceContainer)dataProviderParent.getDataSourceContainers().get(dataSourceId);

        if (dataSourceContainer != null) {
            DataSource currentDataSource = dataSourceContainer.getDataSource();
            boolean successfulDeletion = true;
            log.info("Deleting Data Source with id " + dataSourceId);

            // remove files from z39.50 - IdListHarvester
            File idListFilePermanent;
            if ((currentDataSource instanceof DataSourceZ3950) && (((DataSourceZ3950)currentDataSource).getHarvestMethod() instanceof IdListHarvester)) {
                String pathFile = ((IdListHarvester)((DataSourceZ3950)currentDataSource).getHarvestMethod()).getIdListFile().getAbsolutePath();
                idListFilePermanent = new File(pathFile);
                boolean success = idListFilePermanent.delete();
                if (!success) {
                    log.error("Error deleting the file: " + idListFilePermanent.getAbsolutePath());
                }
            }

            //Delete AccessPoints
            for (AccessPoint accessPoint : currentDataSource.getAccessPoints().values()) {
                try {
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().deleteIndex(accessPoint);
                    log.info("Deleted AccessPoint with id " + accessPoint.getId());
                } catch (Exception e) {
                    log.error("Unable to delete Table from Database: " + accessPoint.getId() + " (" + e.getMessage() + ")", e);
                    successfulDeletion = false;
                }
            }

            //Delete repository dir
            try {
                File dataSourceDir = currentDataSource.getDataSourceDir();
                try {
                    FileUtils.deleteDirectory(dataSourceDir);
                    log.info("Deleted Data Source dir with success from Data Source with id " + dataSourceId);
                } catch (IOException e) {
                    log.error("Unable to delete Data Source dir from Data Source with id " + dataSourceId);
                }
            } catch (Exception e) {
                log.error("Unable to delete Data Source dir from Data Source with id " + dataSourceId);
                successfulDeletion = false;
            }

            if (successfulDeletion) {
                try {
                    //Delete Record Counts cache
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().removeDataSourceCounts(dataSourceId);

                    //Delete Scheduled Tasks
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().deleteDataSourceTasks(dataSourceId);

                    // Delete Old Tasks
                    removeOldTasks(dataSourceId);

                    // todo test if it's removing...
                    dataProviderParent.getDataSourceContainers().remove(dataSourceId);

                    saveData();
                    return MessageType.OK;
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                return MessageType.OTHER;
            } else {
                return MessageType.OTHER;
            }
        } else {
            return MessageType.NOT_FOUND;
        }
    }

    public HashMap<String, DataSourceContainer> loadDataSourceContainers() throws DocumentException, IOException {
        HashMap<String, DataSourceContainer> allDataSourceContainers = new HashMap<String, DataSourceContainer>();

        for (Aggregator aggregator : aggregators) {
            for (DataProvider dataProvider : aggregator.getDataProviders()) {
                allDataSourceContainers.putAll(dataProvider.getDataSourceContainers());
            }
        }
        return allDataSourceContainers;
    }

    public void deleteDataSourceContainer(String dataSourceId) throws IOException, ObjectNotFoundException {
        deleteDataSource(dataSourceId);

        for (Aggregator aggregator : aggregators) {
            for (DataProvider currentDataProvider : aggregator.getDataProviders()) {
                DataSourceContainer dataSourceContainer = currentDataProvider.getDataSourceContainers().get(dataSourceId);

                if (dataSourceContainer != null) {
                }
                currentDataProvider.getDataSourceContainers().remove(dataSourceId);
            }
        }
        saveData();
    }

    public DataSourceContainer getDataSourceContainer(String dataSourceId) throws DocumentException, IOException {
        for (Aggregator aggregator : aggregators) {
            for (DataProvider currentDataProvider : aggregator.getDataProviders()) {
                if (currentDataProvider.getDataSourceContainers() != null && currentDataProvider.getDataSourceContainers().size() > 0) {
                    DataSourceContainer dataSourceContainer = currentDataProvider.getDataSourceContainers().get(dataSourceId);
                    if (dataSourceContainer != null) {
                        return dataSourceContainer;
                    }
                }
            }
        }
        return null;
    }

    public MessageType updateDataSourceContainer(DataSourceContainer dataSourceContainer, String oldDataSourceId) {
        try {
            for (Aggregator aggregator : aggregators) {
                for (DataProvider currentDataProvider : aggregator.getDataProviders()) {
                    DataSourceContainer currentDataSourceContainer = currentDataProvider.getDataSourceContainers().get(oldDataSourceId);

                    if (currentDataSourceContainer != null) {
                        if (!dataSourceContainer.getDataSource().getId().equals(oldDataSourceId)) {
                            // database - update id
                            updateDataSourceId(oldDataSourceId, dataSourceContainer.getDataSource().getId());
                        }
                        currentDataProvider.getDataSourceContainers().remove(oldDataSourceId).getDataSource();
                        currentDataProvider.getDataSourceContainers().put(dataSourceContainer.getDataSource().getId(), dataSourceContainer);
                        // update the data source container HashMap
                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(currentDataProvider.getDataSourceContainers());

                        saveData();
                        break;
                    }
                }
            }
            return MessageType.OK;
        } catch (SQLException e) {
            return MessageType.OTHER;
        } catch (DocumentException e) {
            return MessageType.OTHER;
        } catch (IOException e) {
            return MessageType.OTHER;
        }
    }

    /**
     * Updates the Data Source with id newDataSourceId from oldDataSourceId.
     *
     * @param oldDataSourceId, newDataSourceId
     * @return true if deletion was completely successful, false otherwise.
     * @throws DocumentException
     */
    protected void updateDataSourceId(String oldDataSourceId, String newDataSourceId) throws IOException, DocumentException, SQLException {
        DataSource dataSource = getDataSource(oldDataSourceId);
        // dataSource.initAccessPoints();

        log.info("Updating Data Source with id " + oldDataSourceId + " to id " + newDataSourceId);
        DefaultAccessPointsManager accessPointsManager = (DefaultAccessPointsManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager();

        //Update Access Points
        AccessPoint defaultTimestampAP = dataSource.getAccessPoints().get(AccessPoint.PREFIX_INTERNAL_BD + oldDataSourceId + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD);
        accessPointsManager.updateDataSourceAccessPoint(dataSource, defaultTimestampAP.typeOfIndex(), AccessPoint.PREFIX_INTERNAL_BD + oldDataSourceId + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD, AccessPoint.PREFIX_INTERNAL_BD + newDataSourceId + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD);
        log.info("Updated AccessPoint with id " + AccessPoint.PREFIX_INTERNAL_BD + oldDataSourceId + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD + " to " + AccessPoint.PREFIX_INTERNAL_BD + newDataSourceId + AccessPoint.SUFIX_TIMESTAMP_INTERNAL_BD);

        AccessPoint defaultRecordAP = dataSource.getAccessPoints().get(AccessPoint.PREFIX_INTERNAL_BD + oldDataSourceId + AccessPoint.SUFIX_RECORD_INTERNAL_BD);
        accessPointsManager.updateDataSourceAccessPoint(dataSource, defaultRecordAP.typeOfIndex(), AccessPoint.PREFIX_INTERNAL_BD + oldDataSourceId + AccessPoint.SUFIX_RECORD_INTERNAL_BD, AccessPoint.PREFIX_INTERNAL_BD + newDataSourceId + AccessPoint.SUFIX_RECORD_INTERNAL_BD);
        log.info("Updated AccessPoint with id " + AccessPoint.PREFIX_INTERNAL_BD + oldDataSourceId + AccessPoint.SUFIX_RECORD_INTERNAL_BD + " to " + AccessPoint.PREFIX_INTERNAL_BD + newDataSourceId + AccessPoint.SUFIX_RECORD_INTERNAL_BD);

        // update dataSet info
        dataSource.setId(newDataSourceId);

        // update access points
        dataSource.getAccessPoints().clear();
        dataSource.initAccessPoints();

        //Rename Folder
        dataSource.renameDataSourceDir(oldDataSourceId, newDataSourceId);
        log.info("Renamed Data Source (with new Id " + newDataSourceId + ") Repository Dir");

        //Update Record Counts cache
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().renameDataSourceCounts(oldDataSourceId, newDataSourceId);

        //Update Scheduled Tasks
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().updateDataSourceTasks(oldDataSourceId, newDataSourceId);
    }

    /**
     * Gets the DataSource with id dataSourceId
     *
     * @param dataSourceId
     * @return DataSource with id dataSourceId if exists, null otherwise
     * @throws DocumentException
     * @throws IOException
     */
    protected synchronized DataSource getDataSource(String dataSourceId) throws DocumentException, IOException {
        for (Aggregator aggregator : aggregators) {
            for (DataProvider currentDataProvider : aggregator.getDataProviders()) {
                DataSource dataSource = currentDataProvider.getDataSource(dataSourceId);
                if (dataSource != null) {
                    return dataSource;
                }
            }
        }
        return null;
    }

    /**
     * Create OAI-PMH data source
     * @param dataProviderId
     * @param id
     * @param description
     * @param nameCode
     * @param name
     * @param exportPath
     * @param schema
     * @param namespace
     * @param metadataFormat
     * @param metadataTransformations
     * @return MessageType
     * @throws DocumentException
     * @throws IOException
     */
    public DataSource createDataSourceSruRecordUpdate(String dataProviderId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, Map<String, MetadataTransformation> metadataTransformations,
            List<ExternalRestService> externalRestServices, String marcFormat) throws DocumentException, IOException, InvalidArgumentsException, ObjectNotFoundException, AlreadyExistsException, SQLException {
        if (getDataSourceContainer(id) == null) {
            if (isIdValid(id)) {
                DataProvider dataProvider = getDataProvider(dataProviderId);

                if (dataProvider != null) {

                    DataSource newDataSource = new SruRecordUpdateDataSource(dataProvider, id, description, schema, namespace, metadataFormat, new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());

                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                    dataProvider.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                    newDataSource.initAccessPoints();
                    newDataSource.setMetadataTransformations(metadataTransformations);
                    newDataSource.setExternalRestServices(externalRestServices);
                    newDataSource.setExportDir(exportPath);
                    newDataSource.setMarcFormat(marcFormat);

                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSourceContainers());
                    updateDataProvider(dataProvider, dataProviderId);
                    return newDataSource;
                } else {
                    throw new ObjectNotFoundException(dataProviderId);
                }
            } else {
                throw new InvalidArgumentsException(id);
            }
        } else {
            throw new AlreadyExistsException(id);
        }
    }

    public DataSource createDataSourceOai(String dataProviderId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, String oaiSourceURL, String oaiSet, Map<String, MetadataTransformation> metadataTransformations,
            List<ExternalRestService> externalRestServices, String marcFormat) throws DocumentException, IOException, InvalidArgumentsException, ObjectNotFoundException, AlreadyExistsException, SQLException {
        if (getDataSourceContainer(id) == null) {
            if (isIdValid(id)) {
                DataProvider dataProvider = getDataProvider(dataProviderId);

                if (dataProvider != null) {

                    //validate the URL server
                    if (!oaiSourceURL.startsWith("http://") && !oaiSourceURL.startsWith("https://")) {
                        oaiSourceURL = "http://" + oaiSourceURL;
                    }
                    if (new java.net.URL(oaiSourceURL).openConnection().getHeaderField(0) != null && FileUtilSecond.checkUrl(oaiSourceURL)) {
                        DataSource newDataSource = new OaiDataSource(dataProvider, id, description, schema, namespace, metadataFormat, oaiSourceURL, oaiSet, new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());

                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                        dataProvider.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                        newDataSource.initAccessPoints();
                        newDataSource.setMetadataTransformations(metadataTransformations);
                        newDataSource.setExternalRestServices(externalRestServices);
                        newDataSource.setExportDir(exportPath);
                        newDataSource.setMarcFormat(marcFormat);

                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSourceContainers());
                        updateDataProvider(dataProvider, dataProviderId);
                        return newDataSource;
                    } else {
                        throw new InvalidArgumentsException(oaiSourceURL);
                    }
                } else {
                    throw new ObjectNotFoundException(dataProviderId);
                }
            } else {
                throw new InvalidArgumentsException(id);
            }
        } else {
            throw new AlreadyExistsException(id);
        }
    }

    /**
     * createDataSourceZ3950IdList
     * @param dataProviderId
     * @param id
     * @param description
     * @param nameCode
     * @param name
     * @param exportPath
     * @param schema
     * @param namespace
     * @param address
     * @param port
     * @param database
     * @param user
     * @param password
     * @param recordSyntax
     * @param charset
     * @param filePath
     * @param recordIdPolicyClass
     * @param idXpath
     * @param metadataTransformations
     * @return MessageType
     * @throws DocumentException
     * @throws IOException
     * @throws ParseException
     */
    public DataSource createDataSourceZ3950IdList(String dataProviderId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String address, String port, String database, String user, String password, String recordSyntax, String charset,
            String filePath, String recordIdPolicyClass, String idXpath, Map<String, String> namespaces, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices) throws DocumentException, IOException, ParseException, ObjectNotFoundException,
            InvalidArgumentsException, AlreadyExistsException, SQLException {
        if (getDataSourceContainer(id) == null) {
            if (isIdValid(id)) {
                DataProvider dataProvider = getDataProvider(dataProviderId);

                if (dataProvider != null) {
                    if (charset == null || charset.equals(""))
                        throw new InvalidArgumentsException("charset is missing");

                    CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(charset);
                    Target target = new Target(address, Integer.valueOf(port), database, user, password, targetCharacterEncoding, recordSyntax);

                    File newFile = IdListHarvester.getIdListFilePermanent();
                    Harvester harvestMethod;
                    if (!newFile.getParentFile().getAbsolutePath().equalsIgnoreCase(new File(filePath).getParentFile().getAbsolutePath())) {
                        FileUtils.copyFile(new File(filePath), newFile);
                        harvestMethod = new IdListHarvester(target, newFile);
                    } else {
                        harvestMethod = new IdListHarvester(target, new File(filePath));
                    }

                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    DataSource newDataSource = new DataSourceZ3950(dataProvider, id, description, schema, namespace, harvestMethod, recordIdPolicy, new TreeMap<String, MetadataTransformation>());

                    newDataSource.setExportDir(exportPath);

                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                    dataProvider.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                    newDataSource.initAccessPoints();
                    newDataSource.setMetadataTransformations(metadataTransformations);
                    newDataSource.setExternalRestServices(externalRestServices);

                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSourceContainers());
                    updateDataProvider(dataProvider, dataProviderId);
                    return newDataSource;
                } else {
                    throw new ObjectNotFoundException(dataProviderId);
                }
            } else {
                throw new InvalidArgumentsException(id);
            }
        } else {
            throw new AlreadyExistsException(id);
        }
    }

    /**
     * createDataSourceZ3950Timestamp
     * @param dataProviderId
     * @param id
     * @param description
     * @param nameCode
     * @param name
     * @param exportPath
     * @param schema
     * @param namespace
     * @param address
     * @param port
     * @param database
     * @param user
     * @param password
     * @param recordSyntax
     * @param charset
     * @param earliestTimestampString
     * @param recordIdPolicyClass
     * @param idXpath
     * @param metadataTransformations
     * @return MessageType
     * @throws DocumentException
     * @throws IOException
     * @throws ParseException
     */
    public DataSource createDataSourceZ3950Timestamp(String dataProviderId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String address, String port, String database, String user, String password, String recordSyntax,
            String charset, String earliestTimestampString, String recordIdPolicyClass, String idXpath, Map<String, String> namespaces, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices) throws DocumentException, IOException, ParseException,
            SQLException, ObjectNotFoundException, InvalidArgumentsException, AlreadyExistsException {
        if (getDataSourceContainer(id) == null) {
            if (isIdValid(id)) {
                DataProvider dataProvider = getDataProvider(dataProviderId);

                if (dataProvider != null) {
                    if (charset == null || charset.equals(""))
                        throw new InvalidArgumentsException("charset is missing");

                    CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(charset);
                    Target target = new Target(address, Integer.valueOf(port), database, user, password, targetCharacterEncoding, recordSyntax);

                    Harvester harvestMethod;
                    try {
                        harvestMethod = new TimestampHarvester(target, DateUtil.string2Date(earliestTimestampString, "yyyyMMdd"));
                    } catch (ParseException e) {
                        throw new InvalidArgumentsException("earliestTimestamp");
                    }

                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    DataSource newDataSource = new DataSourceZ3950(dataProvider, id, description, schema, namespace, harvestMethod, recordIdPolicy, new TreeMap<String, MetadataTransformation>());

                    newDataSource.setExportDir(exportPath);

                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                    dataProvider.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                    newDataSource.initAccessPoints();
                    newDataSource.setMetadataTransformations(metadataTransformations);
                    newDataSource.setExternalRestServices(externalRestServices);

                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSourceContainers());
                    updateDataProvider(dataProvider, dataProviderId);
                    return newDataSource;
                } else {
                    throw new ObjectNotFoundException(dataProviderId);
                }
            } else {
                throw new InvalidArgumentsException(id);
            }
        } else {
            throw new AlreadyExistsException(id);
        }
    }

    /**
     * createDataSourceZ3950IdSequence
     * @param dataProviderId
     * @param id
     * @param description
     * @param nameCode
     * @param name
     * @param exportPath
     * @param schema
     * @param namespace
     * @param address
     * @param port
     * @param database
     * @param user
     * @param password
     * @param recordSyntax
     * @param charset
     * @param maximumIdString
     * @param recordIdPolicyClass
     * @param idXpath
     * @param metadataTransformations
     * @return MessageType
     * @throws DocumentException
     * @throws IOException
     * @throws ParseException
     */
    public DataSource createDataSourceZ3950IdSequence(String dataProviderId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String address, String port, String database, String user, String password, String recordSyntax,
            String charset, String maximumIdString, String recordIdPolicyClass, String idXpath, Map<String, String> namespaces, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices) throws DocumentException, IOException, ParseException,
            SQLException, ObjectNotFoundException, InvalidArgumentsException, AlreadyExistsException {
        if (getDataSourceContainer(id) == null) {
            if (isIdValid(id)) {
                DataProvider dataProvider = getDataProvider(dataProviderId);

                if (dataProvider != null) {
                    if (charset == null || charset.equals(""))
                        throw new InvalidArgumentsException("charset is missing");

                    CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(charset);
                    Target target = new Target(address, Integer.valueOf(port), database, user, password, targetCharacterEncoding, recordSyntax);

                    Long maximumId = (maximumIdString != null && !maximumIdString.isEmpty() ? Long.valueOf(maximumIdString) : null);
                    Harvester harvestMethod = new IdSequenceHarvester(target, maximumId);

                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    DataSource newDataSource = new DataSourceZ3950(dataProvider, id, description, schema, namespace, harvestMethod, recordIdPolicy, new TreeMap<String, MetadataTransformation>());

                    newDataSource.setExportDir(exportPath);

                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                    dataProvider.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                    newDataSource.initAccessPoints();
                    newDataSource.setMetadataTransformations(metadataTransformations);
                    newDataSource.setExternalRestServices(externalRestServices);

                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSourceContainers());
                    updateDataProvider(dataProvider, dataProviderId);
                    return newDataSource;
                } else {
                    throw new ObjectNotFoundException(dataProviderId);
                }
            } else {
                throw new InvalidArgumentsException(id);
            }
        } else {
            throw new AlreadyExistsException(id);
        }
    }

    /**
     * createDataSourceFtp
     * @param dataProviderId
     * @param id
     * @param description
     * @param nameCode
     * @param name
     * @param exportPath
     * @param schema
     * @param namespace
     * @param metadataFormat
     * @param isoFormat
     * @param charset
     * @param recordIdPolicyClass
     * @param idXpath
     * @param recordXPath
     * @param server
     * @param user
     * @param password
     * @param ftpPath
     * @param metadataTransformations
     * @return MessageType
     * @throws DocumentException
     * @throws IOException
     */
    public DataSource createDataSourceFtp(String dataProviderId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass, String idXpath,
            Map<String, String> namespaces, String recordXPath, String server, String user, String password, String ftpPath, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices, String marcFormat) throws DocumentException, IOException,
            InvalidArgumentsException, ObjectNotFoundException, SQLException, AlreadyExistsException {
        if (getDataSourceContainer(id) == null) {
            if (isIdValid(id)) {
                DataProvider dataProvider = getDataProvider(dataProviderId);

                if (dataProvider != null) {
                    String accessType;
                    if (user.equals("") && password.equals("")) {
                        accessType = FtpFileRetrieveStrategy.ANONYMOUS;
                    } else {
                        accessType = FtpFileRetrieveStrategy.NORMAL;
                    }

                    FileRetrieveStrategy retrieveStrategy = new FtpFileRetrieveStrategy(server, user, password, accessType, ftpPath);

                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    if (recordIdPolicy != null) {
                        CharacterEncoding characterEncoding = null;
                        FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(metadataFormat, isoFormat);
                        if (extractStrategy.getClass() == Iso2709FileExtractStrategy.class) {
                            if (charset == null || charset.equals(""))
                                throw new InvalidArgumentsException("charset is missing");
                            characterEncoding = CharacterEncoding.get(charset);
                        } else if (extractStrategy.getClass() == MarcXchangeFileExtractStrategy.class) {
                        } else if (extractStrategy.getClass() == SimpleFileExtractStrategy.class) {
                        }

                        DataSource newDataSource = new DirectoryImporterDataSource(dataProvider, id, description, schema, namespace, metadataFormat, extractStrategy, retrieveStrategy, characterEncoding, FtpFileRetrieveStrategy.getOutputFtpPath(server, id), recordIdPolicy, new TreeMap<String, MetadataTransformation>(), recordXPath, new HashMap<String, String>());

                        newDataSource.setExportDir(exportPath);

                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                        dataProvider.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                        newDataSource.initAccessPoints();
                        newDataSource.setMetadataTransformations(metadataTransformations);
                        newDataSource.setExternalRestServices(externalRestServices);
                        newDataSource.setMarcFormat(marcFormat);

                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSourceContainers());
                        updateDataProvider(dataProvider, dataProviderId);
                        return newDataSource;
                    } else {
                        throw new InvalidArgumentsException("recordIdPolicy");
                    }
                } else {
                    throw new ObjectNotFoundException(dataProviderId);
                }
            } else {
                throw new InvalidArgumentsException(id);
            }
        } else {
            throw new AlreadyExistsException(id);
        }
    }

    /**
     * createDataSourceHttp
     * @param dataProviderId
     * @param id
     * @param description
     * @param nameCode
     * @param name
     * @param exportPath
     * @param schema
     * @param namespace
     * @param metadataFormat
     * @param isoFormat
     * @param charset
     * @param recordIdPolicyClass
     * @param idXpath
     * @param recordXPath
     * @param url
     * @param metadataTransformations
     * @return MessageType
     * @throws DocumentException
     * @throws IOException
     */
    public DataSource createDataSourceHttp(String dataProviderId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass, String idXpath,
            Map<String, String> namespaces, String recordXPath, String url, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices, String marcFormat) throws DocumentException, IOException, InvalidArgumentsException, ObjectNotFoundException,
            SQLException, AlreadyExistsException {
        if (getDataSourceContainer(id) == null) {
            if (isIdValid(id)) {
                DataProvider dataProvider = getDataProvider(dataProviderId);

                if (dataProvider != null) {
                    if (url.equals("") || !FileUtilSecond.checkUrl(url))
                        throw new InvalidArgumentsException("url");

                    FileRetrieveStrategy retrieveStrategy = new HttpFileRetrieveStrategy(url);

                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    if (recordIdPolicy != null) {
                        CharacterEncoding characterEncoding = null;
                        FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(metadataFormat, isoFormat);
                        if (extractStrategy.getClass() == Iso2709FileExtractStrategy.class) {
                            if (charset == null || charset.equals(""))
                                throw new InvalidArgumentsException("charset is missing");
                            characterEncoding = CharacterEncoding.get(charset);
                        } else if (extractStrategy.getClass() == MarcXchangeFileExtractStrategy.class) {
                        } else if (extractStrategy.getClass() == SimpleFileExtractStrategy.class) {
                        }

                        DataSource newDataSource = new DirectoryImporterDataSource(dataProvider, id, description, schema, namespace, metadataFormat, extractStrategy, retrieveStrategy, characterEncoding, HttpFileRetrieveStrategy.getOutputHttpPath(url, id), recordIdPolicy, new TreeMap<String, MetadataTransformation>(), recordXPath, new HashMap<String, String>());

                        newDataSource.setExportDir(exportPath);

                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                        dataProvider.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                        newDataSource.initAccessPoints();
                        newDataSource.setMetadataTransformations(metadataTransformations);
                        newDataSource.setExternalRestServices(externalRestServices);
                        newDataSource.setMarcFormat(marcFormat);

                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSourceContainers());
                        updateDataProvider(dataProvider, dataProviderId);
                        return newDataSource;
                    } else {
                        throw new InvalidArgumentsException("recordIdPolicy");
                    }
                } else {
                    throw new ObjectNotFoundException(dataProviderId);
                }
            } else {
                throw new InvalidArgumentsException(id);
            }
        } else {
            throw new AlreadyExistsException(id);
        }
    }

    /**
     * createDataSourceFolder
     * @param dataProviderId
     * @param id
     * @param description
     * @param nameCode
     * @param name
     * @param exportPath
     * @param schema
     * @param namespace
     * @param metadataFormat
     * @param isoVariant
     * @param charset
     * @param recordIdPolicyClass
     * @param idXpath
     * @param recordXPath
     * @param sourcesDirPath
     * @param metadataTransformations
     * @return MessageType
     * @throws DocumentException
     * @throws IOException
     */
    public DataSource createDataSourceFolder(String dataProviderId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, String isoVariant, String charset, String recordIdPolicyClass, String idXpath,
            Map<String, String> namespaces, String recordXPath, String sourcesDirPath, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices, String marcFormat) throws DocumentException, IOException, InvalidArgumentsException,
            ObjectNotFoundException, AlreadyExistsException, SQLException {
        if (getDataSourceContainer(id) == null) {
            if (isIdValid(id)) {
                if (sourcesDirPath.equals(""))
                    throw new InvalidArgumentsException("sourcesDirPath");

                DataProvider dataProvider = getDataProvider(dataProviderId);

                if (dataProvider != null) {
                    FileRetrieveStrategy retrieveStrategy = new FolderFileRetrieveStrategy();

                    RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                    if (recordIdPolicy != null) {
                        CharacterEncoding characterEncoding = null;
                        FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(metadataFormat, isoVariant);
                        if (extractStrategy.getClass() == Iso2709FileExtractStrategy.class) {
                            if (charset == null || charset.equals(""))
                                throw new InvalidArgumentsException("charset is missing");
                            characterEncoding = CharacterEncoding.get(charset);
                        } else if (extractStrategy.getClass() == MarcXchangeFileExtractStrategy.class) {
                        } else if (extractStrategy.getClass() == SimpleFileExtractStrategy.class) {
                        }

                        DataSource newDataSource = new DirectoryImporterDataSource(dataProvider, id, description, schema, namespace, metadataFormat, extractStrategy, retrieveStrategy, characterEncoding, sourcesDirPath, recordIdPolicy, new TreeMap<String, MetadataTransformation>(), recordXPath, new HashMap<String, String>());

                        newDataSource.setExportDir(exportPath);

                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                        dataProvider.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                        newDataSource.initAccessPoints();
                        newDataSource.setMetadataTransformations(metadataTransformations);
                        newDataSource.setExternalRestServices(externalRestServices);
                        newDataSource.setMarcFormat(marcFormat);

                        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(dataProvider.getDataSourceContainers());
                        updateDataProvider(dataProvider, dataProviderId);
                        return newDataSource;
                    } else {
                        throw new InvalidArgumentsException("recordIdPolicy");
                    }
                } else {
                    throw new ObjectNotFoundException(dataProviderId);
                }
            } else {
                throw new InvalidArgumentsException(id);
            }
        } else {
            throw new AlreadyExistsException(id);
        }
    }

    public DataSource updateDataSourceSruRecordUpdate(String oldId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, Map<String, MetadataTransformation> metadataTransformations,
            List<ExternalRestService> externalRestServices, String marcFormat, boolean useLastIngestDate) throws DocumentException, IOException, ObjectNotFoundException, InvalidArgumentsException, IncompatibleInstanceException {
        DefaultDataSourceContainer oldDataSourceContainer = (DefaultDataSourceContainer)getDataSourceContainer(oldId);
        if (oldDataSourceContainer != null) {
            DataSource dataSource = oldDataSourceContainer.getDataSource();
            if (!isIdValid(id))
                throw new InvalidArgumentsException(id);

            DataProvider dataProviderParent = getDataProviderParent(oldId);
            if (dataProviderParent != null) {
                if (!(dataSource instanceof SruRecordUpdateDataSource)) {
                    DataSource newDataSource = new SruRecordUpdateDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat, new IdGeneratedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
                    newDataSource.setAccessPoints(dataSource.getAccessPoints());
                    newDataSource.setStatus(dataSource.getStatus());

                    setLastIngestDate(useLastIngestDate, dataSource, newDataSource);
                    newDataSource.setOldTasksList(dataSource.getOldTasksList());
                    newDataSource.setTags(dataSource.getTags());

                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                    dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                    dataSource = newDataSource;
                }

                dataSource.setId(id);
                dataSource.setDescription(description);
                dataSource.setSchema(schema);
                dataSource.setNamespace(namespace);
                dataSource.setMetadataFormat(metadataFormat);
                dataSource.setMetadataTransformations(metadataTransformations);
                dataSource.setExternalRestServices(externalRestServices);
                dataSource.setMarcFormat(marcFormat);

                dataSource.setExportDir(exportPath);

                if (!id.equals(oldId)) {
                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(dataSource, nameCode, name, exportPath);
                    updateDataSourceContainer(dataSourceContainer, oldId);
                } else {
                    oldDataSourceContainer.setName(name);
                    oldDataSourceContainer.setNameCode(nameCode);
                    //                            dataSourceContainer.setExportPath(exportPath);
                }
                updateDataProvider(dataProviderParent, dataProviderParent.getId());
                return dataSource;
            } else {
                throw new ObjectNotFoundException(id);
            }
        } else {
            throw new ObjectNotFoundException(id);
        }
    }

    public DataSource updateDataSourceOai(String oldId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, String oaiSourceURL, String oaiSet, Map<String, MetadataTransformation> metadataTransformations,
            List<ExternalRestService> externalRestServices, String marcFormat, boolean useLastIngestDate) throws DocumentException, IOException, ObjectNotFoundException, InvalidArgumentsException, IncompatibleInstanceException {
        DefaultDataSourceContainer oldDataSourceContainer = (DefaultDataSourceContainer)getDataSourceContainer(oldId);
        if (oldDataSourceContainer != null) {
            DataSource dataSource = oldDataSourceContainer.getDataSource();
            if (!isIdValid(id))
                throw new InvalidArgumentsException(id);

            //validate the URL server
            if (!oaiSourceURL.startsWith("http://") && !oaiSourceURL.startsWith("https://")) {
                oaiSourceURL = "http://" + oaiSourceURL;
            }
            if (new java.net.URL(oaiSourceURL).openConnection().getHeaderField(0) != null && FileUtilSecond.checkUrl(oaiSourceURL)) {
                DataProvider dataProviderParent = getDataProviderParent(oldId);
                if (dataProviderParent != null) {
                    if (!(dataSource instanceof OaiDataSource)) {
                        DataSource newDataSource = new OaiDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat, oaiSourceURL, oaiSet, new IdProvidedRecordIdPolicy(), new TreeMap<String, MetadataTransformation>());
                        newDataSource.setAccessPoints(dataSource.getAccessPoints());
                        newDataSource.setStatus(dataSource.getStatus());

                        setLastIngestDate(useLastIngestDate, dataSource, newDataSource);
                        newDataSource.setOldTasksList(dataSource.getOldTasksList());
                        newDataSource.setTags(dataSource.getTags());

                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                        dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                        dataSource = newDataSource;
                    }

                    dataSource.setId(id);
                    dataSource.setDescription(description);
                    dataSource.setSchema(schema);
                    dataSource.setNamespace(namespace);
                    dataSource.setMetadataFormat(metadataFormat);
                    ((OaiDataSource)dataSource).setOaiSourceURL(oaiSourceURL);
                    ((OaiDataSource)dataSource).setOaiSet(oaiSet);
                    dataSource.setMetadataTransformations(metadataTransformations);
                    dataSource.setExternalRestServices(externalRestServices);
                    dataSource.setMarcFormat(marcFormat);

                    dataSource.setExportDir(exportPath);

                    if (!id.equals(oldId)) {
                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(dataSource, nameCode, name, exportPath);
                        updateDataSourceContainer(dataSourceContainer, oldId);
                    } else {
                        oldDataSourceContainer.setName(name);
                        oldDataSourceContainer.setNameCode(nameCode);
                        //                            dataSourceContainer.setExportPath(exportPath);
                    }
                    updateDataProvider(dataProviderParent, dataProviderParent.getId());
                    return dataSource;
                } else {
                    throw new ObjectNotFoundException(id);
                }
            } else {
                throw new InvalidArgumentsException(oaiSourceURL);
            }
        } else {
            throw new ObjectNotFoundException(id);
        }
    }

    public DataSource updateDataSourceZ3950Timestamp(String oldId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String address, String port, String database, String user, String password, String recordSyntax, String charset,
            String earliestTimestampString, String recordIdPolicyClass, String idXpath, Map<String, String> namespaces, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices, boolean useLastIngestDate) throws DocumentException, IOException,
            ParseException, ObjectNotFoundException, IncompatibleInstanceException, InvalidArgumentsException {
        DefaultDataSourceContainer oldDataSourceContainer = (DefaultDataSourceContainer)getDataSourceContainer(oldId);
        if (oldDataSourceContainer != null) {
            DataSource dataSource = oldDataSourceContainer.getDataSource();
            if (!isIdValid(id))
                throw new InvalidArgumentsException(id);

            DataProvider dataProviderParent = getDataProviderParent(oldId);
            if (dataProviderParent != null) {
                if (charset == null || charset.equals(""))
                    throw new InvalidArgumentsException("charset is missing");
                CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(charset);
                Target target = new Target(address, Integer.valueOf(port), database, user, password, targetCharacterEncoding, recordSyntax);

                Harvester harvestMethod;
                try {
                    harvestMethod = new TimestampHarvester(target, DateUtil.string2Date(earliestTimestampString, "yyyyMMdd"));
                } catch (ParseException e) {
                    throw new InvalidArgumentsException("earliestTimestamp");
                }

                RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                if (!(dataSource instanceof DataSourceZ3950)) {
                    DataSource newDataSource = new DataSourceZ3950(dataProviderParent, id, description, schema, namespace, harvestMethod, recordIdPolicy, new TreeMap<String, MetadataTransformation>());
                    newDataSource.setExportDir(exportPath);
                    newDataSource.setAccessPoints(dataSource.getAccessPoints());
                    newDataSource.setStatus(dataSource.getStatus());

                    setLastIngestDate(useLastIngestDate, dataSource, newDataSource);
                    newDataSource.setOldTasksList(dataSource.getOldTasksList());
                    newDataSource.setTags(dataSource.getTags());

                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                    dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                    dataSource = newDataSource;
                }

                dataSource.setId(id);
                dataSource.setDescription(description);
                dataSource.setSchema(schema);
                dataSource.setNamespace(namespace);
                dataSource.setRecordIdPolicy(recordIdPolicy);
                ((DataSourceZ3950)dataSource).setHarvestMethod(harvestMethod);
                dataSource.setMetadataTransformations(metadataTransformations);
                dataSource.setExternalRestServices(externalRestServices);
                dataSource.setExportDir(exportPath);

                if (!id.equals(oldId)) {
                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(dataSource, nameCode, name, exportPath);
                    updateDataSourceContainer(dataSourceContainer, oldId);
                } else {
                    oldDataSourceContainer.setName(name);
                    oldDataSourceContainer.setNameCode(nameCode);
                    //                        dataSourceContainer.setExportPath(exportPath);
                }
                updateDataProvider(dataProviderParent, dataProviderParent.getId());
                return dataSource;
            } else {
                throw new ObjectNotFoundException(id);
            }
        } else {
            throw new ObjectNotFoundException(id);
        }
    }

    public DataSource updateDataSourceZ3950IdList(String oldId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String address, String port, String database, String user, String password, String recordSyntax, String charset,
            String filePath, String recordIdPolicyClass, String idXpath, Map<String, String> namespaces, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices, boolean useLastIngestDate) throws DocumentException, IOException, ParseException,
            InvalidArgumentsException, ObjectNotFoundException, IncompatibleInstanceException {
        DefaultDataSourceContainer oldDataSourceContainer = (DefaultDataSourceContainer)getDataSourceContainer(oldId);
        if (oldDataSourceContainer != null) {
            DataSource dataSource = oldDataSourceContainer.getDataSource();
            if (!isIdValid(id))
                throw new InvalidArgumentsException(id);

            DataProvider dataProviderParent = getDataProviderParent(oldId);
            if (dataProviderParent != null) {
                if (charset == null || charset.equals(""))
                    throw new InvalidArgumentsException("charset is missing");
                CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(charset);
                Target target = new Target(address, Integer.valueOf(port), database, user, password, targetCharacterEncoding, recordSyntax);

                File file;
                if (!filePath.isEmpty() && !filePath.startsWith(new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getXmlConfigPath()).getAbsolutePath())) {
                    try {
                        FileUtils.forceDelete(((IdListHarvester)((DataSourceZ3950)dataSource).getHarvestMethod()).getIdListFile());
                    } catch (Exception e) {
                        log.error("Error removing z39.50 file: " + ((IdListHarvester)((DataSourceZ3950)dataSource).getHarvestMethod()).getIdListFile());
                    }
                    file = ((IdListHarvester)((DataSourceZ3950)dataSource).getHarvestMethod()).getIdListFile();
                    FileUtils.copyFile(new File(filePath), file);
                    FileUtils.forceDelete(new File(filePath));
                    ((IdListHarvester)((DataSourceZ3950)dataSource).getHarvestMethod()).setIdListFile(file);
                } else {
                    file = ((IdListHarvester)((DataSourceZ3950)dataSource).getHarvestMethod()).getIdListFile();
                }
                Harvester harvestMethod = new IdListHarvester(target, file);

                RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                if (!(dataSource instanceof DataSourceZ3950)) {
                    DataSource newDataSource = new DataSourceZ3950(dataProviderParent, id, description, schema, namespace, harvestMethod, recordIdPolicy, new TreeMap<String, MetadataTransformation>());
                    newDataSource.setExportDir(exportPath);
                    newDataSource.setAccessPoints(dataSource.getAccessPoints());
                    newDataSource.setStatus(dataSource.getStatus());

                    setLastIngestDate(useLastIngestDate, dataSource, newDataSource);
                    newDataSource.setOldTasksList(dataSource.getOldTasksList());
                    newDataSource.setTags(dataSource.getTags());

                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                    dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                    dataSource = newDataSource;
                }

                dataSource.setId(id);
                dataSource.setDescription(description);
                dataSource.setSchema(schema);
                dataSource.setNamespace(namespace);
                dataSource.setRecordIdPolicy(recordIdPolicy);
                ((DataSourceZ3950)dataSource).setHarvestMethod(harvestMethod);
                dataSource.setMetadataTransformations(metadataTransformations);
                dataSource.setExternalRestServices(externalRestServices);
                dataSource.setExportDir(exportPath);

                if (!id.equals(oldId)) {
                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(dataSource, nameCode, name, exportPath);
                    updateDataSourceContainer(dataSourceContainer, oldId);
                } else {
                    oldDataSourceContainer.setName(name);
                    oldDataSourceContainer.setNameCode(nameCode);
                    //                        dataSourceContainer.setExportPath(exportPath);
                }
                updateDataProvider(dataProviderParent, dataProviderParent.getId());
                return dataSource;
            } else {
                throw new ObjectNotFoundException(id);
            }
        } else {
            throw new ObjectNotFoundException(id);
        }
    }

    public DataSource updateDataSourceZ3950IdSequence(String oldId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String address, String port, String database, String user, String password, String recordSyntax, String charset,
            String maximumIdString, String recordIdPolicyClass, String idXpath, Map<String, String> namespaces, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices, boolean useLastIngestDate) throws DocumentException, IOException,
            ParseException, InvalidArgumentsException, ObjectNotFoundException, IncompatibleInstanceException {
        DefaultDataSourceContainer oldDataSourceContainer = (DefaultDataSourceContainer)getDataSourceContainer(oldId);
        if (oldDataSourceContainer != null) {
            DataSource dataSource = oldDataSourceContainer.getDataSource();
            if (!isIdValid(id))
                throw new InvalidArgumentsException(id);

            DataProvider dataProviderParent = getDataProviderParent(oldId);
            if (dataProviderParent != null) {
                if (charset == null || charset.equals(""))
                    throw new InvalidArgumentsException("charset is missing");
                CharacterEncoding targetCharacterEncoding = CharacterEncoding.get(charset);
                Target target = new Target(address, Integer.valueOf(port), database, user, password, targetCharacterEncoding, recordSyntax);

                Long maximumId = (maximumIdString != null && !maximumIdString.isEmpty() ? Long.valueOf(maximumIdString) : null);
                Harvester harvestMethod = new IdSequenceHarvester(target, maximumId);

                RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                if (!(dataSource instanceof DataSourceZ3950)) {
                    DataSource newDataSource = new DataSourceZ3950(dataProviderParent, id, description, schema, namespace, harvestMethod, recordIdPolicy, new TreeMap<String, MetadataTransformation>());
                    newDataSource.setExportDir(exportPath);
                    newDataSource.setAccessPoints(dataSource.getAccessPoints());
                    newDataSource.setStatus(dataSource.getStatus());

                    setLastIngestDate(useLastIngestDate, dataSource, newDataSource);
                    newDataSource.setOldTasksList(dataSource.getOldTasksList());
                    newDataSource.setTags(dataSource.getTags());

                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                    dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                    dataSource = newDataSource;
                }

                dataSource.setId(id);
                dataSource.setDescription(description);
                dataSource.setSchema(schema);
                dataSource.setNamespace(namespace);
                dataSource.setRecordIdPolicy(recordIdPolicy);
                ((DataSourceZ3950)dataSource).setHarvestMethod(harvestMethod);
                dataSource.setMetadataTransformations(metadataTransformations);
                dataSource.setExternalRestServices(externalRestServices);
                dataSource.setExportDir(exportPath);

                if (!id.equals(oldId)) {
                    DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(dataSource, nameCode, name, exportPath);
                    updateDataSourceContainer(dataSourceContainer, oldId);
                } else {
                    oldDataSourceContainer.setName(name);
                    oldDataSourceContainer.setNameCode(nameCode);
                    //                        dataSourceContainer.setExportPath(exportPath);
                }
                updateDataProvider(dataProviderParent, dataProviderParent.getId());
                return dataSource;
            } else {
                throw new ObjectNotFoundException(id);
            }
        } else {
            throw new ObjectNotFoundException(id);
        }
    }

    public DataSource updateDataSourceFtp(String oldId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass, String idXpath,
            Map<String, String> namespaces, String recordXPath, String server, String user, String password, String ftpPath, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices, String marcFormat, boolean useLastIngestDate)
            throws DocumentException, IOException, InvalidArgumentsException, ObjectNotFoundException, IncompatibleInstanceException {
        DefaultDataSourceContainer oldDataSourceContainer = (DefaultDataSourceContainer)getDataSourceContainer(oldId);
        if (oldDataSourceContainer != null) {
            DataSource dataSource = oldDataSourceContainer.getDataSource();
            if (!isIdValid(id))
                throw new InvalidArgumentsException(id);

            if (ftpPath.equals(""))
                throw new InvalidArgumentsException("ftpPath");

            DataProvider dataProviderParent = getDataProviderParent(oldId);
            if (dataProviderParent != null) {
                String accessType;
                if (user.equals("") && password.equals("")) {
                    accessType = FtpFileRetrieveStrategy.ANONYMOUS;
                } else {
                    accessType = FtpFileRetrieveStrategy.NORMAL;
                }

                FileRetrieveStrategy retrieveStrategy = new FtpFileRetrieveStrategy(server, user, password, accessType, ftpPath);

                RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                if (recordIdPolicy != null) {
                    CharacterEncoding characterEncoding = null;
                    FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(metadataFormat, isoFormat);
                    if (extractStrategy.getClass() == Iso2709FileExtractStrategy.class) {
                        if (charset == null || charset.equals("")) {
                            throw new InvalidArgumentsException("Charset is missing");
                        }
                        characterEncoding = CharacterEncoding.get(charset);
                    } else if (extractStrategy.getClass() == MarcXchangeFileExtractStrategy.class) {
                    } else if (extractStrategy.getClass() == SimpleFileExtractStrategy.class) {
                    }

                    if (!(dataSource instanceof DirectoryImporterDataSource)) {
                        DataSource newDataSource = new DirectoryImporterDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat, extractStrategy, retrieveStrategy, characterEncoding, FtpFileRetrieveStrategy.getOutputFtpPath(server, id), recordIdPolicy, new TreeMap<String, MetadataTransformation>(), recordXPath, new HashMap<String, String>());
                        newDataSource.setExportDir(exportPath);
                        newDataSource.setAccessPoints(dataSource.getAccessPoints());
                        newDataSource.setStatus(dataSource.getStatus());

                        setLastIngestDate(useLastIngestDate, dataSource, newDataSource);
                        newDataSource.setOldTasksList(dataSource.getOldTasksList());
                        newDataSource.setTags(dataSource.getTags());

                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                        dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                        dataSource = newDataSource;
                    }

                    dataSource.setId(id);
                    dataSource.setDescription(description);
                    dataSource.setSchema(schema);
                    dataSource.setNamespace(namespace);
                    dataSource.setMetadataFormat(metadataFormat);
                    dataSource.setRecordIdPolicy(recordIdPolicy);
                    ((DirectoryImporterDataSource)dataSource).setExtractStrategy(extractStrategy);
                    ((DirectoryImporterDataSource)dataSource).setRetrieveStrategy(retrieveStrategy);
                    ((DirectoryImporterDataSource)dataSource).setCharacterEncoding(characterEncoding);
                    ((DirectoryImporterDataSource)dataSource).setSourcesDirPath(FtpFileRetrieveStrategy.getOutputFtpPath(server, id));
                    ((DirectoryImporterDataSource)dataSource).setRecordXPath(recordXPath);
                    ((DirectoryImporterDataSource)dataSource).setNamespaces(new HashMap<String, String>());
                    dataSource.setMetadataTransformations(metadataTransformations);
                    dataSource.setExternalRestServices(externalRestServices);
                    dataSource.setExportDir(exportPath);
                    dataSource.setMarcFormat(marcFormat);

                    if (!id.equals(oldId)) {
                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(dataSource, nameCode, name, exportPath);
                        updateDataSourceContainer(dataSourceContainer, oldId);
                    } else {
                        oldDataSourceContainer.setName(name);
                        oldDataSourceContainer.setNameCode(nameCode);
                        //                            dataSourceContainer.setExportPath(exportPath);
                    }
                    updateDataProvider(dataProviderParent, dataProviderParent.getId());
                    return dataSource;
                } else {
                    throw new InvalidArgumentsException("recordIdPolicy");
                }
            } else {
                throw new ObjectNotFoundException(id);
            }
        } else {
            throw new ObjectNotFoundException(id);
        }
    }

    public DataSource updateDataSourceHttp(String oldId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass, String idXpath,
            Map<String, String> namespaces, String recordXPath, String url, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices, String marcFormat, boolean useLastIngestDate) throws DocumentException, IOException, InvalidArgumentsException,
            ObjectNotFoundException, IncompatibleInstanceException {
        DefaultDataSourceContainer oldDataSourceContainer = (DefaultDataSourceContainer)getDataSourceContainer(oldId);
        if (oldDataSourceContainer != null) {
            DataSource dataSource = oldDataSourceContainer.getDataSource();
            if (!isIdValid(id))
                throw new InvalidArgumentsException(id);

            if (url.equals(""))
                throw new InvalidArgumentsException("url");

            DataProvider dataProviderParent = getDataProviderParent(oldId);
            if (dataProviderParent != null) {
                RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                if (recordIdPolicy != null) {
                    CharacterEncoding characterEncoding = null;
                    FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(metadataFormat, isoFormat);
                    if (extractStrategy.getClass() == Iso2709FileExtractStrategy.class) {
                        if (charset == null || charset.equals(""))
                            throw new InvalidArgumentsException("charset is missing");
                        characterEncoding = CharacterEncoding.get(charset);
                    } else if (extractStrategy.getClass() == MarcXchangeFileExtractStrategy.class) {
                    } else if (extractStrategy.getClass() == SimpleFileExtractStrategy.class) {
                    }

                    FileRetrieveStrategy retrieveStrategy = new HttpFileRetrieveStrategy(url);

                    if (!(dataSource instanceof DirectoryImporterDataSource)) {
                        DataSource newDataSource = new DirectoryImporterDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat, extractStrategy, retrieveStrategy, characterEncoding, HttpFileRetrieveStrategy.getOutputHttpPath(url, id), recordIdPolicy, new TreeMap<String, MetadataTransformation>(), recordXPath, new HashMap<String, String>());
                        newDataSource.setExportDir(exportPath);
                        newDataSource.setAccessPoints(dataSource.getAccessPoints());
                        newDataSource.setStatus(dataSource.getStatus());

                        setLastIngestDate(useLastIngestDate, dataSource, newDataSource);
                        newDataSource.setOldTasksList(dataSource.getOldTasksList());
                        newDataSource.setTags(dataSource.getTags());

                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                        dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                        dataSource = newDataSource;
                    }

                    dataSource.setId(id);
                    dataSource.setDescription(description);
                    dataSource.setSchema(schema);
                    dataSource.setNamespace(namespace);
                    dataSource.setMetadataFormat(metadataFormat);
                    dataSource.setRecordIdPolicy(recordIdPolicy);
                    ((DirectoryImporterDataSource)dataSource).setExtractStrategy(extractStrategy);
                    ((DirectoryImporterDataSource)dataSource).setRetrieveStrategy(retrieveStrategy);
                    ((DirectoryImporterDataSource)dataSource).setRetrieveStrategy(new HttpFileRetrieveStrategy(url));
                    ((DirectoryImporterDataSource)dataSource).setCharacterEncoding(characterEncoding);
                    ((DirectoryImporterDataSource)dataSource).setSourcesDirPath(HttpFileRetrieveStrategy.getOutputHttpPath(url, id));
                    ((DirectoryImporterDataSource)dataSource).setRecordXPath(recordXPath);
                    ((DirectoryImporterDataSource)dataSource).setNamespaces(new HashMap<String, String>());
                    dataSource.setMetadataTransformations(metadataTransformations);
                    dataSource.setExternalRestServices(externalRestServices);
                    dataSource.setExportDir(exportPath);
                    dataSource.setMarcFormat(marcFormat);

                    if (!id.equals(oldId)) {
                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(dataSource, nameCode, name, exportPath);
                        updateDataSourceContainer(dataSourceContainer, oldId);
                    } else {
                        oldDataSourceContainer.setName(name);
                        oldDataSourceContainer.setNameCode(nameCode);
                        //                            dataSourceContainer.setExportPath(exportPath);
                    }
                    updateDataProvider(dataProviderParent, dataProviderParent.getId());
                    return dataSource;
                } else {
                    throw new InvalidArgumentsException("recordIdPolicy");
                }
            } else {
                throw new ObjectNotFoundException(id);
            }
        } else {
            throw new ObjectNotFoundException(id);
        }
    }

    public DataSource updateDataSourceFolder(String oldId, String id, String description, String nameCode, String name, String exportPath, String schema, String namespace, String metadataFormat, String isoFormat, String charset, String recordIdPolicyClass, String idXpath,
            Map<String, String> namespaces, String recordXPath, String sourcesDirPath, Map<String, MetadataTransformation> metadataTransformations, List<ExternalRestService> externalRestServices, String marcFormat, boolean useLastIngestDate) throws IOException, DocumentException,
            InvalidArgumentsException, ObjectNotFoundException, IncompatibleInstanceException {
        DefaultDataSourceContainer oldDataSourceContainer = (DefaultDataSourceContainer)getDataSourceContainer(oldId);
        if (oldDataSourceContainer != null) {
            DataSource dataSource = oldDataSourceContainer.getDataSource();
            if (!isIdValid(id))
                throw new InvalidArgumentsException(id);

            if (sourcesDirPath.equals(""))
                throw new InvalidArgumentsException("sourcesDirPath");

            DataProvider dataProviderParent = getDataProviderParent(oldId);
            if (dataProviderParent != null) {
                RecordIdPolicy recordIdPolicy = DataSourceUtil.createIdPolicy(recordIdPolicyClass, idXpath, namespaces);

                if (recordIdPolicy != null) {
                    CharacterEncoding characterEncoding = null;
                    FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(metadataFormat, isoFormat);
                    if (extractStrategy.getClass() == Iso2709FileExtractStrategy.class) {
                        if (charset == null || charset.equals(""))
                            throw new InvalidArgumentsException("charset is missing");
                        characterEncoding = CharacterEncoding.get(charset);
                    } else if (extractStrategy.getClass() == MarcXchangeFileExtractStrategy.class) {
                    } else if (extractStrategy.getClass() == SimpleFileExtractStrategy.class) {
                    }

                    FileRetrieveStrategy retrieveStrategy = new FolderFileRetrieveStrategy();

                    if (!(dataSource instanceof DirectoryImporterDataSource)) {
                        DataSource newDataSource = new DirectoryImporterDataSource(dataProviderParent, id, description, schema, namespace, metadataFormat, extractStrategy, retrieveStrategy, characterEncoding, sourcesDirPath, recordIdPolicy, new TreeMap<String, MetadataTransformation>(), recordXPath, new HashMap<String, String>());
                        newDataSource.setExportDir(exportPath);
                        newDataSource.setAccessPoints(dataSource.getAccessPoints());
                        newDataSource.setStatus(dataSource.getStatus());

                        setLastIngestDate(useLastIngestDate, dataSource, newDataSource);
                        newDataSource.setOldTasksList(dataSource.getOldTasksList());
                        newDataSource.setTags(dataSource.getTags());

                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(newDataSource, nameCode, name, exportPath);
                        dataProviderParent.getDataSourceContainers().put(newDataSource.getId(), dataSourceContainer);
                        dataSource = newDataSource;
                    }

                    dataSource.setId(id);
                    dataSource.setDescription(description);
                    dataSource.setSchema(schema);
                    dataSource.setNamespace(namespace);
                    dataSource.setMetadataFormat(metadataFormat);
                    dataSource.setRecordIdPolicy(recordIdPolicy);
                    ((DirectoryImporterDataSource)dataSource).setExtractStrategy(extractStrategy);
                    ((DirectoryImporterDataSource)dataSource).setRetrieveStrategy(retrieveStrategy);
                    ((DirectoryImporterDataSource)dataSource).setCharacterEncoding(characterEncoding);
                    ((DirectoryImporterDataSource)dataSource).setSourcesDirPath(sourcesDirPath);
                    ((DirectoryImporterDataSource)dataSource).setRecordXPath(recordXPath);
                    ((DirectoryImporterDataSource)dataSource).setNamespaces(namespaces);
                    dataSource.setMetadataTransformations(metadataTransformations);
                    dataSource.setExternalRestServices(externalRestServices);
                    dataSource.setExportDir(exportPath);
                    dataSource.setMarcFormat(marcFormat);

                    if (!id.equals(oldId)) {
                        DefaultDataSourceContainer dataSourceContainer = new DefaultDataSourceContainer(dataSource, nameCode, name, exportPath);
                        updateDataSourceContainer(dataSourceContainer, oldId);
                    } else {
                        oldDataSourceContainer.setName(name);
                        oldDataSourceContainer.setNameCode(nameCode);
                        //                            dataSourceContainer.setExportPath(exportPath);
                    }
                    updateDataProvider(dataProviderParent, dataProviderParent.getId());
                    return dataSource;
                } else {
                    throw new InvalidArgumentsException("recordIdPolicy");
                }
            } else {
                throw new ObjectNotFoundException(id);
            }
        } else {
            throw new ObjectNotFoundException(id);
        }
    }

    /**
     * Start the data source ingestion
     * @param dataSourceId
     * @param fullIngest
     * @throws DocumentException
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws ParseException
     * @throws ObjectNotFoundException
     * @throws AlreadyExistsException
     */
    public void startIngestDataSource(String dataSourceId, boolean fullIngest) throws DocumentException, IOException, NoSuchMethodException, ClassNotFoundException, ParseException, ObjectNotFoundException, AlreadyExistsException {
        DataSourceContainer dataSourceContainer = getDataSourceContainer(dataSourceId);
        if (dataSourceContainer != null) {
            DataSource dataSource = dataSourceContainer.getDataSource();
            dataSource.setMaxRecord4Sample(-1);
            Task harvestTask = new DataSourceIngestTask(String.valueOf(dataSource.getNewTaskId()), dataSource.getId(), String.valueOf(fullIngest));

            if (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().isTaskExecuting(harvestTask)) {
                throw new AlreadyExistsException(dataSourceId);
            } else {
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().addOnetimeTask(harvestTask);
            }
        } else {
            throw new ObjectNotFoundException(dataSourceId);
        }
    }

    /**
     * Stop the data source ingestion
     * @param dataSourceId
     * @throws DocumentException
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws ObjectNotFoundException
     * @throws ClassNotFoundException
     * @throws ParseException
     */
    public void stopIngestDataSource(String dataSourceId, Task.Status status) throws DocumentException, IOException, NoSuchMethodException, ObjectNotFoundException, ClassNotFoundException, ParseException {
        DataSourceContainer dataSourceContainer = getDataSourceContainer(dataSourceId);
        if (dataSourceContainer != null) {
            List<Task> allTasks = new ArrayList<Task>();

            List<Task> runningTasks = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getRunningTasks();
            if (runningTasks != null) {
                allTasks.addAll(runningTasks);
            }

            List<Task> onetimeTasks = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().getOnetimeTasks();
            if (onetimeTasks != null) {
                allTasks.addAll(onetimeTasks);
            }

            DataSourceTask dummyTask; // necessary to compare the action with the running tasks
            dummyTask = new DataSourceIngestTask(null, dataSourceId, null); // only works for ingestion (not implemented for exportation)

            for (Task task : allTasks) {
                if (task instanceof ScheduledTask && task.getParameters() != null && task.getParameters().length > 0) {
                    dummyTask.setTaskId(((ScheduledTask)task).getId());
                }

                if (task.equalsAction((Task)dummyTask)) {
                    task.stop(status);
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().removeOnetimeTask(task);
                    return;
                }
            }
            throw new ObjectNotFoundException("Task not found");
        } else {
            throw new ObjectNotFoundException(dataSourceId);
        }
    }

    /**
     * Start the data source exportation
     * @param dataSourceId
     * @param recordsPerFile
     * @throws DocumentException
     * @throws AlreadyExistsException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws ParseException
     * @throws ObjectNotFoundException
     */
    public void startExportDataSource(String dataSourceId, String recordsPerFile, String metadataExportFormat) throws DocumentException, AlreadyExistsException, IOException, ClassNotFoundException, NoSuchMethodException, ParseException, ObjectNotFoundException {
        DefaultDataSourceContainer dataSourceContainer = (DefaultDataSourceContainer)getDataSourceContainer(dataSourceId);
        if (dataSourceContainer != null) {
            DataSource dataSource = dataSourceContainer.getDataSource();
            File exportDir = new File(dataSource.getExportDir().getAbsolutePath());
            FileUtils.forceMkdir(exportDir);

            if (recordsPerFile.equals("All"))
                recordsPerFile = "-1";

            if (metadataExportFormat == null) {
                // this is a non mandatory field for REST (if it is not defined by user, it uses the default format)
                metadataExportFormat = dataSource.getMetadataFormat();
            }

            Task exportTask = new DataSourceExportTask(String.valueOf(dataSource.getNewTaskId()), dataSource.getId(), exportDir.getAbsolutePath(), recordsPerFile, metadataExportFormat);

            if (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().isTaskExecuting(exportTask)) {
                throw new AlreadyExistsException(dataSourceId);
            }
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTaskManager().addOnetimeTask(exportTask);
            saveData();
        } else {
            throw new ObjectNotFoundException(dataSourceId);
        }
    }

    /**** RECORDS *****************************************************************************************************/
    public Node getRecord(Urn recordUrn) throws IOException, DocumentException, SQLException {
        byte[] recordMetadata = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().getRecord(recordUrn).getMetadata();
        SAXReader reader = new SAXReader();
        Document recordDocument = reader.read(new ByteArrayInputStream(recordMetadata));
        return recordDocument.getRootElement().detach();
    }

    public MessageType saveRecord(String recordId, String dataSourceId, String recordString) throws IOException, DocumentException {
        DataSourceContainer dataSourceContainer = getDataSourceContainer(dataSourceId);
        if (dataSourceContainer != null) {
            try {
                Element recordRoot = (Element)DocumentHelper.parseText(recordString).getRootElement().detach();
                DataSource dataSource = dataSourceContainer.getDataSource();
                RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(recordRoot, recordId, false, false);
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().processRecord(dataSource, recordRepox, null);

                return MessageType.OK;
            } catch (Exception e) {
                return MessageType.OTHER;
            }
        } else {
            return MessageType.NOT_FOUND;
        }
    }

    public MessageType deleteRecord(String recordId) throws IOException {
        try {
            Urn recordUrn = new Urn(recordId);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().deleteRecord(recordUrn);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().updateDeletedRecordsCount(recordUrn.getDataSourceId(), 1);

            return MessageType.OK;
        } catch (Exception e) {
            return MessageType.OTHER;
        }
    }

    public MessageType eraseRecord(String recordId) throws IOException {
        try {
            Urn recordUrn = new Urn(recordId);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().removeRecord(recordUrn);

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(recordUrn.getDataSourceId()).setLastLineCounted(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(recordUrn.getDataSourceId()).getLastLineCounted() - 1);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().updateDeletedRecordsCount(recordUrn.getDataSourceId(), 1);

            return MessageType.OK;
        } catch (Exception e) {
            return MessageType.OTHER;
        }

    }

    public MessageType importDataProviders(File file2read, File repoPath) {
        //todo
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<DataProvider> loadDataProvidersFromFile(File file2read, File repoPath) {
        //todo
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    public MetadataTransformationManager getMetadataTransformationManager() {
        //todo
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    /**** TASKS *****************************************************************************************************/

    public synchronized void saveOldTask(OldTask oldTask) {
        try {
            SAXReader reader = new SAXReader();

            Document document;
            if (oldTasksFile.exists())
                document = reader.read(oldTasksFile);
            else {
                document = DocumentHelper.createDocument();
                document.addElement("old-tasks");
            }

            Element oldTaskEl = document.getRootElement().addElement("oldTask");
            oldTaskEl.addAttribute("id", oldTask.getId());
            oldTaskEl.addElement("time").addText(oldTask.getYear() + "-" + oldTask.getMonth() + "-" + oldTask.getDay() + " " + oldTask.getHours() + ":" + oldTask.getMinutes());
            oldTaskEl.addElement("dataSourceSet").addText(oldTask.getDataSource().getId());
            oldTaskEl.addElement("logName").addText(oldTask.getLogName());
            oldTaskEl.addElement("ingestType").addText(oldTask.getIngestType());
            oldTaskEl.addElement("status").addText(oldTask.getStatus());
            oldTaskEl.addElement("records").addText(oldTask.getRecords());
            Element retries = oldTaskEl.addElement("retries");
            retries.addAttribute("max", oldTask.getRetryMax());
            retries.addAttribute("delay", oldTask.getDelay());
            retries.addText(oldTask.getRetries());

            try {
                XmlUtil.writePrettyPrint(oldTasksFile, document);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isIdValid(String id) {
        return (id.length() <= ID_MAX_SIZE) && Pattern.compile(ID_REGULAR_EXPRESSION).matcher(id).matches();
    }

    public String getDirPathFtp(String dataSourceId) {
        try {
            DataSource dataSource = null;
            while (dataSource == null) {
                dataSource = getDataSource(dataSourceId);
                if (dataSource != null && ((DirectoryImporterDataSource)dataSource).getRetrieveStrategy() instanceof FtpFileRetrieveStrategy) {
                    return ((DirectoryImporterDataSource)dataSource).getSourcesDirPath();
                }
            }
        } catch (DocumentException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private void setLastIngestDate(boolean useLastIngestDate, DataSource originalDataSet, DataSource targetDataSet) {
        if (!useLastIngestDate && originalDataSet.getLastUpdate() != null) {
            Date date = originalDataSet.getLastUpdate();
            date.setYear(70);
            targetDataSet.setLastUpdate(date);
        } else
            targetDataSet.setLastUpdate(originalDataSet.getLastUpdate());
    }

    public void removeLogsAndOldTasks(String dataSetId) throws IOException, DocumentException {
        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSetId).getDataSource().getOldTasksList().clear();
        removeOldTasks(dataSetId);
        File logsDir = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath() + File.separator + dataSetId, "logs");
        FileUtils.deleteDirectory(logsDir);
    }

}
