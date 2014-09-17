package harvesterUI.shared.filters;

import java.util.Date;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 18-02-2011
 * Time: 13:48
 */
public class FilterQueryLastIngest extends FilterQuery{

    private Date beginDate;
    private Date endDate;
    private Date onDate;
    private Date beginTime;
    private Date endTime;

    public FilterQueryLastIngest() {}

    public FilterQueryLastIngest(FilterType filterType,Date beginDate, Date endDate, Date onDate, Date beginTime, Date endTime) {
        super(filterType, null);
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.onDate = onDate;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getOnDate() {
        return onDate;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
