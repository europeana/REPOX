package pt.utl.ist.dataProvider;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import pt.utl.ist.util.ProviderType;

import java.net.URL;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 15-06-2011
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "dataprovider")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultDataProvider extends DataProvider{
    @XmlElement
    private String nameCode;
    @XmlElement
    private URL homePage;
    @XmlElement
    private ProviderType providerType;

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

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType dataSetType) {
        this.providerType = dataSetType;
    }

    /**
     * Create Element from data provider information
     * @return Document
     */
    @Override
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

        if(writeDataSources &&  this.getDataSourceContainers() != null){
            for (DataSourceContainer dataSourceContainer : this.getDataSourceContainers().values()) {
                DefaultDataSourceContainer dDataSourceContainerEuropeana = (DefaultDataSourceContainer) dataSourceContainer;
                dataProviderElement.add(dDataSourceContainerEuropeana.createElement());
            }
        }

        return dataProviderElement;
    }

    /**
     * Create new DefaultDataProvider
     * @param id
     * @param name
     * @param country
     * @param description
     * @param dataSourceContainers
     * @param nameCode
     * @param homePage
     * @param dataSetType
     */
    public DefaultDataProvider(String id, String name, String country, String description, HashMap<String, DataSourceContainer> dataSourceContainers, String nameCode, URL homePage, ProviderType dataSetType) {
        super(id, name, country, description, dataSourceContainers);
        this.nameCode = nameCode;
        this.homePage = homePage;
        this.providerType = dataSetType;
    }

    public DefaultDataProvider() {
    }
}
