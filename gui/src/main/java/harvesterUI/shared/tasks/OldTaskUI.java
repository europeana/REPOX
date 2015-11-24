package harvesterUI.shared.tasks;

import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 29-03-2011
 * Time: 12:12
 */
public class OldTaskUI extends HarvestTask {

    public OldTaskUI() {}

//    public OldTaskUI(DataSourceUI dataSourceUI, String id, String logName, String ingestType,
//                     String status, String retries, String retryMax, String delay, String dateString,
//                     String records) {
//        super();
//        set("dataSourceUI", dataSourceUI);
//        set("id",id);
//        set("logName",logName);
//        set("ingestType",ingestType);
//        set("status",status);
//        set("retries",retries);
//        set("retryMax",retryMax);
//        set("delay",delay);
//        set("dateString",dateString);
//        set("records",records);
//        set("type","");
//
//        parseDate();
//    }

    public OldTaskUI(String dataSetId, String id, String logName, String ingestType,
                     String status, String retries, String retryMax, String delay, String dateString,
                     String records) {
        super();
        set("dataSetId", dataSetId);
        set("id",id);
        set("logName",logName);
        set("ingestType",ingestType);
        set("status",status);
        set("retries",retries);
        set("retryMax",retryMax);
        set("delay",delay);
        set("dateString",dateString);
        set("records",records);
        set("type","");

        parseDate();
    }

//    public DataSourceUI getDataSource() {return (DataSourceUI) get("dataSourceUI");}
    public String getStatus() {return (String) get("status");}
//    public String getDataSourceId() {return (String) get("dataSourceId");}
    public String getIngestType() {return (String) get("ingestType");}
    public String getDelay() {return (String) get("delay");}
    public String getRecords() {return (String) get("records");}
    public String getRetries() {return (String) get("retries");}
    public String getRetryMax() {return (String) get("retryMax");}
    public String getLogName() {return (String) get("logName");}

    public Integer getDay() {return (Integer) get("day");}
    public Integer getMonth() {return (Integer) get("month");}
    public Integer getYear() {return (Integer) get("year");}
    public Integer getHours() {return (Integer) get("hours");}
    public Integer getMinutes() {return (Integer) get("minutes");}

    public String getOnlyDate() {return (String) get("onlyDate");}
    public String getOnlyTime() {return (String) get("onlyTime");}

    public void setDataSourceUI(DataSourceUI dataSourceUI) {set("dataSourceUI", dataSourceUI);}

    @SuppressWarnings("deprecation")
    public void parseDate() {
        String dateString = (String) get("dateString");
        String delimDateTime = "[ ]+";
        String[] tokensDateTime = dateString.split(delimDateTime);

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
//        Date actualDate = new Date(getYear(),getMonth(),getDay());
//        actualDate.setYear(getYear()-1900);
//        actualDate.setMonth(getMonth()-1);
//        actualDate.setHours(getHours());
//        actualDate.setMinutes(getMinutes());
        
//        DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
//        Date actualDate = null;
//        actualDate = sdf.parse(dateString);
        Date actualDate = new Date(getYear()-1900, getMonth()-1, getDay(), getHours(), getMinutes());
        set("date",actualDate);

        String monthString = convertSingleNumberToDate(getMonth());
        String dayString = convertSingleNumberToDate(getDay());

        String minString = convertSingleNumberToDate(getMinutes());
        String hoursString = convertSingleNumberToDate(getHours());
        
        set("onlyTime",hoursString + ":" + minString);
        set("onlyDate",getYear()+"-"+monthString+"-"+dayString);
    }

    private String convertSingleNumberToDate(int number){
        if(number < 10)
            return "0" + number;
        else
            return String.valueOf(number);
    }
}
