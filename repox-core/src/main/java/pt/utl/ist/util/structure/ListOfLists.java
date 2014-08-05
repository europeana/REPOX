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
 * @param <O> 
 *
 */
public class ListOfLists<O> implements Iterable<ArrayList<O>> {
    ArrayList<ArrayList<O>> list;

    /**
     * Creates a new instance of this class.
     */
    public ListOfLists() {
        list = new ArrayList<ArrayList<O>>();
    }

    /**
     * Creates a new instance of this class.
     * @param initialCapacity
     */
    public ListOfLists(int initialCapacity) {
        list = new ArrayList<ArrayList<O>>();
    }

    /**
     * @param value
     */
    public void add(O value) {
        ArrayList<O> newList = new ArrayList<O>();
        newList.add(value);
        list.add(newList);
    }

    /**
     * @param values
     */
    public void add(Collection<? extends O> values) {
        ArrayList<O> newList = new ArrayList<O>(values);
        list.add(newList);
    }

    /**
     * @param idx
     * @param value
     */
    public void add(int idx, O value) {
        list.get(idx).add(value);
    }

    /**
     * @param idx
     * @param idx2
     * @param value
     */
    public void add(int idx, int idx2, O value) {
        list.get(idx).add(idx2, value);
    }

    /**
     * @param idx
     * @param values
     */
    public void addAll(int idx, Collection<? extends O> values) {
        list.get(idx).addAll(values);
    }

    /**
     * @param idx
     */
    public void remove(int idx) {
        list.remove(idx);
    }

    /**
     * @param idx
     * @param idx2
     */
    public void remove(int idx, int idx2) {
        list.get(idx).remove(idx2);
    }

    /**
     * @param idx
     * @return
     */
    public ArrayList<O> get(int idx) {
        return list.get(idx);
    }

    /**
     * @param idx
     * @param idx2
     * @return O at the index location
     */
    public O get(int idx, int idx2) {
        return list.get(idx).get(idx2);
    }

    /**
     * @return the size of the list
     */
    public int size() {
        return list.size();
    }

    /**
     * @return size of all lists summarized
     */
    public int sizeOfAllLists() {
        int sum = 0;
        for (ArrayList<O> l : list) {
            sum += l.size();
        }
        return sum;
    }

    /**
     * @return size of the largest list
     */
    public int sizeOfLargerList() {
        int max = 0;
        for (ArrayList<O> l : list) {
            max = Math.max(max, l.size());
        }
        return max;
    }

    @Override
    public Iterator<ArrayList<O>> iterator() {
        return list.iterator();
    }

}
