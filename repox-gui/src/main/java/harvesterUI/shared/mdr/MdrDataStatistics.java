package harvesterUI.shared.mdr;

import com.google.gwt.user.client.rpc.IsSerializable;
import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;

import java.util.List;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 28-05-2012
 * Time: 16:46
 */
public class MdrDataStatistics implements IsSerializable{

    private int nUsedInDataSets;
    private List<SimpleDataSetInfo> usedInList;

    public MdrDataStatistics() {
    }

    public MdrDataStatistics(int nUsedInDataSets, List<SimpleDataSetInfo> usedInList) {
        this.nUsedInDataSets = nUsedInDataSets;
        this.usedInList = usedInList;
    }

    public int getNumberTimesUsedInDataSets() {
        return nUsedInDataSets;
    }

    public List<SimpleDataSetInfo> getUsedInDataSetsList() {
        return usedInList;
    }
}
