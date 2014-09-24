package harvesterUI.client.panels.browse;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 01-03-2011
 * Time: 16:35
 */

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.models.FilterButton;
import harvesterUI.shared.filters.FilterQueryLastIngest;
import harvesterUI.shared.filters.FilterType;

import java.util.Date;

public class LastIngestFilter extends FilterButton {

    private DateField greaterThenField;
    private DateField smallerThenField;
    private DateField equalToField;
    private NumberField beforeTimeHourField, afterTimeHourField,beforeTimeMinuteField,afterTimeMinuteField;
    private FormData formData;

    private Date beginDate;
    private Date endDate;
    private Date onDate;
    private Date beginTime;
    private Date endTime;

    private String attributeInfo;
    private Menu radioMenu;

    public LastIngestFilter(BrowseFilterPanel browseFilterPanel) {
        super(browseFilterPanel);

        formData = new FormData("-10");

        setText(HarvesterUI.CONSTANTS.range());

        greaterThenField = new DateField();
        greaterThenField.setPropertyEditor(new DateTimePropertyEditor("yyyy/MM/dd"));
        smallerThenField = new DateField();
        smallerThenField.setPropertyEditor(new DateTimePropertyEditor("yyyy/MM/dd"));
        equalToField = new DateField();
        equalToField.setPropertyEditor(new DateTimePropertyEditor("yyyy/MM/dd"));

        greaterThenField.getDatePicker().addListener(Events.Select, new Listener<DatePickerEvent>() {
            public void handleEvent(DatePickerEvent be) {
                Date date = be.getDate();
                setAttributeBeginDate(date);
                updateDateResults();
            }
        });
        smallerThenField.getDatePicker().addListener(Events.Select, new Listener<DatePickerEvent>() {
            public void handleEvent(DatePickerEvent be) {
                Date date = be.getDate();
                setAttributeEndDate(date);
                updateDateResults();
            }
        });
        equalToField.getDatePicker().addListener(Events.Select, new Listener<DatePickerEvent>() {
            public void handleEvent(DatePickerEvent be) {
                Date date = be.getDate();
                clearInterval();
                setAttributeOnDate(date);
//                if(dataFilter.getChecked() == 1)
//                    Dispatcher.get().dispatch(AppEvents.LoadMainData);
            }
        });

        // Time
        beforeTimeHourField = new NumberField();
        beforeTimeMinuteField = new NumberField();
        afterTimeHourField = new NumberField();
        afterTimeMinuteField = new NumberField();

        KeyListener timePressListener =  new KeyListener(){
            @Override
            public void componentKeyDown(final ComponentEvent event) {
                // If ENTER key pressed
                if(event.getKeyCode()==13){
                    if(beforeTimeHourField.getValue() != null && beforeTimeMinuteField != null){
                        setAttributeEndTime(beforeTimeHourField,beforeTimeMinuteField);
                    }
                    if(afterTimeHourField.getValue() != null && afterTimeMinuteField != null){
                        setAttributeBeginTime(afterTimeHourField, afterTimeMinuteField);
                    }
                    checkTimeInterval();
//                    if(dataFilter.getChecked() == 1)
//                        Dispatcher.get().dispatch(AppEvents.LoadMainData);
                }
                super.componentKeyPress(event);
            }};

        beforeTimeHourField.addKeyListener(timePressListener);
        beforeTimeMinuteField.addKeyListener(timePressListener);
        afterTimeHourField.addKeyListener(timePressListener);
        afterTimeMinuteField.addKeyListener(timePressListener);

        Menu rangeMenu = new Menu();
        rangeMenu.add(createDefaultsMenu());
        rangeMenu.add(createDateMenu());
        rangeMenu.add(createTimeMenu());
        setMenu(rangeMenu);
    }

    /*
     *   Common part of the filter
     */

    /*
    * Function to show the filter info according to given range
    */
    private void updateAttributeInfo() {
        // Case in which the tooltip must be activated
        if(!attributeInfo.isEmpty())
            dataFilter.setRangeInfo(attributeInfo);
        else if(!createDateInfoString().equals("") && !createTimeInfoString().equals("")){
            dataFilter.setRangeInfo(createDateInfoString()
                    + "_" + createTimeInfoString());
        }
        else
            dataFilter.setRangeInfo(createDateInfoString() + createTimeInfoString());
        // Update the attribute info value written on the store
        if(browseFilterPanel.getAttributesListStore().contains(dataFilter))
            browseFilterPanel.getAttributesListStore().update(dataFilter);
        else if(browseFilterPanel.getAttributesSelected().contains(dataFilter))
            browseFilterPanel.getAttributesListStore().update(dataFilter);
    }

    /*
     *  Date Part of the filter
     */

    private MenuItem createDateMenu() {
        MenuItem date = new MenuItem(HarvesterUI.CONSTANTS.date());
        date.addListener(Events.OnClick, new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                //Do nothing
            }
        });
        Menu dateMenu = new Menu();

        MenuItem greater = new MenuItem();
        greater.setIcon(HarvesterUI.ICONS.greater_than());
        greater.setHideOnClick(false);
        greater.setWidget(greaterThenField);
        greater.setCanActivate(false);
        MenuItem lesser = new MenuItem();
        lesser.setIcon(HarvesterUI.ICONS.lesser_than());
        lesser.setHideOnClick(false);
        lesser.setWidget(smallerThenField);
        lesser.setCanActivate(false);
        MenuItem equal = new MenuItem();
        equal.setIcon(HarvesterUI.ICONS.equal_to());
        equal.setHideOnClick(false);
        equal.setWidget(equalToField);
        equal.setCanActivate(false);
        MenuItem reset = new MenuItem(HarvesterUI.CONSTANTS.reset());
        reset.setIcon(HarvesterUI.ICONS.delete());
        reset.setHideOnClick(false);

        reset.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent menuEvent) {
                greaterThenField.setValue(null);
                smallerThenField.setValue(null);
                equalToField.setValue(null);
                clearAttributeDate();
//                if(dataFilter.getChecked() == 1)
//                    Dispatcher.get().dispatch(AppEvents.LoadMainData);
            }
        });

        dateMenu.add(greater);
        dateMenu.add(lesser);
        dateMenu.add(equal);
        dateMenu.add(reset);
        date.setSubMenu(dateMenu);
        return date;
    }

    private MenuItem createDefaultsMenu() {
        radioMenu = new Menu();
        MenuItem radios = new MenuItem("Defaults");
        CheckMenuItem r = new CheckMenuItem("Yesterday");
        r.setGroup("radios");
        r.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                attributeInfo = "Yesterday";
                Date yesterday = new Date();
                yesterday.setDate(yesterday.getDate()-1);
                onDate = yesterday;
                applyDefaultsAction();
            }
        });
        radioMenu.add(r);

        r = new CheckMenuItem("Last Week");
        r.setGroup("radios");
        r.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                attributeInfo = "Last Week";
                Date previousWeekStart = new Date();
                previousWeekStart.setDate(previousWeekStart.getDate()-7);
                beginDate = previousWeekStart;
                endDate = new Date();
                onDate = null;

                applyDefaultsAction();
            }
        });
        radioMenu.add(r);

        r = new CheckMenuItem("Last Month");
        r.setGroup("radios");
        r.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                attributeInfo = "Last Month";
                Date previousMonthStart = new Date();
                previousMonthStart.setDate(previousMonthStart.getDate()-30);
                beginDate = previousMonthStart;
                endDate = new Date();
                onDate = null;
                applyDefaultsAction();
            }
        });
        radioMenu.add(r);

        radios.setSubMenu(radioMenu);
        return radios;
    }

    public void applyDefaultsAction(){
//        clearAttributeDate();
//        clearEmptyTime();
        updateAttributeInfo();
    }

    public void resetDefaultsMenu(){
        attributeInfo = "";
        onDate = null;
        beginDate = null;
        endDate = null;
        for(Component item : radioMenu.getItems()){
            if(item instanceof CheckMenuItem)
                ((CheckMenuItem) item).setChecked(false);
        }
    }

    public void updateFilterData(){}

    private void setAttributeEndDate(Date date){
        resetDefaultsMenu();
        endDate = date;
        updateAttributeInfo();
    }

    private void setAttributeBeginDate(Date date){
        resetDefaultsMenu();
        beginDate = date;
        updateAttributeInfo();
    }

    private void setAttributeOnDate(Date date){
        resetDefaultsMenu();
        onDate = date;
        updateAttributeInfo();
    }

    private void clearAttributeDate(){
        if(greaterThenField.getValue() == null)
            setAttributeBeginDate(null);
        if(smallerThenField.getValue() == null)
            setAttributeEndDate(null);
        if(equalToField.getValue() == null)
            setAttributeOnDate(null);
    }

    private void clearInterval(){
        greaterThenField.clear();
        setAttributeBeginDate(null);
        smallerThenField.clear();
        setAttributeEndDate(null);
    }

    private String createDateInfoString(){
        String result = "";
        Date equal = equalToField.getValue();
        Date smaller = greaterThenField.getValue();
        Date greater = smallerThenField.getValue();

        if(equal != null){
            DateWrapper dateWrapper = new DateWrapper(equal);
            int month = dateWrapper.getMonth();
            month += 1;
            result = "= " + dateWrapper.getDate() + "/" + month + "/" + dateWrapper.getFullYear();
        }else{
            if(smaller != null && greater != null)
            {
                DateWrapper dateWrapperSmaller = new DateWrapper(smaller);
                int monthSmall = dateWrapperSmaller.getMonth();
                monthSmall += 1;
                DateWrapper dateWrapperGreater = new DateWrapper(greater);
                int monthGreat = dateWrapperGreater.getMonth();
                monthGreat += 1;
                result = "[" + dateWrapperSmaller.getFullYear() + "/" + monthSmall + "/" + dateWrapperSmaller.getDate() + "-"
                        + dateWrapperGreater.getFullYear() + "/" + monthGreat + "/" + dateWrapperGreater.getDate() + "]";
            }else {
                if(greater != null) {
                    DateWrapper dateWrapper = new DateWrapper(greater);
                    int month = dateWrapper.getMonth();
                    month += 1;
                    result = "<" + dateWrapper.getFullYear() + "/" + month + "/" + dateWrapper.getDate();
                }
                else if(smaller != null) {
                    DateWrapper dateWrapper = new DateWrapper(smaller);
                    int month = dateWrapper.getMonth();
                    month += 1;
                    result = ">" + dateWrapper.getFullYear() + "/" + month + "/" + dateWrapper.getDate();
                }
            }
        }

        return result;
    }

    private void checkDateInterval(){
        if(smallerThenField.getValue() != null && greaterThenField.getValue() != null){
            if(smallerThenField.getValue().before(greaterThenField.getValue())){
                greaterThenField.clear();
                setAttributeBeginDate(null);
            }
        }
    }

    private void updateDateResults(){
        checkDateInterval();
//        if(dataFilter.getChecked() == 1)
//            Dispatcher.get().dispatch(AppEvents.LoadMainData);
    }

    /*
     *  Time Part of the filter
     */

    private MenuItem createTimeMenu(){
        MenuItem time = new MenuItem(HarvesterUI.CONSTANTS.time());
        Menu timeMenu = new Menu();

        Validator hourValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(Integer.parseInt(s) > 24 || Integer.parseInt(s) < 0)
                    return HarvesterUI.CONSTANTS.invalidHour();
                return null;
            }
        };

        Validator minuteValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(Integer.parseInt(s) > 59 || Integer.parseInt(s) < 0)
                    return HarvesterUI.CONSTANTS.invalidMinute();
                return null;
            }
        };

        FormPanel greaterThanPanel = new FormPanel();
        greaterThanPanel.setHeaderVisible(false);
        greaterThanPanel.setWidth(140);

        afterTimeHourField.setAutoValidate(true);
        afterTimeHourField.setAllowDecimals(false);
        afterTimeHourField.setValidator(hourValidator);
        afterTimeHourField.setFieldLabel(HarvesterUI.CONSTANTS.hours());

        afterTimeMinuteField.setAutoValidate(true);
        afterTimeMinuteField.setAllowDecimals(false);
        afterTimeMinuteField.setValidator(minuteValidator);
        afterTimeMinuteField.setFieldLabel(HarvesterUI.CONSTANTS.minutes());

        MenuItem greater = new MenuItem();
        greater.setIcon(HarvesterUI.ICONS.greater_than());
        greater.setCanActivate(false);
        greater.setHideOnClick(false);

        greaterThanPanel.add(afterTimeHourField,formData);
        greaterThanPanel.add(afterTimeMinuteField, formData);
        greater.setWidget(greaterThanPanel);
        timeMenu.add(greater);

        MenuItem lesser = new MenuItem();
        lesser.setIcon(HarvesterUI.ICONS.lesser_than());
        lesser.setCanActivate(false);
        lesser.setHideOnClick(false);

        FormPanel lesserThanPanel = new FormPanel();
        lesserThanPanel.setHeaderVisible(false);
        lesserThanPanel.setWidth(140);

        beforeTimeHourField.setAutoValidate(true);
        beforeTimeHourField.setAllowDecimals(false);
        beforeTimeHourField.setValidator(hourValidator);
        beforeTimeHourField.setFieldLabel(HarvesterUI.CONSTANTS.hours());

        beforeTimeMinuteField.setAutoValidate(true);
        beforeTimeMinuteField.setAllowDecimals(false);
        beforeTimeMinuteField.setValidator(minuteValidator);
        beforeTimeMinuteField.setFieldLabel(HarvesterUI.CONSTANTS.minutes());

        lesserThanPanel.add(beforeTimeHourField, formData);
        lesserThanPanel.add(beforeTimeMinuteField, formData);
        lesser.setWidget(lesserThanPanel);
        timeMenu.add(lesser);

//        MenuItem filter = new MenuItem(HarvesterUI.CONSTANTS.filter());
//        filter.setIcon(HarvesterUI.ICONS.table());
//        filter.setHideOnClick(false);
//        filter.addSelectionListener(new SelectionListener<MenuEvent>() {
//            @Override
//            public void componentSelected(MenuEvent menuEvent) {
//                if(beforeTimeHourField.getValue() != null && beforeTimeMinuteField != null){
//                    setAttributeEndTime(beforeTimeHourField,beforeTimeMinuteField);
//                }
//                if(afterTimeHourField.getValue() != null && afterTimeMinuteField != null){
//                    setAttributeBeginTime(afterTimeHourField, afterTimeMinuteField);
//                }
//                checkTimeInterval();
//                clearEmptyTime();
////                if(dataFilter.getChecked() == 1)
////                    Dispatcher.get().dispatch(AppEvents.LoadMainData);
//            }
//        });
//        timeMenu.add(filter);

        MenuItem reset = new MenuItem(HarvesterUI.CONSTANTS.reset());
        reset.setIcon(HarvesterUI.ICONS.delete());
        reset.setHideOnClick(false);

        reset.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent menuEvent) {
                beforeTimeHourField.setValue(null);
                beforeTimeMinuteField.setValue(null);
                afterTimeHourField.setValue(null);
                afterTimeMinuteField.setValue(null);
                clearEmptyTime();
//                if(dataFilter.getChecked() == 1)
//                    Dispatcher.get().dispatch(AppEvents.LoadMainData);
            }
        });
        timeMenu.add(reset);

        time.setSubMenu(timeMenu);

        return time;
    }

    private void setAttributeEndTime(NumberField hour, NumberField min){
        endTime = createTime(hour,min);
        resetDefaultsMenu();
        updateAttributeInfo();
    }

    private void setAttributeBeginTime(NumberField hour, NumberField min){
        beginTime = createTime(hour,min);
        resetDefaultsMenu();
        updateAttributeInfo();
    }

    @SuppressWarnings("deprecation")
    private Date createTime(NumberField hours, NumberField minutes){
        int hour, minute;
        hour = hours.getValue().intValue();
        minute = minutes.getValue().intValue();

        return new Date(2011,11,11,hour,minute);
    }

    private String createTimeInfoString(){
        String result = "";

        if(beforeTimeHourField.getValue() != null && beforeTimeMinuteField.getValue() != null &&
                afterTimeHourField.getValue() != null && afterTimeMinuteField.getValue() != null){
            result = "[" + getFormatedTime(afterTimeHourField.getValue().intValue()) + ":"
                    + getFormatedTime(afterTimeMinuteField.getValue().intValue()) + "-" +
                    getFormatedTime((beforeTimeHourField.getValue().intValue())) + ":"
                    + getFormatedTime(beforeTimeMinuteField.getValue().intValue()) + "]";
        }else{
            if(beforeTimeHourField.getValue() != null && beforeTimeMinuteField.getValue() != null){
                result = "<" + getFormatedTime(beforeTimeHourField.getValue().intValue()) + ":"
                        + getFormatedTime(beforeTimeMinuteField.getValue().intValue());
            }
            if(afterTimeHourField.getValue() != null && afterTimeMinuteField.getValue() != null){
                result = ">" + getFormatedTime(afterTimeHourField.getValue().intValue()) + ":"
                        + getFormatedTime(afterTimeMinuteField.getValue().intValue());
            }
        }
        return result;
    }

    private void checkTimeInterval(){
        if(beforeTimeHourField.getValue() != null && beforeTimeMinuteField.getValue() != null &&
                afterTimeHourField.getValue() != null && afterTimeMinuteField.getValue() != null){
            if(afterTimeHourField.getValue().intValue() > beforeTimeHourField.getValue().intValue()){
                beforeTimeHourField.clear();
                beforeTimeMinuteField.clear();
            }
        }
    }

    private void clearEmptyTime(){
        if(afterTimeHourField.getValue() == null || afterTimeMinuteField == null)
            beginTime = null;
        if(beforeTimeHourField.getValue() == null || beforeTimeMinuteField == null)
            endTime = null;

        updateAttributeInfo();
    }

    private String getFormatedTime(int time){
        String timeStr = "" + time;
        if(time < 10)
            return timeStr = "0" + time;
        return  timeStr;
    }

    public FilterQueryLastIngest getFilterQuery(){
        return new FilterQueryLastIngest(FilterType.LAST_INGEST,beginDate, endDate,onDate,beginTime,endTime);
    }
}
