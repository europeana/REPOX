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
 * Date: 24-02-2012
 * Time: 9:55
 */
public class HarvestTopMenu extends WidgetWithRole{

    public HarvestTopMenu(ToolBar toolBar) {
        if(drawWidget){
            toolBar.add(new SeparatorToolItem());
            Button harv = new Button(HarvesterUI.CONSTANTS.harvesting(), HarvesterUI.ICONS.harvesting_menu_icon());
            harv.setId("HARVEST_MENU");
            toolBar.add(harv);
            Menu harvMenu = new Menu();
            MenuItem scheduled = new MenuItem(HarvesterUI.CONSTANTS.scheduledTasksCalendar());
            scheduled.setIcon(HarvesterUI.ICONS.calendar());
            scheduled.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent me) {
                    Dispatcher.forwardEvent(AppEvents.ViewScheduledTasksCalendar);
                }
            });
            harvMenu.add(scheduled);
            MenuItem scheduledList = new MenuItem(HarvesterUI.CONSTANTS.scheduledTasksList());
            scheduledList.setIcon(HarvesterUI.ICONS.table());
            scheduledList.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent me) {
                    Dispatcher.forwardEvent(AppEvents.ViewScheduledTasksList);
                }
            });
            harvMenu.add(scheduledList);
            MenuItem running = new MenuItem(HarvesterUI.CONSTANTS.runningTasks());
            running.setIcon(HarvesterUI.ICONS.running_tasks_icon());
            running.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent me) {
                    Dispatcher.forwardEvent(AppEvents.ViewRunningTasksList);
                }
            });
            harvMenu.add(running);
            harv.setMenu(harvMenu);
        }
    }

    public void checkRole(){
        drawWidget = HarvesterUI.UTIL_MANAGER.getLoggedUserRole() != UserRole.ANONYMOUS;
    }
}
