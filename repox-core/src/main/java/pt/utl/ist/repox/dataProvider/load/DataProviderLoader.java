package pt.utl.ist.repox.dataProvider.load;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.dataProvider.DataProvider;
import pt.utl.ist.repox.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.dataProvider.DataSourceContainerDefault;
import pt.utl.ist.repox.dataProvider.dataSource.IdProvided;
import pt.utl.ist.repox.oai.DataSourceOai;
import pt.utl.ist.repox.util.ConfigSingleton;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class DataProviderLoader {
    private static final Logger log = Logger.getLogger(DataProviderLoader.class);


    public List<DataProvider> loadDataProvidersFromREPOX(File dataProvidersFile) throws DocumentException, IOException, ParseException {
        return ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataProvidersFromFile(dataProvidersFile, new File(ConfigSingleton.getRepoxContextUtil().getRepoxManager().getConfiguration().getRepositoryPath()));
    }



/* TEL EXAMPLE
 * <dataProviders xmlns="http://repox.ist.utl.pt/schemas/dataProviders" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://repox.ist.utl.pt/schemas/dataProviders repox_data_providers.xsd ">
  <dataProvider id="TEL" name="The European Library" country="-">
    <dataSource id="a0078" name="Kramerius - 19th and 20th century digitised periodicals and monographs from the Czech Republic">
      <oaiPmhDataSource>
        <baseUrl>http://kramerius.nkp.cz/kramerius/oai?metadataPrefix=oai_dc&amp;</baseUrl>
        <fullHarvestPeriodicity></fullHarvestPeriodicity>
        <IncrementalHarvestPeriodicity></IncrementalHarvestPeriodicity>
      </oaiPmhDataSource>
      <export>
        <fileSystemLocation>/solr/collections/a0078</fileSystemLocation>
        <periodicity></periodicity>
      </export>
    </dataSource>
 */

    public List<DataProvider> loadDataProvidersFromTEL(File dataProvidersFile) throws DocumentException, IOException {
        List<DataProvider> dataProviders = new ArrayList<DataProvider>();

        if(!dataProvidersFile.exists()) {
            return dataProviders;
        }

        SAXReader reader = new SAXReader();
        Document document = reader.read(dataProvidersFile);

        //List dataProviderElements = document.getRootElement().elements("dataProvider");
        List dataProviderElements = document.getRootElement().elements("provider");
        if(!dataProviderElements.isEmpty()) {
            for (Element currentDataProviderElement : (List<Element>) dataProviderElements) {
                String providerId = currentDataProviderElement.attributeValue("id");
                String providerName = currentDataProviderElement.attributeValue("name");
                String providerCountry = currentDataProviderElement.attributeValue("country");
                if(providerCountry != null) {
                    providerCountry = providerCountry.toLowerCase();
                }
                String providerDescription = currentDataProviderElement.attributeValue("description");

                HashMap<String, DataSourceContainer> dataSourceContainers = new HashMap<String, DataSourceContainer>();

                DataProvider provider = new DataProvider(providerId, providerName, providerCountry, providerDescription, dataSourceContainers);
                //for (Element currentDataSourceElement : (List<Element>) currentDataProviderElement.elements("dataSource")) {
                for (Element currentDataSourceElement : (List<Element>) currentDataProviderElement.elements("source")) {
                    String id = currentDataSourceElement.attributeValue("id");
                    String description = currentDataSourceElement.attributeValue("name");
                    if(description == null) {
                        description = "";
                    }
                    log.debug("DESCRIPTION: " + description);

                    //Create DataSource
                    Element oaiPmhSource = currentDataSourceElement.element("oaiPmhDataSource");
                    String urlString = oaiPmhSource.elementText("baseUrl");
                    DataSourceOai dataSource = null;

                    if(urlString != null && !urlString.isEmpty()) {
                        dataSource = getDataSourceOai(urlString, provider, id, description);
                    }
                    else {

                        dataSource = new DataSourceOai(provider, id, description, "http://www.openarchives.org/OAI/2.0/oai_dc.xsd",
                                "http://www.openarchives.org/OAI/2.0/", "oai_dc", urlString, "", new IdProvided(), null);
                    }

                    dataSourceContainers.put(dataSource.getId(), new DataSourceContainerDefault(dataSource));
                }

                dataProviders.add(provider);
            }
        }

        return dataProviders;
    }

    private DataSourceOai getDataSourceOai(String urlString, DataProvider provider, String id, String description)
            throws MalformedURLException {
        TreeMap<String, String> parametersMap = new TreeMap<String, String>();
        URL newURL = new URL(urlString);
        String oaiSource = "http://" + newURL.getHost() + (newURL.getPort() != -1 ? ":" + newURL.getPort() : "") + newURL.getPath();

        String serverQuery = newURL.getQuery();
        String[] parameters = serverQuery.split("&");
        for (String parameter : parameters) {
            int separatorIndex = parameter.indexOf("=");
            if(separatorIndex > 0) {
                String key = parameter.substring(0, separatorIndex);
                String value = parameter.substring(separatorIndex + 1);

                parametersMap.put(key, value);
            }
            else {
                // do nothing, no value
            }
        }

        String metadataFormat = parametersMap.get("metadataPrefix");
        if(metadataFormat == null) {
            metadataFormat = "oai_dc";
        }
        String oaiSet = parametersMap.get("set");

        log.debug("URL: " + oaiSource);
        log.debug("Set: " + oaiSet);
        log.debug("MF: " + metadataFormat);

        return new DataSourceOai(provider, id, description, "http://www.openarchives.org/OAI/2.0/oai_dc.xsd",
                "http://www.openarchives.org/OAI/2.0/", metadataFormat, oaiSource, oaiSet, new IdProvided(), null);
    }


    public static void main(String[] args) throws Exception {
//		String[] urls = new String[] { "http://sigma.nkp.cz/OAI-script?set=NKC&amp;", "http://kramerius.nkp.cz/kramerius/oai?metadataPrefix=oai_dc&amp;", "http://www.manuscriptorium.com/Manuscriptorium/MnOai/MnOai.oai?metadataPrefix=oai_dc&amp;", "http://sigma.nkp.cz/OAI-script?set=SKC&amp;", "http://sigma.nkp.cz/OAI-script?set=CNB&amp;", "http://digar.nlib.ee/otsing/oai.jsp?metadataPrefix=oai_dc&amp;", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;set=gallica&amp;metadataPrefix=oai_dc", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:0", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:1", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:2", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:3", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:4", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:5", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:6", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:7", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:8", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:9", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:afrique", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:amerique", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:france", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:periodiques", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:images", "http://oai.bnf.fr/oai2/OAIHandler?verb=ListRecords&amp;metadataPrefix=oai_dc&amp;set=gallica:italie", "http://mek.oszk.hu:8180/oai/oai?metadataPrefix=oai_dc&amp;", "http://oai.oszk.hu/oai?metadataPrefix=dcx&amp;set=Map&amp;", "http://oai.oszk.hu/oai?set=Corvina&amp;metadataPrefix=dcx&amp;", "http://193.225.223.20/nda.oai/oai?metadataPrefix=oai_dc&amp;", "http://ferrovia.bncf.firenze.sbn.it/Oai/servlet/OAIHandler?metadataPrefix=oai_dc&amp;set=Bertini&amp;", "http://ferrovia.bncf.firenze.sbn.it/Oai/servlet/OAIHandler?metadataPrefix=oai_dc&amp;set=Arsbni1&amp;", "http://ferrovia.bncf.firenze.sbn.it/Oai/servlet/OAIHandler?metadataPrefix=oai_dc&amp;set=Arsbni2&amp;", "http://ferrovia.bncf.firenze.sbn.it/Oai/servlet/OAIHandler?metadataPrefix=oai_dc&amp;set=Europe&amp;", "http://ferrovia.bncf.firenze.sbn.it/Oai/servlet/OAIHandler?metadataPrefix=oai_dc&amp;set=ManoscrittiInRete&amp;", "http://edit16.iccu.sbn.it:2040/oai2?metadataPrefix=tel_dc&amp;set=ti&amp;", "http://edit16.iccu.sbn.it:2040/oai2?metadataPrefix=tel_dc&amp;set=pd&amp;", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?metadataPrefix=oai_dc&amp;set=CBNPolona&amp;", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3ApowstanieStyczniowe", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3AczasopismaMiedzywojenne", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3AskarbyBN", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3AliteraturaJidysz", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3Akochanowski", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3Amickiewicz", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3Anorwid", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3Aslowacki", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3AwydawnictwaKonspiracyjne", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3Adzieci", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3AWarszawa", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3AfryderykChopin", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3Amikolajrej", "http://www.polona.pl/dlibra/oai-pmh-repository.xml?verb=ListIdentifiers&amp;metadataPrefix=oai_dc&amp;set=CBNPolona%3AkolekcjeTematyczne%3Akresy", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=iconografia&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=leituraespecial&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=seriegeral&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=bnd&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=teses&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=espolios&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=manuscritos&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=impressosreservados&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=cartografia&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=bibliografias&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=musica&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=belasartes&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=historiageografia&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=literatura&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=religiao&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=biblias&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=cienciasartes&amp;", "http://oai.bn.pt/servlet/OAIHandler?metadataPrefix=tel&amp;set=cienciassociais&amp;", "http://www.digital.nbs.bg.ac.yu/oai/oai2.php?metadataPrefix=tel&amp;set=decije&amp;", "http://nainfo.nbs.bg.ac.yu/doiserboai/oai2.aspx?metadataPrefix=tel&amp;", "http://www.digital.nbs.bg.ac.yu/oai/oai2.php?metadataPrefix=tel&amp;set=svetogorska&amp;", "http://www.digital.nbs.bg.ac.yu/oai/oai2.php?metadataPrefix=tel&amp;set=pozorisni&amp;", "http://nukweb.nuk.uni-lj.si:8010/SloBibOAI/OAIHandler?metadataPrefix=oai_dc&amp;", "http://www.nuk.uni-lj.si/PortretnaZbirka/xml.exe/OAIProducer?metadataPrefix=oai_dc&amp;", "http://www.nuk.uni-Lj.si/zemljevidi/XMLZemljevidi.exe?MetadataPrefix=oai_dc&amp;" };
//		DataProviderLoader loader = new DataProviderLoader();
//		for (String currentUrl : urls) {
//			loader.getDataSourceOai(currentUrl, null, "lixo", "lixo");
//		}

//		if(true) {
//			return;
//		}

        DataProviderLoader loader = new DataProviderLoader();
        List<DataProvider> dataProviders = loader.loadDataProvidersFromTEL(new File("F:/dreis/Desktop/REPOX Tel/tel data - attempt2.xml"));
//		ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().addDataProvider();
//		List<DataSource> loadedDataSources = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().loadDataSourceContainers();
//		for (DataSource dataSource : loadedDataSources) {
//			dataSource.initAccessPoints();
//		}
//		ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().initialize(loadedDataSources);
    }
}
