package harvesterUI.client.panels.services;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.externalServices.ServiceParameterUI;

import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 10-01-2012
 * Time: 18:57
 */
public class NewServiceField extends FieldSet{

    private SimpleComboBox<String> typeCombo, semanticsCombo;
    private CheckBoxGroup required;
    private TextField<String> exampleField, nameField;
    private FormData formData;
    private int comboCount;

    public NewServiceField(final NewServiceFieldSet parent, int fieldNumber) {
        formData = new FormData("95%");

        setHeading(HarvesterUI.CONSTANTS.field()+ " " + fieldNumber);
        setLayout(new EditableFormLayout(120));

        LayoutContainer addContainer = new LayoutContainer();
        HBoxLayout operationsLayout = new HBoxLayout();
        operationsLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        operationsLayout.setPack(BoxLayout.BoxLayoutPack.END);
        addContainer.setLayout(operationsLayout);

        if(parent.getItemCount() > 0){
            Button removeButton = new Button(HarvesterUI.CONSTANTS.delete(), new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent be) {
                    removeFromParent();
                    parent.subtractFieldCount();
                }
            });
            removeButton.setIcon(HarvesterUI.ICONS.delete());
            addContainer.add(removeButton, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        }

        add(addContainer);

        nameField = new TextField<String>();
        nameField.setFieldLabel(HarvesterUI.CONSTANTS.parameterName() + HarvesterUI.REQUIRED_STR);
        nameField.setId("parameterName");
        nameField.setAllowBlank(false);

        exampleField = new TextField<String>();
        exampleField.setFieldLabel(HarvesterUI.CONSTANTS.restExample());
        exampleField.setId("parameterExample");

        semanticsCombo = new SimpleComboBox<String>();
        semanticsCombo.setEditable(false);
        semanticsCombo.setFieldLabel(HarvesterUI.CONSTANTS.semantics() + HarvesterUI.REQUIRED_STR);
        semanticsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        semanticsCombo.add("NONE");
        semanticsCombo.add("DATA_SET_ID");
        semanticsCombo.add("DATA_SET_STATUS");
        semanticsCombo.add("SERVER_OAI_URL");
        semanticsCombo.add("DATA_SET_METADATA_FORMAT");
        semanticsCombo.add("DATA_SET_SCHEMA_URL");
        semanticsCombo.add("DATA_SET_LIST");
        semanticsCombo.add("EUDML_IMPORT_PM_CHAIN");
        semanticsCombo.add("EUDML_IMPORT_DMLCZ_SERIAL_CHAIN");

        semanticsCombo.setValue(semanticsCombo.getStore().getAt(0));

        typeCombo = new SimpleComboBox<String>();
        typeCombo.setEditable(false);
        typeCombo.setFieldLabel(HarvesterUI.CONSTANTS.type() + HarvesterUI.REQUIRED_STR);
        typeCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        typeCombo.add("Text");
        typeCombo.add("Boolean");
        typeCombo.add("Date");
        typeCombo.add("Combo");
        typeCombo.setValue(typeCombo.getStore().getAt(0));
        typeCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                if(se.getSelectedItem().getValue().equals("Combo")){
                    addComboValueField();
                } else{
                    removeAllComboValueFields();
                }
            }
        });

        required = new CheckBoxGroup();
        CheckBox checkBox = new CheckBox();
        required.setId("parameterRequiredField");
        checkBox.setValue(true);
        required.setFieldLabel(HarvesterUI.CONSTANTS.fieldRequired() + HarvesterUI.REQUIRED_STR);
        required.add(checkBox);

        add(required,formData);
        add(nameField,formData);
        add(exampleField,formData);
        add(semanticsCombo,formData);
        add(typeCombo,formData);
    }

    public NewServiceField(final NewServiceFieldSet parent,final ServiceParameterUI serviceParameterUI,int fieldNumber) {
        formData = new FormData("95%");

        setHeading(HarvesterUI.CONSTANTS.field()+ " " + fieldNumber);
        setLayout(new EditableFormLayout(120));

        LayoutContainer addContainer = new LayoutContainer();
        HBoxLayout operationsLayout = new HBoxLayout();
        operationsLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        operationsLayout.setPack(BoxLayout.BoxLayoutPack.END);
        addContainer.setLayout(operationsLayout);

        if(parent.getItemCount() > 0){
            Button removeButton = new Button(HarvesterUI.CONSTANTS.delete(), new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent be) {
                    removeFromParent();
                    parent.subtractFieldCount();
                }
            });
            removeButton.setIcon(HarvesterUI.ICONS.delete());
            addContainer.add(removeButton, new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        }

        add(addContainer);

        nameField = new TextField<String>();
        nameField.setFieldLabel(HarvesterUI.CONSTANTS.parameterName() + HarvesterUI.REQUIRED_STR);
        nameField.setId("parameterName");
        nameField.setAllowBlank(false);
        nameField.setValue(serviceParameterUI.getName());

        exampleField = new TextField<String>();
        exampleField.setFieldLabel(HarvesterUI.CONSTANTS.restExample());
        exampleField.setId("parameterExample");
        exampleField.setValue(serviceParameterUI.getExample());

        semanticsCombo = new SimpleComboBox<String>();
        semanticsCombo.setEditable(false);
        semanticsCombo.setFieldLabel(HarvesterUI.CONSTANTS.semantics() + HarvesterUI.REQUIRED_STR);
        semanticsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        semanticsCombo.add("NONE");
        semanticsCombo.add("DATA_SET_ID");
        semanticsCombo.add("DATA_SET_STATUS");
        semanticsCombo.add("SERVER_OAI_URL");
        semanticsCombo.add("DATA_SET_METADATA_FORMAT");
        semanticsCombo.add("DATA_SET_SCHEMA_URL");
        semanticsCombo.add("DATA_SET_LIST");
        semanticsCombo.add("EUDML_IMPORT_PM_CHAIN");
        semanticsCombo.add("EUDML_IMPORT_DMLCZ_SERIAL_CHAIN");
        semanticsCombo.setValue(semanticsCombo.getStore().getAt(0));

        typeCombo = new SimpleComboBox<String>();
        typeCombo.setEditable(false);
        typeCombo.setFieldLabel(HarvesterUI.CONSTANTS.type() + HarvesterUI.REQUIRED_STR);
        typeCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        typeCombo.add("Text");
        typeCombo.add("Boolean");
        typeCombo.add("Date");
        typeCombo.add("Combo");
        typeCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                if(se.getSelectedItem().getValue().equals("Combo")){
                    if(serviceParameterUI.getComboValues().size() > 0)
                        editComboValueFields(serviceParameterUI.getComboValues());
                    else
                        addComboValueField();
                } else{
                    removeAllComboValueFields();
                }
            }
        });

        required = new CheckBoxGroup();
        CheckBox checkBox = new CheckBox();
        checkBox.setValue(serviceParameterUI.getRequired());
        required.setId("parameterRequiredField");
        required.setFieldLabel(HarvesterUI.CONSTANTS.fieldRequired() + HarvesterUI.REQUIRED_STR);
        required.add(checkBox);

        add(required,formData);
        add(nameField,formData);
        add(exampleField,formData);
        add(semanticsCombo,formData);
        add(typeCombo,formData);

        semanticsCombo.setSimpleValue(serviceParameterUI.getSemantics());
        typeCombo.setSimpleValue(getTypeComboFormat(serviceParameterUI.getType()));
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width,height);
        super.layout();
        layout();
    }

    public String getFieldName(){
        return nameField.getValue();
    }

    public String getFieldExample(){
        return exampleField.getValue();
    }

    public String getFieldType(){
        return getTypeStoreFormat(typeCombo.getSimpleValue());
    }

    public String getFieldSemantics(){
        return semanticsCombo.getSimpleValue();
    }

    public boolean getRequired(){
        return (Boolean)required.get(0).getValue();
    }

    public void addComboValueField(){
        LayoutContainer addContainer = new LayoutContainer();
        HBoxLayout addLayout = new HBoxLayout();
        addLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        addContainer.setLayout(addLayout);
        addContainer.setId("comboValueField_AddContainer");

        TextField<String> comboValueField = new TextField<String>();
        comboValueField.setFieldLabel(HarvesterUI.CONSTANTS.comboValue()+ " " + comboCount);
        comboValueField.setId("comboValueField_" + comboCount);
        comboValueField.setAllowBlank(false);

        addContainer.add(new LabelToolItem(HarvesterUI.CONSTANTS.comboValue()+ " " + comboCount), new HBoxLayoutData(new Margins(0, 22, 5, 0)));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 5, 0));
        flex.setFlex(1);
        addContainer.add(comboValueField, flex);

        // First combo
        if(comboCount == 0){
            Button addCombo = new Button(HarvesterUI.CONSTANTS.addComboValue(), new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent be) {
                    addComboValueField();
                }
            });

            addContainer.add(addCombo, new HBoxLayoutData(new Margins(0, 0, 5, 0)));
            add(addContainer,formData);
        } else{
            add(comboValueField,formData);
        }

        layout();
        comboCount++;
    }

    public void editComboValueFields(List<String> comboValues){
        for(String comboValue : comboValues){
            LayoutContainer addContainer = new LayoutContainer();
            HBoxLayout addLayout = new HBoxLayout();
            addLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
            addContainer.setLayout(addLayout);
            addContainer.setId("comboValueField_AddContainer");

            TextField<String> comboValueField = new TextField<String>();
            comboValueField.setFieldLabel(HarvesterUI.CONSTANTS.comboValue()+ " " + comboCount);
            comboValueField.setId("comboValueField_" + comboCount);
            comboValueField.setAllowBlank(false);
            comboValueField.setValue(comboValue);

            addContainer.add(new LabelToolItem(HarvesterUI.CONSTANTS.comboValue()+ " " + comboCount), new HBoxLayoutData(new Margins(0, 22, 5, 0)));
            HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 5, 0));
            flex.setFlex(1);
            addContainer.add(comboValueField, flex);

            // First combo
            if(comboCount == 0){
                Button addCombo = new Button(HarvesterUI.CONSTANTS.addComboValue(), new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent be) {
                        addComboValueField();
                    }
                });

                addContainer.add(addCombo, new HBoxLayoutData(new Margins(0, 0, 5, 0)));
                add(addContainer,formData);
            } else{
                add(comboValueField,formData);
            }

            comboCount++;
        }
        layout();
    }

    private void removeAllComboValueFields(){
        for(int i=getItems().size()-1 ; i >=0 ; i--){
            if(getItems().get(i).getId().startsWith("comboValueField_")){
                getItems().get(i).removeFromParent();
            }
        }
        comboCount = 0;
        layout();
    }

    private String getTypeStoreFormat(String fieldType){
        if(fieldType.equals("Text"))
            return "TEXT_FIELD";
        else if(fieldType.equals("Boolean"))
            return "BOOLEAN_FIELD";
        else if(fieldType.equals("Date"))
            return "DATE_FIELD";
        else if(fieldType.equals("Combo"))
            return "COMBO_FIELD";

        return "";
    }

    private String getTypeComboFormat(String fieldType){
        if(fieldType.equals("TEXT_FIELD"))
            return "Text";
        else if(fieldType.equals("BOOLEAN_FIELD"))
            return "Boolean";
        else if(fieldType.equals("DATE_FIELD"))
            return "Date";
        else if(fieldType.equals("COMBO_FIELD"))
            return "Combo";

        return "";
    }
}
