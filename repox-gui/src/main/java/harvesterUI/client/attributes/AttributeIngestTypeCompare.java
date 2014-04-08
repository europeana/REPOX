//package harvesterUI.client.attributes;
//
//import com.extjs.gxt.ui.client.data.BaseTreeModel;
//import com.extjs.gxt.ui.client.data.ModelData;
//import harvesterUI.client.models.AttributeIngestType;
//import harvesterUI.client.models.AttributeValue;
//import harvesterUI.client.panels.browse.IngestTypeFilter;
//import harvesterUI.shared.dataTypes.DataProviderUI;
//import harvesterUI.shared.dataTypes.dataSet.DataSourceUI;
//import harvesterUI.shared.tasks.OldTaskUI;
//import harvesterUI.shared.tasks.ScheduledTaskUI;
//
///**
// * Created to REPOX.
// * User: Edmundo
// * Date: 29-04-2011
// * Time: 10:28
// */
//public class AttributeIngestTypeCompare {
//
//    public boolean isSameIngestType(AttributeIngestType attribute, BaseTreeModel tree)
//    {
//        boolean result = true;
//        IngestTypeFilter ingestTypeFilter = (IngestTypeFilter) attribute.getMenu();
//
//        DataSourceUI ds = null;
//        if(tree instanceof DataSourceUI)
//            ds = (DataSourceUI) tree;
//        else if(tree instanceof DataProviderUI && ((DataProviderUI)tree).getDataSourceUIList().size() >0)
//            ds = ((DataProviderUI)tree).getDataSourceUIList().get(0);
//        else
//            result = false;
//
//        if(ds != null) {
//            for (ModelData typeMD : ingestTypeFilter.getIngestTypesList()) {
//                AttributeValue type = (AttributeValue) typeMD;
//                if(type.getAttributeName().equals("none")) {
//                    if(ds.getScheduledTasks().size() == 0) {
//                        result = true;
//                        return result;
//                    } else result = false;
//                } else if(type.getAttributeName().equals("partialIngest")) {
//                    for(OldTaskUI oldTaskUI : ds.getOldTasks()) {
//                        if(oldTaskUI.getIngestType().equals("partialIngest")) {
//                            result = true;
//                            return result;
//                        }
//                    }
//                    result = false;
//                } else {
//                    for(ScheduledTaskUI scheduledTaskUI : ds.getScheduledTasks()) {
//                        String convertedType;
//                        if(type.getAttributeName().equals("incrementalIngest"))
//                            convertedType = "false";
//                        else
//                            convertedType = "true";
//
//                        if(scheduledTaskUI.getFullIngest().equals(convertedType)) {
//                            result = true;
//                            return result;
//                        }
//                    }
//                    result = false;
//                }
//            }
//        }
//        return result;
//    }
//}
