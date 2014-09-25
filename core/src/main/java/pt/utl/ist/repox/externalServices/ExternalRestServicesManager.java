package pt.utl.ist.repox.externalServices;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.dataProvider.DataSource;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX. User: Edmundo Date: 12-12-2011 Time: 14:18
 */
public class ExternalRestServicesManager {
    private static final Logger       log = Logger.getLogger(ExternalRestServicesManager.class);

    // Map of sourceMetadataFormat to list of available ExternalRestServices for that source
    private List<ExternalRestService> externalRestServices;
    private File                      configurationFile;

    /**
     * Creates a new instance of this class.
     * 
     * @param configurationFile
     * @throws IOException
     * @throws DocumentException
     */
    public ExternalRestServicesManager(File configurationFile) throws IOException, DocumentException {
        super();
        this.configurationFile = configurationFile;
        loadExternalRestServices();
    }

    @SuppressWarnings("javadoc")
    public List<ExternalRestService> getExternalRestServices() throws IOException, DocumentException {
        return externalRestServices;
    }

    @SuppressWarnings("javadoc")
    protected void setExternalRestServices(List<ExternalRestService> externalRestServices) {
        this.externalRestServices = externalRestServices;
    }

    @SuppressWarnings("javadoc")
    public File getConfigurationFile() {
        return configurationFile;
    }

    @SuppressWarnings("javadoc")
    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    /**
     * @param externalServiceId
     * @return ExternalRestService
     * @throws IOException
     * @throws DocumentException
     */
    public synchronized ExternalRestService loadExternalRestService(String externalServiceId) throws IOException, DocumentException {
        for (ExternalRestService externalRestService : externalRestServices) {
            if (externalRestService.getId().equals(externalServiceId)) { return externalRestService; }
        }

        return null;
    }

    /**
     * 
     */
    public synchronized void loadExternalRestServices() {
        externalRestServices = new ArrayList<ExternalRestService>();
        if (!configurationFile.exists()) { return; }

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(configurationFile);
            List<Element> externalServicesElements = document.getRootElement().elements();

            for (Element currentElement : externalServicesElements) {
                String id = currentElement.attributeValue("id");
                String name = currentElement.attributeValue("name");
                String uri = currentElement.attributeValue("uri");
                String statusUri = currentElement.attributeValue("statusUri");
                String externalResultsUri = currentElement.attributeValue("externalResultsUri");
                String type = currentElement.attributeValue("type");
                String externalServiceType = currentElement.attributeValue("externalServiceType");
                if (externalServiceType == null || externalServiceType.isEmpty()) externalServiceType = ExternalServiceType.MONITORED.name();
                ExternalRestService externalRestService = new ExternalRestService(id, name, uri, statusUri, type, ExternalServiceType.valueOf(externalServiceType));
                if (externalResultsUri != null) externalRestService.setExternalResultsUri(externalResultsUri);

                List list = currentElement.selectNodes("parameters/parameter");
                for (Object node : list) {
                    Node n = (Node)node;
                    String parameterName = n.valueOf("@name");
                    String parameterType = n.valueOf("@type");
                    //                    String value = n.valueOf("value");
                    boolean required = Boolean.parseBoolean(n.valueOf("@required"));
                    String exampleStr = n.valueOf("@example");
                    String semanticsStr = n.valueOf("@semantics");

                    ServiceParameter serviceParameter = new ServiceParameter(parameterName, parameterType, required, exampleStr, semanticsStr);
                    if (parameterType.equals("COMBO_FIELD")) {
                        List comboList = n.selectNodes("comboValues/comboValue");
                        for (Object comboNode : comboList) {
                            Node nodeC = (Node)comboNode;
                            String comboVal = nodeC.getText();
                            serviceParameter.getComboValues().add(comboVal);
                        }
                    }
                    externalRestService.getServiceParameters().add(serviceParameter);
                }

                externalRestServices.add(externalRestService);
            }
        } catch (DocumentException e) {
            log.error("Error loading the external services file (externalServices.xml).");
        }
    }

    /**
     * @param externalRestService
     */
    public void replaceExternalServiceInAllDataSets(ExternalRestService externalRestService) {
        for (Object object : ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getAllDataList()) {
            if (object instanceof DataSourceContainer) {
                DataSource dataSource = ((DataSourceContainer)object).getDataSource();
                for (ExternalRestService externalRestService1 : dataSource.getExternalRestServices()) {
                    if (externalRestService1.getId().equals(externalRestService.getId())) {
                        externalRestService1.setId(externalRestService.getId());
                        externalRestService1.setExternalServiceType(externalRestService.getExternalServiceType());
                        externalRestService1.setName(externalRestService.getName());
                        externalRestService1.setType(externalRestService.getType());
                        externalRestService1.setUri(externalRestService.getUri());
                        externalRestService1.setStatusUri(externalRestService.getStatusUri());
                        externalRestService1.setExternalResultsUri(externalRestService.getExternalResultsUri());
                        // TODO: Update external service parameters
                        //                        externalRestService1.setServiceParameters(externalRestService.getServiceParameters());
                    }
                }
            }
        }

        try {
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param serviceId
     * @param dataSetId
     * @throws Exception
     */
    public void startExternalService(String serviceId, String dataSetId) throws Exception {
        DataSource dataSource = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataSourceContainer(dataSetId).getDataSource();
        File logFile = dataSource.getLogFile(dataSetId + "_single_service_run_");
        ExternalRestServiceContainer externalRestServiceContainer = new ExternalRestServiceContainer(ExternalServiceStates.ContainerType.SINGLE_SERVICE_EXECUTION, dataSource, logFile);

        for (ExternalRestService externalRestService : dataSource.getExternalRestServices()) {
            if (externalRestService.getId().equals(serviceId)) {
                externalRestServiceContainer.addExternalService(new ExternalRestServiceThread(externalRestService, externalRestServiceContainer, logFile));
            }
        }
    }

}