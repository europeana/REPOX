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
public class SchemaVersionContextMenu extends Menu {

    public SchemaVersionContextMenu(final Grid<SchemaTreeUI> grid) {

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
    }
}
