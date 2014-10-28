/* Option.java - created on Oct 13, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Describes the specific option.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 13, 2014
 */
@XmlType(propOrder = { "description", "syntax", "queryParameters" })
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An Option")
public class Option {
    @XmlElement(name = "description")
    @ApiModelProperty(position = 0)
    private String       description;
    @XmlElement(name = "syntax")
    @ApiModelProperty(position = 1)
    private String       syntax;
    
    @XmlElementWrapper(name="queryParameters")
    @XmlElement(name = "queryParameter")
    @ApiModelProperty(position = 2)
    private List<String> queryParameters;

    /**
     * No argument constructor needed for JAXB.
     */
    public Option() {
    }

    /**
     * Creates a new instance of this class.
     * @param description
     * @param syntax
     * @param queryParameters 
     */
    public Option(String description, String syntax, List<String> queryParameters) {
        super();
        this.description = description;
        this.syntax = syntax;
        this.queryParameters = queryParameters;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    /**
     * Returns the queryParameters.
     * @return the queryParameters
     */
    public List<String> getQueryParameters() {
        return queryParameters;
    }

    /**
     * Sets the queryParameters to the given value.
     * @param queryParameters the queryParameters to set
     */
    public void setQueryParameters(List<String> queryParameters) {
        this.queryParameters = queryParameters;
    }
}