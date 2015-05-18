package harvesterUI.client.panels.forms.dataSources;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 15-03-2011
 * Time: 12:46
 */
public class DataSourceTabPanel extends FormDialog {

    private DataSourceFolderForm dataSourceFolderForm;
    private DataSourceOAIForm dataSourceOAIForm;
    private DataSourceZ39Form dataSourceZ39Form;
    private DataSourceYaddaForm dataSourceYaddaForm;
    private DataSourceSruForm dataSourceSruForm;
    private TabPanel tabPanel;
    private TabItem oaiTab,folderTab,z39Tab, yaddaTab, sruTab;

    private boolean onEditMode;
    private DataSourceUI currentDS;

    public DataSourceTabPanel() {
        super(0.9,0.6);
//        setHeading("Create Data Set");
        setIcon(HarvesterUI.ICONS.add());
//        setMaximizable(true);

        FormData formData = new FormData("98%");
        dataSourceFolderForm = new DataSourceFolderForm(formData);
        dataSourceOAIForm = new DataSourceOAIForm(formData);
        dataSourceZ39Form = new DataSourceZ39Form(formData);
        dataSourceSruForm = new DataSourceSruForm(formData);

        if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
            dataSourceOAIForm.addEuropeanaFields();
            dataSourceFolderForm.addEuropeanaFields();
            dataSourceZ39Form.addEuropeanaFields();
            dataSourceSruForm.addEuropeanaFields();
        }

        tabPanel = new TabPanel();

        oaiTab = new TabItem("OAI-PMH");
        oaiTab.add(dataSourceOAIForm);
        oaiTab.setIcon(HarvesterUI.ICONS.oai_icon());
        oaiTab.setLayout(new FitLayout());
        tabPanel.add(oaiTab);

        folderTab = new TabItem("Folder");
        folderTab.add(dataSourceFolderForm);
        folderTab.setIcon(HarvesterUI.ICONS.album());
        folderTab.setLayout(new FitLayout());
        tabPanel.add(folderTab);

        z39Tab = new TabItem("Z39.50");
        z39Tab.add(dataSourceZ39Form);
        z39Tab.setIcon(HarvesterUI.ICONS.album());
        z39Tab.setLayout(new FitLayout());
        tabPanel.add(z39Tab);

        sruTab = new TabItem("SruUpdate");
        sruTab.add(dataSourceSruForm);
        sruTab.setIcon(HarvesterUI.ICONS.album());
        sruTab.setLayout(new FitLayout());
        tabPanel.add(sruTab);

        tabPanel.addListener(Events.Select, new Listener<TabPanelEvent>() {
            public void handleEvent(TabPanelEvent be) {
                TabItem tabItem = be.getItem();
                Component component = tabItem.getItem(0);
                if(component instanceof  DataSourceForm){
                    if(onEditMode && component.isVisible()){
                        HarvesterUI.UTIL_MANAGER.askForIncrementalUpdateDate(currentDS);
                        ((DataSourceForm) component).setEditMode(currentDS);
                    }
                    ((DataSourceForm) component).resetLayout();
                }
            }
        });

        add(tabPanel);
    }

    public void setEditMode(DataSourceUI dataSourceUI){
        onEditMode = true;
        currentDS = dataSourceUI;
        setHeading(HarvesterUI.CONSTANTS.editDataSet()+": " + dataSourceUI.getDataSourceSet());
        setIcon(HarvesterUI.ICONS.operation_edit());
        String type = dataSourceUI.getIngest();
//        String delimType = "[ ]+";
//        String[] tokensType = type.split(delimType);
//        String parsedType = tokensType[0];


        if(type.contains("OAI-PMH")){
            tabPanel.setSelection(oaiTab);
            dataSourceOAIForm.setResetOutputSet(dataSourceUI.getDataSetParent());
            dataSourceOAIForm.setEditMode(dataSourceUI);
        } else if(type.contains("Folder")){
            tabPanel.setSelection(folderTab);
            dataSourceFolderForm.setResetOutputSet(dataSourceUI.getDataSetParent());
            dataSourceFolderForm.setEditMode(dataSourceUI);
        } else if(type.contains("Z3950")){
            tabPanel.setSelection(z39Tab);
            dataSourceZ39Form.setResetOutputSet(dataSourceUI.getDataSetParent());
            dataSourceZ39Form.setEditMode(dataSourceUI);
        } else if(type.contains("SRU")){
            tabPanel.setSelection(sruTab);
            dataSourceSruForm.setResetOutputSet(dataSourceUI.getDataSetParent());
            dataSourceSruForm.setEditMode(dataSourceUI);
        }
    }

    public void reloadTransformations(){
        dataSourceOAIForm.reloadTransformations();
        dataSourceFolderForm.reloadTransformations();
        dataSourceZ39Form.reloadTransformations();
        dataSourceSruForm.reloadTransformations();
    }

    public void reloadSchemas(String schemaShortDesignation){
        dataSourceOAIForm.reloadSchemas(schemaShortDesignation);
        dataSourceFolderForm.reloadSchemas(schemaShortDesignation);
        dataSourceZ39Form.reloadSchemas(schemaShortDesignation);
        dataSourceSruForm.reloadSchemas(schemaShortDesignation);
    }

    public void reloadTags(){
        dataSourceOAIForm.reloadTags();
        dataSourceFolderForm.reloadTags();
        dataSourceZ39Form.reloadTags();
        dataSourceSruForm.reloadTags();
    }

    public void resetValues(DataProviderUI parent){
        onEditMode = false;
        currentDS = null;
        setHeading(HarvesterUI.CONSTANTS.newDSTitle()+": " + parent.getName());
        setIcon(HarvesterUI.ICONS.add());
        dataSourceOAIForm.resetValues(parent);
        dataSourceFolderForm.resetValues(parent);
        dataSourceZ39Form.resetValues(parent);
        dataSourceSruForm.resetValues(parent);

        tabPanel.setSelection(oaiTab);
    }
}
