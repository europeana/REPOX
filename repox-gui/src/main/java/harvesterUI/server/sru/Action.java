/* Action.java - created on 19 de Abr de 2013, Copyright (c) 2011 The European Library, all rights reserved */
package harvesterUI.server.sru;

/**
 * SRU record update actions
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 19 de Abr de 2013
 */
public enum Action {
    /** Action CREATE */
    CREATE("info:srw/action/1/create"),
    /** Action REPLACE */
    REPLACE("info:srw/action/1/replace"),
    /** Action DELETE */
    DELETE("info:srw/action/1/delete");
    
    final String uri;
    
    /**
     * Creates a new instance of this class.
     */
    private Action(String uri) {
        this.uri=uri;
    }
    
    public static Action fromUri(String uri) {
       for(Action a:Action.values()) {
           if(a.uri.equals(uri))
               return a;
       }
       return null;
    }
    
    
}
