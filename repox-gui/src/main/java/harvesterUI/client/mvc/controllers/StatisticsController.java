package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.HarvestingView;
import harvesterUI.client.mvc.views.StatisticsView;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 26-03-2011
 * Time: 17:36
 */
public class StatisticsController extends Controller {

    private StatisticsView statisticsView;

    public StatisticsController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.ViewStatistics);
    }

    @Override
    public void initialize() {
        super.initialize();
        statisticsView = new StatisticsView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            forwardToView(statisticsView, event);
        }else if (type == AppEvents.ViewStatistics){
            forwardToView(statisticsView,event);
        }
    }
}
