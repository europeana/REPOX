package harvesterUI.client.panels.mdr.forms.xmapper;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.panels.mdr.xmapper.MDRMappingApplicationManager;
import harvesterUI.client.panels.mdr.xmapper.OpenMapInfo;
import harvesterUI.client.panels.mdr.xmapper.XMApperContainer;
import harvesterUI.client.servlets.transformations.TransformationsServiceAsync;
import harvesterUI.client.servlets.xmapper.XMApperService;
import harvesterUI.client.servlets.xmapper.XMApperServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.mdr.TransformationUI;
import pt.ist.mdr.gwt.client.widgets.utils.ExceptionDialog;
import pt.ist.mdr.mapping.ui.client.model.MappingScriptProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 22-01-2013
 * Time: 16:36
 */

//TODO multi lang
public class OpenExistingMapDialog  extends FormDialog{

    private XMApperContainer _xmapperContainer;
    private ComboBox<TransformationUI> _transformationsCombo;
    private SourceAndTargetFields _sourceTargetFields;
    //private SchemasDetailsContainer _schemasDetails;

    public OpenExistingMapDialog(XMApperContainer parent) {
        super(0.40, 0.40);
        _xmapperContainer = parent;
        setupForm();
        //Populate combo
        loadCombo();
    }

    public void setupForm() {
        FormData formData = new FormData("98%");
        setHeading("Open Existing Map");
        setIcon(HarvesterUI.ICONS.schema_mapper_icon());

        DefaultFormPanel formPanel;
        formPanel = new DefaultFormPanel();
        formPanel.setHeaderVisible(false);
        formPanel.setLayout(new EditableFormLayout(160));

        _transformationsCombo = new ComboBox<TransformationUI>();
        _transformationsCombo.setFieldLabel("Transformation" + HarvesterUI.REQUIRED_STR);
        _transformationsCombo.setId("transformationComboId");
        ListStore<TransformationUI> transformationsStore = new ListStore<TransformationUI>();
        _transformationsCombo.setEmptyText("Choose Editable Transformation...");
        _transformationsCombo.setAllowBlank(false);

        _transformationsCombo.setDisplayField("identifier");
        _transformationsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        _transformationsCombo.setStore(transformationsStore);
        _transformationsCombo.setEditable(true);
        //Add listener
        _transformationsCombo.addSelectionChangedListener(new SelectionChangedListener<TransformationUI>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<TransformationUI> se) {
                if (se.getSelectedItem() != null) {
                    updateSchemasDetails();
                }
            }
        });
        formPanel.add(_transformationsCombo, formData);

        //TODO make and place here a Transformations Details Container
        _sourceTargetFields = new SourceAndTargetFields(new DefaultFormPanel(), formData);
        _sourceTargetFields.loadCombos(null);
        SchemasDetailsContainer schemasDetails = _sourceTargetFields.getDetailsContainer();
        formPanel.add(schemasDetails, formData);

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

    public void openMap() {
        _xmapperContainer.getXMApperPanel().unmask();
        _xmapperContainer.getXMApperPanel().mask("Loading mappings from server... please wait...");
        hide();

        XMApperServiceAsync service = GWT.create(XMApperService.class);
        service.openExistingMap(_transformationsCombo.getValue(), new AsyncCallback<MappingScriptProxy>() {
            @Override
            public void onFailure(Throwable caught) {
                _xmapperContainer.resetXMApperPanel();
                new ExceptionDialog("Open Existing Map Error", caught.getMessage(), caught).show();
            }

            @Override
            public void onSuccess(MappingScriptProxy mappingModel) {
                _xmapperContainer.getXMApperPanel().unmask();
                OpenMapInfo mapInfo = new OpenMapInfo(false, _sourceTargetFields.getDetailsContainer(),
                        _sourceTargetFields.getSourceSchemaUI(), _sourceTargetFields.getDestSchemaUI(),
                        _sourceTargetFields.getSourceVersionUI(), _sourceTargetFields.getDestVersionUI());
                mapInfo.setTransformation(_transformationsCombo.getValue());
                //_xmapperContainer.resetXMApperPanel();
                _xmapperContainer.setMapInfo(mapInfo);
                _xmapperContainer.getManager().deploy(mappingModel);
            }
        });
    }

    public void loadCombo(){
        AsyncCallback<List<TransformationUI>> callback = new AsyncCallback<List<TransformationUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<TransformationUI> results) {
                List<TransformationUI> editables = new ArrayList<TransformationUI>();
                for(TransformationUI t : results)
                    if(t.isEditable())
                        editables.add(t);
                _transformationsCombo.getStore().removeAll();
                _transformationsCombo.getStore().add(editables);
                _transformationsCombo.getStore().clearFilters(); // clear combo boxes filters
                _transformationsCombo.setValue(_transformationsCombo.getStore().getAt(0)); //Default transformation
            }
        };
        TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        service.getFullTransformationsList(callback);
    }

    public void updateSchemasDetails() {
        TransformationUI t = _transformationsCombo.getValue();
        _sourceTargetFields.edit(t);
        /*AsyncCallback<List<SchemaUI>> callback = new AsyncCallback<List<SchemaUI>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<SchemaUI> results) {
                TransformationUI t = _transformationsCombo.getValue();
                Boolean searchSource = true;
                Boolean searchDest = true;
                for(SchemaUI s : results) {
                    if(s.getShortDesignation().equals(t.getSrcFormat()) && searchSource)
                        for(SchemaVersionUI v : s.getSchemaVersions())
                            if(v.getXsdLink().equals(t.getSourceSchema())) {
                                _schemasDetails.populateDetails(s,v, true);
                                searchSource = false;
                            }
                    if(s.getShortDesignation().equals(t.getDestFormat()) && searchDest)
                        for(SchemaVersionUI v : s.getSchemaVersions())
                            if(v.getXsdLink().equals(t.getDestSchema())) {
                                _schemasDetails.populateDetails(s,v, false);
                                searchDest = false;
                            }
                    if(!searchSource && !searchDest)
                        break;
                }
                _schemasDetails.unmask();
            }
        };
        TransformationsServiceAsync service = (TransformationsServiceAsync) Registry.get(HarvesterUI.TRANSFORMATIONS_SERVICE);
        _schemasDetails.mask("Loading...");
        service.getAllMetadataSchemas(callback);*/
    }
}
