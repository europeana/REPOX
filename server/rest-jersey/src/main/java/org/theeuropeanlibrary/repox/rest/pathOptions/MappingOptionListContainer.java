/* MappingOptionListContainer.java - created on Nov 27, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Mappings options.
 * Extends the {@link org.theeuropeanlibrary.repox.rest.pathOptions.OptionListContainer}, so it can override the name of the tag for JAXB.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 27, 2014
 */
@XmlRootElement(name = "MappingOptions")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An Option List container")
public class MappingOptionListContainer extends OptionListContainer {
    public static final String MAPPINGS = "mappings";
    public static final String MAPPINGID = "{mappingId}";
    public static final String OPTIONS = "options";

    /**
     * No argument constructor needed for JAXB.
     */
    public MappingOptionListContainer() {
    }
    
    /**
     * Initialize custom provider list options.
     * @param baseUri
     */
    public MappingOptionListContainer(URI baseUri) {
        List<Option> optionList = new ArrayList<Option>();
        //BaseUri has a "/" at the end.
        optionList.add(new Option("[OPTIONS]Get options over mappings.", baseUri + MAPPINGS, null));
        
        setOptionList(optionList);
    }

    /**
     * Creates a new instance of this class.
     * @param option
     */
    public MappingOptionListContainer(List<Option> option) {
        super(option);
    }

}
