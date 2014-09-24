package harvesterUI.client.util.formPanel;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 30/01/12
 * Time: 14:55
 */
public class DefaultFormPanel extends FormPanel {

    public DefaultFormPanel() {
        super();
        setBorders(false);
        setFrame(false);
        setBodyBorder(false);
        setScrollMode(Style.Scroll.AUTO);
        setStyleName("repoxFormBackground");
    }
}
