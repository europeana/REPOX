/* RecordsOptionListContainer.java - created on Dec 5, 2014, Copyright (c) 2011 The European Library, all rights reserved */
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
 * Record options.
 * Extends the {@link org.theeuropeanlibrary.repox.rest.pathOptions.OptionListContainer}, so it can override the name of the tag for JAXB.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Dec 5, 2014
 */
@XmlRootElement(name = "RecordsOptions")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An Option List container")
public class RecordOptionListContainer extends OptionListContainer {
    public static final String RECORDS = "records";
    public static final String RECORDIDLITERAL = "recordId";
    public static final String OPTIONS = "options";
    
    //Query parameters
    public static final String TYPE = "type";
    public static final String DELETE = "delete";
    public static final String ERASE = "erase";

    /**
     * No argument constructor needed for JAXB.
     * Creates a new instance of this class.
     */
    public RecordOptionListContainer() {
    }
    
    /**
     * Initialize custom records list options.
     * @param baseUri
     */
    public RecordOptionListContainer(URI baseUri) {
        List<Option> optionList = new ArrayList<Option>();
        //BaseUri has a "/" at the end.
        optionList.add(new Option("[OPTIONS]Get options over Records.", baseUri + RECORDS, null));
        optionList.add(new Option("[GET]Get options over Records.", baseUri + RECORDS + "/" + RecordOptionListContainer.OPTIONS, null));
        optionList.add(new Option("[GET]Retrieve the record with the provided id.", baseUri + RECORDS, new ArrayList<String>(Arrays.asList(RECORDIDLITERAL))));
        optionList.add(new Option("[DELETE]Deletes (mark) or permanently erase a record.", baseUri + RECORDS, new ArrayList<String>(Arrays.asList(RECORDIDLITERAL, TYPE))));
        optionList.add(new Option("[POST]Create a new record.", baseUri + RECORDS, new ArrayList<String>(Arrays.asList(DatasetOptionListContainer.DATASETIDLITERAL, RECORDIDLITERAL))));
        
        setOptionList(optionList);
    }

    /**
     * Creates a new instance of this class by providing the requested option list.
     * @param optionList
     */
    public RecordOptionListContainer(List<Option> optionList) {
        super(optionList);
    }
}
