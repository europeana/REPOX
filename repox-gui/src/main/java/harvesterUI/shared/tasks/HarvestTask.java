package harvesterUI.shared.tasks;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 29-03-2011
 * Time: 12:42
 */
public class HarvestTask extends BaseModel implements IsSerializable {

    public HarvestTask() {
    }

    public void setId(String id) {set("id",id);}
    public String getDataSetId() {return (String) get("dataSetId");}
    public String getId() {return (String) get("id");}
    public String getFullIngest() {return (String) get("fullIngest");}
    public String getType() {return (String) get("type");}
    public Date getDate() {return (Date) get("date");}
}
