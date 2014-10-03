/*
 * MarcField.java
 *
 * Created on 20 de Julho de 2002, 18:40
 */
package pt.utl.ist.marc;

import java.io.Serializable;

/**
 * Represents a Marc Subfield
 * 
 * @author Nuno Freire
 */
public class MarcSubfield implements Serializable {
    static final long serialVersionUID = 2;

    /** Subfield code */
    protected char    code;
    /** Subfield value */
    protected String  value;

    /**************************************************************************
     ************ Constructors ******************
     *************************************************************************/
    /**
     * Creates an empty subfield
     * 
     */
    public MarcSubfield() {
    }

    /**
     * Creates a Subfield and sets it's properties
     * 
     * @param code
     * @param value
     */
    public MarcSubfield(char code, String value) {
        this.code = code;
        setValue(value);
    }

    /**
     * Constructs a subfield from a iso2709 string. Used to aid parsing of
     * Record in ISO 2709
     * 
     * @param iso2709
     *            the iso2709 part of the record that contains this field's data
     */
    public MarcSubfield(String iso2709) {
        this.code = iso2709.charAt(0);
        if (iso2709.length() > 1) {
            setValue(iso2709.substring(1));
        } else
            setValue("");
    }

    /**************************************************************************
     ************ Public Methods ******************
     *************************************************************************/
    /**
     * Forms the string containing values of member variables in the Bean.
     * 
     * @return String containing member variable values.
     */
    @Override
    public String toString() {
        return "$" + code + value;
    }

    /**
     * @return a iso2709 string representation of the field
     */
    public String toIso2709() {
        return String.valueOf(MarcRecord.US) + String.valueOf(getCode()) + getValue();
    }

    /**************************************************************************
     ************ Properties Methods ******************
     *************************************************************************/
    
    @SuppressWarnings("javadoc")
    public String getValue() {
        return value;
    }

    @SuppressWarnings("javadoc")
    public void setValue(String value) {
        this.value = value;
    }

    @SuppressWarnings("javadoc")
    public char getCode() {
        return code;
    }

    @SuppressWarnings("javadoc")
    public void setCode(char code) {
        this.code = code;
    }

}
