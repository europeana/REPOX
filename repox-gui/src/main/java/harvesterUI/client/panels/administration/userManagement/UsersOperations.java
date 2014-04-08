package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.users.User;

import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 24-11-2011
 * Time: 17:41
 */
public class UsersOperations {

    protected Button removeUserButton, editUserButton;
    protected SeparatorToolItem lastSeparator;

    public UsersOperations(final Grid<User> grid) {
        removeUserButton = new Button();
        removeUserButton.setText(HarvesterUI.CONSTANTS.delete());
        removeUserButton.setIcon(HarvesterUI.ICONS.delete());
        removeUserButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            final SelectionListener<ButtonEvent> userRemoveListener = new SelectionListener<ButtonEvent> () {
                public void componentSelected(ButtonEvent ce) {
                    List<User> selected = grid.getSelectionModel().getSelectedItems();
                    if(checkIfSelfSelected(selected))
                        HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.deleteUsers(),HarvesterUI.CONSTANTS.cannotRemoveSelf());
                    else
                        removeUsers(selected);
                }
            };

            @Override
            public void componentSelected(ButtonEvent ce) {
                if(grid.getSelectionModel().getSelectedItems().size() > 0)
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteUserConfirmMessage(), userRemoveListener);
            }
        });

        editUserButton = new Button();
        editUserButton.setText(HarvesterUI.CONSTANTS.edit());
        editUserButton.setIcon(HarvesterUI.ICONS.operation_edit());
        editUserButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                if(grid.getSelectionModel().getSelectedItems().size() > 0) {
                    User selected = grid.getSelectionModel().getSelectedItems().get(0);
                    new SaveUserDialog(selected).showAndCenter();
                }
            }
        });

        lastSeparator = new SeparatorToolItem();
    }

    private boolean checkIfSelfSelected(List<User> selectedUsers){
        for(User user : selectedUsers){
            if(user.getUserName().equals(HarvesterUI.UTIL_MANAGER.getLoggedUserName()))
                return true;
        }
        return false;
    }

    private void removeUsers(List<User> users){
        AsyncCallback callback = new AsyncCallback() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(Object result) {
                HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteUsers(), HarvesterUI.CONSTANTS.usersDeleted());
                Dispatcher.forwardEvent(AppEvents.ReloadUsers);
            }
        };
        UserManagementServiceAsync service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        service.removeUsers(users, callback);
    }

    public void hideTagButtons(ToolBar toolBar) {
        if(toolBar.getItems().contains(removeUserButton)) {
            removeUserButton.removeFromParent();
            editUserButton.removeFromParent();
            lastSeparator.removeFromParent();
        }
    }

    public void showTagButtons(ToolBar toolBar) {
        if(!toolBar.getItems().contains(removeUserButton)) {
            toolBar.insert(lastSeparator,1);
            toolBar.insert(editUserButton,2);
            toolBar.insert(removeUserButton,3);
        }
    }

}
