package harvesterUI.shared.mdr;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 13:34
 */
public class TransformationUI extends BaseModel implements IsSerializable{

    private MdrDataStatistics mdrDataStatistics;

    public TransformationUI() {}

    public TransformationUI(String identifier, String description, String srcFormat, String destFormat,
                            String destSchema, String destMtdNamespace, String xslFilePath, boolean isXslVersion2) {
        set("identifier",identifier);
        set("description",description);
        set("srcFormat", srcFormat);
        set("destFormat",destFormat);
        set("destSchema",destSchema);
        set("destMtdNamespace",destMtdNamespace);
        set("xslFilePath",xslFilePath);
        set("isXslVersion2",isXslVersion2);
        createDSString(identifier,srcFormat,destFormat);
    }

    public TransformationUI(String identifier, String description, String srcFormat, String destFormat,
                            String destSchema, String destMtdNamespace, String xslFilePath, boolean isXslVersion2,
                            MdrDataStatistics mdrDataStatistics) {
        set("identifier",identifier);
        set("description",description);
        set("srcFormat", srcFormat);
        set("destFormat",destFormat);
        set("destSchema",destSchema);
        set("destMtdNamespace",destMtdNamespace);
        set("xslFilePath",xslFilePath);
        set("isXslVersion2",isXslVersion2);
        set("usage", mdrDataStatistics.getNumberTimesUsedInDataSets());
        this.mdrDataStatistics = mdrDataStatistics;
        createDSString(identifier,srcFormat,destFormat);
    }

    public void setIdentifier(String identifier){set("identifier", identifier);}
    public String getIdentifier(){return (String) get("identifier");}

    public void setDescription(String description){set("description", description);}
    public String getDescription(){return (String) get("description");}

    public void setSrcFormat(String srcFormat){set("srcFormat", srcFormat);}
    public String getSrcFormat(){return (String) get("srcFormat");}

    public void setDestFormat(String destFormat){set("destFormat", destFormat);}
    public String getDestFormat(){return (String) get("destFormat");}

    public void setDestSchema(String schema){set("destSchema", schema);}
    public String getDestSchema(){return (String) get("destSchema");}

    public void setMetadataNamespace(String destMtdNamespace){set("destMtdNamespace", destMtdNamespace);}
    public String getDestMetadataNamespace(){return (String) get("destMtdNamespace");}

    public void setIsXslVersion2(boolean isXslVersion2){set("isXslVersion2", isXslVersion2);}
    public boolean getIsXslVersion2(){return (Boolean) get("isXslVersion2");}

    public void setXslFilePath(int xslFilePath){set("xslFilePath", xslFilePath);}
    public String getXslFilePath(){return (String) get("xslFilePath");}

    /* New extra fields */
    public void setSourceSchema(String schema){set("sourceSchema", schema);}
    public String getSourceSchema(){return (String) get("sourceSchema");}

    public void setMDRCompliant(boolean compliant){set("bMDRCompliant", compliant);}
    public Boolean isMDRCompliant(){return (Boolean) get("bMDRCompliant");}

    public void setEditable(boolean editable){set("bEditable", editable);}
    public Boolean isEditable(){return (Boolean) get("bEditable");}

    /**/
    public void createDSString(String identifier, String srcFormat ,String destFormat) {
        if(identifier.equals("-"))
            set("dsStringFormat","-");
        else {
            String dsStringFormat = srcFormat + " to " + destFormat + ": " + identifier;
            set("dsStringFormat",dsStringFormat);
        }
    }

    public String getDSStringFormat() { return (String) get("dsStringFormat");}
    public void setDSStringFormat(String text){set("dsStringFormat",text);}

    public MdrDataStatistics getMdrDataStatistics() {
        return mdrDataStatistics;
    }

}
