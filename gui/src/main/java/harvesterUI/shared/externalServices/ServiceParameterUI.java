package harvesterUI.shared.externalServices;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 07-12-2011
 * Time: 23:50
 */
public class ServiceParameterUI extends BaseModel implements IsSerializable {

    private String name;
    private String id;
    private String type;
    private boolean required;
    private String example;
    private String semantics;

    private String value;
    private List<String> comboValues;

    public ServiceParameterUI(){}

    public ServiceParameterUI(String name, String type, boolean required, String example, String semantics) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.example = example;
        this.semantics = semantics;
    }

    public void setName(String name){this.name = name;}
    public String getName(){return name;}

//    public void setType(String type){set("type", type);}
    public String getType(){return type;}

    public void setValue(String value){this.value = value;}
    public String getValue(){return value;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //    public void setRequired(boolean required){set("required", required);}
    public Boolean getRequired(){return required;}

    public String getSemantics() {
        return semantics;
    }

    //    public void setExample(String example){set("example", example);}
    public String getExample(){return example;}

    public void setComboValues(List<String> comboValues){this.comboValues = comboValues;}
    public List<String> getComboValues(){
//        List<String> comboValues = (List<String>) get("comboValues");
        if(comboValues == null){
            comboValues = new ArrayList<String>();
//            setComboValues(comboValues);
        }
        return comboValues;
    }
}
