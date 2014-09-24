package pt.utl.ist.repox.dataProvider;

import org.dom4j.DocumentException;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.task.OldTask;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 31-03-2011
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public interface DataManager {

    public void saveData() throws IOException, DocumentException;

    public DataProvider createDataProvider(String name, String country, String description) throws IOException, AlreadyExistsException;

    public DataProvider updateDataProvider(String id, String name, String country, String description) throws ObjectNotFoundException, IOException;

    public DataProvider updateDataProvider(DataProvider dataProvider, String dataProviderId) throws IOException, ObjectNotFoundException;

    public void deleteDataProvider(String dataProviderId) throws IOException, ObjectNotFoundException;

    public List<DataProvider> getDataProviders() throws DocumentException, IOException;

    public DataProvider getDataProvider(String dataProviderId);

    public DataProvider getDataProviderParent(String dataSourceId) ;

    public List<DataProvider> loadDataProvidersFromFile(File file2Read, File repositoryPath) throws DocumentException, IOException, ParseException;

    public MessageType addDataSourceContainer(DataSourceContainer dataSourceContainer, String dataProviderId);

    public MessageType updateDataSourceContainer(DataSourceContainer dataSourceContainer, String oldDataSourceId);

    public boolean moveDataSource(String newDataProviderID, String idDataSource2Move) throws IOException, DocumentException;

    public void setDataSetSampleState(boolean isSample, DataSource dataSource);

    public void deleteDataSourceContainer(String dataSourceId) throws IOException, ObjectNotFoundException;

    public void startIngestDataSource(String dataSourceId, boolean fullIngest) throws DocumentException, IOException, NoSuchMethodException, ClassNotFoundException, ParseException, ObjectNotFoundException, AlreadyExistsException;

    public void stopIngestDataSource(String dataSourceId, Task.Status status) throws DocumentException, IOException, NoSuchMethodException, ObjectNotFoundException, ClassNotFoundException, ParseException;

    public void startExportDataSource(String dataSourceId, String recordsPerFile, String metadataExportFormat) throws DocumentException, AlreadyExistsException, IOException, ClassNotFoundException, NoSuchMethodException, ParseException, ObjectNotFoundException;

    public DataSourceContainer getDataSourceContainer(String dataSourceId) throws DocumentException, IOException;

    public MessageType importDataProviders(File file2read, File repoPath);

    public MessageType removeTransformationFromDataSource(String transformationId);

    //public void updateDataSourceId(String oldDataSourceId, String newDataSourceId)  throws IOException, DocumentException, SQLException;

    //public void updateDataSource(DataSource dataSource) throws IOException, DocumentException;

    //public void deleteDataSource(String dataSourceId) throws DocumentException, IOException;

    public boolean isIdValid(String id);

    public HashMap<String, DataSourceContainer> loadDataSourceContainers() throws DocumentException, IOException;

    public MetadataTransformationManager getMetadataTransformationManager();

    public void saveOldTask(OldTask oldTask);

    public void removeOldTasks(String dataSourceId);

    public void removeLogsAndOldTasks(String dataSourceId) throws IOException, DocumentException;

    public String getDirPathFtp(String dataSourceId);

    public List<Object> getAllDataList();

    public int getShowSize();
}
