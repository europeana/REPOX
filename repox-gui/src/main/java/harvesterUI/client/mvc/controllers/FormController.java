package harvesterUI.client.mvc.controllers;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 03-02-2011
 * Time: 18:27
 */
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.FormView;

public class FormController extends Controller {

    private FormView formView;

    public FormController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.ViewAggregatorForm);
        registerEventTypes(AppEvents.ViewDataProviderForm);
        registerEventTypes(AppEvents.ViewDataSourceForm);
        registerEventTypes(AppEvents.ViewDPImportForm);
        registerEventTypes(AppEvents.HideDataSourceForm);
        registerEventTypes(AppEvents.HideDataSourceForm);
        registerEventTypes(AppEvents.ReloadTransformations);
        registerEventTypes(AppEvents.ReloadTags);
        registerEventTypes(AppEvents.ReloadSchemas);
        registerEventTypes(AppEvents.ViewAddMappingDialog);
        registerEventTypes(AppEvents.ViewAddSchemaDialog);
        registerEventTypes(AppEvents.CompareDataSets);
    }

    @Override
    public void initialize() {
        super.initialize();
        formView = new FormView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            forwardToView(formView, event);
        }else if (type == AppEvents.ViewAggregatorForm
                || type == AppEvents.ViewDataProviderForm
                || type == AppEvents.ViewDataSourceForm
                || type == AppEvents.HideDataSourceForm
                || type == AppEvents.ViewDPImportForm
                || type == AppEvents.ReloadTransformations
                || type == AppEvents.ReloadSchemas
                || type == AppEvents.ReloadTags
                || type == AppEvents.ViewAddSchemaDialog
                || type == AppEvents.CompareDataSets
                || type == AppEvents.ViewAddMappingDialog){
            forwardToView(formView,event);
        }
    }
}
