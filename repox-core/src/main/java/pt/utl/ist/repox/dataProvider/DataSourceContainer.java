package pt.utl.ist.repox.dataProvider;

import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Gilberto Pedrosa
 * Date: 28-06-2011
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataSourceContainer {
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public abstract Element createElement();
}
