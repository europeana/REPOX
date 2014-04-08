package pt.utl.ist.repox.util;

/**
 * Created by IntelliJ IDEA.
 * User: GPedrosa
 * Date: 01-04-2011
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public class ConfigSingleton {
    private static RepoxContextUtil repoxContextUtil;

    public static RepoxContextUtil getRepoxContextUtil() {
        return repoxContextUtil;
    }

    public static void setRepoxContextUtil(RepoxContextUtil repoxContextUtil) {
        ConfigSingleton.repoxContextUtil = repoxContextUtil;
    }
}
