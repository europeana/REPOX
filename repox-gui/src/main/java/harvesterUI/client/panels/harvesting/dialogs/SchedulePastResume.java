package harvesterUI.client.panels.harvesting.dialogs;

import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.util.UtilManager;
import harvesterUI.client.util.formPanel.DefaultFormLayout;
import harvesterUI.client.util.formPanel.DefaultFormPanel;
import harvesterUI.shared.tasks.OldTaskUI;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 25-03-2011
 * Time: 17:32
 */
public class SchedulePastResume extends Dialog {

    public SchedulePastResume(final OldTaskUI taskUI){
        String dataSetId = taskUI.getDataSetId();
        FormData formData = new FormData("-10");
        DefaultFormPanel resumeSet = new DefaultFormPanel();
        resumeSet.setAutoHeight(true);
        resumeSet.setHeaderVisible(false);
        resumeSet.setLayout(new DefaultFormLayout(250));

        setButtons("");
        setLayout(new FitLayout());
        setHeading(HarvesterUI.CONSTANTS.ingestResume() +": " + dataSetId);
        setIcon(HarvesterUI.ICONS.side_list());
        setWidth(650);
        setHeight(420);
        setResizable(false);
        setModal(true);

        LabelField date = new LabelField();
        date.setFieldLabel(HarvesterUI.CONSTANTS.ingestDate());
        date.setValue(taskUI.getOnlyDate() + " at " + taskUI.getOnlyTime());
        resumeSet.add(date, formData);

        LabelField status = new LabelField();
        status.setFieldLabel(HarvesterUI.CONSTANTS.status());
        status.setValue(taskUI.getStatus());
        resumeSet.add(status, formData);

        LabelField fullIngest = new LabelField();
        fullIngest.setFieldLabel(HarvesterUI.CONSTANTS.ingestType());
        fullIngest.setValue(taskUI.getIngestType());
        resumeSet.add(fullIngest, formData);

        LabelField retries = new LabelField();
        retries.setFieldLabel(HarvesterUI.CONSTANTS.numberRetries());
        retries.setValue(taskUI.getRetries());
        resumeSet.add(retries, formData);

        LabelField records = new LabelField();
        records.setFieldLabel(HarvesterUI.CONSTANTS.numberRecordsIngested());
        records.setValue(taskUI.getRecords());
        resumeSet.add(records, formData);

        LabelField log = new LabelField();
        log.setFieldLabel(HarvesterUI.CONSTANTS.logLink());
        log.addListener(Events.OnClick,new Listener<BoxComponentEvent>() {
            public void handleEvent(BoxComponentEvent be) {
                UtilManager.showLogFromByLogName(taskUI);
            }
        });
        log.setStyleName("hyperlink_style_label");
        log.setValue("<div style='color:blue'>" + taskUI.getLogName() + "</div>");
        resumeSet.add(log, formData);

        add(resumeSet);
    }
}
