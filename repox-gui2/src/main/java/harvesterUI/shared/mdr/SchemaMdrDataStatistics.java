package harvesterUI.shared.mdr;

import harvesterUI.shared.dataTypes.dataSet.SimpleDataSetInfo;

import java.util.List;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 29-11-2012
 * Time: 15:06
 */
public class SchemaMdrDataStatistics extends MdrDataStatistics {

    private int _nUsedInTransformations;
    private List<TransformationUI> _usedInTransformationsList;

    public SchemaMdrDataStatistics() {
        super();
    }

    public SchemaMdrDataStatistics(int nUsedInDataSets, List<SimpleDataSetInfo> usedInDataSetsList,
                                   int nUsedInTransformations, List<TransformationUI> usedInTransformationsList) {
        super(nUsedInDataSets, usedInDataSetsList);
        _nUsedInTransformations = nUsedInTransformations;
        _usedInTransformationsList = usedInTransformationsList;
    }

    public int getNumberTimesUsedInTransformations() {
        return _nUsedInTransformations;
    }

    public List<TransformationUI> getUsedInTransformationsList() {
        return _usedInTransformationsList;
    }

}
