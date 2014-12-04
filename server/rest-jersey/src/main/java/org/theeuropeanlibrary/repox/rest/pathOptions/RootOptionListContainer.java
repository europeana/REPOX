/* RootOptionListContainer.java - created on Oct 15, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Rest options.
 * Extends the {@link org.theeuropeanlibrary.repox.rest.pathOptions.OptionListContainer}, so it can override the name of the tag for JAXB.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 15, 2014
 */
@XmlRootElement(name = "RestOptions")
public class RootOptionListContainer extends OptionListContainer {
    public static final String OPTIONS = "options";
    
    /**
     * No argument constructor needed for JAXB.
     * Generates the aggregators option list.
     */
    public RootOptionListContainer() {
    }
    
    /**
     * Initialize custom aggregator list options.
     * @param baseUri
     */
    public RootOptionListContainer(URI baseUri) {
        List<Option> optionList = new ArrayList<Option>();
        //BaseUri has a "/" at the end.
        optionList.add(new Option("[OPTIONS]Get information about all the options provided ", baseUri + RootOptionListContainer.OPTIONS, null));
        optionList.add(new Option("[OPTIONS]Get further options over Aggregators ", baseUri + AggregatorOptionListContainer.AGGREGATORS, null));
        optionList.add(new Option("[OPTIONS]Get further options over Providers ", baseUri + ProviderOptionListContainer.PROVIDERS, null));
        optionList.add(new Option("[OPTIONS]Get further options over Datasets ", baseUri + DatasetOptionListContainer.DATASETS, null));
        optionList.add(new Option("[OPTIONS]Get further options over Harvests ", baseUri + DatasetOptionListContainer.DATASETS + "/" +HarvestOptionListContainer.HARVEST, null));
        optionList.add(new Option("[OPTIONS]Get further options over Mappings ", baseUri + MappingOptionListContainer.MAPPINGS, null));
        
        setOptionList(optionList);
    }

    /**
     * Creates a new instance of this class by providing the requested option list.
     * @param optionList
     */
    public RootOptionListContainer(List<Option> optionList) {
        super(optionList);
    }

}
