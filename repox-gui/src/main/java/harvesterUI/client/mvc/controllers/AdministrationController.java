package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.AdministrationView;
import harvesterUI.client.servlets.RepoxServiceAsync;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 12:48
 */
public class AdministrationController extends Controller {

    private AdministrationView administrationView;
    private RepoxServiceAsync service;

    public AdministrationController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.ViewUserManagementForm);
        registerEventTypes(AppEvents.ViewAdminForm);
        registerEventTypes(AppEvents.ReloadUsers);
        registerEventTypes(AppEvents.ViewTagsManager);
        registerEventTypes(AppEvents.ReloadTags);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            onInit(event);
        } else if (type == AppEvents.ViewUserManagementForm) {
            forwardToView(administrationView, event);
        } else if (type == AppEvents.ViewAdminForm ||
                type == AppEvents.ViewTagsManager ||
                type == AppEvents.ReloadTags ||
                type == AppEvents.ReloadUsers) {
            forwardToView(administrationView, event);
        }
    }

    public void initialize() {
        administrationView = new AdministrationView(this);
    }

    private void onInit(AppEvent event) {
        forwardToView(administrationView, event);
    }
}
