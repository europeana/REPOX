/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.util.structure;

import java.io.Serializable;
import java.util.*;

/**
 * @author Nuno Freire
 * @param <K> 
 * @param <O> 
 */
public class MapOfLists<K, O> implements Serializable {

    private static final long serialVersionUID    = 1;

    //HashMap<K,Object[]> hashtable;
    HashMap<K, ArrayList<O>>  hashtable;
    int                       listInitialCapacity = -1;

    /**
     * Creates a new instance of this class.
     */
    public MapOfLists() {
        hashtable = new HashMap<K, ArrayList<O>>();
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     */
    public MapOfLists(int initialCapacity) {
        hashtable = new HashMap<K, ArrayList<O>>(initialCapacity);
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     * @param listInitialCapacity
     */
    public MapOfLists(int initialCapacity, int listInitialCapacity) {
        hashtable = new HashMap<K, ArrayList<O>>(initialCapacity);
        this.listInitialCapacity = listInitialCapacity;
    }

    //		public MapOfObjectArrays(int initialCapacity, int loadFactor){
    //			hashtable=new IntHashtable<Object[]>(initialCapacity,loadFactor);
    //		}

    /**
     * @param key
     * @param value
     */
    public void put(K key, O value) {
        ArrayList<O> recs = hashtable.get(key);
        if (recs == null) {
            recs = new ArrayList<O>(listInitialCapacity == -1 ? 1 : listInitialCapacity);
            recs.add(value);
            hashtable.put(key, recs);
        } else {
            recs.add(value);
        }
    }

    /**
     * @param key
     * @param values
     */
    public void putAll(K key, O... values) {
        ArrayList<O> recs = hashtable.get(key);
        if (recs == null) {
            recs = new ArrayList<O>(listInitialCapacity == -1 ? 1 : listInitialCapacity);
            for (O v : values)
                recs.add(v);
            hashtable.put(key, recs);
        } else {
            for (O v : values)
                recs.add(v);
        }
    }

    /**
     * @param key
     * @param values
     */
    public void putAll(K key, Collection<O> values) {
        ArrayList<O> recs = hashtable.get(key);
        if (recs == null) {
            recs = new ArrayList<O>(listInitialCapacity == -1 ? 1 : listInitialCapacity);
            recs.addAll(values);
            hashtable.put(key, recs);
        } else {
            recs.addAll(values);
        }
    }

    /**
     * @param map
     */
    public void putAll(Map<K, O> map) {
        for (Map.Entry<K, O> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @param map
     */
    public void putAll(MapOfLists<K, O> map) {
        for (K entry : map.keySet()) {
            putAll(entry, map.get(entry));
        }
    }

    /**
     * @param key
     * @param value
     */
    public void remove(K key, O value) {
        ArrayList<O> recs = hashtable.get(key);
        if (recs != null) {
            recs.remove(value);
            if (recs.size() == 0) hashtable.remove(key);
        }
    }

    /**
     * @param key
     */
    public void remove(K key) {
        hashtable.remove(key);
    }

    /**
     * @param key
     * @return boollean if contains key
     */
    public boolean containsKey(K key) {
        return hashtable.containsKey(key);
    }

    /**
     * @param key
     * @return get the arraylist of the map with key
     */
    public ArrayList<O> get(K key) {
        return hashtable.get(key);
    }

    /**
     * @param key
     * @param idx
     * @return get the O of the arraylist in the map with key and from the arraylist wit the index
     */
    public O get(K key, int idx) {
        return hashtable.get(key).get(idx);
    }

    /**
     * @return the keyset of the map
     */
    public Set<K> keySet() {
        return hashtable.keySet();
    }

    /**
     * @return size of the map
     */
    public int size() {
        return hashtable.size();
    }

    /**
     * @return size of all the lists in the map
     */
    public int sizeOfAllLists() {
        int total = 0;
        for (K key : hashtable.keySet()) {
            total += get(key).size();
        }
        return total;
    }

    /**
     * @return values of all the lists in the map
     */
    public ArrayList<O> valuesOfAllLists() {
        ArrayList<O> ret = new ArrayList<O>(sizeOfAllLists());
        for (K key : hashtable.keySet()) {
            ret.addAll(get(key));
        }
        return ret;
    }

    /**
     * @param c
     */
    public void sortLists(Comparator<O> c) {
        for (K k : hashtable.keySet()) {
            Collections.sort(hashtable.get(k), c);
        }
    }

    /**
     * 
     */
    public void sortLists() {
        for (K k : hashtable.keySet()) {
            Collections.sort((ArrayList)hashtable.get(k));
        }
    }

    /**
     * toString methode: creates a String representation of the object
     * @return the String representation
     * @author info.vancauwenberge.tostring plugin
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        ArrayList keys = new ArrayList(hashtable.keySet());
        Collections.sort(keys);
        for (K key : (List<K>)keys) {
            ArrayList<O> vals = get(key);
            buffer.append(key.toString()).append("(").append(vals.size()).append(")").append(":\n");
            for (O val : vals) {
                buffer.append("\t").append(val.toString()).append("\n");
            }
        }
        return buffer.toString();
    }

}
