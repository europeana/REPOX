package harvesterUI.shared.externalServices;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 27/01/12
 * Time: 13:36
 */
public class ExternalServiceResultUI extends BaseModel implements IsSerializable {

    public ExternalServiceResultUI() {}

    public ExternalServiceResultUI(String state,String htmlReportURL) {
        set("state",state);
        set("htmlReportURL",htmlReportURL);
//        set("recordsValidated",recordsValidated);
//        set("resultFilePath", resultFilePath);
    }

    public void setState(String state){set("state", state);}
    public String getState(){return (String) get("state");}
    public String getHtmlReportUrl(){return (String) get("htmlReportURL");}

//    public void setRecordsValidated(String recordsValidated){set("recordsValidated", recordsValidated);}
//    public String getRecordsValidated(){return (String) get("recordsValidated");}

//    public void setResultFilePath(String resultFilePath){set("resultFilePath", resultFilePath);}
//    public String getResultFilePath(){return (String) get("resultFilePath");}

}
