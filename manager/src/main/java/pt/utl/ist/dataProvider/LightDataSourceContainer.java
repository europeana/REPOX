package pt.utl.ist.dataProvider;

import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA. User: Gilberto Pedrosa Date: 28-06-2011 Time: 17:21
 * To change this template use File | Settings | File Templates.
 */
public class LightDataSourceContainer extends DataSourceContainer {

    @Override
    public Element createElement() {
        return getDataSource().createElement();
    }

    /**
     * Creates a new instance of this class.
     * @param dataSource
     */
    public LightDataSourceContainer(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
