package harvesterUI.client.util;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 8:32
 */
public abstract class WidgetWithRole {

    protected boolean drawWidget;

    public WidgetWithRole() {
        checkRole();
    }
    
    public abstract void checkRole();
}
