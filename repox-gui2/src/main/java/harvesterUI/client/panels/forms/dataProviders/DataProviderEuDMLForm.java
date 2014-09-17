package harvesterUI.client.panels.forms.dataProviders;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.models.Attribute;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.util.CountryComboBox;
import harvesterUI.client.util.paging.PageUtil;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.servletResponseStates.ResponseState;
import harvesterUI.shared.users.UserRole;

import java.util.Iterator;
import java.util.Map;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-03-2011
 * Time: 14:56
 */
public class  DataProviderEuDMLForm extends DataProviderForm {

    private FormData formData;
    private Map<String,String> availableCountries;
    private DataProviderUI dataProviderUI;

    //Fields
    private TextField<String> firstName, description,email;//,ipAddress;
    private CountryComboBox<ModelData> combo;
    protected ListStore<ModelData> countryStore;

    public DataProviderEuDMLForm() {
        super();
        formData = new FormData("96%");
        createForm();
    }

    private void createForm() {
        setHeading(HarvesterUI.CONSTANTS.createDataProvider());
        setIcon(HarvesterUI.ICONS.add());
        setScrollMode(Style.Scroll.AUTO);

        countryStore = new ListStore<ModelData>();

        AsyncCallback<Map<String,String>> callback = new AsyncCallback<Map<String,String>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(Map<String,String> countries) {
                availableCountries = countries;
                Iterator iterator=availableCountries.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry mapEntry=(Map.Entry)iterator.next();
                    countryStore.add(new Attribute("country",""+mapEntry.getValue()));
                    combo.setValue(countryStore.getModels().get(0));
                }
            }
        };
        ((RepoxServiceAsync) Registry.get(HarvesterUI.REPOX_SERVICE)).getFullCountryList(callback);

        combo = new CountryComboBox<ModelData>();
        combo.setFieldLabel(HarvesterUI.CONSTANTS.country());
        combo.setDisplayField("value");
        combo.setTriggerAction(CountryComboBox.TriggerAction.ALL);
        combo.setStore(countryStore);
        combo.setForceSelection(true);
        dataProviderFormPanel.add(combo, formData);

        firstName = new TextField<String>();
        firstName.setFieldLabel(HarvesterUI.CONSTANTS.providerName() + HarvesterUI.REQUIRED_STR);
        firstName.setId("dptel_fn");
        firstName.setAllowBlank(false);
        dataProviderFormPanel.add(firstName, formData);

        description = new TextField<String>();
        description.setId("dptel_desc");
        description.setFieldLabel(HarvesterUI.CONSTANTS.description());
        dataProviderFormPanel.add(description, formData);

        email = new TextField<String>();
        email.setId("dptel_email");
        email.setFieldLabel(HarvesterUI.CONSTANTS.email());
        dataProviderFormPanel.add(email, formData);

        /*ipAddress = new TextField<String>();
        ipAddress.setId("dptel_ipaddress");
        ipAddress.setFieldLabel("IP Address");
        dataProviderFormPanel.add(ipAddress, formData);*/

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            String countryAbrev;
            @Override
            public void componentSelected(ButtonEvent be) {
                String country = combo.getValue().get("value");
                Map<String,String> countries = availableCountries;
                Iterator iterator=countries.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry mapEntry=(Map.Entry)iterator.next();
                    if(mapEntry.getValue().equals(country))
                        countryAbrev = ""+mapEntry.getKey();
                }

                String name = firstName.getValue();
                String descript = description.getValue();
                String countryName = combo.getValue().get("value").toString();

                if(dataProviderUI == null) {
                    dataProviderUI = new DataProviderUI("",name,countryAbrev, countryName);
                }

                dataProviderUI.setCountry(countryAbrev);
                dataProviderUI.setCountryName(countryName);
                dataProviderUI.setName(name);
                dataProviderUI.setDescription(descript);
                dataProviderUI.setEmail(email.getValue());
                //dataProviderUI.setIpAddress(ipAddress.getValue());

                AsyncCallback<SaveDataResponse> callback = new AsyncCallback<SaveDataResponse>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(SaveDataResponse response) {
                        ResponseState responseState = response.getResponseState();
                        if(responseState == ResponseState.ALREADY_EXISTS) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataProvider(), HarvesterUI.CONSTANTS.dataProviderAlreadyExists());
                            return;
                        }
                        else if(responseState == ResponseState.OTHER){
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataProvider(), HarvesterUI.CONSTANTS.dataProviderSaveError());
                            return;
                        }
                        dataProviderFormPanel.submit();
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveDataProvider(), HarvesterUI.CONSTANTS.dataProviderSaveSuccess());
                        PageUtil.reloadMainData(response.getPage());
                        hide();
                    }
                };
                service.saveDataProvider(edit,dataProviderUI, PageUtil.getCurrentPageSize(),
                        HarvesterUI.UTIL_MANAGER.getLoggedUserRole() == UserRole.DATA_PROVIDER ? HarvesterUI.UTIL_MANAGER.getLoggedUserName() : null, callback);
            }
        });
        addButton(saveButton);
        addButton(new Button(HarvesterUI.CONSTANTS.cancel(),HarvesterUI.ICONS.cancel_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        }));

        setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(dataProviderFormPanel);
        binding.addButton(saveButton);
        
        add(dataProviderFormPanel);
    }

    public void setEditMode(DataProviderUI dp) {
        setHeading(HarvesterUI.CONSTANTS.editDataProvider() + ": " + dp.getName());
        setIcon(HarvesterUI.ICONS.operation_edit());
        dataProviderUI = dp;
        setEdit(true);
        countryStore.clearFilters();
        for(ModelData comboSel: countryStore.getModels()) {
            if(comboSel.get("value").equals(availableCountries.get(dataProviderUI.getCountry())))
                combo.setValue(comboSel);
        }

        firstName.setValue(dataProviderUI.getName());
        description.setValue(dataProviderUI.getDescription());
        email.setValue(dataProviderUI.getEmail());
        //ipAddress.setValue(dataProviderUI.getIpAddress());
    }

    public void resetValues(Object parent) {
        setHeading(HarvesterUI.CONSTANTS.createDataProvider());
        setIcon(HarvesterUI.ICONS.add());
        setEdit(false);
        firstName.clear();
        description.clear();
        email.clear();
        //ipAddress.clear();
        dataProviderUI = null;

        countryStore.clearFilters();
        if(countryStore.getModels().size() > 0)
            combo.setValue(countryStore.getModels().get(0));
    }
}
