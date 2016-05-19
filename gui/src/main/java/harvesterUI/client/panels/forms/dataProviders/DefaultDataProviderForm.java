package harvesterUI.client.panels.forms.dataProviders;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.models.Attribute;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.util.CountryComboBox;
import harvesterUI.client.util.paging.PageUtil;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.dataTypes.AggregatorUI;
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
public class DefaultDataProviderForm extends DataProviderForm {

    private FormData formData;
    private boolean edit = false;
    private AggregatorUI parent;
    private Map<String,String> availableCountries;
    private DataProviderUI dataProviderUI;

    //Fields
    private TextField<String> firstName, description, nameCode,homepage;
    private SimpleComboBox<String> typeCombo;
    protected CountryComboBox<ModelData> combo;

    public DefaultDataProviderForm() {
        super();
        formData = new FormData("95%");
        createForm();
    }

    private void createForm() {
        setHeading(HarvesterUI.CONSTANTS.createDataProvider());
        setIcon(HarvesterUI.ICONS.add());
        setScrollMode(Style.Scroll.AUTO);

        final ListStore<ModelData> countryStore = new ListStore<ModelData>();

        AsyncCallback<Map<String,String>> callback = new AsyncCallback<Map<String,String>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(Map<String, String> countries) {
                availableCountries = countries;
                Iterator iterator=availableCountries.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry mapEntry=(Map.Entry)iterator.next();
                    countryStore.add(new Attribute("country",""+mapEntry.getValue()));
                    combo.getStore().sort("value",Style.SortDir.ASC);
                    combo.setValue(countryStore.getModels().get(0));
                }
            }
        };
        ((RepoxServiceAsync) Registry.get(HarvesterUI.REPOX_SERVICE)).getFullCountryList(callback);

        combo = new CountryComboBox<ModelData>();
        combo.setFieldLabel(HarvesterUI.CONSTANTS.country() + HarvesterUI.REQUIRED_STR);
        combo.setDisplayField("value");
        combo.setTriggerAction(CountryComboBox.TriggerAction.ALL);
        combo.setStore(countryStore);
        combo.getStore().sort("value",Style.SortDir.ASC);
        combo.setForceSelection(true);
        dataProviderFormPanel.add(combo, formData);

        firstName = new TextField<String>();
        firstName.setFieldLabel(HarvesterUI.CONSTANTS.providerName() + HarvesterUI.REQUIRED_STR);
        firstName.setId("firstNameField");
        firstName.setAllowBlank(false);
        dataProviderFormPanel.add(firstName, formData);

        description = new TextField<String>();
        description.setFieldLabel(HarvesterUI.CONSTANTS.description());
        description.setId("descriptionField");
        dataProviderFormPanel.add(description, formData);

        typeCombo = new SimpleComboBox<String>();
        typeCombo.setFieldLabel(HarvesterUI.CONSTANTS.type() + HarvesterUI.REQUIRED_STR);
        typeCombo.setDisplayField("value");
        typeCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        typeCombo.add("MUSEUM");
        typeCombo.add("ARCHIVE");
        typeCombo.add("LIBRARY");
        typeCombo.add("AUDIO_VISUAL_ARCHIVE");
        typeCombo.add("RESEARCH_EDUCATIONAL");
        typeCombo.add("CROSS_SECTOR");
        typeCombo.add("PUBLISHER");
        typeCombo.add("PRIVATE");
        typeCombo.add("AGGREGATOR");
        typeCombo.add("UNKNOWN");
        typeCombo.setValue(typeCombo.getStore().getModels().get(0));
        typeCombo.setEditable(false);
        dataProviderFormPanel.add(typeCombo, formData);

        nameCode = new TextField<String>();
        nameCode.setFieldLabel(HarvesterUI.CONSTANTS.nameCode());
        nameCode.setId("nameCodeField");
//        nameCode.setAllowBlank(false);
        dataProviderFormPanel.add(nameCode, formData);

        homepage = new TextField<String>();
        homepage.setFieldLabel(HarvesterUI.CONSTANTS.homepage());
        homepage.setId("homePageField");
        dataProviderFormPanel.add(homepage, formData);

        Button b = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            String countryAbrev;
            @Override
            public void componentSelected(ButtonEvent be) {
                String name = firstName.getValue();
                String descript = description.getValue();
                String type = typeCombo.getSimpleValue();
                String nameCd = nameCode.getValue();
                String hmpage = homepage.getValue();

                if(homepage.getValue() != null && !homepage.getValue().isEmpty() && !homepage.getValue().
                        startsWith("http://") && !homepage.getValue().startsWith("https://")){
                    hmpage = "http://" + homepage.getValue();
                }

                String country = combo.getValue().get("value");
                Map<String,String> countries = availableCountries;
                Iterator<Map.Entry<String, String>> iterator=countries.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<String, String> mapEntry=iterator.next();
                    if(mapEntry.getValue().equals(country))
                        countryAbrev = "" + mapEntry.getKey();
                }

                String countryName = combo.getValue().get("value").toString();
                if(parent != null) {
                    dataProviderUI = new DataProviderUI("",name,countryAbrev, countryName);
                    dataProviderUI.setParentAggregatorID(parent.getId());
                }

                dataProviderUI.setCountryCode(countryAbrev);
                dataProviderUI.setCountryName(countryName);
                dataProviderUI.setDescription(descript);
                dataProviderUI.setName(name);
                dataProviderUI.setType(type);
                dataProviderUI.setNameCode(nameCd);
                dataProviderUI.setHomepage(hmpage);

                AsyncCallback<SaveDataResponse> callback = new AsyncCallback<SaveDataResponse>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(SaveDataResponse response) {
                        ResponseState responseState = response.getResponseState();
                        if(responseState == ResponseState.URL_MALFORMED) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataProvider(), HarvesterUI.CONSTANTS.homepageUrlMalformed());
                            return;
                        } else if(responseState == ResponseState.URL_NOT_EXISTS) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataProvider(), HarvesterUI.CONSTANTS.homepageUrlNotExists());
                            return;
                        } else if(responseState == ResponseState.ALREADY_EXISTS) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.saveDataProvider(), HarvesterUI.CONSTANTS.homepageAlreadyExists());
                            return;
                        }

                        dataProviderFormPanel.submit();
                        HarvesterUI.UTIL_MANAGER.getSaveBox(HarvesterUI.CONSTANTS.saveDataProvider(), HarvesterUI.CONSTANTS.dataProviderSaveSuccess());
                        PageUtil.reloadMainData(response.getPage());
                        hide();
                    }
                };
                service.saveDataProvider(edit, dataProviderUI, PageUtil.getCurrentPageSize(),
                        HarvesterUI.UTIL_MANAGER.getLoggedUserRole() == UserRole.DATA_PROVIDER ? HarvesterUI.UTIL_MANAGER.getLoggedUserName() : null, callback);
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

        FormButtonBinding binding = new FormButtonBinding(dataProviderFormPanel);
        binding.addButton(b);
        
        add(dataProviderFormPanel);
    }

    public void setEditMode(DataProviderUI dp) {
        setHeading(HarvesterUI.CONSTANTS.editDataProvider() + ": " + dp.getName());
        setIcon(HarvesterUI.ICONS.operation_edit());
        dataProviderUI = dp;
        parent = null;
        edit = true;

        combo.getStore().clearFilters();
        typeCombo.getStore().clearFilters();

        for(ModelData comboSel: combo.getStore().getModels()) {
            if(comboSel.get("value").equals(availableCountries.get(dataProviderUI.getCountryCode())))
                combo.setValue(comboSel);
        }

        firstName.setValue(dataProviderUI.getName());
        description.setValue(dataProviderUI.getDescription());
        nameCode.setValue(dataProviderUI.getNameCode());

        for(SimpleComboValue<String> comboSel: typeCombo.getStore().getModels()) {
            if(comboSel.getValue().equals(dataProviderUI.getType()))
                typeCombo.setValue(comboSel);
        }

        homepage.setValue(dataProviderUI.getHomepage());
    }

    public void resetValues(Object parentAgg) {
        setHeading(HarvesterUI.CONSTANTS.createDataProvider());
        setIcon(HarvesterUI.ICONS.add());
        if(parentAgg instanceof AggregatorUI)
            parent = (AggregatorUI) parentAgg;
        else
            parent = null;

        edit = false;
        firstName.clear();
        nameCode.clear();
        description.clear();
        homepage.clear();
        dataProviderUI = null;

        combo.getStore().clearFilters();
        typeCombo.getStore().clearFilters();

        if(combo.getStore().getModels().size() > 0)
            combo.setValue(combo.getStore().getModels().get(0));
        typeCombo.setValue(typeCombo.getStore().getModels().get(0));
    }
}
