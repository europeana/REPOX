/*
 * MarcField.java
 *
 * Created on 20 de Julho de 2002, 18:40
 */

package pt.utl.ist.marc;

import java.io.Serializable;
/** Represents a Marc Subfield
 *
 * @author  Nuno Freire
 */
public class  Subfield implements Serializable{
	static final long serialVersionUID=2; 
	
    protected char code;
    protected String value;

/**************************************************************************
 ************                  Constructors              ******************
 *************************************************************************/
    /** Creates an empty subfield
     * 
     */
    public Subfield(){
    }

    /** Creates a Subfield and sets it's properties
     * @param code
     * @param value
     */
    public Subfield(char code, String value){
        this.code=code;
        setValue(value);
    }

    /** Constructs a subfield from a iso2709 string. Used to aid parsing of Record in ISO 2709
     * @param iso2709 the iso2709 part of the record that contains this field's data
     */
    public Subfield(String iso2709){
        this.code=iso2709.charAt(0);
        if(iso2709.length()>1) {
            setValue(iso2709.substring(1));	
        }else
        	setValue("");
    }
/**************************************************************************
 ************                Public Methods              ******************
 *************************************************************************/
    /**
      * Forms the string containing values of member variables in the Bean.
      *
      * @return String containing member variable values.
      */
    public String toString(){
        return "$"+code+value;
    }


    /**
     * @return a iso2709 string representation of the field
     */
    public String toIso2709 (){
        return String.valueOf(Record.US)+String.valueOf(getCode())+getValue();    
    }    
/**************************************************************************
 ************              Properties Methods            ******************
 *************************************************************************/
    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value=value;
    }
    
    public char getCode() {
        return code;
    }
    
    public void setCode(char code) {
        this.code=code;
    } 
    
}
