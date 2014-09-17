package harvesterUI.client.panels.overviewGrid.contextMenus;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.DataContainer;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 27-03-2011
 * Time: 21:54
 */
public class OverviewGridContextMenus {

    private TreeGrid<DataContainer> tree;

    public OverviewGridContextMenus(TreeGrid<DataContainer> mainTree) {
        tree = mainTree;
    }

    public void createTreeContextMenu(){
        Menu contextMenu = new Menu();

        tree.setContextMenu(contextMenu);
        tree.addListener(Events.ContextMenu, new Listener<TreeGridEvent<ModelData>>() {
            public void handleEvent(TreeGridEvent<ModelData> event) {
                if(tree.getSelectionModel().getSelectedItems().size() > 0){
                    DataContainer selectedNode = tree.getSelectionModel().getSelectedItems().get(0);
                    if(selectedNode != null){
                        if(selectedNode instanceof AggregatorUI)
                            tree.setContextMenu(new AggregatorContextMenu(tree));
                        else if(selectedNode instanceof DataProviderUI) {
//                            if(((DataProviderUI)selectedNode).getDataSourceUIList().size() == 1) {
//                                tree.setContextMenu(new DataProviderWithSingleDSContextMenu(tree));
//                            }
//                            else
                                tree.setContextMenu(new DataProviderContextMenu(tree));
                        }
                        else if(selectedNode instanceof DataSourceUI)
                            tree.setContextMenu(new DataSetContextMenu(tree));
                    }
                }
            }
        });
    }
}
