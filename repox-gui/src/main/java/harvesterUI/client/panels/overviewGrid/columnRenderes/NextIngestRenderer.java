package harvesterUI.client.panels.overviewGrid.columnRenderes;

import com.bradrydzewski.gwt.calendar.client.CalendarViews;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.harvesting.calendar.CalendarTaskManager;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import java.util.Date;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 23-02-2012
 * Time: 16:06
 */
public class NextIngestRenderer implements GridCellRenderer<ModelData> {

    public NextIngestRenderer() {
    }

    public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                         ListStore<ModelData> store, Grid<ModelData> grid) {
        final CalendarTaskManager calendarTaskManager =
                (CalendarTaskManager) Registry.get("calendarTaskManager");

        if(model instanceof DataSourceUI) {
            DataSourceUI dataSourceUI = (DataSourceUI) model;

            if(dataSourceUI.getNextIngest() != null) {
                final String schedule = dataSourceUI.getNextIngest();
                if(!schedule.equals("")) {
                    String delimDateTime = "[ ]";
                    String[] tokensDateTime = schedule.split(delimDateTime);
                    String nextDate = tokensDateTime[0];
                    String nextTime = tokensDateTime[1];

                    // Only show links for logged in users
                    if(HarvesterUI.UTIL_MANAGER.getLoggedUserName().equals("Anonymous")){
                        return nextDate +"&nbsp &nbsp"+nextTime;
                    }

                    String date = "<span style='color:blue'>" + nextDate + "</span>";
                    String time = "<span style='color:blue'>" + nextTime + "</span>";

                    LabelToolItem dateLabel = new LabelToolItem(date);
                    LabelToolItem timeLabel = new LabelToolItem(time);
                    dateLabel.addListener(Events.OnClick,new Listener<BoxComponentEvent>() {
                        public void handleEvent(BoxComponentEvent be) {
                            DatePicker dp = (DatePicker) calendarTaskManager.getDpDialog().getItem(0);
                            DateTimeFormat formatter = DateTimeFormat.getFormat("yyyy-MM-dd  HH:mm");
                            Date taskDate = formatter.parse(schedule);
                            dp.setValue(taskDate);
                            calendarTaskManager.getCalendarPanel().getCalendar().setView(CalendarViews.MONTH);
                            Dispatcher.get().dispatch(AppEvents.ViewScheduledTasksCalendar);
                        }
                    });
                    timeLabel.addListener(Events.OnClick,new Listener<BoxComponentEvent>() {
                        public void handleEvent(BoxComponentEvent be) {
                            DatePicker dp = (DatePicker) calendarTaskManager.getDpDialog().getItem(0);
                            DateTimeFormat formatter = DateTimeFormat.getFormat("yyyy-MM-dd  HH:mm");
                            Date taskDate = formatter.parse(schedule);
                            dp.setValue(taskDate);
                            calendarTaskManager.getCalendarPanel().getCalendar().setView(CalendarViews.DAY,1);
                            Dispatcher.get().dispatch(AppEvents.ViewScheduledTasksCalendar);
                            calendarTaskManager.getCalendarPanel().getCalendar().scrollToHour(taskDate.getHours());
                        }
                    });
                    dateLabel.setStyleName("hyperlink_style_label");
                    timeLabel.setStyleName("hyperlink_style_label");

                    dateLabel.setStyleAttribute("float", "left");
//                    timeLabel.setStyleAttribute("float", "left");

                    LayoutContainer dateTimeContainer = new LayoutContainer();
                    FlowLayout layout = new FlowLayout();
                    dateTimeContainer.setLayout(layout);
                    dateTimeContainer.add(dateLabel, new FlowData(new Margins(0,5,0,0)));
                    dateTimeContainer.add(timeLabel);
                    return dateTimeContainer;

                }
            }
            return "";
        }
        else
            return "";
    }
}
