package harvesterUI.client.servlets.xmapper;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;
import pt.ist.mdr.mapping.ui.client.model.MappingScriptProxy;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 02-10-2012
 * Time: 16:26
 */
@RemoteServiceRelativePath("xmapperServices")
public interface XMApperService extends RemoteService {

    //Open empty map for 2 schemas
    MappingScriptProxy openUserEmptyMap(String source, String dest);

    //Save Mappings on the server
    public ResponseState saveMapping(MappingScriptProxy mappings, TransformationUI transformationUI, String oldTransId);

    //Generate Script (XSL)
    public Integer generateScript(String uri);

    //Open and existing map from server
    MappingScriptProxy openExistingMap(TransformationUI transformationUI);

    //Tests
    MappingScriptProxy openTestEmptyMap() throws ServerSideException;

}
