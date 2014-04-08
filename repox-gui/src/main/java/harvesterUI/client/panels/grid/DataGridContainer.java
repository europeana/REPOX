package harvesterUI.client.panels.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import harvesterUI.shared.dataTypes.DataContainer;

/**
 * Created with IntelliJ IDEA.
 * User: AC
 * Date: 17-07-2012
 * Time: 12:22
 */
 public abstract class DataGridContainer<M extends ModelData> {

    protected TreeGrid<M> mainDataGrid;
    protected int scrollBarY = 0;

    public abstract void loadGridData(PagingLoadConfig config);

    public void setScrollBarY() {
        try{
            if(mainDataGrid.getView().getScrollState() != null)
                this.scrollBarY = mainDataGrid.getView().getScrollState().y;
        }catch (NullPointerException e){

        }
    }

    public TreeGrid<M> getMainDataGrid() {
        return mainDataGrid;
    }
}
