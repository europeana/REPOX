package harvesterUI.client.panels.dataSetButtons;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.panels.overviewGrid.contextMenus.DataSetContextMenu;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.WidgetWithRole;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 11:52
 */
public class ManageDataSetMenu extends WidgetWithRole{

    public ManageDataSetMenu(final TreeGrid<DataContainer> tree, List<Component> componentList) {
        if(drawWidget){
            // Management Menu

            Button management = new Button();
            management.setId("manageDSButton");
            management.setText(HarvesterUI.CONSTANTS.manageDataSet());
            management.setIcon(HarvesterUI.ICONS.management_icon());
            Menu manageMenu = new Menu();
            management.setMenu(manageMenu);

            MenuItem move = new MenuItem();
            move.setText(HarvesterUI.CONSTANTS.moveDataSet());
            move.setIcon(HarvesterUI.ICONS.arrow_move());
            move.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent me) {
                    List<DataSourceUI> dataSourceUIList =
                            DataSetContextMenu.getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems());
                    Dispatcher.get().dispatch(AppEvents.ViewMoveDataSetDialog, dataSourceUIList);
                }
            });

            MenuItem edit = new MenuItem();
            edit.setText(HarvesterUI.CONSTANTS.editDataSet());
            edit.setIcon(HarvesterUI.ICONS.operation_edit());
            edit.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    DataSourceUI selected = DataSetContextMenu.getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()).get(0);
                    Dispatcher.get().dispatch(AppEvents.ViewDataSourceForm, selected);
                }
            });

            final SelectionListener<ButtonEvent> removeDSListener = new SelectionListener<ButtonEvent> () {
                public void componentSelected(ButtonEvent ce) {
                    final LayoutContainer wrapper = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
                    final List<DataSourceUI> dataSourcesSelectedUI =
                            DataSetContextMenu.getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems());

                    AsyncCallback callback = new AsyncCallback() {
                        public void onFailure(Throwable caught) {
                            new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        }
                        public void onSuccess(Object result) {
                            wrapper.unmask();
                            HarvesterUI.SEARCH_UTIL_MANAGER.dataSetSearchedDeleted(dataSourcesSelectedUI);
                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteDataSets(), HarvesterUI.CONSTANTS.dataSetDeleted());
                            Dispatcher.get().dispatch(AppEvents.LoadMainData);
                        }
                    };
                    wrapper.mask(HarvesterUI.CONSTANTS.deleteDataSetMask());
                    ((DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE)).deleteDataSources(dataSourcesSelectedUI, callback);
                }
            };

            MenuItem remove = new MenuItem();
            remove.setText(HarvesterUI.CONSTANTS.removeDataSet());
            remove.setIcon(HarvesterUI.ICONS.delete());
            remove.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.removeDataSetMessage(), removeDSListener);
                }
            });

            manageMenu.add(edit);
            manageMenu.add(move);
            manageMenu.add(remove);

            componentList.add(management);
        }
    }

    public void checkRole(){
        switch (HarvesterUI.UTIL_MANAGER.getLoggedUserRole()){
            case ADMIN : drawWidget = true;
                break;
            case NORMAL: drawWidget = true;
                break;
            case DATA_PROVIDER: drawWidget = true;
                break;
            default: drawWidget = false;
                break;
        }
    }
}
