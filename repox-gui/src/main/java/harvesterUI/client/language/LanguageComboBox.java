package harvesterUI.client.language;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.models.Country;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.WidgetWithRole;
import harvesterUI.shared.users.UserRole;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 21-02-2012
 * Time: 19:16
 */
public class LanguageComboBox extends WidgetWithRole{

    public LanguageComboBox(ToolBar toolBar) {
        if(drawWidget){
            ListStore<Country> countries = new ListStore<Country>();
            countries.add(new Country("en","england","English"));
            countries.add(new Country("es","es","Spanish"));
            countries.add(new Country("pt","pt","Portuguese"));
            countries.add(new Country("nl","nl","Dutch"));

            ComboBox<Country>  comboBox = new ComboBox<Country>();
            comboBox.setWidth(85);
            comboBox.setStore(countries);
            comboBox.setTemplate(getFlagTemplate("resources/images/countries/"));
            comboBox.setDisplayField("countryName");
//        setTypeAhead(true);
            comboBox.setEditable(false);
            comboBox.setMinListWidth(150);
            comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);

            setComboValue(comboBox);

            comboBox.addSelectionChangedListener(new SelectionChangedListener<Country>() {
                @Override
                public void selectionChanged(SelectionChangedEvent<Country> se) {
                    if (se.getSelectedItem() != null) {
                        se.getSelectedItem().changeLocale();
                    }
                }
            });
            toolBar.add(comboBox);
        }
    }

    private void setComboValue(ComboBox<Country> comboBox){
        for(Country country: comboBox.getStore().getModels()){
            if(country.getCountryAbrev().equals(UtilManager.getUrlLocaleLanguage())){
                comboBox.setValue(country);
                break;
            } else if(country.getCountryAbrev().equals("en"))
                comboBox.setValue(country);
        }
    }

    private native String getFlagTemplate(String base) /*-{
        return  [
            '<tpl for=".">',
            '<div class="x-combo-list-item"><img width="16px" height="11px" src="' + base + '{[values.countryFlagAbrev]}.png"> {[values.countryName]}</div>',
            '</tpl>'
        ].join("");
    }-*/;

    public void checkRole(){
        drawWidget = HarvesterUI.UTIL_MANAGER.getLoggedUserRole() != UserRole.ANONYMOUS;
    }
}
