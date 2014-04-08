package harvesterUI.client.models;

import com.extjs.gxt.ui.client.widget.button.Button;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.shared.filters.FilterQuery;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 30/03/12
 * Time: 16:07
 */
public abstract class FilterButton extends Button {

    protected DataFilter dataFilter;
    protected BrowseFilterPanel browseFilterPanel;

    public FilterButton(BrowseFilterPanel browseFilterPanel) {
        this.browseFilterPanel = browseFilterPanel;
    }

    public void setDataFilter(DataFilter dataFilter) {
        this.dataFilter = dataFilter;
    }

    public DataFilter getDataFilter() {
        return dataFilter;
    }

    protected FilterButton getFilterButton(){ return this;}

    public abstract FilterQuery getFilterQuery();
    public abstract void updateFilterData();
}
