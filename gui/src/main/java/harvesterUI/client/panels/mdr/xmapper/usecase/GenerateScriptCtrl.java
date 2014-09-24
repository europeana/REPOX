package harvesterUI.client.panels.mdr.xmapper.usecase;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.servlets.xmapper.XMApperStaticServerAcess;
import pt.ist.mdr.gwt.client.widgets.utils.ExceptionDialog;
import pt.ist.mdr.mapping.ui.svg.client.ApplicationConfig;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 20-12-2012
 * Time: 13:12
 */
public class GenerateScriptCtrl extends Controller {

    public GenerateScriptCtrl() {
        registerEventTypes(ApplicationConfig.GenerateScript);
    }

    //Successfully
    @Override
    public void handleEvent(AppEvent event) {

        //MessageBox.alert("Script Generation", "This feature is temporarily unavailable!", null);

        // if(false) {
        final String modelID = event.getData();
        if(modelID == null) return;

        //iish need desktop page?? fedeu neh??? maybe not... TODO
//            final DesktopPage page = ApplicationManager.getCurrentPage();
//            page.mask("generating script...");
        //TODO build service
        XMApperStaticServerAcess.getService().generateScript(modelID, new AsyncCallback<Integer>() {

            @Override
            public void onSuccess(final Integer result) {
//                    page.unmask();
                /* Dispatcher.get().dispatch(
                pt.ist.mdr.system.console.client.ApplicationConfig.ResourceChanged,
                resource);*/
                MessageBox.info("Script Generation", "Script was successfully build!" +
                        "\nThe Answer to the Ultimate Question of Life, the Universe, and Everything is " + result.toString()
                        + "\n ...Probably."
                        , null);
                /*MessageBox.confirm(
        "Script Generation",
        "Script was successfully build!",// Do you wish to open it?",
        new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
                *//*if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
                                        Dispatcher.get().dispatch(
                                                pt.ist.mdr.system.console.client.ApplicationConfig.ViewResource,
                                                resource);
                                    }*//*
                                }
                            });*/
            }

            @Override
            public void onFailure(Throwable caught) {
//                    page.unmask();
                new ExceptionDialog(
                        "Script Generation",
                        "Exception while generating script", caught).show();
            }
        });
        // }
    }
}
