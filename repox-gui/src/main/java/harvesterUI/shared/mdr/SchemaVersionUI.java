package harvesterUI.shared.mdr;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 13:34
 */
public class SchemaVersionUI extends SchemaTreeUI implements IsSerializable{

    public SchemaVersionUI() {}

    public SchemaVersionUI(double version, String xsdLink, SchemaMdrDataStatistics mdrDataStatistics) {
        super(String.valueOf(version),version,"",xsdLink,mdrDataStatistics);
        set("version",version);
        set("xsdLink",xsdLink);
    }

    public Double getVersion(){return (Double) get("version");}

    public String getXsdLink(){return (String) get("xsdLink");}


}
