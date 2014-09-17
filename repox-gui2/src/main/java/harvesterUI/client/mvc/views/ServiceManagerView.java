package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.services.ServiceManagementPanel;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 20:33
 */
public class ServiceManagerView extends View {

    private ServiceManagementPanel serviceManagementPanel;

    public ServiceManagerView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event){
        if (event.getType() == AppEvents.ViewServiceManager) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();

            centerPanel.add(serviceManagementPanel);
            serviceManagementPanel.reloadExternalServices();
            centerPanel.layout();
        }
    }

    @Override
    protected void initialize(){
        serviceManagementPanel = new ServiceManagementPanel();
    }

}
