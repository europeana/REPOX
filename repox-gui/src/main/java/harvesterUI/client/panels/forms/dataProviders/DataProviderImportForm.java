package harvesterUI.client.panels.forms.dataProviders;


import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 15-03-2011
 * Time: 15:54
 */
public class DataProviderImportForm extends FormDialog {

    private FormData formData;
    private FileUploadField fileUploadField;
    private DefaultFormPanel importFormPanel;

    public DataProviderImportForm() {
        super(0.2,0.5);
        formData = new FormData("95%");
        setHeading(HarvesterUI.CONSTANTS.importDataProviders());
        setIcon(HarvesterUI.ICONS.dp_import_icon());

        createDPImportForm();
    }

    protected void createDPImportForm() {
        importFormPanel = new DefaultFormPanel();
        importFormPanel.setHeaderVisible(false);
        importFormPanel.setLayout(new EditableFormLayout(150));

        importFormPanel.setMethod(FormPanel.Method.POST);
        importFormPanel.setEncoding(FormPanel.Encoding.MULTIPART);
        importFormPanel.setAction(GWT.getModuleBaseURL() + "fileupload");

        fileUploadField = new FileUploadField();
        fileUploadField.setName("upload");
        fileUploadField.setAllowBlank(false);
        fileUploadField.setFieldLabel(HarvesterUI.CONSTANTS.importFile()+ HarvesterUI.REQUIRED_STR);
        importFormPanel.add(fileUploadField, formData);

        importFormPanel.addListener(Events.Submit, new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent be) {
                unmask();
                Dispatcher.get().dispatch(AppEvents.LoadMainData);
                hide();
            }
        });

        Button save = new Button(HarvesterUI.CONSTANTS.importName(),HarvesterUI.ICONS.dp_import_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                importFormPanel.submit();
                mask(HarvesterUI.CONSTANTS.importingDPs());
            }
        });
        addButton(save);

        addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        }));

        setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(importFormPanel);
        binding.addButton(save);
        
        add(importFormPanel);
    }

    public void resetFileUploadField() {
        fileUploadField.clear();
    }
}
