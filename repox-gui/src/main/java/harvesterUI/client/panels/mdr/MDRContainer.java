package harvesterUI.client.panels.mdr;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.mdr.xmapper.XMApperContainer;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 12/06/12
 * Time: 18:49
 */
public class MDRContainer extends ContentPanel{

    private ToggleButton schemas,mappings, xmapper;
    private MappingsPanel mappingsPanel;
    private SchemasPanel schemasPanel;
    private XMApperContainer xmapperContainer;
    private ToolBar topToolbar;
    private LayoutContainer overallContainer;

    private static int TOP_TOOLBAR_SIZE = 34;

    public MDRContainer() {
        setHeaderVisible(false);
        setLayout(new FillLayout());

        overallContainer = new LayoutContainer();
        overallContainer.setLayout(new BorderLayout());
        add(overallContainer);

        topToolbar = new ToolBar();
        topToolbar.add(new LabelToolItem("<img src=resources/images/icons/schema_mapper_16x16.png>"));
        topToolbar.setTitle(HarvesterUI.CONSTANTS.schemaMapper());
        LabelToolItem mdrTitle = new LabelToolItem("MDR");
        mdrTitle.addStyleName("mdr-header-title");
        topToolbar.add(mdrTitle);
        topToolbar.add(new FillToolItem());
        topToolbar.setHeight(TOP_TOOLBAR_SIZE);
//        topToolbar.setAlignment(Style.HorizontalAlignment.CENTER);
        addToolbar();

        ToolBar switchPanelToolbar = new ToolBar();
        switchPanelToolbar.setSpacing(5);
        switchPanelToolbar.addStyleName("topNavToolbar");
        topToolbar.add(switchPanelToolbar);

        mappingsPanel = new MappingsPanel();
        schemasPanel = new SchemasPanel();
        xmapperContainer = null;

        schemas = new ToggleButton(HarvesterUI.CONSTANTS.schemas(),HarvesterUI.ICONS.schemas_icon());
        schemas.setAllowDepress(false);
        mappings = new ToggleButton(HarvesterUI.CONSTANTS.mappings(),HarvesterUI.ICONS.mappings_icon());
        mappings.setAllowDepress(false);
        xmapper = new ToggleButton("XMApper [BETA 1.0]", HarvesterUI.ICONS.schema_mapper_icon()); //TODO Constant and Icon
        xmapper.setAllowDepress(false);
        //Xmapper menu
        Menu xmenu = new Menu();
        MenuItem tmpItem = new MenuItem("New Transformation");
        tmpItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent menuEvent) {
                showXMApperOpenDialog(true);
            }
        });
        xmenu.add(tmpItem);
        tmpItem = new MenuItem("Edit Transformation");
        tmpItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent menuEvent) {
                showXMApperOpenDialog(false);
            }
        });
        xmenu.add(tmpItem);
        xmapper.setMenu(xmenu);
        //
        xmapper.setToggleGroup("mdr_panels_button_group");
        mappings.setToggleGroup("mdr_panels_button_group");
        schemas.setToggleGroup("mdr_panels_button_group");
        schemas.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ViewSchemasPanel);
            }
        });
        mappings.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ViewMappingsPanel);
            }
        });
        xmapper.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.ViewXMApperPanel);
            }
        });

        switchPanelToolbar.add(schemas);
        switchPanelToolbar.add(mappings);
        switchPanelToolbar.add(xmapper);
    }

    protected void showXMApperOpenDialog(final boolean isNewMap) {
        final SelectionListener<ButtonEvent> openDialogListener = new SelectionListener<ButtonEvent> () {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                xmapperContainer.resetXMApperPanelIfUsed();
                xmapperContainer.showDialog(isNewMap);
            }
        };

        if(xmapperContainer.getManager().isUsed())
            if(!xmapperContainer.getManager().isSaved()) {
                HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(),
                        "Any unsaved data in the XMApper will be lost. Proceed?", openDialogListener); //TODO multi lang
                return;
            }

        xmapperContainer.resetXMApperPanelIfUsed();
        xmapperContainer.showDialog(isNewMap);
    }

    protected void addToolbar() {
        BorderLayoutData northData = new BorderLayoutData(Style.LayoutRegion.NORTH, TOP_TOOLBAR_SIZE);
        northData.setMargins(new Margins(0));
        overallContainer.add(topToolbar, northData);
    }

    protected void addPanel(Widget panel) {
        BorderLayoutData southData = new BorderLayoutData(Style.LayoutRegion.CENTER);
        southData.setMargins(new Margins(0));
        overallContainer.add(panel, southData);
    }

    protected void removePanel() {
        overallContainer.removeAll();
        addToolbar();
    }

    public void activateSchemasPanel(){
        removePanel();
        addToolbar();
        addPanel(schemasPanel);
        layout();
        schemas.toggle(true);
    }

    public void activateMappingsPanel(){
        removePanel();
        addPanel(mappingsPanel);
        layout();
        mappings.toggle(true);
    }

    public void activateXMApperPanel(){
        removePanel();
        if(xmapperContainer == null) {
            xmapperContainer = new XMApperContainer();
            addPanel(xmapperContainer.getXMApperPanel());
        }
        else {
            addPanel(xmapperContainer.getXMApperPanel());
        }
        layout();
        xmapper.toggle(true);
    }


//    @Override
//    protected void onAttach() {
//        super.onAttach();
//        schemas.fireEvent(Events.Select);
//        schemas.toggle(true);
//    }
}
