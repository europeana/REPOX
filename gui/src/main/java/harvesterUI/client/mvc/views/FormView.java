/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.mvc.views;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.dataSourceView.ChooseDataSetDialog;
import harvesterUI.client.panels.forms.AggregatorForm;
import harvesterUI.client.panels.forms.dataProviders.DataProviderEuropeanaForm;
import harvesterUI.client.panels.forms.dataProviders.DataProviderForm;
import harvesterUI.client.panels.forms.dataProviders.DataProviderImportForm;
import harvesterUI.client.panels.forms.dataProviders.DataProviderLightForm;
import harvesterUI.client.panels.forms.dataSources.DataSourceForm;
import harvesterUI.client.panels.forms.dataSources.DataSourceTabPanel;
import harvesterUI.client.panels.mdr.forms.NewSchemaDialog;
import harvesterUI.client.panels.mdr.forms.NewTransformationDialog;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.TransformationUI;
import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.Window;


public class FormView extends View {

    private AggregatorForm aggregatorForm;
    private DataProviderForm dataProviderForm;
    private DataSourceTabPanel dataSourceTabPanel;
    private DataProviderImportForm dataProviderImportForm;
    private NewTransformationDialog newTransformationDialog;
    private NewSchemaDialog newSchemaDialog;

    private ChooseDataSetDialog chooseDataSetDialog;

    public FormView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.ViewAggregatorForm){
            // Edit mode
            AggregatorUI aggregatorUI = event.getData();
            if(aggregatorUI != null) {
                aggregatorForm.setEditMode(aggregatorUI);
            } else
                aggregatorForm.resetValues();

            aggregatorForm.showAndCenter();
        }else if (event.getType() == AppEvents.ViewDataProviderForm) {
            // Edit mode
            if(event.getData() instanceof DataProviderUI) {
                DataProviderUI folder = event.getData();
                dataProviderForm.setEditMode(folder);
            }
            else
                dataProviderForm.resetValues(event.getData());

            dataProviderForm.showAndCenter();
        }else if (event.getType() == AppEvents.ViewDataSourceForm){

            // Edit mode
            if(event.getData() instanceof DataSourceUI) {
                DataSourceUI dataSourceUI = event.getData();
                if(dataSourceUI.getStatus().startsWith("RUNNING") ||
                        dataSourceUI.getStatus().startsWith("PRE_PROCESSING") ||
                        dataSourceUI.getStatus().startsWith("POST_PROCESSING")){
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.editDataSet(),HarvesterUI.CONSTANTS.dataSetIngesting());
                    return;
                }else
                    dataSourceTabPanel.setEditMode(dataSourceUI);
            }
            else if(event.getData() instanceof DataProviderUI)
                dataSourceTabPanel.resetValues((DataProviderUI)event.getData());

            dataSourceTabPanel.showAndCenter();
        }else if (event.getType() == AppEvents.ViewDPImportForm){
            dataProviderImportForm.showAndCenter();
            dataProviderImportForm.resetFileUploadField();
        }else if (event.getType() == AppEvents.HideDataSourceForm){
            dataSourceTabPanel.hide();
        }else if (event.getType() == AppEvents.CompareDataSets){
            showDataSetComparison();
        }else if (event.getType() == AppEvents.ViewAddMappingDialog){
            newTransformationDialog.showAndCenter();
            if(event.getData() instanceof TransformationUI)
                newTransformationDialog.edit((TransformationUI)event.getData());
            else
                newTransformationDialog.resetValues();
        }else if (event.getType() == AppEvents.ViewAddSchemaDialog){
            newSchemaDialog.showAndCenter();
            if(event.getData() instanceof SchemaUI)
                newSchemaDialog.edit((SchemaUI) event.getData());
            else if(event.getData() instanceof String){
                newSchemaDialog.newDialogFromKnownPrefix((String)event.getData());
                newSchemaDialog.addField(true);
            }else{
                newSchemaDialog.resetValues();
                newSchemaDialog.addField(true);
            }
        }else if (event.getType() == AppEvents.ReloadTransformations){
            if(dataSourceTabPanel.isVisible())
                dataSourceTabPanel.reloadTransformations();
        }else if (event.getType() == AppEvents.ReloadTags){
            if(dataSourceTabPanel.isVisible())
                dataSourceTabPanel.reloadTags();
        }else if (event.getType() == AppEvents.ReloadSchemas){
            String schemaShortDesignation = event.getData();
            dataSourceTabPanel.reloadSchemas(schemaShortDesignation);
        }
    }

    @Override
    protected void initialize(){
        newTransformationDialog = new NewTransformationDialog();
        newSchemaDialog = new NewSchemaDialog();
        dataSourceTabPanel = new DataSourceTabPanel();

        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT){
            aggregatorForm = new AggregatorForm();
            dataProviderForm = new DataProviderEuropeanaForm();
        }else{
            aggregatorForm = null;
            dataProviderForm = new DataProviderLightForm();
        }

        dataProviderImportForm = new DataProviderImportForm();
    }

    private void showDataSetComparison() {
        dataSourceTabPanel.setPosition(0,0);
        dataSourceTabPanel.setHeight(Window.getClientHeight());
        dataSourceTabPanel.setWidth((int) (Window.getClientWidth() * 0.5) - 110);
//        dataSourceTabPanel.layout(true);
        dataSourceTabPanel.setModal(false);
        ((DataSourceForm)((TabPanel)dataSourceTabPanel.getItem(0)).getSelectedItem().getItem(0)).resetLayout();

        if(chooseDataSetDialog == null)
            chooseDataSetDialog = new ChooseDataSetDialog();
        chooseDataSetDialog.showAndCenter();
    }
}
