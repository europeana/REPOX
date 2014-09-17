package harvesterUI.client.panels.dataProviderButtons;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.util.WidgetWithRole;
import harvesterUI.shared.dataTypes.DataContainer;

import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 10:10
 */
public class CreateDataSetButton extends WidgetWithRole {

    public CreateDataSetButton(final TreeGrid<DataContainer> tree, List<Component> buttonList) {
        super();
        if(drawWidget){
            Button createDS = new Button();
            createDS.setText(HarvesterUI.CONSTANTS.createDataSet());
            createDS.setIcon(HarvesterUI.ICONS.add());
            createDS.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    doAction(tree);
                }
            });
            buttonList.add(createDS);
        }
    }

    public CreateDataSetButton(Menu menu, final TreeGrid<DataContainer> tree) {
        if(drawWidget){
            MenuItem createDS = new MenuItem();
            createDS.setText(HarvesterUI.CONSTANTS.createDataSet());
            createDS.setIcon(HarvesterUI.ICONS.add());
            createDS.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    doAction(tree);
                }
            });
            menu.add(new SeparatorMenuItem());
            menu.add(createDS);
        }
    }

    private void doAction(TreeGrid<DataContainer> tree){
        BaseTreeModel selected = tree.getSelectionModel().getSelectedItems().get(0);
        Dispatcher.forwardEvent(AppEvents.ViewDataSourceForm, selected);
    }

    public void checkRole(){
        switch (HarvesterUI.UTIL_MANAGER.getLoggedUserRole()){
            case ADMIN : drawWidget = true;
                break;
            case NORMAL: drawWidget = true;
                break;
            case DATA_PROVIDER: drawWidget = true;
                break;
            default: drawWidget = false;
                break;
        }
    }
}
