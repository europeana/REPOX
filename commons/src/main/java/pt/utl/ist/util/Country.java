
package pt.utl.ist.util;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 26, 2014
 */
 public enum Country
 {
   FYROM("Former Yugoslav Republic Of Macedonia"), 
   ALBANIA("Albania"), 
   ARMENIA("Armenia"), 
   AUSTRIA("Austria"), 
   AZERBAIJAN("Azerbaijan"), 
   BELGIUM("Belgium"), 
   BOSNIA_AND_HERZEGOVINA("Bosnia and Herzegovina"), 
   BULGARIA("Bulgaria"), 
   CROATIA("Croatia"), 
   CYPRUS("Cyprus"), 
   CZECH("Czech Republic"), 
   DENMARK("Denmark"), 
   ESTONIA("Estonia"), 
   EUROPE("Europe"), 
   FINLAND("Finland"), 
   FRANCE("France"), 
   GEORGIA("Georgia"), 
   GERMANY("Germany"), 
   GREECE("Greece"), 
   HUNGARY("Hungary"), 
   ICELAND("Iceland"), 
   IRELAND("Ireland"), 
   ITALY("Italy"), 
   LATVIA("Latvia"), 
   LIECHTENSTEIN("Liechtenstein"), 
   LITHUANIA("Lithuania"), 
   LUXEMBOURG("Luxembourg"), 
   MALTA("Malta"), 
   MOLDOVA("Moldova"), 
   NETHERLANDS("Netherlands"), 
   NORWAY("Norway"), 
   POLAND("Poland"), 
   PORTUGAL("Portugal"), 
   ROMANIA("Romania"), 
   RUSSIA("Russia"), 
   SERBIA("Serbia"), 
   SLOVAKIA("Slovakia"), 
   SLOVENIA("Slovenia"), 
   SPAIN("Spain"), 
   SWEDEN("Sweden"), 
   SWITZERLAND("Switzerland"), 
   TURKEY("Turkey"), 
   UK("United Kingdom"), 
   UKRAINE("Ukraine");
   
   private String englishName;
   
   private Country(String englishName) {
     this.englishName = englishName;
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