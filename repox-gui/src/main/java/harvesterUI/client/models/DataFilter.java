package harvesterUI.client.models;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
* Created by IntelliJ IDEA.
* User: Edmundo
* Date: 11-02-2011
* Time: 13:36
* To change this template use File | Settings | File Templates.
*/
public class DataFilter extends BaseModel {

    public DataFilter(String name, String value, FilterButton button) {
        set("name", name);
        set("value", value);
        set("button", button);
        button.setDataFilter(this);
        setChecked(0);
    }
    
    public String getValue() {
        return (String) get("value");
    }

    public String getIcon() {
        return (String) get("icon");
    }

    public String getName() {
        return (String) get("name");
    }

    public String getRangeInfo(){
        return (String) get("rangeInfo");
    }

    public String setRangeInfo(String rangeInfo){
        return set("rangeInfo",rangeInfo);
    }

    public FilterButton getFilterButton() {
        return (FilterButton) get("button");
    }

    public void setChecked(int checked) { set("checked",checked);}
    public Integer getChecked() { return (Integer) get("checked");}
}
