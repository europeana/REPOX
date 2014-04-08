package harvesterUI.shared.search;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 19-09-2012
 * Time: 11:23
 */
public class EuropeanaSearchResult extends BaseSearchResult {

    public EuropeanaSearchResult() {
    }

    public EuropeanaSearchResult(String id, String name, String nameCode, String description, String dataSet, String dataType) {
        super(id, name, description, dataSet, dataType);
        set("nameCode",nameCode);
    }
}
