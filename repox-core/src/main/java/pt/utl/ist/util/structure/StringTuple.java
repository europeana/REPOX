/*
 * Tuple.java
 *
 * Created on 12 de Fevereiro de 2003, 2:29
 */

package pt.utl.ist.util.structure;

/**
 *
 * @author  Nuno Freire
 */
public class StringTuple {
    protected String v1;
    protected String v2;
        
/**************************************************************************
 ***************                Constructors             ******************
 *************************************************************************/    
    /** Creates a new instance of Tuple */
    public StringTuple() {
    }
    
    /** Creates a new instance of Tuple */
    public StringTuple(String v1, String v2) {
        this.v1=v1;
        this.v2=v2;
    }
    
/**************************************************************************
 *************              Properties Methods           ******************
 *************************************************************************/    
    
    /** Getter for property v1.
     * @return Value of property v1.
     *
     */
    public java.lang.String getV1() {
        return v1;
    }
    
    /** Setter for property v1.
     * @param v1 New value of property v1.
     *
     */
    public void setV1(java.lang.String v1) {
        this.v1 = v1;
    }
    
    /** Getter for property v2.
     * @return Value of property v2.
     *
     */
    public java.lang.String getV2() {
        return v2;
    }
    
    /** Setter for property v2.
     * @param v2 New value of property v2.
     *
     */
    public void setV2(java.lang.String v2) {
        this.v2 = v2;
    }
    
}
