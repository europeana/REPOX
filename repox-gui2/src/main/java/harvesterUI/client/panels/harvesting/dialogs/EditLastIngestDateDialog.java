package harvesterUI.client.panels.harvesting.dialogs;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.panels.forms.FormDialog;
import harvesterUI.client.servlets.harvest.HarvestOperationsServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.formPanel.DefaultFormLayout;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 28-08-2012
 * Time: 13:45
 */
public class EditLastIngestDateDialog extends FormDialog {

    private DataSourceUI usedDataSource;

    public EditLastIngestDateDialog(DataSourceUI dataSourceUI) {
        super(0.3, 0.3);
        this.usedDataSource = dataSourceUI;

        setHeading(HarvesterUI.CONSTANTS.lastIngest());
        setIcon(HarvesterUI.ICONS.form());
        setButtonAlign(Style.HorizontalAlignment.CENTER);

        final DefaultFormPanel editLastIngestFormPanel = new DefaultFormPanel();
        editLastIngestFormPanel.setHeaderVisible(false);
        editLastIngestFormPanel.setLayout(new DefaultFormLayout(150));

        FormData formData = new FormData("90%");

        final DateField lastIngestDate = new DateField();
        lastIngestDate.setFieldLabel("Last Ingest Date" + HarvesterUI.REQUIRED_STR);
        lastIngestDate.setPropertyEditor(new DateTimePropertyEditor("yyyy/MM/dd"));
        lastIngestDate.setAllowBlank(false);
        editLastIngestFormPanel.add(lastIngestDate,formData);

        ComponentPlugin plugin = new ComponentPlugin() {
            public void init(Component component) {
                component.addListener(Events.Render, new Listener<ComponentEvent>() {
                    public void handleEvent(ComponentEvent be) {
                        El elem = be.getComponent().el().findParent(".x-form-element", 3);
                        // should style in external CSS  rather than directly
                        elem.appendChild(XDOM.create("<div style='color: #615f5f;padding: 1 0 2 0px;'>" + be.getComponent().getData("text") + "</div>"));
                    }
                });
            }
        };

        final TextField<String> lastIngestHour = new TextField<String>();
        lastIngestHour.setAllowBlank(false);
        lastIngestHour.setId("lastIngestHour");
        lastIngestHour.setFieldLabel("Last Ingest Hour" + HarvesterUI.REQUIRED_STR);
        lastIngestHour.addPlugin(plugin);
        lastIngestHour.setData("text", "Ex: 16:45 or 07:13 or 12:09");

        Validator lastIngestHourValidator = new Validator() {
            public String validate(Field<?> field, String s) {
                if(!s.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]"))
                    return "Invalid Hour Format - Ex: 16:45 or 07:13 or 12:09";
                return null;
            }
        };
        lastIngestHour.setValidator(lastIngestHourValidator);
        editLastIngestFormPanel.add(lastIngestHour,formData);

        if(dataSourceUI.getLastIngest() != null){
            lastIngestDate.setValue(dataSourceUI.getLastIngest());
            lastIngestHour.setValue(convertSingleNumberToDate(dataSourceUI.getLastIngest().getHours()) + ":" + convertSingleNumberToDate(dataSourceUI.getLastIngest().getMinutes()));
        }
        add(editLastIngestFormPanel);

        Button saveButton = new Button(HarvesterUI.CONSTANTS.save(),HarvesterUI.ICONS.save_icon(),new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                if(usedDataSource.getOldTasks().size() == 0){
                    HarvesterUI.UTIL_MANAGER.getErrorBox("Edit Last Ingest Date","This Data Set doesn't have previous Ingests");
                    return;
                }

                AsyncCallback<ResponseState> callback = new AsyncCallback<ResponseState>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(ResponseState result) {
                        if(result == ResponseState.ERROR){
                            HarvesterUI.UTIL_MANAGER.getErrorBox("Edit Last Ingest Date","Error while changing Last Ingest Date");
                            return;
                        }
                        editLastIngestFormPanel.submit();
                        hide();
                        HarvesterUI.UTIL_MANAGER.getSaveBox("Edit Last Ingest Date","Last Ingest Date changed successfully");
                        History.fireCurrentHistoryState();
                    }
                };
                String lastIngestDateStr = lastIngestDate.getRawValue();
                String lastIngestHourStr = lastIngestHour.getRawValue();
                HarvestOperationsServiceAsync service = (HarvestOperationsServiceAsync) Registry.get(HarvesterUI.HARVEST_OPERATIONS_SERVICE);
                service.changeLastIngestDate(lastIngestDateStr, lastIngestHourStr, usedDataSource.getDataSourceSet(), callback);
            }
        });
        addButton(saveButton);
        addButton(new Button(HarvesterUI.CONSTANTS.cancel(), HarvesterUI.ICONS.cancel_icon(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent be) {
                hide();
            }
        }));

        FormButtonBinding binding = new FormButtonBinding(editLastIngestFormPanel);
        binding.addButton(saveButton);
    }

    private String convertSingleNumberToDate(int number){
        if(number < 10)
            return "0" + number;
        else
            return String.valueOf(number);
    }
}
