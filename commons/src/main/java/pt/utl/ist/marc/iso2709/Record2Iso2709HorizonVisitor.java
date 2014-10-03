/*
 * Iso2709Visitor.java
 *
 * Created on 20 de Julho de 2002, 23:57
 */

package pt.utl.ist.marc.iso2709;

import org.apache.log4j.Logger;

import pt.utl.ist.marc.MarcField;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.MarcSubfield;
import pt.utl.ist.util.marc.Directory;
import pt.utl.ist.util.marc.Leader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 
 * @author Nuno Freire
 */
public class Record2Iso2709HorizonVisitor {
    private static final Logger log = Logger.getLogger(Record2Iso2709HorizonVisitor.class);

    /**
     * @param rec
     * @return converted bytes
     */
    public static byte[] toIso2709(MarcRecord rec) {
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            Directory directory = new Directory();
            Leader leader = new Leader(rec.getLeader());

            // append fields to directory and data
            for (Object o : rec.getFields()) {
                MarcField fld = (MarcField)o;
                byte[] fldStr = fieldToIso2709(fld);
                directory.add(fld.getTagAsString(), fldStr.length);
                data.write(fldStr);
            }

            byte[] fieldsData = data.toByteArray();
            data.close();

            // add base address of data and logical record length tp the leader
            int baseAddress = 24 + directory.getLength();
            int recordLength = baseAddress + fieldsData.length + 1;
            leader.setRecordLength(recordLength);
            leader.setBaseAddressOfData(baseAddress);

            data = new ByteArrayOutputStream();
            data.write(leader.getSerializedForm().getBytes("ISO8859-1"));
            data.write(directory.getSerializedForm().getBytes("ISO8859-1"));
            data.write(fieldsData);
            data.write(MarcRecord.RT);
            return data.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @param fld
     * @return converted bytes
     * @throws IOException
     */
    protected static byte[] fieldToIso2709(MarcField fld) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        if (fld.isControlField()) {
            data.write(fld.getValue().getBytes("ISO8859-1"));
            data.write(MarcRecord.FT);
        } else {
            data.write(fld.getInd1());
            data.write(fld.getInd2());
            for (MarcSubfield subfield1 : fld.getSubfields()) {
                MarcSubfield subfield = subfield1;
                data.write(subfieldToIso2709(subfield, fld.getTagAsString().equals("977") && subfield.getCode() == 'a'));
            }
            data.write(MarcRecord.FT);
        }
        return data.toByteArray();
    }

    /**
     * @param sfld
     * @param converToCp850
     * @return converted bytes
     * @throws IOException
     */
    protected static byte[] subfieldToIso2709(MarcSubfield sfld, boolean converToCp850) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        data.write(MarcRecord.US);
        data.write(sfld.getCode());
        data.write(converToCp850 ? sfld.getValue().getBytes("CP850") : sfld.getValue().getBytes());
        return data.toByteArray();
    }

    /**
     * @param str
     * @return get the first character from String or empty
     */
    protected static char getFirstCharOrEmpty(String str) {
        if (str == null || str.length() == 0) return ' ';
        return str.charAt(0);
    }
}
