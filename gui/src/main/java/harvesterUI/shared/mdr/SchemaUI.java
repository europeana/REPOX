package harvesterUI.shared.mdr;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 13:34
 */
public class SchemaUI extends SchemaTreeUI implements IsSerializable{

    private List<SchemaVersionUI> schemaVersions;

    public SchemaUI() {}

    public SchemaUI(String designation, String shortDesignation, String description,
                    String namespace, String notes) {
        super(shortDesignation,null,"","",null);
        set("designation",designation);
        set("shortDesignation", shortDesignation);
        set("description",description);
//        set("creationDate",creationDate);
//        set("xsdLink",xsdLink);
        set("namespace",namespace);
        set("notes",notes);
    }

    public void setDesignation(String designation){set("designation", designation);}
    public String getDesignation(){return (String) get("designation");}

    public void setShortDesignation(String shortDesignation){set("shortDesignation", shortDesignation);}
    public String getShortDesignation(){return (String) get("shortDesignation");}

    public void setDescription(String description){set("description", description);}
    public String getDescription(){return (String) get("description");}

//    public void setCreationDate(Date creationDate){set("creationDate", creationDate);}
//    public Date getCreationDate(){return (Date) get("creationDate");}

//    public void setXsdLink(String xsdLink){set("xsdLink", xsdLink);}
//    public String getXsdLink(){return (String) get("xsdLink");}

    public void setNamespace(String namespace){set("namespace", namespace);}
    public String getNamespace(){return (String) get("namespace");}

    public void setNotes(int notes){set("notes", notes);}
    public String getNotes(){return (String) get("notes");}

    public void setOAIAvailable(boolean value){set("bOAIAvailable", value);}
    public Boolean isOAIAvailable(){return (Boolean) get("bOAIAvailable");}

    public List<SchemaVersionUI> getSchemaVersions() {
        if(schemaVersions == null)
            schemaVersions = new ArrayList<SchemaVersionUI>();
        return schemaVersions;
    }

    public int getVersionsCount() {
        if(schemaVersions == null)
            return 0;
        return schemaVersions.size();
    }

    public int getVersionsCountFromIndex(int index) {
        if(schemaVersions == null)
            return 0;
        if(index >= schemaVersions.size())
            return 0;
        return schemaVersions.size() - index;
    }

    public void createTreeChildren(){
        if(schemaVersions != null){
            for(SchemaVersionUI schemaVersionUI : schemaVersions){
                add(schemaVersionUI);
            }
        }
    }

    public void createTreeChildren(int posi, int posf) {
        if(schemaVersions != null && posf < schemaVersions.size()) {
            for(int i = posi; i <= posf; i++) {
                add(schemaVersions.get(i));
            }
        }
    }

    //Get the total number of times this schema (all its versions) is used
    public int getTotalTimesUsed(boolean inDataSets) {
        int totalDS = 0;
        int totalT = 0;
        for(SchemaVersionUI schemaVersionUI : getSchemaVersions()){
            if(schemaVersionUI.getMdrDataStatistics() != null){
                totalDS += schemaVersionUI.getMdrDataStatistics().getNumberTimesUsedInDataSets();
                totalT += schemaVersionUI.getMdrDataStatistics().getNumberTimesUsedInTransformations();
            }
        }
        return inDataSets ? totalDS : totalT;
    }
}
