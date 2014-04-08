package harvesterUI.shared.users;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 13:34
 */
public class User extends BaseModel implements IsSerializable {

    public User() {}

    public User(String userName, String password, String role, String mail) {
        set("userName",userName);
        set("password",password);
        set("role", role);
        set("mail",mail);
    }

    public void setPassword(String password){set("password", password);}
    public String getPassword(){return (String) get("password");}

    public void setMail(String mail){set("mail", mail);}
    public String getMail(){return (String) get("mail");}

    public void setUserName(String userName){set("userName", userName);}
    public String getUserName(){return (String) get("userName");}

    public void setRole(String role){set("role", role);}
    public String getRole(){return (String) get("role");}
}
