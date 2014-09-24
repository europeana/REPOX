package harvesterUI.client.panels.mdr.forms.xmapper;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;
import harvesterUI.shared.mdr.TransformationUI;

import java.util.List;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 30-10-2012
 * Time: 16:18
 */
public class SourceAndTargetFields {

    private ComboBox<SchemaUI> _sourceMetadataFormatCombo;
    private ComboBox<SchemaVersionUI> _sourceSchemaVersionCombo;


    private ComboBox<SchemaUI> _targetMetadataFormatCombo;
    private ComboBox<SchemaVersionUI> _targetSchemaVersionCombo;

    private SchemasDetailsContainer _details;

    public SourceAndTargetFields(DefaultFormPanel formPanel, FormData formData) {
        createMetadataFormatCombo(true);
        createMetadataFormatCombo(false);

        createSchemaVersionCombo(true);
        createSchemaVersionCombo(false);

        addFieldsToForm(formPanel, formData);
    }

    private void addFieldsToForm(DefaultFormPanel formPanel, FormData formData) {
        formPanel.add(_sourceMetadataFormatCombo, formData);
        formPanel.add(_sourceSchemaVersionCombo, formData);

        formPanel.add(_targetMetadataFormatCombo, formData);
        formPanel.add(_targetSchemaVersionCombo, formData);
        generateDetailsFields();
    }

    private void generateDetailsFields(){
        _details = new SchemasDetailsContainer();
    }

    protected Html generateDetailsHtml(SchemaUI schema, SchemaVersionUI version) {
        return new Html("<div>"+
                "<div class=\"mdr-transf-details-top\"\">"+schema.getShortDesignation()+"</div>"+
                "<div class=\"mdr-transf-details-mid\">"+version.getXsdLink()+"</div>"+
                "<div style=\"mdr-transf-details-bot\">"+schema.getShortDesignation()+", Version "+version.getVersion()+"</div>"+
                // "<div style=\"color:black;padding-bottom:2px;\">Salary:"+employee.getSalary()+"</div>"+
                "</div>");
    }

    private void createMetadataFormatCombo(final boolean isSource){
        ComboBox<SchemaUI> combo;
        if(isSource) {
            _sourceMetadataFormatCombo = new ComboBox<SchemaUI>();
            combo = _sourceMetadataFormatCombo;
            combo.setFieldLabel("Source Format" + HarvesterUI.REQUIRED_STR);
            combo.setId("sourceMetadataFormatComboId");
        }
        else {
            _targetMetadataFormatCombo =  new ComboBox<SchemaUI>();
            combo = _targetMetadataFormatCombo;
            combo.setFieldLabel("Destination Format" + HarvesterUI.REQUIRED_STR);
            combo.setId("targetMetadataFormatComboId");
        }

        ListStore<SchemaUI> metadataFormatStore = new ListStore<SchemaUI>();
        combo.setEmptyText("Choose Schema...");
        combo.setAllowBlank(false);

        combo.setDisplayField("shortDesignation");
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(metadataFormatStore);
        combo.setEditable(true);
        combo.addSelectionChangedListener(new SelectionChangedListener<SchemaUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SchemaUI> se) {
                if(se.getSelectedItem() != null) {
                    fillSchemaVersionCombo(se.getSelectedItem(), isSource);
                }
            }
        });
    }

    private void createSchemaVersionCombo(final boolean isSource){
        ComboBox<SchemaVersionUI> combo;
        if(isSource) {
            _sourceSchemaVersionCombo = new ComboBox<SchemaVersionUI>();
            combo = _sourceSchemaVersionCombo;
        }
        else {
            _targetSchemaVersionCombo = new ComboBox<SchemaVersionUI>();
            combo = _targetSchemaVersionCombo;
        }

        ListStore<SchemaVersionUI> schemaVersionComboStore = new ListStore<SchemaVersionUI>();
        combo.setFieldLabel("Version" + HarvesterUI.REQUIRED_STR);
        combo.setEmptyText("Choose Schema Version...");
        combo.setAllowBlank(false);
        combo.setDisplayField("version");
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(schemaVersionComboStore);
        combo.setEditable(false);
        combo.addSelectionChangedListener(new SelectionChangedListener<SchemaVersionUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SchemaVersionUI> se) {
                ComboBox<SchemaUI> schema;
                if(isSource)
                    schema = _sourceMetadataFormatCombo;
                else
                    schema = _targetMetadataFormatCombo;

                if(se.getSelectedItem() != null) {
                    _details.mask("Loading...");
                    _details.populateDetails(schema.getValue(), se.getSelectedItem(), isSource);
                    _details.unmask();
                }
                _details.layout(true);
            }
        });
    }

    public void fillSchemaVersionCombo(SchemaUI schemaUI, boolean isSource){
        ComboBox<SchemaVersionUI> combo;
        if(isSource)
            combo = _sourceSchemaVersionCombo;
        else combo = _targetSchemaVersionCombo;

        combo.getStore().removeAll();
        combo.getStore().add(schemaUI.getSchemaVersions());
        combo.setValue(schemaUI.getSchemaVersions().get(0));
    }

    public void loadCombos(final TransformationUI transformationUI){
        AsyncCallback<List<SchemaUI>> callback = new AsyncCallback<List<SchemaUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<SchemaUI> results) {
                _sourceMetadataFormatCombo.getStore().removeAll();
                _sourceMetadataFormatCombo.getStore().add(results);
                _targetMetadataFormatCombo.getStore().removeAll();
                _targetMetadataFormatCombo.getStore().add(results);

                // clear combo boxes filters
                _sourceMetadataFormatCombo.getStore().clearFilters();
                _targetMetadataFormatCombo.getStore().clearFilters();

                /*_sourceMetadataFormatCombo.setValue(_sourceMetadataFormatCombo.getStore().getAt(0)); //Default schema
                _targetMetadataFormatCombo.setValue(_targetMetadataFormatCombo.getStore().getAt(0)); //Default schema*/

                if(transformationUI != null)
                    edit(transformationUI);
            }
        };
        TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        service.getAllMetadataSchemas(callback);
    }

    public void resetFields() {
        _sourceMetadataFormatCombo.setValue(_sourceMetadataFormatCombo.getStore().getAt(0)); //Default schema
        _targetMetadataFormatCombo.setValue(_targetMetadataFormatCombo.getStore().getAt(0)); //Default schema

        _targetSchemaVersionCombo.setValue(_targetSchemaVersionCombo.getStore().getAt(0)); //Default VERSION
        _sourceSchemaVersionCombo.setValue(_sourceSchemaVersionCombo.getStore().getAt(0)); //Default VERSION

    }

    public void edit(TransformationUI transformationUI) {
        if(transformationUI.isMDRCompliant()) {
            SchemaUI srcSchemaUI = _sourceMetadataFormatCombo.getStore().findModel("shortDesignation", transformationUI.getSrcFormat());
            SchemaUI trgSchemaUI = _targetMetadataFormatCombo.getStore().findModel("shortDesignation", transformationUI.getDestFormat());
            _sourceMetadataFormatCombo.setValue(srcSchemaUI);
            _targetMetadataFormatCombo.setValue(trgSchemaUI);


            SchemaVersionUI srcVersionUI = _sourceSchemaVersionCombo.getStore().findModel("xsdLink", transformationUI.getSourceSchema());
            _sourceSchemaVersionCombo.setValue(srcVersionUI);

            SchemaVersionUI trgVersionUI = _targetSchemaVersionCombo.getStore().findModel("xsdLink", transformationUI.getDestSchema());
            _targetSchemaVersionCombo.setValue(trgVersionUI);
        }
        else emptyFields();
    }

    protected void emptyFields() {
        _targetMetadataFormatCombo.clear();
        _sourceMetadataFormatCombo.clear();
        _targetSchemaVersionCombo.clear();
        _sourceSchemaVersionCombo.clear();

        _details.clearDetails();
    }

    /*
     * GETS
     */

    public String getSourceFormat() {
        return _sourceMetadataFormatCombo.getValue().getShortDesignation();
    }

    public String getSourceVersion() {
        return _sourceSchemaVersionCombo.getValue().getVersion().toString();
    }

    public String getSourceSchema() {
        return _sourceSchemaVersionCombo.getValue().getXsdLink();
    }

    public String getSourceMNamespace() {
        return _sourceMetadataFormatCombo.getValue().getNamespace();
    }

    public String getTargetFormat() {
        return _targetMetadataFormatCombo.getValue().getShortDesignation();
    }

    public String getTargetVersion() {
        return _targetSchemaVersionCombo.getValue().getVersion().toString();
    }

    public String getTargetSchema() {
        return _targetSchemaVersionCombo.getValue().getXsdLink();
    }

    public String getTargetMNamespace() {
        return _targetMetadataFormatCombo.getValue().getNamespace();
    }

    public SchemasDetailsContainer getDetailsContainer() {
        return _details;
    }

    /* Get Combo values */
    public SchemaUI getSourceSchemaUI() {
        return  _sourceMetadataFormatCombo.getValue();
    }

    public SchemaVersionUI getSourceVersionUI() {
        return _sourceSchemaVersionCombo.getValue();
    }

    public SchemaUI getDestSchemaUI() {
        return  _targetMetadataFormatCombo.getValue();
    }

    public SchemaVersionUI getDestVersionUI() {
        return _targetSchemaVersionCombo.getValue();
    }
}
