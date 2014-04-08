package harvesterUI.client.panels.overviewGrid.contextMenus;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.panels.dataProviderButtons.CreateDataSetButton;
import harvesterUI.client.servlets.dataManagement.DPServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.ProjectType;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 26-04-2011
 * Time: 17:05
 */
public class DataProviderContextMenu extends Menu {

    private DPServiceAsync service;
    private TreeGrid<DataContainer> tree;
    private boolean drawWidget;

    public DataProviderContextMenu(TreeGrid<DataContainer> mainTree) {
        service = (DPServiceAsync) Registry.get(HarvesterUI.DP_SERVICE);
        tree = mainTree;
        checkRole();

        if(drawWidget){
            MenuItem edit = new MenuItem();
            edit.setText(HarvesterUI.CONSTANTS.editDataProvider());
            edit.setIcon(HarvesterUI.ICONS.operation_edit());
            edit.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    BaseTreeModel selected = (BaseTreeModel) tree.getSelectionModel().getSelectedItems().get(0);
                    Dispatcher.get().dispatch(AppEvents.ViewDataProviderForm, selected);
                }
            });

            final SelectionListener<ButtonEvent> removeDPListener = new SelectionListener<ButtonEvent> () {
                public void componentSelected(ButtonEvent ce) {
                    final List<DataProviderUI> dataProvidersSelectedUI =
                            getOnlyDataProviders(tree.getSelectionModel().getSelectedItems());
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
                    service.deleteDataProviders(dataProvidersSelectedUI, callback);
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

            add(edit);

            if(HarvesterUI.getProjectType() == ProjectType.EUROPEANA) {
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
                add(move);
            }
            add(remove);
        }
        new CreateDataSetButton(this,tree);

        add(new SeparatorMenuItem());
        add(createExpandAllItem());
    }

    private MenuItem createExpandAllItem() {
        MenuItem expandAll = new MenuItem();
        expandAll.setText(HarvesterUI.CONSTANTS.collapseAll());
        expandAll.setIcon(HarvesterUI.ICONS.table());
        expandAll.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                tree.mask(HarvesterUI.CONSTANTS.loadingMainData());

                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        tree.collapseAll();
                    }
                });
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        tree.unmask();
                    }
                });
            }
        });
        return expandAll;
    }

    public static List<DataProviderUI> getOnlyDataProviders(List<DataContainer> models) {
        List<DataProviderUI> dataProviderUIs = new ArrayList<DataProviderUI>();
        for (DataContainer sel : models) {
            if(sel instanceof DataProviderUI){
                dataProviderUIs.add((DataProviderUI)sel);
            }
        }
        return dataProviderUIs;
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
