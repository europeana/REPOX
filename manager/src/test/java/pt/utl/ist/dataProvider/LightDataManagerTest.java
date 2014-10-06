package pt.utl.ist.dataProvider;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxConfiguration;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.metadataTransformation.MetadataFormat;
import pt.utl.ist.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.util.CompareUtil;
import pt.utl.ist.util.PropertyUtil;
import pt.utl.ist.util.XmlUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.util.exceptions.task.IllegalFileFormatException;

public class LightDataManagerTest {
    private final String OUTPUT_CONFIGURATION_FILE_NAME = DefaultRepoxContextUtil.DATA_PROVIDERS_FILENAME;
    private final String OUTPUT_CONFIGURATION_FILE_NAME_ALT = "dataProviders.temp.xml";
    public static final String CONFIG_FILE = "configuration.properties";

    private File configurationFile;
    private File configurationFileAlt;
    private File repositoryPath;

    private DefaultRepoxConfiguration configurationDefault;

    private List<DataProvider> getTestDataProviders() {
        List<DataProvider> dataProviders = new ArrayList<DataProvider>();
        try{
            MetadataTransformationManager transformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataTransformationManager();
            MetadataSchemaManager metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataSchemaManager();

            Properties configurationProperties = PropertyUtil.loadCorrectedConfiguration(CONFIG_FILE);
            configurationDefault = new DefaultRepoxConfiguration(configurationProperties);

            File oldTasksFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getXmlConfigPath(), DefaultRepoxContextUtil.OLD_TASKS_FILENAME);
            LightDataManager dataManager = new LightDataManager(configurationFile, transformationManager,
                    metadataSchemaManager, repositoryPath, oldTasksFile,configurationDefault);

            DataProvider provider = dataManager.createDataProvider("DPName", "pt", "DP_description");


            DataSource source1 = dataManager.createDataSourceOai(provider.getId(), "DS1_Id", "DS1_description",
                    "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "http://www.europeana.eu/schemas/ese/",
                    MetadataFormat.ese.name(), "http://bd1.inesc-id.pt:8080/repoxel/OAIHandler", "bmfinancas", null,null, null);
            source1.setExportDir(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath() + File.separator + "export");

            DataSource source2 = dataManager.createDataSourceOai(provider.getId(), "DS2_Id", "DS2_description",
                    "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "http://www.europeana.eu/schemas/ese/",
                    MetadataFormat.ese.name(), "http://bd1.inesc-id.pt:8080/repoxel/OAIHandler", "bmfinancas", null,null, null);
            source2.setExportDir(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath() + File.separator + "export");
            dataProviders.add(provider);

        }
        catch (AlreadyExistsException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidArgumentsException e) {
            e.printStackTrace();
        }
        return dataProviders;
    }


    private boolean dataProvidersEqual(List<DataProvider> sourceDataProviders, List<DataProvider> targetDataProviders) {
        if(sourceDataProviders == null && targetDataProviders == null) {
            return true;
        }
        else if((sourceDataProviders == null || targetDataProviders == null)
                || sourceDataProviders.size() != targetDataProviders.size()) {
            return false;
        }

        for (int i = 0; i < sourceDataProviders.size(); i++) {
            DataProvider sourceDataProvider = sourceDataProviders.get(i);
            DataProvider targetDataProvider = targetDataProviders.get(i);
            if(!dataProvidersEqual(sourceDataProvider, targetDataProvider)) {
                return false;
            }
        }

        return true;
    }

    private boolean dataProvidersEqual(DataProvider sourceDataProvider, DataProvider targetDataProvider) {
        return CompareUtil.compareObjectsAndNull(sourceDataProvider.getId(), targetDataProvider.getId())
                && CompareUtil.compareObjectsAndNull(sourceDataProvider.getDescription(), targetDataProvider.getDescription())
                && dataSourcesEqual(new HashMap<String, DataSourceContainer>(sourceDataProvider.getDataSourceContainers()),
                new HashMap<String, DataSourceContainer>(targetDataProvider.getDataSourceContainers()));
    }


    private boolean dataSourcesEqual(HashMap<String, DataSourceContainer> dataSourceContainers, HashMap<String, DataSourceContainer> otherDataSourceContainers) {
        boolean result = true;
        if(dataSourceContainers.size() != otherDataSourceContainers.size()) {
            result = false;
        }
        else {
            if(dataSourceContainers.keySet().equals(otherDataSourceContainers.keySet())){
                result = true;
            }
        }

        return result;
    }

    @Before
    public void setUp() throws ClassNotFoundException, IOException, DocumentException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException {

        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());

        String xmlBasedir = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getXmlConfigPath();
        repositoryPath = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getRepositoryPath());
        configurationFile = new File(xmlBasedir + "/" + OUTPUT_CONFIGURATION_FILE_NAME);
        configurationFileAlt = new File(xmlBasedir + "/" + OUTPUT_CONFIGURATION_FILE_NAME_ALT);
    }

    @Test
    public void testXml() throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, AlreadyExistsException {
        MetadataTransformationManager transformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataTransformationManager();
        MetadataSchemaManager metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataSchemaManager();

        File oldTasksFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getXmlConfigPath(), DefaultRepoxContextUtil.OLD_TASKS_FILENAME);
        LightDataManager dataManager = new LightDataManager(configurationFile, transformationManager, metadataSchemaManager, repositoryPath, oldTasksFile,configurationDefault);

        List<DataProvider> dataProviders = getTestDataProviders();
        for(DataProvider dp: dataProviders) {
            dataManager.addDataProvider(dp);
        }

        Element dataSourcesElement = XmlUtil.getRootElement(configurationFile);
        //
        LightDataManager dataManager2 = new LightDataManager(configurationFile, transformationManager, metadataSchemaManager, repositoryPath, oldTasksFile,configurationDefault);
        List<DataProvider> loadedDataProviders = dataManager2.getDataProviders();

        LightDataManager dataManagerReloaded = new LightDataManager(configurationFileAlt, transformationManager, metadataSchemaManager, repositoryPath, oldTasksFile,configurationDefault);
        for(DataProvider dp: loadedDataProviders) {
            dataManagerReloaded.addDataProvider(dp);
        }

        Element loadedDataSourcesElement = XmlUtil.getRootElement(configurationFileAlt);
        Assert.assertTrue(XmlUtil.elementsEqual(dataSourcesElement, loadedDataSourcesElement));
        //Assert.assertFalse(XmlUtil.elementsEqual(dataSourcesElement, loadedDataSourcesElement));

    }
    @After
    public void tearDown() {
        if(configurationFile.exists()) {
            configurationFile.delete();
        }

        if(configurationFileAlt.exists()) {
            configurationFileAlt.delete();
        }
    }
}
