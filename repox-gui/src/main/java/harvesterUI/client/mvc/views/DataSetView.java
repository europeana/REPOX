package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.dataSourceView.DataSetViewInfoDialog;
import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 21-03-2011
 * Time: 16:33
 */
public class DataSetView extends View {

    private DataSetViewInfoDialog currentDataSetView;

    public DataSetView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.ViewDataSetInfo) {
            if(currentDataSetView != null)
                currentDataSetView.hide();

            if(event.getData() instanceof DataSourceUI) {
                DataSourceUI dataSourceUI = (DataSourceUI) event.getData();
                currentDataSetView = new DataSetViewInfoDialog(dataSourceUI);
                UtilManager.unmaskCentralPanel();
                currentDataSetView.showAndCenter();
            }else if(event.getData() instanceof String) {
                String dataSetId = event.getData();
                AsyncCallback<DataSourceUI> callback = new AsyncCallback<DataSourceUI>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(DataSourceUI result) {
                        currentDataSetView = new DataSetViewInfoDialog(result);
                        currentDataSetView.setModal(false);
                        UtilManager.unmaskCentralPanel();
                        currentDataSetView.showAndCenter();
                        currentDataSetView.setPosition(Window.getClientWidth()/2 + 110,0);
                        currentDataSetView.setHeight(Window.getClientHeight());
                        currentDataSetView.setWidth((int) (Window.getClientWidth() * 0.5) - 110);
                    }
                };
                ((DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE)).getDataSetInfo(dataSetId, callback);
            }
        }
    }

    @Override
    protected void initialize(){
//        dataSetViewInfo = new DataSetViewInfo();
    }
}

