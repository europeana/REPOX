package harvesterUI.shared.mdr.xmapper;

import java.io.Serializable;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 02-10-2012
 * Time: 18:32
 */
public class OpenMappingForm implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String _source, _target, _map;

    protected String _mapID = null;

    protected boolean _bFromURL;

    public OpenMappingForm() {
    }

    public OpenMappingForm(String source, String target, String map, boolean fromURL) {
        super();
        _source = source;
        _target = target;
        _map = map;
        _bFromURL = fromURL;
    }

    public String getSource(){
        return _source;
    }

    public String getTarget(){
        return _target;
    }

    public String getMap(){
        return _map;
    }

    public boolean isFromURL() {
        return _bFromURL;
    }

    public void setMapID(String id) {
        _mapID = id;
    }

    public String getMapID() {
        return _mapID;
    }
}
