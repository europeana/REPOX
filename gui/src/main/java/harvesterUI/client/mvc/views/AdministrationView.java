package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.administration.AdminForm;
import harvesterUI.client.panels.administration.userManagement.UserManagementGrid;
import harvesterUI.client.panels.tags.TagManagementPanel;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 12:50
 */
public class AdministrationView extends View {

    private UserManagementGrid userManagementGrid;
    private TagManagementPanel tagManagementPanel;
    private AdminForm adminForm;

    public AdministrationView(Controller controller) {
        super(controller);
        adminForm = new AdminForm();
    }

    @Override
    protected void handleEvent(AppEvent event){
        if (event.getType() == AppEvents.ViewUserManagementForm) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();
            centerPanel.add(userManagementGrid);
            centerPanel.layout();
        } else if (event.getType() == AppEvents.ViewAdminForm) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();
            adminForm.editAdminForm();
            centerPanel.add(adminForm);
            centerPanel.layout();
        } else if (event.getType() == AppEvents.ViewTagsManager) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();
            centerPanel.add(tagManagementPanel);
            centerPanel.layout();
        }else if (event.getType() == AppEvents.ReloadTags) {
            if(tagManagementPanel.isVisible())
                tagManagementPanel.loadData();
        }else if (event.getType() == AppEvents.ReloadUsers) {
            if(userManagementGrid.isVisible())
                userManagementGrid.loadData();
        }
    }

    @Override
    protected void initialize(){
        userManagementGrid = new UserManagementGrid();
        tagManagementPanel = new TagManagementPanel();
    }
}
