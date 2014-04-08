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
public class Tuple<T1,T2> implements Comparable<Tuple<T1,T2>>, Serializable{
	private static final long serialVersionUID=1;	
	
//	public enum Comparison {V1, V2, BOTH};
	
    protected T1 v1;
    protected T2 v2;
    private boolean compareOn1=true;

/**************************************************************************
 ***************                Constructors             ******************
 *************************************************************************/    
    /** Creates a new instance of Tuple */
    public Tuple() {
    }
    
    /** Creates a new instance of Tuple */
    public Tuple(T1 v1, T2 v2) {
        this.v1=v1;
        this.v2=v2;
    }    
    
    /** Creates a new instance of Tuple */
    public Tuple(T1 v1, T2 v2, boolean compareOn1) {
        this.v1=v1;
        this.v2=v2;
        this.compareOn1 = compareOn1;
    }
    
    
    @Override
    public boolean equals(Object arg0) {
    	return compareOn1 ? v1.equals(arg0) : v2.equals(arg0);
    }
    
    @Override
    public int hashCode() {
    	return compareOn1 ? v1.hashCode() : v2.hashCode();
    }
    
    public int compareTo(Tuple<T1, T2> other) {
    	if (compareOn1) {
    		if (v1 instanceof Comparable) {
				Comparable v1c = (Comparable) v1;
				return v1c.compareTo(other.getV1());
			}
    		throw new RuntimeException(v1.getClass().getName()+" is not Comparable");
    	}else {
    		if (v2 instanceof Comparable) {
				Comparable v2c = (Comparable) v2;
				return v2c.compareTo(other.getV2());
			}
    		throw new RuntimeException(v1.getClass().getName()+" is not Comparable");
    	}
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

	/**
	 * toString methode: creates a String representation of the object
	 * @return the String representation
	 * @author info.vancauwenberge.tostring plugin
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<");
		buffer.append("v1 = ").append(v1);
		buffer.append(", v2 = ").append(v2);
		buffer.append(">");
		return buffer.toString();
	}
    

	
	public static <C,V> ArrayList<C> getListOfV1(Collection<Tuple<C, V>> col){
		ArrayList<C> ret=new ArrayList<C>(col.size());
		for(Tuple<C, V> t: col) {
			ret.add(t.getV1());
		}
		return ret;
	}
	
	public static <C,V> ArrayList<V> getListOfV2(Collection<Tuple<C, V>> col){
		ArrayList<V> ret=new ArrayList<V>(col.size());
		for(Tuple<C, V> t: col) {
			ret.add(t.getV2());
		}
		return ret;
	}
}
