package harvesterUI.client.models;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;

/**
* Created by IntelliJ IDEA.
* User: Edmundo
* Date: 11-02-2011
* Time: 13:36
* To change this template use File | Settings | File Templates.
*/
public class Attribute extends BaseModel {

    public Attribute(String name, String value) {
        set("name", name);
        set("value", value);
        set("icon", "DSStyle");
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

    public String getRangeInfo()
    {
        return (String) get("rangeInfo");
    }

    public Button getMenu() {
        return (Button) get("menu");
    }

    public void setChecked(int checked) { set("checked",checked);}
    public Integer getChecked() { return (Integer) get("checked");}
}
