package harvesterUI.client.panels.overviewGrid.contextMenus;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.dataManagement.AGGServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 26-04-2011
 * Time: 16:55
 */
public class AggregatorContextMenu extends Menu {

    private AGGServiceAsync service;
    private TreeGrid<DataContainer> tree;
    private boolean drawWidget;

    public AggregatorContextMenu(TreeGrid<DataContainer> mainTree) {
        service = (AGGServiceAsync) Registry.get(HarvesterUI.AGG_SERVICE);
        tree = mainTree;
        checkRole();

        if(drawWidget){
            MenuItem createDP = new MenuItem();
            createDP.setText(HarvesterUI.CONSTANTS.createDataProvider());
            createDP.setIcon(HarvesterUI.ICONS.add());
            createDP.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    AggregatorUI selected = (AggregatorUI)tree.getSelectionModel().getSelectedItems().get(0);
                    Dispatcher.get().dispatch(AppEvents.ViewDataProviderForm,selected);
                }
            });

            MenuItem edit = new MenuItem();
            edit.setText(HarvesterUI.CONSTANTS.editAggregator());
            edit.setIcon(HarvesterUI.ICONS.operation_edit());
            edit.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    AggregatorUI selected = (AggregatorUI) tree.getSelectionModel().getSelectedItems().get(0);
                    Dispatcher.get().dispatch(AppEvents.ViewAggregatorForm, selected);
                }
            });

            final SelectionListener<ButtonEvent> aggreRemoveListener = new SelectionListener<ButtonEvent> () {
                public void componentSelected(ButtonEvent ce) {
                    UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.deleteAggregatorsMask());
                    final List<AggregatorUI> aggregatorsSelected = new ArrayList<AggregatorUI>();
                    List<DataContainer> selected = tree.getSelectionModel().getSelectedItems();
                    for (DataContainer sel : selected){
                        if(sel instanceof AggregatorUI)
                            aggregatorsSelected.add((AggregatorUI)sel);
                    }

                    AsyncCallback callback = new AsyncCallback() {
                        public void onFailure(Throwable caught) {
                            new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                            UtilManager.unmaskCentralPanel();
                        }
                        public void onSuccess(Object result) {
                            HarvesterUI.SEARCH_UTIL_MANAGER.aggregatorSearchedDeleted(aggregatorsSelected);
                            Dispatcher.forwardEvent(AppEvents.LoadMainData);
                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteAggregators(), HarvesterUI.CONSTANTS.aggregatorsDeleted());
                            UtilManager.unmaskCentralPanel();
                        }
                    };
                    service.deleteAggregators(aggregatorsSelected, callback);
                }
            };

            MenuItem remove = new MenuItem();
            remove.setText(HarvesterUI.CONSTANTS.removeAggregator());
            remove.setIcon(HarvesterUI.ICONS.delete());
            remove.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.aggregatorDeleteMessage(), aggreRemoveListener);
                }
            });

            add(edit);
            add(remove);
            add(new SeparatorMenuItem());
            add(createDP);
//        add(importDP);
            add(new SeparatorMenuItem());
        }
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
