/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.marc;

import pt.utl.ist.dataProvider.dataSource.FileRetrieveStrategy;

/**
 */
public class FolderFileRetrieveStrategy implements FileRetrieveStrategy {
    public static final String FOLDERFILERETRIEVESTRATEGY = "FolderFileRetrieveStrategy";

    @Override
    public boolean retrieveFiles(String dataSourceId) {

        return true;
    }

    /**
     * Creates a new instance of this class.
     */
    public FolderFileRetrieveStrategy() {
    }
}
