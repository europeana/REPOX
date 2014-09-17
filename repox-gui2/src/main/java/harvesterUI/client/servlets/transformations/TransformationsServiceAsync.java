/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.transformations;

import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;

import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

//import harvesterUI.client.models.FilterAttributes;
//import harvesterUI.client.models.MailItem;

public interface TransformationsServiceAsync {

    public void getFullTransformationsList(AsyncCallback<List<TransformationUI>> callback);

    public void saveTransformation(TransformationUI transformationUI, String oldTransId, AsyncCallback<ResponseState> callback);
    public void deleteTransformation(List<String> transfomationIDs, AsyncCallback<String> callback);
    public void getPagedTransformations(FilterPagingLoadConfig config, AsyncCallback<PagingLoadResult<TransformationUI>> callback);
    public void validateTransformation(String id, String xslFilePath, String oldTransId, AsyncCallback<ResponseState> callback);

//    public void getMdrMappings(String schema, String metadataNamespace,AsyncCallback<List<TransformationUI>> callback);

//    public void getAllMdrMappings(AsyncCallback<List<TransformationUI>> callback);

//    public void importMdrTransformation(List<TransformationUI> transformationUIs,AsyncCallback<BaseModel> callback);
//    public void updateMdrTransformations(List<TransformationUI> transformationUIs,AsyncCallback<BaseModel> callback);

    public void getPagedSchemas(PagingLoadConfig config,AsyncCallback<List<SchemaTreeUI>> callback);
    public void getPagingData(PagingLoadConfig config, AsyncCallback<PagingLoadResult<SchemaTreeUI>> callback);
    public void getSchemasTree(AsyncCallback<List<SchemaTreeUI>> callback);
    public void deleteMetadataSchema(List<String> schemaIds,AsyncCallback<ResponseState> callback);
    public void saveMetadataSchema(SchemaUI schemaUI,String oldSchemaUIId,AsyncCallback<ResponseState> callback);
    public void getAllMetadataSchemas(AsyncCallback<List<SchemaUI>> callback);
}
