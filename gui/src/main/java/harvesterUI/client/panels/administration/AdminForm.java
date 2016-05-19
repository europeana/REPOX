package harvesterUI.client.panels.administration;

import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.servlets.RepoxServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.client.util.formPanel.EditableFormLayout;
import harvesterUI.shared.dataTypes.admin.AdminInfo;
import pt.utl.ist.util.shared.ProjectType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created to REPOX. User: Edmundo Date: 07-04-2011 Time: 13:55
 */
public class AdminForm extends VerticalPanel {

  private FormData formData;
  public RepoxServiceAsync service;
  protected DefaultFormPanel simple;

  // Fields
  private TextField<String> repositoryFolderField, configFilesFolderField, oaiRequestFolderField,
      ftpRequestField, baseUrnField, repositoryNameField, maxRecordsOaiField,
      defaultExportFolderField, adminEmailField, smtpServerField, smtpPortField,
      repoxDefaultEmailSenderField, repoxDefualtEmailPassField, ldapHostField, ldapPassField,
      ldapRootDN, ldapBasePath, httpRequestField, sampleRecordsField, backendUrl;

  private CheckBox useCountriesTxtFile, sendEmailAfterIngest, useMailSSLAuthentication,
      useOAINamespace;

  public AdminForm() {
    service = (RepoxServiceAsync) Registry.get(HarvesterUI.REPOX_SERVICE);
    formData = new FormData("-20");
    createForm();
  }

  @Override
  protected void onRender(Element parent, int index) {
    super.onRender(parent, index);
    setLayout(new FitLayout());
  }

  private void createForm() {
    simple = new DefaultFormPanel();
    simple.setHeading(HarvesterUI.CONSTANTS.configurationSettings());
    simple.setIcon(HarvesterUI.ICONS.config_properties());

    EditableFormLayout layout = new EditableFormLayout(FormPanel.LabelAlign.RIGHT);
    layout.setLabelWidth(190);
    layout.setLabelSeparator("");
    simple.setLayout(layout);

    ComponentPlugin plugin = new ComponentPlugin() {
      @Override
      public void init(Component component) {
        component.addListener(Events.Render, new Listener<ComponentEvent>() {
          @Override
          public void handleEvent(ComponentEvent be) {
            El elem = be.getComponent().el().findParent(".x-form-element", 3);
            // should style in external CSS rather than directly
            elem.appendChild(XDOM.create("<div style='color: grey;padding: 1 0 2 0px;'>"
                + be.getComponent().getData("text") + "</div>"));
          }
        });
      }
    };

    repositoryFolderField = new TextField<String>();
    repositoryFolderField.setFieldLabel(HarvesterUI.CONSTANTS.repositoryFolder()
        + HarvesterUI.REQUIRED_STR);
    repositoryFolderField.setId("admin_repoField");
    repositoryFolderField.addPlugin(plugin);
    repositoryFolderField.setData("text", HarvesterUI.CONSTANTS.repositoryFolderExample());
    repositoryFolderField.setAllowBlank(false);
    repositoryFolderField.setEnabled(false);
    simple.add(repositoryFolderField, formData);

    configFilesFolderField = new TextField<String>();
    configFilesFolderField.setFieldLabel(HarvesterUI.CONSTANTS.configurationFilesFolder()
        + HarvesterUI.REQUIRED_STR);
    configFilesFolderField.setId("admin_configffield");
    configFilesFolderField.addPlugin(plugin);
    configFilesFolderField.setData("text", HarvesterUI.CONSTANTS.configurationFilesFolderExample());
    configFilesFolderField.setAllowBlank(false);
    configFilesFolderField.setEnabled(false);
    simple.add(configFilesFolderField, formData);

    oaiRequestFolderField = new TextField<String>();
    oaiRequestFolderField.setFieldLabel(HarvesterUI.CONSTANTS.oaiPmhRequestsFolder()
        + HarvesterUI.REQUIRED_STR);
    oaiRequestFolderField.setId("admin_oaireqfolderfield");
    oaiRequestFolderField.addPlugin(plugin);
    oaiRequestFolderField.setData("text", HarvesterUI.CONSTANTS.oaiPmhRequestsFolderExample());
    oaiRequestFolderField.setAllowBlank(false);
    oaiRequestFolderField.setEnabled(false);
    simple.add(oaiRequestFolderField, formData);

    ftpRequestField = new TextField<String>();
    ftpRequestField.setFieldLabel(HarvesterUI.CONSTANTS.ftpRequestsFolder()
        + HarvesterUI.REQUIRED_STR);
    ftpRequestField.setId("admin_ftpreqfolderfield");
    ftpRequestField.addPlugin(plugin);
    ftpRequestField.setData("text", HarvesterUI.CONSTANTS.ftpRequestsFolderExample());
    ftpRequestField.setAllowBlank(false);
    ftpRequestField.setEnabled(false);
    simple.add(ftpRequestField, formData);

    httpRequestField = new TextField<String>();
    httpRequestField.setFieldLabel(HarvesterUI.CONSTANTS.httpRequestsFolder()
        + HarvesterUI.REQUIRED_STR);
    httpRequestField.setId("admin_httpreqfolderfield");
    httpRequestField.addPlugin(plugin);
    httpRequestField.setData("text", HarvesterUI.CONSTANTS.httpRequestsFolderExample());
    httpRequestField.setAllowBlank(false);
    httpRequestField.setEnabled(false);
    simple.add(httpRequestField, formData);

    defaultExportFolderField = new TextField<String>();
    defaultExportFolderField.setFieldLabel(HarvesterUI.CONSTANTS.defaultExportFolder()
        + HarvesterUI.REQUIRED_STR);
    defaultExportFolderField.addPlugin(plugin);
    defaultExportFolderField.setId("admin_defaultExportField");
    defaultExportFolderField.setData("text", HarvesterUI.CONSTANTS.defaultExportFolderExample());
    defaultExportFolderField.setEnabled(false);
    defaultExportFolderField.setAllowBlank(false);
    simple.add(defaultExportFolderField, formData);

    sampleRecordsField = new TextField<String>();
    sampleRecordsField.setFieldLabel(HarvesterUI.CONSTANTS.sampleRecords()
        + HarvesterUI.REQUIRED_STR);
    sampleRecordsField.setId("admin_samplerecordsfield");
    sampleRecordsField.addPlugin(plugin);
    sampleRecordsField.setData("text", HarvesterUI.CONSTANTS.sampleRecordsExample());
    sampleRecordsField.setAllowBlank(false);
    simple.add(sampleRecordsField, formData);

    baseUrnField = new TextField<String>();
    baseUrnField.setFieldLabel(HarvesterUI.CONSTANTS.baseUrn() + HarvesterUI.REQUIRED_STR);
    baseUrnField.setId("admin_baseurnField");
    baseUrnField.addPlugin(plugin);
    baseUrnField.setData("text", HarvesterUI.CONSTANTS.baseUrnExample());
    baseUrnField.setAllowBlank(false);
    simple.add(baseUrnField, formData);

    repositoryNameField = new TextField<String>();
    repositoryNameField.setFieldLabel("OAI Repository Name");
    repositoryNameField.setId("oai_repositoryName");
    repositoryNameField.addPlugin(plugin);
    repositoryNameField.setData("text", "The name of the repository on the OAI responses");
    simple.add(repositoryNameField, formData);

    maxRecordsOaiField = new TextField<String>();
    maxRecordsOaiField.setFieldLabel("OAI Max List Size");
    maxRecordsOaiField.setId("admin_maxOaiList");
    maxRecordsOaiField.addPlugin(plugin);
    maxRecordsOaiField.setData("text",
        "Maximum number of records listed for each ListRecords OAI call (Ex: 250)");
    maxRecordsOaiField.setAllowBlank(false);
    simple.add(maxRecordsOaiField, formData);

    backendUrl = new TextField<String>();
    backendUrl.setFieldLabel("Eudml backend Url " + HarvesterUI.REQUIRED_STR);
    backendUrl.setId("admin_backendurl");
    backendUrl.setAllowBlank(false);

    adminEmailField = new TextField<String>();
    adminEmailField.setFieldLabel(HarvesterUI.CONSTANTS.administratorEmail()
        + HarvesterUI.REQUIRED_STR);
    adminEmailField.setId("admin_email");
    adminEmailField.setAllowBlank(false);
    adminEmailField.addPlugin(plugin);
    adminEmailField.setData("text", HarvesterUI.CONSTANTS.administratorEmailInfo());
    simple.add(adminEmailField, formData);

    repoxDefaultEmailSenderField = new TextField<String>();
    repoxDefaultEmailSenderField.setFieldLabel(HarvesterUI.CONSTANTS.repoxDefaultEmailSender()
        + HarvesterUI.REQUIRED_STR);
    repoxDefaultEmailSenderField.setAllowBlank(false);
    repoxDefaultEmailSenderField.addPlugin(plugin);
    repoxDefaultEmailSenderField.setData("text",
        HarvesterUI.CONSTANTS.repoxDefaultEmailSenderInfo());
    repoxDefaultEmailSenderField.setId("admin_repoxdefaultemailsenderField");
    simple.add(repoxDefaultEmailSenderField, formData);

    repoxDefualtEmailPassField = new TextField<String>();
    repoxDefualtEmailPassField.setFieldLabel(HarvesterUI.CONSTANTS.repoxDefaultEmailPassword());
    repoxDefualtEmailPassField.setId("admin_repoxdefaultemailpassField");
    repoxDefualtEmailPassField.setPassword(true);
    simple.add(repoxDefualtEmailPassField, formData);

    smtpServerField = new TextField<String>();
    smtpServerField.setFieldLabel(HarvesterUI.CONSTANTS.smtpServer() + HarvesterUI.REQUIRED_STR);
    smtpServerField.setId("admin_smtpserverfield");
    smtpServerField.setAllowBlank(false);
    simple.add(smtpServerField, formData);

    smtpPortField = new TextField<String>();
    smtpPortField.setFieldLabel(HarvesterUI.CONSTANTS.smtpPort() + HarvesterUI.REQUIRED_STR);
    smtpPortField.setId("admin_smtpportfield");
    simple.add(smtpPortField, formData);

    ldapHostField = new TextField<String>();
    ldapHostField.setFieldLabel("LDAP Host");
    ldapHostField.setId("admin_ldapHost");
    simple.add(ldapHostField, formData);
    
    ldapRootDN = new TextField<String>();
    ldapRootDN.setFieldLabel("LDAP Root DN");
    ldapRootDN.setId("admin_ldapRootDN");
    simple.add(ldapRootDN, formData);

    ldapPassField = new TextField<String>();
    ldapPassField.setFieldLabel("LDAP Password");
    ldapPassField.setId("admin_ldappassField");
    ldapPassField.setPassword(true);
    simple.add(ldapPassField, formData);
    
    ldapBasePath = new TextField<String>();
    ldapBasePath.setFieldLabel("LDAP Base Path");
    ldapBasePath.setId("admin_ldapBasePath");
    simple.add(ldapBasePath, formData);

    CheckBoxGroup checkBoxGroup = new CheckBoxGroup();
    checkBoxGroup = new CheckBoxGroup();
    useCountriesTxtFile = new CheckBox();
    checkBoxGroup.setId("useCountriesCheck");
    checkBoxGroup.setFieldLabel("Use Countries File?");
    checkBoxGroup.add(useCountriesTxtFile);
    simple.add(checkBoxGroup, formData);

    checkBoxGroup = new CheckBoxGroup();
    sendEmailAfterIngest = new CheckBox();
    checkBoxGroup.setId("sendEmailAfterIngestCheck");
    checkBoxGroup.setFieldLabel("Send Email after Ingestion?");
    checkBoxGroup.add(sendEmailAfterIngest);
    simple.add(checkBoxGroup, formData);

    checkBoxGroup = new CheckBoxGroup();
    useMailSSLAuthentication = new CheckBox();
    checkBoxGroup.setId("useMailSSLAuthentication");
    checkBoxGroup.setFieldLabel("Use SSL Mail Authentication?");
    checkBoxGroup.add(useMailSSLAuthentication);
    simple.add(checkBoxGroup, formData);

    checkBoxGroup = new CheckBoxGroup();
    useOAINamespace = new CheckBox();
    checkBoxGroup.setId("useOAINamespace");
    checkBoxGroup.setFieldLabel("Use OAI Namespace?");
    checkBoxGroup.add(useOAINamespace);
    simple.add(checkBoxGroup, formData);

    Button saveButton =
        new Button(HarvesterUI.CONSTANTS.save(), HarvesterUI.ICONS.save_icon(),
            new SelectionListener<ButtonEvent>() {
              @Override
              public void componentSelected(ButtonEvent be) {
                AdminInfo adminInfo = new AdminInfo();
                adminInfo.set("repositoryFolder", repositoryFolderField.getValue());
                adminInfo.set("configFilesFolder", configFilesFolderField.getValue());
                adminInfo.set("oaiRequestFolder", oaiRequestFolderField.getValue());
                adminInfo.set("baseUrn", baseUrnField.getValue());
                adminInfo.set("defaultExportFolder", defaultExportFolderField.getValue());
                adminInfo.set("adminEmail", adminEmailField.getValue());
                adminInfo.set("repoxDefaultEmailSender", repoxDefaultEmailSenderField.getValue());
                adminInfo.set("repoxDefaultEmailPass", repoxDefualtEmailPassField.getValue());
                adminInfo.set("smtpServer", smtpServerField.getValue());
                adminInfo.set("smtpPort", smtpPortField.getValue());
                adminInfo.set("ftpRequestFolder", ftpRequestField.getValue());
                adminInfo.set("httpRequestFolder", httpRequestField.getValue());
                adminInfo.set("sampleRecords", sampleRecordsField.getValue());
                adminInfo.set("ldapHost", ldapHostField.getValue());
                adminInfo.set("ldapRootDN", ldapRootDN.getValue());
                adminInfo.set("ldapRootPassword", ldapPassField.getValue());
                adminInfo.set("ldapBasePath", ldapBasePath.getValue());
                adminInfo.set("useCountriesTxt", useCountriesTxtFile.getValue());
                adminInfo.set("sendEmailAfterIngest", sendEmailAfterIngest.getValue());
                adminInfo.set("useMailSSLAuthentication", useMailSSLAuthentication.getValue());
                adminInfo.set("useOAINamespace", useOAINamespace.getValue());
                adminInfo.set("backendUrl", backendUrl.getValue());
                adminInfo.set("oaiRepoName", repositoryNameField.getValue());
                adminInfo.set("oaiMaxList", maxRecordsOaiField.getValue());

                AsyncCallback callback = new AsyncCallback() {
                  @Override
                  public void onFailure(Throwable caught) {
                    new ServerExceptionDialog("Failed to get response from server", caught
                        .getMessage()).show();
                  }

                  @Override
                  public void onSuccess(Object result) {
                    simple.submit();
                    History.newItem("HOME");
                    Window.Location.reload();
                    // Dispatcher.get().dispatch(AppEvents.ViewOverviewGrid);
                    // HarvesterUI.UTIL_MANAGER.getSaveBox("Administration",
                    // "Admin Configuration Saved Successfully");
                  }
                };
                String oaiPropertiesReloadUrl =
                    UtilManager.getOaiServerUrl() + "?reloadOAIProperties";
                adminInfo.setReloadOAIPropertiesUrl(oaiPropertiesReloadUrl);
                service.saveAdminFormInfo(adminInfo, callback);
              }
            });
    simple.addButton(saveButton);
    simple.addButton(new Button(HarvesterUI.CONSTANTS.cancel(), HarvesterUI.ICONS.cancel_icon(),
        new SelectionListener<ButtonEvent>() {
          @Override
          public void componentSelected(ButtonEvent be) {
            Dispatcher.get().dispatch(AppEvents.LoadMainData);
          }
        }));

    simple.setButtonAlign(Style.HorizontalAlignment.CENTER);

    FormButtonBinding binding = new FormButtonBinding(simple);
    binding.addButton(saveButton);

    add(simple);
  }

  public void editAdminForm() {
    AsyncCallback<AdminInfo> callback = new AsyncCallback<AdminInfo>() {
      @Override
      public void onFailure(Throwable caught) {
        new ServerExceptionDialog("Failed to get response from server", caught.getMessage()).show();
      }

      @Override
      public void onSuccess(AdminInfo dataModel) {
        repositoryFolderField.setValue((String) dataModel.get("repositoryFolder"));
        configFilesFolderField.setValue((String) dataModel.get("configFilesFolder"));
        oaiRequestFolderField.setValue((String) dataModel.get("oaiRequestFolder"));
        baseUrnField.setValue((String) dataModel.get("baseUrn"));
        defaultExportFolderField.setValue((String) dataModel.get("defaultExportFolder"));
        adminEmailField.setValue((String) dataModel.get("adminEmail"));
        smtpServerField.setValue((String) dataModel.get("smtpServer"));
        smtpPortField.setValue((String) dataModel.get("smtpPort"));
        repoxDefaultEmailSenderField.setValue((String) dataModel.get("repoxDefaultEmailSender"));
        repoxDefualtEmailPassField.setValue((String) dataModel.get("repoxDefaultEmailPass"));
        httpRequestField.setValue((String) dataModel.get("httpRequestFolder"));
        ftpRequestField.setValue((String) dataModel.get("ftpRequestFolder"));
        sampleRecordsField.setValue(String.valueOf(dataModel.get("sampleRecords")));
        ldapHostField.setValue((String) dataModel.get("ldapHost"));
        ldapRootDN.setValue((String) dataModel.get("ldapRootDN"));
        ldapPassField.setValue((String) dataModel.get("ldapRootPassword"));
        ldapBasePath.setValue((String) dataModel.get("ldapBasePath"));
        useCountriesTxtFile.setValue((Boolean) dataModel.get("useCountriesTxt"));
        sendEmailAfterIngest.setValue((Boolean) dataModel.get("sendEmailAfterIngest"));
        useMailSSLAuthentication.setValue((Boolean) dataModel.get("useMailSSLAuthentication"));
        useOAINamespace.setValue((Boolean) dataModel.get("useOAINamespace"));
        backendUrl.setValue((String) dataModel.get("backendUrl"));
        repositoryNameField.setValue((String) dataModel.get("oaiRepoName"));
        maxRecordsOaiField.setValue((String) dataModel.get("oaiMaxList"));
      }
    };
    service.loadAdminFormInfo(callback);
  }
}
