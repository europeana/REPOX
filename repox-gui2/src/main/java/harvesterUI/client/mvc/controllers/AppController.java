/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.AppView;
import harvesterUI.client.panels.administration.userManagement.LoginDialog;
import harvesterUI.client.util.CookieManager;
import harvesterUI.shared.users.UserRole;

public class AppController extends Controller {

    private AppView appView;

    public AppController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.Login);
        registerEventTypes(AppEvents.Logout);
        registerEventTypes(AppEvents.ViewAccordingToRole);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            onInit(event);
        } else if (type == AppEvents.Login) {
            onLogin(event);
        } else if (type == AppEvents.Logout) {
            onLogout(event);
        }else if (type == AppEvents.ViewAccordingToRole) {
            onRole(event);
        }
    }

    public void initialize() {
        appView = new AppView(this);
        Registry.register("appView", appView);
    }

    private void onInit(AppEvent event) {
        forwardToView(appView, event);
    }

    private void onLogin(AppEvent event) {
        LoginDialog dialog = new LoginDialog();
        dialog.setClosable(false);
        dialog.show();
        forwardToView(appView, event);
    }

    private void onLogout(AppEvent event) {
        CookieManager cookieManager = (CookieManager) Registry.get(HarvesterUI.COOKIE_MANAGER);
        cookieManager.removeAllLoginCookies();
        History.newItem("",false);
        Window.Location.reload();
    }

    private void onRole(AppEvent event) {
        UserRole role = HarvesterUI.UTIL_MANAGER.getLoggedUserRole();
        appView.changeAccordingToRole(role);
    }
}
