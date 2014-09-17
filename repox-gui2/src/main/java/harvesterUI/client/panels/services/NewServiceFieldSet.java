package harvesterUI.client.panels.services;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import harvesterUI.client.HarvesterUI;
import harvesterUI.shared.externalServices.ExternalServiceUI;
import harvesterUI.shared.externalServices.ServiceParameterUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 10-01-2012
 * Time: 18:25
 */
public class NewServiceFieldSet extends FieldSet {

    private int fieldCount = 1;

    public NewServiceFieldSet() {
        setHeading(HarvesterUI.CONSTANTS.serviceFields());
    }

    public void addNewField(){
        NewServiceField newServiceField = new NewServiceField(this,fieldCount);
        add(newServiceField);
        layout();
        fieldCount++;
    }

    public void addNewField(ServiceParameterUI serviceParameterUI){
        add(new NewServiceField(this,serviceParameterUI,fieldCount));
        layout();
        fieldCount++;
    }

    public List<ServiceParameterUI> getAllFields(){
        List<ServiceParameterUI> serviceParameters = new ArrayList<ServiceParameterUI>();
        for(Component component : getItems()){
            if(component instanceof NewServiceField){
                NewServiceField newServiceField = (NewServiceField)component;
                String name = newServiceField.getFieldName();
                String example = newServiceField.getFieldExample();
                String type = newServiceField.getFieldType();
                String semantics = newServiceField.getFieldSemantics();
                boolean required = newServiceField.getRequired();
                ServiceParameterUI serviceParameterUI = new ServiceParameterUI(name,type,required,example,semantics);
                if(type.equals("COMBO_FIELD")){
                    for(Component comboValueField: newServiceField.getItems()){
                        if(comboValueField.getId().startsWith("comboValueField_")){
                            if(comboValueField instanceof TextField){
                                TextField<String> comboField = (TextField<String>)comboValueField;
                                serviceParameterUI.getComboValues().add(comboField.getValue());
                            }else if(comboValueField instanceof LayoutContainer){
                                TextField<String> comboField = (TextField<String>)((LayoutContainer) comboValueField).getItem(1);
                                serviceParameterUI.getComboValues().add(comboField.getValue());
                            }
                        }
                    }
                }
                serviceParameters.add(serviceParameterUI);
            }
        }
        return serviceParameters;
    }

    public void resize() {
        layout(true);
    }

    public void edit(ExternalServiceUI externalServiceUI){
        for(ServiceParameterUI serviceParameterUI : externalServiceUI.getServiceParameters()){
            addNewField(serviceParameterUI);
        }
    }

    public void reset(){
        fieldCount = 1;
        for(int i=getItems().size()-1 ; i >=0 ; i--){
            if(getItem(i) instanceof NewServiceField){
                getItem(i).removeFromParent();
            }
        }
    }

    public void subtractFieldCount() {
        fieldCount--;
    }
}
