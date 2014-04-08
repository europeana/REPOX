package harvesterUI.client.panels.mdr;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
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
 * Created to project REPOX.
 * User: Edmundo
 * Date: 15/06/12
 * Time: 11:53
 */
public class SchemaContextMenu extends Menu {

    public SchemaContextMenu(final Grid<SchemaTreeUI> grid) {

        MenuItem edit = new MenuItem();
        edit.setText(HarvesterUI.CONSTANTS.edit());
        edit.setIcon(HarvesterUI.ICONS.schema_edit());
        edit.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent me) {
                SchemaTreeUI schemaTreeUI = grid.getSelectionModel().getSelectedItem();
                if(schemaTreeUI instanceof SchemaVersionUI){
                    SchemaVersionUI schemaVersionUI = (SchemaVersionUI) schemaTreeUI;
                    Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog,schemaVersionUI.getParent());
                }else
                    Dispatcher.forwardEvent(AppEvents.ViewAddSchemaDialog,grid.getSelectionModel().getSelectedItem());
            }
        });
        add(edit);

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
                List<String> schemaIds = new ArrayList<String>();
                for(SchemaTreeUI schemaUI : grid.getSelectionModel().getSelectedItems()){
                    schemaIds.add(schemaUI.getSchema());
                }
                TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
                service.deleteMetadataSchema(schemaIds, callback);
            }
        };

        MenuItem delete = new MenuItem();
        delete.setText(HarvesterUI.CONSTANTS.delete());
        delete.setIcon(HarvesterUI.ICONS.schema_delete());
        delete.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent me) {
                HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteMetadataSchemaQuestion(), removeSchemaListener);
            }
        });
        add(delete);
    }
}
