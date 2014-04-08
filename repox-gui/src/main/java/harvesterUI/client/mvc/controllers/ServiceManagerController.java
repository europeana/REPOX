package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.ServiceManagerView;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 20:32
 */
public class ServiceManagerController extends Controller{

    private ServiceManagerView serviceManagerView;

    public ServiceManagerController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.ViewServiceManager);
        registerEventTypes(AppEvents.CreateService);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            onInit(event);
        } else if (type == AppEvents.ViewServiceManager
                || type == AppEvents.CreateService) {
            forwardToView(serviceManagerView, event);
        }
    }

    public void initialize() {
        serviceManagerView = new ServiceManagerView(this);
    }

    private void onInit(AppEvent event) {
        forwardToView(serviceManagerView, event);
    }
}
