package harvesterUI.client.panels.mdr.xmapper.usecase;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import harvesterUI.client.panels.mdr.forms.xmapper.SaveExistingMapDialog;
import harvesterUI.client.panels.mdr.forms.xmapper.SaveNewMapDialog;
import harvesterUI.client.panels.mdr.xmapper.MDRMappingApplicationManager;
import pt.ist.mdr.mapping.ui.svg.client.ApplicationConfig;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 20-12-2012
 * Time: 17:29
 */
public class SaveMappingCtrl extends Controller {



    public SaveMappingCtrl() {
        registerEventTypes(ApplicationConfig.SaveMapping);
    }

    @Override
    public void handleEvent(AppEvent event) {
        MDRMappingApplicationManager manager = event.getData();

        //System.out.println("DEBUG - is new Map? "+manager.isNewMap());

        if(manager.isNewMap())
            new SaveNewMapDialog(manager).showAndCenter();
        else
            new SaveExistingMapDialog(manager).showAndCenter();
    }

}
