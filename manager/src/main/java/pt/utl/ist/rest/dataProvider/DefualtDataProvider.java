package pt.utl.ist.rest.dataProvider;

import eu.europeana.repox2sip.models.ProviderType;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSourceContainer;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 15-06-2011
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class DefualtDataProvider extends DataProvider{
    private String nameCode;
    private URL homePage;
    private ProviderType dataSetType;

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public URL getHomePage() {
        return homePage;
    }

    public void setHomePage(URL homePage) {
        this.homePage = homePage;
    }

    public ProviderType getDataSetType() {
        return dataSetType;
    }

    public void setDataSetType(ProviderType dataSetType) {
        this.dataSetType = dataSetType;
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
        if(this.getDataSetType() != null) {
            dataProviderElement.addElement("type").setText(this.getDataSetType().toString());
        }
        if(this.getNameCode() != null) {
            dataProviderElement.addElement("nameCode").setText(this.getNameCode());
        }
        if(this.getHomePage() != null) {
            dataProviderElement.addElement("url").setText(this.getHomePage().toString());
        }

        if(writeDataSources &&  this.getDataSourceContainers() != null){
            for (DataSourceContainer dataSourceContainer : this.getDataSourceContainers().values()) {
                DefaultDataSourceContainer dDataSourceContainerEuropeana = (DefaultDataSourceContainer) dataSourceContainer;
                dataProviderElement.add(dDataSourceContainerEuropeana.createElement());
            }
        }

        return dataProviderElement;
    }




    /**
     * Create new DataProviderEuropeana
     * @param id
     * @param name
     * @param country
     * @param description
     * @param dataSourceContainers
     * @param nameCode
     * @param homePage
     * @param dataSetType
     */
    public DefualtDataProvider(String id, String name, String country, String description, HashMap<String, DataSourceContainer> dataSourceContainers, String nameCode, URL homePage, ProviderType dataSetType) {
        super(id, name, country, description, dataSourceContainers);
        this.nameCode = nameCode;
        this.homePage = homePage;
        this.dataSetType = dataSetType;
    }

    public DefualtDataProvider() {
    }
}
