package harvesterUI.client.panels.mdr;

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
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.mdr.TransformationUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 24-11-2011
 * Time: 17:41
 */
public class TransformationsOperations {

    protected Button removeTransformationButton,editTransformationButton;
    protected SeparatorToolItem lastSeparator;
    private MappingsSearchBar mappingsSearchBar;

    public TransformationsOperations(final Grid<TransformationUI> mainTree, MappingsSearchBar mappingsSearchBar) {
        this.mappingsSearchBar = mappingsSearchBar;

        final SelectionListener<ButtonEvent> removeTransListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent ce) {
                AsyncCallback<String> callback = new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(String result) {
                        mappingSearchedDeleted(mainTree);
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.deleteTransformation(), HarvesterUI.CONSTANTS.deleteTransformationSuccess());
                        Dispatcher.forwardEvent(AppEvents.ReloadTransformations);
                    }
                };
                List<String> transformationsIds = new ArrayList<String>();
                for(TransformationUI transformationUI : mainTree.getSelectionModel().getSelectedItems()) {
                    transformationsIds.add(transformationUI.getIdentifier());
                }
                TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
                service.deleteTransformation(transformationsIds, callback);
            }
        };

        removeTransformationButton = new Button();
        removeTransformationButton.setText(HarvesterUI.CONSTANTS.removeTransformation());
        removeTransformationButton.setIcon(HarvesterUI.ICONS.delete());
        removeTransformationButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                for(TransformationUI transformationUI : mainTree.getSelectionModel().getSelectedItems()) {
                    if(transformationUI.getMdrDataStatistics().getNumberTimesUsedInDataSets() != 0) {
                        HarvesterUI.UTIL_MANAGER.createConfirmMessageBox("Remove Transformation in Use",
                                "Are you sure you want to delete transformation(s) currently being used in Data Sets?", removeTransListener); //todo multilang
                        return;
                    }
                }
                HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.deleteTransformationConfirm(), removeTransListener);
            }
        });

        editTransformationButton = new Button();
        editTransformationButton.setText(HarvesterUI.CONSTANTS.editTransformation());
        editTransformationButton.setIcon(HarvesterUI.ICONS.operation_edit());
        editTransformationButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ViewAddMappingDialog,mainTree.getSelectionModel().getSelectedItem());
            }
        });

        lastSeparator = new SeparatorToolItem();
    }

    public void hideTransformationButtons(ToolBar toolBar) {
        if(toolBar.getItems().contains(removeTransformationButton)) {
            removeTransformationButton.removeFromParent();
            editTransformationButton.removeFromParent();
            lastSeparator.removeFromParent();
        }
    }

    public void showTransformationButtons(ToolBar toolBar) {
        if(!toolBar.getItems().contains(removeTransformationButton)) {
            toolBar.insert(lastSeparator,1);
            toolBar.insert(editTransformationButton,2);
            toolBar.insert(removeTransformationButton,3);
        }
    }

    public void mappingSearchedDeleted(Grid<TransformationUI> mainTree){
        String searchValue = mappingsSearchBar.getRawValue();

        if(searchValue == null || searchValue.isEmpty())
            return;

        for(TransformationUI transformationUI : mainTree.getSelectionModel().getSelectedItems()){
            if(searchValue.equals(transformationUI.getDescription())){
                mappingsSearchBar.clear();
                mappingsSearchBar.setLastSearchResult(null);
                return;
            }
        }
    }
}
