package harvesterUI.client.util.formPanel;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 30/01/12
 * Time: 14:58
 */

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;

/**
 * Layout for form fields and their labels. FormLayout will only render Field
 * subclasses. All other components will be ignored.
 *
 * <p/>
 * To add a component that is not a Field subclass, see {@link com.extjs.gxt.ui.client.widget.form.AdapterField}. To
 * add plain text see {@link com.extjs.gxt.ui.client.widget.form.LabelField}.
 */
public class EditableFormLayout extends AnchorLayout {

    private int defaultWidth = 200;
    private String elementStyle;
    private Template fieldTemplate;
    private boolean hideLabels;
    private int labelAdjust;
    private LabelAlign labelAlign = LabelAlign.LEFT;
    private int labelPad = 5;
    private String labelSeparator = ":";
    private String labelStyle;
    private int labelWidth = 100;

    /**
     * Creates a new form layout.
     */

    public EditableFormLayout() {
        setLabelAlign(LabelAlign.RIGHT);
    }

    public EditableFormLayout(int labelWidth) {
        setLabelAlign(LabelAlign.RIGHT);
        setLabelSeparator("");
        this.labelWidth = labelWidth;
    }

    /**
     * Creates a new form layout.
     *
     * @param labelAlign the label alignment
     */
    public EditableFormLayout(LabelAlign labelAlign) {
        this();
        this.labelAlign = labelAlign;
    }

    /**
     * Returns the default field width.
     *
     * @return the default field width
     */
    public int getDefaultWidth() {
        return defaultWidth;
    }

    /**
     * Returns true if labels are being hidden.
     *
     * @return the hide label state
     */
    public boolean getHideLabels() {
        return hideLabels;
    }

    /**
     * Returns the label alignment.
     *
     * @return the label alignment
     */
    public LabelAlign getLabelAlign() {
        return labelAlign;
    }

    /**
     * Returns the label pad.
     *
     * @return the label pad
     */
    public int getLabelPad() {
        return labelPad;
    }

    /**
     * Returns the label separator.
     *
     * @return the label separator
     */
    public String getLabelSeparator() {
        return labelSeparator;
    }

    /**
     * Returns the label width.
     *
     * @return the label width
     */
    public int getLabelWidth() {
        return labelWidth;
    }

    @Override
    public void setContainer(Container<?> ct) {
        if (labelAlign != null && target != null) {
            target.removeStyleName("x-form-label-" + labelAlign.name().toLowerCase());
        }
        super.setContainer(ct);
    }

    /**
     * Sets the default width for fields (defaults to 200).
     *
     * @param defaultWidth the default width
     */
    public void setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    /**
     * True to hide field labels by default (defaults to false).
     *
     * @param hideLabels true to hide labels
     */
    public void setHideLabels(boolean hideLabels) {
        this.hideLabels = hideLabels;
    }

    /**
     * Sets the label alignment.
     *
     * @param labelAlign the label align
     */
    public void setLabelAlign(LabelAlign labelAlign) {
        if (this.labelAlign != labelAlign) {
            if (this.labelAlign != null && target != null) {
                target.removeStyleName("x-form-label-" + this.labelAlign.name().toLowerCase());
            }
            this.labelAlign = labelAlign;
            if (labelAlign != null && target != null) {
                target.addStyleName("x-form-label-" + labelAlign.name().toLowerCase());
            }
        }
    }

    /**
     * The default padding in pixels for field labels (defaults to 0). labelPad
     * only applies if labelWidth is also specified, otherwise it will be ignored.
     *
     * @param labelPad the label pad
     */
    public void setLabelPad(int labelPad) {
        this.labelPad = labelPad;
    }

    /**
     * Sets the label separator (defaults to ':').
     *
     * @param labelSeparator the label separator
     */
    public void setLabelSeparator(String labelSeparator) {
        this.labelSeparator = labelSeparator;
    }

    /**
     * Sets the default width in pixels of field labels (defaults to 100).
     *
     * @param labelWidth the label width
     */
    public void setLabelWidth(int labelWidth) {
        this.labelWidth = labelWidth;
    }

    @Override
    protected int adjustHeightAnchor(int height, Component comp) {
        if (comp instanceof Field<?>) {
            Field<?> f = (Field<?>) comp;
            if (!f.isHideLabel() && labelAlign.equals(LabelAlign.TOP)) {
                El elem = findLabelElement(comp);
                if (elem != null) {
                    height -= elem.getHeight();
                }
            }
        }
        return super.adjustHeightAnchor(height, comp);
    }

    @Override
    protected int adjustWidthAnchor(int width, Component comp) {
        if (comp instanceof Field<?>) {
            Field<?> f = (Field<?>) comp;
            width -= (f.isHideLabel() ? 0 : labelAdjust);

            // offset due to the label element
            if (((GXT.isIE && !GXT.isStrict) || GXT.isIE6) && !labelAlign.equals(LabelAlign.TOP) && !f.isHideLabel()) {
                width -= 3;
            }
        }

        return super.adjustWidthAnchor(width, comp);
    }

    @Override
    protected void initTarget() {
        super.initTarget();
        if (labelAlign != null && target != null) {
            target.addStyleName("x-form-label-" + labelAlign.name().toLowerCase());
        }
    }

    @Override
    protected void onComponentHide(Component component) {
        super.onComponentHide(component);
        El e = findItemElement(component);
        if (e != null) {
            e.addStyleName(component.getHideMode().value());
        }

    }

    @Override
    protected void onComponentShow(Component component) {
        super.onComponentShow(component);
        El e = findItemElement(component);
        if (e != null) {
            e.removeStyleName(component.getHideMode().value());
        }
    }

    @Override
    protected void onLayout(Container<?> container, El target) {
        if (hideLabels) {
            labelStyle = "display:none";
            elementStyle = "padding-left:0;";
            labelAdjust = 0;
        } else {
            int pad = labelPad != 0 ? labelPad : 5;
            labelAdjust = labelWidth + pad;
            labelStyle = "width:" + (labelWidth) + "px";
            elementStyle = "padding-left:" + (labelWidth + pad) + "px";
            if (labelAlign == LabelAlign.TOP) {
                labelStyle = "width:auto;";
                elementStyle = "padding-left:0;";
                labelAdjust = 0;
            }
        }
        if (fieldTemplate == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("<div role='presentation' class='x-form-item {5}' tabIndex='-1'>");
            sb.append("<label for={8} style='{2};{7}' class=defaultFormFieldLabel>{1}{4}</label>");
            sb.append("<div role='presentation' class='x-form-element x-form-el-{0}' id='x-form-el-{0}' style='{3}'>");
            sb.append("</div><div class='{6}' role='presentation'></div>");
            sb.append("</div>");
            fieldTemplate = new Template(sb.toString());
            fieldTemplate.compile();
        }
        super.onLayout(container, target);
    }

    @Override
    protected void onRemove(Component component) {
        super.onRemove(component);
        El elem = findItemElement(component);
        if (elem != null) {
            elem.removeFromParent();
        }
    }

    @SuppressWarnings("unused")
    @Override
    protected void renderComponent(Component component, int index, El target) {
        if (component instanceof Field<?> && !(component instanceof HiddenField<?>)) {
            Field<?> f = (Field<?>) component;

            FormData layoutData = null;
            LayoutData d = getLayoutData(f);
            if (d != null && d instanceof FormData) {
                layoutData = (FormData) d;
            } else {
                layoutData = f.getData("formData");
            }
            if (layoutData == null) {
                layoutData = new FormData();
            }

            if (layoutData != null) {
                if (layoutData.getWidth() > 0) {
                    f.setWidth(layoutData.getWidth());
                } else if (layoutData.getAnchorSpec() == null) {
                    f.setWidth(defaultWidth);
                }
                if (layoutData.getHeight() > 0) {
                    f.setHeight(layoutData.getHeight());
                }
            } else {
                f.setWidth(defaultWidth);
            }
            renderField(f, index, target);

        } else {
            super.renderComponent(component, index, target);
        }
    }

    protected void renderField(Field<?> field, int index, El target) {
        String ls = field.getLabelSeparator() != null ? field.getLabelSeparator() : labelSeparator;
        field.setLabelSeparator(ls);
        Params p = new Params();
        if (hideLabels) {
            field.setHideLabel(true);
        }

        p.add(field.getId());
        p.add(field.getFieldLabel());
        p.add(labelStyle);
        p.add(elementStyle);
        p.add(ls);
        p.add(field.isHideLabel() ? "x-hide-label" : "");
        p.add("x-form-clear-left");
        p.add(field.getLabelStyle());

        String inputId = field.getId();
        p.add(inputId);

        fieldTemplate.insert(target.dom, index, p);
        if (field.isRendered()) {
            target.selectNode(".x-form-el-" + field.getId()).appendChild(field.getElement());
        } else {
            field.render(target.selectNode(".x-form-el-" + field.getId()).dom);
        }

        if (field.getStyleName().contains("-wrap")) {
            inputId += "-input";
            target.selectNode(".x-form-el-" + field.getId()).previousSibling().setAttribute("for", inputId);
        }
    }

    private El findItemElement(Component c) {
        if (c != null && c instanceof Field<?> && c.isRendered()) {
            El elem = target.selectNode(".x-form-el-" + c.getId());
            if (elem != null) {
                return elem.findParent(".x-form-item", 5);
            }
            return null;
        }
        return null;
    }

    private El findLabelElement(Component c) {
        El elem = findItemElement(c);
        if (elem != null) {
            return elem.firstChild();
        }
        return null;
    }
}

