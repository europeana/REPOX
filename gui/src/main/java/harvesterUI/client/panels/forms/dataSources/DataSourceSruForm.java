package harvesterUI.client.panels.forms.dataSources;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;
import pt.utl.ist.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 15-03-2011
 * Time: 14:14
 */
public class DataSourceSruForm extends DataSourceForm {

    private FormData formData;
    private String oldDataSetId = "";

    // Europeana Fields
    private TextField<String> name, nameCode,exportPath;

    public DataSourceSruForm(FormData data) {
        super(data);
        formData = data;
        dataSourceSchemaForm = new DataSourceSchemaForm();
        setHeaderVisible(false);
        setLayout(new FitLayout());
        setBodyBorder(false);

        createDataSet();
        add(dataSet);
        add(createOutputSet());
        add(dataSourceServicesPanel);

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                saveData();
            }
        });
        addButton(saveButton);

        addButton(new Button("View Other Data Set",HarvesterUI.ICONS.search_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                Dispatcher.forwardEvent(AppEvents.CompareDataSets);
            }
        }));

        addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                Dispatcher.forwardEvent(AppEvents.HideDataSourceForm);
            }
        }));

        setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(this);
        binding.addButton(saveButton);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setScrollMode(Style.Scroll.AUTO);
    }

    private void createDataSet() {
        dataSet = new FieldSet();
        dataSet.setHeading(HarvesterUI.CONSTANTS.dataSet());
        dataSet.setAutoWidth(true);
        dataSet.setAutoHeight(true);
        dataSet.setLayout(new EditableFormLayout(DEFAULT_LABEL_WIDTH));

        dataSourceSchemaForm.addSchemaSRUFormPart(dataSet, smallFixedFormData, formData);

        exportPath = new TextField<String>();
        exportPath.setFieldLabel(HarvesterUI.CONSTANTS.exportPath());
        exportPath.setId("exportPathField");
        dataSet.add(exportPath, formData);

        addIsSampleCheckBox(dataSet);
    }

    public void setEditMode(DataSourceUI ds){
        editIsSampleCheckBox(ds.isSample());
        setScrollMode(Style.Scroll.AUTO);
        dataSourceUI = ds;
        dataSourceSchemaForm.removeNoSchemaFoundWarning();
        dataSourceSchemaForm.addMetadataFormatContainer(dataSourceSchemaForm.getMetadataFormatCombo(),0,false);
        fillMetadataComboStore(true);
        edit = true;
        oldDataSetId = ds.getDataSourceSet();

        editTagsContainer(dataSourceUI);
        setEditTransformationCombo(dataSourceUI);
        dataSourceServicesPanel.setEditServices(dataSourceUI);

        dataSourceSchemaForm.getSchema().setValue(dataSourceUI.getSchema());
        dataSourceSchemaForm.getMetadataNamespace().setValue(dataSourceUI.getMetadataNamespace());
        dataSourceSchemaForm.editMarcCombo(dataSourceUI.getMarcFormat());
        recordSet.setValue(dataSourceUI.getDataSourceSet());
        description.setValue(dataSourceUI.getDescription());
        exportPath.setValue(dataSourceUI.getExportDirectory());
        exportPath.setValue(HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/");

        // Europeana Fields
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            name.setValue(dataSourceUI.getName());
            nameCode.setValue(dataSourceUI.getNameCode());
        }
    }

    public void resetValues(DataProviderUI parent) {
        dataSourceSchemaForm.removeNoSchemaFoundWarning();
        dataSourceSchemaForm.addMetadataFormatContainer(dataSourceSchemaForm.getMetadataFormatCombo(),0,false);
        fillMetadataComboStore(false);
        edit = false;
        oldDataSetId = "";
//        exportPath.clear();
        exportPath.setValue(HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/");

        setResetNamespaces();
        setResetOutputSet(parent);
        dataSourceServicesPanel.resetValues();

        dataSourceSchemaForm.getMetadataFormatCombo().getStore().clearFilters();

        // Europeana Fields
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            name.clear();
            nameCode.clear();
        }
    }

    public void addEuropeanaFields() {
        name = new TextField<String>();
        name.setFieldLabel(HarvesterUI.CONSTANTS.name());
        name.setId("nameField");
        dataSet.add(name, formData);

        nameCode = new TextField<String>();
        nameCode.setFieldLabel(HarvesterUI.CONSTANTS.nameCode());
        nameCode.setId("nameCodeField");
        dataSet.add(nameCode, formData);
    }

    public String getMetadataFormat() {
            return dataSourceSchemaForm.getMetadataFormatCombo().getValue().getShortDesignation();
    }

    public String getFolderPath() {
        return null;
    }

    public String getSchema() {
        return dataSourceSchemaForm.getSchema().getValue();
    }

    private void fillMetadataComboStore(boolean edit){
        dataSourceSchemaForm.loadMetadataFormatComboSchemas(edit, dataSourceUI);
    }

    public void saveData(){
        String metadataFormat = dataSourceSchemaForm.getMetadataFormatCombo().getValue().getShortDesignation();

        String desc = description.getValue();
        if(dataSourceUI == null) {
            dataSourceUI = new DataSourceUI(parent, desc.trim(), "", metadataFormat + " | ese", "SRU " + metadataFormat.trim(),
                    parent.getCountryCode(),desc.trim(), "", "", "","", IdGeneratedRecordIdPolicy.IDGENERATED,metadataFormat);
        }

        dataSourceUI.setIngest("SRU " + metadataFormat.trim());
        dataSourceUI.setSourceMDFormat(metadataFormat.trim());

        dataSourceUI.setExportDirectory(exportPath.getValue() != null ? exportPath.getValue().trim() : "");

        dataSourceUI.setMarcFormat(dataSourceSchemaForm.getMarcFormat().trim());

        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT)
            saveDataSource(dataSourceUI,oldDataSetId, DatasetType.SRU,dataSourceSchemaForm.getSchema().getValue(),dataSourceSchemaForm.getMetadataNamespace().getValue(),
                    metadataFormat,name.getValue(),nameCode.getValue(),exportPath.getValue());
        else
            saveDataSource(dataSourceUI,oldDataSetId,DatasetType.SRU,dataSourceSchemaForm.getSchema().getValue(),dataSourceSchemaForm.getMetadataNamespace().getValue(),
                    metadataFormat,"","","");
    }
}
