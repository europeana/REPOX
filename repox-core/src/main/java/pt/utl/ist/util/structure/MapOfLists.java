/*
 * Created on Oct 12, 2004
 *
 */
package pt.utl.ist.util.structure;

import java.io.Serializable;
import java.util.*;

/**
 * @author Nuno Freire
 *
 */
public class MapOfLists<K,O> implements Serializable{
	
		private static final long serialVersionUID=1;	
		
		//HashMap<K,Object[]> hashtable;
		HashMap<K,ArrayList<O>> hashtable;
		int listInitialCapacity=-1;
		
		public MapOfLists(){
			hashtable=new HashMap<K,ArrayList<O>>();
		}

		public MapOfLists(int initialCapacity){
			hashtable=new HashMap<K,ArrayList<O>>(initialCapacity);
		}
		public MapOfLists(int initialCapacity, int listInitialCapacity){
			hashtable=new HashMap<K,ArrayList<O>>(initialCapacity);
			this.listInitialCapacity = listInitialCapacity;
		}

//		public MapOfObjectArrays(int initialCapacity, int loadFactor){
//			hashtable=new IntHashtable<Object[]>(initialCapacity,loadFactor);
//		}
		
		public void put(K key, O value){
			ArrayList<O> recs=hashtable.get(key);
			if (recs==null){
				recs=new ArrayList<O>(listInitialCapacity==-1 ? 1 : listInitialCapacity);
				recs.add(value);
				hashtable.put(key,recs);
			}else {
				recs.add(value);
			}
		}

		
		public void putAll(K key, O... values){
			ArrayList<O> recs=hashtable.get(key);
			if (recs==null){
				recs=new ArrayList<O>(listInitialCapacity==-1 ? 1 : listInitialCapacity);
				for(O v:values)
					recs.add(v);
				hashtable.put(key,recs);
			}else {
				for(O v:values)
					recs.add(v);
			}
		}
		public void putAll(K key, Collection<O> values){
			ArrayList<O> recs=hashtable.get(key);
			if (recs==null){
				recs=new ArrayList<O>(listInitialCapacity==-1 ? 1 : listInitialCapacity);
				recs.addAll(values);
				hashtable.put(key,recs);
			}else {
				recs.addAll(values);
			}
		}
		
		public void putAll(Map<K,O> map){
			for(Map.Entry<K,O> entry:map.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}

		public void putAll(MapOfLists<K,O> map){
			for(K entry:map.keySet()) {
				putAll(entry, map.get(entry));
			}
		}
		
		public void remove(K key, O value){
			ArrayList<O> recs=hashtable.get(key);
			if(recs!=null) {
				recs.remove(value);
				if (recs.size()==0)
					hashtable.remove(key);
			}
		}

		public void remove(K key){
			hashtable.remove(key);
		}
		
		public boolean containsKey(K key) {
			return hashtable.containsKey(key);
		}
		
		public ArrayList<O> get(K key){
			return  hashtable.get(key);
		}

		public O get(K key, int idx){
			return  hashtable.get(key).get(idx);
		}
		
		public Set<K> keySet(){
			return hashtable.keySet();
		}
		
		public int size() {
			return hashtable.size();
		}

		public int sizeOfAllLists() {
			int total=0;
			for(K key: hashtable.keySet()) {
				total+=get(key).size();
			}
			return total;
		}
		
		
		
		public ArrayList<O> valuesOfAllLists() {
			ArrayList<O> ret=new ArrayList<O>(sizeOfAllLists());
			for(K key: hashtable.keySet()) {
				ret.addAll(get(key));
			}
			return ret;
		}
		
		public void sortLists(Comparator<O> c) {
			for(K k: hashtable.keySet()) {
				Collections.sort(hashtable.get(k), c);
			}
		}
		
		public void sortLists() {
			for(K k: hashtable.keySet()) {
				Collections.sort((ArrayList) hashtable.get(k));
			}
		}
		
		
		/**
		 * toString methode: creates a String representation of the object
		 * @return the String representation
		 * @author info.vancauwenberge.tostring plugin

		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			ArrayList keys=new ArrayList(hashtable.keySet());
			Collections.sort(keys);
			for(K key: (List<K>)keys) {
				ArrayList<O> vals=get(key);
				buffer.append(key.toString()).append("(").append(vals.size()).append(")").append(":\n");
				for(O val: vals) {
					buffer.append("\t").append(val.toString()).append("\n");					
				}
			}
			return buffer.toString();
		}

}
