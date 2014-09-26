package pt.utl.ist.dataProvider.dataSource;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 28/01/13
 * Time: 16:50
 */
public class DataSourceTag {

    private String name;

    @SuppressWarnings("javadoc")
    public DataSourceTag(String name) {
        this.name = name;
    }

    @SuppressWarnings("javadoc")
    public String getName() {
        return name;
    }

    @SuppressWarnings("javadoc")
    public void setName(String name) {
        this.name = name;
    }
}
