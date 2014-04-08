/*
 * Created on Oct 13, 2004
 *
 */
package pt.utl.ist.util.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nuno Freire
 *
 */
public class ListOfLists<O> implements Iterable<ArrayList<O>> {
	ArrayList<ArrayList<O>> list;
	
	public ListOfLists(){
		list=new ArrayList<ArrayList<O>>();
	}
	
	public ListOfLists(int initialCapacity){
		list=new ArrayList<ArrayList<O>>();
	}
	
	public void add(O value) {
		ArrayList<O> newList=new ArrayList<O>();
		newList.add(value);
		list.add(newList);
	}
	
	public void add(Collection<? extends O> values) {
		ArrayList<O> newList=new ArrayList<O>(values);
		list.add(newList);
	}

	public void add(int idx, O value) {
		list.get(idx).add(value);
	}

	public void add(int idx, int idx2, O value) {
		list.get(idx).add(idx2, value);
	}
	
	public void addAll(int idx, Collection<? extends O> values) {
		list.get(idx).addAll(values);
	}
	
	public void remove(int idx) {
		list.remove(idx);
	}

	public void remove(int idx, int idx2) {
		list.get(idx).remove(idx2);
	}
	
	public ArrayList<O> get(int idx){
		return list.get(idx);
	}

	public O get(int idx, int idx2) {
		return list.get(idx).get(idx2);
	}
	
	
	
	public int size() {
		return list.size();
	}

	public int sizeOfAllLists() {
		int sum=0;
		for(ArrayList<O> l: list) {
			sum+=l.size();
		}
		return sum;
	}
	public int sizeOfLargerList() {
		int max=0;
		for(ArrayList<O> l: list) {
			max=Math.max(max, l.size());
		}
		return max;
	}

	public Iterator<ArrayList<O>> iterator() {
		return list.iterator();
	}
	
}
