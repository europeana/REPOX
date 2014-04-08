package harvesterUI.server.rss;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import harvesterUI.client.servlets.rss.RssService;
import harvesterUI.server.util.Util;
import harvesterUI.shared.rss.RssItemUI;
import harvesterUI.shared.ServerSideException;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RssServiceImpl extends RemoteServiceServlet implements RssService {

    public RssServiceImpl() {}

    public List<RssItemUI> getAllRssItems() throws ServerSideException {
        List<RssItemUI> rssItems = new ArrayList<RssItemUI>();

        URL url  = null;
        try {
            url = new URL("http://repox.ist.utl.pt/rssRepox.xml");
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
        XmlReader reader = null;
        try {
            reader = new XmlReader(url);
            SyndFeed feed = new SyndFeedInput().build(reader);

            for(Iterator i = feed.getEntries().iterator(); i.hasNext();) {
                SyndEntry entry = (SyndEntry) i.next();

                Date itemPublishedDate = entry.getPublishedDate();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
                
                rssItems.add(new RssItemUI(entry.getTitle(),entry.getLink(),
                        entry.getDescription().getValue(), itemPublishedDate == null ? "" : formatter.format(itemPublishedDate)));
            }
        }catch (UnknownHostException e){
//            e.printStackTrace();
            return null;
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return rssItems;
    }

    public RssItemUI getRssItem(String rssItemId) throws ServerSideException{
        return null;
    }

    public String sendRssItem(RssItemUI rssItemUI) throws ServerSideException{
        return "YES";
    }

}
