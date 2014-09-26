package pt.utl.ist.metadataTransformation;

/**
 */
public enum MetadataFormat {
    /** MetadataFormat ese */
    ese,
    /** MetadataFormat ISO2709 */
    ISO2709,
    /** MetadataFormat MarcXchange */
    MarcXchange,
    /** MetadataFormat tel */
    tel,
    /** MetadataFormat oai_dc */
    oai_dc;

    /**
     * @return String array of the format names
     */
    public static String[] getMetadataFormatNames() {
        MetadataFormat[] values = MetadataFormat.values();
        String[] returnFormats = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            returnFormats[i] = values[i].toString();
        }
        return returnFormats;
    }
}
