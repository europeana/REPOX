package harvesterUI.client.panels.overviewGrid.columnRenderes;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.client.util.MainGridProgressBar;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.tasks.RunningTask;

import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 23-02-2012
 * Time: 16:06
 */
public class IngestStatusRenderer implements GridCellRenderer<ModelData> {

    public IngestStatusRenderer() {
    }

    public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                         ListStore<ModelData> store, Grid<ModelData> grid) {
        if(model instanceof DataSourceUI) {
            final DataSourceUI dataSourceUI = (DataSourceUI)model;

            if(dataSourceUI.getStatus() == null)
                return null;

            // Status = RUNNING - show progress bar
            if(dataSourceUI.getStatus().equals("RUNNING") || dataSourceUI.getStatus().equals("RUNNING_SAMPLE")) {
                if(dataSourceUI.getTotalRecords() != null && dataSourceUI.getTotalRecords() != -1 &&
                        dataSourceUI.getTotalRecords() != 0 &&
                        !dataSourceUI.getMetadataFormat().equals("ISO2709")){
                    float percentage = dataSourceUI.getIngestPercentage();
                    final MainGridProgressBar bar = new MainGridProgressBar();
                    if(dataSourceUI.getIngestTimeLeft() != 0) {
                        long timeLeft = Math.abs(dataSourceUI.getIngestTimeLeft());
                        bar.setToolTip("<b>"+HarvesterUI.CONSTANTS.records()+":</b> " + dataSourceUI.getRecords() + "/" +dataSourceUI.getTotalRecordsStr() + "<br/>" +
                                "<b>"+HarvesterUI.CONSTANTS.estimate()+":</b> " + UtilManager.formatIntoHHMMSS(timeLeft) + "s");
                    } else {
                        bar.setToolTip(HarvesterUI.CONSTANTS.records()+": " + dataSourceUI.getRecords() + "/" +dataSourceUI.getTotalRecordsStr());
                    }
                    if(percentage > 100)
                        percentage = 100;
                    bar.setHeight(15);
                    bar.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
                        public void handleEvent(BoxComponentEvent be) {
                            UtilManager.showLog(dataSourceUI);
                        }
                    });

                    bar.updateProgress(percentage / 100, (int) percentage + "%");
                    return bar;
                } else {
                    final MainGridProgressBar bar = new MainGridProgressBar();
                    bar.setToolTip("<b>"+HarvesterUI.CONSTANTS.records()+":</b> " + dataSourceUI.getRecords());
                    bar.setHeight(15);
                    bar.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
                        public void handleEvent(BoxComponentEvent be) {
                            UtilManager.showLog(dataSourceUI);
                        }
                    });

                    final Timer t = new Timer() {
                        float i;
                        @Override
                        public void run() {
                            bar.updateProgress(i / 100, "Ingesting...");
                            i += 5;
                            if (i > 105 && bar.isVisible()) {
                                i=0;
//                                        Info.display("Message", "Items were loaded", "");
                            }
                        }
                    };
                    t.scheduleRepeating(500);
                    return bar;
                }
            }else if(dataSourceUI.getStatus() != null && !dataSourceUI.getStatus().isEmpty()) {
                HorizontalPanel container = new HorizontalPanel();
                container.addStyleName("columnLayoutCenter");
                ImageButton imageButton = new ImageButton();
                imageButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                    public void handleEvent(ButtonEvent be) {
                        UtilManager.showLog(dataSourceUI);
                    }
                });
                HarvesterUI.UTIL_MANAGER.setImageUrl(imageButton, dataSourceUI.getStatus());
                container.add(imageButton);

                String status = dataSourceUI.getStatus();
                String delimType = "[_]";
                String[] tokensType = status.split(delimType);

                // Sample ingest
                if(tokensType[tokensType.length-1].equals("SAMPLE")) {
                    HarvesterUI.UTIL_MANAGER.setImageUrl(imageButton, tokensType[0]);
                    ImageButton sampleImage = new ImageButton();
                    HarvesterUI.UTIL_MANAGER.setImageUrl(sampleImage, "RUNNING_SAMPLE");
                    sampleImage.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                        public void handleEvent(ButtonEvent be) {
                            UtilManager.showLog(dataSourceUI);
                        }
                    });

                    container.clear();
//                    columnLayout.setExtraStyle("");
                    imageButton.setStyleAttribute("margin-right", "5px");
                    container.add(imageButton);
                    container.add(sampleImage);
                    return container;
                }
                // Special case when task failed but is retrying
                else if(status.equals("ERROR") && dataSourceUI.getHasRunningTask()) {
                    final ImageButton retryImage = new ImageButton();
                    HarvesterUI.UTIL_MANAGER.setImageUrl(retryImage, "RETRYING");
                    imageButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                        public void handleEvent(ButtonEvent be) {
                            Dispatcher.forwardEvent(AppEvents.ViewRunningTasksList);
                        }
                    });

                    AsyncCallback<RunningTask> callbackRT = new AsyncCallback<RunningTask>() {
                        public void onFailure(Throwable caught) {
                            new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        }
                        public void onSuccess(RunningTask runningTask) {
                            if(runningTask == null)
                                return;
                            ToolTipConfig config = new ToolTipConfig();
                            config.setShowDelay(1000);
                            config.setTitle(HarvesterUI.CONSTANTS.taskFailedRetry());
                            config.setText("(" + runningTask.getRetries() + " "+HarvesterUI.CONSTANTS.of()+" " + runningTask.getRetryMax() + ")");
                            retryImage.getToolTip().update(config);
                        }
                    };
                    HarvestOperationsServiceAsync service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
                    List<FilterQuery> filterQueries = ((BrowseFilterPanel) Registry.get("browseFilterPanel")).getAllQueries();
                    String username = HarvesterUI.UTIL_MANAGER.getLoggedUserName();
                    service.getRunningTask(dataSourceUI.getDataSourceSet(),filterQueries,username,callbackRT);

                    container.clear();
//                    columnLayout.setExtraStyle("");
                    imageButton.setStyleAttribute("margin-right", "5px");
                    container.add(imageButton);
                    container.add(retryImage);
                    return container;
                }
                return container;
            }
            return "";
        }
        else
            return "";
    }
}
