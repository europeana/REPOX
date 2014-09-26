package pt.utl.ist.repox.dataProvider;

import org.dom4j.DocumentException;

import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.task.OldTask;
import pt.utl.ist.repox.task.Task;
import pt.utl.ist.repox.util.exceptions.AlreadyExistsException;
import pt.utl.ist.repox.util.exceptions.ObjectNotFoundException;

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

    void saveData() throws IOException, DocumentException;

    DataProvider createDataProvider(String name, String country, String description) throws IOException, AlreadyExistsException;

    DataProvider updateDataProvider(String id, String name, String country, String description) throws ObjectNotFoundException, IOException;

    DataProvider updateDataProvider(DataProvider dataProvider, String dataProviderId) throws IOException, ObjectNotFoundException;

    void deleteDataProvider(String dataProviderId) throws IOException, ObjectNotFoundException;

    List<DataProvider> getDataProviders() throws DocumentException, IOException;

    DataProvider getDataProvider(String dataProviderId);

    DataProvider getDataProviderParent(String dataSourceId) ;

    List<DataProvider> loadDataProvidersFromFile(File file2Read, File repositoryPath) throws DocumentException, IOException, ParseException;

    MessageType addDataSourceContainer(DataSourceContainer dataSourceContainer, String dataProviderId);

    MessageType updateDataSourceContainer(DataSourceContainer dataSourceContainer, String oldDataSourceId);

    boolean moveDataSource(String newDataProviderID, String idDataSource2Move) throws IOException, DocumentException;

    void setDataSetSampleState(boolean isSample, DataSource dataSource);

    void deleteDataSourceContainer(String dataSourceId) throws IOException, ObjectNotFoundException;

    void startIngestDataSource(String dataSourceId, boolean fullIngest) throws DocumentException, IOException, NoSuchMethodException, ClassNotFoundException, ParseException, ObjectNotFoundException, AlreadyExistsException;

    void stopIngestDataSource(String dataSourceId, Task.Status status) throws DocumentException, IOException, NoSuchMethodException, ObjectNotFoundException, ClassNotFoundException, ParseException;

    void startExportDataSource(String dataSourceId, String recordsPerFile, String metadataExportFormat) throws DocumentException, AlreadyExistsException, IOException, ClassNotFoundException, NoSuchMethodException, ParseException, ObjectNotFoundException;

    DataSourceContainer getDataSourceContainer(String dataSourceId) throws DocumentException, IOException;

    MessageType importDataProviders(File file2read, File repoPath);

    MessageType removeTransformationFromDataSource(String transformationId);

    //void updateDataSourceId(String oldDataSourceId, String newDataSourceId)  throws IOException, DocumentException, SQLException;

    //void updateDataSource(DataSource dataSource) throws IOException, DocumentException;

    //void deleteDataSource(String dataSourceId) throws DocumentException, IOException;

    boolean isIdValid(String id);

    HashMap<String, DataSourceContainer> loadDataSourceContainers() throws DocumentException, IOException;

    MetadataTransformationManager getMetadataTransformationManager();

    void saveOldTask(OldTask oldTask);

    void removeOldTasks(String dataSourceId);

    void removeLogsAndOldTasks(String dataSourceId) throws IOException, DocumentException;

    String getDirPathFtp(String dataSourceId);

    List<Object> getAllDataList();

    int getShowSize();
}
