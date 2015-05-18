package harvesterUI.client.panels.forms.dataSources;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.DatasetType;
import harvesterUI.shared.mdr.SchemaUI;

import java.util.List;
import java.util.Map;

import pt.utl.ist.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 15-03-2011
 * Time: 14:14
 */
public class DataSourceOAIForm extends DataSourceForm {

    private FormData formData;
    private String oldDataSetId = "";

    // Fields
    private TextField<String> oaiUrl, oaiSet;
    private SimpleComboBox<String> setsCombo, mdPrefixesCombo;

    private TextField<String> name, nameCode,exportPath;

    public DataSourceOAIForm(FormData data) {
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

        oaiUrl = new TextField<String>();
        oaiUrl.setId("oaiUrlField");
        oaiUrl.setAllowBlank(false);

        Button check = new Button(HarvesterUI.CONSTANTS.check(),HarvesterUI.ICONS.oai_check(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                AsyncCallback<Map<String,List<String>>> callback = new AsyncCallback<Map<String,List<String>>>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(Map<String,List<String>> result) {
                        if(result == null) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.invalidUrl());
                            return;
                        }else if(result.get("ERROR") != null) {
                            if(result.get("ERROR").get(0).equals("URL_MALFORMED")) {
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.oaiUrlMalformed());
                                return;
                            } else if(result.get("ERROR").get(0).equals("URL_NOT_EXISTS")) {
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.oaiUrlNotExists());
                                return;
                            }
                        }

                        setsCombo.getStore().removeAll();
                        setsCombo.add("--none--");
                        setsCombo.add(result.get("sets"));
                        setsCombo.getStore().sort("value",Style.SortDir.ASC);
                        setsCombo.setValue(setsCombo.getStore().getAt(0));
                        setsCombo.show();
                        setsCombo.setAllowBlank(false);
                        oaiSet.hide();

                        if(result.get("mdPrefixes").size() > 0){
                            mdPrefixesCombo.getStore().removeAll();
                            mdPrefixesCombo.add(result.get("mdPrefixes"));
                            mdPrefixesCombo.getStore().sort("value",Style.SortDir.ASC);
                            mdPrefixesCombo.setValue(mdPrefixesCombo.getStore().getAt(0));
                            mdPrefixesCombo.show();
                            mdPrefixesCombo.setAllowBlank(false);
                            dataSourceSchemaForm.addMetadataFormatContainer(mdPrefixesCombo,3,true);
                        }

                        submit();
                    }
                };
                if(oaiUrl.getValue() == null || oaiUrl.getValue().trim().equals(""))
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.pleaseInsertUrl());
                else
                    dataSetOperationsService.checkOAIURL(oaiUrl.getValue().trim(), callback);
            }
        });
        Button addAll = new Button(HarvesterUI.CONSTANTS.addAll(),HarvesterUI.ICONS.oai_add_all(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                // Check required data in Europeana
                if(HarvesterUI.getProjectType() == ProjectType.DEFAULT && (name.getValue() == null || nameCode.getValue() == null)) {
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.addAll(), HarvesterUI.CONSTANTS.pleaseFillNameAndNamecode());
                    return;
                }

                AsyncCallback callback = new AsyncCallback() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        unmask();
                    }
                    public void onSuccess(Object result) {
                        if(result.equals("URL_MALFORMED")) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.addAll(), HarvesterUI.CONSTANTS.oaiUrlMalformed());
                            unmask();
                            return;
                        } else if(result.equals("URL_NOT_EXISTS")) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.addAll(), HarvesterUI.CONSTANTS.oaiUrlNotExists());
                            unmask();
                            return;
                        }
                        submit();
                        unmask();
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.addAll(), HarvesterUI.CONSTANTS.allDataSetsAddedSuccess());
                        Dispatcher.get().dispatch(AppEvents.LoadMainData);
                        Dispatcher.forwardEvent(AppEvents.HideDataSourceForm);
                    }
                };
                mask(HarvesterUI.CONSTANTS.addAllDataSetsMask());
                String metadataFormat = dataSourceSchemaForm.getMetadataFormatCombo().getValue().getShortDesignation();
                String schem = dataSourceSchemaForm.getSchema().getValue();
                String mtdNamespace = dataSourceSchemaForm.getMetadataNamespace().getValue();
                if(HarvesterUI.getProjectType() == ProjectType.DEFAULT){
                    dataSetOperationsService.addAllOAIURL(oaiUrl.getValue().trim(), parent.getId(), schem,mtdNamespace,
                            metadataFormat, name.getValue(), nameCode.getValue(), exportPath.getValue(), callback);
                }else {
                    dataSetOperationsService.addAllOAIURL(oaiUrl.getValue().trim(), parent.getId(), schem,mtdNamespace,
                            metadataFormat, "", "", "", callback);
                }
            }
        });

        LayoutContainer container = new LayoutContainer();
        HBoxLayout oaiUrlCotnainerLayout = new HBoxLayout();
        oaiUrlCotnainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        container.setLayout(oaiUrlCotnainerLayout);
        LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.oaiUrl()+ HarvesterUI.REQUIRED_STR);
        label.setWidth(SPECIAL_FIELDS_LABEL_WIDTH);
        label.addStyleName("defaultFormFieldLabel");
        container.add(label, new HBoxLayoutData(new Margins(0, 2, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0));
        flex.setFlex(1);
        container.add(oaiUrl, flex);
        container.add(check, new HBoxLayoutData(new Margins(0, 5, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        container.add(addAll, new HBoxLayoutData(new Margins(0, 0, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        dataSet.add(container,formData);

        setsCombo = new SimpleComboBox<String>();
        setsCombo.setFieldLabel(HarvesterUI.CONSTANTS.oaiSet());
        setsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        setsCombo.setEditable(false);
        dataSet.add(setsCombo,smallFixedFormData);

        mdPrefixesCombo = new SimpleComboBox<String>();
        mdPrefixesCombo.setFieldLabel(HarvesterUI.CONSTANTS.metadataFormat());
        mdPrefixesCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        mdPrefixesCombo.setEditable(false);
        mdPrefixesCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                if(se.getSelectedItem() != null) {
                    boolean matchFound = false;
                    for(SchemaUI schemaUI : dataSourceSchemaForm.getMetadataFormatCombo().getStore().getModels()) {
                        if(schemaUI.getShortDesignation().equals(se.getSelectedItem().getValue())) {
                            dataSourceSchemaForm.fillSchemaVersionCombo(schemaUI);
                            dataSourceSchemaForm.getMetadataNamespace().setValue(schemaUI.getNamespace());
                            dataSourceSchemaForm.removeNoSchemaFoundWarning();
                            matchFound = true;
                            break;
                        }
                    }
                    if(!matchFound){
                        dataSourceSchemaForm.getSchema().clear();
                        dataSourceSchemaForm.getMetadataNamespace().clear();
                        dataSourceSchemaForm.setNoSchemasAvailable();
                    }
                }
            }
        });
        dataSet.add(mdPrefixesCombo, smallFixedFormData);

        oaiSet = new TextField<String>();
        oaiSet.setFieldLabel(HarvesterUI.CONSTANTS.oaiSet());
        oaiSet.setId("oaiSetField");
        oaiSet.setAllowBlank(true);
        dataSet.add(oaiSet, formData);

        dataSourceSchemaForm.addSchemaOAIFormaPart(dataSet, smallFixedFormData, formData);

        exportPath = new TextField<String>();
        exportPath.setFieldLabel(HarvesterUI.CONSTANTS.exportPath());
        exportPath.setId("exportPathField");
        exportPath.setValue(HarvesterUI.getMainConfigurationData().getDefaultExportFolder() + "/");
        dataSet.add(exportPath, formData);

        addIsSampleCheckBox(dataSet);
    }

    public void setEditMode(DataSourceUI ds){
        editIsSampleCheckBox(ds.isSample());
        setScrollMode(Style.Scroll.AUTO);
        dataSourceUI = ds;
        dataSourceSchemaForm.removeNoSchemaFoundWarning();
        dataSourceSchemaForm.addMetadataFormatContainer(dataSourceSchemaForm.getMetadataFormatCombo(),4,false);
        fillMetadataComboStore(true);
        edit = true;
        oldDataSetId = ds.getDataSourceSet();

        editTagsContainer(dataSourceUI);
        setEditTransformationCombo(dataSourceUI);
        dataSourceServicesPanel.setEditServices(dataSourceUI);

        dataSourceSchemaForm.getSchema().setValue(dataSourceUI.getSchema());
        dataSourceSchemaForm.getMetadataNamespace().setValue(dataSourceUI.getMetadataNamespace());
        dataSourceSchemaForm.editMarcCombo(dataSourceUI.getMarcFormat());
        oaiUrl.setValue(dataSourceUI.getOaiSource());
        oaiSet.setValue(dataSourceUI.getOaiSet());
        oaiSet.show();
        recordSet.setValue(dataSourceUI.getDataSourceSet());
        description.setValue(dataSourceUI.getDescription());
        exportPath.setValue(dataSourceUI.getExportDirectory());

        setsCombo.hide();
        setsCombo.setAllowBlank(true);
        mdPrefixesCombo.setAllowBlank(true);
        mdPrefixesCombo.hide();

        // Europeana Fields
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            name.setValue(dataSourceUI.getName());
            nameCode.setValue(dataSourceUI.getNameCode());
        }
    }

    public void resetValues(DataProviderUI parent) {
        dataSourceSchemaForm.removeNoSchemaFoundWarning();
        dataSourceSchemaForm.addMetadataFormatContainer(dataSourceSchemaForm.getMetadataFormatCombo(),4,false);
        fillMetadataComboStore(false);
        edit = false;
        oldDataSetId = "";
        oaiUrl.clear();
        oaiSet.clear();
        setsCombo.hide();
        oaiSet.show();
        mdPrefixesCombo.hide();
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
        if(mdPrefixesCombo.isVisible())
            return mdPrefixesCombo.getSimpleValue();
        else
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
        String metadataFormat;
        if(mdPrefixesCombo.isVisible()) {
            metadataFormat = mdPrefixesCombo.getValue().getValue();
        } else
            metadataFormat = dataSourceSchemaForm.getMetadataFormatCombo().getValue().getShortDesignation();

        String oai_url = oaiUrl.getValue().trim();
        String oai_set;
        if(oaiSet.isVisible())
            oai_set = oaiSet.getValue();
        else
            oai_set = setsCombo.getValue().getValue();

        String desc = description.getValue();
        if(dataSourceUI == null) {
            dataSourceUI = new DataSourceUI(parent, desc.trim(), "", metadataFormat + " | ese", "OAI-PMH " + metadataFormat.trim(),
                    parent.getCountryCode(),desc.trim(), "", oai_url.trim(), oai_set != null ? oai_set.trim() : "",
                    "", IdGeneratedRecordIdPolicy.IDGENERATED,metadataFormat);
        }

        dataSourceUI.setIngest("OAI-PMH " + metadataFormat.trim());
        dataSourceUI.setSourceMDFormat(metadataFormat != null ? metadataFormat.trim() : "");
        dataSourceUI.setOaiSource(oai_url != null ? oai_url.trim() : "");
        dataSourceUI.setOaiSet((oai_set != null && !oai_set.equals("") && !oai_set.equals("--none--")) ? oai_set.trim() : null);

        dataSourceUI.setExportDirectory(exportPath.getValue() != null ? exportPath.getValue().trim() : "");

        dataSourceUI.setMarcFormat(dataSourceSchemaForm.getMarcFormat().trim());

        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT)
            saveDataSource(dataSourceUI,oldDataSetId, DatasetType.OAI,dataSourceSchemaForm.getSchema().getValue(),dataSourceSchemaForm.getMetadataNamespace().getValue(),
                    metadataFormat,name.getValue(),nameCode.getValue(),exportPath.getValue());
//        else
//            saveDataSource(dataSourceUI,oldDataSetId,DatasetType.OAI,dataSourceSchemaForm.getSchema().getValue(),dataSourceSchemaForm.getMetadataNamespace().getValue(),
//                    metadataFormat,"","","");
    }
}
