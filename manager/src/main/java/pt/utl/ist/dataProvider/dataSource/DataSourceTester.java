package pt.utl.ist.dataProvider.dataSource;

import org.apache.log4j.Logger;

import pt.utl.ist.dataProvider.DataSource;

/**
 */
public class DataSourceTester implements Runnable {
    private static final Logger log = Logger.getLogger(DataSourceTester.class);

    private DataSource          dataSource;
    private boolean             working;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param dataSource
     */
    public DataSourceTester(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
        this.working = false;
    }

    @Override
    public void run() {
        try {
            working = dataSource.isWorking();
        } catch (Exception e) {
            log.error("Error checking if Data Source " + dataSource.getId() + " is working", e);
            working = false;
        }
    }

}
