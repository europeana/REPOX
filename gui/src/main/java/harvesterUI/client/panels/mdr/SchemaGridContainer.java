package harvesterUI.client.panels.mdr;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.grid.DataGridContainer;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.mdr.SchemaTreeUI;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AC
 * Date: 17-07-2012
 * Time: 15:29
 */
public class SchemaGridContainer extends DataGridContainer{

    private SchemasPanel _gridPanel;

    public SchemaGridContainer(SchemasPanel gridPanel, TreeGrid<SchemaTreeUI> tree){
        _gridPanel = gridPanel;
        mainDataGrid = tree;
    }

    public void loadGridData(PagingLoadConfig config){
        //_gridPanel.mask("Loading Schemas...");
        AsyncCallback<List<harvesterUI.shared.mdr.SchemaTreeUI>> callback = new AsyncCallback<List<harvesterUI.shared.mdr.SchemaTreeUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<harvesterUI.shared.mdr.SchemaTreeUI> schemasTreeData) {
                mainDataGrid.getStore().removeAll();
                mainDataGrid.getTreeStore().add(schemasTreeData, true);
                mainDataGrid.expandAll();
                UtilManager.unmaskCentralPanel();
                _gridPanel.getPagingToolbar().loadPagingInfo();
                _gridPanel.getPagingToolbar().showRefreshIconRunning(false);
               // _gridPanel.unmask();
            }
        };
        TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        service.getPagedSchemas(config, callback);
    }

}
