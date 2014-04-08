package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.OaiTestView;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 20:32
 */
public class OaiTestController extends Controller{

    private OaiTestView oaiTestView;

    public OaiTestController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.ViewOAITest);
        registerEventTypes(AppEvents.ViewRestRecordOperations);
        registerEventTypes(AppEvents.ViewOAISpecificSet);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            onInit(event);
        } else if (type == AppEvents.ViewOAITest
                || type == AppEvents.ViewRestRecordOperations
                || type == AppEvents.ViewOAISpecificSet) {
            forwardToView(oaiTestView, event);
        }
    }

    public void initialize() {
        oaiTestView = new OaiTestView(this);
    }

    private void onInit(AppEvent event) {
        forwardToView(oaiTestView, event);
    }
}
