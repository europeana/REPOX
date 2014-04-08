package pt.utl.ist.repox.util;

import pt.utl.ist.repox.RepoxManager;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 01-04-2011
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public interface RepoxContextUtil {

    public static final String CONFIG_FILE = "configuration.properties";
    public static final String TEST_CONFIG_FILE = "Test-configuration.properties";

    public RepoxManager getRepoxManager();

    public RepoxManager getRepoxManagerTest();

    public void reloadProperties();
}
