package harvesterUI.client.panels.mdr.xmapper;

import harvesterUI.client.panels.mdr.forms.xmapper.SourceAndTargetFields;
import harvesterUI.shared.mdr.TransformationUI;
import pt.ist.mdr.mapping.ui.svg.client.mng.MappingApplicationManager;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 08-01-2013
 * Time: 15:34
 */
public class MDRMappingApplicationManager extends MappingApplicationManager{

    protected OpenMapInfo _mapInfo;

    public MDRMappingApplicationManager() {
        super();
        _mapInfo = null;
    }

    public boolean isNewMap() {
        return _mapInfo.isNewMap();
    }

    public void setNewMap(boolean newMap) {
        _mapInfo.setNewMap(newMap);
    }

    public OpenMapInfo getMapInfo() {
        return _mapInfo;
    }

    public void setMapInfo(OpenMapInfo mapInfo) {
        _mapInfo = mapInfo;
    }

    public TransformationUI getTransformation() {
        return _mapInfo.getTransformation();
    }

    public void setTransformation(TransformationUI t) {
        _mapInfo.setTransformation(t);
    }

    public boolean isUsed() {
        return _mapInfo != null;
    }
}
