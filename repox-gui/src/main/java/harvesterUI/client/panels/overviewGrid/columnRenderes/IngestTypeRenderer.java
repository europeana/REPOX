package harvesterUI.client.panels.overviewGrid.columnRenderes;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Image;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.util.ImageButton;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 23-02-2012
 * Time: 15:53
 */
public class IngestTypeRenderer implements GridCellRenderer<ModelData> {

    public IngestTypeRenderer() {}

    public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                         ListStore<ModelData> store, Grid<ModelData> grid) {
        if(model instanceof DataSourceUI) {
            final DataSourceUI dataSourceUI;
//            if(model instanceof DataSourceUI)
                dataSourceUI = (DataSourceUI) model;
//            else {
//                // Data providers with more then 1 children or has no DSs
//                if(((DataProviderUI)model).getDataSourceUIList().size() > 1 ||
//                        ((DataProviderUI)model).getDataSourceUIList().size() <= 0)
//                    return "";
//                else
//                    dataSourceUI = ((DataProviderUI)model).getDataSourceUIList().get(0);
//            }

            if(dataSourceUI.getRecords() != null) {
                if(dataSourceUI.getIngest().startsWith("OAI-PMH")) {
                    Image image = UtilManager.createOAIImage();
                    image.addMouseDownHandler(new MouseDownHandler(){
                        public void onMouseDown(MouseDownEvent event) {
                            Dispatcher.get().dispatch(AppEvents.ViewOAISpecificSet,dataSourceUI);
                        }
                    });
                    ImageButton imageButton = new ImageButton();
                    imageButton.setIcon(HarvesterUI.ICONS.oai_icon());
                    imageButton.setToolTip(HarvesterUI.CONSTANTS.testOaiPMH());
                    imageButton.addListener(Events.OnMouseDown, new Listener<ButtonEvent>() {
                        public void handleEvent(ButtonEvent be) {
                            Dispatcher.get().dispatch(AppEvents.ViewOAISpecificSet,dataSourceUI);
                        }
                    });
                    imageButton.setStyleAttribute("float", "left");

                    LayoutContainer container = new LayoutContainer();
                    container.setLayout(new FlowLayout());
                    container.add(imageButton,new FlowData(new Margins(0, 5, 0, 0)));
                    container.add(new LabelToolItem(dataSourceUI.getIngest()));
                    return container;
                }
            }
            return dataSourceUI.getIngest();
        }
        else
            return "";
    }
}
