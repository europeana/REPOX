package org.theeuropeanlibrary.repox.rest.pathOptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Statistics options.
 * Extends the {@link org.theeuropeanlibrary.repox.rest.pathOptions.OptionListContainer}, so it can override the name of the tag for JAXB.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Dec 8, 2014
 */
@XmlRootElement(name = "StatisticsOptions")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "An Option List container")
public class StatisticsOptionListContainer extends OptionListContainer {
	public static final String STATISTICS = "statistics";
	public static final String OPTIONS = "options";
	
	/**
     * No argument constructor needed for JAXB.
     * Creates a new instance of this class.
     */
    public StatisticsOptionListContainer() {
    }

    /**
     * Initialize custom records list options.
     * @param baseUri
     */
    public StatisticsOptionListContainer(URI baseUri) {
        List<Option> optionList = new ArrayList<Option>();
        //BaseUri has a "/" at the end.
        optionList.add(new Option("[OPTIONS]Get options over Statistics.", baseUri + STATISTICS, null));
        optionList.add(new Option("[GET]Get options over Statistics.", baseUri + STATISTICS + "/" + OPTIONS, null));
        optionList.add(new Option("[GET]Retrieve the statistics.", baseUri + STATISTICS, null));
        
        setOptionList(optionList);
    }

    /**
     * Creates a new instance of this class by providing the requested option list.
     * @param optionList
     */
    public StatisticsOptionListContainer(List<Option> optionList) {
        super(optionList);
    }
}
