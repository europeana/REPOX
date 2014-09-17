package harvesterUI.client.panels.mdr.xmapper;

import harvesterUI.client.panels.mdr.forms.xmapper.SchemasDetailsContainer;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;
import harvesterUI.shared.mdr.TransformationUI;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 28-01-2013
 * Time: 16:53
 */
public class OpenMapInfo {

    protected boolean _bNewMap;
    SchemasDetailsContainer _details;

    SchemaUI _sourceSchema, _destSchema;
    SchemaVersionUI _sourceVersion, _destVersion;

    TransformationUI _transformation;

    public OpenMapInfo (boolean newMap, SchemasDetailsContainer details, SchemaUI sourceSch,
                        SchemaUI destSch, SchemaVersionUI sourceVer, SchemaVersionUI destVer) {
        _bNewMap = newMap;
        _details = details;
        _sourceSchema = sourceSch;
        _destSchema = destSch;
        _sourceVersion = sourceVer;
        _destVersion = destVer;
    }

    //GETS

    public boolean isNewMap() {
        return _bNewMap;
    }

    public SchemasDetailsContainer getDetails() {
        return _details;
    }

    public SchemaUI getSourceSchema() {
        return _sourceSchema;
    }

    public SchemaUI getDestSchema() {
        return _destSchema;
    }

    public SchemaVersionUI getSourceVersion() {
        return _sourceVersion;
    }

    public SchemaVersionUI getDestVersion() {
        return _destVersion;
    }

    //Existing Map - Transformation SET e GET

    public TransformationUI getTransformation() {
        return _transformation;
    }

    public void setTransformation(TransformationUI transformation) {
        this._transformation = transformation;
    }

    public void setNewMap(boolean newmap) {
        _bNewMap = newmap;
    }
}
