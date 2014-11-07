package pt.utl.ist.dataProvider;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.dom4j.Element;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * @author Gilberto Pedrosa
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 01, 2014
 */

@XmlRootElement(name = "datasetContainer")
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "containerType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultDataSourceContainer.class, name = "DEFAULT")
})
@XmlSeeAlso({ DefaultDataSourceContainer.class})
@ApiModel(value = "A Dataset Container", discriminator="dataSourceContainerType", subTypes={DefaultDataSourceContainer.class})
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
