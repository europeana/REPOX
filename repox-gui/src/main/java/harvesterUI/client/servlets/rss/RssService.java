/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.rss;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.rss.RssItemUI;
import harvesterUI.shared.ServerSideException;

import java.util.List;

@RemoteServiceRelativePath("rssService")
public interface RssService extends RemoteService {

    public List<RssItemUI> getAllRssItems() throws ServerSideException;
    public RssItemUI getRssItem(String rssItemId) throws ServerSideException;
    public String sendRssItem(RssItemUI rssItemUI) throws ServerSideException;

}
