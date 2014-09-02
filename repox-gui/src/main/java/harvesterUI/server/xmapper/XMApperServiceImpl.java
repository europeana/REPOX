package harvesterUI.server.xmapper;

import harvesterUI.client.servlets.xmapper.XMApperService;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;
import pt.ist.mdr.mapping.ui.client.model.MappingScriptProxy;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
//import pt.ist.mdr.mapping.console.server;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 02-10-2012
 * Time: 17:01
 */
public class XMApperServiceImpl extends RemoteServiceServlet implements XMApperService{

    //Tests
    @Override
    public MappingScriptProxy openTestEmptyMap() throws ServerSideException {
       return XMApperServicesServer.getTestEmptyMappingModel();
    }

    //Server Mapping-related Services
    @Override
    public MappingScriptProxy openUserEmptyMap(String source, String dest) {
        return XMApperServicesServer.getEmptyUserMappingModel(source, dest);
    }

    @Override
    public Integer generateScript(String uri) {
        return XMApperServicesServer.generateScript(uri);
    }

    @Override
      public ResponseState saveMapping(MappingScriptProxy mappings, TransformationUI transformationUI, String oldTransId) {
        return XMApperServicesServer.saveMapping(mappings, transformationUI, oldTransId);
    }

    @Override
    public MappingScriptProxy openExistingMap(TransformationUI transformationUI) {
        return XMApperServicesServer.getExistingMappingModel(transformationUI);
    }

}
