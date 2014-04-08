package pt.utl.ist.repox.marc;

public enum CharacterEncoding {

	UTF_8, ISO8859_1, ISO8859_2, ISO8859_3, ISO8859_4, ISO8859_5, ISO8859_6, ISO8859_7, ISO8859_8, ISO8859_9, ISO8859_10, ISO8859_11,
	ISO8859_12, ISO8859_13, ISO8859_14, ISO8859_15, ISO8859_16, WINDOWS_1250, WINDOWS_1252, ISO_5426, ISO_6937, ANSEL, CP1251;
	
	public static CharacterEncoding[] getValues() {
		return values();
	}
	
	/**
	 * @param value
	 * @return the CharacterEncoding that corresponds to value or null if its not supported.
	 */
	public static CharacterEncoding get(String value) {
		for (CharacterEncoding currentCharacterEncoding : values()) {
			if(currentCharacterEncoding.toString().equals(value)) {
				return currentCharacterEncoding;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return name().replace('_', '-');
	}
}
