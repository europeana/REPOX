package harvesterUI.client.panels.mdr.forms;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 13-03-2011
 * Time: 16:25
 */
public class ShowConnectedDSDialog extends FormDialog {

    public ShowConnectedDSDialog(List<SimpleDataSetInfo> simpleDataSetInfoList) {
        super(0.4,0.5);

        createForm(simpleDataSetInfoList);
        setHeading("Usage List");
        setIcon(HarvesterUI.ICONS.table());
        setResizable(true);
        setScrollMode(Style.Scroll.AUTO);
        setLayout(new FitLayout());
    }

    private void createForm(List<SimpleDataSetInfo> simpleDataSetInfoList) {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        configs.add(new ColumnConfig("name",HarvesterUI.CONSTANTS.name(),150));

        ListStore<SimpleDataSetInfo> store = new ListStore<SimpleDataSetInfo>();
        store.add(simpleDataSetInfoList);

        ColumnModel cm = new ColumnModel(configs);

        final Grid<SimpleDataSetInfo> grid = new Grid<SimpleDataSetInfo>(store, cm);
        grid.getView().setForceFit(true);
        grid.setStripeRows(true);
        grid.setLoadMask(true);
        grid.setTrackMouseOver(false);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noTransformationsAvailable());

        ToolBar toolBar = new ToolBar();
        Button viewInfo = new Button();
        viewInfo.setText(HarvesterUI.CONSTANTS.viewInfo());
        viewInfo.setIcon(HarvesterUI.ICONS.view_info_icon());
        viewInfo.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                if(grid.getSelectionModel().getSelectedItem() == null) {
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.selection(),HarvesterUI.CONSTANTS.selectionEmptyWarning());
                }

                AsyncCallback<DataSourceUI> callback = new AsyncCallback<DataSourceUI>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(DataSourceUI dataSourceUI) {
                        if(dataSourceUI == null) {
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.viewDataSet(),HarvesterUI.CONSTANTS.noDataSetFound());
                            Dispatcher.forwardEvent(AppEvents.LoadMainData);
                        } else
                            Dispatcher.forwardEvent(AppEvents.ViewDataSetInfo, dataSourceUI);
                    }
                };
                DataManagementServiceAsync service = (DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE);
                service.getDataSetInfo(grid.getSelectionModel().getSelectedItem().getId(), callback);
            }
        });
        toolBar.add(viewInfo);

        setTopComponent(toolBar);

        add(grid);
    }
}
