package harvesterUI.client.panels.overviewGrid.contextMenus;

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
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.models.SingleExternalServiceStartData;
import harvesterUI.client.panels.harvesting.dialogs.EditLastIngestDateDialog;
import harvesterUI.client.panels.harvesting.dialogs.ExportNowDialog;
import harvesterUI.client.panels.harvesting.dialogs.ScheduleExportDialog;
import harvesterUI.client.panels.harvesting.dialogs.ScheduleTaskDialog;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.externalServices.ExternalServiceUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 26-04-2011
 * Time: 17:09
 */
public class DataSetContextMenu extends Menu {

    private TreeGrid<DataContainer> tree;
    private boolean drawWidget;

    public DataSetContextMenu(TreeGrid<DataContainer> mainTree) {
        tree = mainTree;
        checkRole();

        final SelectionListener<ButtonEvent> emptyDataSetListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.EmptyDataSet,
                        getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
            }
        };

        MenuItem empty = new MenuItem();
        empty.setText(HarvesterUI.CONSTANTS.emptyDataSet());
        empty.setIcon(HarvesterUI.ICONS.broom_icon());
        empty.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent me) {
                HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.emptyDataSetMessage(), emptyDataSetListener);
            }
        });

        MenuItem ingestNow = new MenuItem();
        ingestNow.setText(HarvesterUI.CONSTANTS.ingestNow());
        ingestNow.setIcon(HarvesterUI.ICONS.ingest_now_icon());
        ingestNow.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent me) {
                Dispatcher.forwardEvent(AppEvents.IngestDataSet,
                        getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
            }
        });

        MenuItem ingestSample = new MenuItem();
        ingestSample.setText(HarvesterUI.CONSTANTS.ingestSample());
        ingestSample.setIcon(HarvesterUI.ICONS.ingest_sample_icon());
        ingestSample.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent me) {
                Dispatcher.forwardEvent(AppEvents.IngestDataSetSample,
                        getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
            }
        });

        MenuItem editLastIngestDate = new MenuItem();
        editLastIngestDate.setText("Edit Last Ingest Date");
        editLastIngestDate.setIcon(HarvesterUI.ICONS.form());
        editLastIngestDate.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent me) {
                new EditLastIngestDateDialog((DataSourceUI)tree.getSelectionModel().getSelectedItem()).showAndCenter();
            }
        });

        MenuItem scheduleIngest = new MenuItem();
        scheduleIngest.setText(HarvesterUI.CONSTANTS.scheduleIngest());
        scheduleIngest.setIcon(HarvesterUI.ICONS.calendar());
        scheduleIngest.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                DataSourceUI selectedDS = (DataSourceUI) tree.getSelectionModel().getSelectedItems().get(0);
                ScheduleTaskDialog scheduleTaskDialog = new ScheduleTaskDialog(selectedDS.getDataSourceSet());
            }
        });

        MenuItem viewInfo = new MenuItem();
        viewInfo.setText(HarvesterUI.CONSTANTS.viewInfo());
        viewInfo.setIcon(HarvesterUI.ICONS.view_info_icon());
        viewInfo.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                DataSourceUI selected = (DataSourceUI) tree.getSelectionModel().getSelectedItems().get(0);
                Dispatcher.get().dispatch(AppEvents.ViewDataSetInfo,selected);
            }
        });

        MenuItem scheduleExport = new MenuItem();
        scheduleExport.setText(HarvesterUI.CONSTANTS.scheduleExport());
        scheduleExport.setIcon(HarvesterUI.ICONS.schedule_export_icon());
        scheduleExport.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                DataSourceUI selectedDS = (DataSourceUI) tree.getSelectionModel().getSelectedItems().get(0);
                ScheduleExportDialog scheduleExportDialog = new ScheduleExportDialog(selectedDS.getDataSourceSet());
                scheduleExportDialog.setModal(true);
                scheduleExportDialog.resetValues();
                scheduleExportDialog.show();
                scheduleExportDialog.center();
            }
        });

        MenuItem exportNow = new MenuItem();
        exportNow.setText(HarvesterUI.CONSTANTS.exportNow());
        exportNow.setIcon(HarvesterUI.ICONS.export_now_icon());
        exportNow.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                DataSourceUI selectedDS = (DataSourceUI) tree.getSelectionModel().getSelectedItems().get(0);
                ExportNowDialog exportNowDialog = new ExportNowDialog(selectedDS);
                exportNowDialog.setModal(true);
                exportNowDialog.show();
                exportNowDialog.center();
            }
        });

        MenuItem forceRecordUpdate = new MenuItem();
        forceRecordUpdate.setText("Force Record Update");
        forceRecordUpdate.setIcon(HarvesterUI.ICONS.side_list());
        forceRecordUpdate.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ForceRecordUpdate,
                        getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
            }
        });

        MenuItem removeOldTasksAndLogs = new MenuItem();
        removeOldTasksAndLogs.setText("Clear Logs");
        removeOldTasksAndLogs.setIcon(HarvesterUI.ICONS.log_icon());
        removeOldTasksAndLogs.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                Dispatcher.forwardEvent(AppEvents.RemoveLogs,
                        getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
            }
        });

        add(empty);
        add(ingestNow);
        add(ingestSample);
        add(editLastIngestDate);
        createRestServicesMenu(mainTree);
        add(viewInfo);
        add(scheduleIngest);
        add(exportNow);
        add(scheduleExport);
        add(forceRecordUpdate);
        add(removeOldTasksAndLogs);

        if(drawWidget){
            MenuItem move = new MenuItem();
            move.setText(HarvesterUI.CONSTANTS.moveDataSet());
            move.setIcon(HarvesterUI.ICONS.arrow_move());
            move.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent me) {
                    List<DataContainer> selected = tree.getSelectionModel().getSelectedItems();
                    final List<DataSourceUI> dataSourceUIList = new ArrayList<DataSourceUI>();
                    for (DataContainer sel : selected) {
                        if(sel instanceof DataSourceUI)
                            dataSourceUIList.add((DataSourceUI)sel);
                    }
                    Dispatcher.get().dispatch(AppEvents.ViewMoveDataSetDialog,dataSourceUIList);
                }
            });

            MenuItem edit = new MenuItem();
            edit.setText(HarvesterUI.CONSTANTS.editDataSet());
            edit.setIcon(HarvesterUI.ICONS.operation_edit());
            edit.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    DataSourceUI selected = (DataSourceUI) tree.getSelectionModel().getSelectedItems().get(0);
                    Dispatcher.get().dispatch(AppEvents.ViewDataSourceForm, selected);
                }
            });

            final SelectionListener<ButtonEvent> removeDSListener = new SelectionListener<ButtonEvent> () {
                public void componentSelected(ButtonEvent ce) {
                    Dispatcher.forwardEvent(AppEvents.RemoveDataSet,
                            getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
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
            add(move);
            add(edit);
            add(remove);
        }
        add(new SeparatorMenuItem());
        add(createExpandAllItem());
    }

    private void createRestServicesMenu(TreeGrid<DataContainer> mainTree){
        DataSourceUI dataSourceUI = (DataSourceUI)mainTree.getSelectionModel().getSelectedItem();
        if(dataSourceUI.getRestServiceUIList().size() > 0){
            Menu externalServicesMenu = new Menu();
            for(ExternalServiceUI externalServiceUI : dataSourceUI.getRestServiceUIList()){
                final SingleExternalServiceStartData singleExternalServiceStartData = new SingleExternalServiceStartData(externalServiceUI.getId(),dataSourceUI.getId());
                MenuItem restService1 = new MenuItem();
                restService1.setText(externalServiceUI.getName());
                restService1.setIcon(HarvesterUI.ICONS.externalServicesIcon());
                restService1.addSelectionListener(new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent me) {
                        Dispatcher.forwardEvent(AppEvents.StartExternalService,singleExternalServiceStartData);
                    }
                });
                externalServicesMenu.add(restService1);
            }

            MenuItem externalMenu = new MenuItem();
            externalMenu.setText("Run External Service");
            externalMenu.setIcon(HarvesterUI.ICONS.externalServicesIcon());
            externalMenu.setSubMenu(externalServicesMenu);
            add(externalMenu);
        }
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

    public static List<DataSourceUI> getOnlyDataSourceUIs(List<DataContainer> models) {
        List<DataSourceUI> dataSourceUIs = new ArrayList<DataSourceUI>();
        for (DataContainer sel : models) {
            if(sel instanceof AggregatorUI){
                // Do nothing
            }else if(sel instanceof DataProviderUI && ((DataProviderUI) sel).getDataSourceUIList().size() == 1)
                dataSourceUIs.add(((DataProviderUI)sel).getDataSourceUIList().get(0));
            else if(sel instanceof DataSourceUI)
                dataSourceUIs.add((DataSourceUI)sel);
        }
        return dataSourceUIs;
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
