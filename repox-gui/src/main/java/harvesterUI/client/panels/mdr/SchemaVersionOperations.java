//package harvesterUI.client.panels.mdr;
//
//import com.extjs.gxt.ui.client.Registry;
//import com.extjs.gxt.ui.client.event.ButtonEvent;
//import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.mvc.Dispatcher;
//import com.extjs.gxt.ui.client.widget.button.Button;
//import com.extjs.gxt.ui.client.widget.grid.Grid;
//import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import harvesterUI.client.HarvesterUI;
//import harvesterUI.client.core.AppEvents;
//import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
//import harvesterUI.client.util.ServerExceptionDialog;
//import harvesterUI.shared.mdr.SchemaTreeUI;
//import harvesterUI.shared.servletResponseStates.ResponseState;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created to REPOX.
// * User: Edmundo
// * Date: 24-11-2011
// * Time: 17:41
// */
//public class SchemaVersionOperations {
//
//    protected Button removeButton, editButton;
//    protected SeparatorToolItem lastSeparator;
//
//    public SchemaVersionOperations(final Grid<SchemaTreeUI> grid) {
//
//        final SelectionListener<ButtonEvent> removeTransListener = new SelectionListener<ButtonEvent> () {
//            public void componentSelected(ButtonEvent ce) {
//                AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
//                    public void onFailure(Throwable caught) {
//                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
//                    }
//                    public void onSuccess(ResponseState result) {
//                        HarvesterUI.UTIL_MANAGER.getSaveBox("Delete Schema Version", "Schema Version deleted sucessfully");
//                        Dispatcher.forwardEvent(AppEvents.ReloadSchemas);
//                    }
//                };
//                List<String> schemaIds = new ArrayList<String>();
//                for(SchemaTreeUI schemaUI : grid.getSelectionModel().getSelectedItems()){
//                    schemaIds.add(schemaUI.getDestSchema());
//                }
//                TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
//                service.deleteMetadataSchema(schemaIds, callback);
//            }
//        };
//
//        removeButton = new Button();
//        removeButton.setText(HarvesterUI.CONSTANTS.delete());
//        removeButton.setIcon(HarvesterUI.ICONS.delete());
//        removeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//            public void componentSelected(ButtonEvent ce) {
//                HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), "Are you sure you want to delete this version?", removeTransListener);
//            }
//        });
//
//        editButton = new Button();
//        editButton.setText("Edit");
//        editButton.setIcon(HarvesterUI.ICONS.operation_edit());
//        editButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//            public void componentSelected(ButtonEvent ce) {
//                // todo : edit version dialog
////                Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog,grid.getSelectionModel().getSelectedItem());
//            }
//        });
//
//        lastSeparator = new SeparatorToolItem();
//    }
//
//    public void hideSchemasButtons(ToolBar toolBar) {
//        if(toolBar.getItems().contains(removeButton)) {
//            removeButton.removeFromParent();
//            editButton.removeFromParent();
//            lastSeparator.removeFromParent();
//        }
//    }
//
//    public void showSchemasButtons(ToolBar toolBar) {
//        if(!toolBar.getItems().contains(removeButton)) {
//            toolBar.insert(lastSeparator,1);
//            toolBar.insert(editButton,2);
//            toolBar.insert(removeButton,3);
//        }
//    }
//}
