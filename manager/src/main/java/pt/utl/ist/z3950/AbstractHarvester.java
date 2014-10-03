package pt.utl.ist.z3950;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.provider.iface.SearchException;
import org.jzkit.search.provider.iface.Searchable;
import org.jzkit.search.provider.z3950.SimpleZAuthenticationMethod;
import org.jzkit.search.provider.z3950.Z3950ServiceFactory;
import org.jzkit.search.util.RecordModel.InformationFragment;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.jzkit.search.util.ResultSet.IRResultSetException;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.StringUtil;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 */
public abstract class AbstractHarvester implements Harvester {
    /**
     * Logger for this class
     */
    private static final Logger   log          = Logger.getLogger(AbstractHarvester.class);

    protected ApplicationContext  appContext;
    protected Z3950ServiceFactory factory;
    protected Searchable          currentSearchable;
    protected Target              target;
    protected int                 queryRetries = 3;

    @Override
    public Target getTarget() {
        return target;
    }

    @Override
    public void setTarget(Target target) {
        this.target = target;
    }

    /**
     * Creates a new instance of this class.
     * @param target
     */
    protected AbstractHarvester(Target target) {
        this.target = target;
        appContext = new ClassPathXmlApplicationContext("ZHarvesterApplicationContext.xml");
        if (appContext == null) throw new RuntimeException("Unable to locate TestApplicationContext.xml definition file");
    }

    /**
     * 
     */
    protected void connect() {
        factory = new Z3950ServiceFactory(target.getAddress(), target.getPort());
        factory.setApplicationContext(appContext);
        factory.setDefaultRecordSyntax(target.getRecordSyntax());
        factory.setDefaultElementSetName("F");
        //	        factory.getRecordArchetypes().put("Default","sutrs:Resource:F");
        if (target.getUser() != null && !target.getUser().equals("")) {
            SimpleZAuthenticationMethod auth_method = new SimpleZAuthenticationMethod(3, target.getUser(), null, target.getPassword());
            factory.setAuthMethod(auth_method);
        }
    }

    /**
     * 
     */
    protected void close() {
        if (currentSearchable != null) {
            try {
                currentSearchable.close();
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    /**
     * @param queryString
     * @param logFile
     * @param dataSetId
     * @return IRResultSet
     */
    protected IRResultSet runQuery(String queryString, File logFile, String dataSetId) {
        log.info(queryString);
        int tries = 0;
        while (tries < queryRetries) {
            tries++;

            IRQuery query = new IRQuery();
            query.collections.add(target.getDatabase());
            query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString(queryString);

            IRResultSet result;
            try {
                if (currentSearchable == null) {
                    currentSearchable = factory.newSearchable();
                    currentSearchable.setApplicationContext(appContext);
                }
                result = currentSearchable.evaluate(query);

                result.waitForStatus(IRResultSetStatus.COMPLETE | IRResultSetStatus.FAILURE, 30000);

                //	    		log.info(result.getFragmentCount());
            } catch (IRResultSetException ex) {
                log.info(ex.getMessage(), ex);
                continue;
            } catch (SearchException ex) {
                log.info(ex.getMessage(), ex);
                currentSearchable = null;
                continue;
            }

            try {
                if (result != null && result.getStatus() != IRResultSetStatus.FAILURE)
                    return result;
                else if (result != null && result.getStatus() == IRResultSetStatus.FAILURE) {
                    StringUtil.simpleLog("Search failure: " + result.getResultSetInfo(), this.getClass(), logFile);
                    //                log.info("Search failure: "+result.getResultSetInfo());
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().stopIngestDataSource(dataSetId, Task.Status.CANCELED);
                } else {
                    //                log.info("Search failure - uknown error");
                    StringUtil.simpleLog("Search failure: - Unknown Error", this.getClass(), logFile);
                    ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().stopIngestDataSource(dataSetId, Task.Status.CANCELED);
                }
            } catch (IOException e) {
                e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
            } catch (ObjectNotFoundException e) {
                e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
            } catch (NoSuchMethodException e) {
                e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
            } catch (ParseException e) {
                e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
            } catch (ClassNotFoundException e) {
                e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
            } catch (DocumentException e) {
                e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return null;
    }

    /**
     * @param frag
     * @return Record
     */
    protected MarcRecord handleRecord(InformationFragment frag) {
        if (frag != null) {
            byte[] originalObject = (byte[])frag.getOriginalObject();
            return new MarcRecord(originalObject, target.getCharacterEncoding().toString());
        } else {
            System.out.println("frag = " + frag);
            return null;
        }
    }

    @Override
    public void init() {
        connect();
    }

    @Override
    public void cleanup() {
        close();
    }
}
