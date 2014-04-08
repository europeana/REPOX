package harvesterUI.shared.dataTypes.dataSet;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 28-05-2012
 * Time: 16:49
 */
public class SimpleDataSetInfo extends BaseModel implements IsSerializable{

    public SimpleDataSetInfo() {
    }

    public SimpleDataSetInfo(String id, String name){
        set("id",id);
        set("name",name);
    }

    public String getId(){return get("id");}
    public String getName(){return get("name");}
}
