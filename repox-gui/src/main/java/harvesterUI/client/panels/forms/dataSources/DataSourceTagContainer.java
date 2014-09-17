package harvesterUI.client.panels.forms.dataSources;

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
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 13-07-2012
 * Time: 12:28
 */
public class DataSourceTagContainer extends LayoutContainer {

    private ContentPanel tagsDSPanel;
    private Grid<DataSetTagUI> tagsChosenList;
    private DataSourceTagsDialog dataSourceTagsDialog;

    public DataSourceTagContainer() {
        createTaggingLists();
    }

    private void createTaggingLists(){
        HBoxLayout transformContainerLayout = new HBoxLayout();
        transformContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        setLayout(transformContainerLayout);

        tagsDSPanel = new ContentPanel();
        tagsDSPanel.setHeaderVisible(false);
        tagsDSPanel.setLayout(new FlowLayout(0));

        final ToolBar topToolbar = new ToolBar();
        tagsDSPanel.setTopComponent(topToolbar);

        Button addTransformationButton = new Button();
        addTransformationButton.setText("&nbsp&nbspAdd");
        addTransformationButton.setIcon(HarvesterUI.ICONS.tag_add_icon());
        addTransformationButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                dataSourceTagsDialog = new DataSourceTagsDialog(tagsChosenList);
                dataSourceTagsDialog.showAndCenter();
            }
        });
        topToolbar.add(addTransformationButton);

        final Button deleteButton = new Button();
        deleteButton.setText("&nbsp&nbspDelete");
        deleteButton.setIcon(HarvesterUI.ICONS.tag_remove_icon());
        deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                for(DataSetTagUI dataSetTagUI : tagsChosenList.getSelectionModel().getSelectedItems())
                    tagsChosenList.getStore().remove(dataSetTagUI);
            }
        });

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        CheckBoxSelectionModel<DataSetTagUI> sm = new CheckBoxSelectionModel<DataSetTagUI>();
        sm.setSelectionMode(Style.SelectionMode.MULTI);
        configs.add(sm.getColumn());

        ColumnConfig column = new ColumnConfig("name", HarvesterUI.CONSTANTS.name(),50);
        column.setAlignment(Style.HorizontalAlignment.LEFT);
        configs.add(column);
        ColumnModel cm = new ColumnModel(configs);

        ListStore<DataSetTagUI> store = new ListStore<DataSetTagUI>();
        tagsChosenList = new Grid<DataSetTagUI>(store, cm);
        tagsChosenList.setBorders(false);
        tagsChosenList.setAutoExpandColumn("name");
        tagsChosenList.setLoadMask(true);
        tagsChosenList.setSelectionModel(sm);
        tagsChosenList.addPlugin(sm);
        tagsChosenList.setStripeRows(true);
        tagsChosenList.getView().setForceFit(true);
        tagsChosenList.setHeight(100);
        tagsChosenList.disableTextSelection(false);
        tagsChosenList.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<DataSetTagUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<DataSetTagUI> se) {
                if (se.getSelectedItem() != null) {
                    topToolbar.insert(deleteButton,1);
                } else {
                    topToolbar.remove(deleteButton);
                }
            }
        });

        LabelToolItem label = new LabelToolItem("Tags");
        label.setStyleName("alignTop");
        label.setWidth(DataSourceForm.SPECIAL_FIELDS_LABEL_WIDTH);
        label.addStyleName("defaultFormFieldLabel");
        add(label, new HBoxLayoutData(new Margins(0, 2, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0)));
        HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, UtilManager.DEFAULT_HBOX_BOTTOM_MARGIN, 0));
        flex.setFlex(1);

        BorderLayoutData centerData = new BorderLayoutData(Style.LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0));

        tagsDSPanel.add(tagsChosenList);

        add(tagsDSPanel, flex);
    }

    public List<DataSetTagUI> getTags(){
        return tagsChosenList.getStore().getModels();
    }

//    public void reload(final DataSourceUI dataSourceUI, final boolean reset){
//        sourceList.getStore().removeAll();
//        if(targetList.getStore().getModels().size() <= 0 || reset)
//            targetList.getStore().removeAll();
//
//        AsyncCallback<List<DataSetTagUI>> callback = new AsyncCallback<List<DataSetTagUI>>() {
//            public void onFailure(Throwable caught) {
//                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
//            }
//            public void onSuccess(List<DataSetTagUI> tags) {
//                if(sourceList.getStore().getModels().size() <= 0)
//                    sourceList.getStore().add(tags);
//
//                if(dataSourceUI != null)
//                    loadEditData(dataSourceUI,tags,reset);
//
//                for(DataSetTagUI dataSetTagUI : targetList.getStore().getModels()){
//                    for(DataSetTagUI sourceDSTagUI : sourceList.getStore().getModels()){
//                        if(sourceDSTagUI.getName().equals(dataSetTagUI.getName()))
//                            sourceList.getStore().remove(sourceDSTagUI);
//                    }
//                }
//            }
//        };
//        TagsServiceAsync service = (TagsServiceAsync) Registry.get(HarvesterUI.TAGS_SERVICE);
//        service.getAllTags(callback);
//    }

    public void reset(){
        tagsChosenList.getStore().removeAll();
    }

    public void loadEditData(DataSourceUI dataSourceUI){
        tagsChosenList.getStore().removeAll();
        tagsChosenList.getStore().add(dataSourceUI.getTags());
    }

    public DataSourceTagsDialog getDataSourceTagsDialog() {
        return dataSourceTagsDialog;
    }
}
