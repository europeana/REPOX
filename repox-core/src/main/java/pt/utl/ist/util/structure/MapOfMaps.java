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
public class MapOfMaps<K1, K2, V> implements Serializable{
		private static final long serialVersionUID=1;	
		Map<K1, Map<K2, V>> map;
		
		public MapOfMaps(){
			map=new HashMap<K1, Map<K2, V>>();
		}

		public MapOfMaps(int initialCapacity){
			map=new HashMap<K1, Map<K2, V>>(initialCapacity);
		}

		public MapOfMaps(int initialCapacity, int loadFactor){
			map=new HashMap<K1, Map<K2, V>>(initialCapacity,loadFactor);
		}
		
		public void put(K1 key, K2 key2, V value){
			Map<K2, V> recs=get(key);
			if (recs==null){
				recs=new HashMap<K2, V>();
				map.put(key,recs);
			}
			recs.put(key2,value);
		}

		public void remove(K1 key, K2 key2){
			Map<K2, V> recs=get(key);
			recs.remove(key2);
			if (recs.size()==0)
				map.remove(key);
		}
		
		public void remove(K1 key){
			map.remove(key);
		}
		
		public Map<K2, V> get(K1 key){
			return map.get(key);
		}

		public V get(K1 key, K2 key2){
			Map<K2, V> midMap=get(key);
			if (midMap==null)
				return null;
			return midMap.get(key2);
		}
		
		public Set<K1> keySet(){
			return map.keySet();
		}
		
		public int sizeTotal() {
			int size=0;
			for(K1 key: map.keySet()) {
				Map<K2, V> recs=get(key);
				size+=recs.size();
			}
			return size;
		}
		
		
		
		public boolean containsKey(K1 key, K2 key2){
			Map<K2, V> midMap=get(key);
            return midMap != null && midMap.containsKey(key2);
        }
		
		
		public ArrayList<V> valuesOfAllMaps() {
			ArrayList<V> ret=new ArrayList<V>(sizeTotal());
			for(K1 key: map.keySet()) {
				ret.addAll(get(key).values());
			}
			return ret;
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
