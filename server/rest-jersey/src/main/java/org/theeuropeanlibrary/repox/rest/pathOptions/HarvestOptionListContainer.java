/* HarvestOptionListContainer.java - created on Nov 17, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Harvest options.
 * Extends the {@link org.theeuropeanlibrary.repox.rest.pathOptions.OptionListContainer}, so it can override the name of the tag for JAXB.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 17, 2014
 */
@XmlRootElement(name = "HarvestOptions")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An Option List container")
public class HarvestOptionListContainer extends OptionListContainer {
    public static final String HARVEST = "harvest";
    public static final String HARVESTS = "harvests";
    public static final String START = "start";
    public static final String CANCEL = "cancel";
    public static final String SCHEDULE = "schedule";
    public static final String SCHEDULES = "schedules";
    
    public static final String FULL = "full";
    public static final String SAMPLE = "sample";

    /**
     * No argument constructor needed for JAXB.
     */
    public HarvestOptionListContainer() {
    }
    
    
    /**
     * Initialize custom provider list options.
     * @param baseUri
     */
    public HarvestOptionListContainer(URI baseUri) {
        List<Option> optionList = new ArrayList<Option>();
        //BaseUri has a "/" at the end.
        optionList.add(new Option("[OPTIONS]Get options over dataset.", baseUri + DatasetOptionListContainer.DATASETS + "/" + HARVEST, null));
        
        setOptionList(optionList);
    }

    /**
     * Creates a new instance of this class by providing the requested option list.
     * @param option
     */
    public HarvestOptionListContainer(List<Option> option) {
        super(option);
    }

}
