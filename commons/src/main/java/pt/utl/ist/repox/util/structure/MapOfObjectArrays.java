/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.repox.util.structure;

/**
 * @author Nuno Freire
 * @param <O> 
 */
public class MapOfObjectArrays<O> {
    private static final long serialVersionUID = 1;
    //HashMap<K,Object[]> hashtable;
    IntHashtable<Object[]>    hashtable;

    /**
     * Creates a new instance of this class.
     */
    public MapOfObjectArrays() {
        hashtable = new IntHashtable<Object[]>();
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     */
    public MapOfObjectArrays(int initialCapacity) {
        hashtable = new IntHashtable<Object[]>(initialCapacity);
    }

    //		public MapOfObjectArrays(int initialCapacity, int loadFactor){
    //			hashtable=new IntHashtable<Object[]>(initialCapacity,loadFactor);
    //		}

    /**
     * @param key
     * @param value
     */
    public void put(int key, O value) {
        Object[] recs = hashtable.get(key);
        if (recs == null) {
            recs = new Object[1];
            hashtable.put(key, recs);
        } else {
            Object[] old = recs;
            recs = new Object[old.length + 1];
            for (int i = 0; i < old.length; i++) {
                recs[i] = old[i];
            }
            hashtable.put(key, recs);
        }
        recs[recs.length - 1] = value;
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
     * @return the value in the map with key
     */
    public Object[] get(int key) {
        return (Object[])hashtable.get(key);
    }

    /**
     * toString method: creates a String representation of the object
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
