package harvesterUI.client.panels.mdr;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 24-11-2011
 * Time: 17:41
 */
public class SchemasOperations {

    protected Button removeButton, editButton;
    protected SeparatorToolItem lastSeparator;

    public SchemasOperations(final Grid<SchemaTreeUI> grid) {

        final SelectionListener<ButtonEvent> removeSchemaListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent ce) {
                AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(ResponseState result) {
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteMetadataSchema(), HarvesterUI.CONSTANTS.deleteMetadataSchemaSuccess());
                        Dispatcher.forwardEvent(AppEvents.ReloadSchemas);
                    }
                };

                // Safe case so that you cant remove all schemas!
                if((grid.getStore().getModels().size() - grid.getSelectionModel().getSelectedItems().size()) < 2){
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.deleteMetadataSchema(), "Sorry, at least one schema must be available");
                    return;
                }

                List<String> schemaIds = new ArrayList<String>();
                for(SchemaTreeUI schemaUI : grid.getSelectionModel().getSelectedItems()){
                    schemaIds.add(schemaUI.getSchema());
                }
                TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
                service.deleteMetadataSchema(schemaIds, callback);
            }
        };

//        addVersionButton = new Button();
//        addVersionButton.setText("Add Version");
//        addVersionButton.setIcon(HarvesterUI.ICONS.schema_version_new());
//        addVersionButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//            public void componentSelected(ButtonEvent ce) {
//                // todo: add version dialog
//            }
//        });

        removeButton = new Button();
        removeButton.setText(HarvesterUI.CONSTANTS.delete());
        removeButton.setIcon(HarvesterUI.ICONS.schema_delete());
        removeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                //Check if the schema is being used in a DataSet
                for(SchemaTreeUI schema : grid.getSelectionModel().getSelectedItems()) {
                    if(schema instanceof SchemaUI) {
                        if(((SchemaUI) schema).getTotalTimesUsed(true) != 0) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.error(),"Schema(s) currently used in Data Sets can't be removed."); //todo multi lang
                            return;
                        }
                        if(((SchemaUI) schema).getTotalTimesUsed(false) != 0) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.error(),"Schema(s) currently used in Transformations can't be removed."); //todo multi lang
                            return;
                        }
                    }
                }
                HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteMetadataSchemaQuestion(), removeSchemaListener);
            }
        });

        editButton = new Button();
        editButton.setText("Edit");
        editButton.setIcon(HarvesterUI.ICONS.schema_edit());
        editButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                SchemaTreeUI schemaTreeUI = grid.getSelectionModel().getSelectedItem();
                if(schemaTreeUI instanceof SchemaVersionUI){
                    SchemaVersionUI schemaVersionUI = (SchemaVersionUI) schemaTreeUI;
                    Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog,schemaVersionUI.getParent());
                }else
                    Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog,grid.getSelectionModel().getSelectedItem());
            }
        });

        lastSeparator = new SeparatorToolItem();
    }

    public void hideSchemasButtons(ToolBar toolBar) {
        if(toolBar.getItems().contains(removeButton))
            removeButton.removeFromParent();
        if(toolBar.getItems().contains(editButton))
            editButton.removeFromParent();
        if(toolBar.getItems().contains(lastSeparator))
            lastSeparator.removeFromParent();
        //            addVersionButton.removeFromParent();
    }

    public void hideDeleteButton(ToolBar toolBar) {
        if(toolBar.getItems().contains(removeButton))
            removeButton.removeFromParent();
    }

    public void hideEditButton(ToolBar toolbar) {
        if(toolbar.getItems().contains(editButton))
            editButton.removeFromParent();
    }

    public void showSchemasButtons(ToolBar toolBar, boolean isSchemaUI) {
        if(isSchemaUI) {
            if(!toolBar.getItems().contains(editButton)) {
                toolBar.insert(lastSeparator,1);
                toolBar.insert(editButton,2);
            }
            if(!toolBar.getItems().contains(removeButton))
                toolBar.insert(removeButton,3);
        } else {
            if(!toolBar.getItems().contains(editButton)) {
                toolBar.insert(lastSeparator,1);
                toolBar.insert(editButton,2);
            }
            if(toolBar.getItems().contains(removeButton))
                hideDeleteButton(toolBar);
        }
    }
}
