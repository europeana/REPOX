package harvesterUI.client.panels.tags;

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
import harvesterUI.client.servlets.dataManagement.TagsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 24-11-2011
 * Time: 17:41
 */
public class TagsOperations {

    protected Button removeTagButton,editTagButton;
    protected SeparatorToolItem lastSeparator;

    public TagsOperations(final Grid<DataSetTagUI> mainTree) {
        final SelectionListener<ButtonEvent> removeTagsListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent ce) {
                AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(ResponseState responseState) {
                        HarvesterUI.UTIL_MANAGER.getSaveBox("Delete Tags", "Tags deleted Successfully");
                        Dispatcher.forwardEvent(AppEvents.ReloadTags);
                    }
                };
                TagsServiceAsync service = (TagsServiceAsync) Registry.get(HarvesterUI.TAGS_SERVICE);
                service.removeTag(mainTree.getSelectionModel().getSelectedItems(), callback);
            }
        };

        removeTagButton = new Button();
        removeTagButton.setText("&nbsp&nbspDelete");
        removeTagButton.setIcon(HarvesterUI.ICONS.tag_remove_icon());
        removeTagButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                if(mainTree.getSelectionModel().getSelectedItems().size() > 1)
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), "Are you sure you want to delete these tags?", removeTagsListener);
                else
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), "Are you sure you want to delete this tag?", removeTagsListener);
            }
        });

        editTagButton = new Button();
        editTagButton.setText(HarvesterUI.CONSTANTS.edit());
        editTagButton.setIcon(HarvesterUI.ICONS.operation_edit());
        editTagButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new TagDialog(mainTree.getSelectionModel().getSelectedItem()).showAndCenter();
            }
        });

        lastSeparator = new SeparatorToolItem();
    }

    public void hideTagButtons(ToolBar toolBar) {
        if(toolBar.getItems().contains(removeTagButton)) {
            removeTagButton.removeFromParent();
            editTagButton.removeFromParent();
            lastSeparator.removeFromParent();
        }
    }

    public void showTagButtons(ToolBar toolBar) {
        if(!toolBar.getItems().contains(removeTagButton)) {
            toolBar.insert(lastSeparator,1);
            toolBar.insert(editTagButton,2);
            toolBar.insert(removeTagButton,3);
        }
    }

}
