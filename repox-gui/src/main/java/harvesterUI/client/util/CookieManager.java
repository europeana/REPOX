package harvesterUI.client.util;

import com.google.gwt.user.client.Cookies;

import java.util.Date;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 31-05-2012
 * Time: 11:50
 */
public class CookieManager {

    private long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.

    public static String SID = "sid";
    public static String LOGGED_USERNAME = "loggedUsrName";
    public static String LOGGED_USER_ROLE = "userRole";

    public static String TEMP_LANGUAGE = "tempLang";
    public static String TEMP_USER = "tempUsername";

    public static String LOGIN_DIALOG_USER = "enteredUsername";
    public static String LOGIN_DIALOG_PASS = "enteredPassword";

    public static String ROWS_PER_PAGE = "rowsPerPage";

    public static String REPOX_LANGUAGE = "repoxUILanguage";

    public CookieManager() {
    }

    public void saveLanguageCookie(String language){
        Date expires = new Date(System.currentTimeMillis() + DURATION);
        Cookies.setCookie(REPOX_LANGUAGE,language, expires, null, "/", false);
    }

    public void saveLoginForTwoWeeks(String sid, String username, String role){
        Date expires = new Date(System.currentTimeMillis() + DURATION);
        Cookies.setCookie(SID, sid, expires, null, "/", false);
        Cookies.setCookie(LOGGED_USERNAME, username, expires, null, "/", false);
        Cookies.setCookie(LOGGED_USER_ROLE, role, expires, null, "/", false);
    }

    public void saveLoginForBrowserSessionOnly(String sid, String username, String role){
        Cookies.setCookie(SID, sid);
        Cookies.setCookie(LOGGED_USERNAME, username);
        Cookies.setCookie(LOGGED_USER_ROLE, role);
    }

    public void saveLoginDataForLoginDialog(String username, String password){
        Date expires = new Date(System.currentTimeMillis() + DURATION);
        Cookies.setCookie(LOGIN_DIALOG_USER + UtilManager.getServerUrl(),username, expires, null, "/", false);
        Cookies.setCookie(LOGIN_DIALOG_PASS + UtilManager.getServerUrl(),password, expires, null, "/", false);
    }

    public void saveTempLanguageData(String language, String username){
        Cookies.setCookie(TEMP_LANGUAGE,language);
        Cookies.setCookie(TEMP_USER,username);
    }

    public void saveRowsPerPageData(String perPageNumber){
        Date expires = new Date(System.currentTimeMillis() + DURATION);
        Cookies.setCookie(ROWS_PER_PAGE, perPageNumber, expires, null, "/", false);
    }

    public void removeAllLoginCookies(){
        Cookies.removeCookie(SID,"/");
        Cookies.removeCookie(LOGGED_USER_ROLE,"/");
        Cookies.removeCookie(LOGGED_USERNAME,"/");
        // session cookies
        Cookies.removeCookie(SID);
        Cookies.removeCookie(LOGGED_USER_ROLE);
        Cookies.removeCookie(LOGGED_USERNAME);

        Cookies.removeCookie(TEMP_LANGUAGE);
        Cookies.removeCookie(TEMP_USER);
    }
}
