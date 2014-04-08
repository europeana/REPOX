package harvesterUI.client.panels.dataSourceView;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 16-09-2011
 * Time: 16:52
 */
public class ChooseDataSetDialog extends FormDialog {
    private DataSetOperationsServiceAsync service;
    private ListStore<SimpleDataSetInfo> store;
    private Grid<SimpleDataSetInfo> grid;
    private TextField<String> searchBox;

    public ChooseDataSetDialog() {
        super(0.3,0.3);

        setHeight((int)(Window.getClientHeight()));
        setWidth(220);

        setModal(false);
        service = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);
        createMoveDSDialog();
    }

    private void createMoveDSDialog() {
        setHeading("Choose Data Set To View");
        setIcon(HarvesterUI.ICONS.view_info_icon());
        setResizable(true);

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column =  new ColumnConfig();
        column.setId("name");
        column.setHeader(HarvesterUI.CONSTANTS.name());
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        column.setWidth(75);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        store = new ListStore<SimpleDataSetInfo>();

        grid = new Grid<SimpleDataSetInfo>(store, cm);
        grid.setBorders(false);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noDataSetSelected());
        grid.setStripeRows(true);
        grid.setColumnLines(true);
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
        grid.setHideHeaders(true);
        grid.setColumnReordering(true);
        grid.setLayoutData(new FitLayout());
        grid.getView().setForceFit(true);
        grid.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                loadData();
            }
        });
        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<SimpleDataSetInfo>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleDataSetInfo> se) {
                Dispatcher.forwardEvent(AppEvents.ViewDataSetInfo, se.getSelectedItem().getId());
            }
        });
        add(grid);

        ToolBar toolBar = new ToolBar();
        toolBar.setAlignment(Style.HorizontalAlignment.CENTER);
        setTopComponent(toolBar);

        searchBox = new TextField<String>();
        searchBox.setEmptyText("Search...");
        KeyListener keyListener = new KeyListener(){
            public void componentKeyUp(ComponentEvent event) {
                grid.getStore().filter("id",searchBox.getValue());
            }
        };
        searchBox.addKeyListener(keyListener);
        toolBar.add(searchBox);

        Button clearSearchButton = new Button("",HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                searchBox.clear();
                grid.getStore().filter("id");
            }
        });
        toolBar.add(clearSearchButton);

//        toolBar.add(viewButton);
    }

    public void loadData() {
        mask("Loading Data Sets...");
        AsyncCallback<List<SimpleDataSetInfo>> callback = new AsyncCallback<List<SimpleDataSetInfo>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<SimpleDataSetInfo> result) {
                store.removeAll();
                store.add(result);
                unmask();
            }
        };
        ((DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE)).getAllDataSets(callback);
    }
}

