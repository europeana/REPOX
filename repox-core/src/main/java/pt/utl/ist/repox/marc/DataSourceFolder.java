/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.repox.marc;

import pt.utl.ist.repox.dataProvider.dataSource.FileRetrieveStrategy;

/**
 */
public class DataSourceFolder implements FileRetrieveStrategy {

    @Override
    public boolean retrieveFiles(String dataSourceId) {

        return true;
    }

    /**
     * Creates a new instance of this class.
     */
    public DataSourceFolder() {
    }
}
