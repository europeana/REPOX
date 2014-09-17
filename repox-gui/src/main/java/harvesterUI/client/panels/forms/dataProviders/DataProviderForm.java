package harvesterUI.client.panels.forms.dataProviders;

import com.extjs.gxt.ui.client.Registry;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.dataManagement.DPServiceAsync;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.DataProviderUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 14-03-2011
 * Time: 14:56
 */
public abstract class DataProviderForm extends FormDialog {

    protected DPServiceAsync service;
    protected boolean edit = false;
    protected DefaultFormPanel dataProviderFormPanel;

    public DataProviderForm() {
        super(0.6,0.5);
        service = (DPServiceAsync) Registry.get(HarvesterUI.DP_SERVICE);

        dataProviderFormPanel = new DefaultFormPanel();
        dataProviderFormPanel.setHeaderVisible(false);
        dataProviderFormPanel.setLayout(new EditableFormLayout(150));
    }

    public void setEdit(boolean update) {edit = update;}

    public abstract void setEditMode(DataProviderUI dataProviderUI);
    public abstract void resetValues(Object dp);
}
