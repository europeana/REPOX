package harvesterUI.client.panels.rss;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.rss.RssServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.shared.rss.RssItemUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 12:51
 */
public class RssFeedPanel extends ContentPanel{

    private RssServiceAsync rssServiceAsync;
    private Grid<RssItemUI> grid;

    public RssFeedPanel() {
        setLayout(new FitLayout());

        rssServiceAsync = (RssServiceAsync) Registry.get(HarvesterUI.RSS_SERVICE);

        setHeading(HarvesterUI.CONSTANTS.rssPanelTitle());
        setIcon(HarvesterUI.ICONS.rss_feed_icon());

        createRssPanel();
    }

    protected void createRssPanel(){
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        XTemplate tpl = XTemplate.create("<p>{description}</p>");

        RowExpander expander = new RowExpander();
        expander.setTemplate(tpl);
        configs.add(expander);

        ColumnConfig column = new ColumnConfig("pubDate",HarvesterUI.CONSTANTS.date(),50);
        configs.add(column);

        column = new ColumnConfig("title",HarvesterUI.CONSTANTS.title(),100);
        configs.add(column);

        column = new ColumnConfig("link",HarvesterUI.CONSTANTS.link(),100);
        configs.add(column);
        GridCellRenderer<RssItemUI> linkRenderer = new GridCellRenderer<RssItemUI>() {
            public String render(RssItemUI model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<RssItemUI> stor, Grid<RssItemUI> grid) {
                return "<a href="+model.getLink()+" target=\"_blank\">"+model.getLink()+"</a>";
            }
        };
        column.setRenderer(linkRenderer);

        column = new ColumnConfig("description",HarvesterUI.CONSTANTS.description(),250);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        grid = new Grid<RssItemUI>(new ListStore<RssItemUI>(), cm);
        grid.getView().setForceFit(true);
        grid.addPlugin(expander);
        grid.getView().setEmptyText(HarvesterUI.CONSTANTS.noFeedsAvailable());
        add(grid);
    }

    public void loadRssFeeds(){
        grid.mask(HarvesterUI.CONSTANTS.loadingFeedsMask());
        AsyncCallback<List<RssItemUI>> callback = new AsyncCallback<List<RssItemUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<RssItemUI> rssItems) {
                grid.getStore().removeAll();
                grid.getStore().add(rssItems);
                grid.getStore().sort("pubDate", Style.SortDir.DESC);
                grid.unmask();
            }
        };
        rssServiceAsync.getAllRssItems(callback);
    }
}
