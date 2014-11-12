package pt.utl.ist.dataProvider;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Node;

import pt.utl.ist.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.task.OldTask;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.Urn;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

/**
 * @author GPedrosa
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 01, 2014
 */
public interface DataManager {

    //    void loadAllDataList()

    List<Object> getAllDataList();

    int getShowSize();

    //********************READ/WRITE XML file ********************/
    void saveData() throws IOException, DocumentException;

    //********************AGGREGATOR ********************/    
    Aggregator createAggregator(String aggregatorId, String name, String nameCode, String homepageUrl) throws InvalidArgumentsException, DocumentException, IOException, AlreadyExistsException;

    Aggregator updateAggregator(String aggregatorId, String newAggregatorId, String name, String nameCode, String homepage) throws ObjectNotFoundException, InvalidArgumentsException, IOException, AlreadyExistsException;

    void deleteAggregator(String aggregatorId) throws ObjectNotFoundException, IOException, DocumentException;

    Aggregator getAggregator(String aggregatorId);

    Aggregator getAggregatorParent(String dataProviderId);

    boolean checkIfAggregatorExists(List<Aggregator> aggregators, Aggregator aggregatorToCheck);

    List<Aggregator> getAggregators();

    List<Aggregator> getAggregatorsListSorted(int offset, int number) throws IndexOutOfBoundsException;

    //********************DATA PROVIDER's ********************/
    DataProvider createDataProvider(String aggregatorId, String providerId, String name, String country, String description, String nameCode,
            String homepage, String dataSetType, String email) throws InvalidArgumentsException, AlreadyExistsException, IOException, ObjectNotFoundException;

    DataProvider createDataProvider(String name, String country, String description) throws IOException, AlreadyExistsException;

    DataProvider updateDataProvider(String newAggregatorId, String providerId, String newProviderId, String name, String country, String description, String nameCode, String homepage,
            String dataSetType, String email) throws ObjectNotFoundException, InvalidArgumentsException, IOException, AlreadyExistsException;

    DataProvider updateDataProvider(String id, String name, String country, String description);

    DataProvider updateDataProvider(DataProvider dataProvider, String dataProviderId) throws ObjectNotFoundException;

    boolean moveDataProvider(String newAggregatorId, String idDataProvider2Move) throws IOException;

    void deleteDataProvider(String dataProviderId) throws IOException, ObjectNotFoundException;

    DataProvider getDataProvider(String dataProviderId);

    DataProvider getDataProvider(String aggregatorId, String name);

    DataProvider getDataProviderParent(String dataSourceId);

    List<DataProvider> getDataProviders() throws DocumentException, IOException;

    List<DataProvider> getDataProvidersListSorted(String aggregatorId, int offset, int number) throws ObjectNotFoundException;

    boolean checkIfDataProviderExists(String aggregatorId, DataProvider dataProvider);

    MessageType importDataProviders(File file2read, File repoPath);

    List<DataProvider> loadDataProvidersFromFile(File file2Read, File repositoryPath) throws DocumentException, IOException, ParseException;

    //********************DATA SOURCE CONTAINER ********************/
    boolean moveDataSource(String newDataProviderID, String idDataSource2Move) throws IOException, DocumentException;

    void setDataSetSampleState(boolean isSample, DataSource dataSource);

    MessageType removeTransformationFromDataSource(String transformationId);

    MessageType addDataSourceContainer(DataSourceContainer dataSourceContainer, String dataProviderId);

    //    MessageType deleteDataSource(String dataSourceId) throws ObjectNotFoundException;

    MessageType updateDataSourceContainer(DataSourceContainer dataSourceContainer, String oldDataSourceId);

    HashMap<String, DataSourceContainer> loadDataSourceContainers() throws DocumentException, IOException;

    void deleteDataSourceContainer(String dataSourceId) throws IOException, ObjectNotFoundException;

    DataSourceContainer getDataSourceContainer(String dataSourceId) throws DocumentException, IOException;

    List<DataSourceContainer> getDataSourceContainerListSorted(String providerId, int offset, int number) throws ObjectNotFoundException;

    void updateDataSourceId(String oldDataSourceId, String newDataSourceId) throws IOException, DocumentException, SQLException;

    //    DataSource getDataSource(String dataSourceId) throws DocumentException, IOException;

    void startIngestDataSource(String dataSourceId, boolean fullIngest) throws SecurityException, NoSuchMethodException, DocumentException, IOException, AlreadyExistsException,
            ClassNotFoundException, ParseException, ObjectNotFoundException;

    void stopIngestDataSource(String dataSourceId, Task.Status status) throws DocumentException, IOException, NoSuchMethodException, ObjectNotFoundException, ClassNotFoundException, ParseException;

    void startExportDataSource(String dataSourceId, String recordsPerFile, String metadataExportFormat) throws DocumentException, AlreadyExistsException, IOException, ClassNotFoundException,
            NoSuchMethodException, ParseException, ObjectNotFoundException;

    String getDirPathFtp(String dataSourceId);

    //********************RECORDS ********************/
    Node getRecord(Urn recordUrn) throws IOException, DocumentException, SQLException;

    MessageType saveRecord(String recordId, String dataSourceId, String recordString) throws IOException, DocumentException;

    MessageType deleteRecord(String recordId) throws IOException;

    MessageType eraseRecord(String recordId) throws IOException;

    MetadataTransformationManager getMetadataTransformationManager();

    //********************TASKS ********************/
    void saveOldTask(OldTask oldTask);

    boolean isIdValid(String id);

    void removeLogsAndOldTasks(String dataSourceId) throws IOException, DocumentException;
}
