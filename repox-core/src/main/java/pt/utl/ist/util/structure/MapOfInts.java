/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.util.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Nuno Freire
 *
 */
public class MapOfInts<K> extends HashMap<K, Integer> implements Serializable{
	
		private static final long serialVersionUID=1;	
		
		//HashMap<K,Object[]> hashtable;
//		HashMap<K,Integer> hashtable;
//		int listInitialCapacity=-1;
		
		public MapOfInts(){
			super();
		}

		public MapOfInts(int initialCapacity){
			super(initialCapacity);
		}

//		public MapOfObjectArrays(int initialCapacity, int loadFactor){
//			hashtable=new IntHashtable<Object[]>(initialCapacity,loadFactor);
//		}
		
//		public void put(K key, Integer value){
//			hashtable.put(key,value);
//		}
//
//		public void remove(K key){
//			hashtable.remove(key);
//		}
//		
//		public Integer get(K key){
//			return  hashtable.get(key);
//		}
//
//		public O get(K key, int idx){
//			return  hashtable.get(key).get(idx);
//		}
		
		
		
		public int total() {
			int total=0;
			for(K key: keySet()) {
				total+=get(key);
			}
			return total;
		}
		
		

		public void addTo(K key, Integer value){
			Integer v=get(key);
			if(v!=null) {
				put(key,value+v);
			} else {
				put(key,value);
			}
		}
		
		public void incrementTo(K key){
			Integer v=get(key);
			if(v!=null) {
				put(key,1+v);
			} else {
				put(key,1);
			}
		}

		public void subtractTo(K key, Integer value){
			Integer v=get(key);
			if(v!=null) {
				put(key, value-v);
			} else {
				put(key, -value);
			}
		}
		
		public void decrementTo(K key){
			Integer v=get(key);
			if(v!=null) {
				put(key,v-1);
			} else {
				put(key,-1);
			}
		}
		

		public ArrayList<Tuple<K, Integer>> getAsSortedList(boolean sortAscending){
			ArrayList<Tuple<K, Integer>> ret=new ArrayList<Tuple<K,Integer>>();
			
			for(K key: keySet()) {
				ret.add(new Tuple<K, Integer>(key, get(key), false));
			}
			
			Collections.sort(ret);
			if(!sortAscending)
				Collections.reverse(ret);
			return ret;
		}
		
}
