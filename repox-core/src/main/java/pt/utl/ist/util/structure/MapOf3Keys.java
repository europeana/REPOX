/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.util.structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Nuno Freire
 *
 */
public class MapOf3Keys<K1, K2, K3, V> implements Serializable{
		private static final long serialVersionUID=1;	
		Map<K1, Map<K2, Map<K3, V>>> map;
		
		public MapOf3Keys(){
			map=new HashMap<K1, Map<K2, Map<K3, V>>>();
		}
 
		public MapOf3Keys(int initialCapacity){
			map=new HashMap<K1, Map<K2, Map<K3, V>>>(initialCapacity);
		}

		public MapOf3Keys(int initialCapacity, int loadFactor){
			map=new HashMap<K1, Map<K2, Map<K3, V>>>(initialCapacity,loadFactor);
		}
		
		public void put(K1 key, K2 key2, K3 key3, V value){
			Map<K2, Map<K3, V>> recs=get(key);
			if (recs==null){
				recs=new HashMap<K2, Map<K3, V>>();
				map.put(key,recs);
			}
			Map<K3, V> recs2=recs.get(key2);
			if (recs2==null){
				recs2=new HashMap<K3, V>();
				recs.put(key2,recs2);
			}
			recs2.put(key3,value);
		}

		
		public void remove(K1 key, K2 key2, K3 key3){
			Map<K2, Map<K3, V>> recs=get(key);
			if(recs==null)
				return;
			Map<K3, V> recs2=recs.get(key2);
			if(recs2==null)
				return;			
			recs2.remove(key3);
			if (recs2.size()==0)
				recs.remove(key2);
			if (recs.size()==0)
				map.remove(key);
		}
		
		public void remove(K1 key, K2 key2){
			Map<K2, Map<K3, V>> recs=get(key);
			recs.remove(key2);
			if (recs.size()==0)
				map.remove(key);
		}
		
		public void remove(K1 key){
			map.remove(key);
		}
		
		public Map<K2, Map<K3, V>> get(K1 key){
			return map.get(key);
		}
		public V get(K1 key, K2 key2, K3 key3){
			Map<K2, Map<K3, V>> midMap=get(key);
			if (midMap==null)
				return null;
			Map<K3, V> midMap2=midMap.get(key2);
			if (midMap2==null)
				return null;
			return midMap2.get(key3);
		}
		public Map<K3, V> get(K1 key, K2 key2){
			Map<K2, Map<K3, V>> midMap=get(key);
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
				Map<K2, Map<K3, V>> recs=get(key);
				for(K2 key2: recs.keySet()) {
					Map<K3, V> recs2=recs.get(key2);
					size+=recs2.size();
				}
			}
			return size;
		}
		
		
		
		public boolean containsKey(K1 key, K2 key2, K3 key3){
			Map<K2, Map<K3, V>> midMap=get(key);
			if (midMap==null)
				return false;
			Map<K3, V> midMap2=midMap.get(key2);
            return midMap2 != null && midMap2.containsKey(key3);
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
