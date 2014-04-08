package pt.utl.ist.repox.dataProvider.dataSource;

public interface FileRetrieveStrategy {



    /**
     *
     * @return path of files
     * @throws Exception
     */

    public abstract boolean retrieveFiles(String dataSourceId);
}
