package harvesterUI.client.panels.harvesting.calendar;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.Calendar;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings;
import com.bradrydzewski.gwt.calendar.client.CalendarViews;
import com.bradrydzewski.gwt.calendar.client.event.*;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.panels.harvesting.dialogs.ScheduleExportDialog;
import harvesterUI.client.panels.harvesting.dialogs.SchedulePastResume;
import harvesterUI.client.panels.harvesting.dialogs.ScheduleTaskDialog;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.tasks.HarvestTask;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.Date;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 23-03-2011
 * Time: 15:15
 */
public class GoogleCalendarPanel extends ContentPanel {

    public Calendar calendar = null;

    private int calendarHeightSize = 58;
    private Button currentMonthAndYear;
    private Dialog dpDialog;
    private CalendarTaskManager calendarTaskManager;

    private CalendarSettings settings = new CalendarSettings();

    public GoogleCalendarPanel(CalendarTaskManager calendarManager) {

        // style this element as absolute position
        DOM.setStyleAttribute(this.getElement(), "position", "absolute");

        calendarTaskManager = calendarManager;

        configureCalendar();
        createDatePickerDialog();

        setHeaderVisible(false);
        setBodyBorder(false);
        setBorders(false);
        setTopComponent(createCalendarToolbar());
        add(calendar);

        // window events to handle resizing
        Window.enableScrolling(false);
        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                resizeTimer.schedule(500);
                int h = event.getHeight();
            }
        });
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                LayoutContainer center = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
                calendar.setHeight(center.getHeight() - calendarHeightSize + "px");
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void configureCalendar() {

        // change hour offset to false to facilitate google style
        settings.setOffsetHourLabels(false);
//        settings.setTimeBlockClickNumber(CalendarSettings.Click.Single);
        settings.setEnableDragDrop(true);

        calendar = new Calendar();
        calendar.setSettings(settings);
        calendar.setView(CalendarViews.MONTH);
        calendar.setWidth("100%");

        // TODO: Preparar estes dois handlers para a SCHEDULED TASK
        calendar.addDeleteHandler(new DeleteHandler<Appointment>() {
            public void onDelete(DeleteEvent<Appointment> event) {
                boolean commit = Window
                        .confirm(HarvesterUI.CONSTANTS.deleteScheduledTaskConfirmMessage()+ " " + event.getTarget().getTitle());
                if (!commit) {
                    event.setCancelled(true);
                }
            }
        });
        calendar.addUpdateHandler(new UpdateHandler<Appointment>() {
            public void onUpdate(UpdateEvent<Appointment> event) {
                if(event.getTarget() != null) {
                    CalendarAppointment appt = (CalendarAppointment) event.getTarget();
                    if(appt.getHarvestTask() instanceof OldTaskUI) {
                        HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.moveOldTasks(),HarvesterUI.CONSTANTS.moveOldTaskError());
                        History.fireCurrentHistoryState();
                    }
                    else {
                        if(appt.getStart().after(new Date())) {
                            if(appt.getHarvestTask() instanceof ScheduledTaskUI) {
                                final ScheduledTaskUI scheduledTaskUI = (ScheduledTaskUI)appt.getHarvestTask();
                                final Date date = appt.getStart();
                                scheduledTaskUI.setDate(date);

                                AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
                                    public void onFailure(Throwable caught) {
                                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                                    }
                                    public void onSuccess(Boolean result) {
                                        if(!result) {
                                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.moveScheduledTask(), HarvesterUI.CONSTANTS.moveScheduledTaskError());
                                            return;
                                        }
                                        DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM/yyyy");
                                        scheduledTaskUI.createDateString(scheduledTaskUI.getScheduleType());
                                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.moveScheduledTask(),
                                                HarvesterUI.CONSTANTS.scheduleTaskMoved() + " " + fmt.format(date));
                                    }
                                };
                                HarvestOperationsServiceAsync service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
                                service.updateScheduledTask(scheduledTaskUI,callback);
                            }
                        }
                        else {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.moveScheduledTask(),
                                    HarvesterUI.CONSTANTS.moveScheduledTaskPrevDateError());
                        }
                        History.fireCurrentHistoryState();
                    }
                }
            }
        });
        calendar.addOpenHandler(new OpenHandler<Appointment>() {
            public void onOpen(OpenEvent<Appointment> event) {
                if (event != null) {
                    Appointment appt = event.getTarget();
                    if(appt instanceof CalendarAppointment) {
                        if(((CalendarAppointment) appt).getHarvestTask() instanceof OldTaskUI)
                            showPastScheduleDialog(((CalendarAppointment) appt).getHarvestTask());
                        else if(((CalendarAppointment) appt).getHarvestTask() instanceof ScheduledTaskUI) {
                            ScheduledTaskUI scheduledTaskUI = (ScheduledTaskUI) ((CalendarAppointment) appt).getHarvestTask();
                            if(scheduledTaskUI.getRecordsPerFile() == null)
                                showScheduleDialog(appt);
                            else
                                showScheduleExportDialog(appt);
                        }
                    }
                }
            }
        });

//        calendar.addSelectionHandler(new SelectionHandler<Appointment>(){
//            public void onSelection(SelectionEvent<Appointment> event) {
//                if (event != null) {
//                    Appointment appt = event.getSelectedItem();
//                    if(appt instanceof CalendarAppointment)
//                    {
//                        Date now = new Date();
//                        // TODO: hora do dia tambem
//                        if(appt.getEnd().before(now))
//                        {
//                            showPastScheduleDialog(((CalendarAppointment) appt).getHarvestTask());
//                        }
//                        else
//                            showScheduleDialog(appt);
//                    }
//                    calendar.resetSelectedAppointment();
//                }
//            }
//        });

        // Handler to show + items for a day
        calendar.addDateRequestHandler(new DateRequestHandler<Date>(){
            public void onDateRequested(DateRequestEvent<Date> event) {
                calendar.setDate(event.getTarget());
                calendar.setView(CalendarViews.DAY, 1);
            }
        });
    }


    private void clickChangeDateButton(int numOfDays) {
        if (numOfDays == 0) {
            calendar.setDate(new Date());
        } else {
            calendar.addDaysToDate(numOfDays);
        }
    }

    private void showScheduleDialog(Appointment appointment){
        new ScheduleTaskDialog(appointment);
    }

    private void showScheduleExportDialog(Appointment appointment){
        new ScheduleExportDialog(appointment);
    }

    private void showPastScheduleDialog(HarvestTask task){
        SchedulePastResume pastResume = new SchedulePastResume((OldTaskUI)task);
        pastResume.show();
        pastResume.center();
    }

    public Calendar getCalendar(){
        return calendar;
    }

    private int height = -1;
    private Timer resizeTimer = new Timer() {
        @Override
        public void run() {
            int newHeight = Window.getClientHeight();
            int newWidth = Window.getClientWidth();
            if (newHeight != height) {
                height = newHeight;
                LayoutContainer center = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
                calendar.setHeight(center.getHeight() - calendarHeightSize + "px");
                calendar.doSizing();
                calendar.doLayout();
            }
        }
    };

    private ToolBar createCalendarToolbar(){
        ToolBar calendarToolbar = new ToolBar();

        currentMonthAndYear = new Button();
        DateTimeFormat formatter = DateTimeFormat.getFormat("MMMM yyyy");
        String result = formatter.format(calendar.getDate());
        currentMonthAndYear.setText(result);
        currentMonthAndYear.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                dpDialog.show();
                dpDialog.center();
            }
        });

        Button today = new Button("<b>" + HarvesterUI.CONSTANTS.today() + "</b>");
        today.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                ((DatePicker)dpDialog.getItem(0)).setValue(new Date());
//                DateTimeFormat formatter = DateTimeFormat.getFormat("MMMM yyyy");
//                String result = formatter.format(calendar.getDate());
//                currentMonthAndYear.setText(result);
//                calendarTaskManager.updateScheduleTasks();
            }
        });

        Button yearLeft = new Button("<b>" + "<<" + "</b>");
        yearLeft.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                DateWrapper dw = new DateWrapper(calendar.getDate());
                ((DatePicker)dpDialog.getItem(0)).setValue(dw.addMonths(-1).asDate());
//                DateTimeFormat formatter = DateTimeFormat.getFormat("MMMM yyyy");
//                String result = formatter.format(calendar.getDate());
//                currentMonthAndYear.setText(result);
//                calendarTaskManager.updateScheduleTasks();
            }
        });

        Button yearRight = new Button("<b>" + ">>" + "</b>");
        yearRight.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                DateWrapper dw = new DateWrapper(calendar.getDate());
                ((DatePicker)dpDialog.getItem(0)).setValue(dw.addMonths(1).asDate());
//                DateTimeFormat formatter = DateTimeFormat.getFormat("MMMM yyyy");
//                String result = formatter.format(calendar.getDate());
//                currentMonthAndYear.setText(result);
//                calendarTaskManager.updateScheduleTasks();
            }
        });

        calendarToolbar.add(today);
        calendarToolbar.add(new SeparatorToolItem());
        calendarToolbar.add(yearLeft);
        calendarToolbar.add(currentMonthAndYear);
        calendarToolbar.add(yearRight);
        calendarToolbar.add(new SeparatorToolItem());

        calendarToolbar.add(new FillToolItem());
        createViewsButtons(calendarToolbar);

        return calendarToolbar;
    }

    private void createViewsButtons(ToolBar calendarToolbar){
        final ToggleButton oneDay = new ToggleButton("<b>" + HarvesterUI.CONSTANTS.oneDay() + "</b>");
        final ToggleButton threeDay = new ToggleButton("<b>"+HarvesterUI.CONSTANTS.threeDay()+"</b>");
        final ToggleButton week = new ToggleButton("<b>"+HarvesterUI.CONSTANTS.week()+"</b>");
        final ToggleButton month = new ToggleButton("<b>"+HarvesterUI.CONSTANTS.month()+"</b>");
        month.toggle();

        oneDay.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            public void handleEvent(ButtonEvent ce) {
                threeDay.toggle(false);
                week.toggle(false);
                month.toggle(false);
                oneDay.toggle(true);
                calendar.setView(CalendarViews.DAY, 1);
            }
        });

        threeDay.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            public void handleEvent(ButtonEvent ce) {
                threeDay.toggle(true);
                week.toggle(false);
                month.toggle(false);
                oneDay.toggle(false);
                calendar.setView(CalendarViews.DAY, 3);
            }
        });

        week.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            public void handleEvent(ButtonEvent ce) {
                threeDay.toggle(false);
                week.toggle(true);
                month.toggle(false);
                oneDay.toggle(false);
                calendar.setView(CalendarViews.DAY, 7);
            }
        });

        month.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            public void handleEvent(ButtonEvent ce) {
                threeDay.toggle(false);
                week.toggle(false);
                month.toggle(true);
                oneDay.toggle(false);
                calendar.setView(CalendarViews.MONTH);
            }
        });

        calendarToolbar.add(oneDay);
        calendarToolbar.add(threeDay);
        calendarToolbar.add(week);
        calendarToolbar.add(month);
    }

    private void createDatePickerDialog(){
        dpDialog = new Dialog();
        dpDialog.setHeading(HarvesterUI.CONSTANTS.datePicker());
        dpDialog.setIcon(HarvesterUI.ICONS.calendar());
        dpDialog.setButtons("");
        dpDialog.setResizable(false);
        dpDialog.setWidth(400);
        dpDialog.setHeight(250);
        dpDialog.setLayout(new FitLayout());
        dpDialog.setBodyStyleName("pad-text");
        dpDialog.setHideOnButtonClick(true);

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(new Date());
        datePicker.addListener(Events.Select, new Listener<DatePickerEvent>() {
            public void handleEvent(DatePickerEvent be) {
                calendar.setDate(be.getDate());
                DateTimeFormat formatter = DateTimeFormat.getFormat("MMMM yyyy");
                String result = formatter.format(calendar.getDate());
                currentMonthAndYear.setText(result);
                calendarTaskManager.updateScheduleTasks();
            }
        });

        dpDialog.add(datePicker);
    }

    public Dialog getDatePickerDialog() {return dpDialog;}
}