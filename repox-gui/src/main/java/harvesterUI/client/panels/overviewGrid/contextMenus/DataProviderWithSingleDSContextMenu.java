//package harvesterUI.client.panels.overviewGrid.contextMenus;
//
//import com.extjs.gxt.ui.client.Registry;
//import com.extjs.gxt.ui.client.data.BaseTreeModel;
//import com.extjs.gxt.ui.client.event.ButtonEvent;
//import com.extjs.gxt.ui.client.event.MenuEvent;
//import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.mvc.Dispatcher;
//import com.extjs.gxt.ui.client.widget.LayoutContainer;
//import com.extjs.gxt.ui.client.widget.menu.Menu;
//import com.extjs.gxt.ui.client.widget.menu.MenuItem;
//import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
//import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
//import com.google.gwt.user.client.Command;
//import com.google.gwt.user.client.DeferredCommand;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import harvesterUI.client.HarvesterUI;
//import harvesterUI.client.core.AppEvents;
//import harvesterUI.client.mvc.views.AppView;
//import harvesterUI.client.panels.harvesting.dialogs.ExportNowDialog;
//import harvesterUI.client.panels.harvesting.dialogs.ScheduleExportDialog;
//import harvesterUI.client.panels.harvesting.dialogs.ScheduleTaskDialog;
//import harvesterUI.client.servlets.dataManagement.DPServiceAsync;
//import harvesterUI.client.util.ServerExceptionDialog;
//import harvesterUI.shared.ProjectType;
//import harvesterUI.shared.dataTypes.DataContainer;
//import harvesterUI.shared.dataTypes.DataProviderUI;
//import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created to REPOX.
// * User: Edmundo
// * Date: 26-04-2011
// * Time: 17:13
// */
//public class DataProviderWithSingleDSContextMenu extends Menu {
//
//    public DPServiceAsync service;
//    private TreeGrid<DataContainer> tree;
//    private boolean drawWidget;
//
//    public DataProviderWithSingleDSContextMenu(TreeGrid<DataContainer> mainTree) {
//        service = (DPServiceAsync) Registry.get(HarvesterUI.DP_SERVICE);
//        tree = mainTree;
//        checkRole();
//
//        if(drawWidget){
//            final SelectionListener<ButtonEvent> removeDPListener = new SelectionListener<ButtonEvent> () {
//                public void componentSelected(ButtonEvent be) {
//                    final List<DataProviderUI> dataProvidersSelectedUI =
//                            DataProviderContextMenu.getOnlyDataProviders(tree.getSelectionModel().getSelectedItems());
//                    final LayoutContainer wrapper = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
//
//                    AsyncCallback callback = new AsyncCallback() {
//                        public void onFailure(Throwable caught) {
//                            new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
//                        }
//                        public void onSuccess(Object result) {
//                            wrapper.unmask();
//                            HarvesterUI.SEARCH_UTIL_MANAGER.dataProviderSearchedDeleted(dataProvidersSelectedUI);
//                            Dispatcher.get().dispatch(AppEvents.LoadMainData);
//                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteDataProviders(), HarvesterUI.CONSTANTS.dataProvidersDeleted());
//                        }
//                    };
//                    wrapper.mask(HarvesterUI.CONSTANTS.deletingDataProvidersMask());
//                    service.deleteDataProviders(dataProvidersSelectedUI, callback);
//                }
//            };
//
//
//
//            final SelectionListener<ButtonEvent> removeDSListener = new SelectionListener<ButtonEvent> () {
//                public void componentSelected(ButtonEvent ce) {
//                    Dispatcher.forwardEvent(AppEvents.RemoveDataSet,
//                            DataSetContextMenu.getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
//                }
//            };
//
//            MenuItem createDS = new MenuItem();
//            createDS.setText(HarvesterUI.CONSTANTS.createDataSet());
//            createDS.setIcon(HarvesterUI.ICONS.add());
//            createDS.addSelectionListener(new SelectionListener<MenuEvent>() {
//                public void componentSelected(MenuEvent ce) {
//                    BaseTreeModel selected = (BaseTreeModel)tree.getSelectionModel().getSelectedItems().get(0);
//                    Dispatcher.get().dispatch(AppEvents.ViewDataSourceForm,selected);
//                }
//            });
//
//            MenuItem edit = new MenuItem();
//            edit.setText(HarvesterUI.CONSTANTS.editDataProvider());
//            edit.setIcon(HarvesterUI.ICONS.operation_edit());
//            edit.addSelectionListener(new SelectionListener<MenuEvent>() {
//                public void componentSelected(MenuEvent ce) {
//                    BaseTreeModel selected = tree.getSelectionModel().getSelectedItems().get(0);
//                    Dispatcher.get().dispatch(AppEvents.ViewDataProviderForm, selected);
//                }
//            });
//
//            MenuItem remove = new MenuItem();
//            remove.setText(HarvesterUI.CONSTANTS.removeDataProvider());
//            remove.setIcon(HarvesterUI.ICONS.delete());
//            remove.addSelectionListener(new SelectionListener<MenuEvent>() {
//                public void componentSelected(MenuEvent ce) {
//                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteDataProvidersMessage(), removeDPListener);
//                }
//            });
//            add(edit);
//
//            if(HarvesterUI.getProjectType() == ProjectType.EUROPEANA) {
//                MenuItem move = new MenuItem();
//                move.setText(HarvesterUI.CONSTANTS.moveDataProvider());
//                move.setIcon(HarvesterUI.ICONS.arrow_move());
//                move.addSelectionListener(new SelectionListener<MenuEvent>() {
//                    @Override
//                    public void componentSelected(MenuEvent me) {
//                        List<DataContainer> selected = tree.getSelectionModel().getSelectedItems();
//                        final List<DataProviderUI> dataProvidersSelected = new ArrayList<DataProviderUI>();
//                        for (DataContainer sel : selected) {
//                            if(sel instanceof DataProviderUI)
//                                dataProvidersSelected.add((DataProviderUI)sel);
//                        }
//                        Dispatcher.get().dispatch(AppEvents.ViewMoveDataProviderDialog,dataProvidersSelected);
//                    }
//                });
//                add(move);
//            }
//
//            add(remove);
//            add(new SeparatorMenuItem());
//
//            MenuItem move = new MenuItem();
//            move.setText(HarvesterUI.CONSTANTS.moveDataSet());
//            move.setIcon(HarvesterUI.ICONS.arrow_move());
//            move.addSelectionListener(new SelectionListener<MenuEvent>() {
//                @Override
//                public void componentSelected(MenuEvent me) {
//                    List<DataContainer> selected = tree.getSelectionModel().getSelectedItems();
//                    final List<DataSourceUI> dataSourceUIList = new ArrayList<DataSourceUI>();
//                    for (DataContainer sel : selected) {
//                        if(sel instanceof DataProviderUI) {
//                            DataSourceUI dataSourceUI = ((DataProviderUI)sel).getDataSourceUIList().get(0);
//                            dataSourceUIList.add(dataSourceUI);
//                        }
//                    }
//                    Dispatcher.get().dispatch(AppEvents.ViewMoveDataSetDialog,dataSourceUIList);
//                }
//            });
//
//            MenuItem editDS = new MenuItem();
//            editDS.setText(HarvesterUI.CONSTANTS.editDataSet());
//            editDS.setIcon(HarvesterUI.ICONS.operation_edit());
//            editDS.addSelectionListener(new SelectionListener<MenuEvent>() {
//                public void componentSelected(MenuEvent ce) {
//                    DataProviderUI dataProviderUI = (DataProviderUI) tree.getSelectionModel().getSelectedItems().get(0);
//                    DataSourceUI dataSourceUI = dataProviderUI.getDataSourceUIList().get(0);
//                    Dispatcher.get().dispatch(AppEvents.ViewDataSourceForm, dataSourceUI);
//                }
//            });
//
//            MenuItem removeDS = new MenuItem();
//            removeDS.setText(HarvesterUI.CONSTANTS.removeDataSet());
//            removeDS.setIcon(HarvesterUI.ICONS.delete());
//            removeDS.addSelectionListener(new SelectionListener<MenuEvent>() {
//                public void componentSelected(MenuEvent ce) {
//                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.removeDataSetMessage(), removeDSListener);
//                }
//            });
//
//            add(createDS);
//            add(editDS);
//            add(move);
//            add(removeDS);
//            add(new SeparatorMenuItem());
//        }
//
//        final SelectionListener<ButtonEvent> emptyDataSetListener = new SelectionListener<ButtonEvent> () {
//            public void componentSelected(ButtonEvent be) {
//                Dispatcher.forwardEvent(AppEvents.EmptyDataSet,
//                        DataSetContextMenu.getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
//            }
//        };
//
//        MenuItem empty = new MenuItem();
//        empty.setText(HarvesterUI.CONSTANTS.emptyDataSet());
//        empty.setIcon(HarvesterUI.ICONS.broom_icon());
//        empty.addSelectionListener(new SelectionListener<MenuEvent>() {
//            @Override
//            public void componentSelected(MenuEvent me) {
//                HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.emptyDataSetMessage(), emptyDataSetListener);
//            }
//        });
//
//        MenuItem ingestNow = new MenuItem();
//        ingestNow.setText(HarvesterUI.CONSTANTS.ingestNow());
//        ingestNow.setIcon(HarvesterUI.ICONS.ingest_now_icon());
//        ingestNow.addSelectionListener(new SelectionListener<MenuEvent>() {
//            @Override
//            public void componentSelected(MenuEvent me) {
//                Dispatcher.forwardEvent(AppEvents.IngestDataSet,
//                        DataSetContextMenu.getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
//            }
//        });
//
//        MenuItem ingestSample = new MenuItem();
//        ingestSample.setText(HarvesterUI.CONSTANTS.ingestSample());
//        ingestSample.setIcon(HarvesterUI.ICONS.ingest_sample_icon());
//        ingestSample.addSelectionListener(new SelectionListener<MenuEvent>() {
//            @Override
//            public void componentSelected(MenuEvent me) {
//                Dispatcher.forwardEvent(AppEvents.IngestDataSetSample,
//                        DataSetContextMenu.getOnlyDataSourceUIs(tree.getSelectionModel().getSelectedItems()));
//            }
//        });
//
//        MenuItem scheduleIngest = new MenuItem();
//        scheduleIngest.setText(HarvesterUI.CONSTANTS.scheduleIngest());
//        scheduleIngest.setIcon(HarvesterUI.ICONS.calendar());
//        scheduleIngest.addSelectionListener(new SelectionListener<MenuEvent>() {
//            @Override
//            public void componentSelected(MenuEvent ce) {
//                DataProviderUI selectedDP = (DataProviderUI) tree.getSelectionModel().getSelectedItems().get(0);
//                ScheduleTaskDialog scheduleTaskDialog = new ScheduleTaskDialog(selectedDP.getDataSourceUIList().get(0).getDataSourceSet());
//            }
//        });
//
//        MenuItem scheduleExport = new MenuItem();
//        scheduleExport.setText(HarvesterUI.CONSTANTS.scheduleExport());
//        scheduleExport.setIcon(HarvesterUI.ICONS.schedule_export_icon());
//        scheduleExport.addSelectionListener(new SelectionListener<MenuEvent>() {
//            @Override
//            public void componentSelected(MenuEvent ce) {
//                DataProviderUI selectedDP = (DataProviderUI) tree.getSelectionModel().getSelectedItems().get(0);
//                ScheduleExportDialog scheduleExportDialog = new ScheduleExportDialog(selectedDP.getDataSourceUIList().get(0).getDataSourceSet());
//                scheduleExportDialog.setModal(true);
//                scheduleExportDialog.resetValues();
//                scheduleExportDialog.show();
//                scheduleExportDialog.center();
//            }
//        });
//
//        MenuItem exportNow = new MenuItem();
//        exportNow.setText(HarvesterUI.CONSTANTS.exportNow());
//        exportNow.setIcon(HarvesterUI.ICONS.export_now_icon());
//        exportNow.addSelectionListener(new SelectionListener<MenuEvent>() {
//            @Override
//            public void componentSelected(MenuEvent ce) {
//                DataProviderUI selectedDP = (DataProviderUI) tree.getSelectionModel().getSelectedItems().get(0);
//                ExportNowDialog exportNowDialog = new ExportNowDialog(selectedDP.getDataSourceUIList().get(0));
//                exportNowDialog.setModal(true);
//                exportNowDialog.show();
//                exportNowDialog.center();
//            }
//        });
//
//        MenuItem viewInfo = new MenuItem();
//        viewInfo.setText(HarvesterUI.CONSTANTS.viewInfo());
//        viewInfo.setIcon(HarvesterUI.ICONS.view_info_icon());
//        viewInfo.addSelectionListener(new SelectionListener<MenuEvent>() {
//            public void componentSelected(MenuEvent ce) {
//                DataProviderUI dataProviderUI = (DataProviderUI) tree.getSelectionModel().getSelectedItems().get(0);
//                DataSourceUI dataSourceUI = dataProviderUI.getDataSourceUIList().get(0);
//                Dispatcher.get().dispatch(AppEvents.ViewDataSetInfo, dataSourceUI);
//            }
//        });
//
//
//
//        add(empty);
//        add(ingestNow);
//        add(ingestSample);
//        add(viewInfo);
//        add(scheduleIngest);
//        add(exportNow);
//        add(scheduleExport);
//        add(new SeparatorMenuItem());
//        add(createExpandAllItem());
//    }
//
//    private MenuItem createExpandAllItem(){
//        MenuItem expandAll = new MenuItem();
//        expandAll.setText(HarvesterUI.CONSTANTS.collapseAll());
//        expandAll.setIcon(HarvesterUI.ICONS.table());
//        expandAll.addSelectionListener(new SelectionListener<MenuEvent>() {
//            public void componentSelected(MenuEvent ce) {
//                tree.mask(HarvesterUI.CONSTANTS.loadingMainData());
//
//                DeferredCommand.addCommand(new Command() {
//                    public void execute() {
//                        tree.collapseAll();
//                    }
//                });
//                DeferredCommand.addCommand(new Command() {
//                    public void execute() {
//                        tree.unmask();
//                    }
//                });
//            }
//        });
//        return expandAll;
//    }
//
//    public void checkRole(){
//        switch (HarvesterUI.UTIL_MANAGER.getLoggedUserRole()){
//            case ADMIN : drawWidget = true;
//                break;
//            case NORMAL: drawWidget = true;
//                break;
//            default: drawWidget = false;
//                break;
//        }
//    }
//}
