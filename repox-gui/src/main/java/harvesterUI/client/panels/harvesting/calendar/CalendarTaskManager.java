package harvesterUI.client.panels.harvesting.calendar;

//import com.bradrydzewski.gwt.calendar.client.Calendar;
//import com.bradrydzewski.gwt.calendar.client.CalendarViews;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.servlets.harvest.TaskManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.filters.FilterQuery;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 23-03-2011
 * Time: 12:28
 */
public class CalendarTaskManager extends ContentPanel {

    private GoogleCalendarPanel calendarPanel;
    private CalendarAppointmentManager calendarAppointmentManager;

    public CalendarTaskManager(){
        setScrollMode(Style.Scroll.AUTO);

        setHeading(HarvesterUI.CONSTANTS.calendar());
        setIcon(HarvesterUI.ICONS.calendar());
        calendarPanel = new GoogleCalendarPanel(this);
        add(calendarPanel);

        calendarAppointmentManager = new CalendarAppointmentManager(calendarPanel.getCalendar());
    }

    public void updateScheduleTasks(){
        AsyncCallback<ModelData> callback = new AsyncCallback<ModelData>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(ModelData calendarData) {
                calendarAppointmentManager.resetCalendarTasks();
                List<ScheduledTaskUI> scheduledTaskUIs = calendarData.get("schedules");
                erasePastScheduleTasks(scheduledTaskUIs, new Date());
                List<OldTaskUI> oldTaskUIs = calendarData.get("oldTasks");
                calendarAppointmentManager.addCalendarTasks(scheduledTaskUIs);
                calendarAppointmentManager.addOldCalendarTasks(oldTaskUIs);
                ArrayList<Appointment> appts = new ArrayList<Appointment>();
                appts.addAll(calendarAppointmentManager.getCalendarTasksList());

                calendarPanel.getCalendar().suspendLayout();
                calendarPanel.getCalendar().addAppointments(appts);
                calendarPanel.getCalendar().resumeLayout();
            }
        };
        List<FilterQuery> filterQueries = ((BrowseFilterPanel) Registry.get("browseFilterPanel")).getAllQueries();
        String username = HarvesterUI.UTIL_MANAGER.getLoggedUserName();
        ((TaskManagementServiceAsync) Registry.get(HarvesterUI.TASK_MANAGEMENT_SERVICE)).getCalendarTasks(filterQueries,username,callback);
    }

    private void erasePastScheduleTasks(List<ScheduledTaskUI> scheduleTaskUIs, Date today) {
        Iterator iter = scheduleTaskUIs.iterator();
        while(iter.hasNext()) {
            ScheduledTaskUI scheduledTaskUI = (ScheduledTaskUI) iter.next();
            if(scheduledTaskUI == null || scheduledTaskUI.getDate().before(today) && scheduledTaskUI.getType().equals("ONCE"))
                iter.remove();
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        updateScheduleTasks();
    }

    public Dialog getDpDialog()
    {
        return getCalendarPanel().getDatePickerDialog();
    }

    public GoogleCalendarPanel getCalendarPanel()
    {
        return calendarPanel;
    }

    public CalendarAppointmentManager getCalendarAppointmentManager()
    {
        return calendarAppointmentManager;
    }
}
