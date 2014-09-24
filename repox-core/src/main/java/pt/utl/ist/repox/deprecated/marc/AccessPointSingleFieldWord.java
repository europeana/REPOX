//package pt.utl.ist.repox.deprecated.marc;
//
//import pt.utl.ist.marc.Field;
//import pt.utl.ist.marc.Record;
//import pt.utl.ist.marc.xml.MarcXChangeDom4jBuilder;
//import pt.utl.ist.repox.accessPoint.AccessPoint;
//import pt.utl.ist.repox.marc.RecordRepoxMarc;
//import pt.utl.ist.repox.recordPackage.RecordRepox;
//import pt.utl.ist.repox.util.IndexUtil;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
///**
// */
//public class AccessPointSingleFieldWord extends AccessPoint {
//    protected boolean indexDeletedRecords = false;
//    private int       tag;
//    private Character subfield;
//
//    /**
//     * Creates a new instance of this class.
//     * 
//     * @param id
//     * @param tag
//     * @param subfield
//     */
//    protected AccessPointSingleFieldWord(String id, int tag, Character subfield) {
//        super(id);
//        this.tag = tag;
//        this.subfield = subfield;
//    }
//
//    @Override
//    public Collection<String> index(RecordRepox record) {
//        RecordRepoxMarc repoxRecord = (RecordRepoxMarc)record;
//        return indexRecordRepoxMarc(repoxRecord);
//    }
//
//    @Override
//    public List index(List<RecordRepox> records) {
//        List idxVals = new ArrayList();
//
//        for (RecordRepox recordRepox : records) {
//            idxVals.add(indexRecordRepoxMarc(recordRepox));
//        }
//
//        return idxVals;
//    }
//
//    private List<String> indexRecordRepoxMarc(RecordRepox repoxRecord) {
//        Record record = (repoxRecord instanceof RecordRepoxMarc ? ((RecordRepoxMarc)repoxRecord).getRecord() : MarcXChangeDom4jBuilder.parseRecord(repoxRecord.getDom()));
//        List<String> idxVals = new ArrayList<String>();
//        List<Field> fields = record.getField(tag);
//        for (Field f : fields) {
//            if (subfield == null) {
//                String val = f.getValue();
//                if (val != null) {
//                    idxVals.add(IndexUtil.encode(val));
//                }
//            } else {
//                for (String val : f.getSubfieldValues(subfield)) {
//                    String[] indVals = IndexUtil.encode(val).split(" ");
//                    for (String word : indVals) {
//                        idxVals.add(word);
//                    }
//                }
//            }
//        }
//        return idxVals;
//    }
//
//    @Override
//    public Class typeOfIndex() {
//        return String.class;
//    }
//
//    @Override
//    public String getId() {
//        return id;
//    }
//
//    /**
//     * @return String of the description
//     */
//    public String getDescription() {
//        return "Campo " + tag + " $" + subfield + "(palavras)";
//    }
//}
