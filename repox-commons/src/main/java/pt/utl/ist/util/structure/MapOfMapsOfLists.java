/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.util.structure;

import java.io.Serializable;
import java.util.*;

/**
 * @author Nuno Freire
 * @param <K1> 
 * @param <K2> 
 * @param <V> 
 *
 */
public class MapOfMapsOfLists<K1, K2, V> implements Serializable {
    private static final long  serialVersionUID = 1;
    Map<K1, MapOfLists<K2, V>> map;

    /**
     * Creates a new instance of this class.
     */
    public MapOfMapsOfLists() {
        map = new HashMap<K1, MapOfLists<K2, V>>();
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     */
    public MapOfMapsOfLists(int initialCapacity) {
        map = new HashMap<K1, MapOfLists<K2, V>>(initialCapacity);
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     * @param loadFactor
     */
    public MapOfMapsOfLists(int initialCapacity, int loadFactor) {
        map = new HashMap<K1, MapOfLists<K2, V>>(initialCapacity, loadFactor);
    }

    /**
     * @param key
     * @param key2
     * @param value
     */
    public void put(K1 key, K2 key2, V value) {
        MapOfLists<K2, V> recs = get(key);
        if (recs == null) {
            recs = new MapOfLists<K2, V>();
            map.put(key, recs);
        }
        recs.put(key2, value);
    }

    /**
     * @param key
     * @param key2
     */
    public void remove(K1 key, K2 key2) {
        MapOfLists<K2, V> recs = get(key);
        if (recs == null) return;
        recs.remove(key2);
        if (recs.size() == 0) map.remove(key);
    }

    /**
     * @param key
     */
    public void remove(K1 key) {
        map.remove(key);
    }

    /**
     * @param key
     * @return get the value of the map with K1
     */
    public MapOfLists<K2, V> get(K1 key) {
        return map.get(key);
    }

    /**
     * @param key
     * @param key2
     * @return get list of values of the maps with keys K1, K2
     */
    public List<V> get(K1 key, K2 key2) {
        MapOfLists<K2, V> midMap = get(key);
        if (midMap == null) return null;
        return midMap.get(key2);
    }

    /**
     * @return keyset of the map with key K1
     */
    public Set<K1> keySet() {
        return map.keySet();
    }

    /**
     * @return total size
     */
    public int sizeTotal() {
        int size = 0;
        for (K1 key : map.keySet()) {
            MapOfLists<K2, V> recs = get(key);
            size += recs.sizeOfAllLists();
        }
        return size;
    }

    /**
     * @param key
     * @param key2
     * @return boolean if contains a value with the keys K1, K2
     */
    public boolean containsKey(K1 key, K2 key2) {
        MapOfLists<K2, V> midMap = get(key);
        return midMap != null && midMap.containsKey(key2);
    }

    /**
     * @return list with all the values in the maps
     */
    public ArrayList<V> valuesOfAllMaps() {
        ArrayList<V> ret = new ArrayList<V>(sizeTotal());
        for (K1 key : map.keySet()) {
            ret.addAll(get(key).valuesOfAllLists());
        }
        return ret;
    }

    //		/**
    //		 * toString methode: creates a String representation of the object
    //		 * @return the String representation
    //		 * @author info.vancauwenberge.tostring plugin
    //
    //		 */
    //		public String toString() {
    //			StringBuffer buffer = new StringBuffer();
    //			buffer.append("Index[");
    //			buffer.append(super.toString());
    //			buffer.append("]");
    //			return buffer.toString();
    //		}

}
