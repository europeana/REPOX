package harvesterUI.shared.dataTypes.admin;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 18-07-2012
 * Time: 16:54
 */
public class RepoxConfig implements IsSerializable{

    private String id;
    private String displayName;
    private String value;
    private String exampleText;

    public RepoxConfig() {}

    public RepoxConfig(String id, String displayName, String value, String exampleText) {
        this.id = id;
        this.displayName = displayName;
        this.value = value;
        this.exampleText = exampleText;
    }
}
