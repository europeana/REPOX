/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.rss;

import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.rss.RssItemUI;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("rssService")
public interface RssService extends RemoteService {

    public List<RssItemUI> getAllRssItems() throws ServerSideException;
    public RssItemUI getRssItem(String rssItemId) throws ServerSideException;
    public String sendRssItem(RssItemUI rssItemUI) throws ServerSideException;

}
