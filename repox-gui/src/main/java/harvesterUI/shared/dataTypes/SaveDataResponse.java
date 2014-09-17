package harvesterUI.shared.dataTypes;

import com.google.gwt.user.client.rpc.IsSerializable;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 11-04-2011
 * Time: 13:54
 */
public class SaveDataResponse implements IsSerializable{

    private ResponseState responseState;
    private int page;

    public SaveDataResponse() {}

    public void setResponseState(ResponseState responseState) {
        this.responseState = responseState;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ResponseState getResponseState() {
        return responseState;
    }
}
