package harvesterUI.server.transformations;

import harvesterUI.client.servlets.transformations.TransformationsService;
import harvesterUI.server.dataManagement.RepoxDataExchangeManager;
import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.SchemaVersionUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pt.utl.ist.repox.dataProvider.MessageType;
import pt.utl.ist.repox.metadataSchemas.MetadataSchemaVersion;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.SameStylesheetTransformationException;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TransformationsServiceImpl extends RemoteServiceServlet implements TransformationsService {

    private static MdrStatisticsManager mdrStatisticsManager;
    private int _total = -1;
    private List<Object> _schemaAndVersions;

    public TransformationsServiceImpl() {
        mdrStatisticsManager = new MdrStatisticsManager();
    }

    /****************************
     *      PUBLIC METHODS      *
     ****************************/

    public static MdrStatisticsManager getTransformationMatcher() {
        return mdrStatisticsManager;
    }

    public List<TransformationUI> getFullTransformationsList() throws ServerSideException{
        return RepoxDataExchangeManager.getFullTransformationsList(mdrStatisticsManager);
    }

    public PagingLoadResult<TransformationUI> getPagedTransformations(FilterPagingLoadConfig config) throws ServerSideException{
        List<TransformationUI> transformationUIList = getFullTransformationsList();

        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            if (sortField != null) {
                Collections.sort(transformationUIList, config.getSortInfo().getSortDir().comparator(new Comparator<TransformationUI>() {
                    public int compare(TransformationUI p1, TransformationUI p2) {
                        if (sortField.equals("identifier")) {
                            return p1.getIdentifier().compareTo(p2.getIdentifier());
                        } else if (sortField.equals("srcFormat")) {
                            return p1.getSrcFormat().compareTo(p2.getSrcFormat());
                        } else if (sortField.equals("destFormat")) {
                            return p1.getDestFormat().compareTo(p2.getDestFormat());
                        } else if (sortField.equals("usage")) {
                            int used1 = p1.getMdrDataStatistics().getNumberTimesUsedInDataSets();
                            int used2 = p2.getMdrDataStatistics().getNumberTimesUsedInDataSets();

                            if(used1 > used2)
                                return 1;
                            else if(used1 < used2)
                                return -1;
                            else
                                return 0;
                        }
                        return 0;
                    }
                }));
            }
        }

        ArrayList<TransformationUI> temp = new ArrayList<TransformationUI>();
        ArrayList<TransformationUI> remove = new ArrayList<TransformationUI>();
        for (TransformationUI s : transformationUIList) {
            temp.add(s);
        }

        List<FilterConfig> filters = config.getFilterConfigs();
        for (FilterConfig f : filters) {
            String type = f.getType();
            String test = (String)f.getValue();
            String path = f.getField();
            String comparison = f.getComparison();

            String safeTest = test == null ? "" : test.toString();

            for (TransformationUI s : transformationUIList) {
                String value = getTransformationValue(s, path);
                String safeValue = value == null ? null : value.toString();

                if (safeTest.length() == 0 && (safeValue == null || safeValue.length() == 0)) {
                    continue;
                } else if (safeValue == null) {
                    remove.add(s);
                    continue;
                }

                if ("string".equals(type)) {
                    if (safeValue.toLowerCase().indexOf(safeTest.toLowerCase()) == -1) {
                        remove.add(s);
                    }
                }
//                else if ("date".equals(type)) {
//                    if (isDateFiltered(safeTest, comparison, safeValue)) {
//                        remove.add(s);
//                    }
//                } else if ("boolean".equals(type)) {
//                    if (isBooleanFiltered(safeTest, comparison, safeValue)) {
//                        remove.add(s);
//                    }
//                } else if ("list".equals(type)) {
//                    if (isListFiltered(safeTest, safeValue)) {
//                        remove.add(s);
//                    }
//                } else if ("numeric".equals(type)) {
//                    if (isNumberFiltered(safeTest, comparison, safeValue)) {
//                        remove.add(s);
//                    }
//                }
            }
        }

        for (TransformationUI s : remove) {
            temp.remove(s);
        }

        ArrayList<TransformationUI> sublist = new ArrayList<TransformationUI>();
        int start = config.getOffset();
        int limit = temp.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(temp.get(i));
        }
        return new BasePagingLoadResult<TransformationUI>(sublist, config.getOffset(), temp.size());
    }

    public ResponseState validateTransformation(String id, String xslFilePath, String oldTransId) throws ServerSideException {
        try {
//            if(!TransformationFileUpload.isCopySuccessful())
//                return ResponseState.ERROR_SAVING_XSL;

//            if(!oldTransId.isEmpty())           //TODO Possible bug when changing the id or file of an existing Transf.
//                return ResponseState.SUCCESS;

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    checkTransformationValidity(id, xslFilePath, oldTransId);

            return ResponseState.SUCCESS;
        } catch (SameStylesheetTransformationException e) {
            return ResponseState.MAPPING_SAME_XSL;
        }catch (AlreadyExistsException e) {
            return ResponseState.ALREADY_EXISTS;
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    // Schema mapper
    public ResponseState saveTransformation(TransformationUI transformationUI,String oldTransId) throws ServerSideException{
        try {
            String xslFilePath = transformationUI.getXslFilePath().toLowerCase();

            if(!TransformationFileUpload.isCopySuccessful())
                return ResponseState.ERROR_SAVING_XSL;

            //Check if there was a file uploaded in this process, if yes the transformation is no longer editable in the XMApper.
            Boolean isEditable = TransformationFileUpload.isFileUploaded() ? false : transformationUI.isEditable();

            MetadataTransformation mtdTransformation = new MetadataTransformation(transformationUI.getIdentifier(),
                    transformationUI.getDescription(),transformationUI.getSrcFormat(),
                    transformationUI.getDestFormat(),xslFilePath,
                    isEditable ,transformationUI.getIsXslVersion2(),transformationUI.getDestSchema(),transformationUI.getDestMetadataNamespace());
            mtdTransformation.setSourceSchema(transformationUI.getSourceSchema());
            mtdTransformation.setMDRCompliant(transformationUI.isMDRCompliant());

            //If a file was uploaded, then erase its old files
            if(TransformationFileUpload.isFileUploaded())
                    mtdTransformation.setDeleteOldFiles(true);

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(mtdTransformation,oldTransId);
            return ResponseState.SUCCESS;
        } catch (SameStylesheetTransformationException e) {
            return ResponseState.MAPPING_SAME_XSL;
        }catch (AlreadyExistsException e) {
            return ResponseState.ALREADY_EXISTS;
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public String deleteTransformation(List<String> transfomationIDs) throws ServerSideException{
        try {
            for(String transformationID : transfomationIDs){
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                        deleteMetadataTransformation(transformationID);
            }
            return "SUCCESS";
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public static String forURL(String aURLFragment){
        String result = null;
        try {
            result = URLEncoder.encode(aURLFragment, "UTF-8");
        }
        catch (UnsupportedEncodingException ex){
            throw new RuntimeException("UTF-8 not supported", ex);
        }
        return result;
    }

    public List<SchemaTreeUI> getSchemasTree() throws ServerSideException {
        return getSchemasTreeRaw();
    }

    public static List<SchemaTreeUI> getSchemasTreeRaw() throws ServerSideException{
        List<SchemaUI> schemaUIList = RepoxDataExchangeManager.convertRepoxSchemas(mdrStatisticsManager);

        List<SchemaTreeUI> schemaTreeUIs = new ArrayList<SchemaTreeUI>();

        for(SchemaUI schemaUI : schemaUIList){
            schemaUI.createTreeChildren();
        }

        schemaTreeUIs.addAll(schemaUIList);

        return schemaTreeUIs;
    }

    public List<SchemaTreeUI> getPagedSchemas(PagingLoadConfig config) throws ServerSideException {
        //if(_schemaAndVersions == null)
        getSchemaAndVersionsList();

        int start = config.getOffset();
        int softLimit = config.getLimit();
        int limit = _schemaAndVersions.size();

        int realLimit = Math.min(softLimit, limit);

        List<SchemaUI> pagedSchemaUIList = new ArrayList<SchemaUI>();

        //All available rows-1
        SchemaUI schema = null;
        for(int i = start; i < realLimit-1; i++){
            Object tmp = _schemaAndVersions.get(i);

            if(tmp instanceof  SchemaUI){
                schema = (SchemaUI) tmp;
                pagedSchemaUIList.add(schema);
            }
            if(tmp instanceof SchemaVersionUI){
                if(schema == null){
                    schema = getPreviousSchema(i);
                    pagedSchemaUIList.add(schema);
                }
                schema.add((SchemaVersionUI)tmp);
            }
        }
        //last row
        if(schema != null) {
            Object tmp = _schemaAndVersions.get(realLimit-1);
            if(tmp instanceof SchemaVersionUI)
                schema.add((SchemaVersionUI)tmp);
        }


        List<SchemaTreeUI> schemaTreeUIs = new ArrayList<SchemaTreeUI>();
        schemaTreeUIs.addAll(pagedSchemaUIList);
        return schemaTreeUIs;
    }


    public PagingLoadResult<SchemaTreeUI> getPagingData(PagingLoadConfig config) throws ServerSideException {
        try{
            int showSize = getTotalLength();
            return new BasePagingLoadResult<SchemaTreeUI>(null, config.getOffset(), showSize);
        } catch (Exception e) {
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public List<SchemaUI> getAllMetadataSchemas() throws ServerSideException{
        return RepoxDataExchangeManager.convertRepoxSchemas(mdrStatisticsManager);
    }

    public ResponseState saveMetadataSchema(SchemaUI schemaUI,String oldSchemaUIId) throws ServerSideException{
        try {
            List<MetadataSchemaVersion> metadataSchemaVersions = new ArrayList<MetadataSchemaVersion>();
            for(SchemaVersionUI schemaVersionUI : schemaUI.getSchemaVersions()){
                metadataSchemaVersions.add(new MetadataSchemaVersion(schemaVersionUI.getVersion(),schemaVersionUI.getXsdLink()));
            }

            MessageType messageType = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                    saveMetadataSchema(schemaUI.getDesignation(),schemaUI.getShortDesignation(),
                            schemaUI.getDescription(),schemaUI.getNamespace(),
                            schemaUI.getNotes(),oldSchemaUIId,metadataSchemaVersions, schemaUI.isOAIAvailable());

            if(messageType == MessageType.ALREADY_EXISTS)
                return ResponseState.ALREADY_EXISTS;
            else
                return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public ResponseState deleteMetadataSchema(List<String> schemaIds) throws ServerSideException{
        try {
            for(String schemaID : schemaIds){
                ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager().
                        deleteMetadataSchema(schemaID);
            }
            return ResponseState.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    public static void main(String[] args){
//        try {
//            TransformationsServiceImpl.testMDrServices();
//        } catch (ServerSideException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
    }

    /****************************
     *      PRIVATE METHODS      *
     ****************************/

    private String getTransformationValue(TransformationUI transformation, String property) {
        if (property.equals("description")) {
            return transformation.getDescription();
        } else if (property.equals("srcFormat")) {
            return transformation.getSrcFormat();
        } else if (property.equals("destFormat")) {
            return transformation.getDestFormat();
        } else if (property.equals("dsStringFormat")) {
            return transformation.getDSStringFormat();
        } else if (property.equals("schema")) {
            return transformation.getDestSchema();
        }else if (property.equals("mtdNamespace")) {
            return transformation.getDestMetadataNamespace();
        }
        return "";
    }

    // METADATA SCHEMA PART

    private int getTotalLength() throws ServerSideException {
        if(!(_total < 0))
            return _total;
        int tot = 0;
        List<SchemaUI> schemaUIList = RepoxDataExchangeManager.convertRepoxSchemas(mdrStatisticsManager);

        for(SchemaUI schemaUI : schemaUIList){
            String db = schemaUI.getDesignation();
            tot++;
            tot += schemaUI.getVersionsCount();
        }
        _total = tot;
        return tot;
    }

    private List<Object> getSchemaAndVersionsList() throws ServerSideException {
//        if(_schemaAndVersions != null)
//            return  _schemaAndVersions;

        _schemaAndVersions = new ArrayList<Object>();
        List<SchemaUI> schemaUIList = RepoxDataExchangeManager.convertRepoxSchemas(mdrStatisticsManager);

        for(SchemaUI schema : schemaUIList) {
            _schemaAndVersions.add(schema);
            for(SchemaVersionUI version : schema.getSchemaVersions()){
                _schemaAndVersions.add(version);
            }
        }

        return _schemaAndVersions;
    }

    private SchemaUI getPreviousSchema(int index) {
        for(int i = index-1; i >= 0; i--){
            Object test = _schemaAndVersions.get(i);
            if(test instanceof SchemaUI)
                return (SchemaUI) test;
        }
        return null;
    }



    /*public PagingLoadResult<SchemaUI> getPagedSchemas(PagingLoadConfig config) throws ServerSideException{
        List<SchemaUI> schemaUIList = RepoxDataExchangeManager.convertRepoxSchemas(mdrStatisticsManager);

        if (config.getSortInfo().getSortField() != null) {
            final String sortField = config.getSortInfo().getSortField();
            if (sortField != null) {
                Collections.sort(schemaUIList, config.getSortInfo().getSortDir().comparator(new Comparator<SchemaUI>() {
                    public int compare(SchemaUI p1, SchemaUI p2) {
                        if (sortField.equals("shortDesignation")) {
                            return p1.getShortDesignation().compareTo(p2.getShortDesignation());
                        } else if (sortField.equals("designation")) {
                            return p1.getDesignation().compareTo(p2.getDesignation());
                        }
//                        else if (sortField.equals("xsdLink")) {
//                            return p1.getXsdLink().compareTo(p2.getXsdLink());
//                        }
                        else if (sortField.equals("namespace")) {
                            return p1.getNamespace().compareTo(p2.getNamespace());
                        }
                        return 0;
                    }
                }));
            }
        }
        ArrayList<SchemaUI> sublist = new ArrayList<SchemaUI>();
        int start = config.getOffset();
        int limit = schemaUIList.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(schemaUIList.get(i));
        }
        return new BasePagingLoadResult<SchemaUI>(sublist, config.getOffset(), schemaUIList.size());
    }*/


}
