package harvesterUI.client.panels.tags;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.dataManagement.TagsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-04-2011
 * Time: 17:19
 */
public class TagDialog extends FormDialog {

    private TextField<String> nameField;
    private DefaultFormPanel newSchemaFormPanel;
    private DataSetTagUI associatedTagUI;
    private String oldId = "";

    public TagDialog() {
        super(0.2,0.2);
        createNewTagDialog();
    }

    public TagDialog(DataSetTagUI dataSetTagUI) {
        this();
        associatedTagUI = dataSetTagUI;
        edit(dataSetTagUI);
    }

    private void createNewTagDialog() {
        FormData formData = new FormData("95%");
        setIcon(HarvesterUI.ICONS.tag_add_icon());
        setHeading(HarvesterUI.CONSTANTS.add());

        newSchemaFormPanel = new DefaultFormPanel();
        newSchemaFormPanel.setHeaderVisible(false);

        KeyListener keyListener = new KeyListener() {
            public void componentKeyDown(ComponentEvent event) {
                if(event.getKeyCode()== KeyCodes.KEY_ENTER){
                    saveData();
                }
            }
        };

        nameField = new TextField<String>();
        nameField.addKeyListener(keyListener);
        nameField.setFieldLabel(HarvesterUI.CONSTANTS.name() + HarvesterUI.REQUIRED_STR);
        nameField.setId("name");
        nameField.setAllowBlank(false);
        newSchemaFormPanel.add(nameField, formData);

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                saveData();
            }
        });

        newSchemaFormPanel.addButton(saveButton);
        newSchemaFormPanel.addButton(new Button(HarvesterUI.CONSTANTS.cancel(), HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        }));

        newSchemaFormPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(newSchemaFormPanel);
        binding.addButton(saveButton);

        add(newSchemaFormPanel);
    }

    public void saveData() {
        AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(ResponseState responseState) {
                if(responseState == ResponseState.ALREADY_EXISTS){
                    HarvesterUI.UTIL_MANAGER.getInfoBox("New Tag","Tag with that name already exists"); //todo multi lang
                    return;
                }
                hide();
                HarvesterUI.UTIL_MANAGER.getSaveBox("Save Tag", "Tag Saved successfully");
                Dispatcher.forwardEvent(AppEvents.ReloadTags);
            }
        };
        newSchemaFormPanel.submit();
        String name = nameField.getValue().trim();
        TagsServiceAsync service = (TagsServiceAsync) Registry.get(HarvesterUI.TAGS_SERVICE);
        service.saveTag(associatedTagUI != null,new DataSetTagUI(name),oldId.isEmpty() ? name : oldId, callback);
    }

    public void edit(DataSetTagUI dataSetTagUI){
        oldId = dataSetTagUI.getName();
        setHeading("Edit Tag"+": " + dataSetTagUI.getName());
        setIcon(HarvesterUI.ICONS.operation_edit());
        nameField.setValue(dataSetTagUI.getName());
    }

    @Override
    public void showAndCenter() {
        super.showAndCenter();
        setFocusWidget(nameField);
    }
    //
//    @Override
//    protected void onResize(int width, int height) {
//        super.onResize(width,height);
//        super.layout(true);
//        generalInfoSet.layout(true);
//        newSchemaVersionFieldSet.resize();
//        layout(true);
//    }
}
