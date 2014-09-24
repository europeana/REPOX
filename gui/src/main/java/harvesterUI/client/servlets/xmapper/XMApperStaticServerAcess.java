package harvesterUI.client.servlets.xmapper;

import com.google.gwt.core.client.GWT;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 20-12-2012
 * Time: 15:49
 */
public class XMApperStaticServerAcess {

    private static final XMApperServiceAsync _service = GWT.create(XMApperService.class);

    public static XMApperServiceAsync getService() {
        return _service;
    }

}
