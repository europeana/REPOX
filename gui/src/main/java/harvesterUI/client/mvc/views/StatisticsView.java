package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.statistics.StatisticsPanel;
import harvesterUI.shared.statistics.StatisticsType;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 26-03-2011
 * Time: 17:37
 */
public class StatisticsView extends View {

    private StatisticsPanel statisticsPanel;

    public StatisticsView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event){
        if (event.getType() == AppEvents.ViewStatistics){
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();
            centerPanel.add(statisticsPanel);
            statisticsPanel.getStatistics(StatisticsType.ALL);
            centerPanel.layout();
        }
    }

    @Override
    protected void initialize(){
        statisticsPanel = new StatisticsPanel();
    }
}
