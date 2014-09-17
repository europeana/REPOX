package harvesterUI.client.util;

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import harvesterUI.shared.externalServices.ExternalServiceUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 15-12-2011
 * Time: 11:20
 */
public class FieldSetWithExternalService extends FieldSet {

    protected ExternalServiceUI externalServiceUI;
    protected boolean isNewFieldSet;

    public FieldSetWithExternalService(){
        super();
        isNewFieldSet = true;
    }

    public ExternalServiceUI getExternalServiceUI(){
        return externalServiceUI;
    }

    public void setExternalServiceUI(ExternalServiceUI externalServiceUI){
        this.externalServiceUI = externalServiceUI;
    }

    public void setIsNewFieldSet(boolean value){
        isNewFieldSet = value;
    }

    public boolean isNewFieldSet(){
        return isNewFieldSet;
    }
}
