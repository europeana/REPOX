package harvesterUI.client.panels.harvesting.dialogs;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormLayout;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.mdr.TransformationUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 17:19
 */
public class ExportNowDialog extends Dialog {

    private HarvestOperationsServiceAsync service;
    private SimpleComboBox<String> recordsPerFileCombo;
    private ListStore<TransformationUI> transformationsStore;
    private ComboBox<TransformationUI> transformationCombo;
    private TextField<String> exportPathField;
    private Button exportNowButton;
    protected DefaultFormPanel exportNowFormPanel;
    private DataSourceUI dataSourceUI;

    public ExportNowDialog(DataSourceUI dataSourceUI) {
        service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
        this.dataSourceUI = dataSourceUI;
        createExportNowDialog();
    }

    private void createExportNowDialog() {
        setButtons("");
        setLayout(new FitLayout());
        setHeading(HarvesterUI.CONSTANTS.exportNow());
        setIcon(HarvesterUI.ICONS.export_now_icon());
        setResizable(true);
        setModal(true);
        setSize(600,190);

        exportNowFormPanel = new DefaultFormPanel();
        exportNowFormPanel.setHeaderVisible(false);

        exportNowFormPanel.setLayout(new DefaultFormLayout(150));

        recordsPerFileCombo = new SimpleComboBox<String>();
        recordsPerFileCombo.setEditable(false);
        recordsPerFileCombo.setFieldLabel(HarvesterUI.CONSTANTS.recordsPerFile());
        recordsPerFileCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        recordsPerFileCombo.add("1");
        recordsPerFileCombo.add("10");
        recordsPerFileCombo.add("100");
        recordsPerFileCombo.add("1000");
        recordsPerFileCombo.add("All");
        recordsPerFileCombo.setValue(recordsPerFileCombo.getStore().getAt(0));
        exportNowFormPanel.add(recordsPerFileCombo, new FormData("100%"));

        transformationsStore = new ListStore<TransformationUI>();
        transformationsStore.add(new TransformationUI(dataSourceUI.getSourceMDFormat(),"","",dataSourceUI.getSourceMDFormat(),"","","",false));
        if(dataSourceUI.getMetadataTransformations().size() > 0) {
                transformationsStore.add(dataSourceUI.getMetadataTransformations());
        }

        exportPathField = new TextField<String>();
        exportPathField.setAllowBlank(false);
        exportPathField.setId("exportFullPathField");
        exportPathField.setFieldLabel(HarvesterUI.CONSTANTS.fullPath());
        exportPathField.setValue(dataSourceUI.getExportDirectory());
        exportNowFormPanel.add(exportPathField,new FormData("100%"));
        
        transformationCombo = new ComboBox<TransformationUI>();
        transformationCombo.setEditable(false);
        transformationCombo.setFieldLabel(HarvesterUI.CONSTANTS.exportFormat());
        transformationCombo.setDisplayField("destFormat");
        transformationCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        transformationCombo.setStore(transformationsStore);
        transformationCombo.setValue(transformationsStore.getModels().get(0));
        exportNowFormPanel.add(transformationCombo, new FormData("100%"));

        exportNowButton = new Button(HarvesterUI.CONSTANTS.export(),HarvesterUI.ICONS.export_now_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.exportNow(), HarvesterUI.CONSTANTS.exportFailed());
                            return;
                        }
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.exportNow(), HarvesterUI.CONSTANTS.exportWillStart());
                        hide();
                    }
                };
                dataSourceUI.setRecordsPerFile(recordsPerFileCombo.getValue().getValue());
                dataSourceUI.setExportDirectory(exportPathField.getValue());
                String outputFormat = transformationCombo.getValue().getDestFormat();
                if(outputFormat.equals(dataSourceUI.getSourceMDFormat()))
                    outputFormat = "";
                dataSourceUI.setExportFormat(outputFormat);
                service.dataSourceExport(dataSourceUI, callback);
            }
        });
        exportNowFormPanel.addButton(exportNowButton);

        exportNowFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        add(exportNowFormPanel);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
    }
}
