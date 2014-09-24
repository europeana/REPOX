package pt.utl.ist.repox.statistics;

import java.util.Calendar;

/**
 */
public class RecordCount {
    private String   dataSourceId;
    private int      count, deleted;
    private int      lastLineCounted;
    private Calendar lastCountDate;
    private Calendar lastCountWithChangesDate;

    @SuppressWarnings("javadoc")
    public int getCount() {
        return count;
    }

    @SuppressWarnings("javadoc")
    public void setCount(int count) {
        this.count = count;
    }

    @SuppressWarnings("javadoc")
    public int getLastLineCounted() {
        return lastLineCounted;
    }

    @SuppressWarnings("javadoc")
    public void setLastLineCounted(int lastLineCounted) {
        this.lastLineCounted = lastLineCounted;
    }

    @SuppressWarnings("javadoc")
    public String getDataSourceId() {
        return dataSourceId;
    }

    @SuppressWarnings("javadoc")
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @SuppressWarnings("javadoc")
    public Calendar getLastCountDate() {
        return lastCountDate;
    }

    @SuppressWarnings("javadoc")
    public void setLastCountDate(Calendar lastCountDate) {
        this.lastCountDate = lastCountDate;
    }

    @SuppressWarnings("javadoc")
    public Calendar getLastCountWithChangesDate() {
        return lastCountWithChangesDate;
    }

    @SuppressWarnings("javadoc")
    public void setLastCountWithChangesDate(Calendar lastCountWithChangesDate) {
        this.lastCountWithChangesDate = lastCountWithChangesDate;
    }

    @SuppressWarnings("javadoc")
    public int getDeleted() {
        return deleted;
    }

    @SuppressWarnings("javadoc")
    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    /**
     * Creates a new instance of this class.
     * @param dataSourceId
     * @param count
     * @param deleted
     * @param lastLineCounted
     * @param lastCountDate
     * @param lastCountWithChangesDate
     */
    public RecordCount(String dataSourceId, int count, int deleted, int lastLineCounted, Calendar lastCountDate, Calendar lastCountWithChangesDate) {
        super();
        this.dataSourceId = dataSourceId;
        this.count = count;
        this.deleted = deleted;
        this.lastLineCounted = lastLineCounted;
        this.lastCountDate = lastCountDate;
        this.lastCountWithChangesDate = lastCountWithChangesDate;
    }

}
