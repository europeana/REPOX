/* Result.java - created on Oct 22, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 22, 2014
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "Result")
public class Result {
    @XmlElement(name="result")
    @ApiModelProperty(required=false)
    String result;
    
    /**
     * Creates a new instance of this class.
     */
    public Result() {
    }
    
    /**
     * Creates a new instance of this class.
     * @param result
     */
    public Result(String result) {
        super();
        this.result = result;
    }

    /**
     * Returns the result.
     * @return the result
     */
    public String getResult() {
        return result;
    }
    /**
     * Sets the result to the given value.
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }
}
