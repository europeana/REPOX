package harvesterUI.shared.dataTypes.dataSet;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;
import harvesterUI.shared.mdr.MdrDataStatistics;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 30-04-2012
 * Time: 11:08
 */
public class DataSetTagUI extends BaseModel implements IsSerializable {

    private MdrDataStatistics mdrDataStatistics;

    public DataSetTagUI() {
    }

    public DataSetTagUI(String name, MdrDataStatistics mdrDataStatistics) {
        this(name);
        this.mdrDataStatistics = mdrDataStatistics;
    }

    public DataSetTagUI(String name) {
        set("name",name);
    }

    public String getName() {
        return get("name");
    }

    public MdrDataStatistics getMdrDataStatistics() {
        return mdrDataStatistics;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof DataSetTagUI){
            DataSetTagUI otherObj = (DataSetTagUI) other;
            return otherObj.getName().equals(getName());
        }else
            return false;
    }

    @Override
    public int hashCode() { return 0; }
}
