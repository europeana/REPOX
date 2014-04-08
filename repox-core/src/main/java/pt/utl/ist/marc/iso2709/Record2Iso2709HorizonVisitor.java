/*
 * Iso2709Visitor.java
 *
 * Created on 20 de Julho de 2002, 23:57
 */

package pt.utl.ist.marc.iso2709;

import org.apache.log4j.Logger;
import pt.utl.ist.marc.Field;
import pt.utl.ist.marc.Record;
import pt.utl.ist.marc.Subfield;
import pt.utl.ist.marc.util.Directory;
import pt.utl.ist.marc.util.Leader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
/**
 *
 * @author  Nuno Freire
 */
public class Record2Iso2709HorizonVisitor {
	private static final Logger log = Logger
			.getLogger(Record2Iso2709HorizonVisitor.class);
    
    public static byte[] toIso2709(Record rec) {
    	try {
	    	ByteArrayOutputStream data = new ByteArrayOutputStream ();
	        Directory directory = new Directory();
	        Leader leader=new Leader(rec.getLeader());
	         
	        // append fields to directory and data
            for (Object o : rec.getFields()) {
                Field fld = (Field) o;
                byte[] fldStr = fieldToIso2709(fld);
                directory.add(fld.getTagAsString(), fldStr.length);
                data.write(fldStr);
            }
	        
	        byte[] fieldsData=data.toByteArray();
	        data.close();
	        
	        // add base address of data and logical record length tp the leader
	        int baseAddress = 24 + directory.getLength();
	        int recordLength = baseAddress + fieldsData.length + 1;
	        leader.setRecordLength(recordLength);
	        leader.setBaseAddressOfData(baseAddress);
	              
	    	data = new ByteArrayOutputStream ();
	    	data.write(leader.getSerializedForm().getBytes("ISO8859-1"));
	    	data.write(directory.getSerializedForm().getBytes("ISO8859-1"));
	    	data.write(fieldsData);
	    	data.write(Record.RT);
	        return data.toByteArray();
    	}catch(IOException e) {
    		log.error(e.getMessage(), e);
    		return null;
    	}
    }

    protected static byte[] fieldToIso2709 (Field fld) throws IOException{
    	ByteArrayOutputStream data = new ByteArrayOutputStream ();
        if (fld.isControlField()){
        	data.write(fld.getValue().getBytes("ISO8859-1"));
	    	data.write(Record.FT);
        }else{
        	data.write(fld.getInd1());
        	data.write(fld.getInd2());
            for (Subfield subfield1 : fld.getSubfields()) {
                Subfield subfield = subfield1;
                data.write(subfieldToIso2709(subfield, fld.getTagAsString().equals("977") && subfield.getCode() == 'a'));
            }
	    	data.write(Record.FT);
        }    
        return data.toByteArray();
    }

    protected static byte[] subfieldToIso2709 (Subfield sfld, boolean converToCp850)throws IOException{
    	ByteArrayOutputStream data = new ByteArrayOutputStream ();
    	data.write(Record.US);
    	data.write(sfld.getCode());         
    	data.write(converToCp850 ? sfld.getValue().getBytes("CP850") : sfld.getValue().getBytes());
	    return data.toByteArray();    
    }
    
    
    protected static char getFirstCharOrEmpty(String str){
        if (str==null || str.length()==0)
            return ' ';
        return str.charAt(0);
    }    
}
