package harvesterUI.client.util.paging;

import com.extjs.gxt.ui.client.Registry;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.browse.BrowseFilterPanel;
import harvesterUI.client.panels.overviewGrid.MainGrid;
import harvesterUI.client.panels.overviewGrid.MyPagingToolBar;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-04-2012
 * Time: 19:17
 */
public class PageUtil {

    public static int getCurrentPageSize(){
        MyPagingToolBar myPagingToolBar = Registry.get(MainGrid.PAGING_TOOLBAR);
        return myPagingToolBar.getPageSize();
    }

    public static void setActivePage(int page){
        MyPagingToolBar myPagingToolBar = Registry.get(MainGrid.PAGING_TOOLBAR);
        myPagingToolBar.setActivePageAlwaysReload(page);
    }

    public static void reloadMainData(int page){
        BrowseFilterPanel browseFilterPanel = Registry.get("browseFilterPanel");
        MainGrid mainGrid = Registry.get("mainGrid");
        mainGrid.getBrowseFilterPanel().updateAllFilterValues();
        if(!mainGrid.getTopToolbar().getSearchCombo().getRawValue().isEmpty())
            HarvesterUI.UTIL_MANAGER.getMainGridSearchResults();
        else if(browseFilterPanel.getAttributesSelected().size() > 0)
            browseFilterPanel.applyFilter();
        else
            setActivePage(page);
    }
}
