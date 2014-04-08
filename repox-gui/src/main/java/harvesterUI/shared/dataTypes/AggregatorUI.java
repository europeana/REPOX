package harvesterUI.shared.dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 11-04-2011
 * Time: 13:54
 */
public class AggregatorUI extends DataContainer {

    private List<DataProviderUI> dataProviderUIList;

    public AggregatorUI() {}

    public AggregatorUI(String id, String name, String nameCode, String homepage) {
        super(id);
        set("name", name);
        set("nameCode", nameCode);
        set("homepage", homepage);
        dataProviderUIList = new ArrayList<DataProviderUI>();
    }

    public void addDataProvider(DataProviderUI dp) {
        dataProviderUIList.add(dp);
    }
    public List<DataProviderUI> getDataProviderUIList() {return dataProviderUIList;}

    public void setNameCode(String nameCode) {
        set("nameCode",nameCode);
    }

    public void setHomepage(String homepage) {
        set("homepage",homepage);
    }

    public void setId(String id) {
        set("id",id);
    }

//    public void setIdDB(String IdDB) {
//        set("IdDB",IdDB);
//    }

    public void setName(String name) {
        set("name",name);
    }

    public String getId() {
        return (String) get("id");
    }

    public String getIdDB() {
        return (String) get("idDB");
    }

    public String getHomepage() {
        return (String) get("homepage");
    }

    public String getName() {
        return (String) get("name");
    }

    public String getNameCode() {
        return (String) get("nameCode");
    }
}
