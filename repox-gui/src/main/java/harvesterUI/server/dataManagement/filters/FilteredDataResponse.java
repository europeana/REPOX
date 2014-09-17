package harvesterUI.server.dataManagement.filters;

import java.util.List;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 06/02/13
 * Time: 15:33
 */
public class FilteredDataResponse {
    private List<Object> filteredData;
    private boolean dataWasFiltered;

    public FilteredDataResponse(List<Object> filteredData, boolean dataWasFiltered) {
        this.filteredData = filteredData;
        this.dataWasFiltered = dataWasFiltered;
    }

    public List<Object> getFilteredData() {
        return filteredData;
    }

    public boolean isDataWasFiltered() {
        return dataWasFiltered;
    }
}
