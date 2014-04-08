package pt.utl.ist.repox.metadataTransformation;

public enum MetadataFormat {
	ese, ISO2709, MarcXchange, tel, oai_dc;

	public static String[] getMetadataFormatNames() {
		MetadataFormat[] values = MetadataFormat.values();
		String[] returnFormats = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			returnFormats[i] = values[i].toString();
		}
		return returnFormats;
	}
}
