/* OptionsList.java - created on Oct 13, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * An abstract structure for containing a List of path options.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 13, 2014
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ApiModel(value = "An Option List container")
public abstract class OptionListContainer {

    @XmlElement(name="option")
    @ApiModelProperty(required=true)
    private List<Option> optionList;

    /**
     * No argument constructor needed for JAXB.
     */
    public OptionListContainer() {
    }
    
    public OptionListContainer(List<Option> option) {
        this.optionList = option;
    }

    public List<Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<Option> optionList) {
        this.optionList = optionList;
    }
}
