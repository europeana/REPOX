/*
 * DOMBuilder.java
 *
 * Created on 4 de Janeiro de 2002, 10:44
 */

package pt.utl.ist.marc.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pt.utl.ist.marc.Record;

import java.util.List;

/** Utility class to code Marc records in XML
 * @author Nuno Freire
 */
public class MarcXChangeBuilder {
	private static final Logger log = Logger.getLogger(MarcXChangeBuilder.class);
    
    /** Creates a new instance of DOMBuilder */
    public MarcXChangeBuilder() {
    }

    /** Creates an XML representation of a marc record
     * @param rec a marc record
     * @return the record in xml
     */    
    public static String record2XMLString(Record rec){ 
        return record2XMLString( rec, null);        
    }
    public static String record2XMLString(Record rec, String recType){ 
    	return new MarcXChangeXmlBuilder(false).record2XMLString(rec, true, recType);        
    }
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     * @param withXmlDeclaration include the xml declaration
     * @return the record in xml
     */    
      public static String record2XMLString(Record rec, boolean withXmlDeclaration, String marcType){ 
        return new MarcXChangeXmlBuilder(false).record2XMLString(rec, withXmlDeclaration, marcType);
    } 

    /** Creates an XML representation of a marc record
     * @param recs a list of marc records
     * @return the records in xml
     */  
    public static String record2XMLString(List recs){ 
        return new MarcXChangeXmlBuilder(false).record2XMLString(recs);        
    }   
    

    /** Creates an XML representation of a marc record
     * @param recs a list of marc records
     * @param withXmlDeclaration include the xml declaration
     * @return the records in xml
     */  
    public static String record2XMLString(List recs, boolean withXmlDeclaration){ 
        return new MarcXChangeXmlBuilder(false).record2XMLString(recs, withXmlDeclaration);    
    }
        
    
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     * @return the record in xml
     */    
    public static byte[] record2XMLBytes(Record rec){ 
        return record2XMLBytes(rec, null);      
    }
    public static byte[] record2XMLBytes(Record rec, String marcType){ 
    	return new MarcXChangeXmlBuilder(false).record2XMLBytes(rec, true, marcType);        
    }
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     * @param withXmlDeclaration include the xml declaration
     * @return the record in xml
     */    
    public static byte[] record2XMLBytes(Record rec, boolean withXmlDeclaration){ 
        return record2XMLBytes(rec, withXmlDeclaration, null);  
    }   
    public static byte[] record2XMLBytes(Record rec, boolean withXmlDeclaration, String marcType){ 
    	return new MarcXChangeXmlBuilder(false).record2XMLBytes(rec, withXmlDeclaration, marcType);  
    }   

    /** Creates an XML representation of a marc record
     * @param recs a list of marc records
     * @return the record in xml
     */    
    public static byte[] record2XMLBytes(List recs){ 
        return new MarcXChangeXmlBuilder(false).record2XMLBytes(recs);         
    }   

    /** Creates an XML representation of several marc records
     * @param recs marc records
     * @param withXmlDeclaration include the xml declaration
     * @return the record in xml
     */    
    public static byte[] record2XMLBytes(List recs, boolean withXmlDeclaration){ 
        return new MarcXChangeXmlBuilder(false).record2XMLBytes(recs, withXmlDeclaration);  
    }    
    
    

    

    public static Document record2Dom(List recs){ 
        return new MarcXChangeXmlBuilder(false).record2Dom(recs);
    }  
    
    
    
        
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     *
     * @return Dom Document representing the record
     *
     */    
    public static Document record2Dom(Record rec){ 
        return new MarcXChangeXmlBuilder(false).record2Dom(rec);
    } 
    
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     *
     * @return Dom Document representing the record
     *
     */    
    public static Document record2Dom(Record rec, boolean withCollectionElement){ 
        return new MarcXChangeXmlBuilder(false).record2Dom(rec, withCollectionElement, null);        
    } 
    public static Document record2Dom(Record rec, boolean withCollectionElement, String marcType){ 
    	return new MarcXChangeXmlBuilder(false).record2Dom(rec, withCollectionElement, marcType);        
    } 
    
    /** Creates an XML representation of a marc record
     * @param rec a marc record
     *
     * @return Dom Document representing the record
     *
     */    
    public static Element record2DomElement(Record rec, Document document){ 
        return new MarcXChangeXmlBuilder(false).record2DomElement(rec, document, null);         
    }     
    public static Element record2DomElement(Record rec, Document document, String marcType){ 
    	return new MarcXChangeXmlBuilder(false).record2DomElement(rec, document, marcType);         
    }     
    
    
     
}
