package harvesterUI.client.panels.mdr.forms.xmapper;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.user.client.DOM;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 17-01-2013
 * Time: 18:29
 */

//TODO: multi lang
public class SchemasDetailsContainer extends LayoutContainer{

    protected HBoxLayoutData _data;
    protected LayoutContainer _sourceDetails, _targetDetails;

    public SchemasDetailsContainer() {
        setLayout(new HBoxLayout());
        _data = new HBoxLayoutData(0,5,0,0);
        _data.setFlex(1);

        FieldSet fs = new FieldSet();
        fs.setHeading("Source Format Info");

        _sourceDetails = new LayoutContainer();
        _sourceDetails.setStyleAttribute("border", "solid 1px rgb(181,184,200)");
        _sourceDetails.setStyleAttribute("background-color", "white");
        _sourceDetails.setStyleAttribute("padding", "5px");
        _sourceDetails.setHeight(54);
        _sourceDetails.setAutoHeight(true);
        _sourceDetails.setElement(DOM.createElement("div"));
        fs.add(_sourceDetails);
        this.add(fs, _data);


        fs = new FieldSet();
        fs.setHeading("Destination Format Info");

        _targetDetails = new LayoutContainer();
        _targetDetails.setStyleAttribute("border", "solid 1px rgb(181,184,200)");
        _targetDetails.setStyleAttribute("background-color", "white");
        _targetDetails.setStyleAttribute("padding", "5px");
        _targetDetails.setHeight(54);
        _targetDetails.setAutoHeight(true);
        _targetDetails.setElement(DOM.createElement("div"));
        fs.add(_targetDetails);
        this.add(fs, _data);
    }

    public void populateDetails(SchemaUI schema, SchemaVersionUI version, boolean isSource) {
        populateDetails(schema.getShortDesignation(), version.getXsdLink(), ""+version.getVersion(), isSource);
    }

    public void populateDetails(String shortDesig, String xsdLink, String ver, boolean isSource) {
        LayoutContainer container = new LayoutContainer();
        Text top, mid, bot;

        top = new Text(shortDesig);
        top.setStyleName("mdr-transf-details-top");
        top.setToolTip(shortDesig);
        mid = new Text(xsdLink);
        mid.setStyleName("mdr-transf-details-mid");
        mid.setToolTip(xsdLink);
        bot = new Text(shortDesig+", Version "+ver);
        bot.setStyleName("mdr-transf-details-bot");
        bot.setToolTip(shortDesig+", Version "+ver);
        container.add(top);
        container.add(mid);
        container.add(bot);

        if(isSource) {
            _sourceDetails.removeAll();
            _sourceDetails.add(container);
        }
        else {
            _targetDetails.removeAll();
            _targetDetails.add(container);
        }
        layout(true);
    }

    public void clearDetails() {
        _sourceDetails.removeAll();
        _targetDetails.removeAll();
    }
}
