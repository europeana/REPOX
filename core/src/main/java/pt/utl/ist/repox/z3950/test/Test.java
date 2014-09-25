package pt.utl.ist.repox.z3950.test;

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

import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.dataProvider.DefaultDataManager;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.repox.dataProvider.dataSource.IdGeneratedRecordIdPolicy;
import pt.utl.ist.repox.marc.CharacterEncoding;
import pt.utl.ist.repox.z3950.*;
import pt.utl.ist.util.date.DateUtil;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.util.exceptions.task.IllegalFileFormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

/**
 */
public class Test {
    private enum HarvestType {
        timestamp, idList, idSequence
    }

    // harvester.setIdBibAttribute("14");
    // harvester.setIdBibAttribute("1007");

    Searchable          currentSearchable = null;
    Z3950ServiceFactory factory;
    ApplicationContext  appContext;

    private Target      target;
    private Date        earliestTimestamp;
    private File        idListFile;
    private Long        maxId;

    /**
     * Creates a new instance of this class.
     * @param target
     * @param earliestTimestamp
     * @param idListFile
     * @param maxId
     */
    public Test(Target target, Date earliestTimestamp, File idListFile, Long maxId) {
        super();
        this.target = target;
        this.earliestTimestamp = earliestTimestamp;
        this.idListFile = idListFile;
        this.maxId = maxId;
    }

    /**
     * @param harvestType
     * @return HarvestMethod depending of harvestType
     */
    public HarvestMethod getHarvestMethod(HarvestType harvestType) {
        switch (harvestType) {
        case timestamp:
            return new TimestampHarvester(target, earliestTimestamp);
        case idList:
            return new IdListHarvester(target, idListFile);
        case idSequence:
            return new IdSequenceHarvester(target, maxId);

        default:
            throw new RuntimeException("Unknown Harvest Type");
        }
    }

    /**
     * @param harvestMethod
     * @return DataSourceZ3950
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalFileFormatException
     * @throws ParseException
     * @throws AlreadyExistsException
     */
    public DataSourceZ3950 createDummyDataSource(HarvestMethod harvestMethod) throws IOException, DocumentException, SQLException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, ParseException, AlreadyExistsException {

        HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();

        DataProvider dummyDP = new DataProvider("tempDP", "tempDP", null, "temporary Data Provider - delete", dataSourceContainers);
        DataSourceZ3950 dataSourceZ3950 = new DataSourceZ3950(dummyDP, "tempZ3950", "tempZ3950", "", "", harvestMethod, new IdGeneratedRecordIdPolicy(), null);

        dataSourceContainers.put(dataSourceZ3950.getId(), new DefaultDataSourceContainer(dataSourceZ3950));

        ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager()).addDataProvider(dummyDP);
        dataSourceZ3950.initAccessPoints();
        ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getAccessPointsManager().initialize(dummyDP.getDataSourceContainers());

        return dataSourceZ3950;
    }

    private void deleteDummyDataSource(DataSourceZ3950 dataSourceZ3950) throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, ObjectNotFoundException {
        DataProvider dataProviderParent = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSourceZ3950.getId());
        String dataProviderId = dataProviderParent.getId();
        ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getDataManager().deleteDataProvider(dataProviderId);
    }

    /**
     * @param args
     * @throws ParseException
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IllegalFileFormatException
     * @throws NoSuchMethodException
     * @throws AlreadyExistsException
     */
    public static void main(String[] args) throws ParseException, IOException, DocumentException, SQLException, ClassNotFoundException, IllegalFileFormatException, NoSuchMethodException, AlreadyExistsException {
        //        Target target = new Target("roze.lanet.lv", 9991, "lnc04", "z39_lnc04", "_zlnc04__", CharacterEncoding.UTF_8, "usmarc");
        //        Target target = new Target(1, "porbase.bnportugal.pt", 210, "porbase", "", "", "", "unimarc");
        //        Target target = new Target("porbase.bnportugal.pt", 210, "porbase", "", "", CharacterEncoding.ISO_5426, "unimarc");
        //        Target target = new Target("z3950.porbase.org", 21000, "bnd", "", "", "ISO8859-1", "unimarc");
        //        Target target = new Target("193.6.201.205", 1616, "B1", "LIBRI", "vision98", "ANSEL", "usmarc");
        //        Target target = new Target("aleph.lbfl.li", 9909, "LLB_IDS", "", "", CharacterEncoding.UTF_8, "usmarc");

        //		  193.6.201.205  1616 B1 LIBRI vision98 ANSEL usmarc f:/dreis/Desktop/Z39.50Harvester2/HungaryIdList.txt
        //
        //Date earliestTimestamp = DateUtil.string2Date("20090401", "yyyyMMdd");
        Date earliestTimestamp = DateUtil.string2Date("20110301", "yyyyMMdd");
        File idListFile = new File("C:\\Users\\Toshiba\\Desktop\\Novo Documento de Texto.txt");
        //File idListFile = new File("C:\\Users\\Toshiba\\Desktop\\nuno\\1900028192z3950idList.txt");
        Long maxId = (long)5000;

        Target target = new Target("193.6.201.205", 1616, "B1", "LIBRI", "vision98", CharacterEncoding.UTF_8, "usmarc");
        Test test = new Test(target, earliestTimestamp, idListFile, maxId);
        DataSourceZ3950 dataSourceZ3950 = test.createDummyDataSource(test.getHarvestMethod(HarvestType.idList));
        File logFile = new File("C:\\Users\\Toshiba\\Desktop\\log.log");

        //dataSourceZ3950.ingestRecords(logFile, false);

        BufferedReader reader = new BufferedReader(new FileReader(idListFile));
        String recordId = reader.readLine();

        test.init();

        recordId = recordId.replace('\"', ' ');

        int currentId = 0;

        while (recordId != null) {
            String queryStr = "@attrset bib-1 " + "@attr 1=" + "12" + " \"" + recordId + "\"";

            currentId++;
            System.out.println(currentId);

            IRResultSet results = test.runQuery(queryStr);

            Enumeration<InformationFragment> currentInformationFragment = new org.jzkit.search.util.ResultSet.ReadAheadEnumeration(results);

            currentInformationFragment.nextElement().getOriginalObject();
            //Record nextRecord = new Record("sada", null);

            //System.out.println("nextRecord = " + nextRecord.getNc());
            System.out.println("status = " + results.getStatus());

            recordId = reader.readLine();
        }

        System.exit(0);

        /*Target target = new Target("roze.lanet.lv", 9991, "nll01", "z39_nll01", "_znll01__", CharacterEncoding.UTF_8,"usmarc");
        Test test = new Test(target, earliestTimestamp, idListFile, maxId);
        DataSourceZ3950 dataSourceZ3950 = test.createDummyDataSource(test.getHarvestMethod(HarvestType.timestamp));
        File logFile = new File("C:\\Users\\Toshiba\\Desktop\\log.log");
        dataSourceZ3950.ingestRecords(logFile, false);
        System.exit(0);*/

        /*Target target = new Target("roze.lanet.lv", 9991, "nll01", "z39_nll01", "_znll01__", CharacterEncoding.UTF_8, "usmarc");
        Test test = new Test(target, null, null, 40000);
        DataSourceZ3950 dataSourceZ3950 = test.createDummyDataSource(test.getHarvestMethod(HarvestType.idSequence));
        File logFile = new File("C:\\Users\\Toshiba\\Desktop\\log.log");
        dataSourceZ3950.ingestRecords(logFile, false);
        System.exit(0);*/

    }

    /**
     * 
     */
    public void init() {
        connect();
    }

    /**
     * 
     */
    protected void connect() {
        appContext = new ClassPathXmlApplicationContext("ZHarvesterApplicationContext.xml");

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
     * @param queryString
     * @return IRResultSet
     */
    protected IRResultSet runQuery(String queryString) {
        int queryRetries = 3;

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
                continue;
            } catch (SearchException ex) {
                currentSearchable = null;
                continue;
            }
            if (result != null && result.getStatus() != IRResultSetStatus.FAILURE)
                return result;

            else if (result != null && result.getStatus() == IRResultSetStatus.FAILURE)
                System.out.println("Search failure: " + result.getResultSetInfo());
            else
                System.out.println("Search failure - uknown error");
        }
        return null;
    }

}
