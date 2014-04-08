/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.rss;

import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.rss.RssItemUI;

import java.util.List;

//import harvesterUI.client.models.FilterAttributes;
//import harvesterUI.client.models.MailItem;

public interface RssServiceAsync {

    public void getRssItem(String rssItemId, AsyncCallback<RssItemUI> callback);
    public void getAllRssItems(AsyncCallback<List<RssItemUI>> callback);
    public void sendRssItem(RssItemUI rssItemUI,AsyncCallback<String> callback);
}
