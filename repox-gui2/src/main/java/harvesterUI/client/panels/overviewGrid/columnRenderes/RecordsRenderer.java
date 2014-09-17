package harvesterUI.client.panels.overviewGrid.columnRenderes;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 23-02-2012
 * Time: 15:53
 */
public class RecordsRenderer implements GridCellRenderer<ModelData> {

    public RecordsRenderer() {}

    public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                         ListStore<ModelData> store, Grid<ModelData> grid) {
        if(model instanceof DataSourceUI) {
            DataSourceUI dataSourceUI;
//            if(model instanceof DataSourceUI)
                dataSourceUI = (DataSourceUI) model;
//            else {
//                // Data providers with more then 1 children or has no DSs
//                if(((DataProviderUI)model).getDataSourceUIList().size() > 1 ||
//                        ((DataProviderUI)model).getDataSourceUIList().size() <= 0)
//                    return "";
//                else
//                    dataSourceUI = ((DataProviderUI)model).getDataSourceUIList().get(0);
//            }

            if(dataSourceUI.getRecords() != null) {
                if(!dataSourceUI.getRecords().equals("")) {
                    // Only show while ingesting
                    return dataSourceUI.getRecords();
                }
            }
            return "";
        }
        else
            return "";
    }
}
