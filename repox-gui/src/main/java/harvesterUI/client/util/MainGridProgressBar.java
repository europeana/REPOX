package harvesterUI.client.util;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.core.CompositeElement;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Accessibility;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 18-10-2011
 * Time: 14:51
 */
public class MainGridProgressBar extends BoxComponent {

    private int duration = Style.DEFAULT;
    private int i = 0;
    private int increment = 10;
    private int interval = 300;
    private El progressBar;
    private boolean running;
    private String text = "";
    private CompositeElement textEl;
    private El textTopElem, textBackElem;
    private Timer timer;
    private double value;
    private boolean auto;

    /**
     * Creates a new progress bar.
     */
    public MainGridProgressBar() {
        baseStyle = "x-progress";
    }

    /**
     * Initiates an auto-updating progress bar using the current duration,
     * increment, and interval.
     *
     * @return this
     */
    public MainGridProgressBar auto() {
        auto = true;
        if (timer == null) {
            timer = new Timer() {
                public void run() {
                    int inc = getIncrement();
                    updateProgress(((((i++ + inc) % inc) + 1) * (100 / inc)) * .01, null);
                }
            };
        }
        timer.scheduleRepeating(getInterval());
        running = true;
        return this;
    }

    /**
     * Returns the duration.
     *
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns the bar's increment value.
     *
     * @return the increment the increment
     */
    public int getIncrement() {
        return increment;
    }

    /**
     * Returns the bar's interval value.
     *
     * @return the interval in millseconds
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Returns the current value.
     *
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns true if the progress bar is currently in a {@link #auto} operation.
     *
     * @return true if waiting, else false
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Resets the progress bar value to 0 and text to empty string.
     *
     * @return this
     */
    public MainGridProgressBar reset() {
        updateProgress(0, "");
        if (timer != null) {
            timer.cancel();
        }
        running = false;
        return this;
    }

    /**
     * The length of time in milliseconds that the progress bar should run before
     * resetting itself (defaults to DEFAULT, in which case it will run
     * indefinitely until reset is called)
     *
     * @param duration the duration in milliseconds
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * The number of progress update segments to display within the progress bar
     * (defaults to 10). If the bar reaches the end and is still updating, it will
     * automatically wrap back to the beginning.
     *
     * @param increment the new increment
     */
    public void setIncrement(int increment) {
        this.increment = increment;
    }

    /**
     * Sets the length of time in milliseconds between each progress update
     * (defaults to 300 ms).
     *
     * @param interval the interval to set
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * Updates the progress bar value, and optionally its text. If the text
     * argument is not specified, any existing text value will be unchanged. To
     * blank out existing text, pass "". Note that even if the progress bar value
     * exceeds 1, it will never automatically reset -- you are responsible for
     * determining when the progress is complete and calling {@link #reset} to
     * clear and/or hide the control.
     *
     * @param value A value between 0 and 1 (e.g., .5, defaults to 0)
     * @param text The string to display in the progress text element or null.
     * @return this
     */
    public MainGridProgressBar updateProgress(double value, String text) {
        value = Math.min(Math.max(value, 0), 1);

        this.value = value;
        if (text != null) {
            updateText(text);
        }
        if (!rendered) {
            return this;
        }

        if (GXT.isAriaEnabled()) {
            int v = (int) (value * 100);
            if (!auto) {
                Accessibility.setState(getElement(), "aria-valuenow", "" + v);
            }
            if (text != null) {
                Accessibility.setState(getElement(), "aria-valuetext", "" + text);
            }
        }

        double w = Math.floor(value * el().firstChild().getWidth());
        progressBar.setWidth((int) w);
//        if (textTopElem != null && w != 0) {
//            textTopElem.removeStyleName("x-hidden").setWidth((int) w, true);
//        } else if (textTopElem != null && w == 0) {
            textTopElem.addStyleName("x-hidden");
//        }
        fireEvent(Events.Update, new ComponentEvent(this));
        return this;
    }

    /**
     * Updates the progress bar text. If specified, textEl will be updated,
     * otherwise the progress bar itself will display the updated text.
     *
     * @param text The string to display in the progress text element
     */
    public void updateText(String text) {
        this.text = text;
        if (rendered) {
            textEl.setInnerHtml(Util.isEmptyString(text) ? "&#160;" : text);
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        update();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if (isRunning()) {
            reset();
        }
    }

    @Override
    protected void onRender(Element target, int index) {
        StringBuffer sb = new StringBuffer();
        sb.append("<div class='{cls}-wrap'><div class='{cls}-inner'><div class='{cls}-bar'>");
        sb.append("<div class='{cls}-text'><div>&#160;</div></div></div>");
        sb.append("<div class='{cls}-text {cls}-text-back'><div>&#160;</div></div></div></div>");

        Template t = new Template(sb.toString());
        setElement(t.create(new Params("cls", baseStyle)), target, index);

        El inner = el().firstChild();
        progressBar = inner.firstChild();
        textTopElem = progressBar.firstChild();
        textBackElem = inner.childNode(1);
        textTopElem.setStyleAttribute("zIndex", 99).addStyleName("x-hidden");

        textEl = new CompositeElement();
        textEl.add(textTopElem.firstChild().dom);
        textEl.add(textBackElem.firstChild().dom);

        if (GXT.isHighContrastMode) {
            textEl.getElement(0).getStyle().setProperty("backgroundColor", "#ffffff");
        }

        if (GXT.isAriaEnabled()) {
            setAriaRole("progressbar");
            if (!auto) {
                getAriaSupport().setState("aria-valuemin", "0");
                getAriaSupport().setState("aria-valuemax", "100");
            }
        }
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width, height);
        update();
    }

    private void update() {
        textEl.setWidth(el().firstChild().getWidth());
        updateProgress(value, text);
    }
}
