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

/**
 * DataProvider type
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 */
@XmlRootElement(name = "dataprovider")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "A Provider")
public class DataProvider {
    @XmlElement
    private String                               id;
    @XmlElement
    private String                               name;
    @XmlElement
    private String                               country;
    @XmlElement
    private String                               description;
    @XmlTransient
    private HashMap<String, DataSourceContainer> dataSourceContainers;
    
    @XmlElement
    private String nameCode;
    @XmlElement
    private String homepage;
    @XmlElement
    private ProviderType providerType;

    // optional
    @XmlElement
    private String                               email;

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

    public String getHomePage() {
        return homepage;
    }

    public void setHomePage(String homePage) {
        this.homepage = homePage;
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
            if ((name.charAt(i) >= 'a' && name.charAt(i) <= 'z') || (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z')) {
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
        if (dataSourceContainers.get(dataSourceId) != null) { return dataSourceContainers.get(dataSourceId).getDataSource(); }
        return null;
    }

//    /**
//     * @return Collection of DataSource
//     */
//    // todo to be removed after GWT migration
//    public Collection<DataSource> getReversedDataSourceContainers() {
//        List<DataSource> reversedDataSources = new ArrayList<DataSource>();
//        for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
//            reversedDataSources.add(0, dataSourceContainer.getDataSource());
//        }
//        return reversedDataSources;
//    }

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
     * @param country
     * @param description
     * @param dataSourceContainers
     * @param nameCode 
     * @param homepage 
     * @param dataSetType 
     */
    public DataProvider(String id, String name, String country, String description, HashMap<String, DataSourceContainer> dataSourceContainers, String nameCode, String homepage, ProviderType dataSetType) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
        this.dataSourceContainers = dataSourceContainers;
        this.nameCode = nameCode;
        this.homepage = homepage;
        this.providerType = dataSetType;
    }

    /**
     * Create Element from data provider information.
     * 
     * @param writeDataSources 
     * @return Document
     */
    public Element createElement(boolean writeDataSources){
        Element dataProviderElement = DocumentHelper.createElement("provider");

        dataProviderElement.addAttribute("id", this.getId());
        dataProviderElement.addElement("name").setText(this.getName());
        if(this.getCountry() != null) {
            dataProviderElement.addElement("country").setText(this.getCountry());
        }
        if(this.getDescription() != null) {
            dataProviderElement.addElement("description").setText(this.getDescription());
        }
        if(this.getProviderType() != null) {
            dataProviderElement.addElement("type").setText(this.getProviderType().toString());
        }
        if(this.getNameCode() != null) {
            dataProviderElement.addElement("nameCode").setText(this.getNameCode());
        }
        if(this.getHomePage() != null) {
            dataProviderElement.addElement("url").setText(this.getHomePage().toString());
        }
        if (this.getEmail() != null && !this.getEmail().isEmpty()) {
            dataProviderElement.addElement("email").setText(this.getEmail());
        }

        if(writeDataSources &&  this.getDataSourceContainers() != null){
            for (DataSourceContainer dataSourceContainer : this.getDataSourceContainers().values()) {
                DefaultDataSourceContainer dDataSourceContainer = (DefaultDataSourceContainer) dataSourceContainer;
                dataProviderElement.add(dDataSourceContainer.createElement());
            }
        }

        return dataProviderElement;
    }
}
