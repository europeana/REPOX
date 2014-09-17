package harvesterUI.client.panels.harvesting;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import harvesterUI.client.panels.harvesting.runningTasks.RunningDataSetsPanel;
import harvesterUI.client.panels.harvesting.runningTasks.RunningTasksPanel;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 25-03-2011
 * Time: 22:26
 */
public class RunningTasksContainer extends LayoutContainer{

    private RunningTasksPanel runningTasksPanel;
    private RunningDataSetsPanel runningDataSetsPanel;

    public RunningTasksContainer(){
        setLayout(new BorderLayout());

        createRunningTasksPanel();
        createDataSetsPanel();
    }

    public void updateRunningTasks(){
        runningTasksPanel.loadTasks();
        runningDataSetsPanel.loadRunningDataSets();
    }


    private void createRunningTasksPanel(){
        runningTasksPanel = new RunningTasksPanel();
        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
        data.setMargins(new Margins(1, 5, 1, 1));
        add(runningTasksPanel,data);
    }


    private void createDataSetsPanel(){
        runningDataSetsPanel = new RunningDataSetsPanel();
        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.EAST, 150, 100, 750);
        data.setMargins(new Margins(1, 1, 1, 0));
        data.setCollapsible(true);
        add(runningDataSetsPanel,data);
    }


}
