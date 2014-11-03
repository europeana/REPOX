package pt.utl.ist.dataProvider;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.dom4j.Element;

import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.sru.SruRecordUpdateDataSource;
import pt.utl.ist.z3950.DataSourceZ3950;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by IntelliJ IDEA. User: Gilberto Pedrosa Date: 28-06-2011 Time: 17:10
 * To change this template use File | Settings | File Templates.
 */

@XmlRootElement(name = "datasetContainer")
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultDataSourceContainer.class, name = "DEFAULT")
})
@XmlSeeAlso({ DefaultDataSourceContainer.class})
@ApiModel(value = "A Dataset Container")
public abstract class DataSourceContainer {
    @XmlElement
    @ApiModelProperty(position = 1)
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public abstract Element createElement();
}
