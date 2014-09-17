package harvesterUI.client.util.paging;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 23/11/12
 * Time: 17:41
 */
/*
 * Ext GWT 2.2.5 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 *
 * http://extjs.com/license
 */

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.TextBox;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionListener;

/**
 * A specialized toolbar that is bound to a {@link com.extjs.gxt.ui.client.data.ListLoader} and provides
 * automatic paging controls.
 *
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>Component Enable</dd>
 * <dd>Component Disable</dd>
 * <dd>Component BeforeHide</dd>
 * <dd>Component Hide</dd>
 * <dd>Component BeforeShow</dd>
 * <dd>Component Show</dd>
 * <dd>Component Attach</dd>
 * <dd>Component Detach</dd>
 * <dd>Component BeforeRender</dd>
 * <dd>Component Render</dd>
 * <dd>Component BrowserEvent</dd>
 * <dd>Component BeforeStateRestore</dd>
 * <dd>Component StateRestore</dd>
 * <dd>Component BeforeStateSave</dd>
 * <dd>Component SaveState</dd>
 * </dl>
 */
@SuppressWarnings("deprecation")
public class ListPagingToolBar extends ToolBar {

    /**
     * PagingToolBar images.
     */
    public static class PagingToolBarImages {
        private AbstractImagePrototype first = GXT.isHighContrastMode
                ? IconHelper.create("gxt/themes/access/images/grid/page-first.gif") : GXT.IMAGES.paging_toolbar_first();
        private AbstractImagePrototype firstDisabled = GXT.IMAGES.paging_toolbar_first_disabled();
        private AbstractImagePrototype last = GXT.isHighContrastMode
                ? IconHelper.create("gxt/themes/access/images/grid/page-last.gif") : GXT.IMAGES.paging_toolbar_last();
        private AbstractImagePrototype lastDisabled = GXT.IMAGES.paging_toolbar_last_disabled();
        private AbstractImagePrototype next = GXT.isHighContrastMode
                ? IconHelper.create("gxt/themes/access/images/grid/page-next.gif") : GXT.IMAGES.paging_toolbar_next();

        private AbstractImagePrototype nextDisabled = GXT.IMAGES.paging_toolbar_next_disabled();
        private AbstractImagePrototype prev = GXT.isHighContrastMode
                ? IconHelper.create("gxt/themes/access/images/grid/page-prev.gif") : GXT.IMAGES.paging_toolbar_prev();
        private AbstractImagePrototype prevDisabled = GXT.IMAGES.paging_toolbar_prev_disabled();
        private AbstractImagePrototype refresh = GXT.isHighContrastMode
                ? IconHelper.create("gxt/themes/access/images/grid/refresh.gif") : GXT.IMAGES.paging_toolbar_refresh();

        public AbstractImagePrototype getFirst() {
            return first;
        }

        public AbstractImagePrototype getFirstDisabled() {
            return firstDisabled;
        }

        public AbstractImagePrototype getLast() {
            return last;
        }

        public AbstractImagePrototype getLastDisabled() {
            return lastDisabled;
        }

        public AbstractImagePrototype getNext() {
            return next;
        }

        public AbstractImagePrototype getNextDisabled() {
            return nextDisabled;
        }

        public AbstractImagePrototype getPrev() {
            return prev;
        }

        public AbstractImagePrototype getPrevDisabled() {
            return prevDisabled;
        }

        public AbstractImagePrototype getRefresh() {
            return refresh;
        }

        public void setFirst(AbstractImagePrototype first) {
            this.first = first;
        }

        public void setFirstDisabled(AbstractImagePrototype firstDisabled) {
            this.firstDisabled = firstDisabled;
        }

        public void setLast(AbstractImagePrototype last) {
            this.last = last;
        }

        public void setLastDisabled(AbstractImagePrototype lastDisabled) {
            this.lastDisabled = lastDisabled;
        }

        public void setNext(AbstractImagePrototype next) {
            this.next = next;
        }

        public void setNextDisabled(AbstractImagePrototype nextDisabled) {
            this.nextDisabled = nextDisabled;
        }

        public void setPrev(AbstractImagePrototype prev) {
            this.prev = prev;
        }

        public void setPrevDisabled(AbstractImagePrototype prevDisabled) {
            this.prevDisabled = prevDisabled;
        }

        public void setRefresh(AbstractImagePrototype refresh) {
            this.refresh = refresh;
        }

    }

    /**
     * PagingToolBar messages.
     */
    public static class PagingToolBarMessages {
        private String afterPageText;
        private String beforePageText = GXT.MESSAGES.pagingToolBar_beforePageText();
        private String displayMsg;
        private String emptyMsg = GXT.MESSAGES.pagingToolBar_emptyMsg();
        private String firstText = GXT.MESSAGES.pagingToolBar_firstText();
        private String lastText = GXT.MESSAGES.pagingToolBar_lastText();
        private String nextText = GXT.MESSAGES.pagingToolBar_nextText();
        private String prevText = GXT.MESSAGES.pagingToolBar_prevText();
        private String refreshText = GXT.MESSAGES.pagingToolBar_refreshText();

        /**
         * Returns the after page text.
         *
         * @return the after page text
         */
        public String getAfterPageText() {
            return afterPageText;
        }

        /**
         * Returns the before page text.
         *
         * @return the before page text
         */
        public String getBeforePageText() {
            return beforePageText;
        }

        /**
         * Returns the display message.
         *
         * @return the display message.
         */
        public String getDisplayMsg() {
            return displayMsg;
        }

        /**
         * Returns the empty message.
         *
         * @return the empty message
         */
        public String getEmptyMsg() {
            return emptyMsg;
        }

        public String getFirstText() {
            return firstText;
        }

        /**
         * Returns the last text.
         *
         * @return the last text
         */
        public String getLastText() {
            return lastText;
        }

        /**
         * Returns the next text.
         *
         * @return the next ext
         */
        public String getNextText() {
            return nextText;
        }

        /**
         * Returns the previous text.
         *
         * @return the previous text
         */
        public String getPrevText() {
            return prevText;
        }

        /**
         * Returns the refresh text.
         *
         * @return the refresh text
         */
        public String getRefreshText() {
            return refreshText;
        }

        /**
         * Customizable piece of the default paging text (defaults to "of {0}").
         *
         * @param afterPageText the after page text
         */
        public void setAfterPageText(String afterPageText) {
            this.afterPageText = afterPageText;
        }

        /**
         * Customizable piece of the default paging text (defaults to "Page").
         *
         * @param beforePageText the before page text
         */
        public void setBeforePageText(String beforePageText) {
            this.beforePageText = beforePageText;
        }

        /**
         * The paging status message to display (defaults to "Displaying {0} - {1}
         * of {2}"). Note that this string is formatted using the braced numbers 0-2
         * as tokens that are replaced by the values for start, end and total
         * respectively. These tokens should be preserved when overriding this
         * string if showing those values is desired.
         *
         * @param displayMsg the display message
         */
        public void setDisplayMsg(String displayMsg) {
            this.displayMsg = displayMsg;
        }

        /**
         * The message to display when no records are found (defaults to "No data to
         * display").
         *
         * @param emptyMsg the empty message
         */
        public void setEmptyMsg(String emptyMsg) {
            this.emptyMsg = emptyMsg;
        }

        /**
         * Customizable piece of the default paging text (defaults to "First Page").
         *
         * @param firstText the first text
         */
        public void setFirstText(String firstText) {
            this.firstText = firstText;
        }

        /**
         * Customizable piece of the default paging text (defaults to "Last Page").
         *
         * @param lastText the last text
         */
        public void setLastText(String lastText) {
            this.lastText = lastText;
        }

        /**
         * Customizable piece of the default paging text (defaults to "Next Page").
         *
         * @param nextText the next text
         */
        public void setNextText(String nextText) {
            this.nextText = nextText;
        }

        /**
         * Customizable piece of the default paging text (defaults to "Previous
         * Page").
         *
         * @param prevText the prev text
         */
        public void setPrevText(String prevText) {
            this.prevText = prevText;
        }

        /**
         * Customizable piece of the default paging text (defaults to "Refresh").
         *
         * @param refreshText the refresh text
         */
        public void setRefreshText(String refreshText) {
            this.refreshText = refreshText;
        }

    }

    protected int activePage = -1, pages;
    protected LabelToolItem afterText;
    protected LabelToolItem beforePage;
    protected PagingLoadConfig config;
    protected LabelToolItem displayText;
    protected Button first, prev, next, last, refresh;
    protected PagingToolBarImages images;
    protected Listener<ComponentEvent> listener = new Listener<ComponentEvent>() {

        public void handleEvent(ComponentEvent be) {
            Component c = be.getComponent();
            if (be.getType() == Events.Disable) {
                if (c == first) {
                    first.setIcon(getImages().getFirstDisabled());
                } else if (c == prev) {
                    prev.setIcon(getImages().getPrevDisabled());
                } else if (c == next) {
                    next.setIcon(getImages().getNextDisabled());
                } else if (c == last) {
                    last.setIcon(getImages().getLastDisabled());
                }
            } else {
                if (c == first) {
                    first.setIcon(getImages().getFirst());
                } else if (c == prev) {
                    prev.setIcon(getImages().getPrev());
                } else if (c == next) {
                    next.setIcon(getImages().getNext());
                } else if (c == last) {
                    last.setIcon(getImages().getLast());
                }
            }
        }
    };
    protected PagingLoader<?> loader;
    protected LoadListener loadListener;
    protected PagingToolBarMessages msgs;
    protected TextBox pageText;

    protected boolean showToolTips = true;
    protected int start, pageSize, totalLength;
    private LoadEvent renderEvent;
    private boolean reuseConfig = true;
    private boolean savedEnableState = true;

    /**
     * Creates a new paging tool bar with the given page size.
     *
     * @param pageSize the page size
     */
    public ListPagingToolBar(final int pageSize) {
        this.pageSize = pageSize;

        first = new Button();
        first.addListener(Events.Disable, listener);
        first.addListener(Events.Enable, listener);
        first.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                first();
            }
        });

        prev = new Button();
        prev.addListener(Events.Disable, listener);
        prev.addListener(Events.Enable, listener);
        prev.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                previous();
            }
        });

        next = new Button();
        next.addListener(Events.Disable, listener);
        next.addListener(Events.Enable, listener);
        next.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                next();
            }
        });

        last = new Button();
        last.addListener(Events.Disable, listener);
        last.addListener(Events.Enable, listener);
        last.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                last();
            }
        });

        refresh = new Button();
        refresh.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                refresh();
            }
        });

        beforePage = new LabelToolItem();
        beforePage.setStyleName("my-paging-text");

        afterText = new LabelToolItem();
        afterText.setStyleName("my-paging-text");
        pageText = new TextBox();
        if (GXT.isAriaEnabled()) pageText.setTitle("Page");
        pageText.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    onPageChange();
                }
            }
        });

        pageText.setWidth("30px");

        displayText = new LabelToolItem();
        displayText.setId(getId() + "-display");
        displayText.setStyleName("my-paging-display");

        add(first);
        add(prev);
        add(new SeparatorToolItem());
        add(beforePage);
        add(new WidgetComponent(pageText));
        add(afterText);
        add(new SeparatorToolItem());
        add(next);
        add(last);
        add(new SeparatorToolItem());
        add(refresh);

        add(new FillToolItem());
        add(displayText);

        setMessages(new PagingToolBarMessages());
        setImages(new PagingToolBarImages());
    }

    /**
     * Binds the toolbar to the loader.
     *
     * @param loader the loader
     */
    public void bind(PagingLoader<?> loader) {
        if (this.loader != null) {
            this.loader.removeLoadListener(loadListener);
        }
        this.loader = loader;
        if (loader != null) {
            loader.setLimit(pageSize);
            if (loadListener == null) {
                loadListener = new LoadListener() {
                    public void loaderBeforeLoad(final LoadEvent le) {
                        savedEnableState = true;
                        setEnabled(true);
                        refresh.setIcon(IconHelper.createStyle("x-tbar-loading"));
                        DeferredCommand.addCommand(new Command() {
                            public void execute() {
                                if (le.isCancelled()) {
                                    refresh.setIcon(getImages().getRefresh());
                                    setEnabled(savedEnableState);
                                }
                            }
                        });
                    }

                    public void loaderLoad(LoadEvent le) {
                        refresh.setIcon(getImages().getRefresh());
                        setEnabled(savedEnableState);
                        onLoad(le);
                    }

                    public void loaderLoadException(LoadEvent le) {
                        refresh.setIcon(getImages().getRefresh());
                        setEnabled(savedEnableState);
                    }
                };
            }
            loader.addLoadListener(loadListener);
        }
    }

    /**
     * Clears the current toolbar text.
     */
    public void clear() {
        if (rendered) {
            pageText.setText("");
            afterText.setLabel("");
            displayText.setLabel("");
        }
    }

    /**
     * Moves to the first page.
     */
    public void first() {
        doLoadRequest(0, pageSize);
    }

    /**
     * Returns the active page.
     *
     * @return the active page
     */
    public int getActivePage() {
        return activePage;
    }

    public PagingToolBarImages getImages() {
        if (images == null) {
            images = new PagingToolBarImages();
        }
        return images;
    }

    /**
     * Returns the tool bar's messages.
     *
     * @return the messages
     */
    public PagingToolBarMessages getMessages() {
        return msgs;
    }

    /**
     * Returns the current page size.
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Returns the total number of pages.
     *
     * @return the
     */
    public int getTotalPages() {
        return pages;
    }

    /**
     * Returns true if the previous load config is reused.
     *
     * @return the reuse config state
     */
    public boolean isReuseConfig() {
        return reuseConfig;
    }

    /**
     * Returns true if tooltip are enabled.
     *
     * @return the show tooltip state
     */
    public boolean isShowToolTips() {
        return showToolTips;
    }

    /**
     * Moves to the last page.
     */
    public void last() {
        if (totalLength > 0) {
            int extra = totalLength % pageSize;
            int lastStart = extra > 0 ? (totalLength - extra) : totalLength - pageSize;
            doLoadRequest(lastStart, pageSize);
        }
    }

    /**
     * Moves to the last page.
     */
    public void next() {
        doLoadRequest(start + pageSize, pageSize);
    }

    /**
     * Moves the the previous page.
     */
    public void previous() {
        doLoadRequest(Math.max(0, start - pageSize), pageSize);
    }

    /**
     * Refreshes the data using the current configuration.
     */
    public void refresh() {
        doLoadRequest(start, pageSize);
    }

    /**
     * Sets the active page (1 to page count inclusive).
     *
     * @param page the page
     */
    public void setActivePage(int page) {
        if (page > pages) {
            last();
            return;
        }
        if (page != activePage && page > 0 && page <= pages) {
            doLoadRequest(--page * pageSize, pageSize);
        } else {
            pageText.setText(String.valueOf((int) activePage));
        }
    }

    public void setImages(PagingToolBarImages images) {
        this.images = images;
        refresh.setIcon(getImages().getRefresh());
        last.setIcon(last.isEnabled() ? getImages().getLast() : getImages().getLastDisabled());
        first.setIcon(first.isEnabled() ? getImages().getFirst() : getImages().getFirstDisabled());
        prev.setIcon(prev.isEnabled() ? getImages().getPrev() : getImages().getPrevDisabled());
        next.setIcon(next.isEnabled() ? getImages().getNext() : getImages().getNextDisabled());
    }

    /**
     * Sets the tool bar's messages.
     *
     * @param messages the messages
     */
    public void setMessages(PagingToolBarMessages messages) {
        msgs = messages;
        if (showToolTips) {
            first.setToolTip(msgs.getFirstText());
            prev.setToolTip(msgs.getPrevText());
            next.setToolTip(msgs.getNextText());
            last.setToolTip(msgs.getLastText());
            refresh.setToolTip(msgs.getRefreshText());
        } else {
            first.removeToolTip();
            prev.removeToolTip();
            next.removeToolTip();
            last.removeToolTip();
            refresh.removeToolTip();
        }
        if (GXT.isAriaEnabled()) {
            first.getAriaSupport().setLabel(msgs.getFirstText());
            prev.getAriaSupport().setLabel(msgs.getPrevText());
            next.getAriaSupport().setLabel(msgs.getNextText());
            last.getAriaSupport().setLabel(msgs.getLastText());
            refresh.getAriaSupport().setLabel(msgs.getRefreshText());
        }
        beforePage.setLabel(msgs.getBeforePageText());
    }

    /**
     * Sets the current page size. This method does not effect the data currently
     * being displayed. The new page size will not be used until the next load
     * request.
     *
     * @param pageSize the new page size
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * True to reuse the previous load config (defaults to true).
     *
     * @param reuseConfig true to reuse the load config
     */
    public void setReuseConfig(boolean reuseConfig) {
        this.reuseConfig = reuseConfig;
    }

    /**
     * Sets if the button tool tips should be displayed (defaults to true,
     * pre-render).
     *
     * @param showToolTips true to show tool tips
     */
    public void setShowToolTips(boolean showToolTips) {
        this.showToolTips = showToolTips;
    }

    protected void doLoadRequest(int offset, int limit) {
        if (reuseConfig && config != null) {
            config.setOffset(offset);
            config.setLimit(pageSize);
            loader.load(config);
        } else {
            loader.setLimit(pageSize);
            loader.load(offset, limit);
        }
    }

    protected void onLoad(LoadEvent event) {
        if (!rendered) {
            renderEvent = event;
            return;
        }
        config = (PagingLoadConfig) event.getConfig();
        PagingLoadResult<?> result = event.getData();
        start = result.getOffset();
        totalLength = result.getTotalLength();
        activePage = (int) Math.ceil((double) (start + pageSize) / pageSize);

        pages = totalLength < pageSize ? 1 : (int) Math.ceil((double) totalLength / pageSize);

        if (activePage > pages && totalLength > 0) {
            last();
            return;
        } else if (activePage > pages) {
            start = 0;
            activePage = 1;
        }

        pageText.setText(String.valueOf((int) activePage));

        String after = null, display = null;
        if (msgs.getAfterPageText() != null) {
            after = Format.substitute(msgs.getAfterPageText(), "" + pages);
        } else {
            after = GXT.MESSAGES.pagingToolBar_afterPageText(pages);
        }

        afterText.setLabel(after);

        first.setEnabled(activePage != 1);
        prev.setEnabled(activePage != 1);
        next.setEnabled(activePage != pages);
        last.setEnabled(activePage != pages);

        int temp = activePage == pages ? totalLength : start + pageSize;

        if (msgs.getDisplayMsg() != null) {
            String[] params = new String[] {"" + (start + 1), "" + temp, "" + totalLength};
            display = Format.substitute(msgs.getDisplayMsg(), (Object[]) params);
        } else {
            display = GXT.MESSAGES.pagingToolBar_displayMsg(start + 1, (int) temp, (int) totalLength);
        }

        String msg = display;
        if (totalLength == 0) {
            msg = msgs.getEmptyMsg();
        }
        displayText.setLabel(msg);
    }

    protected void onPageChange() {
        String value = pageText.getText();
        if (value.equals("") || !Util.isInteger(value)) {
            pageText.setText(String.valueOf((int) activePage));
            return;
        }
        int p = Integer.parseInt(value);
        setActivePage(p);
    }

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);

        if (renderEvent != null) {
            onLoad(renderEvent);
            renderEvent = null;
        }

        if (GXT.isAriaEnabled()) {
            getAriaSupport().setDescribedBy(displayText.getId());
        }
    }

}

