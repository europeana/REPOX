package harvesterUI.client.panels.overviewGrid;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import harvesterUI.client.panels.dataProviderButtons.CreateDataSetButton;
import harvesterUI.client.panels.dataProviderButtons.ManageDataProviderMenu;
import harvesterUI.client.util.GridOperations;
import harvesterUI.shared.dataTypes.DataContainer;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 24-11-2011
 * Time: 17:41
 */
public class OverviewGridDataProviderOperations extends GridOperations{

    public OverviewGridDataProviderOperations(TreeGrid<DataContainer> mainTree) {

        new CreateDataSetButton(mainTree,componentList);
        new ManageDataProviderMenu(mainTree,componentList);

        createSeparator();
    }

    public void hideButtons(ToolBar toolBar) {
        for(Component component : componentList) {
            if(toolBar.getItems().contains(component))
                component.removeFromParent();
        }
    }

    public void showButtons(ToolBar toolBar) {
        if(toolBar.getItem(0).getId().equals("firstToolBarButton"))
            for(int i = 0; i < componentList.size() ; i++)
                toolBar.insert(componentList.get(i),i+STATIC_BUTTONS_INDEX+1);
        else
            for(int i = 0; i < componentList.size() ; i++)
                toolBar.insert(componentList.get(i),i+STATIC_BUTTONS_INDEX);
    }
}
