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
    /** RepoxContextUtil OAI_FILE */
    String OAI_FILE      = "oaicat.properties";

    RepoxManager getRepoxManager();

    RepoxManager getRepoxManagerTest();

    void reloadProperties();
}
