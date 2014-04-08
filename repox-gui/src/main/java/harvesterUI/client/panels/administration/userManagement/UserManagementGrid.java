package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.paging.ListPagingToolBar;
import harvesterUI.shared.users.User;
import harvesterUI.shared.users.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 12:51
 */
public class UserManagementGrid extends ContentPanel{

    private UserManagementServiceAsync service;
    private Grid<User> grid;
    private PagingLoader<PagingLoadResult<User>> loader;
    private ToolBar topToolbar;

    public UserManagementGrid() {
        setHeading(HarvesterUI.CONSTANTS.userManagement());
        setFrame(true);
        setIcon(HarvesterUI.ICONS.user_manage_icon());
        setLayout(new FitLayout());
        service = (UserManagementServiceAsync) Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);

        createMainGrid();

        topToolbar = new ToolBar();
        setTopComponent(topToolbar);

        UserRole userRole = HarvesterUI.UTIL_MANAGER.getLoggedUserRole();
        if(userRole == UserRole.ADMIN || userRole == UserRole.NORMAL){
            Button add = new Button(HarvesterUI.CONSTANTS.addUser());
            add.setIcon(HarvesterUI.ICONS.add16());
            add.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    new SaveUserDialog().showAndCenter();
                }

            });
            topToolbar.insert(add, 0);

            final UsersOperations usersOperations = new UsersOperations(grid);
            grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<User>() {
                @Override
                public void selectionChanged(SelectionChangedEvent<User> se) {
                    if (se.getSelectedItem() != null) {
                        usersOperations.showTagButtons(topToolbar);
                    } else {
                        usersOperations.hideTagButtons(topToolbar);
                    }
                }
            });
        }
    }

    private void createMainGrid(){
        RpcProxy<PagingLoadResult<User>> proxy = new RpcProxy<PagingLoadResult<User>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<User>> callback) {
                service.getPagedUsers((PagingLoadConfig) loadConfig, callback);
            }
        };

        loader = new BasePagingLoader<PagingLoadResult<User>>(proxy);
        loader.setRemoteSort(true);

        ListStore<User> store = new ListStore<User>(loader);

        ListPagingToolBar toolBar = new ListPagingToolBar(25);
        toolBar.bind(loader);

        setBottomComponent(toolBar);

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<User> sm = new CheckBoxSelectionModel<User>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        ColumnConfig column = new ColumnConfig("userName",HarvesterUI.CONSTANTS.username(),150);
        configs.add(column);

        column = new ColumnConfig("mail",HarvesterUI.CONSTANTS.email(),150);
        configs.add(column);

        column = new ColumnConfig("role",HarvesterUI.CONSTANTS.role(),130);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        grid = new Grid<User>(store, cm);
        grid.getView().setForceFit(true);
        grid.setAutoExpandColumn("userName");
        grid.setBorders(true);
        grid.setSelectionModel(sm);
        grid.addPlugin(sm);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noUsersAvailable());
        grid.addListener(Events.Attach, new Listener<GridEvent<User>>() {
            public void handleEvent(GridEvent<User> be) {
                loadData();
            }
        });
        add(grid);

        createContextMenus();
    }

    private void createContextMenus(){
        Menu contextMenu = new Menu();

        MenuItem updateUser = new MenuItem();
        updateUser.setText(HarvesterUI.CONSTANTS.updateUser());
        updateUser.setIcon(HarvesterUI.ICONS.user_add());
        updateUser.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                if(grid.getSelectionModel().getSelectedItems().size() > 0) {
                    User selected = grid.getSelectionModel().getSelectedItems().get(0);
                    new SaveUserDialog(selected).showAndCenter();
                }
            }
        });
        contextMenu.add(updateUser);

        MenuItem remove = new MenuItem();
        remove.setText(HarvesterUI.CONSTANTS.removeUsers());
        remove.setIcon(HarvesterUI.ICONS.user_delete());
        remove.addSelectionListener(new SelectionListener<MenuEvent>() {
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
            public void componentSelected(MenuEvent ce) {
                if(grid.getSelectionModel().getSelectedItems().size() > 0)
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteUserConfirmMessage(), userRemoveListener);
            }

        });
        contextMenu.add(remove);
        grid.setContextMenu(contextMenu);
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
        service.removeUsers(users, callback);
    }

    public void loadData(){
        loader.load(0,25);
    }
}
