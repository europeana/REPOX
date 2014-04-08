package pt.utl.ist.repox.dataProvider;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import pt.utl.ist.repox.util.ConfigSingleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class DataProvider {
    private String id;
    private String name;
    private String country;
    private String description;
    private HashMap<String, DataSourceContainer> dataSourceContainers;

    // optional
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String generateId(String name) {
        String generatedIdPrefix = "";

        for (int i = 0; (i < name.length() && i < 32); i++) {
            if((name.charAt(i) >= 'a' && name.charAt(i) <= 'z')
                    || (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z')) {
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

        while(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProvider(currentFullId) != null) {
            currentNumber++;
            currentFullId = basename + currentNumber;
        }

        return currentNumber;
    }

    /**
     * Retrieves this DataProvider's DataSource with identifier dataSourceId if this DataProvider contains
     * the DataSource with dataSourceId or null otherwise
     *
     * @param dataSourceId
     * @return DataSource with id dataSourceId or null otherwise
     */
    public DataSource getDataSource(String dataSourceId) {
        if(dataSourceContainers.get(dataSourceId) != null){
            return dataSourceContainers.get(dataSourceId).getDataSource();
        }
        return null;
    }

    // todo to be removed after GWT miggration
    public Collection<DataSource> getReversedDataSourceContainers() {
        List<DataSource> reversedDataSources = new ArrayList<DataSource>();
        for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
            reversedDataSources.add(0, dataSourceContainer.getDataSource());
        }
        return reversedDataSources;
    }

    public HashMap<String, DataSourceContainer> getDataSourceContainers() {
        return dataSourceContainers;
    }

    public void setDataSourceContainers(HashMap<String, DataSourceContainer> dataSourceContainers) {
        this.dataSourceContainers = dataSourceContainers;
    }

    public DataProvider() {
        super();
        dataSourceContainers = new HashMap<String, DataSourceContainer>();
    }

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
        if(this.getEmail() != null && !this.getEmail().isEmpty()) {
            dataProviderElement.addElement("email").setText(this.getEmail());
        }
        if(writeDataSources && dataSourceContainers != null){
            for (DataSourceContainer dataSourceContainer : dataSourceContainers.values()) {
                dataProviderElement.add(dataSourceContainer.createElement());
            }
        }

        return dataProviderElement;
    }
}

