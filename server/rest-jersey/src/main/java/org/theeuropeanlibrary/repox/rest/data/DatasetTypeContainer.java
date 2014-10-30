/* DatasetTypeContainer.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.oai.OaiDataSource;

/**
 * Container for communication with rest services. Specifies the actual dataset type with a field.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 30, 2014
 */
@XmlRootElement(name = "datasetTypeContainer")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "A DatasetTypeContainer")
public class DatasetTypeContainer {
    @XmlElement
    @ApiModelProperty(position = 0)
   private DataSource datasource = null;
    @XmlElement
    @ApiModelProperty(position = 1)
   private DataSourceType dataSourceType = null;

    /**
     * No argument constructor needed for JAXB.
     */
    public DatasetTypeContainer() {
    }

    /**
     * Creates a new instance of this class.
     * @param datasource
     * @param dataSourceType
     */
    public DatasetTypeContainer(DataSource datasource, DataSourceType dataSourceType) {
        super();
        this.datasource = datasource;
        this.dataSourceType = dataSourceType;
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public void setDatasource(OaiDataSource datasource) {
        this.datasource = datasource;
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }
    
    public void setDataSourceType(DataSourceType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
}
