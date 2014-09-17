package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.administration.userManagement.AccountEditPanel;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 20:15
 */
public class AccountEditView extends View {

    private AccountEditPanel accountEditPanel;

    public AccountEditView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event){
        if (event.getType() == AppEvents.ViewAccountEditForm) {
            accountEditPanel.edit();
            accountEditPanel.showAndCenter();
        }
    }

    @Override
    protected void initialize(){
        accountEditPanel = new AccountEditPanel();
    }
}
