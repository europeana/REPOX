package harvesterUI.server.util;

import pt.utl.ist.statistics.MetadataFormatStatistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created to Project REPOX
 * User: Edmundo
 * Date: 08-01-2013
 * Time: 15:28
 */
public class StatisticsUtil {

    public static Map<String, Integer> getMetadataFormatStatistics(Map<String, MetadataFormatStatistics> metadataFormatStatisticsMap, boolean showRecords){
        Map<String, Integer> resultsStatistics = new HashMap<String, Integer>();
        if(showRecords){
            for (Map.Entry<String, MetadataFormatStatistics> currentFormat : metadataFormatStatisticsMap.entrySet()) {
                resultsStatistics.put(currentFormat.getKey(),currentFormat.getValue().getRecordNumber());
            }
            return resultsStatistics;
        }else{
            for (Map.Entry<String, MetadataFormatStatistics> currentFormat : metadataFormatStatisticsMap.entrySet()) {
                resultsStatistics.put(currentFormat.getKey(),currentFormat.getValue().getCollectionNumber());
            }
            return resultsStatistics;
        }
    }
}
