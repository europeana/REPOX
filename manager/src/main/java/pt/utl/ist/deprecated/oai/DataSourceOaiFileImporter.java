package pt.utl.ist.deprecated.oai;
//package pt.utl.ist.repox.oai;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.HashMap;
//
//import org.dom4j.DocumentException;
//
//import pt.utl.ist.repox.dataProvider.DefaultDataManager;
//import pt.utl.ist.repox.dataProvider.DataProvider;
//import pt.utl.ist.repox.dataProvider.DataSourceContainer;
//import pt.utl.ist.repox.dataProvider.DefaultDataSourceContainer;
//import pt.utl.ist.repox.dataProvider.dataSource.IdProvidedRecordIdPolicy;
//import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
//import pt.utl.ist.repox.util.ConfigSingleton;
//import pt.utl.ist.util.exceptions.AlreadyExistsException;
//
///**
// */
//public class DataSourceOaiFileImporter {
//
//    /**
//     * @param sourceFile
//     * @throws IOException
//     * @throws DocumentException
//     * @throws AlreadyExistsException
//     */
//    public void doImport(File sourceFile) throws IOException, DocumentException, AlreadyExistsException {
//        BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
//        String currentLine;
//
//        while ((currentLine = reader.readLine()) != null) {
//            String[] tokens = currentLine.split("\t");
//            String dataProviderName = tokens[0];
//            String dataSourceName = tokens[1];
//            String oaiUrl = tokens[2];
//
//            DataProvider newDataProvider = new DataProvider();
//            newDataProvider.setName(dataProviderName);
//            newDataProvider.setId(DataProvider.generateId(dataProviderName));
//
//            HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();
//
//            OaiDataSource dataSourceOai = new OaiDataSource(newDataProvider, dataSourceName, dataSourceName, "", "", MetadataFormat.oai_dc.toString(), oaiUrl, null, new IdProvidedRecordIdPolicy(), null);
//
//            dataSourceContainers.put(dataSourceOai.getId(), new DefaultDataSourceContainer(dataSourceOai));
//
//            newDataProvider.setDataSourceContainers(dataSourceContainers);
//
//            ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).addDataProvider(newDataProvider);
//        }
//    }
//
//    /**
//     * @param args
//     * @throws IOException
//     * @throws DocumentException
//     * @throws AlreadyExistsException
//     */
//    public static void main(String[] args) throws IOException, DocumentException, AlreadyExistsException {
//        DataSourceOaiFileImporter importer = new DataSourceOaiFileImporter();
//
//        File file = new File("f:/dreis/Desktop/sources.txt");
//        importer.doImport(file);
//    }
//}
