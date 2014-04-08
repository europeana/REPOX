package harvesterUI.client.panels.harvesting.dialogs;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.harvesting.calendar.CalendarAppointment;
import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.tasks.HarvestTask;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.Date;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 12-04-2011
 * Time: 17:56
 */
@SuppressWarnings("deprecation")
public class ScheduleExportDialog extends Dialog {
    private CalendarAppointment selectedAppointment;
    private RadioGroup frequencyGroup;
    private SimpleComboBox<String> hoursCombo,minutesCombo, everyCombo, recordsPerFileCombo;
    private ListStore<TransformationUI> transformationsStore;
    private ComboBox<TransformationUI> transformationCombo;
    private TextField<String> exportPathField;
    private DatePicker datePicker1;
    private HarvestOperationsServiceAsync service;
    private DataSourceUI dataSourceUI;
    protected DefaultFormPanel scheduleForm;

    public ScheduleExportDialog(String dataSetId) {
        setModal(true);
        service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
        AsyncCallback<DataSourceUI> callback = new AsyncCallback<DataSourceUI>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(DataSourceUI resultDS) {
                dataSourceUI = resultDS;
                createCalendaryEventDialogBox(1);
                resetValues();
                showAndCenter();
            }
        };
        DataManagementServiceAsync service = (DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE);
        service.getDataSetInfo(dataSetId, callback);
    }

    public void showAndCenter(){
        show();
        center();
    }

    public ScheduleExportDialog(Appointment appointment) {
        setModal(true);
        selectedAppointment = (CalendarAppointment) appointment;
        String dataSetId = selectedAppointment.getHarvestTask().getDataSetId();
        service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
        AsyncCallback<DataSourceUI> callback = new AsyncCallback<DataSourceUI>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(DataSourceUI resultDS) {
                dataSourceUI = resultDS;
                createCalendaryEventDialogBox(0);
                edit(selectedAppointment);
                showAndCenter();
            }
        };
        DataManagementServiceAsync service = (DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE);
        service.getDataSetInfo(dataSetId, callback);
    }

    @SuppressWarnings("deprecation")
    private void createCalendaryEventDialogBox(int type) {
        // Create a dialog box and set the caption text
        setButtons("");
        setLayout(new FitLayout());
        setHeading("Calendar event");
        setIcon(HarvesterUI.ICONS.schedule_export_icon());
        setWidth(650);
        setHeight(480);
        setResizable(false);

        FormData formData = new FormData("100%");

        scheduleForm = new DefaultFormPanel();
        scheduleForm.setHeaderVisible(false);
        scheduleForm.setLayout(new EditableFormLayout(UtilManager.DEFAULT_DATASET_VIEWINFO_LABEL_WIDTH));

        Date today = new Date();

        datePicker1 = new DatePicker();
        DateWrapper dw = new DateWrapper(today);
        final LabelToolItem firstRun = new LabelToolItem(dw.getDate() + "-"
                + (dw.getMonth()+1) + "-" + dw.getFullYear());

        datePicker1.setMinDate(today);
        datePicker1.addListener(Events.Select, new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent ce) {
                DatePicker dp = ce.getComponent();
                DateWrapper dw = new DateWrapper(dp.getValue());
                firstRun.setLabel(dw.getDate() + "-" + (dw.getMonth() + 1) + "-" + dw.getFullYear());
            }
        });
        scheduleForm.add(datePicker1,formData);

        Date nextMonth = new Date();
        nextMonth.setMonth(today.getMonth() + 1);

        LayoutContainer firstRunContainer = new LayoutContainer();
        HBoxLayout firstRunContainerLayout = new HBoxLayout();
        firstRunContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        firstRunContainer.setLayout(firstRunContainerLayout);
        LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.firstRun());
        label.setWidth(153);
        label.addStyleName("defaultFormFieldLabel");
        firstRunContainer.add(label, new HBoxLayoutData(new Margins(5, 5, 4, 0)));
        firstRunContainer.add(firstRun,new HBoxLayoutData(new Margins(5, 2, 4, 0)));

        hoursCombo = new SimpleComboBox<String>();
        firstRunContainer.add(new LabelToolItem("at"),new HBoxLayoutData(new Margins(5, 7, 4, 6)));
        hoursCombo.setDisplayField("value");
        for(int i=0; i<24; i++){
            if(i < 10)
                hoursCombo.add("0"+i);
            else
                hoursCombo.add(String.valueOf(i));
        }
        hoursCombo.setValue(hoursCombo.getStore().getModels().get(12));
        hoursCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        hoursCombo.setEditable(false);
        hoursCombo.setWidth(55);
        firstRunContainer.add(hoursCombo,new HBoxLayoutData(new Margins(5, 0, 4, 5)));
        firstRunContainer.add(new LabelToolItem("h"),new HBoxLayoutData(new Margins(5, 10, 4, 5)));

        minutesCombo = new SimpleComboBox<String>();
        minutesCombo.setDisplayField("value");
        for(int i=0; i<60; i+=5){
            if(i == 0)
                minutesCombo.add("00");
            else if(i == 5)
                minutesCombo.add("05");
            else
                minutesCombo.add(String.valueOf(i));
        }
        minutesCombo.setValue(minutesCombo.getStore().getModels().get(0));
        minutesCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        minutesCombo.setEditable(false);
        minutesCombo.setWidth(55);
        firstRunContainer.add(minutesCombo,new HBoxLayoutData(new Margins(5, 0, 4, 0)));
        firstRunContainer.add(new LabelToolItem("min"),new HBoxLayoutData(new Margins(5, 5, 4, 5)));
        scheduleForm.add(firstRunContainer);

        recordsPerFileCombo = new SimpleComboBox<String>();
        recordsPerFileCombo.setFieldLabel(HarvesterUI.CONSTANTS.recordsPerFile());
        recordsPerFileCombo.setDisplayField("value");
        recordsPerFileCombo.add("1");
        recordsPerFileCombo.add("10");
        recordsPerFileCombo.add("100");
        recordsPerFileCombo.add("250");
        recordsPerFileCombo.add("1000");
        recordsPerFileCombo.add("All");
        recordsPerFileCombo.setValue(recordsPerFileCombo.getStore().getModels().get(0));
        recordsPerFileCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        recordsPerFileCombo.setEditable(false);
        scheduleForm.add(recordsPerFileCombo);

        transformationsStore = new ListStore<TransformationUI>();
//        transformationsStore.add(new TransformationUI(dataSourceUI.getSourceMDFormat(),"","",dataSourceUI.getSourceMDFormat(),"","",""));

        transformationCombo = new ComboBox<TransformationUI>();
        transformationCombo.setEmptyText(HarvesterUI.CONSTANTS.noAvailableTransformation());
        transformationCombo.setEditable(false);
        transformationCombo.setFieldLabel(HarvesterUI.CONSTANTS.exportFormat());
        transformationCombo.setDisplayField("destFormat");
        transformationCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        transformationCombo.setStore(transformationsStore);
//        transformationCombo.setValue(transformationsStore.getModels().get(0));
        scheduleForm.add(transformationCombo);

        exportPathField = new TextField<String>();
        exportPathField.setAllowBlank(false);
        exportPathField.setId("exportFullPathField");
        exportPathField.setFieldLabel(HarvesterUI.CONSTANTS.fullPath());
        scheduleForm.add(exportPathField,new FormData("95%"));

        Radio once = new Radio();
        once.setBoxLabel(HarvesterUI.CONSTANTS.once());
        Radio daily = new Radio();
        daily.setBoxLabel(HarvesterUI.CONSTANTS.daily());
        Radio weekly = new Radio();
        weekly.setBoxLabel(HarvesterUI.CONSTANTS.weekly());
        Radio month = new Radio();
        month.setBoxLabel(HarvesterUI.CONSTANTS.every());

        frequencyGroup = new RadioGroup();
        frequencyGroup.setFieldLabel(HarvesterUI.CONSTANTS.frequency());
        frequencyGroup.setValue(once);
        frequencyGroup.add(once);
        frequencyGroup.add(daily);
        frequencyGroup.add(weekly);
        frequencyGroup.add(month);
        scheduleForm.add(frequencyGroup);

        everyCombo = new SimpleComboBox<String>();
        everyCombo.setDisplayField("value");
        for(int i=1; i<13; i++){
            everyCombo.add(String.valueOf(i));
        }
        everyCombo.setValue(everyCombo.getStore().getModels().get(0));
        everyCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        everyCombo.setEditable(false);
        everyCombo.setWidth(50);

        LayoutContainer everyContainer = new LayoutContainer();
        HBoxLayout everyContainerLayout = new HBoxLayout();
        everyContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        everyContainer.setLayout(everyContainerLayout);
        everyContainer.add(new LabelField(""),new HBoxLayoutData(new Margins(0, 375, 5, 0)));
        everyContainer.add(everyCombo,new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        everyContainer.add(new LabelField(HarvesterUI.CONSTANTS.months()),new HBoxLayoutData(new Margins(0, 5, 5, 0)));
        scheduleForm.add(everyContainer);

        String viewDSStr = "<span style='color:blue'>"+HarvesterUI.CONSTANTS.viewDataSet()+"</span>";
        LabelToolItem viewDSLabel = new LabelToolItem(viewDSStr);
        viewDSLabel.setStyleName("hyperlink_style_label");
        viewDSLabel.addListener(Events.OnClick,new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                hide();
                Dispatcher.get().dispatch(AppEvents.ViewDataSetInfo, dataSourceUI);
            }
        });
        viewDSLabel.setStyleName("hyperlink_style_label");
        getHeader().addTool(viewDSLabel);

        // Dialog Box of Type update
        Button scheduleButton;
        if(type == 0) {
            final SelectionListener<ButtonEvent> scheduledExportRemoveListener = new SelectionListener<ButtonEvent> () {
                public void componentSelected(ButtonEvent ce) {
                    final String selectedTaskId = selectedAppointment.getHarvestTask().getId();

                    AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
                        public void onFailure(Throwable caught) {
                            new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        }
                        public void onSuccess(Boolean result) {
                            if(!result) {
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.deleteScheduledTask(), HarvesterUI.CONSTANTS.failedDeleteScheduledTask());
                                return;
                            }
                            Dispatcher.get().dispatch(AppEvents.ViewScheduledTasksCalendar);
                            hide();
                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteScheduledTask(),
                                    "Scheduled Task with id " + selectedTaskId + " was deleted successfully");
                        }
                    };
                    service.deleteScheduledTask(selectedTaskId, callback);
                }
            };

            scheduleButton = new Button(HarvesterUI.CONSTANTS.schedule(),HarvesterUI.ICONS.schedule_export_icon(), new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {

                    final ScheduledTaskUI scheduledTaskUI = ((ScheduledTaskUI)selectedAppointment.getHarvestTask());
                    Date date = datePicker1.getValue();
                    date.setHours(Integer.parseInt((String)hoursCombo.getValue().get("value")));
                    date.setMinutes(Integer.parseInt((String)minutesCombo.getValue().get("value")));
                    int monthPeriod = Integer.parseInt((String)everyCombo.getValue().get("value"));
                    String recordsPerFile = (String)recordsPerFileCombo.getValue().get("value");
                    String type = convertFrequencyLabelToType();

                    String newParams = "Data Set: " + selectedAppointment.getHarvestTask().getDataSetId()
                            + " -- Folder: " + HarvesterUI.getMainConfigurationData().getRepositoryFolderPath() + "/" +
                            selectedAppointment.getHarvestTask().getDataSetId() + "/export";

                    scheduledTaskUI.setDate(date);
                    scheduledTaskUI.setMonthPeriod(monthPeriod);
                    scheduledTaskUI.setType(type);
                    scheduledTaskUI.setParameters(newParams);
                    scheduledTaskUI.setRecordsPerFile(recordsPerFile);
                    scheduledTaskUI.setExportDirectory(exportPathField.getValue());
                    String outputFormat = transformationCombo.getValue().getDestFormat();
                    if(outputFormat.equals(dataSourceUI.getSourceMDFormat()))
                        outputFormat = null;
                    scheduledTaskUI.setExportFormat(outputFormat);

                    AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
                        public void onFailure(Throwable caught) {
                            new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        }
                        public void onSuccess(Boolean result) {
                            if(!result) {
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.scheduleTaskUpdate(), HarvesterUI.CONSTANTS.failedScheduleUpdate());
                                return;
                            }

                            hide();
                            scheduledTaskUI.createDateString(1);
                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.scheduleTaskUpdate(), HarvesterUI.CONSTANTS.scheduleUpdateSuccess());
                            Dispatcher.get().dispatch(AppEvents.ViewScheduledTasksCalendar);
                        }
                    };
                    scheduleForm.submit();
                    service.updateScheduledTask(scheduledTaskUI,callback);
                }
            });
            scheduleForm.addButton(scheduleButton);

            Button deleteButton = new Button(HarvesterUI.CONSTANTS.delete(),HarvesterUI.ICONS.delete(), new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteScheduledTaskMessage(),
                            scheduledExportRemoveListener);
                }
            });
            scheduleForm.addButton(deleteButton);
        } else {
            // Dialog Box of Type create new Schedule
            scheduleButton = new Button(HarvesterUI.CONSTANTS.schedule(),HarvesterUI.ICONS.schedule_export_icon(),  new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    final ScheduledTaskUI newTask = new ScheduledTaskUI(dataSourceUI.getDataSourceSet(),"342",
                            datePicker1.getValue(),
                            Integer.parseInt((String)hoursCombo.getValue().get("value")),
                            Integer.parseInt((String)minutesCombo.getValue().get("value")),
                            convertFrequencyLabelToType(),
                            Integer.parseInt((String)everyCombo.getValue().get("value")),"",1);

                    newTask.setParameters("Data Set: " + newTask.getDataSetId() + " -- Folder: " +
                            HarvesterUI.getMainConfigurationData().getRepositoryFolderPath() + "/" + newTask.getDataSetId() + "/export");
                    newTask.setRecordsPerFile((String)recordsPerFileCombo.getValue().get("value"));
                    newTask.setScheduleType("Data Set Export");
                    newTask.setExportDirectory(exportPathField.getValue());
                    String outputFormat = transformationCombo.getValue().getDestFormat();
                    if(outputFormat.equals(dataSourceUI.getSourceMDFormat()))
                        outputFormat = null;
                    newTask.setExportFormat(outputFormat);

                    AsyncCallback<String> callback = new AsyncCallback<String>() {
                        public void onFailure(Throwable caught) {
                            new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                        }
                        public void onSuccess(String id) {
                            if(id.equals("notFound")) {
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.exportSchedule(), HarvesterUI.CONSTANTS.exportScheduleError());
                                return;
                            } else if(id.equals("alreadyExists")) {
                                HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.exportSchedule(), HarvesterUI.CONSTANTS.exportAlreadyExists());
                                return;
                            }
                            hide();
                            newTask.setId(id);
                            History.fireCurrentHistoryState();
                            HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.exportSchedule(), HarvesterUI.CONSTANTS.exportScheduleSuccess());
                        }
                    };
                    service.addScheduledTask(newTask, callback);
                }
            });
            scheduleForm.addButton(scheduleButton);
        }

        scheduleForm.setButtonAlign(Style.HorizontalAlignment.CENTER);
        FormButtonBinding binding = new FormButtonBinding(scheduleForm);
        binding.addButton(scheduleButton);

        add(scheduleForm);
    }

    public void edit(Appointment appt) {
        // Get the appointment selected
        if(appt instanceof CalendarAppointment) {
            setHeading(dataSourceUI.getDataSetParent().getName() + " - " + dataSourceUI.getDataSourceSet());
            chooseType(selectedAppointment.getHarvestTask());
            setTimeAndDateAndPeriod(selectedAppointment.getHarvestTask());

            exportPathField.setValue(((ScheduledTaskUI) selectedAppointment.getHarvestTask()).getExportDirectory());

            recordsPerFileCombo.getStore().clearFilters();
            for(SimpleComboValue<String> comboSel: recordsPerFileCombo.getStore().getModels()) {
                if(comboSel.getValue().equals(
                        "" + ((ScheduledTaskUI) selectedAppointment.getHarvestTask()).getRecordsPerFile()))
                    recordsPerFileCombo.setValue(comboSel);
            }
            transformationsStore.clearFilters();
            transformationsStore.removeAll();
            transformationsStore.add(new TransformationUI(dataSourceUI.getSourceMDFormat(),"","",dataSourceUI.getSourceMDFormat(),"","","",false));
            if(dataSourceUI.getMetadataTransformations().size() > 0) {
                transformationsStore.add(dataSourceUI.getMetadataTransformations());
                for(TransformationUI comboSel: transformationCombo.getStore().getModels()) {
                    if(comboSel.getDestFormat().equals(
                            ""+((ScheduledTaskUI)selectedAppointment.getHarvestTask()).getExportFormat())) {
                        transformationCombo.setValue(comboSel);
                        break;
                    } else
                        transformationCombo.setValue(transformationsStore.getModels().get(0));
                }
            } else
                transformationCombo.setValue(transformationsStore.getModels().get(0));
        }
//        } else if (event instanceof TimeBlockClickEvent) {
//            TimeBlockClickEvent<Date> clickedDate =
//                    (TimeBlockClickEvent<Date>) event;
//            Date startDate = clickedDate.getTarget();
////            eventWhenText.setText(startDate.toString());
//            ap = new Appointment();
//            ap.setTitle("(No title)");
//            ap.setStart(startDate);
//            Date endDate = (Date) startDate.clone();
//            // default time is 1 hour
//            endDate.setHours(startDate.getHours() + 1);
//            ap.setEnd(endDate);
//            calendar.addAppointment(ap);
//        }
    }

    private void chooseType(HarvestTask task) {
        String type = task.getType();
        if(type.equals("ONCE"))
            frequencyGroup.setValue((Radio)frequencyGroup.get(0));
        if(type.equals("DAILY"))
            frequencyGroup.setValue((Radio)frequencyGroup.get(1));
        if(type.equals("WEEKLY"))
            frequencyGroup.setValue((Radio)frequencyGroup.get(2));
        if(type.equals("XMONTHLY"))
            frequencyGroup.setValue((Radio)frequencyGroup.get(3));
    }

    private String convertFrequencyLabelToType() {
        String freqLabel = frequencyGroup.getValue().getBoxLabel();
        if(freqLabel.equals(HarvesterUI.CONSTANTS.once()))
            return "ONCE";
        if(freqLabel.equals(HarvesterUI.CONSTANTS.daily()))
            return "DAILY";
        if(freqLabel.equals(HarvesterUI.CONSTANTS.weekly()))
            return "WEEKLY";
        if(freqLabel.equals(HarvesterUI.CONSTANTS.every()))
            return "XMONTHLY";
        return "Null";
    }

    @SuppressWarnings("deprecation")
    private void setTimeAndDateAndPeriod(HarvestTask task) {
        datePicker1.setValue(task.getDate());

        hoursCombo.getStore().clearFilters();
        minutesCombo.getStore().clearFilters();
        everyCombo.getStore().clearFilters();

        for(SimpleComboValue<String> comboSel: hoursCombo.getStore().getModels()) {
            if(comboSel.getValue().equals("" + task.getDate().getHours()))
                hoursCombo.setValue(comboSel);
        }
        for(SimpleComboValue<String> comboSel: minutesCombo.getStore().getModels()) {
            if(comboSel.getValue().equals("" + task.getDate().getMinutes()))
                minutesCombo.setValue(comboSel);
        }

        for(SimpleComboValue<String> comboSel: everyCombo.getStore().getModels()) {
            if(comboSel.getValue().equals("" + ((ScheduledTaskUI) task).getMonthPeriod()))
                everyCombo.setValue(comboSel);
        }
    }

    public void resetValues() {
        setHeading(dataSourceUI.getDataSetParent().getName() + " - " + dataSourceUI.getDataSourceSet());
        Date today = new Date();
        datePicker1.setValue(today);
        frequencyGroup.setValue((Radio) frequencyGroup.get(0));

        hoursCombo.getStore().clearFilters();
        minutesCombo.getStore().clearFilters();
        everyCombo.getStore().clearFilters();
        recordsPerFileCombo.getStore().clearFilters();
        transformationsStore.clearFilters();

        recordsPerFileCombo.setValue(recordsPerFileCombo.getStore().getAt(0));
        transformationsStore.removeAll();
        transformationsStore.add(new TransformationUI(dataSourceUI.getSourceMDFormat(),"","",dataSourceUI.getSourceMDFormat(),"","","",false));
        transformationCombo.setValue(transformationsStore.getModels().get(0));
        // special case at hours last 5 minutes
        int nextFiveMinutes = getNextFiveMinutes(today.getMinutes());
        if(nextFiveMinutes == 60) {
            hoursCombo.setValue(hoursCombo.getStore().getAt(today.getHours()+1));
            getActualMinutes(0);
        } else {
            hoursCombo.setValue(hoursCombo.getStore().getAt(today.getHours()));
            getActualMinutes(nextFiveMinutes);
        }

        everyCombo.setValue(everyCombo.getStore().getAt(0));
        selectedAppointment = null;
        exportPathField.setValue(dataSourceUI.getExportDirectory());
    }

    protected int getNextFiveMinutes(int minutes) {
        while(true) {
            if(minutes % 5 == 0)
                return minutes;
            else minutes++;
        }
    }

    protected void getActualMinutes(int minutes) {
        for(SimpleComboValue<String> comboSel: minutesCombo.getStore().getModels()) {
            if(comboSel.getValue().equals(String.valueOf(minutes)))
                minutesCombo.setValue(comboSel);
            else if(comboSel.getValue().equals("00") && minutes == 0)
                minutesCombo.setValue(comboSel);
            else if(comboSel.getValue().equals("05") && minutes == 5)
                minutesCombo.setValue(comboSel);
        }
    }
}
