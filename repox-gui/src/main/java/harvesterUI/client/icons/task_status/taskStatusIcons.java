package harvesterUI.client.icons.task_status;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 01-02-2011
 * Time: 13:53
 */

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

@SuppressWarnings("deprecation")
public interface taskStatusIcons extends ImageBundle {

//    String path = "icons\\task_status\\";

    @Resource("ok.png")
    AbstractImagePrototype ok();

    @Resource("warning.png")
    AbstractImagePrototype warning();

    @Resource("running.gif")
    AbstractImagePrototype running();

    @Resource("error.png")
    AbstractImagePrototype error();

    @Resource("canceled_task.png")
    AbstractImagePrototype canceled_task();

    @Resource("post_process_16x16.png")
    AbstractImagePrototype post_process_16x16();

    @Resource("post_process_error_16x16.png")
    AbstractImagePrototype post_process_error_16x16();

    @Resource("pre_process_16x16.png")
    AbstractImagePrototype pre_process_16x16();

    @Resource("pre_process_error_16x16.png")
    AbstractImagePrototype pre_process_error_16x16();

    @Resource("retrying.png")
    AbstractImagePrototype retrying();

    @Resource("sample.png")
    AbstractImagePrototype sample();

    @Resource("status_online.png")
    AbstractImagePrototype status_online();

    @Resource("status_offline.png")
    AbstractImagePrototype status_offline();
}

