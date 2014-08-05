/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.util.structure;

import java.util.ArrayList;

/**
 * @author Nuno Freire
 * @param <O> 
 */
public class MapOfIntsToLists<O> {
    private static final long  serialVersionUID = 1;
    //HashMap<K,Object[]> hashtable;
    IntHashtable<ArrayList<O>> hashtable;

    /**
     * Creates a new instance of this class.
     */
    public MapOfIntsToLists() {
        hashtable = new IntHashtable<ArrayList<O>>();
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     */
    public MapOfIntsToLists(int initialCapacity) {
        hashtable = new IntHashtable<ArrayList<O>>(initialCapacity);
    }

    //		public MapOfObjectArrays(int initialCapacity, int loadFactor){
    //			hashtable=new IntHashtable<Object[]>(initialCapacity,loadFactor);
    //		}

    /**
     * @param key
     * @param value
     */
    public void put(int key, O value) {
        ArrayList<O> recs = hashtable.get(key);
        if (recs == null) {
            recs = new ArrayList<O>(1);
            recs.add(value);
            hashtable.put(key, recs);
        } else {
            recs.add(value);
        }
    }

    //		public void putAll(K key, Collection<Object> values){
    //			Object[] recs=hashtable.get(key);
    //			if (recs==null){
    //				recs=new Object[values.size];
    //				hashtable.put(key,recs);
    //			}
    //			recs.addAll(values);
    //		}

    //		public void remove(K key, O value){
    //			Object[] recs=hashtable.get(key);
    //			recs.remove(value);
    //			if (recs.size()==0)
    //				remove(key);
    //		}
    //		
    /**
     * @param key
     * @return arraylist in the map with ley
     */
    public ArrayList<O> get(int key) {
        return (ArrayList<O>)hashtable.get(key);
    }

    /**
     * toString methode: creates a String representation of the object
     * @return the String representation
     * @author info.vancauwenberge.tostring plugin
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Index[");
        buffer.append(super.toString());
        buffer.append("]");
        return buffer.toString();
    }

}
