package harvesterUI.client.servlets.xmapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;
import pt.ist.mdr.mapping.ui.client.model.MappingScriptProxy;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 02-10-2012
 * Time: 16:20
 */
public interface XMApperServiceAsync {

    //Open empty map for 2 schemas
    void openUserEmptyMap(String source, String dest, AsyncCallback<MappingScriptProxy> callback);

    //Save Mappings
    void saveMapping(MappingScriptProxy mappings, TransformationUI transformationUI, String oldTransId, AsyncCallback<ResponseState> callback);

    //Generate Script (XSL)
    void generateScript(String uri, AsyncCallback<Integer> callback);

    //Open and existing map from server
    void openTestEmptyMap(AsyncCallback<MappingScriptProxy> callback);

    //Tests
    void openExistingMap(TransformationUI transformationUI, AsyncCallback<MappingScriptProxy> callback);

}
