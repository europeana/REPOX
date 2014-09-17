package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.DataSetView;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 21-03-2011
 * Time: 16:34
 */
public class DataSetController extends Controller{

    private DataSetView dataSetView;

    public DataSetController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.ViewDataSetInfo);
    }

    @Override
    public void initialize() {
        super.initialize();
        dataSetView = new DataSetView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            forwardToView(dataSetView, event);
        }else if (type == AppEvents.ViewDataSetInfo){
            forwardToView(dataSetView,event);
        }
    }
}