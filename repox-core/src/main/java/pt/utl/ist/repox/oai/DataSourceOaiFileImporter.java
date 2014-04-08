package pt.utl.ist.repox.oai;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.dataProvider.DataManagerDefault;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.dataProvider.DataSourceContainerDefault;
import pt.utl.ist.repox.dataProvider.dataSource.IdGenerated;
import pt.utl.ist.repox.dataProvider.dataSource.IdProvided;
import pt.utl.ist.repox.metadataTransformation.MetadataFormat;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.util.exceptions.AlreadyExistsException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class DataSourceOaiFileImporter {

	public void doImport(File sourceFile) throws IOException, DocumentException, AlreadyExistsException {
		BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
		String currentLine;
		
		while((currentLine = reader.readLine()) != null) {
			String[] tokens = currentLine.split("\t");
			String dataProviderName = tokens[0];
			String dataSourceName = tokens[1];
			String oaiUrl = tokens[2];
			
			DataProvider newDataProvider = new DataProvider();
			newDataProvider.setName(dataProviderName);
			newDataProvider.setId(DataProvider.generateId(dataProviderName));


            HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();


			DataSourceOai dataSourceOai = new DataSourceOai(newDataProvider, dataSourceName, dataSourceName, "", "",
					MetadataFormat.oai_dc.toString(), oaiUrl, null, new IdProvided(), null);

            dataSourceContainers.put(dataSourceOai.getId(), new DataSourceContainerDefault(dataSourceOai));

			newDataProvider.setDataSourceContainers(dataSourceContainers);
			
			((DataManagerDefault)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager()).addDataProvider(newDataProvider);
		}
	}
	
	public static void main(String[] args) throws IOException, DocumentException, AlreadyExistsException {
		DataSourceOaiFileImporter importer = new DataSourceOaiFileImporter();
		
		File file = new File("f:/dreis/Desktop/sources.txt");
		importer.doImport(file);
	}
}
