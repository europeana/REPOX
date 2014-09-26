package pt.utl.ist.repox.externalServices;

import pt.utl.ist.repox.dataProvider.DataSource;

/**
 * Created to Project REPOX User: Edmundo Date: 10-09-2012 Time: 12:06
 */
public class ExternalServiceNoMonitor extends ExternalRestService {

    private DataSource dataSource;

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     * @param name
     * @param uri
     * @param dataSource
     */
    public ExternalServiceNoMonitor(String id, String name, String uri, DataSource dataSource) {
        super(id, name, uri, "", "POST_PROCESS", ExternalServiceType.NO_MONITOR);
        this.dataSource = dataSource;
    }

    @SuppressWarnings("javadoc")
    public DataSource getDataSource() {
        return dataSource;
    }
}
