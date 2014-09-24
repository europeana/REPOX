package harvesterUI.client.panels.browse;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 01-03-2011
 * Time: 16:35
 */

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.event.dom.client.KeyCodes;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.models.FilterButton;
import harvesterUI.shared.filters.FilterQueryRecords;
import harvesterUI.shared.filters.FilterType;

public class RecordsFilter extends FilterButton {

    private NumberField greaterThenField;
    private NumberField smallerThenField;
    private NumberField equalToField;

    private int beginRecords = -1;
    private int endRecords = -1;
    private int onRecords = -1;

    public RecordsFilter(BrowseFilterPanel browseFilterPanel) {
        super(browseFilterPanel);

        setText(HarvesterUI.CONSTANTS.range());

        greaterThenField = new NumberField();
        smallerThenField = new NumberField();
        equalToField = new NumberField();

        greaterThenField.setAllowDecimals(false);
        smallerThenField.setAllowDecimals(false);
        equalToField.setAllowDecimals(false);

        KeyListener pressListener =  new KeyListener(){
            @Override
            public void componentKeyDown(ComponentEvent event) {
                // If ENTER key pressed
                if(event.getKeyCode()== KeyCodes.KEY_ENTER){
                    if(greaterThenField.getValue() != null)
                        setAttributeBeginRecords(greaterThenField.getValue().intValue());
                    else
                        beginRecords = -1;

                    if(smallerThenField.getValue() != null)
                        setAttributeEndRecords(smallerThenField.getValue().intValue());
                    else
                        endRecords = -1;

                    if(equalToField.getValue() != null){
                        setAttributeOnRecords(equalToField.getValue().intValue());
                        clearInterval();
                    } else
                        onRecords = -1;

                    checkInterval();
//                    clearAttributeRecords();
//                    if(dataFilter.getChecked() == 1)
//                        Dispatcher.get().dispatch(AppEvents.LoadMainData);
                }
                super.componentKeyPress(event);
            }};

        greaterThenField.addKeyListener(pressListener);
        smallerThenField.addKeyListener(pressListener);
        equalToField.addKeyListener(pressListener);

        MenuItem greater = new MenuItem();
        greater.setIcon(HarvesterUI.ICONS.greater_than());
        greater.setHideOnClick(false);
        greater.setCanActivate(false);
        greater.setWidget(greaterThenField);
        MenuItem lesser = new MenuItem();
        lesser.setIcon(HarvesterUI.ICONS.lesser_than());
        lesser.setHideOnClick(false);
        lesser.setCanActivate(false);
        lesser.setWidget(smallerThenField);
        MenuItem equal = new MenuItem();
        equal.setIcon(HarvesterUI.ICONS.equal_to());
        equal.setHideOnClick(false);
        equal.setCanActivate(false);
        equal.setWidget(equalToField);
        MenuItem filter = new MenuItem(HarvesterUI.CONSTANTS.filter());
        filter.setIcon(HarvesterUI.ICONS.table());
        filter.setHideOnClick(false);
        filter.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent menuEvent) {
                if(greaterThenField.getValue() != null)
                    setAttributeBeginRecords(greaterThenField.getValue().intValue());
                if(smallerThenField.getValue() != null)
                    setAttributeEndRecords(smallerThenField.getValue().intValue());
                if(equalToField.getValue() != null){
                    setAttributeOnRecords(equalToField.getValue().intValue());
                    clearInterval();
                }
//                checkInterval();
//                clearAttributeRecords();
//                if(dataFilter.getChecked() == 1)
//                    Dispatcher.get().dispatch(AppEvents.LoadMainData);
            }
        });

        MenuItem reset = new MenuItem(HarvesterUI.CONSTANTS.reset());
        reset.setIcon(HarvesterUI.ICONS.delete());
        reset.setHideOnClick(false);
        reset.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent menuEvent) {
                greaterThenField.setValue(null);
                smallerThenField.setValue(null);
                equalToField.setValue(null);
                clearAttributeRecords();
//                if(dataFilter.getChecked() == 1)
//                    Dispatcher.get().dispatch(AppEvents.LoadMainData);
            }
        });

        Menu rangeMenu = new Menu();
        rangeMenu.add(greater);
        rangeMenu.add(lesser);
        rangeMenu.add(equal);
//        rangeMenu.add(filter);
        rangeMenu.add(reset);
        setMenu(rangeMenu);
    }

    public void updateFilterData(){}

    private void setAttributeEndRecords(int records){
        endRecords = records;
        updateAttributeInfo();
    }

    private void setAttributeBeginRecords(int records){
        beginRecords = records;
        updateAttributeInfo();
    }

    private void setAttributeOnRecords(int records){
        onRecords = records;
        updateAttributeInfo();
    }

    private void clearAttributeRecords(){
        if(greaterThenField.getValue() == null)
            setAttributeBeginRecords(-1);
        if(smallerThenField.getValue() == null)
            setAttributeEndRecords(-1);
        if(equalToField.getValue() == null)
            setAttributeOnRecords(-1);
    }

    private void clearInterval(){
        setAttributeBeginRecords(-1);
        setAttributeEndRecords(-1);
        greaterThenField.clear();
        smallerThenField.clear();
    }

    private void checkInterval(){
        if(smallerThenField.getValue() != null && greaterThenField.getValue() != null){
            if(smallerThenField.getValue().intValue() <= greaterThenField.getValue().intValue())
                greaterThenField.clear();
        }
    }

    private void updateAttributeInfo(){
        dataFilter.setRangeInfo(createInfoString());
        // Update the attribute info value written on the store
        if(browseFilterPanel.getAttributesListStore().contains(dataFilter))
            browseFilterPanel.getAttributesListStore().update(dataFilter);
        else if(browseFilterPanel.getAttributesSelected().contains(dataFilter))
            browseFilterPanel.getAttributesListStore().update(dataFilter);
    }

    private String createInfoString(){
        String result = "";

        if(equalToField.getValue() != null){
            result = "= " + equalToField.getValue().intValue();
        }
        else{
            if(smallerThenField.getValue() != null && greaterThenField.getValue() != null){
                result = "[" + greaterThenField.getValue().intValue() + "-" + smallerThenField.getValue().intValue() + "]";
            }
            else{
                if(greaterThenField.getValue() != null)
                    result = ">" + greaterThenField.getValue().intValue();
                else if(smallerThenField.getValue() != null)
                    result = "<" + smallerThenField.getValue().intValue();
            }
        }

        return result;
    }

    public FilterQueryRecords getFilterQuery(){
        return new FilterQueryRecords(FilterType.RECORDS,beginRecords,endRecords,onRecords);
    }
}
