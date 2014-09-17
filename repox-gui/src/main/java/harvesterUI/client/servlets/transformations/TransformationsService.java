/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.transformations;

import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.mdr.SchemaTreeUI;
import harvesterUI.shared.mdr.SchemaUI;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.util.List;

import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("transformationsService")
public interface TransformationsService extends RemoteService {

    public List<TransformationUI> getFullTransformationsList() throws ServerSideException;
    public ResponseState saveTransformation(TransformationUI transformationUI, String oldTransId) throws ServerSideException;
    public String deleteTransformation(List<String> transfomationIDs) throws ServerSideException;
    public PagingLoadResult<TransformationUI> getPagedTransformations(FilterPagingLoadConfig config) throws ServerSideException;
    public ResponseState validateTransformation(String id, String xslFilePath, String oldTransId) throws ServerSideException ;

    public List<SchemaTreeUI> getPagedSchemas(PagingLoadConfig config) throws ServerSideException;
    public PagingLoadResult<SchemaTreeUI> getPagingData(PagingLoadConfig config) throws ServerSideException;
    public List<SchemaTreeUI> getSchemasTree() throws ServerSideException;
    public ResponseState deleteMetadataSchema(List<String> schemaIds) throws ServerSideException;
    public ResponseState saveMetadataSchema(SchemaUI schemaUI,String oldSchemaUIId) throws ServerSideException;
    public List<SchemaUI> getAllMetadataSchemas() throws ServerSideException;
}
