package harvesterUI.client.mvc.controllers;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.mvc.views.SchemaMapperView;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 20:32
 */
public class SchemaMapperController extends Controller{

    private SchemaMapperView schemaMapperView;

    public SchemaMapperController() {
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.ViewSchemasPanel);
        registerEventTypes(AppEvents.ViewMappingsPanel);
        registerEventTypes(AppEvents.ViewXMApperPanel);
        registerEventTypes(AppEvents.ReloadTransformations);
        registerEventTypes(AppEvents.ReloadSchemas);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == AppEvents.Init) {
            onInit(event);
        } else if (
                type == AppEvents.ViewSchemasPanel ||
                type == AppEvents.ViewMappingsPanel ||
                type == AppEvents.ViewXMApperPanel ||
                type == AppEvents.ReloadSchemas ||
                type == AppEvents.ReloadTransformations) {
            forwardToView(schemaMapperView, event);
        }
    }

    public void initialize() {
        schemaMapperView = new SchemaMapperView(this);
    }

    private void onInit(AppEvent event) {
        forwardToView(schemaMapperView, event);
    }
}
