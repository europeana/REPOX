package harvesterUI.client.util;

/**
 * NotificationWindow is translation of Ext.ux.ToastWindow
 *
 * @author Edouard Fattal
 * @date March 14, 2008
 *
 * @class Ext.ux.ToastWindow
 * @extends Ext.Window
 *
 *********************************************************************
 *
 * NotificationWindow
 *
 * @author Alejandro Casagrande - Advenio Software
 * @company Advenio Software
 * @website http://www.advenio.com.ar
 * @date August 4, 2011
 *
 * @class com.extjs.gxt.ui.client.plugins.notification.NotificationWindow
 * @extends com.extjs.gxt.ui.client.widget.Window
 *
 */


import java.util.ArrayList;


import com.extjs.gxt.ui.client.Style.Direction;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.ui.RootPanel;


public class AlertMessageBox extends Window{

    protected static ArrayList<Integer> positions = new ArrayList<Integer>();

    protected DelayedTask task = null;
    protected boolean autoDestroy = false;
    protected int hideDelay = 5000;
    protected int position = 0;
    protected com.google.gwt.user.client.Element document;
    protected String message;
    protected String iconCls;
    protected Listener<BaseEvent> clickListener, onMouseOutListener;

    public static int SUCCESS_MESSAGE = 0;
    public static int ERROR_MESSAGE = 1;
    public static int INFORMATION_MESSAGE = 2;

    public int getHideDelay() {
        return hideDelay;
    }


    public void setHideDelay(int hideDelay) {
        this.hideDelay = hideDelay;
    }


    public AlertMessageBox(int duration,String title,String message,boolean autoDestroy,int messageType){
        super();
        setSize(200,100);
        this.document = RootPanel.getBodyElement();
        this.getHeader().setText(title);
        this.setMessage(message);
        setHideDelay(duration);
//        this.setAutoHeight(true);
        this.setPlain(false);
        this.setBodyBorder(false);
        this.setDraggable(false);
        this.setBodyStyle("text-align:center");
        this.autoDestroy = autoDestroy;
        this.setResizable(false);
        this.setAutoHeight(true);

        if(messageType == SUCCESS_MESSAGE)
            this.setStyleName("x-notification-success");
        else if(messageType == ERROR_MESSAGE)
            this.setStyleName("x-notification-error");
        else if(messageType == INFORMATION_MESSAGE)
            this.setStyleName("x-notification-info");

        if (autoDestroy){
            this.setClosable(false);
            task = new DelayedTask(new Listener<BaseEvent>() {
                @Override
                public void handleEvent(BaseEvent be) {
                    animOnhide();
                }
            });

            clickListener = new Listener<BaseEvent>() {
                @Override
                public void handleEvent(BaseEvent be) {
                    removeListener(Events.OnMouseOver, clickListener);
                    cancelHiding();
                }
            };
            onMouseOutListener = new Listener<BaseEvent>() {
                @Override
                public void handleEvent(BaseEvent be) {
                    restartHiding();
                }
            };
            this.addListener(Events.OnMouseOver, clickListener);
            this.addListener(Events.OnMouseOut, onMouseOutListener);
        }else{
            this.setClosable(true);
        }

        //        this.iconCls = iconCls;
//        if (iconCls!=null){
//            this.setIcon(IconHelper.createStyle(iconCls));
//        }
    }

    public void setMessage(String message){
        this.message = "<b>" + message + "</b>";
    }

    protected void cancelHiding(){
        setClosable(true);
        initTools();
//        this.addStyleName("fixed");
        if(this.autoDestroy) {
            this.task.cancel();
        }
    }

    protected void restartHiding() {
        setClosable(false);
        getHeader().repaint();
        task.cancel();
        task = new DelayedTask(new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                animOnhide();
            }
        });
        this.task.delay(this.hideDelay);
    }


    @Override
    protected void afterShow() {
        super.afterShow();

        if(this.autoDestroy) {
            this.task.delay(this.hideDelay);
        }

        Html html = new Html(message);
        this.body.update(html.getHtml());

        animShow();

    }

    protected void animOnhide(){
        FxConfig fxConfig = FxConfig.NONE;
        fxConfig.setDuration(1000);
        el().slideOut(Direction.DOWN, fxConfig);
        el().fadeOut(fxConfig);
        focusable = false;
        if (positions.indexOf(this.position)!=-1){
            positions.remove(positions.indexOf(this.position));
            this.position=-1;
        }
    }

    @Override
    protected void onHide() {
        if (positions.indexOf(this.position)!=-1){
            positions.remove(positions.indexOf(this.position));
            this.position=-1;
        }
        super.onHide();
    }

    protected void animShow(){
        this.position = 0;

        while (positions.indexOf(position)>-1){
            position++;
        }
        positions.add(position);

//        this.setSize(200,100);

        int[] offsets = new int[2];
        offsets[0] = -20;
        offsets[1] = -20-((this.getSize().height+10)*this.position);
        this.el().alignTo(document, "br-br", offsets);

        FxConfig cfg = FxConfig.NONE;
        cfg.setDuration(1);
        el().slideIn(Direction.UP, cfg);
    }
}