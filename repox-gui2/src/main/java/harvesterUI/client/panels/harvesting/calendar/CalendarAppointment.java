package harvesterUI.client.panels.harvesting.calendar;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.extjs.gxt.ui.client.util.DateWrapper;
import harvesterUI.shared.tasks.HarvestTask;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.Date;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 24-03-2011
 * Time: 19:30
 */
public class CalendarAppointment extends Appointment{

    private HarvestTask harvestTask;

    @SuppressWarnings("deprecation")
    public CalendarAppointment(HarvestTask task,Date date,AppointmentStyle style)
    {
        harvestTask = task;

        setId(harvestTask.getId());

        setStart(date);
        setEnd(date);

        int minutes = date.getMinutes();
        String minString = "" + minutes;
        if(minutes < 10)
            minString = "0" + minutes;

        int hours = date.getHours();
        String hoursString = "" + hours;
        if(hours < 10)
            hoursString = "0" + hours;

        String ingest;
        if(task instanceof ScheduledTaskUI) {
            if(task.getFullIngest().equals("true"))
                ingest = "full";
            else
                ingest = "";
        }
        else
            ingest = "";

        setTitle(parseTypeForTitle(task.getType()) + " " + hoursString + ":" + minString + " " +
                task.get("dataSetId") + " " + ingest);

        setStyle(style);
    }

    public HarvestTask getHarvestTask()
    {
        return harvestTask;
    }

    private String parseTypeForTitle(String type)
    {
        if(type.equals("ONCE"))
            return "";
        if(type.equals("DAILY"))
            return "<b><FONT style=\"BACKGROUND-COLOR: yellow\">D</FONT></b>";
        if(type.equals("WEEKLY"))
            return "<b><FONT style=\"BACKGROUND-COLOR: yellow\">W</FONT></b>";
        if(type.equals("XMONTHLY"))
            return "<b><FONT style=\"BACKGROUND-COLOR: yellow\">M</FONT></b>";
        return "";
    }

    public String getOnlyDate()
    {
        DateWrapper dw = new DateWrapper(getStart());
        return dw.getDate()+"/"+(dw.getMonth()+1)+"/"+dw.getFullYear();
    }

    @SuppressWarnings("deprecation")
    public String getOnlyTime()
    {
        int minutes = getStart().getMinutes();
        String minString = "" + minutes;
        if(minutes < 10)
            minString = "0" + minutes;

        int hours = getStart().getHours();
        String hoursString = "" + hours;
        if(hours < 10)
            hoursString = "0" + hours;

        return hoursString + ":" + minString;
    }
}
