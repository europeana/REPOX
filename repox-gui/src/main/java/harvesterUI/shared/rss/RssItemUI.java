package harvesterUI.shared.rss;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 13:34
 */
public class RssItemUI extends BaseModel implements IsSerializable {

    public RssItemUI() {}

    public RssItemUI(String title, String link, String description, String pubDate) {
        set("title",title);
        set("link",link);
        set("description", description);
        set("pubDate",pubDate);
    }

    public void setTitle(String title){set("title", title);}
    public String getTitle(){return (String) get("title");}

    public void setLink(String link){set("link", link);}
    public String getLink(){return (String) get("link");}

    public void setDescription(String description){set("description", description);}
    public String getDescription(){return (String) get("description");}

    public void setPubDate(String pubDate){set("pubDate", pubDate);}
    public String getPubDate(){return (String) get("pubDate");}
}
