package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.userManagement.UserManagementServiceAsync;
import harvesterUI.shared.dataTypes.DataProviderUI;
import harvesterUI.shared.tasks.ScheduledTaskUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-07-2011
 * Time: 15:31
 */
public class AddDataProvidersDialog extends FormDialog {

    private UserManagementServiceAsync service;
    private Grid<DataProviderUI> grid;
    private PagingLoader<PagingLoadResult<ModelData>> loader;

    public AddDataProvidersDialog(final Grid<DataProviderUI> dataProviderUIGrid) {
        super(0.6,0.5);
        service = (UserManagementServiceAsync)Registry.get(HarvesterUI.USER_MANAGEMENT_SERVICE);
        setIcon(HarvesterUI.ICONS.schema_mapper_icon());
        setHeading("Available Data Providers");
        setLayout(new FitLayout());
        setBodyBorder(false);
        setBorders(false);

        ToolBar topToolbar = new ToolBar();
        setTopComponent(topToolbar);

        createDataProvidersGrid();

        Button addDataProviderButton = new Button();
        addDataProviderButton.setText("Add Selected");
        addDataProviderButton.setIcon(HarvesterUI.ICONS.add());
        addDataProviderButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                if (grid.getSelectionModel().getSelectedItems().size() == 0) {
                    HarvesterUI.UTIL_MANAGER.getInfoBox("Add Data Providers", "No data providers selected.");
                    return;
                }

                for (DataProviderUI dataProviderUI : grid.getSelectionModel().getSelectedItems()) {
                    if (!dataProviderAlreadyExists(dataProviderUI,dataProviderUIGrid))
                        dataProviderUIGrid.getStore().add(dataProviderUI);
                }
                hide();
            }
        });
        topToolbar.insert(addDataProviderButton, 0);
    }

    private boolean dataProviderAlreadyExists(DataProviderUI dataProviderUI,Grid<DataProviderUI> dataProviderUIGrid){
        for(DataProviderUI existingDataProvider : dataProviderUIGrid.getStore().getModels()){
            if(existingDataProvider.getId().equals(dataProviderUI.getId())){
                return true;
            }
        }
        return false;
    }

    private void createDataProvidersGrid(){
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<DataProviderUI> sm = new CheckBoxSelectionModel<DataProviderUI>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        ColumnConfig column = new ColumnConfig("country", HarvesterUI.CONSTANTS.country(),25);
        column.setRenderer(new GridCellRenderer<DataProviderUI>() {
            public Object render(DataProviderUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<DataProviderUI> store, Grid<DataProviderUI> grid) {
                if(model != null) {
                    if(model.get("country") != null && !model.get("country").equals("none")){
                        return "<img src=\"resources/images/countries/" +
                                model.get("country") + ".png\" alt=\"" + model.get("countryName") + "\" title=\"" +
                                model.get("countryName") + "\"/> ";
                    }
                }
                return "";
            }
        });
        configs.add(column);
        column = new ColumnConfig("name",HarvesterUI.CONSTANTS.name(),100);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        RpcProxy<PagingLoadResult<DataProviderUI>> proxy = new RpcProxy<PagingLoadResult<DataProviderUI>>() {
            @Override
            public void load(Object loadConfig, AsyncCallback<PagingLoadResult<DataProviderUI>> callback) {
                service.getPagedAvailableDataProviders((PagingLoadConfig) loadConfig, callback);
            }
        };

        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(true);

        ListStore<DataProviderUI> store = new ListStore<DataProviderUI>(loader);

        PagingToolBar toolBar = new PagingToolBar(25);
        toolBar.bind(loader);

        setBottomComponent(toolBar);

        grid = new Grid<DataProviderUI>(store, cm);
        grid.getView().setForceFit(true);
        grid.setSelectionModel(sm);
        grid.setStripeRows(true);
        grid.setLoadMask(true);
        grid.setTrackMouseOver(false);
        grid.addPlugin(sm);
        grid.getView().setEmptyText("No Data Providers Available.");
        grid.addListener(Events.Attach, new Listener<GridEvent<ScheduledTaskUI>>() {
            public void handleEvent(GridEvent<ScheduledTaskUI> be) {
                loadData();
            }
        });

        add(grid);
    }

    public void loadData(){
        loader.load(0,25);
    }

}
