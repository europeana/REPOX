package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.RssView;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 20:14
 */
public class RssController extends Controller {

    private RssView rssView;

    public RssController() {
        registerEventTypes(AppEvents.ViewRssFeedPanel);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.ViewRssFeedPanel) {
            forwardToView(rssView,event);
        }
    }

    public void initialize() {
        rssView = new RssView(this);
    }

    private void onInit(AppEvent event) {
//        forwardToView(accountEditView, event);
    }
}
