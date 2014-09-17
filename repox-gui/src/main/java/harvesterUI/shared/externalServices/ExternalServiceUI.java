package harvesterUI.shared.externalServices;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 07-12-2011
 * Time: 23:50
 */
public class ExternalServiceUI extends BaseModel implements IsSerializable{
    
    private List<ServiceParameterUI> serviceParameters;
    private boolean online;

    private boolean isEnabled;
    private String externalResultUI;

    public ExternalServiceUI() {}

    public ExternalServiceUI(String id,String name,String uri, String statusUri,String type,
                             List<ServiceParameterUI> serviceParameters, String externalServiceType) {
        set("id",id);
        set("name",name);
        set("uri",uri);
        set("statusUri",statusUri);
        set("type",type);
        set("externalServiceType",externalServiceType);
        setServiceParameters(serviceParameters);
        online = false;
        isEnabled = true;
    }

    public List<ServiceParameterUI> getServiceParameters() {
        return serviceParameters;
    }

    public void setServiceParameters(List<ServiceParameterUI> serviceParameters) {
        this.serviceParameters = serviceParameters;
    }

    public void setId(String id){set("id", id);}
    public String getId(){return (String) get("id");}

    public void setName(String name){set("name", name);}
    public String getName(){return (String) get("name");}

    public void setUri(String uri){set("uri", uri);}
    public String getUri(){return (String) get("uri");}

    public void setStatusUri(String statusUri){set("statusUri", statusUri);}
    public String getStatusUri(){return (String) get("statusUri");}

    public void setType(String type){set("type", type);}
    public String getType(){return (String) get("type");}

    public void setExternalServiceType(String externalServiceType){set("externalServiceType", externalServiceType);}
    public String getExternalServiceType(){return (String) get("externalServiceType");}

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getExternalResultUI() {
        return externalResultUI;
    }

    public void setExternalResultUI(String externalResultUI) {
        this.externalResultUI = externalResultUI;
    }
}
