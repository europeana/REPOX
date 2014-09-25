package harvesterUI.server.dataManagement.dataSets;

import harvesterUI.client.servlets.dataManagement.TagsService;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.dataTypes.dataSet.DataSetTagUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.ArrayList;
import java.util.List;

import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.dataProvider.MessageType;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TagsServiceImpl extends RemoteServiceServlet implements TagsService {

    private static TagsStatisticsManager tagsStatisticsManager;

    public TagsServiceImpl() {
        tagsStatisticsManager = new TagsStatisticsManager();
    }

    public PagingLoadResult<DataSetTagUI> getPagedTags(FilterPagingLoadConfig config) throws ServerSideException{
        List<DataSetTagUI> dataSetTagUIs = RepoxDataExchangeManager.getAllParsedTags(tagsStatisticsManager);

//        if (config.getSortInfo().getSortField() != null) {
//            final String sortField = config.getSortInfo().getSortField();
//            if (sortField != null) {
//                Collections.sort(transformationUIList, config.getSortInfo().getSortDir().comparator(new Comparator<TransformationUI>() {
//                    public int compare(TransformationUI p1, TransformationUI p2) {
//                        if (sortField.equals("identifier")) {
//                            return p1.getIdentifier().compareTo(p2.getIdentifier());
//                        } else if (sortField.equals("srcFormat")) {
//                            return p1.getSrcFormat().compareTo(p2.getSrcFormat());
//                        } else if (sortField.equals("destFormat")) {
//                            return p1.getDestFormat().compareTo(p2.getDestFormat());
//                        } else if (sortField.equals("usage")) {
//                            int used1 = p1.getMdrDataStatistics().getNumberTimesUsedInDataSets();
//                            int used2 = p2.getMdrDataStatistics().getNumberTimesUsedInDataSets();
//
//                            if (used1 > used2)
//                                return 1;
//                            else if (used1 < used2)
//                                return -1;
//                            else
//                                return 0;
//                        }
//                        return 0;
//                    }
//                }));
//            }
//        }

        ArrayList<DataSetTagUI> sublist = new ArrayList<DataSetTagUI>();
        int start = config.getOffset();
        int limit = dataSetTagUIs.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(dataSetTagUIs.get(i));
        }
        return new BasePagingLoadResult<DataSetTagUI>(sublist, config.getOffset(), dataSetTagUIs.size());
    }

    public List<DataSetTagUI> getAllTags() throws ServerSideException {
        try {
            return RepoxDataExchangeManager.getAllParsedTags(tagsStatisticsManager);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ResponseState removeTag(List<DataSetTagUI> dataSetTagUIs) throws ServerSideException{
        try {
            MessageType result = null;
            for(DataSetTagUI dataSetTagUI : dataSetTagUIs){
                result = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getTagsManager().removeTag(dataSetTagUI.getName());
            }

            if(result != null && result != MessageType.OK)
                return ResponseState.ERROR;
            else
                return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ResponseState saveTag(boolean isUpdate, DataSetTagUI dataSetTagUI, String oldTagName) throws ServerSideException{
        try {
            MessageType result = ConfigSingleton.getRepoxContextUtil().getRepoxManager().
                    getTagsManager().saveTag(isUpdate,dataSetTagUI.getName(),oldTagName);

            if(result == MessageType.ALREADY_EXISTS)
                return ResponseState.ALREADY_EXISTS;
            else if(result != MessageType.OK)
                return ResponseState.ERROR;
            else
                return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }
}
