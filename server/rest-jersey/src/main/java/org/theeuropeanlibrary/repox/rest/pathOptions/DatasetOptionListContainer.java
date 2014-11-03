/* DatasetOptionListContainer.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Dataset options.
 * Extends the {@link org.theeuropeanlibrary.repox.rest.pathOptions.OptionListContainer}, so it can override the name of the tag for JAXB. 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 30, 2014
 */
@XmlRootElement(name = "DatasetOptions")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An Option List container")
public class DatasetOptionListContainer extends OptionListContainer {
    public static final String DATASETS = "datasets";
    public static final String DATASETID = "{datasetId}";

    /**
     * No argument constructor needed for JAXB.
     */
    public DatasetOptionListContainer() {
    }
    
    /**
     * Initialize custom provider list options.
     * @param baseUri
     */
    public DatasetOptionListContainer(URI baseUri) {
        List<Option> optionList = new ArrayList<Option>();
        //BaseUri has a "/" at the end.
        optionList.add(new Option("[OPTIONS]Get options over dataset.", baseUri + DATASETS, null));
//        optionList.add(new Option("[GET]Gets a provider by Id.", baseUri + PROVIDERS + "/" + PROVIDERID, null));
//        optionList.add(new Option("[POST]Create a provider provided in the body of the post call.", baseUri + PROVIDERS, new ArrayList<String>(Arrays.asList(AGGREGATORID))));
//        optionList.add(new Option("[DELETE]Delete a provider by specifying the Id.", baseUri + PROVIDERS + "/" + PROVIDERID, null));
//        optionList.add(new Option("[PUT]Update aprovider by specifying the Id on the context path.", baseUri + PROVIDERS + "/" + PROVIDERID, new ArrayList<String>(Arrays.asList(NEWAGGREGATORID))));
//        optionList.add(new Option("[GET]Get a list of providers by specifying a range.", baseUri + PROVIDERS, new ArrayList<String>(Arrays.asList(AGGREGATORID, OFFSET, NUMBER))));
        
        setOptionList(optionList);
    }

    /**
     * Creates a new instance of this class by providing the requested option list.
     * @param option
     */
    public DatasetOptionListContainer(List<Option> option) {
        super(option);
    }

}
