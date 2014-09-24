/*
 * Created on 2006/08/24
 *
 */
package pt.utl.ist.marc.iso2709;

/**
 */
public class BatchInfo {

    enum CharSet {
        ISO8859_1, HORIZON, CP850, ANSEL
    }
    enum Sgb {
        PB4, ALEPH, OTHER
    }

    CharSet charSet;
    boolean standardSeparators;
    boolean lineBreaks;
    Sgb     sgb;

    @SuppressWarnings("javadoc")
    public CharSet getCharSet() {
        return charSet;
    }

    @SuppressWarnings("javadoc")
    public void setCharSet(CharSet charSet) {
        this.charSet = charSet;
    }

    @SuppressWarnings("javadoc")
    public boolean isLineBreaks() {
        return lineBreaks;
    }

    @SuppressWarnings("javadoc")
    public void setLineBreaks(boolean lineBreaks) {
        this.lineBreaks = lineBreaks;
    }

    @SuppressWarnings("javadoc")
    public Sgb getSgb() {
        return sgb;
    }

    @SuppressWarnings("javadoc")
    public void setSgb(Sgb sgb) {
        this.sgb = sgb;
    }

    @SuppressWarnings("javadoc")
    public boolean isStandardSeparators() {
        return standardSeparators;
    }

    @SuppressWarnings("javadoc")
    public void setStandardSeparators(boolean standardSeparators) {
        this.standardSeparators = standardSeparators;
    }

    /**
     * toString methode: creates a String representation of the object
     * 
     * @return the String representation
     * @author info.vancauwenberge.tostring plugin
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("BatchInfo[");
        buffer.append("charSet = ").append(charSet);
        buffer.append(", standardSeparators = ").append(standardSeparators);
        buffer.append(", lineBreaks = ").append(lineBreaks);
        buffer.append(", sgb = ").append(sgb);
        buffer.append("]");
        return buffer.toString();
    }
}
