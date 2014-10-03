/*
 * Iso2709Visitor.java
 *
 * Created on 20 de Julho de 2002, 23:57
 */

package pt.utl.ist.marc.iso2709;

import pt.utl.ist.marc.MarcField;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.MarcSubfield;
import pt.utl.ist.util.marc.Directory;
import pt.utl.ist.util.marc.Leader;

/**
 * 
 * @deprecated use Record.toIso2709
 * @author Nuno Freire
 */
@Deprecated
public class Record2Iso2709Visitor {

    /**
     * @deprecated use Record.toIso2709
     */
    @Deprecated
    public static String toIso2709(MarcRecord rec) {
        StringBuffer data = new StringBuffer();
        Directory directory = new Directory();
        Leader leader = new Leader(rec.getLeader());

        // append fields to directory and data
        for (Object o : rec.getFields()) {
            MarcField fld = (MarcField)o;
            String fldStr = fieldToIso2709(fld);
            directory.add(fld.getTagAsString(), fldStr.length());
            data.append(fldStr);
        }

        // add base address of data and logical record length tp the leader
        int baseAddress = 24 + directory.getLength();
        int recordLength = baseAddress + data.length() + 1;
        leader.setRecordLength(recordLength);
        leader.setBaseAddressOfData(baseAddress);

        // return record in tape format
        return leader.getSerializedForm() + directory.getSerializedForm() + data + MarcRecord.RT;
    }

    /**
     * @param fld
     * @return converted String
     */
    protected static String fieldToIso2709(MarcField fld) {
        if (fld.isControlField()) {
            return fld.getValue() + MarcRecord.FT;
        } else {
            StringBuffer dataField = new StringBuffer().append(fld.getInd1()).append(fld.getInd2());
            for (MarcSubfield subfield1 : fld.getSubfields()) {
                MarcSubfield subfield = subfield1;
                dataField.append(subfieldToIso2709(subfield));
            }
            dataField.append(MarcRecord.FT);
            return dataField.toString();
        }
    }

    /**
     * @param sfld
     * @return converted String
     */
    protected static String subfieldToIso2709(MarcSubfield sfld) {
        return new StringBuffer().append(MarcRecord.US).append(sfld.getCode()).append(sfld.getValue()).toString();
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
