/*
 * MarcField.java
 *
 * Created on 20 de Julho de 2002, 18:40
 */

package pt.utl.ist.marc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 *
 * @author  Nuno Freire
 */
/**
 * @author Nuno Freire
 *
 */
public class Field  implements Serializable{
	static final long serialVersionUID=2; 
	
	
    protected short tag;    
    protected char ind1=' ';
    protected char ind2=' ';
    protected String value=null;
    protected List<Subfield> subfields=new ArrayList<Subfield>();

/**************************************************************************
 ************                  Constructors              ******************
 *************************************************************************/
    /** Constructs an empty Field
     * 
     */
    public Field(){
    }
    

    /** Constructs a data Field with empty subfields
     * @param tag should be > 10
     * @param ind1
     * @param ind2
     */
    public Field( int tag, char ind1, char ind2){
        this((short)tag,ind1,ind2);
    }    
    /** Constructs a data Field with empty subfields
     * @param tag should be > 10
     * @param ind1
     * @param ind2
     */
    public Field( short tag, char ind1, char ind2){
        this.tag=tag;
        this.ind1=ind1;
        this.ind2=ind2;
        this.subfields=new ArrayList<Subfield>(1);
    }
    
    public Field( short tag, char ind1, char ind2, char subfield, String value){
        this(tag, ind1, ind2);
        addSubfield(subfield, value);
        
    }
    
    public Field( short tag, char ind1, char ind2, char subfield, String value, char subfield2, String value2){
    	this(tag, ind1, ind2);
    	addSubfield(subfield, value); 
    	addSubfield(subfield2, value2);
    	
    }
    /** Constructs a control Field 
     * @param tag should be < 10
     * @param value 
     */
    public Field( int tag, String value){
        this((short)tag, value);
    }
    /** Constructs a control Field 
     * @param tag should be < 10
     * @param value 
     */
    public Field( short tag, String value){
        this.tag=(short)tag;
        setValue(value);
    }

    /**
     * @deprecated 
     */
    public Field( String tag, char ind1, char ind2){
    	this(Integer.parseInt(tag),ind1,ind2);
    }

    /**
     * @deprecated
     */
    public Field( String tag, String value){
    	this(Integer.parseInt(tag),value);
    }
    
    
    /** Constructs a field from a iso2709 string. Used to aid parsing of Record in ISO 2709
     * @param iso2709 the iso2709 part of the record that contains this field's data
     * @param tag the tag of the field
     */
    public Field(String iso2709, short tag) {
        this.tag=tag;
        if (iso2709.endsWith(String.valueOf(Record.FT)))
        	iso2709=iso2709.substring(0,iso2709.length()-1);
        if (tag<10)
        	value=iso2709;
        else {
        	ind1=iso2709.charAt(0);
        	ind2=iso2709.charAt(1);
        	if(iso2709.length()>=4) {
		    	String[] sfs=iso2709.substring(3).split(String.valueOf(Record.US));
		    	for (String sfIso: sfs) {
		    		if (sfIso.length()>=2)
		    			subfields.add(new Subfield(sfIso));
		    	}
        	}
        }
    }
    
/**************************************************************************
 ************                Public Methods              ******************
 *************************************************************************/
 
 	public void removeSubfield(char code) {
	 	for (Iterator<Subfield> it=subfields.iterator() ; it.hasNext() ;) {
	 		Subfield sf=it.next();
	 		if (sf.getCode()==code)
	 			it.remove();	 		
	 	}
 	}
 
    /** add a new subfield to the end of the subfield list
     * @param code the subfield code
     * @return the created subfield
     */
    public pt.utl.ist.marc.Subfield addSubfield(char code){
        Subfield ret=new Subfield(code, " ");
        subfields.add(ret);
        return ret;
    }
    
    /** add a new subfield to the end of the subfield list
     * @param code
     * @param value
     * @return the created subfield
     */
    public pt.utl.ist.marc.Subfield addSubfield(char code, String value){
        Subfield ret=new Subfield(code, value);
        subfields.add(ret);
        return ret;
    }
    

    
    
    
    /**
     * @return true if this field is a control field, false otherwise
     */
    public boolean isControlField(){
        return tag<10;
    }

    /**
     * @return true if this field is a data field, false otherwise
     */
    public boolean isDataField(){
        return ! isControlField();
    }

    /**
      * Forms the string containing values of member variables in the Bean.
      *
      * @return String containing member variable values.
      */
    public String toString(){
        StringBuffer b=new StringBuffer().append(tag).append(": ");
        if (isControlField())
            b.append(value);
        else{
            b.append(ind1);
            b.append(ind2);
            for (Subfield el : subfields) {
                b.append(el);
            }
        }
        return b.toString();
    }     
    
    

    
    public String toHtml() {
    	StringBuffer ret=new StringBuffer()
        .append("<b>"+tag+"</b> ");
    	if (isControlField())
    		ret.append(value);
        else{
        	ret.append(ind1).append(" ");
        	ret.append(ind2).append(" ");
            for (Subfield el : subfields) {
                ret.append("<b>$").append(el.getCode()).append("</b>").append(el.getValue());
            }
        }
        return ret.toString();
    	
    }
    
    /**
     * @return a iso2709 string representation of the field
     */
    public String toIso2709 (){
        if (isControlField()){
            return getValue() + Record.FT;
        }else{
            StringBuffer dataField = new StringBuffer()
            .append(getInd1()).append(getInd2());
            for (Subfield subfield1 : getSubfields()) {
                Subfield subfield = subfield1;
                dataField.append(subfield.toIso2709());
            }
            dataField.append(Record.FT);
            return dataField.toString();
        }    
    }

/**************************************************************************
 ************              Properties Methods            ******************
 *************************************************************************/    
    public char getInd1(){
        return ind1;
    }
    public void setInd1(char ind1){
        this.ind1 =  ind1;
    }

    public char getInd2(){
        return ind2;
    }
    public void setInd2(char ind2){
        this.ind2 =  ind2;
    }

    public List<Subfield> getSubfields(){
        return subfields;
    }
    public void setSubfields(List<Subfield> subfields){
        this.subfields =  subfields;
    }
    
   
    /**
     * @return the tag of the field, formated in a 3 char string
     */
    public String getTagAsString() {
    	return tagAsString(tag);
    }
    
    
    /**
     * @return the tag of the field, formated in a 3 char string
     */
    public int getTagAsInt() {
    	return (int) tag;
    }
    
    /**
     * @return the tag of the field, formated in a 3 char string
     */
    public static String tagAsString(int tag) {
    	if (tag<10)
    		return "00"+tag;
    	if (tag<100)
    		return "0"+tag;
    	return String.valueOf(tag);
    }
    
    
    /** sets the tag property using a string. 
     * @param tag 
     */
    public void setTag(String tag) {
        this.tag=Short.parseShort(tag);
    }

    public short getTag() {
    	return tag;
    }
    public void setTag(int tag) {
        this.tag=(short)tag;
    }
    public void setTag(short tag) {
        this.tag=tag;
    }
    
    public String getValue() {
        return value;
    }   
    public void setValue(String value) {
        this.value=value;
    }
    

    public Field clone() {
        Field newField=new Field();
        newField.setTag(getTag());            
        newField.setInd1(getInd1());
        newField.setInd2(getInd2());
        if (isControlField())
            newField.setValue(getValue());
        else{
            for (Object o : getSubfields()) {
                Subfield srcSf = (Subfield) o;
                newField.addSubfield(srcSf.getCode(), srcSf.getValue());
            }
        }
        return newField;
    }


    /**gets a subfield value in the field. To be used only for non repeatable subfields, as it returns the first subfield that matches.
     * @param sf the subfield code
     * @return the first subfield value that matches, null otherwise
     */
    public String getSingleSubfieldValue(char sf) {
        int sz = subfields.size() - 1;
		for (int idx = 0 ; idx <= sz ; idx++) {
		    Subfield f=subfields.get( idx);
            if (f.getCode()==sf)
                return f.getValue();
		}        
        return null;
    }


    /**gets a subfield in the field. To be used only for non repeatable subfields, as it returns the first subfield that matches.
     * @param sf the subfield code
     * @return the first subfield that matches, null otherwise
     */
    public Subfield getSingleSubfield(char sf) {
        int sz = subfields.size() - 1;
		for (int idx = 0 ; idx <= sz ; idx++) {
		    Subfield f=subfields.get( idx);
            if (f.getCode()==sf)
                return f;
		}        
        return null;
    }

    
    /** gets all values of the subfields that match
     * @param sf
     * @return all values of the subfields that match, or an empty list
     */
    public List<String> getSubfieldValues(char sf) {
        List<String> ret=new ArrayList<String>();
        int sz = subfields.size() - 1;
		for (int idx = 0 ; idx <= sz ; idx++) {
		    Subfield f=subfields.get( idx);
            if (f.getCode()==sf){
                ret.add(f.getValue());
            }
		}        
        return ret;
    }
    
    
    /** builds a string with the values of several subfields.
     * @param useSubfields the subfield codes to include in the string
     * @param separator the separator to use between subfields
     * @param removeBicos remove the "<...>" if the subfields contain it. 
     * @return the values of several subfields
     */
    public String getHeadingString(char[] useSubfields, String separator, boolean removeBicos){        
        StringBuffer value=new StringBuffer();
        boolean first=true;
        for (Object o : getSubfields()) {
            Subfield sf = (Subfield) o;
            boolean append = false;
            int sz = useSubfields.length;
            for (int idx = sz - 1; idx >= 0; idx--) {
                if (useSubfields[idx] == sf.getCode()) {
                    append = true;
                    break;
                }
            }
            if (append) {
                if (!first) {
                    value.append(separator);
                } else
                    first = false;
                String sfv = sf.getValue();
                if (removeBicos) {
                    sfv = sfv.replaceFirst("^\\s*<([^>]+)>", "$1");
                }
                value.append(sfv);
            }
        }
        String ret=value.toString().trim();
        if (ret.length()>0){
            while (ret.endsWith(" "))
                ret=ret.substring(0, ret.length()-1);
            char lastChar=ret.charAt(ret.length()-1);
            if (lastChar==',' || lastChar==';' || lastChar==':')
                ret=ret.substring(0, ret.length()-1);
        }
        return ret;    
    }  
}
