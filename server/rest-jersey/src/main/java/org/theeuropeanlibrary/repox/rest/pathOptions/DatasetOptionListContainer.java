/* DatasetOptionListContainer.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final String DATE = "date";
    public static final String COUNT = "count";
    //Query parameters
    public static final String OFFSET = "offset";
    public static final String NUMBER = "number";
    public static final String PROVIDERID = "providerId";


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
        optionList.add(new Option("[GET]Gets a dataset by Id.", baseUri + DATASETS + "/" + DATASETID, null));
        optionList.add(new Option("[POST]Create a dataset provided in the body of the post call.", baseUri + DATASETS, new ArrayList<String>(Arrays.asList(PROVIDERID))));
        optionList.add(new Option("[DELETE]Delete a dataset by specifying the Id.", baseUri + DATASETS + "/" + DATASETID, null));
        optionList.add(new Option("[PUT]Update a dataset by specifying the Id on the context path.", baseUri + DATASETS + "/" + DATASETID, null));
        optionList.add(new Option("[GET]Get a list of datasets by specifying a range.", baseUri + DATASETS, new ArrayList<String>(Arrays.asList(PROVIDERID, OFFSET, NUMBER))));
        
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
