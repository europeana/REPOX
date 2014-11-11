/* IsoVariant.java - created on Nov 7, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package pt.utl.ist.marc.iso2709.shared;

import javax.xml.bind.annotation.XmlEnum;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration of Iso2709 variants
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 7, 2014
 */
@XmlEnum(String.class)
public enum Iso2709Variant  implements IsSerializable{
    STANDARD("pt.utl.ist.marc.iso2709.IteratorIso2709"),
    ALBANIA("pt.utl.ist.marc.iso2709.IteratorIso2709Albania"),
    UKRAINE("pt.utl.ist.marc.iso2709.IteratorIso2709Ukraine");
    
    private final String isoVariant;

    private Iso2709Variant(final String isoVariant) {
        this.isoVariant = isoVariant;
    }
    
    public String getIsoVariant() {
        return isoVariant;
    }
    
    public static Iso2709Variant fromString(String isoVariant) {
        if (isoVariant != null) {
          for (Iso2709Variant b : Iso2709Variant.values()) {
            if (isoVariant.equalsIgnoreCase(b.isoVariant)) {
              return b;
            }
          }
        }
        return null;
      }
}
