/* ProviderOptionListContainer.java - created on Oct 24, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Provider options.
 * Extends the {@link org.theeuropeanlibrary.repox.rest.pathOptions.OptionListContainer}, so it can override the name of the tag for JAXB.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 24, 2014
 */
public class ProviderOptionListContainer extends OptionListContainer {
    public static final String PROVIDERS = "providers";
    public static final String PROVIDERID = "{providerId}";
    
    /**
     * No argument constructor needed for JAXB.
     * Creates a new instance of this class.
     */
    public ProviderOptionListContainer() {
    }

    /**
     * Initialize custom provider list options.
     * @param baseUri
     */
    public ProviderOptionListContainer(URI baseUri) {
        List<Option> optionList = new ArrayList<Option>();
        //BaseUri has a "/" at the end.
        optionList.add(new Option("Get options over Provider", baseUri + PROVIDERS));
        optionList.add(new Option("Gets an provider by Id", baseUri + PROVIDERS + "/" + PROVIDERID));
        
        setOptionList(optionList);
    }
    
    /**
     * Creates a new instance of this class by providing the requested option list.
     * @param option
     */
    public ProviderOptionListContainer(List<Option> option) {
        super(option);
    }

}
