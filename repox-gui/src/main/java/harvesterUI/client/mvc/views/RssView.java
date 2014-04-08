/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.mvc.views;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.rss.RssFeedPanel;


public class RssView extends View {

    private RssFeedPanel rssFeedPanel;

    public RssView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == AppEvents.ViewRssFeedPanel){
            eraseBrowseNavPanel();

            LayoutContainer centerPanel = (LayoutContainer) Registry.get(AppView.CENTER_PANEL);
            centerPanel.removeAll();
            centerPanel.add(rssFeedPanel);
            rssFeedPanel.loadRssFeeds();
            centerPanel.layout();
        }
    }

    @Override
    protected void initialize(){
        rssFeedPanel = new RssFeedPanel();
    }

    private void eraseBrowseNavPanel(){
        BorderLayout west = (BorderLayout) Registry.get("mainBorderLayout");
        west.hide(Style.LayoutRegion.WEST);
    }
}
