package harvesterUI.shared.search;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 19-09-2012
 * Time: 11:23
 */
public class BaseSearchResult extends BaseModel implements IsSerializable {


    public BaseSearchResult() {
    }

    public BaseSearchResult(String id, String name, String description, String dataSet, String dataType) {
        set("id", id);
        set("name", name);
        set("dataSet", dataSet);
        set("dataType", dataType);
        set("description", description);
    }
}
