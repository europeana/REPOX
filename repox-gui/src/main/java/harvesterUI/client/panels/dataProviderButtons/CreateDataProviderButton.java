package harvesterUI.client.panels.dataProviderButtons;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.WidgetWithRole;
import harvesterUI.shared.ProjectType;
import harvesterUI.shared.users.UserRole;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 10:08
 */
public class CreateDataProviderButton extends WidgetWithRole{

    public CreateDataProviderButton(ToolBar toolBar) {
        if(drawWidget){
            Button createMainDataButton;
            if(HarvesterUI.getProjectType() == ProjectType.EUROPEANA){
                createMainDataButton = new Button(HarvesterUI.CONSTANTS.createAggregator());
                createMainDataButton.setIcon(HarvesterUI.ICONS.add());
                createMainDataButton.setId("firstToolBarButton");
                createMainDataButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        Dispatcher.get().dispatch(AppEvents.ViewAggregatorForm);
                    }
                });
                toolBar.add(createMainDataButton);

                addExportDataButton(toolBar);

                toolBar.add(new SeparatorToolItem());
            }else{
                createMainDataButton = new Button(HarvesterUI.CONSTANTS.createDataProvider());
                createMainDataButton.setIcon(HarvesterUI.ICONS.add());
                createMainDataButton.setId("firstToolBarButton");
                createMainDataButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        Dispatcher.get().dispatch(AppEvents.ViewDataProviderForm);
                    }
                });
                Button importDataProvider = new Button(HarvesterUI.CONSTANTS.importDataProviders());
                importDataProvider.setIcon(HarvesterUI.ICONS.dp_import_icon());
                importDataProvider.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        Dispatcher.get().dispatch(AppEvents.ViewDPImportForm);
                    }
                });

                toolBar.add(createMainDataButton);
                toolBar.add(importDataProvider);

                addExportDataButton(toolBar);

                toolBar.add(new SeparatorToolItem());
            }
        }
    }

    private void addExportDataButton(ToolBar toolBar){
        if(HarvesterUI.UTIL_MANAGER.getLoggedUserRole() == UserRole.ADMIN){
            Button exportAllDataFile = new Button("Export All");
            exportAllDataFile.setIcon(HarvesterUI.ICONS.export_now_icon());
            exportAllDataFile.addSelectionListener(new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    UtilManager.exportDataFile();
                }
            });
            toolBar.add(exportAllDataFile);
        }
    }

    public void checkRole(){
        switch (HarvesterUI.UTIL_MANAGER.getLoggedUserRole()){
            case ADMIN : drawWidget = true;
                break;
            case NORMAL: drawWidget = true;
                break;
            case DATA_PROVIDER: drawWidget = true;
                break;
            default: drawWidget = false;
                break;
        }
    }
}
