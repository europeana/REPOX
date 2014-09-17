package harvesterUI.client.mvc.controllers;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-02-2011
 * Time: 18:27
 */

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.BrowseView;

//import harvesterUI.client.parsing.ParsingManagerClient;

//import harvesterUI.client.models.FilterAttributes;

public class BrowseController extends Controller {

    private BrowseView browseView;

    public BrowseController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.LoadMainData);
        registerEventTypes(AppEvents.RemoveGridOperations);
        registerEventTypes(AppEvents.ResetFilter);
        registerEventTypes(AppEvents.ViewMoveDataProviderDialog);
        registerEventTypes(AppEvents.ViewMoveDataSetDialog);
        registerEventTypes(AppEvents.AutoRefreshData);
    }

    @Override
    public void initialize() {
        super.initialize();
        browseView = new BrowseView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            forwardToView(browseView, event);
        }else if (type == AppEvents.LoadMainData) {
            forwardToView(browseView, event);
        }
        else if (type == AppEvents.ResetFilter
                || type == AppEvents.ViewMoveDataProviderDialog
                || type == AppEvents.ViewMoveDataSetDialog
                || type == AppEvents.AutoRefreshData
                || type == AppEvents.RemoveGridOperations
                ) {
            forwardToView(browseView, event);
        }
    }
}
