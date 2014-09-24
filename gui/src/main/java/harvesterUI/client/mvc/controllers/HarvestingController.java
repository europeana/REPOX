package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.HarvestingView;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 23-03-2011
 * Time: 12:23
 */
public class HarvestingController extends Controller {

    private HarvestingView harvestingView;

    public HarvestingController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.ViewScheduledTasksCalendar);
        registerEventTypes(AppEvents.ViewScheduledTasksList);
        registerEventTypes(AppEvents.ViewRunningTasksList);
    }

    @Override
    public void initialize() {
        super.initialize();
        harvestingView = new HarvestingView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            forwardToView(harvestingView, event);
        }
        else if (type == AppEvents.ViewScheduledTasksCalendar ||
                type == AppEvents.ViewScheduledTasksList ||
                type == AppEvents.ViewRunningTasksList)
        {
            forwardToView(harvestingView,event);
        }
    }
}
