/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.repox.util.structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nuno Freire
 * @param <K> 
 * @param <V> 
 */
public class MapOfSets<K, V> {
    private static final long serialVersionUID = 1;
    HashMap<K, HashSet<V>>    hashtable;

    /**
     * Creates a new instance of this class.
     */
    public MapOfSets() {
        hashtable = new HashMap<K, HashSet<V>>();
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     */
    public MapOfSets(int initialCapacity) {
        hashtable = new HashMap<K, HashSet<V>>(initialCapacity);
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     * @param loadFactor
     */
    public MapOfSets(int initialCapacity, int loadFactor) {
        hashtable = new HashMap<K, HashSet<V>>(initialCapacity, loadFactor);
    }

    /**
     * @param k
     * @param v
     * @return boolean if contains value with the keys K and V
     */
    public boolean contains(K k, V v) {
        HashSet<V> vs = get(k);
        return vs != null && vs.contains(v);
    }

    /**
     * @param key
     * @param value
     */
    public void put(K key, V value) {
        HashSet<V> recs = hashtable.get(key);
        if (recs == null) {
            recs = new HashSet<V>();
            hashtable.put(key, recs);
        }
        recs.add(value);
    }

    /**
     * @param key
     * @param values
     */
    public void putAll(K key, Collection<V> values) {
        HashSet<V> recs = hashtable.get(key);
        if (recs == null) {
            recs = new HashSet<V>();
            hashtable.put(key, recs);
        }
        recs.addAll(values);
    }

    /**
     * @param key
     * @param value
     */
    public void remove(K key, V value) {
        Set recs = hashtable.get(key);
        recs.remove(value);
        if (recs.size() == 0) hashtable.remove(key);
    }

    /**
     * @param key
     */
    public void remove(K key) {
        hashtable.remove(key);
    }

    /**
     * @param key
     * @return get HashSet that is in the map with the key K
     */
    public HashSet<V> get(K key) {
        return hashtable.get(key);
    }

    /**
     * @return size of the map
     */
    public int size() {
        return hashtable.size();
    }

    /**
     * @return size of all the sets
     */
    public int sizeOfKeysAndSets() {
        int totalSize = hashtable.size();
        for (Set s : hashtable.values()) {
            totalSize += s.size();
        }
        return totalSize;
    }

    /**
     * @return keyset of the map
     */
    public Set<K> keySet() {
        return hashtable.keySet();
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
