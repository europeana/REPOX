/* AggregatorOptionList.java - created on Oct 13, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Aggregator options.
 * Extends the {@link org.theeuropeanlibrary.repox.rest.pathOptions.OptionListContainer}, so it can override the name of the tag for JAXB.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 13, 2014
 */
@XmlRootElement(name = "aggregatorOptions")
public class AggregatorOptionListContainer extends OptionListContainer {

    /**
     * No argument constructor needed for JAXB.
     * Generates the aggregators option list.
     */
    public AggregatorOptionListContainer() {
    }
    
    /**
     * Initialize custom aggregator list options.
     * @param baseUri
     */
    public AggregatorOptionListContainer(URI baseUri) {
        List<Option> optionList = new ArrayList<Option>();
        optionList.add(new Option("Gets an Aggregator by Id", baseUri + "aggregators/{aggregatorId}"));
        
        setOptionList(optionList);
    }

    /**
     * Creates a new instance of this class by providing the requested option list.
     * @param optionList
     */
    public AggregatorOptionListContainer(List<Option> optionList) {
        super(optionList);
    }
}