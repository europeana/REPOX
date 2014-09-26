package pt.utl.ist.deprecated.rest.dataProvider.sorter;
//package pt.utl.ist.repox.deprecated.rest.dataProvider.sorter;
//
//import pt.utl.ist.repox.rest.dataProvider.AggregatorEuropeana;
//
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.TreeSet;
//
//
//public abstract class AggregatorSorter {
//	public TreeSet<AggregatorEuropeana> orderAggregators(Collection<AggregatorEuropeana> aggregators, boolean filterInvalid) {
//		TreeSet<AggregatorEuropeana> orderedAggregatorsEuropeana = new TreeSet<AggregatorEuropeana>(getComparator());
//
//		for (AggregatorEuropeana aggregatorEuropeana : aggregators) {
//			if(!filterInvalid || isAggregatorValid(aggregatorEuropeana)) {
//				orderedAggregatorsEuropeana.add(aggregatorEuropeana);
//			}
//		}
//
//		return orderedAggregatorsEuropeana;
//	}
//
//	protected abstract boolean isAggregatorValid(AggregatorEuropeana AggregatorEuropeana);
//	protected abstract Comparator<AggregatorEuropeana> getComparator();
//}