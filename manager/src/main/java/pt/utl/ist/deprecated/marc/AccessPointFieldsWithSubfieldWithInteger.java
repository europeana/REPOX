package pt.utl.ist.deprecated.marc;
//package pt.utl.ist.repox.deprecated.marc;
//
//import pt.utl.ist.marc.Field;
//import pt.utl.ist.marc.Record;
//import pt.utl.ist.marc.xml.MarcXChangeDom4jBuilder;
//import pt.utl.ist.repox.accessPoint.AccessPoint;
//import pt.utl.ist.repox.marc.RecordRepoxMarc;
//import pt.utl.ist.repox.recordPackage.RecordRepox;
//
//import java.util.*;
//
///**
// */
//public class AccessPointFieldsWithSubfieldWithInteger extends AccessPoint {
//    protected boolean indexDeletedRecords = false;
//    private int[]     tags;
//    private char      subfield;
//
//    /**
//     * Creates a new instance of this class.
//     * 
//     * @param id
//     * @param tags
//     * @param subfield
//     */
//    public AccessPointFieldsWithSubfieldWithInteger(String id, List<Integer> tags, char subfield) {
//        super(id);
//        this.tags = new int[tags.size()];
//        int idx = 0;
//        for (int i : tags) {
//            this.tags[idx] = i;
//            idx++;
//        }
//        this.subfield = subfield;
//    }
//
//    @Override
//    public Collection<Integer> index(RecordRepox record) {
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
//    private Collection<Integer> indexRecordRepoxMarc(RecordRepox repoxRecord) {
//        Record record = (repoxRecord instanceof RecordRepoxMarc ? ((RecordRepoxMarc)repoxRecord).getRecord() : MarcXChangeDom4jBuilder.parseRecord(repoxRecord.getDom()));
//        Set<Integer> idxVals = new HashSet<Integer>();
//        List<Field> fields = record.getFields(tags);
//        for (Field f : fields) {
//            for (String val : f.getSubfieldValues(subfield)) {
//                try {
//                    idxVals.add(Integer.parseInt(val));
//                } catch (Exception e) {
//                    //ignore. the value wont be indexed
//                }
//            }
//        }
//        return idxVals;
//    }
//
//    @Override
//    public Class typeOfIndex() {
//        return Integer.class;
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
//        return "Campos " + tags + " com subcampo $" + subfield;
//    }
//}
