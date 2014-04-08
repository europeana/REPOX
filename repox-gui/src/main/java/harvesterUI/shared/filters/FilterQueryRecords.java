package harvesterUI.shared.filters;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 18-02-2011
 * Time: 13:48
 */
public class FilterQueryRecords extends FilterQuery{

    private int beginRecords;
    private int endRecords;
    private int onRecords;

    public FilterQueryRecords() {}

    public FilterQueryRecords(FilterType filterType, int beginRecords, int endRecords, int onRecords) {
        super(filterType, null);
        this.beginRecords = beginRecords;
        this.endRecords = endRecords;
        this.onRecords = onRecords;
    }

    public int getBeginRecords() {
        return beginRecords;
    }

    public int getEndRecords() {
        return endRecords;
    }

    public int getOnRecords() {
        return onRecords;
    }
}
