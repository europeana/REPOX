package harvesterUI.client.util;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Element;
import harvesterUI.client.HarvesterUI;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 10-04-2012
 * Time: 16:36
 */
public class ConfirmMessageBox extends Dialog {

    private String icon = "";
    private int minWidth = 100;
    private String message = "&#160;";
    private Element iconEl;
    private Element msgEl;

    public ConfirmMessageBox(String title, String message, SelectionListener<ButtonEvent> buttonListener) {
        this.message = message;

        setHeading(title);
        setResizable(false);
        setConstrain(true);
        setMinimizable(false);
        setMaximizable(false);
        setMinWidth(minWidth);
        setClosable(false);
        setModal(true);
        setButtonAlign(Style.HorizontalAlignment.CENTER);
        setMinHeight(80);
        setPlain(true);
        setFooter(true);
        setButtons("");

        SelectionListener<ButtonEvent> hideOnClickListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        };

        Button yesButton = new Button(HarvesterUI.CONSTANTS.yes(),HarvesterUI.ICONS.yes_icon());
        yesButton.addSelectionListener(buttonListener);
        yesButton.addSelectionListener(hideOnClickListener);
        setFocusWidget(yesButton);
        addButton(yesButton);

        Button noButton = new Button(HarvesterUI.CONSTANTS.no(),HarvesterUI.ICONS.cancel_icon());
        noButton.addSelectionListener(hideOnClickListener);
        addButton(noButton);
    }

    @Override
    protected void onRender(Element element, int index) {
        super.onRender(element, index);
        addStyleName("x-window-dlg");

        El body = new El(getElement("body"));

        String html = "<div class='ext-mb-icon x-hidden'></div><div class=ext-mb-content><span class=ext-mb-text></span><br /></div>";
        body.dom.setInnerHTML(html);

        iconEl = body.firstChild().dom;
        Element contentEl = body.dom.getChildNodes().getItem(1).cast();
        msgEl = contentEl.getFirstChild().cast();
        msgEl.setInnerHTML(message);
        msgEl.setId(getId() + "-content");

        getAriaSupport().setDescribedBy(getId() + "-content");

        setIcon("ext-mb-question");
    }

    @Override
    protected void initTools() {
        setClosable(false);
        super.initTools();
    }

    public void setIcon(String iconStyle) {
        this.icon = iconStyle;
        if (iconEl != null) {
            El el = El.fly(iconEl);
            if (iconStyle != null) {
                el.removeStyleName("x-hidden");
                el.replaceStyleName(this.icon, iconStyle);
            } else {
                el.replaceStyleName(this.icon, "x-hidden");
                icon = "";
            }
        }
    }
}
