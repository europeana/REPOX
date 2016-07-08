
package pt.utl.ist.util;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 26, 2014
 */
public enum Country {
    FYROM("MK", "Former Yugoslav Republic Of Macedonia"),
    ALBANIA("AL", "Albania"),
    ARMENIA("AM", "Armenia"),
    AUSTRIA("AT", "Austria"),
    AZERBAIJAN("AZ", "Azerbaijan"),
    BELGIUM("BE", "Belgium"),
    BOSNIA_AND_HERZEGOVINA("BA", "Bosnia and Herzegovina"),
    BULGARIA("BG", "Bulgaria"),
    CROATIA("HR", "Croatia"),
    CYPRUS("CY", "Cyprus"),
    CZECH("CZ", "Czech Republic"),
    DENMARK("DK", "Denmark"),
    ESTONIA("EE", "Estonia"),
    EUROPE("EU", "Europe"),
    FINLAND("FI", "Finland"),
    FRANCE("FR", "France"),
    GEORGIA("GE", "Georgia"),
    GERMANY("DE", "Germany"),
    GREECE("GR", "Greece"),
    HUNGARY("HU", "Hungary"),
    ICELAND("IS", "Iceland"),
    IRELAND("IE", "Ireland"),
    ITALY("IT", "Italy"),
    LATVIA("LV", "Latvia"),
    LIECHTENSTEIN("LI", "Liechtenstein"),
    LITHUANIA("LT", "Lithuania"),
    LUXEMBOURG("LU", "Luxembourg"),
    MALTA("MT", "Malta"),
    MOLDOVA("MD", "Moldova"),
    NETHERLANDS("NL", "Netherlands"),
    NORWAY("NO", "Norway"),
    POLAND("PL", "Poland"),
    PORTUGAL("PT", "Portugal"),
    ROMANIA("RO", "Romania"),
    RUSSIA("RU", "Russia"),
    SERBIA("RS", "Serbia"),
    SLOVAKIA("SK", "Slovakia"),
    SLOVENIA("SI", "Slovenia"),
    SPAIN("ES", "Spain"),
    SWEDEN("SE", "Sweden"),
    SWITZERLAND("CH", "Switzerland"),
    TURKEY("TR", "Turkey"),
    UK("UK", "United Kingdom"),
    UKRAINE("UA", "Ukraine"),
    USA("US", "United States");

    private String codeName;
    private String englishName;

    private Country(String codeName, String englishName) {
        this.codeName = codeName;
        this.englishName = englishName;
    }

    public String getCodeName() {
        return this.codeName;
    }
    public String getEnglishName() {
        return this.englishName;
    }

    public static Country get(String string) {
        for (Country t : values()) {
            if (t.getEnglishName().equalsIgnoreCase(string)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Did not recognize Country: [" + string + "]");
    }
}