package harvesterUI.client.panels.forms;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 08/02/12
 * Time: 18:34
 */
public class FormDialog extends Dialog {

    private double heightPercentage;
    private double widthPercentage;

    public FormDialog(double heightPercentage, double widthPercentage) {
        this.heightPercentage = heightPercentage;
        this.widthPercentage = widthPercentage;
        setLayout(new FitLayout());

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                resize();
            }
        });

        setBorders(false);
        setBodyBorder(false);

        setModal(true);
        setHeight((int)(Window.getClientHeight() * heightPercentage));
        setWidth((int)(Window.getClientWidth() * widthPercentage));
        setButtons("");
    }

    private void resize(){
        setHeight((int)(Window.getClientHeight() * heightPercentage));
        setWidth((int)(Window.getClientWidth() * widthPercentage));
        if(isRendered()){
            center();
            layout(true);
        }
    }

    public void showAndCenter(){
        show();
        center();
    }

    @Override
    protected void onHide() {
        super.onHide();
//        OverviewGrid overviewGrid = (OverviewGrid) Registry.get("asynctree");
//        if(overviewGrid.isVisible())
//            History.newItem("HOME", false);
    }
}
