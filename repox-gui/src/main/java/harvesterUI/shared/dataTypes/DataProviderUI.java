package harvesterUI.shared.dataTypes;

import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 11-04-2011
 * Time: 13:51
 */
public class DataProviderUI extends DataContainer {

    private List<DataSourceUI> dataSourceUIList;

    // Eudml Only
    private String email;
    private String ipAddress;

    public DataProviderUI() {}

    public DataProviderUI(String id, String name, String country, String countryName) {
        super(id);
        set("name", name);
        set("country", country);
        set("countryName", countryName);

        dataSourceUIList = new ArrayList<DataSourceUI>();
    }

    public void addDataSource(DataSourceUI dp) {
        dataSourceUIList.add(dp);
    }
    public List<DataSourceUI> getDataSourceUIList() {return dataSourceUIList;}

//    public void setGridPropertiesForSingleDS(String dsName, String dataSourceSet, String metadataFormat,
//                                             String ingest, String records, Date usedDate, String type) {
//        set("dsName", dsName);
//        set("dataSourceSet", dataSourceSet);
//        set("metadataFormat", metadataFormat);
//        set("ingest", ingest);
//        set("records", records);
//        set("usedDate", usedDate);
//        //TODO: Demo only - to remove (type)
//        setType(type);
//        setFiltered(false);
//    }
//
//    public void resetGridPropertiesForSingleDS() {
//        set("dsName", null);
//        set("dataSourceSet", null);
//        set("metadataFormat", null);
//        set("ingest", null);
//        set("status", "");
//        set("records", null);
////        set("nameCode", null);
//        set("lastIngest", null);
//        set("usedDate", null);
//        setFiltered(false);
//    }

//    public void setFiltered(boolean filtered){set("filtered", filtered);}
//    public boolean getFiltered() {return (Boolean) get("filtered");}

    public String getId() {
        return (String) get("id");
    }

    public void setId(String id) {
        set("id",id);
    }

    public String getProject() {
        return (String) get("project");
    }

    public String getName() {
        return (String) get("name");
    }

    public void setType(String type) {
        set("type",type);
    }

    public void setName(String name) {
        set("name",name);
    }

    public void setCountry(String country) {
        set("country",country);
    }

    public void setCountryName(String countryName) {
        set("countryName",countryName);
    }

    public void setNameCode(String nameCode) {
        set("nameCode",nameCode);
    }

    public void setHomepage(String homepage) {
        set("homepage",homepage);
    }

    public void setIdDb(String idDb) {
        set("idDb",idDb);
    }

    public String getType() {
        return (String) get("type");
    }

    public String getHomepage() {
        return (String) get("homepage");
    }

    public String getNameCode() {
        return (String) get("nameCode");
    }

    public String getCountry() {
        return (String) get("country");
    }

    // Data Provider only
    public String getDescription() {
        return (String) get("description");
    }

    public void setDescription(String description) {
        set("description",description);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // Europeana Only
    public void setParentAggregatorID(String parentID) {
        set("parentID",parentID);
    }

    public String getParentAggregatorID() {
        return (String) get("parentID");
    }
}
