package harvesterUI.client.panels.harvesting.calendar;

import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.bradrydzewski.gwt.calendar.client.Calendar;
import com.extjs.gxt.ui.client.util.DateWrapper;
import harvesterUI.shared.tasks.OldTaskUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 25-03-2011
 * Time: 13:48
 */
@SuppressWarnings("deprecation")
public class CalendarAppointmentManager {

    private List<CalendarAppointment> calendarTasksList;
    private Calendar calendar;

    public CalendarAppointmentManager(Calendar cal) {
        calendarTasksList = new ArrayList<CalendarAppointment>();
        calendar = cal;
    }

    public List<CalendarAppointment> getCalendarTasksList() {
        return calendarTasksList;
    }

    public void addCalendarTasks(List<ScheduledTaskUI> scheduledTaskUIList) {
        Date today = new Date();

        for(ScheduledTaskUI taskUI : scheduledTaskUIList) {
            if(taskUI.getType().equals("ONCE")) {
                Date date = taskUI.getDate();
                DateWrapper dw = new DateWrapper(date);

                Date nextDate = new Date(dw.getFullYear()-1900,dw.getMonth(),dw.getDate());
                nextDate.setHours(dw.getHours());
                nextDate.setMinutes(dw.getMinutes());
                nextDate.setDate(dw.getDate());

                calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BLUE));
            }
            else if(taskUI.getType().equals("DAILY"))
            {
                Date date = taskUI.getDate();
                DateWrapper dw = new DateWrapper(date);
                Date currentDate = calendar.getDate();
                currentDate.setHours(dw.getHours());
                currentDate.setMinutes(dw.getMinutes());
                currentDate.setDate(1);

                if(currentDate.getMonth() == dw.getMonth()) {
                    for(int i=0; i<31; i++) {
                        Date nextDate = new Date(dw.getFullYear()-1900,dw.getMonth(),dw.getDate());
                        nextDate.setHours(dw.getHours());
                        nextDate.setMinutes(dw.getMinutes());
                        nextDate.setDate(dw.getDate()+i);
                        if(nextDate.after(today))
                            calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BLUE));
                    }
                } else {
                    if(currentDate.after(date)) {
                        // Add appointments do previous month days that show in current month view
                        for(int i=1; i<5; i++) {
                            Date nextDate = new Date(currentDate.getYear(),currentDate.getMonth(),currentDate.getDate());
                            nextDate.setHours(currentDate.getHours());
                            nextDate.setMinutes(currentDate.getMinutes());
                            nextDate.setDate(currentDate.getDate()-i);
                            if(nextDate.after(today))
                                calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BLUE));
                        }
                        for(int i=0; i<36; i++) {
                            Date nextDate = new Date(currentDate.getYear(),currentDate.getMonth(),currentDate.getDate());
                            nextDate.setHours(currentDate.getHours());
                            nextDate.setMinutes(currentDate.getMinutes());
                            nextDate.setDate(currentDate.getDate()+i);
                            if(nextDate.after(today))
                                calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BLUE));
                        }
                    }
                }
            }
            else if(taskUI.getType().equals("WEEKLY")) {
                Date date = taskUI.getDate();
                DateWrapper dw = new DateWrapper(date);
                Date currentDate = calendar.getDate();
                currentDate.setHours(dw.getHours());
                currentDate.setMinutes(dw.getMinutes());
                currentDate.setDate(1);
                int weeklyDayOfTheWeek = dw.getDay();

                if(currentDate.getMonth() == dw.getMonth()) {
                    for(int i=0; i<35; i+=7) {
                        Date nextDate = new Date(dw.getFullYear()-1900,dw.getMonth(),dw.getDate());
                        nextDate.setHours(dw.getHours());
                        nextDate.setMinutes(dw.getMinutes());
                        nextDate.setDate(dw.getDate()+i);
                        if(nextDate.after(today))
                            calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BLUE));
                    }
                } else {
                    if(currentDate.after(date)) {
                        for(int i=1; i<5; i++) {
                            Date nextDate = new Date(currentDate.getYear(),currentDate.getMonth(),currentDate.getDate());
                            nextDate.setHours(currentDate.getHours());
                            nextDate.setMinutes(currentDate .getMinutes());
                            nextDate.setDate(currentDate.getDate()-i);
                            if(nextDate.getDay() == weeklyDayOfTheWeek && nextDate.after(today))
                                calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BLUE));
                        }
                        for(int i=0; i<35; i++) {
                            Date nextDate = new Date(currentDate.getYear(),currentDate.getMonth(),currentDate.getDate());
                            nextDate.setHours(currentDate.getHours());
                            nextDate.setMinutes(currentDate .getMinutes());
                            nextDate.setDate(currentDate.getDate()+i);
                            if(nextDate.getDay() == weeklyDayOfTheWeek && nextDate.after(today))
                                calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BLUE));
                        }
                    }
                }
            } else if(taskUI.getType().equals("XMONTHLY")) {
                Date date = taskUI.getDate();
                DateWrapper dw = new DateWrapper(date);

                if(calendar.getDate().getMonth() == dw.getMonth()) {
                    Date nextDate = new Date(dw.getFullYear()-1900,dw.getMonth(),dw.getDate());
                    nextDate.setHours(dw.getHours());
                    nextDate.setMinutes(dw.getMinutes());
                    nextDate.setYear(calendar.getDate().getYear());
                    nextDate.setMonth(calendar.getDate().getMonth());
                    if(nextDate.after(today))
                        calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BLUE));
                } else {
                    if(calendar.getDate().after(date)) {
                        int period = taskUI.getMonthPeriod();
                        if(isCorrectMonth(period,dw,calendar.getDate())) {
                            Date nextDate = new Date(dw.getFullYear()-1900,dw.getMonth(),dw.getDate());
                            nextDate.setHours(dw.getHours());
                            nextDate.setMinutes(dw.getMinutes());
                            nextDate.setYear(calendar.getDate().getYear());
                            nextDate.setMonth(calendar.getDate().getMonth());
                            if(nextDate.after(today))
                                calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BLUE));
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void addOldCalendarTasks(List<OldTaskUI> oldTaskUIList)
    {
        for(OldTaskUI taskUI : oldTaskUIList)
        {
            Date date = taskUI.getDate();
            DateWrapper dw = new DateWrapper(date);

            Date nextDate = new Date(dw.getFullYear()-1900,dw.getMonth(),dw.getDate());
            nextDate.setHours(dw.getHours());
            nextDate.setMinutes(dw.getMinutes());
            nextDate.setDate(dw.getDate());

            calendarTasksList.add(new CalendarAppointment(taskUI,nextDate, AppointmentStyle.BROWN));
        }
    }

    public CalendarAppointment getNextAppointment(String dataSourceSet)
    {
        List<CalendarAppointment> nextTasks = new ArrayList<CalendarAppointment>();

        for(CalendarAppointment task : calendarTasksList)
        {
            if(task.getStart().after(new Date()) &&
                    task.getHarvestTask().getDataSetId().equals(dataSourceSet))
                nextTasks.add(task);
        }

        CalendarAppointment resultTask = null;

        if(nextTasks.size() > 0)
        {
            resultTask = nextTasks.get(0);

            if(nextTasks.size() == 1)
                return resultTask;
            else
            {
                for(int i = 1; i <nextTasks.size() ; i++)
                {
                    if(resultTask.getStart().after(nextTasks.get(i).getStart()))
                        resultTask = nextTasks.get(i);
                }
            }
        }
        return resultTask;
    }

//    public void removeCalendarTask(String id)
//    {
//        CalendarAppointment taskToRemove = null;
//
//        for(CalendarAppointment calendarAppointment : calendarTasksList)
//        {
//            if(calendarAppointment.getId().equals(id))
//            {
//                taskToRemove = calendarAppointment;
//            }
//        }
//        calendarTasksList.remove(taskToRemove);
//        calendar.removeAppointment(taskToRemove);
//    }

    private boolean isCorrectMonth(int period, DateWrapper originalDate,Date currentDate) {
        boolean result = false;
        int startYear = originalDate.getFullYear();
        DateWrapper currentDw = new DateWrapper(currentDate);
        int currentYear = currentDw.getFullYear();
        int yearDifference = (currentYear-startYear)+1;
        int startMonth = originalDate.getMonth()+1;
        HashMap<String,List<Integer>> hm = new HashMap<String,List<Integer>>();

        for(int j=0; j<yearDifference; j++) {
            List<Integer> months = new ArrayList<Integer>();
            for(int i=startMonth; i<=12; i+=period) {
                int newYearMonth = i+period;
                if(newYearMonth > 12 ) {
                    startMonth = (i+period)%12;
                    if(i%12 == 0)
                        months.add(12);
                    else
                        months.add(i%12);
                    break;
                }
                months.add(i%12);
            }
            hm.put(""+j,months);
        }

        int currentMonth = currentDw.getMonth()+1;
        if(hm.get(""+(yearDifference-1)).contains(currentMonth))
            result = true;


        return result;
    }

    public void resetCalendarTasks()
    {
        calendar.getAppointments().clear();
        calendarTasksList.clear();
    }
}
