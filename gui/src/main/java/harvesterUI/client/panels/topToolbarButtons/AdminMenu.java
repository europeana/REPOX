package harvesterUI.client.panels.topToolbarButtons;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.util.WidgetWithRole;
import harvesterUI.shared.users.UserRole;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 21-02-2012
 * Time: 19:16
 */
public class AdminMenu extends WidgetWithRole{

    public AdminMenu(ToolBar toolBar) {
        if(drawWidget){
            toolBar.add(new SeparatorToolItem());

            Button adminData = new Button(HarvesterUI.CONSTANTS.administration(), HarvesterUI.ICONS.admin_menu_icon());
            Menu adminMenu = new Menu();
            MenuItem configSettings = new MenuItem(HarvesterUI.CONSTANTS.configurationSettings(),HarvesterUI.ICONS.config_properties());
            configSettings.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent me) {
                    Dispatcher.get().dispatch(AppEvents.ViewAdminForm);
                }
            });
            adminMenu.add(configSettings);
            MenuItem userManagement = new MenuItem(HarvesterUI.CONSTANTS.userManagement(),HarvesterUI.ICONS.user_manage_icon());
            userManagement.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent me) {
                    Dispatcher.get().dispatch(AppEvents.ViewUserManagementForm);
                }
            });
            adminMenu.add(userManagement);
            MenuItem externalServices = new MenuItem(HarvesterUI.CONSTANTS.externalServicesManager());
            externalServices.setIcon(HarvesterUI.ICONS.externalServicesIcon());
            externalServices.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent me) {
                    Dispatcher.get().dispatch(AppEvents.ViewServiceManager);
                }
            });
            adminMenu.add(externalServices);
//            MenuItem restServices = new MenuItem(HarvesterUI.CONSTANTS.restServices());
//            restServices.setIcon(HarvesterUI.ICONS.web_services_icon());
//            restServices.addSelectionListener(new SelectionListener<MenuEvent>() {
//                @Override
//                public void componentSelected(MenuEvent me) {
//                    Dispatcher.get().dispatch(AppEvents.ViewRestRecordOperations);
//                }
//            });
//            adminMenu.add(restServices);

            MenuItem tagsManager = new MenuItem("Tags Manager");
            tagsManager.setIcon(HarvesterUI.ICONS.tag_icon());
            tagsManager.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent me) {
                    Dispatcher.get().dispatch(AppEvents.ViewTagsManager);
                }
            });
            adminMenu.add(tagsManager);

            adminData.setMenu(adminMenu);
            toolBar.add(adminData);
        }
    }

    public void checkRole(){
        drawWidget = HarvesterUI.UTIL_MANAGER.getLoggedUserRole() == UserRole.ADMIN;
    }
}
