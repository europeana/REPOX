package harvesterUI.client.panels.mdr.xmapper;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.mdr.forms.xmapper.OpenExistingMapDialog;
import harvesterUI.client.panels.mdr.forms.xmapper.OpenNewMapDialog;
import pt.ist.mdr.mapping.ui.svg.client.panel.MappingPanel;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 08-01-2013
 * Time: 14:59
 */
//TODO multi lang
public class XMApperContainer {

    protected MappingPanel _xmapperPanel;
    protected MDRMappingApplicationManager _manager;

    public XMApperContainer(){
        _manager = new MDRMappingApplicationManager();
        _xmapperPanel = new MappingPanel(_manager);
        _xmapperPanel.mask("Please select and option from the XMApper drop-down menu...");
    }

    public MappingPanel getXMApperPanel() {
        return _xmapperPanel;
    }

    public MDRMappingApplicationManager getManager() {
        return _manager;
    }

    public void showDialog(Boolean isNewMap) {
        //Todo re-use dialog windows
        if(isNewMap)
            new OpenNewMapDialog(this).showAndCenter();
        else
            new OpenExistingMapDialog(this).showAndCenter();
    }

    public void resetXMApperPanelIfUsed() {
        if(_manager.isUsed())
            resetXMApperPanel();

    }

    public void resetXMApperPanel() {
        _manager = new MDRMappingApplicationManager();
        _xmapperPanel = new MappingPanel(_manager);
        _xmapperPanel.mask("Please select and option from the XMApper drop-down menu...");
        Dispatcher.forwardEvent(AppEvents.ViewXMApperPanel);
    }

    public void setMapInfo(OpenMapInfo mapInfo) {
        _manager.setMapInfo(mapInfo);
        _xmapperPanel.setSourceSchemaTitle(mapInfo.getSourceSchema().getShortDesignation());
        _xmapperPanel.setTargetSchemaTitle(mapInfo.getDestSchema().getShortDesignation());
    }

}
