package harvesterUI.client.panels.overviewGrid.columnRenderes;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.mdr.TransformationUI;

import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 23-02-2012
 * Time: 15:53
 */
public class OaiSchemasRenderer implements GridCellRenderer<ModelData> {

    public OaiSchemasRenderer() {}

    public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                         ListStore<ModelData> store, Grid<ModelData> grid) {
        if(model instanceof DataSourceUI) {
            final DataSourceUI dataSourceUI = (DataSourceUI) model;

            HorizontalPanel container = new HorizontalPanel();

            container.add(createOAILink(dataSourceUI.getSourceMDFormat(), dataSourceUI.getDataSourceSet(), false));

            List<TransformationUI> dsTransformations = dataSourceUI.getMetadataTransformations();
            for(TransformationUI transformationUI : dsTransformations){
                container.add(createOAILink(transformationUI.getDestFormat(),dataSourceUI.getDataSourceSet(),true));
            }

            return container;
        }
        else
            return "";
    }

    private HorizontalPanel createOAILink(final String sourceMDFormat, final String dataSet, boolean useSeparator){
        HorizontalPanel container = new HorizontalPanel();

        if(useSeparator){
            Label separatorLabel = new Label("|");
            separatorLabel.setStyleName("oai-schemas-transformation-separator");
            container.add(separatorLabel);
        }

        Label sourceHTML = new Label(sourceMDFormat);
        sourceHTML.setStyleName("oai_schemas_hyperlink_style_label");
        sourceHTML.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                openOaiResultsWindow(dataSet, sourceMDFormat);
            }
        });

        container.add(sourceHTML);
        return container;
    }

    private void openOaiResultsWindow(String dataSet, String metadataFormat) {
        String link = UtilManager.getOaiServerUrl();
        link += "?verb=ListRecords&set="+dataSet+"&metadataPrefix=" + metadataFormat;
        Window.open(link,"_blank","");
    }
}
