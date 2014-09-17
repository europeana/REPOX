package harvesterUI.client.panels.mdr.forms;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX Project.
 * User: Edmundo
 * Date: 10-01-2012
 * Time: 18:25
 */
public class NewSchemaVersionFieldSet extends FieldSet {

    private int fieldCount = 1;

    public NewSchemaVersionFieldSet() {
        setHeading("Schema Versions");
    }

    public void addNewField(boolean solo){
        NewSchemaVersionField NewSchemaVersionField = new NewSchemaVersionField(this,fieldCount);
        if(solo)
            NewSchemaVersionField.hideRemoveButton();
        add(NewSchemaVersionField);
        layout();
        fieldCount++;
    }

    public void addNewField(SchemaVersionUI schemaVersionUI, boolean solo){
        NewSchemaVersionField field = new NewSchemaVersionField(this,schemaVersionUI,fieldCount);
        if(solo)
            field.hideRemoveButton();
        if(schemaVersionUI.getMdrDataStatistics().getNumberTimesUsedInDataSets() > 0 ||
                schemaVersionUI.getMdrDataStatistics().getNumberTimesUsedInTransformations() > 0)
            field.setBeingUsed(true);
        add(field);
        layout();
        fieldCount++;
    }

    public List<SchemaVersionUI> getAllSchemaVersions(){
        List<SchemaVersionUI> schemaVersionUIList = new ArrayList<SchemaVersionUI>();
        for(Component component : getItems()){
            if(component instanceof NewSchemaVersionField){
                NewSchemaVersionField schemaVersionField = (NewSchemaVersionField)component;
                String xsdLink = schemaVersionField.getXsdLink();
                double version = schemaVersionField.getVersion().doubleValue();
                SchemaVersionUI schemaVersionUI = new SchemaVersionUI(version,xsdLink,null);
                schemaVersionUIList.add(schemaVersionUI);
            }
        }
        return schemaVersionUIList;
    }

    public void resize() {
        for(Component component : getItems()){
            if(component instanceof NewSchemaVersionField){
                ((NewSchemaVersionField) component).layout(true);
            }
        }
        layout(true);
    }

    public void edit(SchemaUI schemaUI){
        List<SchemaVersionUI> versions = schemaUI.getSchemaVersions();
        for(SchemaVersionUI schemaVersionUI : versions){
            addNewField(schemaVersionUI,versions.size() == 1);
        }
    }

    public void reset(){
        fieldCount = 1;
        for(int i=getItems().size()-1 ; i >=0 ; i--){
            if(getItem(i) instanceof NewSchemaVersionField){
                getItem(i).removeFromParent();
            }
        }
    }

    public void subtractFieldCount() {
        fieldCount--;
        reevaluateChildren();
    }

    private void reevaluateChildren(){
        List<Component> children = getItems();
        List<NewSchemaVersionField> versions = new ArrayList<NewSchemaVersionField>();
        for(Component c : children) {
            if(c instanceof NewSchemaVersionField)
                versions.add((NewSchemaVersionField)c);
        }

        /*int i = 1; //Re-order the headings..
        for(NewSchemaVersionField v : versions) {
            v.setHeading(i);
            i++;
        }*/

        if(versions.size() == 1)
            versions.get(0).hideRemoveButton();
    }
}
