package harvesterUI.client.panels.services;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.externalServices.ESManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.externalServices.ExternalServiceUI;

import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 24-11-2011
 * Time: 17:41
 */
public class ServicesOperations {

    protected Button removeServiceButton, editServiceButton;
    protected SeparatorToolItem lastSeparator;

    public ServicesOperations(final Grid<ExternalServiceUI> servicesGrid) {

        final SelectionListener<ButtonEvent> removeServiceListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent ce) {
                AsyncCallback<String> callback = new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(String result) {
                        Dispatcher.forwardEvent(AppEvents.ViewServiceManager);
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteServices(), HarvesterUI.CONSTANTS.deleteServiceSuccess());
                    }
                };
                List<ExternalServiceUI> selected = servicesGrid.getSelectionModel().getSelectedItems();
                ESManagementServiceAsync esManagementServiceAsync = (ESManagementServiceAsync) Registry.get(HarvesterUI.ES_MANAGEMENT_SERVICE);
                esManagementServiceAsync.removeExternalService(selected, callback);
            }
        };

        removeServiceButton = new Button();
        removeServiceButton.setText(HarvesterUI.CONSTANTS.removeExternalService());
        removeServiceButton.setIcon(HarvesterUI.ICONS.delete());
        removeServiceButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteServiceConfirmMessage(), removeServiceListener);
            }
        });

        editServiceButton = new Button();
        editServiceButton.setText(HarvesterUI.CONSTANTS.editExternalService());
        editServiceButton.setIcon(HarvesterUI.ICONS.operation_edit());
        editServiceButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        if (servicesGrid.getSelectionModel().getSelectedItems().size() > 0) {
                            ExternalServiceUI selected = servicesGrid.getSelectionModel().getSelectedItems().get(0);
                            NewServiceDialog newServiceDialog = new NewServiceDialog();
                            newServiceDialog.show();
                            newServiceDialog.edit(selected);
                        }
                    }
                });
            }
        });

        lastSeparator = new SeparatorToolItem();
    }

    public void hideTransformationButtons(ToolBar toolBar) {
        if(toolBar.getItems().contains(removeServiceButton)) {
            removeServiceButton.removeFromParent();
            editServiceButton.removeFromParent();
            lastSeparator.removeFromParent();
        }
    }

    public void showTransformationButtons(ToolBar toolBar) {
        if(!toolBar.getItems().contains(removeServiceButton)) {
            toolBar.insert(lastSeparator,1);
            toolBar.insert(editServiceButton,2);
            toolBar.insert(removeServiceButton,3);
        }
    }
}
