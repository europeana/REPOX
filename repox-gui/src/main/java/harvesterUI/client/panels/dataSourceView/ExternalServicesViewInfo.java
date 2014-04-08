package harvesterUI.client.panels.dataSourceView;

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormLayout;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.externalServices.ExternalServiceUI;
import harvesterUI.shared.externalServices.ServiceParameterUI;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 20-03-2012
 * Time: 13:50
 */
public class ExternalServicesViewInfo extends FieldSet {

    private FormData formData;

    public ExternalServicesViewInfo(DataSourceUI dataSourceUI) {
        setLayoutOnChange(true);
        setHeading("External Services Info");
        setLayout(new DefaultFormLayout(UtilManager.DEFAULT_DATASET_VIEWINFO_LABEL_WIDTH));
        formData = new FormData("100%");

        for(ExternalServiceUI externalServiceUI : dataSourceUI.getRestServiceUIList()){
            addInfo(dataSourceUI,externalServiceUI);
        }
    }

    public void addInfo(DataSourceUI dataSourceUI,ExternalServiceUI externalServiceUI){
        LabelField name = new LabelField();
        name.setFieldLabel(HarvesterUI.CONSTANTS.name());
        name.setValue(externalServiceUI.getName());
        add(name, formData);

        LabelField executionType = new LabelField();
        executionType.setFieldLabel(HarvesterUI.CONSTANTS.executionType());
        executionType.setValue(externalServiceUI.getType());
        add(executionType, formData);

        LabelField uri = new LabelField();
        uri.setFieldLabel(HarvesterUI.CONSTANTS.uri());
        uri.setValue(externalServiceUI.getUri());
        add(uri, formData);

        for(ServiceParameterUI serviceParameterUI : externalServiceUI.getServiceParameters()){
            LabelField parameter = new LabelField();
            parameter.setFieldLabel(serviceParameterUI.getName());
            parameter.setValue(serviceParameterUI.getValue());
            add(parameter, formData);

//            LabelField value = new LabelField();
//            value.setFieldLabel("Value");
//            value.setValue(externalServiceUI.getUri());
//            add(value, formData);
        }

        if(!dataSourceUI.getRestServiceUIList().get(dataSourceUI.getRestServiceUIList().size()-1).equals(externalServiceUI))
            add(new LabelToolItem("<br/>"),formData);
    }
}
