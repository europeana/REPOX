package pt.utl.ist.repox.dataProvider;

import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA. User: Gilberto Pedrosa Date: 28-06-2011 Time: 17:21
 * To change this template use File | Settings | File Templates.
 */
public class DefaultDataSourceContainer extends DataSourceContainer {

    @Override
    public Element createElement() {
        return getDataSource().createElement();
    }

    /**
     * Creates a new instance of this class.
     * @param dataSource
     */
    public DefaultDataSourceContainer(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
