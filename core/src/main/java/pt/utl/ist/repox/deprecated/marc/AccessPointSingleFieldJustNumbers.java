//package pt.utl.ist.repox.deprecated.marc;
//
//import org.apache.log4j.Logger;
//
///**
// */
//public class AccessPointSingleFieldJustNumbers extends AccessPointSingleField {
//    private static final Logger log = Logger.getLogger(AccessPointSingleFieldJustNumbers.class);
//
//    /**
//     * Creates a new instance of this class.
//     * 
//     * @param id
//     * @param tag
//     * @param subfield
//     */
//    public AccessPointSingleFieldJustNumbers(String id, int tag, Character subfield) {
//        super(id, tag, subfield);
//    }
//
//    /**
//     * Creates a new instance of this class.
//     * 
//     * @param id
//     * @param tag
//     * @param subfield
//     * @param tokenizable
//     */
//    public AccessPointSingleFieldJustNumbers(String id, int tag, Character subfield, boolean tokenizable) {
//        super(id, tag, subfield, tokenizable);
//    }
//
//    @Override
//    public String indexValue(String value) {
//        StringBuffer sb = new StringBuffer();
//        char[] chars = value.toCharArray();
//        boolean lastWasDigit = false;
//        for (char aChar : chars) {
//            if (Character.isDigit(aChar)) {
//                sb.append(aChar);
//                lastWasDigit = true;
//            } else {
//                if (lastWasDigit) sb.append(' ');
//                lastWasDigit = false;
//            }
//        }
//        return sb.toString().trim();
//    }
//
//    @Override
//    public String getDescription() {
//        return "Campo " + tag + " $" + subfield + " (s� d�gitos)";
//    }
//
//    public static void main(String[] args) {
//        AccessPointSingleFieldJustNumbers a = new AccessPointSingleFieldJustNumbers("t", 205, 'a');
//        log.debug("-" + a.indexValue("1� ed., 3� tiragem") + "-");
//    }
//
//}
