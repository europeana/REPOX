package pt.utl.ist.dataProvider;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import pt.utl.ist.configuration.ConfigSingleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 */
public class DataProvider {
    private String                               id;
    private String                               name;
    private String                               country;
    private String                               description;
    private HashMap<String, DataSourceContainer> dataSourceContainers;

    // optional
    private String                               email;

    @SuppressWarnings("javadoc")
    public String getId() {
        return id;
    }

    @SuppressWarnings("javadoc")
    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("javadoc")
    public String getName() {
        return name;
    }

    @SuppressWarnings("javadoc")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("javadoc")
    public String getCountry() {
        return country;
    }

    @SuppressWarnings("javadoc")
    public void setCountry(String country) {
        this.country = country;
    }

    @SuppressWarnings("javadoc")
    public String getDescription() {
        return description;
    }

    @SuppressWarnings("javadoc")
    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings("javadoc")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("javadoc")
    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("javadoc")
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

    /**
     * @return Collection of DataSource
     */
    // todo to be removed after GWT migration
    public Collection<DataSource> getReversedDataSourceContainers() {
        List<DataSource> reversedDataSources = new ArrayList<DataSource>();
        for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
            reversedDataSources.add(0, dataSourceContainer.getDataSource());
        }
        return reversedDataSources;
    }

    @SuppressWarnings("javadoc")
    public HashMap<String, DataSourceContainer> getDataSourceContainers() {
        return dataSourceContainers;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourceContainers(HashMap<String, DataSourceContainer> dataSourceContainers) {
        this.dataSourceContainers = dataSourceContainers;
    }

    /**
     * Creates a new instance of this class.
     */
    public DataProvider() {
        super();
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
     */
    public DataProvider(String id, String name, String country, String description, HashMap<String, DataSourceContainer> dataSourceContainers) {
        this();
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
        this.dataSourceContainers = dataSourceContainers;
    }

    /**
     * Create Element from data provider information
     * 
     * @param writeDataSources
     * @return Document
     */
    public Element createElement(boolean writeDataSources) {
        Element dataProviderElement = DocumentHelper.createElement("provider");

        dataProviderElement.addAttribute("id", this.getId());
        dataProviderElement.addElement("name").setText(this.getName());
        if (this.getCountry() != null) {
            dataProviderElement.addElement("country").setText(this.getCountry());
        }
        if (this.getDescription() != null) {
            dataProviderElement.addElement("description").setText(this.getDescription());
        }
        if (this.getEmail() != null && !this.getEmail().isEmpty()) {
            dataProviderElement.addElement("email").setText(this.getEmail());
        }
        if (writeDataSources && dataSourceContainers != null) {
            for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
                dataProviderElement.add(dataSourceContainer.createElement());
            }
        }

        return dataProviderElement;
    }
}
