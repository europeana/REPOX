package harvesterUI.client.panels.overviewGrid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.util.DefaultComparator;
import harvesterUI.shared.dataTypes.DataProviderUI;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 26/03/12
 * Time: 11:35
 */
public class MainGridDefaultComparator {

    public MainGridDefaultComparator() {

    }

    public int compareMainGridItem(Store<ModelData> store, ModelData m1, ModelData m2, String property){
        if (property != null){
            if(property.equals("records")){
                if (m1 instanceof DataProviderUI && m2 instanceof DataProviderUI) {
                    DataProviderUI dp1 = (DataProviderUI) m1;
                    DataProviderUI dp2 = (DataProviderUI) m2;
                    int r1, r2;

                    if(dp1.getDataSourceUIList().size() == 1 && dp2.getDataSourceUIList().size() == 1) {
                        if(dp1.getDataSourceUIList().get(0).get(property) == null)
                            r1 = 0;
                        else
                            r1 = Integer.parseInt(((String)dp1.getDataSourceUIList().get(0).get(property)).replace(".", ""));

                        if(dp2.getDataSourceUIList().get(0).get(property) == null)
                            r2 = 0;
                        else
                            r2 = Integer.parseInt(((String)dp2.getDataSourceUIList().get(0).get(property)).replace(".", ""));

                        if(r1 > r2)
                            return 1;
                        else if(r1 < r2)
                            return -1;
                        else
                            return 0;
                    }
                    else if(dp1.getChildCount() > 0 && dp2.getChildCount() > 0){
                        if(dp1.getChild(0).get(property) == null)
                            r1 = 0;
                        else
                            r1 = Integer.parseInt(((String)dp1.getChild(0).get(property)).replace(".", ""));

                        if(dp2.getChild(0).get(property) == null)
                            r2 = 0;
                        else
                            r2 = Integer.parseInt(((String)dp2.getChild(0).get(property)).replace(".", ""));

                        if(r1 > r2)
                            return 1;
                        else if(r1 < r2)
                            return -1;
                        else
                            return 0;
                    }
                }
                return 0;
            } else {
                Object v1 = m1.get(property);
                Object v2 = m2.get(property);
                DefaultComparator<Object> compr = new DefaultComparator<Object>();
                return compr.compare(v1, v2);
            }
        }
        return 0;
    }
}
