package harvesterUI.client.panels.dataSourceView;

import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.ProjectType;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.mdr.TransformationUI;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 29-01-2013
 * Time: 19:23
 */
public class DataSetDefaultViewInfo {

    private CheckBox isSample;
    private LabelField idPolicy,recordSet, description, schema, metadataSchema,
            records, deletedRecords, name, nameCode, tags, metadataFormat;

    protected void addDefaultFields(FieldSet infoSetFolder, FormData formData){

        metadataFormat = new LabelField();
        metadataFormat.setFieldLabel(HarvesterUI.CONSTANTS.localMetadataFormat());
        infoSetFolder.add(metadataFormat, formData);

        idPolicy = new LabelField();
        idPolicy.setFieldLabel(HarvesterUI.CONSTANTS.idPolicy());
        infoSetFolder.add(idPolicy,formData);

        recordSet = new LabelField();
        recordSet.setFieldLabel(HarvesterUI.CONSTANTS.recordSet());
        infoSetFolder.add(recordSet,formData);

        description = new LabelField();
        description.setFieldLabel(HarvesterUI.CONSTANTS.description());
        infoSetFolder.add(description,formData);

        schema = new LabelField();
        schema.setFieldLabel(HarvesterUI.CONSTANTS.schema());
        infoSetFolder.add(schema,formData);

        metadataSchema = new LabelField();
        metadataSchema.setId("metadataschema");
        metadataSchema.setFieldLabel(HarvesterUI.CONSTANTS.metadataNamespace());
        infoSetFolder.add(metadataSchema,formData);

        // Europeana Only
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            name = new LabelField();
            name.setFieldLabel(HarvesterUI.CONSTANTS.name());
            infoSetFolder.add(name,formData);

            nameCode = new LabelField();
            nameCode.setFieldLabel(HarvesterUI.CONSTANTS.nameCode());
            infoSetFolder.add(nameCode,formData);
        }

        records = new LabelField();
        records.setId("recordsViewDSInfo");
        records.setFieldLabel(HarvesterUI.CONSTANTS.numberOfRecords());
        infoSetFolder.add(records,formData);

        deletedRecords = new LabelField();
        deletedRecords.setId("deletedRecordsViewDSInfo");
        deletedRecords.setFieldLabel("Deleted Records");
        infoSetFolder.add(deletedRecords,formData);

        CheckBoxGroup checkBoxGroup = new CheckBoxGroup();
        isSample = new CheckBox();
        isSample.setEnabled(false);
        checkBoxGroup.setId("isSampleCB");
        checkBoxGroup.setFieldLabel("Is Sample?");
        checkBoxGroup.add(isSample);
        infoSetFolder.add(checkBoxGroup);

        tags = new LabelField();
        tags.setFieldLabel("Tags");
        infoSetFolder.add(tags,formData);
    }

    protected void loadDefaultFields(final DataSourceUI dataSourceUI, FieldSet infoSetFolder, FormData formData){
        metadataFormat.setValue(dataSourceUI.getMetadataFormat());
        recordSet.setValue(dataSourceUI.getDataSourceSet());
        description.setValue(dataSourceUI.getDescription());
        schema.setValue(dataSourceUI.getSchema());
        metadataSchema.setValue(dataSourceUI.getMetadataNamespace());
        isSample.setValue(dataSourceUI.isSample());
        idPolicy.setValue(dataSourceUI.getRecordIdPolicy());
        tags.setValue(loadTags(dataSourceUI));
        if(idPolicy.getValue().toString().equals(IdExtractedRecordIdPolicy.IDEXTRACTED)) {
            LabelField idXPath = new LabelField();
            idXPath.setId("idxpath");
            idXPath.setValue(dataSourceUI.getIdXPath());
            idXPath.setFieldLabel(HarvesterUI.CONSTANTS.identifierXpath());
            infoSetFolder.add(idXPath,formData);
            infoSetFolder.insert(idXPath, infoSetFolder.getItems().indexOf(infoSetFolder.getItemByItemId("idpolicy")) + 1);

            for(int i=0; i< dataSourceUI.getNamespaceList().size() ; i+=2) {
                LabelField namespace = new LabelField();
                namespace.setFieldLabel(HarvesterUI.CONSTANTS.namespacePrefixUri());
                namespace.setId("namespaceField" + i);
                namespace.setValue(dataSourceUI.getNamespaceList().get(i) + " - " + dataSourceUI.getNamespaceList().get(i + 1));
                infoSetFolder.add(namespace,formData);
                infoSetFolder.insert(namespace, infoSetFolder.getItems().indexOf(infoSetFolder.getItemByItemId("idxpath")) + ((i / 2)+1));
            }
        }

        int transPositionCount = 0;
        for(final TransformationUI transformationUI : dataSourceUI.getMetadataTransformations()) {

//            HorizontalPanel panel = new HorizontalPanel();
////            panel.setStyleName("view-transformation-panel");
////            panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
////            LabelField transformation = new LabelField();
////            panel.setStyleName("view-transformation-panel ");
//            HTML label = new HTML(HarvesterUI.CONSTANTS.transformation());
////            label.setWidth("170px");
//            label.setStyleName("view-transformation-row-label");
//            panel.setCellWidth(label, "1%");
//            HTML value = new HTML(transformationUI.getDSStringFormat());
//            value.setStyleName("view-transformation-row-value");
//            panel.setCellWidth(value, "1%");
//            panel.add(label);
//            panel.add(value);
//            Image image = new Image();
////            image.setStyleName("view-transformation-row-icon");
//            image.addStyleName("hyperlink_icon");
//            image.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent event) {
//                    UtilManager.showTransformationResultLog(dataSourceUI.getDataSourceSet(),transformationUI.getIdentifier());
//                }
//            });
//            image.setUrl("resources/images/icons/task_status/warning.png");
//            panel.add(image);
            final LayoutContainer transformationResultContainer = new LayoutContainer();
            transformationResultContainer.setStyleName("transformation-result-row");
            HBoxLayout statusContainerLayout = new HBoxLayout();
            statusContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
            transformationResultContainer.setLayout(statusContainerLayout);
            LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.transformation());
            label.setWidth(173);
            label.addStyleName("defaultFormFieldLabel");
            transformationResultContainer.add(label, new HBoxLayoutData(new Margins(0, 2, 4, 0)));
            transformationResultContainer.add(new LabelToolItem(transformationUI.getDSStringFormat()), new HBoxLayoutData(new Margins(0, 5, 4, 3)));

            AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
                public void onFailure(Throwable caught) {
                    new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                }
                public void onSuccess(Boolean fileExists) {
                    if(!fileExists) {
                        return;
                    }
                    ImageButton imageButton = new ImageButton();
                    imageButton.setToolTip("Transformation Result Log");
                    imageButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                        public void handleEvent(ButtonEvent be) {
                            UtilManager.showTransformationResultLog(dataSourceUI.getDataSourceSet(), transformationUI.getIdentifier());
                        }
                    });
                    imageButton.setIcon(HarvesterUI.ICONS.log_icon());
                    transformationResultContainer.add(imageButton, new HBoxLayoutData(new Margins(0, 0, 4, 0)));
                    transformationResultContainer.layout();
                }
            };
            RepoxServiceAsync repoxService = (RepoxServiceAsync) Registry.get(HarvesterUI.REPOX_SERVICE);
            repoxService.transformationResultFileExists(dataSourceUI.getDataSourceSet(), transformationUI.getIdentifier(), callback);

            infoSetFolder.add(transformationResultContainer, formData);
            infoSetFolder.insert(transformationResultContainer, infoSetFolder.getItems().indexOf(infoSetFolder.getItemByItemId("recordsViewDSInfo")) + (transPositionCount+1));
            transPositionCount++;
        }

        DataSetViewInfo.setRecordsLabel(dataSourceUI,records);
        deletedRecords.setValue(dataSourceUI.getDeletedRecords());

        // Europeana Only
        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            name.setValue(dataSourceUI.getName());
            nameCode.setValue(dataSourceUI.getNameCode());
        }
    }

    private String loadTags(DataSourceUI dataSourceUI){
        String result = "";
        for(DataSetTagUI dataSetTagUI : dataSourceUI.getTags()){
            result += dataSetTagUI.getName() + "; ";
        }
        return result;
    }
}
