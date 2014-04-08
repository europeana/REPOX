package harvesterUI.client.panels.administration.userManagement;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.dataSources.DataSourceForm;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.DataProviderUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 13-07-2012
 * Time: 12:28
 */
public class DataProvidersContainer extends LayoutContainer {

    private Grid<DataProviderUI> dataProviderUIGrid;
    private ContentPanel dataProvidersPanel;

    public DataProvidersContainer() {
        setId("dpContainer");
        createMainGrid();
    }

    private void createMainGrid(){
        HBoxLayout transformContainerLayout = new HBoxLayout();
        transformContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        setLayout(transformContainerLayout);

        dataProvidersPanel = new ContentPanel();
        dataProvidersPanel.setHeaderVisible(false);
        dataProvidersPanel.setLayout(new FlowLayout(0));

        final ToolBar topToolbar = new ToolBar();
        dataProvidersPanel.setTopComponent(topToolbar);

        Button addDataProviderButton = new Button();
        addDataProviderButton.setText("Add");
        addDataProviderButton.setIcon(HarvesterUI.ICONS.add());
        addDataProviderButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new AddDataProvidersDialog(dataProviderUIGrid).showAndCenter();
            }
        });
        topToolbar.add(addDataProviderButton);

        final Button deleteButton = new Button();
        deleteButton.setText("Delete");
        deleteButton.setIcon(HarvesterUI.ICONS.delete());
        deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                for(DataProviderUI dataProviderUI : dataProviderUIGrid.getSelectionModel().getSelectedItems())
                    dataProviderUIGrid.getStore().remove(dataProviderUI);
            }
        });

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
        column = new ColumnConfig("name",HarvesterUI.CONSTANTS.name(),50);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        ListStore<DataProviderUI> store = new ListStore<DataProviderUI>();
        dataProviderUIGrid = new Grid<DataProviderUI>(store, cm);
        dataProviderUIGrid.setBorders(false);
        dataProviderUIGrid.setAutoExpandColumn("name");
        dataProviderUIGrid.setLoadMask(true);
        dataProviderUIGrid.setSelectionModel(sm);
        dataProviderUIGrid.addPlugin(sm);
        dataProviderUIGrid.setStripeRows(true);
        dataProviderUIGrid.getView().setForceFit(true);
        dataProviderUIGrid.setHeight(150);
        dataProviderUIGrid.disableTextSelection(false);
        dataProviderUIGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<DataProviderUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<DataProviderUI> se) {
                if (se.getSelectedItem() != null) {
                    topToolbar.insert(deleteButton,1);
                } else {
                    topToolbar.remove(deleteButton);
                }
            }
        });

        LabelToolItem label = new LabelToolItem("Allowed Data Providers" + HarvesterUI.REQUIRED_STR);
        label.setStyleName("alignTop");
        label.setWidth(DataSourceForm.SPECIAL_FIELDS_LABEL_WIDTH - 15);
        label.addStyleName("defaultFormFieldLabel");
        add(label, new HBoxLayoutData(new Margins(0, 2, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0));
        flex.setFlex(1);

        BorderLayoutData centerData = new BorderLayoutData(Style.LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0));

        dataProvidersPanel.add(dataProviderUIGrid);

        add(dataProvidersPanel, flex);
    }

    public List<String> getAllowedDataProviderIds(){
        List<String> dpIds = new ArrayList<String>();
        for(DataProviderUI dataProviderUI : getDataProviderUIGrid().getStore().getModels()){
            dpIds.add(dataProviderUI.getId());
        }
        return dpIds;
    }

    public Grid<DataProviderUI> getDataProviderUIGrid() {
        return dataProviderUIGrid;
    }
}
