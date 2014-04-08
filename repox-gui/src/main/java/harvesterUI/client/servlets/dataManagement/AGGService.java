/*
 * Ext GWT 2.2.1 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package harvesterUI.client.servlets.dataManagement;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import harvesterUI.shared.dataTypes.AggregatorUI;
import harvesterUI.shared.dataTypes.SaveDataResponse;
import harvesterUI.shared.ServerSideException;

import java.util.List;

@RemoteServiceRelativePath("aggregatorsService")
public interface AGGService extends RemoteService {

    public SaveDataResponse saveAggregator(boolean update,AggregatorUI aggregatorUI, int pageSize) throws ServerSideException;
    public String deleteAggregators(List<AggregatorUI> aggregatorUIs) throws ServerSideException;

}
