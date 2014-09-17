package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.AccountEditView;
import harvesterUI.client.servlets.RepoxServiceAsync;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 20:14
 */
public class AccountEditController extends Controller {

    private AccountEditView accountEditView;

    public AccountEditController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.ViewAccountEditForm);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            onInit(event);
        } else if (type == AppEvents.ViewAccountEditForm) {
            forwardToView(accountEditView, event);
        }
    }

    public void initialize() {
        accountEditView = new AccountEditView(this);
    }

    private void onInit(AppEvent event) {
        forwardToView(accountEditView, event);
    }
}
