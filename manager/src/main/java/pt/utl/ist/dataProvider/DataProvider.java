package pt.utl.ist.dataProvider;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.util.ProviderType;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * DataProvider type
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 */
@XmlRootElement(name = "dataprovider")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "A Provider")
public class DataProvider {
    @XmlElement
    @ApiModelProperty(position = 1)
    private String                               id;
    @XmlElement
    @ApiModelProperty(position = 2, required = true)
    private String                               name;
    @XmlElement
    @ApiModelProperty(position = 3)
    private String                               country;
    @XmlElement
    @ApiModelProperty(position = 4)
    private String                               countryCode;
    @XmlElement
    @ApiModelProperty(position = 5)
    private String                               description;
    @XmlElement
    @ApiModelProperty(position = 6)
    private String                               nameCode;
    @XmlElement
    @ApiModelProperty(position = 7)
    private String                               homepage;
    @XmlElement
    @ApiModelProperty(position = 8, required = true)
    private ProviderType                         providerType;
    @XmlElement
    @ApiModelProperty(position = 9)
    private String                               email;

    @XmlTransient
    private HashMap<String, DataSourceContainer> dataSourceContainers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String country) {
        this.countryCode = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType dataSetType) {
        this.providerType = dataSetType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String generateId(String name) {
        String generatedIdPrefix = "";

        for (int i = 0; (i < name.length() && i < 32); i++) {
            if ((name.charAt(i) >= 'a' && name.charAt(i) <= 'z') || (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') || (name.charAt(i) >= '0' && name.charAt(i) <= '9')) {
                generatedIdPrefix += name.charAt(i);
            }
        }
        generatedIdPrefix += "r";

        String fullId = generatedIdPrefix + generateNumberSufix(generatedIdPrefix);

        return fullId;
    }

    private static int generateNumberSufix(String basename) {
        int currentNumber = 0;
        String currentFullId = basename + currentNumber;

        while (ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProvider(currentFullId) != null) {
            currentNumber++;
            currentFullId = basename + currentNumber;
        }

        return currentNumber;
    }

    /**
     * Retrieves this DataProvider's DataSource with identifier dataSourceId if
     * this DataProvider contains the DataSource with dataSourceId or null
     * otherwise
     * 
     * @param dataSourceId
     * @return DataSource with id dataSourceId or null otherwise
     */
    public DataSource getDataSource(String dataSourceId) {
        if (dataSourceContainers.get(dataSourceId) != null) {
            return dataSourceContainers.get(dataSourceId).getDataSource();
        }
        return null;
    }

    public HashMap<String, DataSourceContainer> getDataSourceContainers() {
        return dataSourceContainers;
    }

    public void setDataSourceContainers(HashMap<String, DataSourceContainer> dataSourceContainers) {
        this.dataSourceContainers = dataSourceContainers;
    }

    /**
     * Creates a new instance of this class.
     */
    public DataProvider() {
        dataSourceContainers = new HashMap<String, DataSourceContainer>();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     * @param name
     * @param countryCode
     * @param description
     * @param dataSourceContainers
     * @param nameCode 
     * @param homepage 
     * @param providerType 
     * @param email 
     */
    public DataProvider(String id, String name, String countryCode, String description, HashMap<String, DataSourceContainer> dataSourceContainers, String nameCode, String homepage,
                        ProviderType providerType, String email) {
        
        //Setting to "" is required for the response of a rest service.
        if (id != null)
            this.id = id;
        else
            this.id = "";
        if (name != null)
            this.name = name;
        else
            this.name = "";
        if (countryCode != null)
            this.countryCode = countryCode;
        else
            this.countryCode = "";
        if (description != null)
            this.description = description;
        else
            this.description = "";
        if (nameCode != null)
            this.nameCode = nameCode;
        else
            this.nameCode = "";
        if (homepage != null)
            this.homepage = homepage;
        else
            this.homepage = "";
        if (providerType != null)
            this.providerType = providerType;
        else
            this.providerType = ProviderType.UNKNOWN;
        if (email != null)
            this.email = email;
        else
            this.email = "";

        this.dataSourceContainers = dataSourceContainers;
    }

    /**
     * Create Element from data provider information.
     * 
     * @param writeDataSources 
     * @return Document
     */
    public Element createElement(boolean writeDataSources) {
        Element dataProviderElement = DocumentHelper.createElement("provider");

        dataProviderElement.addAttribute("id", this.getId());
        dataProviderElement.addElement("name").setText(this.getName());
        if (this.getCountryCode() != null) {
            dataProviderElement.addElement("country").setText(this.getCountryCode());
        }
        if (this.getDescription() != null) {
            dataProviderElement.addElement("description").setText(this.getDescription());
        }
        if (this.getProviderType() != null) {
            dataProviderElement.addElement("type").setText(this.getProviderType().toString());
        }
        if (this.getNameCode() != null) {
            dataProviderElement.addElement("nameCode").setText(this.getNameCode());
        }
        if (this.getHomepage() != null) {
            dataProviderElement.addElement("url").setText(this.getHomepage().toString());
        }
        if (this.getEmail() != null && !this.getEmail().isEmpty()) {
            dataProviderElement.addElement("email").setText(this.getEmail());
        }

        if (writeDataSources && this.getDataSourceContainers() != null) {
            for (DataSourceContainer dataSourceContainer : this.getDataSourceContainers().values()) {
                DefaultDataSourceContainer dDataSourceContainer = (DefaultDataSourceContainer)dataSourceContainer;
                dataProviderElement.add(dDataSourceContainer.createElement());
            }
        }

        return dataProviderElement;
    }
}
