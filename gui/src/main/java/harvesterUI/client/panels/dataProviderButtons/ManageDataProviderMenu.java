package harvesterUI.client.panels.dataProviderButtons;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.panels.overviewGrid.contextMenus.DataProviderContextMenu;
import harvesterUI.client.servlets.dataManagement.DPServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.WidgetWithRole;
import harvesterUI.shared.ProjectType;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
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

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 11:52
 */
public class ManageDataProviderMenu extends WidgetWithRole{

    public ManageDataProviderMenu(final TreeGrid<DataContainer> tree, List<Component> componentList) {
        if(drawWidget){
            Button management = new Button();
            management.setId("manageDPButton");
            management.setText(HarvesterUI.CONSTANTS.manageDataProvider());
            management.setIcon(HarvesterUI.ICONS.management_icon());
            Menu manageMenu = new Menu();
            management.setMenu(manageMenu);
            componentList.add(management);

            MenuItem edit = new MenuItem();
            edit.setText(HarvesterUI.CONSTANTS.editDataProvider());
            edit.setIcon(HarvesterUI.ICONS.form());
            edit.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    BaseTreeModel selected = (BaseTreeModel) tree.getSelectionModel().getSelectedItems().get(0);
                    Dispatcher.get().dispatch(AppEvents.ViewDataProviderForm, selected);
                }
            });

            final SelectionListener<ButtonEvent> removeDPListener = new SelectionListener<ButtonEvent> () {
                public void componentSelected(ButtonEvent ce) {
                    final List<DataProviderUI> dataProvidersSelectedUI =
                            DataProviderContextMenu.getOnlyDataProviders(tree.getSelectionModel().getSelectedItems());
                    final LayoutContainer wrapper = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);

                    AsyncCallback<String> callback = new AsyncCallback<String>() {
                        public void onFailure(Throwable caught) {
                            new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        }
                        public void onSuccess(String result) {
                            if(result.equals("OTHER")){
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.deleteDataProviders(), HarvesterUI.CONSTANTS.deleteDataProvidersError());
                                return;
                            } else if(result.equals("NOT_FOUND")){
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.deleteDataProviders(), HarvesterUI.CONSTANTS.deleteDataProvidersError());
                                return;
                            }
                            wrapper.unmask();
                            HarvesterUI.SEARCH_UTIL_MANAGER.dataProviderSearchedDeleted(dataProvidersSelectedUI);
                            Dispatcher.get().dispatch(AppEvents.LoadMainData);
                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteDataProviders(), HarvesterUI.CONSTANTS.dataProvidersDeleted());
                        }
                    };
                    wrapper.mask(HarvesterUI.CONSTANTS.deletingDataProvidersMask());
                    ((DPServiceAsync) Registry.get(HarvesterUI.DP_SERVICE)).deleteDataProviders(dataProvidersSelectedUI, callback);
                }
            };

            MenuItem remove = new MenuItem();
            remove.setText(HarvesterUI.CONSTANTS.removeDataProvider());
            remove.setIcon(HarvesterUI.ICONS.delete());
            remove.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteDataProvidersMessage(), removeDPListener);
                }
            });

            manageMenu.add(edit);
            manageMenu.add(remove);

            if(HarvesterUI.getProjectType() == ProjectType.DEFAULT) {
                MenuItem move = new MenuItem();
                move.setText(HarvesterUI.CONSTANTS.moveDataProvider());
                move.setIcon(HarvesterUI.ICONS.arrow_move());
                move.addSelectionListener(new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent me) {
                        List<DataContainer> selected = tree.getSelectionModel().getSelectedItems();
                        final List<DataProviderUI> dataProvidersSelected = new ArrayList<DataProviderUI>();
                        for (DataContainer sel : selected) {
                            if(sel instanceof DataProviderUI)
                                dataProvidersSelected.add((DataProviderUI)sel);
                        }
                        Dispatcher.get().dispatch(AppEvents.ViewMoveDataProviderDialog,dataProvidersSelected);
                    }
                });
                manageMenu.insert(move,1);
            }
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
