/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.util.structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nuno Freire
 *
 */
public class MapOfSets<K,V> {
		private static final long serialVersionUID=1;	
		HashMap<K,HashSet<V>> hashtable;
		
		
		public MapOfSets(){
			hashtable=new HashMap<K, HashSet<V>>();
		}

		public MapOfSets(int initialCapacity){
			hashtable=new HashMap<K, HashSet<V>>(initialCapacity);
		}

		public MapOfSets(int initialCapacity, int loadFactor){
			hashtable=new HashMap<K, HashSet<V>>(initialCapacity,loadFactor);
		}
		
		public boolean contains(K k,V v) {
			HashSet<V> vs=get(k);
            return vs != null && vs.contains(v);
        }
		
		public void put(K key, V value){
			HashSet<V> recs=hashtable.get(key);
			if (recs==null){
				recs=new HashSet<V>();
				hashtable.put(key,recs);
			}
			recs.add(value);
		}

		public void putAll(K key, Collection<V> values){
			HashSet<V> recs=hashtable.get(key);
			if (recs==null){
				recs=new HashSet<V>();
				hashtable.put(key,recs);
			}
			recs.addAll(values);
		}
		
		public void remove(K key, V value){
			Set recs=hashtable.get(key);
			recs.remove(value);
			if (recs.size()==0)
				hashtable.remove(key);
		}
		
		public void remove(K key){
			hashtable.remove(key);
		}
		
		public HashSet<V> get(K key){
			return hashtable.get(key);
		}
		
		
		public int size() {
			return hashtable.size();
		}

		public int sizeOfKeysAndSets() {
			int totalSize=hashtable.size();
			for(Set s:hashtable.values()) {
				totalSize+=s.size();
			}
			return totalSize;
		}
		
		
		public Set<K> keySet() {
			return hashtable.keySet();
		}
		/**
		 * toString methode: creates a String representation of the object
		 * @return the String representation
		 * @author info.vancauwenberge.tostring plugin

		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Index[");
			buffer.append(super.toString());
			buffer.append("]");
			return buffer.toString();
		}

}
