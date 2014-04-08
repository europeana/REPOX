package harvesterUI.shared.tasks;

import com.extjs.gxt.ui.client.util.DateWrapper;

import java.util.Date;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 24-03-2011
 * Time: 15:35
 */
public class ScheduledTaskUI extends HarvestTask {

    private int hourDiff = 0;
    private int minuteDiff = 0;

    public ScheduledTaskUI() {}

    public ScheduledTaskUI(String dataSetId,String id, String dateString,
                           String type, Integer monthPeriod, String fullIngest) {
        super();
        set("dataSetId",dataSetId);
        set("id",id);
        set("dateString",dateString);
        set("type",type);
        set("monthPeriod",monthPeriod);
        set("fullIngest",fullIngest);

        parseDate();
        createTaskListStrings();
    }

    @SuppressWarnings("deprecation")
    public ScheduledTaskUI(String dataSetId, String id, Date date,
                           int hours, int minutes, String type, int monthPeriod, String fullIngest,int scheduleType) {
        super();
        set("dataSetId",dataSetId);
        set("id",id);
        set("date",date);
        set("minutes",minutes);
        set("hours",hours);
        date.setMinutes(minutes);
        date.setHours(hours);
        set("type",type);
        set("monthPeriod",monthPeriod);
        set("fullIngest",fullIngest);
        set("scheduleType",scheduleType);
        createDateString(scheduleType);
        createTaskListStrings();
    }

    public String getDateString() {return (String) get("dateString");}
    public void setScheduleType(Integer scheduleType) { set("scheduleType",scheduleType);}
    public Integer getScheduleType() {return (Integer) get("scheduleType");}
    public void setDateString(String dateString) { set("dateString",dateString);}
    public void setDate(Date date){set("date",date);}

    public Integer getDay() {return (Integer) get("day");}
    public Integer getMonth() {return (Integer) get("month");}
    public Integer getYear() {return (Integer) get("year");}
    public Integer getHours() {return (Integer) get("hours");}
    public void setHours(int hours) { set("hours",hours);}

    public Integer getMinutes() {return (Integer) get("minutes");}
    public void setMinutes(int minutes) { set("minutes",minutes);}

    public String getParameters() {return (String) get("parameters");}
    public void setParameters(String parameters) { set("parameters",parameters);}

    public String getType() {return (String) get("type");}
    public void setType(String type) { set("type",type);}

    public void setScheduleType(String listType) { set("listType",listType);}

    public void setFullIngest(String fullIngest) { set("fullIngest",fullIngest);}

    public Integer getMonthPeriod() {return (Integer) get("monthPeriod");}
    public void setMonthPeriod(int monthPeriod) { set("monthPeriod",monthPeriod);}

    public String getOnlyDate() {return (String) get("onlyDate");}
    public String getOnlyTime() {return (String) get("onlyTime");}

    // Export Task only
    public String getRecordsPerFile() {return (String) get("recordsPerFile");}
    public void setRecordsPerFile(String recordsPerFile) { set("recordsPerFile",recordsPerFile);}

    public String getExportDirectory() {return (String) get("exportDirectory");}
    public void setExportDirectory(String exportDirectory) { set("exportDirectory",exportDirectory);}

    public void setExportFormat(String exportFormat){set("exportFormat", exportFormat);}
    public String getExportFormat(){return (String) get("exportFormat");}

    @SuppressWarnings("deprecation")
    public void parseDate() {
        String date = (String) get("dateString");
        String delimDateTime = "[ ]+";
        String[] tokensDateTime = date.split(delimDateTime);

        String delimDate = "[-]+";
        String[] tokensDate = tokensDateTime[0].split(delimDate);
        set("year",Integer.parseInt(tokensDate[0]));
        set("month",Integer.parseInt(tokensDate[1]));
        set("day",Integer.parseInt(tokensDate[2]));

        //Parse Time
        String delimTime = "[:]+";
        String[] tokensTime = tokensDateTime[1].split(delimTime);
        set("hours",Integer.parseInt(tokensTime[0]));
        set("minutes",Integer.parseInt(tokensTime[1]));

        //Create Date type
        Date actualDate = new Date();

        actualDate.setYear(getYear()-1900);
        actualDate.setMonth(getMonth()-1);
        actualDate.setDate(getDay());
        actualDate.setHours(getHours());
        actualDate.setMinutes(getMinutes());
        set("date",actualDate);

        String monthString = convertSingleNumberToDate(getMonth());
        String dayString = convertSingleNumberToDate(getDay());

        String minString = convertSingleNumberToDate(getMinutes());
        String hoursString = convertSingleNumberToDate(getHours());

        String ingest;
        if(getFullIngest().equals("true"))
            ingest = "Full";
        else
            ingest = "";

        set("onlyTime",hoursString + ":" + minString);
        set("onlyDate",getDay()+"/"+getMonth()+"/"+getYear());
        set("dateString","Ingest " + parseType() + " Starting " + getYear()+"-"+
               monthString +"-"+dayString + " at " + hoursString + ":" + minString + " " + ingest);
    }

    private String convertSingleNumberToDate(int number){
        if(number < 10)
            return "0" + number;
        else
            return String.valueOf(number);
    }

    public void createDateString(int scheduleType) {
        DateWrapper dw = new DateWrapper(getDate());

        int minutes = dw.getMinutes();
        String minString = "" + minutes;
        if(minutes < 10)
            minString = "0" + minutes;

        int hours = dw.getHours();
        String hoursString = "" + hours;
        if(hours < 10)
            hoursString = "0" + hours;

        String ingest;
        if(getFullIngest().equals("true"))
            ingest = "Full";
        else
            ingest = "";

        set("onlyTime",hoursString + ":" + minString);
        set("onlyDate",dw.getDate()+"/"+(dw.getMonth()+1)+"/"+dw.getFullYear());

        String type = "";
        if(scheduleType == 0)
            type = "Ingest ";
        else
            type = "Export ";
        
        String result = type + parseType() + " Starting " + dw.getFullYear() +"/"+
                (dw.getMonth()+1)+"/"+dw.getDate() + " at " + hoursString + ":" + minString + " " + ingest;
        set("dateString",result);
        createTaskListStrings();
    }

    public String parseType() {
        String type = getType();
        if(type.equals("ONCE"))
            return "Once";
        if(type.equals("DAILY"))
            return "Daily";
        if(type.equals("WEEKLY"))
            return "Weekly";
        if(type.equals("XMONTHLY"))
            return "Every " + getMonthPeriod() + " Months ";
        return null;
    }

    public void createTaskListStrings() {
        set("listType","Data Set Importer");
        set("parameters","Data Set: " + getDataSetId() + " -- Full Ingest: " + getFullIngest());
    }

    public int getHourDiff() {
        return hourDiff;
    }

    public void setHourDiff(int hourDiff) {
        this.hourDiff = hourDiff;
    }

    public int getMinuteDiff() {
        return minuteDiff;
    }

    public void setMinuteDiff(int minuteDiff) {
        this.minuteDiff = minuteDiff;
    }
}
