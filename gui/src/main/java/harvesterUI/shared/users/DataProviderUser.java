package harvesterUI.shared.users;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 13:34
 */
public class DataProviderUser extends User {

    private List<String> allowedDataProviderIds;

    public DataProviderUser() {}

    public DataProviderUser(String userName, String password, String role, String mail, List<String> allowedDataProviderIds) {
        super(userName, password, role, mail);
        setAllowedDataProviderIds(allowedDataProviderIds);
    }

//    public void setPassword(String password){set("password", password);}
//    public String getPassword(){return (String) get("password");}

    public List<String> getAllowedDataProviderIds() {
        if(allowedDataProviderIds == null)
            allowedDataProviderIds = new ArrayList<String>();
        return allowedDataProviderIds;
    }

    public boolean allowsDP(String dataProviderId){
        for(String currentDataProviderId : allowedDataProviderIds){
            if(currentDataProviderId.equals(dataProviderId))
                return true;
        }
        return false;
    }

    public void setAllowedDataProviderIds(List<String> allowedDataProviderIds) {
        this.allowedDataProviderIds = allowedDataProviderIds;
    }
}
