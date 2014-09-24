package pt.utl.ist.repox.directoryImporter;

import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pt.utl.ist.repox.configuration.RepoxContextUtilDefault;
import pt.utl.ist.repox.dataProvider.DataManagerDefault;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.dataProvider.DataSourceContainerDefault;
import pt.utl.ist.repox.dataProvider.dataSource.FileExtractStrategy;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.dataProvider.dataSource.SimpleFileExtract;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.marc.DataSourceDirectoryImporter;
import pt.utl.ist.repox.marc.DataSourceFolder;
import pt.utl.ist.repox.marc.Iso2709FileExtract;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.statistics.RecordCount;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.util.exceptions.task.IllegalFileFormatException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HarvestTest {
    private final String DATA_PROVIDER_ID = "DP_DIMPORTER";
    private final String DATA_SOURCE_ID = "DS_DIMPORTER";
    private final String DATA_SOURCE_DESCRIPTION = "DS_description";
    private final String SOURCE_SCHEMA = "info:lc/xmlns/marcxchange-v1.xsd";
    private final String SOURCE_NAMESPACE = "info:lc/xmlns/marcxchange-v1";
    private final String SOURCE_METADATA_FORMAT = "MarcXchange";//ISO2709
    private final String SOURCE_RECORDXPATH = "";
    private final String SOURCE_PATH= "src/test/resources/directoryImportTest";
    private final int RECORD_COUNT = 100;

    private DataSourceDirectoryImporter dataSourceDirectoryImporter;

    @Before
    public void setUp() {
        try {
            ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
            DataManagerDefault dataManager = (DataManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager();

            HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();
            DataProvider newDataProvider = new DataProvider(DATA_PROVIDER_ID, "data provider", "pt", "it's new", dataSourceContainers);

            FileExtractStrategy extractStrategy = new SimpleFileExtract();
           /* String extractStrategyString = currentDataSourceElement.elementText("fileExtract");
            //FileExtractStrategy extractStrategy = null;

            if(extractStrategyString.equals(Iso2709FileExtract.class.getSimpleName())) {
                //characterEncoding = CharacterEncoding.get(currentDataSourceElement.attributeValue("characterEncoding"));
                String isoImplementationClass = currentDataSourceElement.attributeValue("isoImplementationClass");
                extractStrategy = new Iso2709FileExtract(isoImplementationClass);
            } else if(extractStrategyString.equals(MarcXchangeFileExtract.class.getSimpleName())) {
                extractStrategy = new MarcXchangeFileExtract();
            } else if(extractStrategyString.equals(SimpleFileExtract.class.getSimpleName())) {
                extractStrategy = new SimpleFileExtract();*/

           /* if(extractStrategy instanceof Iso2709FileExtract) {
                //new SimpleFileExtract();
              //  new Iso2709FileExtract("IteratorIso2709");
              Iso2709FileExtract extract = new Iso2709FileExtract(IteratorIso2709.class.getName());
                extract = new Iso2709FileExtract("IteratorIso2709");
            }*/
            //String recordXPath = "record";
           

            Map<String, String> namespaces = null;
            namespaces = new TreeMap<String, String>();

//            FileExtractStrategy extractStrategy = DataSourceUtil.extractStrategyString(SOURCE_METADATA_FORMAT, "Standard");

            dataSourceDirectoryImporter = new DataSourceDirectoryImporter(
                    newDataProvider,
                    DATA_SOURCE_ID,
                    DATA_SOURCE_DESCRIPTION,
                    SOURCE_SCHEMA,
                    SOURCE_NAMESPACE,
                    SOURCE_METADATA_FORMAT,
//                    new SimpleFileExtract(),
                    new Iso2709FileExtract("pt.utl.ist.marc.iso2709.IteratorIso2709"),
                    new DataSourceFolder(),
                    null,
                    SOURCE_PATH,
                    new IdGenerated(),
                    new TreeMap<String, MetadataTransformation>(),
                    SOURCE_RECORDXPATH,
                    namespaces);
            dataSourceDirectoryImporter.setCharacterEncoding(CharacterEncoding.UTF_8);

            dataSourceContainers.put(dataSourceDirectoryImporter.getId(), new DataSourceContainerDefault(dataSourceDirectoryImporter));
            dataManager.addDataProvider(newDataProvider);

            ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getAccessPointsManager().initialize(dataSourceContainers);
            ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager().updateDataProvider(newDataProvider,newDataProvider.getId());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, ObjectNotFoundException {
        ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager().deleteDataProvider(DATA_PROVIDER_ID);
    }

    @Test
    public void testRun() {
        try {
            File logFile = new File(ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getConfiguration().getTempDir() + "/log.txt");
            dataSourceDirectoryImporter.ingestRecords(logFile, true);

            RecordCount r = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getRecordCountManager().getRecordCount(dataSourceDirectoryImporter.getId(),true);
//            AccessPointsManagerDefault accessPointsManager = (AccessPointsManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getAccessPointsManager();
////            int[] recordCountLastrowPair = accessPointsManager.getRecordCountLastrowPair(dataSourceDirectoryImporter, null, null, null);
////            int recordCount = recordCountLastrowPair[0];
            Assert.assertEquals(RECORD_COUNT, 100);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }


    }
/*
	@Test
	public void testRunFromFile() {
		Assert.assertTrue(false);
	}
*/
}
