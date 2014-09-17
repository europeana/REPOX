package harvesterUI.client.panels.services;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.externalServices.ServiceParameterUI;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 10/02/12
 * Time: 15:38
 */
public class DataSetListParameter extends LayoutContainer{

    private TextField<String> textField;
    private DataSetListDialog dataSetListDialog;

    public DataSetListParameter(ServiceParameterUI serviceParameterUI) {
        setId(serviceParameterUI.getId());

        dataSetListDialog = new DataSetListDialog(this);

        Button showDataSetListButton = new Button("Add Data Sets");
        showDataSetListButton.setIcon(HarvesterUI.ICONS.add());
        showDataSetListButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                dataSetListDialog.edit(textField.getValue());
                dataSetListDialog.showAndCenter();
            }
        });

        HBoxLayout dataSetListContainerLayout = new HBoxLayout();
        dataSetListContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        setLayout(dataSetListContainerLayout);
        LabelToolItem label = new LabelToolItem(serviceParameterUI.getName());
        label.setWidth(154);
        label.addStyleName("defaultFormFieldLabel");
        add(label, new HBoxLayoutData(new Margins(-1, 2, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
        add(showDataSetListButton, new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0));
        flex.setFlex(1);

        textField = new TextField<String>();
        textField.setAllowBlank(false);
        add(textField, flex);

        // Check if has value
        if(serviceParameterUI.getValue() != null)
            textField.setValue(serviceParameterUI.getValue());
    }

    public TextField<String> getTextField() {
        return textField;
    }
}
