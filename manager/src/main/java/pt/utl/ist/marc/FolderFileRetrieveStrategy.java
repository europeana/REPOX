/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.marc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;

import pt.utl.ist.dataProvider.dataSource.FileRetrieveStrategy;

/**
 */
@XmlRootElement(name = "FolderFileRetrieveStrategy")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An FolderFileRetrieveStrategy")
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
