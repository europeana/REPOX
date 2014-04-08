package harvesterUI.client.panels.oaiTest;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.ProjectType;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 18-04-2011
 * Time: 16:49
 */
public class RestRecordOperationsPanel extends ContentPanel {
    private Frame resultFrame;
    private TextField<String> recordURN,dataSourceId,recordId;
    protected TextArea recordXML;
    private FieldSet oaiSet;
    protected String serverUrl;
    protected LabelToolItem iframeUrlLabel;
    protected DefaultFormPanel oaiPmhTestFormPanel;
    protected FormData formData;

    protected LabelToolItem operationsListLabel,recordFillLabel,recordsLabel,aggregatorsLabel,dataSourcesListLabel,statisticsLabel;

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setScrollMode(Style.Scroll.AUTO);

        addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent attachEvent) {
                doOAIAction("test");
            }
        });

        formData = new FormData("100%");

        // Debug mode check
        if(Window.Location.getHost().contains("127.0.0.1:8888"))
            serverUrl = "http://" + Window.Location.getHost() + "/";
        else
            serverUrl = UtilManager.getServerUrl();

        setLayout(new FitLayout());
        setHeading(HarvesterUI.CONSTANTS.restServices());
        setIcon(HarvesterUI.ICONS.web_services_icon());

        LayoutContainer main = new LayoutContainer();
        main.setLayout(new BorderLayout());
        main.setStyleName("repoxFormBackground");
        main.setScrollMode(Style.Scroll.AUTO);

        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.WEST, 340,300,350);
        data.setMargins(new Margins(5,5,5,5));

        LayoutContainer left = new LayoutContainer();
//        left.setStyleAttribute("paddingRight", "10px");
        FlowLayout layout = new FlowLayout();
        left.setLayout(layout);

        left.add(createParametersFieldSet());
        left.add(createCommonRetrievalOperationsSet());
        left.add(createOperationsFieldSet());
        main.add(left,data);

        BorderLayoutData rightData = new BorderLayoutData(Style.LayoutRegion.CENTER);
        rightData.setMargins(new Margins(5,5,5,5));
        main.add(createOAIFrameFieldSet(),rightData);

        add(main);
    }

    private FieldSet createParametersFieldSet() {
        FormLayout layout = new FormLayout();
        layout.setLabelWidth(100);
        FieldSet retrievalOperationsSet = new FieldSet();
        retrievalOperationsSet.setHeading(HarvesterUI.CONSTANTS.restOperations());
        retrievalOperationsSet.setAutoHeight(true);
        retrievalOperationsSet.setLayout(layout);

        operationsListLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                HarvesterUI.CONSTANTS.operationsList() + "</span>");
        operationsListLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                setIframeUrl("rest");
                clearAllStyles();
                operationsListLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        operationsListLabel.setStyleName("hyperlink_style_label_selected");
        retrievalOperationsSet.add(operationsListLabel,formData);

        if(HarvesterUI.getProjectType() == ProjectType.EUROPEANA){
            aggregatorsLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                    HarvesterUI.CONSTANTS.aggregatorsOperations() + "</span>");
            aggregatorsLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
                public void handleEvent(BoxComponentEvent be) {
                    setIframeUrl("rest/aggregators");
                    clearAllStyles();
                    aggregatorsLabel.setStyleName("hyperlink_style_label_selected");
                }
            });
            aggregatorsLabel.setStyleName("hyperlink_style_label");
            retrievalOperationsSet.add(aggregatorsLabel,formData);
        }

        dataSourcesListLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                HarvesterUI.CONSTANTS.dataProvidersOperations() + "</span>");
        dataSourcesListLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                setIframeUrl("rest/dataProviders");
                clearAllStyles();
                dataSourcesListLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        dataSourcesListLabel.setStyleName("hyperlink_style_label");
        retrievalOperationsSet.add(dataSourcesListLabel,formData);

        recordFillLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                HarvesterUI.CONSTANTS.dataSetOperations() + "</span>");
        recordFillLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                setIframeUrl("rest/dataSources");
                clearAllStyles();
                recordFillLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        recordFillLabel.setStyleName("hyperlink_style_label");
        retrievalOperationsSet.add(recordFillLabel,formData);

        recordsLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                HarvesterUI.CONSTANTS.recordsOperations() + "</span>");
        recordsLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                setIframeUrl("rest/records");
                clearAllStyles();
                recordsLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        recordsLabel.setStyleName("hyperlink_style_label");
        retrievalOperationsSet.add(recordsLabel,formData);

        statisticsLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                "Statistics" + "</span>");
        statisticsLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                setIframeUrl("rest/statistics");
                clearAllStyles();
                recordsLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        statisticsLabel.setStyleName("hyperlink_style_label");
        retrievalOperationsSet.add(statisticsLabel,formData);

        return retrievalOperationsSet;
    }

    private FieldSet createCommonRetrievalOperationsSet() {
        FieldSet commonRetrievalSet = new FieldSet();
        commonRetrievalSet.setHeading(HarvesterUI.CONSTANTS.commonRetrievalOperations());
        commonRetrievalSet.setAutoHeight(true);
        commonRetrievalSet.setLayout(new EditableFormLayout(110));

        recordURN = new TextField<String>();
        recordURN.setFieldLabel(HarvesterUI.CONSTANTS.recordUrn());
        recordURN.setId("restService_recordUrn");
        commonRetrievalSet.add(recordURN, formData);

        LabelToolItem recordFillLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                HarvesterUI.CONSTANTS.recordFillUrn() + "</span>");
        recordFillLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                if(recordURN.getValue() == null || recordURN.getValue().equals("")) {
                    HarvesterUI.UTIL_MANAGER.getInfoBox(HarvesterUI.CONSTANTS.commonRetrievalOperations(),HarvesterUI.CONSTANTS.pleaseInsertUrn());
                    return;
                }
                oaiPmhTestFormPanel.submit();
                setIframeUrl("rest/records/getRecord?recordId=" + recordURN.getValue());
            }
        });
        recordFillLabel.setStyleName("hyperlink_style_label");
        commonRetrievalSet.add(recordFillLabel,formData);

        if(HarvesterUI.getProjectType() == ProjectType.EUROPEANA){
            LabelToolItem aggregatorsListLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                    HarvesterUI.CONSTANTS.listAggregators() + "</span>");
            aggregatorsListLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
                public void handleEvent(BoxComponentEvent be) {
                    setIframeUrl("rest/aggregators/list");
                }
            });
            aggregatorsListLabel.setStyleName("hyperlink_style_label");
            commonRetrievalSet.add(aggregatorsListLabel,formData);
        }

        LabelToolItem listDataProvidersLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                HarvesterUI.CONSTANTS.listdataProviders() + "</span>");
        listDataProvidersLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                setIframeUrl("rest/dataProviders/list");
            }
        });
        listDataProvidersLabel.setStyleName("hyperlink_style_label");
        commonRetrievalSet.add(listDataProvidersLabel,formData);

        LabelToolItem listDataSourcesLabel = new LabelToolItem("<span style='color:blue" + "'>" +
                HarvesterUI.CONSTANTS.listDataSets() + "</span>");
        listDataSourcesLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                setIframeUrl("rest/dataSources/list");
            }
        });
        listDataSourcesLabel.setStyleName("hyperlink_style_label");
        commonRetrievalSet.add(listDataSourcesLabel,formData);

        return commonRetrievalSet;
    }

    private FieldSet createOperationsFieldSet() {
        FieldSet operationsSet = new FieldSet();
        operationsSet.setHeading(HarvesterUI.CONSTANTS.modificationOperations());
        operationsSet.setAutoHeight(true);
        operationsSet.setLayout(new EditableFormLayout(110));

        recordXML = new TextArea();
        recordXML.setFieldLabel(HarvesterUI.CONSTANTS.recordXML() + HarvesterUI.REQUIRED_STR);
        recordXML.setId("restServ_rcrdxml");
        recordXML.setAllowBlank(false);

        final SimpleComboBox<String> modificationOperationCombo = new SimpleComboBox<String>();
        modificationOperationCombo.setEditable(false);
        modificationOperationCombo.setFieldLabel(HarvesterUI.CONSTANTS.operation());
        modificationOperationCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
        modificationOperationCombo.add(HarvesterUI.CONSTANTS.selectOperation());
        modificationOperationCombo.add(HarvesterUI.CONSTANTS.saveRecord());
        modificationOperationCombo.add(HarvesterUI.CONSTANTS.deleteRecord());
        modificationOperationCombo.add(HarvesterUI.CONSTANTS.eraseRecord());
        modificationOperationCombo.setValue(modificationOperationCombo.getStore().getAt(0));
        operationsSet.add(modificationOperationCombo, formData);

        modificationOperationCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                if(se.getSelectedItem().getValue().equals(HarvesterUI.CONSTANTS.saveRecord())){
                    recordXML.show();
                    dataSourceId.show();
                }else{
                    recordXML.hide();
                    dataSourceId.hide();
                }
            }
        });

        dataSourceId = new TextField<String>();
        dataSourceId.setFieldLabel(HarvesterUI.CONSTANTS.dataSetId() + HarvesterUI.REQUIRED_STR);
        dataSourceId.setAllowBlank(false);
        dataSourceId.setId("restServ_dsid");
        operationsSet.add(dataSourceId, formData);

        recordId = new TextField<String>();
        recordId.setFieldLabel(HarvesterUI.CONSTANTS.recordId() + HarvesterUI.REQUIRED_STR);
        recordId.setId("restServ_rcrdid");
        recordId.setAllowBlank(false);
        operationsSet.add(recordId, formData);

        operationsSet.add(recordXML, formData);

        Button submitButton = new Button(HarvesterUI.CONSTANTS.submit(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                if(modificationOperationCombo.getSelection().get(0).getValue().equals(HarvesterUI.CONSTANTS.saveRecord())){
                    doOAIAction("/rest/records/saveRecord?recordId=" + recordId.getValue() + "&dataSourceId=" + dataSourceId.getValue() + "&recordString=" + recordXML.getValue());
                }else if(modificationOperationCombo.getSelection().get(0).getValue().equals(HarvesterUI.CONSTANTS.deleteRecord())){
                    doOAIAction("rest/records/deleteRecord?recordId=" + recordId.getValue());
                }else if(modificationOperationCombo.getSelection().get(0).getValue().equals(HarvesterUI.CONSTANTS.eraseRecord())){
                    doOAIAction("rest/records/eraseRecord?recordId=" + recordId.getValue());
                }
            }
        });
        operationsSet.add(submitButton);

        return operationsSet;
    }

    private FieldSet createOAIFrameFieldSet() {
        oaiSet = new FieldSet();
        oaiSet.setLayout(new RowLayout());
        oaiSet.setHeading(HarvesterUI.CONSTANTS.response());

        iframeUrlLabel = new LabelToolItem("<b>"+HarvesterUI.CONSTANTS.urlRetrievalOperations()+"</b> ");
        oaiSet.add(iframeUrlLabel,new RowData(1, -1, new Margins(0, 4, 10, 4)));

        resultFrame = new Frame();
        doOAIAction("test");
        oaiSet.add(resultFrame,new RowData(1, 1, new Margins(0, 4, 0, 4)));

        return oaiSet;
    }

    protected void setIframeUrl(String url) {
        resultFrame.setUrl(serverUrl + url);
        setIframeUrlLabelText(url);
    }

    private void setIframeUrlLabelText(String text) {
        iframeUrlLabel.setLabel("<b>"+HarvesterUI.CONSTANTS.urlRetrievalOperations()+"</b> " + text);
    }

//    private void URLencode(String url) {
//        return escape(url).replaceAll("\\+", "%2B").replaceAll("\"","%22")
//                .replaceAll("\'", "%27").replace("\\/", "%2F");
//    }
//
//    protected String escape(String s) {
//        String n = s;
//        n = n.replace('&', '&amp;');
//        n = n.replace(/</g, "&lt;");
//        n = n.replace(/>/g, "&gt;");
//        n = n.replace(/"/g, "&quot;");
//
//        return n;
//    }

    private void doOAIAction(String action) {
        if(action.equals("test")) {
            resultFrame.setUrl(serverUrl + "rest");
            setIframeUrlLabelText("rest");
        }else{
            resultFrame.setUrl(serverUrl + action);
            setIframeUrlLabelText(action);
        }
    }

    protected void clearAllStyles() {
        aggregatorsLabel.setStyleName("hyperlink_style_label");
        operationsListLabel.setStyleName("hyperlink_style_label");
        recordsLabel.setStyleName("hyperlink_style_label");
        recordFillLabel.setStyleName("hyperlink_style_label");
        dataSourcesListLabel.setStyleName("hyperlink_style_label");
        statisticsLabel.setStyleName("hyperlink_style_label");
    }
}
