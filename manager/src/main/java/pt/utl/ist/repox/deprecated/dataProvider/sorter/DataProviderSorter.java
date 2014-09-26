//package pt.utl.ist.repox.deprecated.dataProvider.sorter;
//
//import pt.utl.ist.repox.dataProvider.DataProvider;
//
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.TreeSet;
//
///**
// */
//public abstract class DataProviderSorter {
//    /**
//     * @param dataProviders
//     * @param filterInvalid
//     * @return TreeSet of DataProvider
//     */
//    public TreeSet<DataProvider> orderDataProviders(Collection<DataProvider> dataProviders, boolean filterInvalid) {
//        TreeSet<DataProvider> orderedDataProviders = new TreeSet<DataProvider>(getComparator());
//
//        for (DataProvider dataProvider : dataProviders) {
//            if (!filterInvalid || isDataProviderValid(dataProvider)) {
//                orderedDataProviders.add(dataProvider);
//            }
//        }
//
//        return orderedDataProviders;
//    }
//
//    /**
//     * @param dataProvider
//     * @return boolean
//     */
//    protected abstract boolean isDataProviderValid(DataProvider dataProvider);
//
//    protected abstract Comparator<DataProvider> getComparator();
//}
