/*
 * Tuple.java
 *
 * Created on 12 de Fevereiro de 2003, 2:29
 */

package pt.utl.ist.util.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author  Nuno Freire
 */
public class Triple<T1,T2,T3> implements Comparable<Triple<T1,T2,T3>>, Serializable{
	private static final long serialVersionUID=1;	
	
    protected T1 v1;
    protected T2 v2;
    protected T3 v3;
    private int compareOn=1;
        
/**************************************************************************
 ***************                Constructors             ******************
 *************************************************************************/    
    /** Creates a new instance of Tuple */
    public Triple() {
    }
    
    /** Creates a new instance of Tuple */
    public Triple(T1 v1, T2 v2, T3 v3) {
        this.v1=v1;
        this.v2=v2;
        this.v3=v3;
    }    
    
    /** Creates a new instance of Tuple */
    public Triple(T1 v1, T2 v2, T3 v3, int compareOn) {
        this.v1=v1;
        this.v2=v2;
        this.v3=v3;
        this.compareOn = compareOn;
    }
    
    
    
    
    public int compareTo(Triple<T1, T2, T3> other) {
    	if (compareOn==1) {
    		if (v1 instanceof Comparable) {
				Comparable v1c = (Comparable) v1;
				return v1c.compareTo(other.getV1());
			}
    		throw new RuntimeException(v1.getClass().getName()+" is not Comparable");
    	}else if (compareOn==2) {
    		if (v2 instanceof Comparable) {
				Comparable v2c = (Comparable) v2;
				return v2c.compareTo(other.getV2());
			}
    		throw new RuntimeException(v2.getClass().getName()+" is not Comparable");
    	}else if (compareOn==3) {
    		if (v3 instanceof Comparable) {
				Comparable v3c = (Comparable) v3;
				return v3c.compareTo(other.getV3());
			}
    		throw new RuntimeException(v3.getClass().getName()+" is not Comparable");
    	}
    	return -1;
    }
/**************************************************************************
 *************              Properties Methods           ******************
 *************************************************************************/    
    
    /** Getter for property v1.
     * @return Value of property v1.
     *
     */
    public T1 getV1() {
        return v1;
    }
    
    /** Setter for property v1.
     * @param v1 New value of property v1.
     *
     */
    public void setV1(T1 v1) {
        this.v1 = v1;
    }
    
    /** Getter for property v2.
     * @return Value of property v2.
     *
     */
    public T2 getV2() {
        return v2;
    }
    
    /** Setter for property v2.
     * @param v2 New value of property v2.
     *
     */
    public void setV2(T2 v2) {
        this.v2 = v2;
    }
    
    /** Getter for property v3
     * @return Value of property v3.
     *
     */
    public T3 getV3() {
        return v3;
    }
    
    /** Setter for property v3.
     * @param v3 New value of property v3.
     *
     */
    public void setV3(T3 v3) {
        this.v3 = v3;
    }

	/**
	 * toString methode: creates a String representation of the object
	 * @return the String representation
	 * @author info.vancauwenberge.tostring plugin
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Tuple[");
		buffer.append("v1 = ").append(v1);
		buffer.append(", v2 = ").append(v2);
		buffer.append(", v3 = ").append(v3);
		buffer.append("]");
		return buffer.toString();
	}
    

	
	public static <C,V,Z> ArrayList<C> getListOfV1(Collection<Triple<C, V, Z>> col){
		ArrayList<C> ret=new ArrayList<C>(col.size());
		for(Triple<C, V, Z> t: col) {
			ret.add(t.getV1());
		}
		return ret;
	}
	
	public static <C,V, Z> ArrayList<V> getListOfV2(Collection<Triple<C, V, Z>> col){
		ArrayList<V> ret=new ArrayList<V>(col.size());
		for(Triple<C, V,Z> t: col) {
			ret.add(t.getV2());
		}
		return ret;
	}
	
	public static <C,V, Z> ArrayList<Z> getListOfV3(Collection<Triple<C, V, Z>> col){
		ArrayList<Z> ret=new ArrayList<Z>(col.size());
		for(Triple<C, V,Z> t: col) {
			ret.add(t.getV3());
		}
		return ret;
	}
}
