/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.repox.marc;

import pt.utl.ist.repox.dataProvider.dataSource.FileRetrieveStrategy;


public class DataSourceFolder implements FileRetrieveStrategy{

    public boolean retrieveFiles(String dataSourceId){
        
        return true;
    }

    public DataSourceFolder() {
    }
}


