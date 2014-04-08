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
public class DataSetYaddaViewInfo extends DataSetDefaultViewInfo {

    private FormData formData;
    private FieldSet infoSetFolder;

    private LabelField type,yaddaCollection, exportPath;
    private DefaultFormPanel defaultFormPanel;


    public DataSetYaddaViewInfo(DefaultFormPanel defaultFormPanel, FormData formData) {
        this.defaultFormPanel = defaultFormPanel;
        this.formData = formData;
    }

    private void createYaddaInfoSet() {
        infoSetFolder = new FieldSet();
        infoSetFolder.setAutoHeight(true);
        infoSetFolder.setAutoWidth(true);
        infoSetFolder.setHeading(HarvesterUI.CONSTANTS.info());
        infoSetFolder.setLayout(new DefaultFormLayout(UtilManager.DEFAULT_DATASET_VIEWINFO_LABEL_WIDTH));

        type = new LabelField();
        type.setFieldLabel(HarvesterUI.CONSTANTS.type());
        infoSetFolder.add(type,formData);

        yaddaCollection = new LabelField();
        yaddaCollection.setFieldLabel("Yadda Collection");
        infoSetFolder.add(yaddaCollection,formData);

        exportPath = new LabelField();
        exportPath.setFieldLabel(HarvesterUI.CONSTANTS.exportPath());
        infoSetFolder.add(exportPath, formData);

        addDefaultFields(infoSetFolder,formData);
    }

    public DefaultFormPanel showInfo(DataSourceUI dataSourceUI) {
        String ingest = dataSourceUI.getIngest();
        String delimType = "[ ]+";
        String[] tokensType = ingest.split(delimType);
        String typeOfDS = tokensType[0];

        createYaddaInfoSet();

        type.setValue(typeOfDS);
        yaddaCollection.setValue(dataSourceUI.getYaddaCollection());
        exportPath.setValue(dataSourceUI.getExportDirectory());

        loadDefaultFields(dataSourceUI,infoSetFolder,formData);

        defaultFormPanel.add(infoSetFolder);
        return defaultFormPanel;
    }
}
