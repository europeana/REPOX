package harvesterUI.client.panels.forms;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.dataManagement.AGGServiceAsync;
import harvesterUI.client.util.paging.PageUtil;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 13-03-2011
 * Time: 16:25
 */
public class AggregatorForm extends FormDialog {

    private FormData formData;
    private TextField<String> name,nameCode,homepage;
    private AGGServiceAsync service;
    private boolean edit = false;
    private DefaultFormPanel aggregatorFormPanel;

    private AggregatorUI aggregatorUI;

    public AggregatorForm() {
        super(0.4,0.5);
        formData = new FormData("95%");
        service = (AGGServiceAsync) Registry.get(HarvesterUI.AGG_SERVICE);
        createForm();
        setHeading(HarvesterUI.CONSTANTS.createAggregator());
        setIcon(HarvesterUI.ICONS.add());
    }

    private void createForm() {
        aggregatorFormPanel = new DefaultFormPanel();
        aggregatorFormPanel.setHeaderVisible(false);
        aggregatorFormPanel.setLayout(new EditableFormLayout(150));

        name = new TextField<String>();
        name.setFieldLabel(HarvesterUI.CONSTANTS.name() + HarvesterUI.REQUIRED_STR);
        name.setId("aggNameField");
        name.setAllowBlank(false);
        aggregatorFormPanel.add(name, formData);

        nameCode = new TextField<String>();
        nameCode.setFieldLabel(HarvesterUI.CONSTANTS.nameCode());
        nameCode.setId("aggNameCode");
//        nameCode.setAllowBlank(false);
        aggregatorFormPanel.add(nameCode, formData);

        homepage = new TextField<String>();
        homepage.setFieldLabel(HarvesterUI.CONSTANTS.homepage());
        homepage.setId("aggHPField");
        aggregatorFormPanel.add(homepage, formData);

        Button b = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                String nameStr = name.getValue();
                String nameCd = nameCode.getValue();
                String hmpage = homepage.getValue();
                if (hmpage != null && !hmpage.startsWith("http://") && !hmpage.startsWith("https://")) {
                    hmpage = "http://" + hmpage;
                }

                if(aggregatorUI == null)
                    aggregatorUI = new AggregatorUI("",nameStr,nameCd,hmpage);

                aggregatorUI.setHomepage(hmpage);
                aggregatorUI.setName(nameStr);
                aggregatorUI.setNameCode(nameCd);

                AsyncCallback<SaveDataResponse> callback = new AsyncCallback<SaveDataResponse>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(SaveDataResponse response) {
                        ResponseState responseState = response.getResponseState();
                        if(responseState == ResponseState.URL_MALFORMED) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveAggregator(), HarvesterUI.CONSTANTS.homepageUrlMalformed());
                            return;
                        } else if(responseState == ResponseState.URL_NOT_EXISTS) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveAggregator(), HarvesterUI.CONSTANTS.homepageUrlNotExists());
                            return;
                        } else if(responseState == ResponseState.ALREADY_EXISTS) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveAggregator(), HarvesterUI.CONSTANTS.aggregatorAlreadyExists());
                            return;
                        }

                        aggregatorFormPanel.submit();
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveAggregator(), HarvesterUI.CONSTANTS.saveAggregatorSuccess());
                        PageUtil.reloadMainData(response.getPage());
                        hide();
                    }
                };
                service.saveAggregator(edit, aggregatorUI, PageUtil.getCurrentPageSize(), callback);
            }
        });
        addButton(b);
        addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        }));

        setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(aggregatorFormPanel);
        binding.addButton(b);
        
        add(aggregatorFormPanel);
    }

    public void setEditMode(AggregatorUI aggregatorUI) {
        this.aggregatorUI = aggregatorUI;
        edit = true;
        name.setValue(aggregatorUI.getName());
        nameCode.setValue(aggregatorUI.getNameCode());
        homepage.setValue(aggregatorUI.getHomepage());
        setHeading(HarvesterUI.CONSTANTS.editAggregator() + ": " + aggregatorUI.getName());
        setIcon(HarvesterUI.ICONS.table());
    }

    public void resetValues() {
        setHeading(HarvesterUI.CONSTANTS.createAggregator());
        setIcon(HarvesterUI.ICONS.add());
        aggregatorUI = null;
        edit = false;
        name.clear();
        nameCode.clear();
        homepage.clear();
    }
}
