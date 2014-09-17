package harvesterUI.client.util;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 11:45
 */
public abstract class GridOperations {

    protected static int STATIC_BUTTONS_INDEX = 3;

    protected List<Component> componentList;

    public GridOperations() {
        componentList = new ArrayList<Component>();
    }

    protected void createSeparator(){
        if(componentList.size() > 0){
            SeparatorToolItem lastSeparator = new SeparatorToolItem();
            componentList.add(lastSeparator);
        }
    }

    public abstract void hideButtons(ToolBar toolBar);
    public abstract void showButtons(ToolBar toolBar);
}
