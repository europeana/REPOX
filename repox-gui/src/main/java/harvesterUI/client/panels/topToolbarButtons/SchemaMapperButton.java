package harvesterUI.client.panels.topToolbarButtons;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.util.WidgetWithRole;
import harvesterUI.shared.users.UserRole;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 9:55
 */
public class SchemaMapperButton extends WidgetWithRole{

    public SchemaMapperButton(ToolBar toolBar) {
        if(drawWidget){
            Button schemaMapper = new Button(HarvesterUI.CONSTANTS.schemaMapper());
            schemaMapper.setId("SCHEMA_MAPPER_BUTTON");
            schemaMapper.setIcon(HarvesterUI.ICONS.schema_mapper_icon());
            schemaMapper.addSelectionListener(new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent me) {
                    Dispatcher.forwardEvent(AppEvents.ViewMappingsPanel);
                }
            });
            toolBar.add(schemaMapper);
        }
    }

    public void checkRole(){
        drawWidget = HarvesterUI.UTIL_MANAGER.getLoggedUserRole() != UserRole.ANONYMOUS &&
                HarvesterUI.UTIL_MANAGER.getLoggedUserRole() != UserRole.DATA_PROVIDER;
    }
}
