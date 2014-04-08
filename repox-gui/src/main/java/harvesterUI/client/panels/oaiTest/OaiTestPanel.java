package harvesterUI.client.panels.oaiTest;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.dataManagement.DataSetOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.TextFieldWithButton;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;

import java.util.List;
import java.util.Map;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 08-04-2011
 * Time: 20:34
 */
public class OaiTestPanel extends ContentPanel {

    private Frame resultFrame;
    private TextField<String> metadataPrefix,from,until,set,identifier,resumptionToken;
    private TextFieldWithButton<String> serverUrl;
    private FieldSet oaiSet;
    private SimpleComboBox<String> setsCombo, mdPrefixesCombo;
    protected Button check;

    protected LabelToolItem identifyLabel,listMetadataFormatsLabel,listSetsLabel,
            listIdentifiersLabel,listRecordsLabel,getRecordLabel;

    protected DefaultFormPanel parametersSet;
    protected FieldSet globalParametersSet;

    public OaiTestPanel() {
        setsCombo = new SimpleComboBox<String>();
        setsCombo.setForceSelection(true);
        setsCombo.setFieldLabel(HarvesterUI.CONSTANTS.set());
        setsCombo.add("<ALL>");
        setsCombo.setTriggerAction(ComboBox.TriggerAction.ALL);

        mdPrefixesCombo = new SimpleComboBox<String>();
        mdPrefixesCombo.setForceSelection(true);
        mdPrefixesCombo.setFieldLabel(HarvesterUI.CONSTANTS.metadataPrefix());
        mdPrefixesCombo.setTriggerAction(ComboBox.TriggerAction.ALL);

        set = new TextField<String>();
        set.setFieldLabel(HarvesterUI.CONSTANTS.set());
        set.setId("oaiTest_set");

        metadataPrefix = new TextField<String>();
        metadataPrefix.setFieldLabel(HarvesterUI.CONSTANTS.metadataPrefix());
        metadataPrefix.setId("oaitest_mtdprefix");

        from = new TextField<String>();
        from.setFieldLabel(HarvesterUI.CONSTANTS.from());
        from.setId("oaitest_from");

        until = new TextField<String>();
        until.setFieldLabel(HarvesterUI.CONSTANTS.until());
        until.setId("oaitest_until");

        identifier = new TextField<String>();
        identifier.setFieldLabel(HarvesterUI.CONSTANTS.identifier());
        identifier.setId("oaitest_identifier");

        resumptionToken = new TextField<String>();
        resumptionToken.setFieldLabel(HarvesterUI.CONSTANTS.resumptionToken());
        resumptionToken.setId("oaitest_resToken");

        check = new Button(HarvesterUI.CONSTANTS.check(),HarvesterUI.ICONS.oai_check(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                AsyncCallback<Map<String,List<String>>> callback = new AsyncCallback<Map<String,List<String>>>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(Map<String,List<String>> result) {
                        parametersSet.submit();
                        setsCombo.getStore().removeAll();
                        if(result.get("sets").size() == 0)
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.noSetsFound());
                        setsCombo.add(result.get("sets"));
                        setsCombo.getStore().sort("value",Style.SortDir.ASC);
                        setsCombo.setValue(setsCombo.getStore().getAt(0));
                        setsCombo.show();
                        set.hide();

                        mdPrefixesCombo.getStore().removeAll();
                        if(result.get("mdPrefixes").size() == 0)
                            HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.noMetadataPrefixesFound());
                        mdPrefixesCombo.add(result.get("mdPrefixes"));
                        mdPrefixesCombo.getStore().sort("value",Style.SortDir.ASC);
                        mdPrefixesCombo.setValue(mdPrefixesCombo.getStore().getAt(0));
                        mdPrefixesCombo.show();
                        metadataPrefix.hide();

                        doOAIAction("Identify");
                    }
                };
                if(serverUrl == null || serverUrl.getValue().trim().equals(""))
                    HarvesterUI.UTIL_MANAGER.getErrorBox(HarvesterUI.CONSTANTS.checkUrl(), HarvesterUI.CONSTANTS.noUrlFound());
                else {
                    DataSetOperationsServiceAsync service = (DataSetOperationsServiceAsync) Registry.get(HarvesterUI.DATA_SET_OPERATIONS_SERVICE);
                    service.checkOAIURL(serverUrl.getValue().trim(), callback);
                }
            }
        });

        serverUrl = new TextFieldWithButton<String>(check);
        serverUrl.setFieldLabel(HarvesterUI.CONSTANTS.oaiUrl());
        serverUrl.setId("oaiTest_serverUrl");

        globalParametersSet = createParametersFieldSet();
        identifyLabel = new LabelToolItem("<span style='color:blue" + "'>" + "Identify" + "</span>");
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        setLayout(new FitLayout());
        setHeading(HarvesterUI.CONSTANTS.oaiPmhTests());
        setIcon(HarvesterUI.ICONS.oai_icon());
        setScrollMode(Style.Scroll.AUTO);

        LayoutContainer main = new LayoutContainer();
        main.setLayout(new BorderLayout());
        main.setStyleName("repoxFormBackground");
        main.setScrollMode(Style.Scroll.AUTO);

        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.WEST, 340,300,350);
        data.setMargins(new Margins(5,5,5,5));

        LayoutContainer left = new LayoutContainer();
        FlowLayout layout = new FlowLayout();
        left.setLayout(layout);

        LayoutContainer othersContainer = new LayoutContainer();
        othersContainer.setLayout(new ColumnLayout());
        LabelToolItem othersLabel = new LabelToolItem("<span style='color:blue" + "'>" + HarvesterUI.CONSTANTS.restServices() + "</span>");
        othersLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                Dispatcher.get().dispatch(AppEvents.ViewRestRecordOperations);
            }
        });
        othersLabel.setStyleName("hyperlink_style_label");
        othersContainer.add(new LabelToolItem(HarvesterUI.CONSTANTS.others() + ":&nbsp &nbsp"));
        othersContainer.add(othersLabel);
        left.add(globalParametersSet);
        left.add(createOperationsFieldSet());
        left.add(othersContainer);
        main.add(left,data);

        BorderLayoutData rightData = new BorderLayoutData(Style.LayoutRegion.CENTER, 300,300,350);
        rightData.setMargins(new Margins(5,5,5,5));
        main.add(createOAIFrameFieldSet(),rightData);

        add(main);
    }

    public void newOAITest() {
        set.show();
        set.clear();
        metadataPrefix.clear();
        setsCombo.hide();
        mdPrefixesCombo.hide();
        metadataPrefix.show();
        from.clear();
        until.clear();
        identifier.clear();
        resumptionToken.clear();

        serverUrl.setValue(UtilManager.getOaiServerUrl());
    }

    public void loadOAITest(DataSourceUI dataSourceUI) {
        serverUrl.setValue(dataSourceUI.getOaiSource());
        set.setValue(dataSourceUI.getOaiSet());
        metadataPrefix.setValue(dataSourceUI.getSourceMDFormat());
        mdPrefixesCombo.hide();
        setsCombo.hide();
        set.show();
        metadataPrefix.show();

        identifyLabel.fireEvent(Events.OnClick);
    }

    private FieldSet createParametersFieldSet() {
        FieldSet fieldSet = new FieldSet();
        fieldSet.setHeading(HarvesterUI.CONSTANTS.parameters());

        parametersSet = new DefaultFormPanel();
        parametersSet.setAutoHeight(true);
        parametersSet.setLayout(new EditableFormLayout(110));
        parametersSet.setHeaderVisible(false);

        parametersSet.add(serverUrl);

        parametersSet.add(setsCombo);

        parametersSet.add(set);

        parametersSet.add(mdPrefixesCombo);
        parametersSet.add(metadataPrefix);

        parametersSet.add(from);
        parametersSet.add(until);
        parametersSet.add(identifier);
        parametersSet.add(resumptionToken);

        fieldSet.add(parametersSet);
        return fieldSet;
    }

    private FieldSet createOperationsFieldSet() {
        FormLayout layout = new FormLayout();
        layout.setLabelWidth(100);
        FieldSet operationsSet = new FieldSet();
        operationsSet.setHeading(HarvesterUI.CONSTANTS.operations());
        operationsSet.setAutoHeight(true);
        operationsSet.setLayout(layout);

        LayoutContainer identifyContainer = new LayoutContainer();
        identifyContainer.setLayout(new ColumnLayout());
        identifyLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                doOAIAction("Identify");
                clearAllStyles();
                identifyLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        identifyLabel.setStyleName("hyperlink_style_label_selected");

        ToolButton identifyHelp = new ToolButton("x-tool-help", new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
                Window.open("http://www.openarchives.org/OAI/openarchivesprotocol.html#Identify","","");
            }
        });
        identifyContainer.add(identifyHelp);
        identifyContainer.add(new LabelToolItem("&nbsp &nbsp"));
        identifyContainer.add(identifyLabel);
        operationsSet.add(identifyContainer);

        LayoutContainer listMetadataFormatsContainer = new LayoutContainer();
        listMetadataFormatsContainer.setLayout(new ColumnLayout());
        listMetadataFormatsLabel = new LabelToolItem("<span style='color:blue" + "'>" + "ListMetadataFormats" + "</span>");
        listMetadataFormatsLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                doOAIAction("ListMetadataFormats");
                clearAllStyles();
                listMetadataFormatsLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        listMetadataFormatsLabel.setStyleName("hyperlink_style_label");

        ToolButton listMetadataFormatsHelp = new ToolButton("x-tool-help", new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
                Window.open("http://www.openarchives.org/OAI/openarchivesprotocol.html#ListMetadataFormats","","");
            }
        });
        listMetadataFormatsContainer.add(listMetadataFormatsHelp);
        listMetadataFormatsContainer.add(new LabelToolItem("&nbsp &nbsp"));
        listMetadataFormatsContainer.add(listMetadataFormatsLabel);
        operationsSet.add(listMetadataFormatsContainer);

        LayoutContainer listSetsContainer = new LayoutContainer();
        listSetsContainer.setLayout(new ColumnLayout());
        listSetsLabel = new LabelToolItem("<span style='color:blue" + "'>" + "ListSets" + "</span>");
        listSetsLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                doOAIAction("ListSets");
                clearAllStyles();
                listSetsLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        listSetsLabel.setStyleName("hyperlink_style_label");

        ToolButton listSetsHelp = new ToolButton("x-tool-help", new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
                Window.open("http://www.openarchives.org/OAI/openarchivesprotocol.html#ListSets","","");
            }
        });
        listSetsContainer.add(listSetsHelp);
        listSetsContainer.add(new LabelToolItem("&nbsp &nbsp"));
        listSetsContainer.add(listSetsLabel);
        operationsSet.add(listSetsContainer);

        LayoutContainer listIdentifiersContainer = new LayoutContainer();
        listIdentifiersContainer.setLayout(new ColumnLayout());
        listIdentifiersLabel = new LabelToolItem("<span style='color:blue" + "'>" + "ListIdentifiers" + "</span>");
        listIdentifiersLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                doOAIAction("ListIdentifiers");
                clearAllStyles();
                listIdentifiersLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        listIdentifiersLabel.setStyleName("hyperlink_style_label");

        ToolButton listIdentifiersHelp = new ToolButton("x-tool-help", new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
                Window.open("http://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers","","");
            }
        });
        listIdentifiersContainer.add(listIdentifiersHelp);
        listIdentifiersContainer.add(new LabelToolItem("&nbsp &nbsp"));
        listIdentifiersContainer.add(listIdentifiersLabel);
        operationsSet.add(listIdentifiersContainer);

        LayoutContainer listRecordsContainer = new LayoutContainer();
        listRecordsContainer.setLayout(new ColumnLayout());
        listRecordsLabel = new LabelToolItem("<span style='color:blue" + "'>" + "ListRecords" + "</span>");
        listRecordsLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                doOAIAction("ListRecords");
                clearAllStyles();
                listRecordsLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        listRecordsLabel.setStyleName("hyperlink_style_label");

        ToolButton listRecordsHelp = new ToolButton("x-tool-help", new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
                Window.open("http://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords","","");
            }
        });
        listRecordsContainer.add(listRecordsHelp);
        listRecordsContainer.add(new LabelToolItem("&nbsp &nbsp"));
        listRecordsContainer.add(listRecordsLabel);
        operationsSet.add(listRecordsContainer);

        LayoutContainer getRecordContainer = new LayoutContainer();
        getRecordContainer.setLayout(new ColumnLayout());
        getRecordLabel = new LabelToolItem("<span style='color:blue" + "'>" + "GetRecord" + "</span>");
        getRecordLabel.addListener(Events.OnClick, new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                doOAIAction("GetRecord");
                clearAllStyles();
                getRecordLabel.setStyleName("hyperlink_style_label_selected");
            }
        });
        getRecordLabel.setStyleName("hyperlink_style_label");

        ToolButton getRecordHelp = new ToolButton("x-tool-help", new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
                Window.open("http://www.openarchives.org/OAI/openarchivesprotocol.html#GetRecord","","");
            }
        });
        getRecordContainer.add(getRecordHelp);
        getRecordContainer.add(new LabelToolItem("&nbsp &nbsp"));
        getRecordContainer.add(getRecordLabel);
        operationsSet.add(getRecordContainer);

        return operationsSet;
    }

    private FieldSet createOAIFrameFieldSet() {
        oaiSet = new FieldSet();
        oaiSet.setLayout(new FitLayout());
        oaiSet.setHeading(HarvesterUI.CONSTANTS.response());
//        oaiSet.setHeight(Window.getClientHeight() - 100 + "px");
//        oaiSet.setWidth(Window.getClientWidth() *0.65 + "px");

//        Window.addResizeHandler(new ResizeHandler() {
//            public void onResize(com.google.gwt.event.logical.shared.ResizeEvent event) {
//                resizeTimer.schedule(500);
//                int h = event.getHeight();
//            }
//        });

        resultFrame = new Frame();
        doOAIAction("Identify");
        oaiSet.add(resultFrame);

        return oaiSet;
    }

    private void doOAIAction(String action) {
        if(parametersSet.isRendered())
            parametersSet.submit();
        if(action.equals("Identify"))
            resultFrame.setUrl(serverUrl.getValue() + "?verb=Identify");
        else if(action.equals("ListMetadataFormats"))
            resultFrame.setUrl(serverUrl.getValue() + "?verb=ListMetadataFormats");
        else if(action.equals("ListSets"))
            resultFrame.setUrl(serverUrl.getValue() + "?verb=ListSets");
        else if(action.equals("ListIdentifiers"))
            resultFrame.setUrl(serverUrl.getValue() + "?verb=ListIdentifiers" + getListParameters());
        else if(action.equals("ListRecords"))
            resultFrame.setUrl(serverUrl.getValue() + "?verb=ListRecords" + getListParameters());
        else if(action.equals("GetRecord"))
            resultFrame.setUrl(serverUrl.getValue() + "?verb=GetRecord&identifier=" + identifier.getValue() + "&metadataPrefix=" + (metadataPrefix.getValue() != null ? metadataPrefix.getValue() : ((mdPrefixesCombo.getValue() != null && mdPrefixesCombo.getValue().getValue() != null) ? mdPrefixesCombo.getValue().getValue() : "")));
    }

    private String getListParameters() {
        String parameters = "";
        if(!setsCombo.isVisible()){
            setsCombo.clear();
            mdPrefixesCombo.clear();
        }

        if(resumptionToken.getValue() != null) {
            parameters += "&resumptionToken=" + resumptionToken.getValue();
        }
        else {
            parameters += "&set=" + (set.getValue() != null ? set.getValue() : ((setsCombo.getValue() != null && setsCombo.getValue().getValue() != null) ? setsCombo.getValue().getValue() : ""));
            parameters += "&metadataPrefix=" + (metadataPrefix.getValue() != null ? metadataPrefix.getValue() : ((mdPrefixesCombo.getValue() != null && mdPrefixesCombo.getValue().getValue() != null) ? mdPrefixesCombo.getValue().getValue() : ""));
            if(from.getValue() != null && !from.getValue().isEmpty())
                parameters += "&from=" + from.getValue();
            if(until.getValue() != null && !until.getValue().isEmpty())
                parameters += "&until=" + until.getValue();
        }
        return parameters;
    }

    private int height = -1;
    private Timer resizeTimer = new Timer() {
        @Override
        public void run() {
            int newHeight = Window.getClientHeight();
            if (newHeight != height) {
                height = newHeight;
                oaiSet.setHeight(Window.getClientHeight() - 100 + "px");
                oaiSet.setWidth(Window.getClientWidth() *0.65 + "px");
            }
        }
    };

    protected void clearAllStyles() {
        identifyLabel.setStyleName("hyperlink_style_label");
        listMetadataFormatsLabel.setStyleName("hyperlink_style_label");
        listSetsLabel.setStyleName("hyperlink_style_label");
        listIdentifiersLabel.setStyleName("hyperlink_style_label");
        listRecordsLabel.setStyleName("hyperlink_style_label");
        getRecordLabel.setStyleName("hyperlink_style_label");
    }
}
