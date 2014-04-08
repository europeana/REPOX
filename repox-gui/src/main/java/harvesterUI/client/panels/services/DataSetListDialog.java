package harvesterUI.client.panels.services;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 13-03-2011
 * Time: 16:25
 */
public class DataSetListDialog extends FormDialog {

    private Grid<DataSourceUI> grid;

    public DataSetListDialog(DataSetListParameter dataSetListParameter) {
        super(0.4,0.2);
        createForm(dataSetListParameter);
        setHeading("Data Sets to Compare");
        setIcon(HarvesterUI.ICONS.add());
    }

    private void createForm(final DataSetListParameter dataSetListParameter) {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<DataSourceUI> mainGridSelectionModel = new CheckBoxSelectionModel<DataSourceUI>();
        mainGridSelectionModel.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(mainGridSelectionModel.getColumn());

        ColumnConfig column =  new ColumnConfig("dataSourceSet","Set",75);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        ListStore<DataSourceUI> store = new ListStore<DataSourceUI>();

        grid = new Grid<DataSourceUI>(store, cm);
        grid.setBorders(false);
        grid.getView().setEmptyText("No other data providers available.");
        grid.setStripeRows(true);
        grid.setColumnLines(true);
        grid.setSelectionModel(mainGridSelectionModel);
        grid.addPlugin(mainGridSelectionModel);
        grid.setHideHeaders(true);
        grid.setColumnReordering(true);
        grid.setLayoutData(new FitLayout());
        grid.getView().setForceFit(true);
        add(grid);

        ToolBar toolBar = new ToolBar();
        toolBar.setAlignment(Style.HorizontalAlignment.CENTER);

        Button move = new Button("Add Data Sets");
        move.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                if (grid.getSelectionModel().getSelectedItems().size() == 0)
                    return;

                // Fill the text field
                String allDataSetsIds = "";
                for(DataSourceUI dataSourceUI : grid.getSelectionModel().getSelectedItems()){
                    allDataSetsIds = allDataSetsIds.concat(dataSourceUI.getDataSourceSet() + ";");
                }
                dataSetListParameter.getTextField().setValue(allDataSetsIds);
                hide();
            }
        });
        toolBar.add(move);
        setBottomComponent(toolBar);

        populateStore();
    }

    private void populateStore() {
        // Fill store with data providers
        grid.getStore().removeAll();
        List<BaseTreeModel> models;
        // todo: get all data sets
//        if(HarvesterUI.getProjectType() == ProjectType.EUROPEANA)
//            models = mainDataManager.getMainDataEuropeana().getAllData();
//        else
//            models = mainDataManager.getMainDataTel().getAllData();

//        for(BaseTreeModel model : models) {
//            if(model instanceof DataSourceUI) {
//                grid.getStore().add((DataSourceUI)model);
//            }
//        }
    }

    public void edit(String values){
        if(values != null && !values.isEmpty()){
            String delimType = ";";
            String[] tokensType = values.split(delimType);
            for(int i=0; i< tokensType.length ; i++){
                String dataSetId = tokensType[i];
                for(DataSourceUI dataSourceUI : grid.getStore().getModels()){
                    if(dataSourceUI.getDataSourceSet().equals(dataSetId))
                        grid.getSelectionModel().select(dataSourceUI,true);
                }
            }
        }
    }
}
