package harvesterUI.client.panels.dataSourceView;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Image;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
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
public class DataSetOAIViewInfo extends DataSetDefaultViewInfo {

    private FormData formData;
    private FieldSet infoSetFolder;

    private LabelField oaiUrl, oaiSet;
    private LayoutContainer typeContainer;
    private LabelToolItem type;
    private Image oaiButton;
    private DataSourceUI dataSourceUI;
    private DefaultFormPanel defaultFormPanel;


    public DataSetOAIViewInfo(DefaultFormPanel defaultFormPanel, FormData formData) {
        this.defaultFormPanel = defaultFormPanel;
        this.formData = formData;
    }

    private void createFolderInfoSetFolder() {
        infoSetFolder = new FieldSet();
        infoSetFolder.setAutoHeight(true);
        infoSetFolder.setAutoWidth(true);
        infoSetFolder.setHeading(HarvesterUI.CONSTANTS.info());
        infoSetFolder.setLayout(new DefaultFormLayout(UtilManager.DEFAULT_DATASET_VIEWINFO_LABEL_WIDTH));

        type = new LabelToolItem();
        typeContainer = new LayoutContainer();
        HBoxLayout typeContainerLayout = new HBoxLayout();
        typeContainerLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        typeContainer.setLayout(typeContainerLayout);
        LabelToolItem label = new LabelToolItem(HarvesterUI.CONSTANTS.type());
        label.setWidth(UtilManager.SPECIAL_DATASET_VIEWINFO_LABEL_WIDTH);
        label.addStyleName("defaultFormFieldLabel");
        typeContainer.add(label, new HBoxLayoutData(new Margins(0, 2, 4, 0)));
        typeContainer.add(type, new HBoxLayoutData(new Margins(0, 5, 4, 5)));
        oaiButton = UtilManager.createOAIImage();
        typeContainer.add(oaiButton,new HBoxLayoutData(new Margins(0, 5, 4, 0)));
        infoSetFolder.add(typeContainer,formData);

        oaiUrl = new LabelField();
        oaiUrl.setFieldLabel(HarvesterUI.CONSTANTS.oaiUrl());
        infoSetFolder.add(oaiUrl,formData);

        oaiSet = new LabelField();
        oaiSet.setFieldLabel(HarvesterUI.CONSTANTS.oaiSet());
        infoSetFolder.add(oaiSet,formData);

        addDefaultFields(infoSetFolder,formData);
    }

    public DefaultFormPanel showInfo(DataSourceUI ds) {
        dataSourceUI = ds;
        String ingest = dataSourceUI.getIngest();
        String delimType = "[ ]+";
        String[] tokensType = ingest.split(delimType);
        String typeOfDS = tokensType[0];

        createFolderInfoSetFolder();

        type.setLabel(typeOfDS);
        oaiButton.addMouseDownHandler(new MouseDownHandler(){
            public void onMouseDown(MouseDownEvent event) {
                Dispatcher.get().dispatch(AppEvents.ViewOAISpecificSet,dataSourceUI);
            }
        });
        oaiUrl.setValue(dataSourceUI.getOaiSource());
        oaiSet.setValue(dataSourceUI.getOaiSet());

        loadDefaultFields(dataSourceUI,infoSetFolder,formData);

        defaultFormPanel.add(infoSetFolder);
        return defaultFormPanel;
    }
}
