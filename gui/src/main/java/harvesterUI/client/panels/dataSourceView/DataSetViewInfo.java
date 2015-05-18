package harvesterUI.client.panels.dataSourceView;

import com.bradrydzewski.gwt.calendar.client.CalendarViews;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.harvesting.calendar.CalendarTaskManager;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ImageButton;
import harvesterUI.client.util.OldTasksUtil;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormLayout;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.externalServices.ExternalServiceResultUI;
import harvesterUI.shared.externalServices.ExternalServiceUI;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 21-03-2011
 * Time: 16:37
 */
public class DataSetViewInfo extends ContentPanel {

    public FormData formData;
    public FieldSet operationsSet;

    private DataSetFolderViewInfo dataSetFolderViewInfo;
    private DataSetOAIViewInfo dataSetOAIViewInfo;
    private DataSetZ39ViewInfo dataSetZ39ViewInfo;
    private DataSetYaddaViewInfo dataSetYaddaViewInfo;

    private DataSetOperationsServiceAsync dataSetOperationsService;
    private HarvestOperationsServiceAsync harvestOperationsService;

    private Button showAllButton;
    private LayoutContainer showAllOldTasksContainer;
    protected List<LayoutContainer> oldTasksHiddenContainers = new ArrayList<LayoutContainer>();
    protected DataSetOperations dataSetOperationsToolbar;

    protected DefaultFormPanel dataSetInfoForm;

    public DataSetViewInfo() {
        setHeaderVisible(false);
//        setBodyBorder(false);
        setBorders(false);

        dataSetInfoForm = new DefaultFormPanel();
        dataSetInfoForm.setHeaderVisible(false);
        dataSetInfoForm.setLayout(new DefaultFormLayout(150));

        formData = new FormData("95%");
        setIcon(HarvesterUI.ICONS.view_info_icon());
        dataSetOperationsService = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);
        harvestOperationsService = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);

        dataSetOperationsToolbar = new DataSetOperations();
        setTopComponent(dataSetOperationsToolbar);

        dataSetFolderViewInfo = new DataSetFolderViewInfo(dataSetInfoForm,formData);
        dataSetOAIViewInfo = new DataSetOAIViewInfo(dataSetInfoForm,formData);
        dataSetZ39ViewInfo = new DataSetZ39ViewInfo(dataSetInfoForm,formData);
        dataSetYaddaViewInfo = new DataSetYaddaViewInfo(dataSetInfoForm,formData);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setLayout(new FitLayout());
    }

    @Override
    protected void onHide() {
        super.onHide();
//        LayoutContainer center = Registry.get(AppView.CENTER_PANEL);
//        if(center.getItemCount() == 0)
        Dispatcher.forwardEvent(AppEvents.LoadMainData);
//        else
//            History.back();
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width,height);
        super.layout(true);
        operationsSet.layout(true);
        layout(true);
    }

    public void createForm(final DataSourceUI dataSourceUI) {
        dataSourceUI.setUsed(new Date());
        dataSetInfoForm.removeAll();
//        dataSetInfoForm.setLayoutOnChange(true);

        setHeading(HarvesterUI.CONSTANTS.viewDataSet()+": " + dataSourceUI.getDataSourceSet() +
                " ("+HarvesterUI.CONSTANTS.dataProvider()+": " + dataSourceUI.getDataSetParent().getName() + ")");

        removeAll();

        dataSetOperationsToolbar.setAssociatedDataSourceUI(dataSourceUI);

        String ingest = dataSourceUI.getIngest();
//        String delimType = "[ ]+";
//        String[] tokensType = ingest.split(delimType);
//        String typeOfDS = tokensType[0];

        if(ingest.contains("Folder"))
            dataSetInfoForm = dataSetFolderViewInfo.showInfo(dataSourceUI);
        if(ingest.contains("OAI-PMH"))
            dataSetInfoForm = dataSetOAIViewInfo.showInfo(dataSourceUI);
        if(ingest.contains("Z3950"))
            dataSetInfoForm = dataSetZ39ViewInfo.showInfo(dataSourceUI);
        if(ingest.contains("Yadda"))
            dataSetInfoForm = dataSetYaddaViewInfo.showInfo(dataSourceUI);

        add(dataSetInfoForm,formData);

        if(dataSourceUI.getRestServiceUIList().size() > 0)
            dataSetInfoForm.add(new ExternalServicesViewInfo(dataSourceUI));

        createOperationsSet(dataSourceUI);

//        dataSetInfoForm.layout();
    }

    public void createOperationsSet(final DataSourceUI dataSourceUI) {
        operationsSet = new FieldSet();
        operationsSet.setLayoutOnChange(true);
//        operationsSet.setAutoHeight(true);
        operationsSet.setHeading(HarvesterUI.CONSTANTS.informationHistory());
        operationsSet.setLayout(new EditableFormLayout(150));

        AsyncCallback<String> callback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(String result) {
                if(result.equals("NOT_FOUND")) {
                    return;
                }
                String delimDir = "&&";
                String[] tokensDir = result.split(delimDir);
                String fileDir = tokensDir[0];
                String fileName = tokensDir[1];
                String path = fileDir + "/" + fileName;

                LabelField lastExportField = new LabelField();
                lastExportField.setFieldLabel(HarvesterUI.CONSTANTS.lastExportData());
                String filePath = "harvesterui/exportFileDownload?fileName=" + path;
                lastExportField.setValue(new HTML("<a target='_blank' href='"+filePath + "' title='"+HarvesterUI.CONSTANTS.lastExportZipArchive()+"'>"+fileName+"</a>"));
                operationsSet.insert(lastExportField,operationsSet.getItems().indexOf(operationsSet.getItemByItemId("exportContainer_DS_VI")) + 1);
                layout();
            }
        };
        dataSetOperationsService.getExportPath(dataSourceUI.getDataSourceSet(), callback);

        setEditExternalServicesResults(dataSourceUI);
        setEditScheduledTasks(dataSourceUI);
        setEditOldTasks(dataSourceUI);

        dataSetInfoForm.add(operationsSet);
    }

    private void setEditExternalServicesResults(final DataSourceUI dataSourceUI){
        for(final ExternalServiceUI externalServiceUI : dataSourceUI.getRestServiceUIList()){
            String reportUrl = externalServiceUI.getUri().substring(0, externalServiceUI.getUri().lastIndexOf("/")) + "/getReportFile?id="+dataSourceUI.getDataSourceSet();
            AsyncCallback<ExternalServiceResultUI> callback = new AsyncCallback<ExternalServiceResultUI>() {
                public void onFailure(Throwable caught) {
                    new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                }
                public void onSuccess(ExternalServiceResultUI result) {
                    if(result == null) {
                        return;
                    }

                    LayoutContainer validationContainer = new LayoutContainer();
                    HBoxLayout validationContainerLayout = new HBoxLayout();
                    validationContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                    validationContainer.setLayout(validationContainerLayout);
                    LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.externalServiceResult());
                    label.setWidth(UtilManager.SPECIAL_DATASET_VIEWINFO_LABEL_WIDTH);
                    label.addStyleName("defaultFormFieldLabel");
                    validationContainer.add(label, new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
                    validationContainer.add(new LabelToolItem(HarvesterUI.CONSTANTS.service()+": " +
                            externalServiceUI.getName() + " State: " + result.getState()),new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));

                    addXMLReportButton(result,validationContainer, getCorrectXMLReportUrl(externalServiceUI,dataSourceUI.getDataSourceSet()));

                    addShowHTMLResultIcon(externalServiceUI,validationContainer, dataSourceUI);
                    addZipDownloadIcon(externalServiceUI,validationContainer, dataSourceUI);

                    operationsSet.insert(validationContainer,0);
                    layout();
                }
            };
            RepoxServiceAsync repoxService = (RepoxServiceAsync) Registry.get(HarvesterUI.REPOX_SERVICE);
            repoxService.getValidationState(reportUrl, callback);
        }
    }

    private String getCorrectXMLReportUrl(ExternalServiceUI externalServiceUI, String dataSetId){
        if(externalServiceUI.getExternalResultUI() != null && !externalServiceUI.getExternalResultUI().isEmpty())
            return externalServiceUI.getExternalResultUI() + "/getReportFile?dataSetId="+dataSetId;
        else
            return externalServiceUI.getUri().substring(0, externalServiceUI.getUri().lastIndexOf("/")) + "/getReportFile?dataSetId="+dataSetId;
    }

    private void addXMLReportButton(ExternalServiceResultUI result,LayoutContainer validationContainer, final String reportUrl){
        ImageButton viewValidationResultButton = new ImageButton();
        viewValidationResultButton.setToolTip("View Report");
        if(result.getState().equals("ERROR"))
            viewValidationResultButton.setIcon(HarvesterUI.ICONS.externalServiceErrorIcon());
        else
            viewValidationResultButton.setIcon(HarvesterUI.ICONS.externalServiceSuccessIcon());

        viewValidationResultButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                Window.open(reportUrl, "_blank", "");
            }
        });
        validationContainer.add(viewValidationResultButton,new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
    }

    private void addShowHTMLResultIcon(ExternalServiceUI externalServiceUI,LayoutContainer validationContainer, DataSourceUI dataSourceUI){
        if(!dataSourceUI.getStatus().equals("RUNNING")){
            final String htmlUrl = getCorrectHTMLReportUrl(externalServiceUI,dataSourceUI.getDataSourceSet());
            if(htmlUrl != null && !htmlUrl.isEmpty()){
                ImageButton viewHTMLReportButton = new ImageButton();
                ToolTipConfig toolTipConfig = new ToolTipConfig();
                toolTipConfig.setText("View HTML Report");
                toolTipConfig.setShowDelay(0);
                viewHTMLReportButton.setToolTip(toolTipConfig);
                viewHTMLReportButton.setIcon(HarvesterUI.ICONS.html_report_icon());
                viewHTMLReportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        Window.open(htmlUrl, "_blank", "");
                    }
                });
                validationContainer.add(viewHTMLReportButton,new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
            }
        }
    }

    private String getCorrectHTMLReportUrl(ExternalServiceUI externalServiceUI, String dataSetId){
        if(externalServiceUI.getExternalResultUI() != null && !externalServiceUI.getExternalResultUI().isEmpty())
            return externalServiceUI.getExternalResultUI() + "/getHTMLReport?dataSetId="+dataSetId;
        else
            return externalServiceUI.getUri().substring(0, externalServiceUI.getUri().lastIndexOf("/")) + "/getHTMLReport?dataSetId="+dataSetId;
    }

    private void addZipDownloadIcon(ExternalServiceUI externalServiceUI,LayoutContainer validationContainer, DataSourceUI dataSourceUI){
        if(!dataSourceUI.getStatus().equals("RUNNING")){
            final String zipUrl = getCorrectZipReportUrl(externalServiceUI,dataSourceUI.getDataSourceSet());
            if(zipUrl != null && !zipUrl.isEmpty()){
                ImageButton viewHTMLReportButton = new ImageButton();
                ToolTipConfig toolTipConfig = new ToolTipConfig();
                toolTipConfig.setText("Download All Report Data");
                toolTipConfig.setShowDelay(0);
                viewHTMLReportButton.setToolTip(toolTipConfig);
                viewHTMLReportButton.setIcon(HarvesterUI.ICONS.zip_icon());
                viewHTMLReportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        Window.open(zipUrl, "_blank", "");
                    }
                });
                validationContainer.add(viewHTMLReportButton,new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
            }
        }
    }

    private String getCorrectZipReportUrl(ExternalServiceUI externalServiceUI, String dataSetId){
        if(externalServiceUI.getExternalResultUI() != null && !externalServiceUI.getExternalResultUI().isEmpty())
            return externalServiceUI.getExternalResultUI() + "/getReportZip?dataSetId="+dataSetId;
        else
            return externalServiceUI.getUri().substring(0, externalServiceUI.getUri().lastIndexOf("/")) + "/getReportZip?dataSetId="+dataSetId;
    }

    public void setEditScheduledTasks(final DataSourceUI treeDataSourceUI) {
        List<ScheduledTaskUI> scheduledTaskUIs = treeDataSourceUI.getScheduledTasks();

        // Remove schedule past tasks
        Iterator<ScheduledTaskUI> iterator = scheduledTaskUIs.iterator();
        while (iterator.hasNext()){
            ScheduledTaskUI scheduledTaskUI = iterator.next();
            if(scheduledTaskUI.getDate().before(new Date()) && scheduledTaskUI.getType().equals("ONCE"))
                iterator.remove();
        }

        int scheduledTasksSize = scheduledTaskUIs.size();

        if(scheduledTasksSize > 0) {
            LabelField scheduledTask = new LabelField();
            scheduledTask.setFieldLabel(HarvesterUI.CONSTANTS.scheduledTasks());
            scheduledTask.setId(scheduledTaskUIs.get(0).getId());
            scheduledTask.setValue(scheduledTaskUIs.get(0).getDateString());

            LayoutContainer taskContainer = new LayoutContainer();
            HBoxLayout scheduledTasksLayout = new HBoxLayout();
            scheduledTasksLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
            taskContainer.setLayout(scheduledTasksLayout);
            LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.scheduledTasks());
            label.setWidth(UtilManager.SPECIAL_DATASET_VIEWINFO_LABEL_WIDTH);
            label.addStyleName("defaultFormFieldLabel");
            taskContainer.add(label, new HBoxLayoutData(new Margins(0, 3, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
            taskContainer.add(scheduledTask,new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
            Button deleteButton = new Button();
            deleteButton.setIcon(HarvesterUI.ICONS.delete());
            deleteButton.setToolTip(HarvesterUI.CONSTANTS.deleteScheduledTask());
            deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    LayoutContainer lc = (LayoutContainer) ce.getButton().getParent();
                    createConfirmMessageBox(treeDataSourceUI,lc,HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteScheduledTaskConfirmMessage());
                }
            });
            taskContainer.add(deleteButton, new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));

            operationsSet.add(taskContainer,formData);

            for(int i=1; i<scheduledTasksSize; i++) {
                LabelField anotherScheduledTask = new LabelField();
                anotherScheduledTask.setFieldLabel("");
                anotherScheduledTask.setId(scheduledTaskUIs.get(i).getId());
                anotherScheduledTask.setValue(scheduledTaskUIs.get(i).getDateString());

                LayoutContainer anotherTaskContainer = new LayoutContainer();
                HBoxLayout newScheduledTaskLayout = new HBoxLayout();
                newScheduledTaskLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                anotherTaskContainer.setLayout(newScheduledTaskLayout);
                anotherTaskContainer.add(new LabelToolItem("&nbsp;"),
                        new HBoxLayoutData(new Margins(0, UtilManager.SPECIAL_DATASET_VIEWINFO_LABEL_WIDTH,
                                UtilManager.DEFAULT_HBOX_RIGHT_MARGIN, 0)));
                anotherTaskContainer.add(anotherScheduledTask, new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
                Button newDeleteButton = new Button();
                newDeleteButton.setToolTip(HarvesterUI.CONSTANTS.deleteScheduledTask());
                newDeleteButton.setIcon(HarvesterUI.ICONS.delete());
                newDeleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        LayoutContainer lc = (LayoutContainer) ce.getButton().getParent();
                        createConfirmMessageBox(treeDataSourceUI, lc, HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteScheduledTaskConfirmMessage());
                    }
                });
                anotherTaskContainer.add(newDeleteButton,new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));

                operationsSet.add(anotherTaskContainer,formData);
            }
        }
    }

    private void createConfirmMessageBox(final DataSourceUI treeDataSourceUI,
                                         final LayoutContainer lc,
                                         String title, String msg){
        SelectionListener<ButtonEvent> scheduledTaskRemoveListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent ce) {
                AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(Boolean result) {
                        if(!result) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.deleteScheduledTask(), HarvesterUI.CONSTANTS.failedDeleteScheduledTask());
                            return;
                        }
                        treeDataSourceUI.removeScheduledTask(lc.getItem(1).getId());
                        History.fireCurrentHistoryState();
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteScheduledTask(), HarvesterUI.CONSTANTS.deleteScheduledTaskSuccess());
                    }
                };
                harvestOperationsService.deleteScheduledTask(lc.getItem(1).getId(), callback);
            }
        };

        HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(title,msg,scheduledTaskRemoveListener);
    }

    public void setEditOldTasks(final DataSourceUI treeDataSourceUI) {
        OldTasksUtil.sortOldTasks(treeDataSourceUI.getOldTasks());
        List<OldTaskUI> oldTaskUIs = treeDataSourceUI.getOldTasks();
        int oldTasksSize = oldTaskUIs.size();

        if(oldTasksSize > 0) {
            for(int i=oldTasksSize-1; i>=0; i--) {
                LabelToolItem nextLabel;
                if(i == oldTasksSize-1){
                    nextLabel = new LabelToolItem(HarvesterUI.CONSTANTS.oldTasks());
                    nextLabel.setId(oldTaskUIs.get(i).getId());
                    LayoutContainer taskContainer = new LayoutContainer();
                    taskContainer.setId("firstOldTaskContainer");
                    HBoxLayout firstOldTaskLayout = new HBoxLayout();
//                    firstOldTaskLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                    taskContainer.setLayout(firstOldTaskLayout);
                    nextLabel.setWidth(UtilManager.SPECIAL_DATASET_VIEWINFO_LABEL_WIDTH);
                    nextLabel.addStyleName("defaultFormFieldLabel");
                    taskContainer.add(nextLabel, new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
                    taskContainer.add(getOldTaskDateAndTimeObject(oldTaskUIs.get(oldTasksSize-1)),
                            new HBoxLayoutData(new Margins(UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
                    taskContainer.add(new Button(HarvesterUI.CONSTANTS.logFile(),HarvesterUI.ICONS.log_icon(),new SelectionListener<ButtonEvent>() {
                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            LayoutContainer lc = (LayoutContainer) ce.getButton().getParent();
                            OldTaskUI oldTaskUI = treeDataSourceUI.getOldTask(lc.getItem(0).getId());
                            UtilManager.showLogFromByLogName(oldTaskUI);
                        }
                    }),new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));

                    operationsSet.add(taskContainer,new HBoxLayoutData(new Margins(0, 5, 1, 0)));
                    oldTasksHiddenContainers.clear();
                    continue;
                }else
                    nextLabel = new LabelToolItem("&nbsp;");
                nextLabel.setId(oldTaskUIs.get(i).getId());

                LayoutContainer anotherTaskContainer = new LayoutContainer();
                HBoxLayout newOldTaskLayout = new HBoxLayout();
                newOldTaskLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                anotherTaskContainer.setLayout(newOldTaskLayout);
                anotherTaskContainer.add(nextLabel, new HBoxLayoutData(new Margins(0, UtilManager.SPECIAL_DATASET_VIEWINFO_LABEL_WIDTH, 1, 0)));
                anotherTaskContainer.add(getOldTaskDateAndTimeObject(oldTaskUIs.get(i)),
                        new HBoxLayoutData(new Margins(UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
                anotherTaskContainer.add(new Button(HarvesterUI.CONSTANTS.logFile(),HarvesterUI.ICONS.log_icon(), new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        LayoutContainer lc = (LayoutContainer) ce.getButton().getParent();
                        OldTaskUI oldTaskUI = treeDataSourceUI.getOldTask(lc.getItem(0).getId());
                        UtilManager.showLogFromByLogName(oldTaskUI);
                    }
                }),new HBoxLayoutData(new Margins(0, 5, UtilManager.SPECIAL_HBOX_BOTTOM_MARGIN, 0)));
                operationsSet.add(anotherTaskContainer,new HBoxLayoutData(new Margins(0, 5, 1, 0)));

                if(oldTasksSize > 3 && i < oldTasksSize-3) {
                    if(showAllButton == null) {
                        showAllButton = new Button(HarvesterUI.CONSTANTS.showAll(),new SelectionListener<ButtonEvent>() {
                            @Override
                            public void componentSelected(ButtonEvent ce) {
                                Button button = (Button)ce.getComponent();
                                if(button.getText().equals(HarvesterUI.CONSTANTS.showAll())) {
                                    for(LayoutContainer container : oldTasksHiddenContainers){
                                        container.show();
                                    }
                                    button.setText(HarvesterUI.CONSTANTS.hideAll());
                                } else {
                                    for(LayoutContainer container : oldTasksHiddenContainers){
                                        container.hide();
                                    }
                                    button.setText(HarvesterUI.CONSTANTS.showAll());
                                }
                                operationsSet.layout(true);
                            }
                        });
                        showAllOldTasksContainer = new LayoutContainer();
                        HBoxLayout showAllLayout = new HBoxLayout();
                        showAllLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
                        showAllOldTasksContainer.setLayout(showAllLayout);
                        showAllOldTasksContainer.add(new LabelToolItem("&nbsp;"), new HBoxLayoutData(new Margins(0, 172, 0, 0)));
                        showAllOldTasksContainer.add(showAllButton, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
                        operationsSet.add(showAllOldTasksContainer,formData);
                    }
                    operationsSet.insert(showAllOldTasksContainer,operationsSet.getItems().indexOf(operationsSet.getItemByItemId("firstOldTaskContainer")) + 3);
                    anotherTaskContainer.hide();
                    oldTasksHiddenContainers.add(anotherTaskContainer);
                }
            }
        }
        operationsSet.layout(true);
    }

    private Widget getOldTaskDateAndTimeObject(final OldTaskUI taskUI) {
        LayoutContainer dateTimeContainer = new LayoutContainer();
        dateTimeContainer.setLayout(new ColumnLayout());

        final CalendarTaskManager calendarTaskManager = (CalendarTaskManager) Registry.get("calendarTaskManager");

        Date schedule = taskUI.getDate();
        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
        String nextDate = fmt.format(schedule);
        String nextTime = taskUI.getOnlyTime();
        String date = "<span style='color:brown" + "'>" + nextDate + "</span>";
        String time = "<span style='color:brown" + "'>" + nextTime + "</span>";

        LabelToolItem dateLabel = new LabelToolItem(date);
        LabelToolItem timeLabel = new LabelToolItem(time);
        dateLabel.addListener(Events.OnClick,new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                DatePicker dp = (DatePicker) calendarTaskManager.getDpDialog().getItem(0);
                dp.setValue(taskUI.getDate());
                calendarTaskManager.getCalendarPanel().getCalendar().setView(CalendarViews.MONTH);
                Dispatcher.get().dispatch(AppEvents.ViewScheduledTasksCalendar);
            }
        });
        timeLabel.addListener(Events.OnClick,new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                DatePicker dp = (DatePicker) calendarTaskManager.getDpDialog().getItem(0);
                dp.setValue(taskUI.getDate());
                calendarTaskManager.getCalendarPanel().getCalendar().setView(CalendarViews.DAY,1);
                Dispatcher.get().dispatch(AppEvents.ViewScheduledTasksCalendar);
            }
        });
        dateLabel.setStyleName("hyperlink_style_label");
        timeLabel.setStyleName("hyperlink_style_label");

        dateTimeContainer.add(dateLabel);
        dateTimeContainer.add(new LabelField("--"));
        dateTimeContainer.add(timeLabel);
        return dateTimeContainer;
    }

    public static void setRecordsLabel(DataSourceUI dataSourceUI, LabelField records) {
        if(dataSourceUI.getStatus() != null) {
            Image image = new Image();
            String style = "style=\"vertical-align: middle\"";
            if(dataSourceUI.getStatus().startsWith("RUNNING")){
                setImageUrl(image, "RUNNING");
                records.setValue(dataSourceUI.getRecords() +"&nbsp; &nbsp;"+ "<img "+style+" src=\"" + image.getUrl() + "\" title=\""+dataSourceUI.getStatus()+"\"/>");
            }else{
                Image sampleImage = null;
                if(dataSourceUI.getStatus().endsWith("SAMPLE") && !dataSourceUI.getStatus().startsWith("RUNNING")) {
                    sampleImage = new Image();
                    setImageUrl(sampleImage,"RUNNING_SAMPLE");
                }
                setImageUrl(image,dataSourceUI.getStatus());
                if(sampleImage != null)
                    records.setValue(dataSourceUI.getRecords() +"&nbsp; &nbsp;"+ "<img "+style+" src=\"" + image.getUrl() +
                            "\"/>" + "<img src=\"" + sampleImage.getUrl() + "\" title=\""+dataSourceUI.getStatus()+"\"/>");
                else
                    records.setValue(dataSourceUI.getRecords() +"&nbsp; &nbsp;"+ "<img "+style+" src=\"" +
                            image.getUrl() + "\" title=\""+dataSourceUI.getStatus()+"\"/>");
            }
        }
        else
            records.setValue(dataSourceUI.getRecords());
    }

    private static void setImageUrl(Image image, String status) {
        image.setStyleName("hyperlink_icon");
        if(status.equals("RUNNING")){
            image.setUrl("resources/images/icons/task_status/running.gif");
        }else if(status.equals("WARNING")){
            image.setUrl("resources/images/icons/task_status/warning.png");
        }else if(status.equals("ERROR")){
            image.setUrl("resources/images/icons/task_status/error.png");
        }else if(status.equals("CANCELED")){
            image.setUrl("resources/images/icons/task_status/canceled_task.png");
        }else if(status.equals("OK")){
            image.setUrl("resources/images/icons/task_status/ok.png");
        }else if(status.equals("PRE_PROCESSING")){
            image.setUrl("resources/images/icons/task_status/pre_process_16x16.png");
        }else if(status.equals("POST_PROCESSING")){
            image.setUrl("resources/images/icons/task_status/post_process_16x16.png");
        }else if(status.equals("PRE_PROCESS_ERROR")){
            image.setUrl("resources/images/icons/task_status/pre_process_error_16x16.png");
        }else if(status.equals("POST_PROCESS_ERROR")){
            image.setUrl("resources/images/icons/task_status/post_process_error_16x16.png");
        }else if(status.equals("RUNNING_SAMPLE")){
            image.setUrl("resources/images/icons/task_status/sample.png");
        }
        else if(status.endsWith("SAMPLE")){
            String state = status.substring(0, status.indexOf("_"));
            setImageUrl(image,state);
        }
    }
}
