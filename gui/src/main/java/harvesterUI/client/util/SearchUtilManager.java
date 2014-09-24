package harvesterUI.client.util;

import com.extjs.gxt.ui.client.Registry;
import harvesterUI.client.panels.overviewGrid.MainGrid;
import harvesterUI.client.panels.overviewGrid.SearchComboBox;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 20-09-2012
 * Time: 12:48
 */
public class SearchUtilManager {

    public void dataProviderSearchedDeleted(List<DataProviderUI> dataProviderUIList){
        SearchComboBox searchComboBox = ((MainGrid)Registry.get("mainGrid")).getTopToolbar().getSearchCombo();
        String searchValue = searchComboBox.getRawValue();

        if(searchValue == null || searchValue.isEmpty())
            return;

        for(DataProviderUI dataProviderUI : dataProviderUIList){
            if(searchValue.equals(dataProviderUI.getName())){
                searchComboBox.clear();
                searchComboBox.setLastSavedSearch(null);
                return;
            }
        }
    }

    public void dataSetSearchedDeleted(List<DataSourceUI> dataSourceUIList){
        SearchComboBox searchComboBox = ((MainGrid)Registry.get("mainGrid")).getTopToolbar().getSearchCombo();
        String searchValue = searchComboBox.getRawValue();

        if(searchValue == null || searchValue.isEmpty())
            return;

        for(DataSourceUI dataSourceUI : dataSourceUIList){
            if(searchValue.equals(dataSourceUI.getName())){
                searchComboBox.clear();
                searchComboBox.setLastSavedSearch(null);
                return;
            }
        }
    }

    public void aggregatorSearchedDeleted(List<AggregatorUI> aggregatorUIs){
        SearchComboBox searchComboBox = ((MainGrid)Registry.get("mainGrid")).getTopToolbar().getSearchCombo();
        String searchValue = searchComboBox.getRawValue();

        if(searchValue == null || searchValue.isEmpty())
            return;

        for(AggregatorUI aggregatorUI : aggregatorUIs){
            if(searchValue.equals(aggregatorUI.getName())){
                searchComboBox.clear();
                searchComboBox.setLastSavedSearch(null);
                return;
            }
        }
    }
}
