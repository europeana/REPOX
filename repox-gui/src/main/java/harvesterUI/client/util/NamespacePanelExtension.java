package harvesterUI.client.util;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import harvesterUI.client.HarvesterUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 09-01-2012
 * Time: 17:39
 */
public class NamespacePanelExtension {

    public List<TextField<String>> namespacesList = new ArrayList<TextField<String>>();

    private LayoutContainer addNamespaceContainer;
    private FieldSetWithClickOption associatedFormPanel;
    private FormData formData;

    public NamespacePanelExtension(FieldSetWithClickOption formPanel, FormData formData) {
        associatedFormPanel = formPanel;
        this.formData = formData;
        addNamespaceContainer = new LayoutContainer();
        HBoxLayout addNamespaceLayout = new HBoxLayout();
        addNamespaceLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        addNamespaceContainer.setLayout(addNamespaceLayout);

        Button addNamespace = new Button(HarvesterUI.CONSTANTS.addNamespace(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if(namespacesList.get(namespacesList.size()-2).getValue() != null)
                    createNewNamespace();
            }
        });

        addNamespaceContainer.add(addNamespace,new HBoxLayoutData(new Margins(0,0,5,155)));
    }

    public List<TextField<String>> getNamespacesList() {
        return namespacesList;
    }

    private void createNamespaceCombo(){
        TextField<String> namespacePrefix = new TextField<String>();
        namespacePrefix.setFieldLabel(HarvesterUI.CONSTANTS.namespacePrefix()+ " " + ((namespacesList.size()/2)+1));
        namespacePrefix.setId("nmspacePref" + ((namespacesList.size()/2)+1));

        TextField<String> namespaceURI = new TextField<String>();
        namespaceURI.setFieldLabel(HarvesterUI.CONSTANTS.namespaceUri() +" " + ((namespacesList.size()/2)+1));
        namespaceURI.setId("nmspaceURI" + ((namespacesList.size()/2)+1));

        associatedFormPanel.add(namespacePrefix,formData);
        associatedFormPanel.add(namespaceURI,formData);

        namespacesList.add(namespacePrefix);
        namespacesList.add(namespaceURI);
    }

    public void createNewNamespace(){
        createNamespaceCombo();
        associatedFormPanel.add(addNamespaceContainer);
        associatedFormPanel.layout();
    }

    public void editNamespaces(List<String> namespaces){
        clearNamespacesList(-1);
        for(int i=0; i<namespaces.size(); i+=2){
            TextField<String> namespacePrefix = new TextField<String>();
            namespacePrefix.setFieldLabel(HarvesterUI.CONSTANTS.namespacePrefix() + " " + ((namespacesList.size()/2)+1));
            namespacePrefix.setId("nmspacePref" + ((namespacesList.size()/2)+1));
            namespacePrefix.setValue(namespaces.get(i));

            TextField<String> namespaceURI = new TextField<String>();
            namespaceURI.setFieldLabel(HarvesterUI.CONSTANTS.namespaceUri()+ " " + ((namespacesList.size()/2)+1));
            namespaceURI.setId("nmspaceURI" + ((namespacesList.size()/2)+1));
            namespaceURI.setValue(namespaces.get(i+1));

            associatedFormPanel.add(namespacePrefix,formData);
            associatedFormPanel.add(namespaceURI,formData);

            namespacesList.add(namespacePrefix);
            namespacesList.add(namespaceURI);
        }

        associatedFormPanel.add(addNamespaceContainer);

        associatedFormPanel.layout();
    }

    public void clearNamespacesList(int limit){
        for(int i =namespacesList.size()-1; i>limit; i--){
            namespacesList.get(i).removeFromParent();
            namespacesList.remove(i);
        }
        if(!namespacesList.isEmpty())
            resetFirstNamespaceCombo();
    }

    private void resetFirstNamespaceCombo(){
        namespacesList.get(0).clear();
        namespacesList.get(1).clear();
    }

    public List<String> getFinalNamespacesList(){
        List<String> namespaces = new ArrayList<String>();
        for(int i=0; i<namespacesList.size(); i+=2) {
            if(namespacesList.get(i).getValue() != null) {
                namespaces.add(namespacesList.get(i).getValue().trim());
                namespaces.add(namespacesList.get(i+1).getValue().trim());
            }
        }
        return namespaces;
    }
}
