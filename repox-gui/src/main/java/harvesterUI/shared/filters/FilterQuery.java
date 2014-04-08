package harvesterUI.shared.filters;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 18-02-2011
 * Time: 13:48
 */
public class FilterQuery implements IsSerializable {

    private FilterType filterType;
    private List<String> values;

    public FilterQuery() {
    }

    public FilterQuery(FilterType filterType, List<String> values) {
        this.filterType = filterType;
        this.values = values;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
