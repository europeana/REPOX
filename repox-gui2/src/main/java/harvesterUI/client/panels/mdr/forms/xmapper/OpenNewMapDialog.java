package harvesterUI.client.panels.mdr.forms.xmapper;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.panels.mdr.xmapper.MDRMappingApplicationManager;
import harvesterUI.client.panels.mdr.xmapper.OpenMapInfo;
import harvesterUI.client.panels.mdr.xmapper.XMApperContainer;
import harvesterUI.client.servlets.xmapper.XMApperService;
import harvesterUI.client.servlets.xmapper.XMApperServiceAsync;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import pt.ist.mdr.gwt.client.widgets.utils.ExceptionDialog;
import pt.ist.mdr.mapping.ui.client.model.MappingScriptProxy;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 12-12-2012
 * Time: 13:44
 */

//TODO all - multi lang
public class OpenNewMapDialog extends FormDialog {

    private SourceAndTargetFields _sourceTargetFields;
    private XMApperContainer _xmapperContainer;

    public OpenNewMapDialog(XMApperContainer parent) {
        super(0.55, 0.41);
        _xmapperContainer = parent;
        setupForm();
    }

    public void setupForm() {
        FormData formData = new FormData("98%");
        setHeading("Open New Map: Choose source and destination formats");
        setIcon(HarvesterUI.ICONS.schema_mapper_icon());

        DefaultFormPanel formPanel;
        formPanel = new DefaultFormPanel();
        formPanel.setHeaderVisible(false);
        formPanel.setLayout(new EditableFormLayout(160));

        _sourceTargetFields = new SourceAndTargetFields(formPanel,formData);
        _sourceTargetFields.loadCombos(null);
        SchemasDetailsContainer c = _sourceTargetFields.getDetailsContainer();
        formPanel.add(c, formData);

        Button openButton = new Button("Open",HarvesterUI.ICONS.schema_mapper_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                openMap();
            }
        });
        formPanel.addButton(openButton);
        formPanel.setButtonAlign(Style.HorizontalAlignment.CENTER);
        FormButtonBinding binding = new FormButtonBinding(formPanel);
        binding.addButton(openButton);

        add(formPanel);
    }

    @Override
    public void  onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        layout();
    }


    public void openMap() {
        _xmapperContainer.getXMApperPanel().unmask();
        _xmapperContainer.getXMApperPanel().mask("Loading mappings from server... please wait...");
        hide();

        XMApperServiceAsync service = GWT.create(XMApperService.class);
        service.openUserEmptyMap(_sourceTargetFields.getSourceSchema(), _sourceTargetFields.getTargetSchema(), new AsyncCallback<MappingScriptProxy>() {

            @Override
            public void onFailure(Throwable caught) {
                _xmapperContainer.resetXMApperPanel();
                new ExceptionDialog("New Map Error", caught.getMessage(), caught).show();
            }

            @Override
            public void onSuccess(MappingScriptProxy mappingModel) {
                _xmapperContainer.getXMApperPanel().unmask();
                OpenMapInfo mapInfo = new OpenMapInfo(true, _sourceTargetFields.getDetailsContainer(),
                        _sourceTargetFields.getSourceSchemaUI(), _sourceTargetFields.getDestSchemaUI(),
                        _sourceTargetFields.getSourceVersionUI(), _sourceTargetFields.getDestVersionUI());
                //_xmapperContainer.resetXMApperPanel();
                _xmapperContainer.setMapInfo(mapInfo);
                _xmapperContainer.getManager().deploy(mappingModel);
            }
        });
    }

}
