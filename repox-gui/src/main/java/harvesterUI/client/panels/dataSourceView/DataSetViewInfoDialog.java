package harvesterUI.client.panels.dataSourceView;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 29-01-2013
 * Time: 19:50
 */
public class DataSetViewInfoDialog extends FormDialog{

    public DataSetViewInfoDialog(DataSourceUI dataSourceUI) {
        super(0.9,0.8);
        setIcon(HarvesterUI.ICONS.view_info_icon());

        DataSetViewInfo dataSetViewInfo =new DataSetViewInfo();
        dataSetViewInfo.createForm(dataSourceUI);
        setHeading(dataSetViewInfo.getHeading());
        add(dataSetViewInfo);
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setLayout(new FitLayout());
    }

    @Override
    protected void onHide() {
        super.onHide();
//        LayoutContainer center = Registry.get(AppView.CENTER_PANEL);
//        if(center.getItemCount() == 0)
        Dispatcher.forwardEvent(AppEvents.LoadMainData);
//        else
//            History.back();
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width,height);
        super.layout(true);
//        operationsSet.layout(true);
        layout(true);
    }


}

