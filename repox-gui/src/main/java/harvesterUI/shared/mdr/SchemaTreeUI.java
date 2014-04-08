package harvesterUI.shared.mdr;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 13:34
 */
public class SchemaTreeUI extends BaseTreeModel implements IsSerializable{

    protected SchemaMdrDataStatistics mdrDataStatistics;

    public SchemaTreeUI() {}

    public SchemaTreeUI(String schema, Double version, String namespace, String xsdLink, SchemaMdrDataStatistics mdrDataStatistics) {
        set("schema",schema);
        set("version", version);
        set("namespace",namespace);
        set("xsdLink",xsdLink);
        this.mdrDataStatistics = mdrDataStatistics;
    }

    public String getSchema(){return (String) get("schema");}
    public String getNamespace(){return (String) get("namespace");}

    public SchemaMdrDataStatistics getMdrDataStatistics() {
        return mdrDataStatistics;
    }

}
