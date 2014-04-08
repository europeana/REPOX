package harvesterUI.shared.users;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 24-02-2012
 * Time: 7:51
 */
public enum UserRole implements IsSerializable {
    ADMIN,
    NORMAL,
    HARVESTER,
    DATA_PROVIDER,
    ANONYMOUS
}
