package harvesterUI.client.panels.forms.dataSources;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.mdr.TransformationUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 13-07-2012
 * Time: 12:28
 */
public class TransformationsContainer extends LayoutContainer {

    private Grid<TransformationUI> transformationsGrid;
    private ContentPanel transformationsDSPanel;

    private DataSourceSchemaForm dataSourceSchemaForm;
    private MappingsDialog lastMappingsDialog;

    public TransformationsContainer(DataSourceSchemaForm dataSourceSchemaForm) {
        this.dataSourceSchemaForm = dataSourceSchemaForm;
        createMainGrid();
    }

    private void createMainGrid(){
        HBoxLayout transformContainerLayout = new HBoxLayout();
        transformContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        setLayout(transformContainerLayout);

        transformationsDSPanel = new ContentPanel();
        transformationsDSPanel.setHeaderVisible(false);
        transformationsDSPanel.setLayout(new FlowLayout(0));

        final ToolBar topToolbar = new ToolBar();
        transformationsDSPanel.setTopComponent(topToolbar);

        Button addTransformationButton = new Button();
        addTransformationButton.setText("Add");
        addTransformationButton.setIcon(HarvesterUI.ICONS.add());
        addTransformationButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                lastMappingsDialog = new MappingsDialog(transformationsGrid,dataSourceSchemaForm.getMetadataFormatCombo().getValue().getShortDesignation());
                lastMappingsDialog.showAndCenter();
            }
        });
        topToolbar.add(addTransformationButton);

        final Button deleteButton = new Button();
        deleteButton.setText("Delete");
        deleteButton.setIcon(HarvesterUI.ICONS.delete());
        deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                for(TransformationUI transformationUI : transformationsGrid.getSelectionModel().getSelectedItems())
                    transformationsGrid.getStore().remove(transformationUI);
            }
        });

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<TransformationUI> sm = new CheckBoxSelectionModel<TransformationUI>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        ColumnConfig column = new ColumnConfig("srcFormat", HarvesterUI.CONSTANTS.sourceFormat(),50);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        configs.add(column);
        column = new ColumnConfig("destFormat",HarvesterUI.CONSTANTS.destinationFormat(),50);
        column.setAlignment(Style.HorizontalAlignment.CENTER);
        configs.add(column);
        configs.add(new ColumnConfig("description",HarvesterUI.CONSTANTS.description(),150));

        ColumnModel cm = new ColumnModel(configs);

        ListStore<TransformationUI> store = new ListStore<TransformationUI>();
        transformationsGrid = new Grid<TransformationUI>(store, cm);
        transformationsGrid.setBorders(false);
        transformationsGrid.setAutoExpandColumn("description");
        transformationsGrid.setLoadMask(true);
        transformationsGrid.setSelectionModel(sm);
        transformationsGrid.addPlugin(sm);
        transformationsGrid.setStripeRows(true);
        transformationsGrid.getView().setForceFit(true);
        transformationsGrid.setHeight(100);
        transformationsGrid.disableTextSelection(false);
        transformationsGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<TransformationUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<TransformationUI> se) {
                if (se.getSelectedItem() != null) {
                    topToolbar.insert(deleteButton,1);
                } else {
                    topToolbar.remove(deleteButton);
                }
            }
        });

        LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.transformations());
        label.setStyleName("alignTop");
        label.setWidth(DataSourceForm.SPECIAL_FIELDS_LABEL_WIDTH);
        label.addStyleName("defaultFormFieldLabel");
        add(label, new HBoxLayoutData(new Margins(0, 2, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0));
        flex.setFlex(1);

        BorderLayoutData centerData = new BorderLayoutData(Style.LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0));

        transformationsDSPanel.add(transformationsGrid);

        add(transformationsDSPanel, flex);
    }

    public void refresh(){
//        transformationsDSPanel.layout(true);
//        layout(true);
    }

//    public ContentPanel getTransformationsDSPanel() {
//        return transformationsDSPanel;
//    }


    public MappingsDialog getLastMappingsDialog() {
        return lastMappingsDialog;
    }

    public Grid<TransformationUI> getTransformationsGrid() {
        return transformationsGrid;
    }
}
