package pt.utl.ist.marc.iso2709;

import org.apache.log4j.Logger;

import pt.utl.ist.characters.CharacterConverterI;
import pt.utl.ist.marc.MarcField;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.MarcSubfield;
import pt.utl.ist.util.marc.Leader;

import java.util.ArrayList;
import java.util.List;

class ISOHandler implements MARCHandler {
    private static final Logger log           = Logger.getLogger(ISOHandler.class);

    CharacterConverterI         charConverter = null;
    protected MarcRecord            rec;
    private MarcField               dataField;
    public List<MarcRecord>         records;

    public ISOHandler() {
    }

    public ISOHandler(CharacterConverterI charConverter) {
        this.charConverter = charConverter;
    }

    @Override
    public void startTape() {
        records = new ArrayList<MarcRecord>();
    }

    @Override
    public void endTape() {
    }

    @Override
    public void startRecord(Leader leader) {
        rec = new MarcRecord();
        rec.setLeader(getString(leader.getSerializedForm()));
    }

    @Override
    public void endRecord() {
        records.add(rec);
    }

    @Override
    public void controlField(String tag, String chars) {
        MarcField fld = rec.addField(Integer.parseInt(tag));
        fld.setValue(getString(chars));
        if (tag.equals("001")) {
            try {
                rec.setNc(fld.getValue());
            } catch (NumberFormatException e) {//just ignore...               
            }
        }
    }

    @Override
    public void startDataField(String tag, char ind1, char ind2) {
        try {
            short tagInt = Short.parseShort(tag);
            dataField = rec.addField(tagInt);
            dataField.setInd1(ind1);
            dataField.setInd2(ind2);
        } catch (NumberFormatException e) {
            dataField = rec.addField(999);
            dataField.setInd1(ind1);
            dataField.setInd2(ind2);
        }
    }

    @Override
    public void endDataField(String tag) {
        //        rec.getFields().add(dataField);
    }

    @Override
    public void subfield(char code, String chars) {
        MarcSubfield sfld = new MarcSubfield(code, getString(chars));
        dataField.getSubfields().add(sfld);
    }

    protected String getString(String string) {
        if (charConverter != null) return charConverter.convert(string);
        return string;
    }

}