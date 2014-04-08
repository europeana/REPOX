package harvesterUI.shared.dataTypes.admin;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 19-09-2012
 * Time: 12:12
 */
public class AdminInfo extends BaseModel implements IsSerializable {

    private String reloadOAIPropertiesUrl;

    public AdminInfo() {
    }

    public String getReloadOAIPropertiesUrl() {
        return reloadOAIPropertiesUrl;
    }

    public void setReloadOAIPropertiesUrl(String reloadOAIPropertiesUrl) {
        this.reloadOAIPropertiesUrl = reloadOAIPropertiesUrl;
    }
}
