package pt.utl.ist.rest.dataProvider.sorter;

import pt.utl.ist.rest.dataProvider.AggregatorEuropeana;

import java.util.Comparator;


public class NameSorter extends AggregatorSorter {
	@Override
	protected Comparator<AggregatorEuropeana> getComparator() {
		return new Comparator<AggregatorEuropeana>() {
			public int compare(AggregatorEuropeana o1, AggregatorEuropeana o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};
	}

	@Override
	protected boolean isAggregatorValid(AggregatorEuropeana AggregatorEuropeana) {
		return true;
	}
}
