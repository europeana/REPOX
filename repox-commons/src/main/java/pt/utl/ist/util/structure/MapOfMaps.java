/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.util.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Nuno Freire
 *
 */
public class MapOfMaps<K1, K2, V> implements Serializable {
    private static final long serialVersionUID = 1;
    Map<K1, Map<K2, V>>       map;

    /**
     * Creates a new instance of this class.
     */
    public MapOfMaps() {
        map = new HashMap<K1, Map<K2, V>>();
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     */
    public MapOfMaps(int initialCapacity) {
        map = new HashMap<K1, Map<K2, V>>(initialCapacity);
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     * @param loadFactor
     */
    public MapOfMaps(int initialCapacity, int loadFactor) {
        map = new HashMap<K1, Map<K2, V>>(initialCapacity, loadFactor);
    }

    /**
     * @param key
     * @param key2
     * @param value
     */
    public void put(K1 key, K2 key2, V value) {
        Map<K2, V> recs = get(key);
        if (recs == null) {
            recs = new HashMap<K2, V>();
            map.put(key, recs);
        }
        recs.put(key2, value);
    }

    /**
     * @param key
     * @param key2
     */
    public void remove(K1 key, K2 key2) {
        Map<K2, V> recs = get(key);
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
     * @return map with K1
     */
    public Map<K2, V> get(K1 key) {
        return map.get(key);
    }

    /**
     * @param key
     * @param key2
     * @return get the value of the maps with the keys K1, K2
     */
    public V get(K1 key, K2 key2) {
        Map<K2, V> midMap = get(key);
        if (midMap == null) return null;
        return midMap.get(key2);
    }

    /**
     * @return keyset of the map with K1
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
            Map<K2, V> recs = get(key);
            size += recs.size();
        }
        return size;
    }

    /**
     * @param key
     * @param key2
     * @return boolean if value exists with keys K1, K2
     */
    public boolean containsKey(K1 key, K2 key2) {
        Map<K2, V> midMap = get(key);
        return midMap != null && midMap.containsKey(key2);
    }

    /**
     * @return list with the values of all the maps
     */
    public ArrayList<V> valuesOfAllMaps() {
        ArrayList<V> ret = new ArrayList<V>(sizeTotal());
        for (K1 key : map.keySet()) {
            ret.addAll(get(key).values());
        }
        return ret;
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
