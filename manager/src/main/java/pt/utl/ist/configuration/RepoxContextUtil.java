package pt.utl.ist.configuration;


/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 01-04-2011
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public interface RepoxContextUtil {
    /** RepoxContextUtil CONFIG_FILE */
    String CONFIG_FILE      = "configuration.properties";
    /** RepoxContextUtil TEST_CONFIG_FILE */
    String TEST_CONFIG_FILE = "Test-configuration.properties";

    @SuppressWarnings("javadoc")
    RepoxManager getRepoxManager();

    @SuppressWarnings("javadoc")
    RepoxManager getRepoxManagerTest();

    @SuppressWarnings("javadoc")
    void reloadProperties();
}
