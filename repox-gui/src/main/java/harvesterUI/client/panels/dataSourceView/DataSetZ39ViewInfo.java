package harvesterUI.client.panels.dataSourceView;

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormLayout;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 22-03-2011
 * Time: 15:46
 */
public class DataSetZ39ViewInfo extends DataSetDefaultViewInfo{

    private FormData formData;
    private FieldSet infoSetFolder;
    private DefaultFormPanel defaultFormPanel;

    private LabelField type, address, port, database, charset, recordSyntax, harvestMethod;

    public DataSetZ39ViewInfo(DefaultFormPanel defaultFormPanel, FormData formData) {
        this.defaultFormPanel = defaultFormPanel;
        this.formData = formData;
    }

    private void createFolderinfoSetFolder() {
        infoSetFolder = new FieldSet();
        infoSetFolder.setAutoHeight(true);
        infoSetFolder.setAutoWidth(true);
        infoSetFolder.setHeading(HarvesterUI.CONSTANTS.info());
        infoSetFolder.setLayout(new DefaultFormLayout(UtilManager.DEFAULT_DATASET_VIEWINFO_LABEL_WIDTH));

        type = new LabelField();
        type.setFieldLabel(HarvesterUI.CONSTANTS.type());
        infoSetFolder.add(type,formData);

        address = new LabelField();
        address.setFieldLabel(HarvesterUI.CONSTANTS.address());
        infoSetFolder.add(address,formData);

        port = new LabelField();
        port.setFieldLabel(HarvesterUI.CONSTANTS.port());
        infoSetFolder.add(port,formData);

        database = new LabelField();
        database.setFieldLabel(HarvesterUI.CONSTANTS.database());
        infoSetFolder.add(database,formData);

        charset = new LabelField();
        charset.setFieldLabel(HarvesterUI.CONSTANTS.characterEncoding());
        infoSetFolder.add(charset,formData);

        recordSyntax = new LabelField();
        recordSyntax.setFieldLabel(HarvesterUI.CONSTANTS.recordSyntax());
        infoSetFolder.add(recordSyntax,formData);

        harvestMethod = new LabelField();
        harvestMethod.setFieldLabel(HarvesterUI.CONSTANTS.harvestMethod());
        infoSetFolder.add(harvestMethod,formData);

        addDefaultFields(infoSetFolder,formData);
    }

    public DefaultFormPanel showInfo(final DataSourceUI dataSourceUI) {
        String ingest = dataSourceUI.getIngest();
        String delimType = "[ ]+";
        String[] tokensType = ingest.split(delimType);
        String typeOfDS = tokensType[0];

        createFolderinfoSetFolder();

        type.setValue(typeOfDS);
        address.setValue(dataSourceUI.getZ39Address());
        port.setValue(dataSourceUI.getZ39Port());
        database.setValue(dataSourceUI.getZ39Database());
        recordSyntax.setValue(dataSourceUI.getZ39RecordSyntax());
        harvestMethod.setValue(dataSourceUI.getZ39HarvestMethod());
        charset.setValue(dataSourceUI.getCharacterEncoding());

        loadDefaultFields(dataSourceUI,infoSetFolder,formData);

        defaultFormPanel.add(infoSetFolder);
        return defaultFormPanel;
    }
}
