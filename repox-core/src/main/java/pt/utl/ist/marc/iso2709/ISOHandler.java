package pt.utl.ist.marc.iso2709;

import org.apache.log4j.Logger;
import pt.utl.ist.characters.CharacterConverterI;
import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;
import pt.utl.ist.marc.util.Leader;

import java.util.ArrayList;
import java.util.List;

class ISOHandler implements MARCHandler {
	private static final Logger log = Logger.getLogger(ISOHandler.class);

	CharacterConverterI charConverter=null;
    protected Record rec;
    private Field dataField;
    public List<Record> records;
    
    public ISOHandler() {
    }
    public ISOHandler(CharacterConverterI charConverter) {
    	this.charConverter = charConverter;
    }

    public void startTape() {
        records=new ArrayList<Record>();
    }

    public void endTape() {
    }

    public void startRecord(Leader leader) {
        rec=new Record();
        rec.setLeader(getString(leader.getSerializedForm()));
    }

    public void endRecord() {
        records.add(rec);
    }

    public void controlField(String tag, String chars) {
        Field fld=rec.addField(Integer.parseInt(tag));
        fld.setValue(getString(chars));
        if (tag.equals("001")){
            try{
                rec.setNc(fld.getValue());
            }catch(NumberFormatException e){//just ignore...               
            }
        }
    }

    public void startDataField(String tag, char ind1, char ind2) {
        try {
			short tagInt=Short.parseShort(tag);   
			dataField=rec.addField(tagInt);
			dataField.setInd1(ind1);
			dataField.setInd2(ind2);
		} catch (NumberFormatException e) {
			dataField=rec.addField(999);
			dataField.setInd1(ind1);
			dataField.setInd2(ind2);
		}
    }

    public void endDataField(String tag) {
//        rec.getFields().add(dataField);
    }

    public void subfield(char code, String chars) {
        Subfield sfld=new Subfield(code, getString(chars));
        dataField.getSubfields().add(sfld);
    }
    
    protected String getString(String string) {
    	if(charConverter!=null)
    		return charConverter.convert(string);
    	return string;
    }
    
    
}