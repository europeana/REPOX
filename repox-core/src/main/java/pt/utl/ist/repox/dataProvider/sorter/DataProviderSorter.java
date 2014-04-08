package pt.utl.ist.repox.dataProvider.sorter;

import pt.utl.ist.repox.dataProvider.DataProvider;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;


public abstract class DataProviderSorter {
	public TreeSet<DataProvider> orderDataProviders(Collection<DataProvider> dataProviders, boolean filterInvalid) {
		TreeSet<DataProvider> orderedDataProviders = new TreeSet<DataProvider>(getComparator());
		
		for (DataProvider dataProvider : dataProviders) {
			if(!filterInvalid || isDataProviderValid(dataProvider)) {
				orderedDataProviders.add(dataProvider);
			}
		}
		
		return orderedDataProviders;
	}
	
	protected abstract boolean isDataProviderValid(DataProvider dataProvider);
	protected abstract Comparator<DataProvider> getComparator();
}
