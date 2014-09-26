package pt.utl.ist.repox.dataProvider.dataSource;

/**
 */
public interface FileRetrieveStrategy {

    /**
     * 
     * @param dataSourceId 
     * @return path of files
     */
    boolean retrieveFiles(String dataSourceId);
}
