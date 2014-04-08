package harvesterUI.client.util;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.HTML;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 25-11-2011
 * Time: 15:45
 */
public class ServerExceptionDialog extends Dialog {

    public ServerExceptionDialog(String title, String message) {
        setHeading(title);
        setLayout(new RowLayout(Style.Orientation.VERTICAL));
        setResizable(false);
        setConstrain(true);
        setMinimizable(false);
        setMaximizable(false);
        setModal(true);
        setButtonAlign(Style.HorizontalAlignment.CENTER);
        setPlain(true);
        setFooter(true);
        setButtons(Dialog.OK);
        setHideOnButtonClick(true);
        setSize(600, 400);
        setBodyBorder(false);

        addStyleName("x-window-dlg");
        String smallMessage = "";
        if(message != null) {
            if(message.length() > 100) smallMessage = message.substring(0, 100);
        }
        else smallMessage = "";
        String html = "<div style='text-aling: center' class='ext-mb-icon ext-mb-warning'></div>" +
                "<div style='vertical-align: middle' class=ext-mb-content><span class=ext-mb-text>"
                + smallMessage + ": See the log message for details." + "</span></div>";
        add(new HTML(html), new RowData(1, 50));

        String stack = message;
        HtmlContainer messageContainer = new HtmlContainer(stack);
        //messageContainer.setStyleAttribute("white-space", "nowrap");
        messageContainer.setStyleAttribute("font-family", "Courier New");
        //messageContainer.setStyleAttribute("line-height", "8px");
        //messageContainer.setStyleAttribute(attr, value);
        LayoutContainer container = new LayoutContainer(new FitLayout());
        container.setScrollMode(Style.Scroll.AUTO);
        container.add(messageContainer);
        container.setStyleAttribute("background-color", "white");
        container.setBorders(true);
        add(container, new RowData(1, 1));
    }
}
