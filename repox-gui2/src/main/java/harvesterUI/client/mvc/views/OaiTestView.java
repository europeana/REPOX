package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.oaiTest.OaiTestPanel;
import harvesterUI.client.panels.oaiTest.RestRecordOperationsPanel;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 20:33
 */
public class OaiTestView extends View {

    private OaiTestPanel oaiTestPanel;
    private RestRecordOperationsPanel restRecordOperationsPanel;

    public OaiTestView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event)
    {
//        if (event.getType() == AppEvents.ChangeToLightVersion)
//            makeLightChanges();
//        else if (event.getType() == AppEvents.ChangeToEuropeana)
//            makeEuropeanaChanges();
        if (event.getType() == AppEvents.ViewOAITest) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();

            eraseBrowseNavPanel();

            centerPanel.add(oaiTestPanel);
            oaiTestPanel.newOAITest();
            centerPanel.layout();
        }
        if (event.getType() == AppEvents.ViewOAISpecificSet) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();

            eraseBrowseNavPanel();

            centerPanel.add(oaiTestPanel);
            DataSourceUI dataSourceUI = (DataSourceUI) event.getData();
            oaiTestPanel.loadOAITest(dataSourceUI);
            centerPanel.layout();
        }else if (event.getType() == AppEvents.ViewRestRecordOperations) {
            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();

            eraseBrowseNavPanel();

            centerPanel.add(restRecordOperationsPanel);
            centerPanel.layout();
        }
    }

    @Override
    protected void initialize()
    {
        oaiTestPanel = new OaiTestPanel();
        restRecordOperationsPanel = new RestRecordOperationsPanel();
    }

    protected void makeEuropeanaChanges()
    {

    }

    protected void makeLightChanges()
    {

    }

    private void eraseBrowseNavPanel(){
        BorderLayout west = (BorderLayout) Registry.get("mainBorderLayout");
        west.hide(Style.LayoutRegion.WEST);
    }
}
