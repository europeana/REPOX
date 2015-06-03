/*
 * Created on 2007/01/23
 *
 */
package pt.utl.ist.dataProvider.dataSource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.marc.CharacterEncoding;
import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.recordPackage.RecordRepox;
import pt.utl.ist.recordPackage.RecordSAXParser;
import pt.utl.ist.util.StringUtil;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.File;
import java.io.FileInputStream;

/**
 */
public class SimpleFileExtractStrategy implements FileExtractStrategy {
  public static final String OLDCLASS = "SimpleFileExtract";
    private static final Logger log = Logger.getLogger(SimpleFileExtractStrategy.class);

    @Override
    public void iterateRecords(RecordHandler recordHandler, DataSource dataSource, File file, CharacterEncoding characterEncoding, File logFile) {
        new RecordIterator(recordHandler, dataSource, file, characterEncoding, logFile);
    }

    @Override
    public boolean isXmlExclusive() {
        return true;
    }

    private class RecordIterator {
        RecordHandler recordHandler;

        public RecordIterator(final RecordHandler recordHandler, final DataSource dataSource, final File file, CharacterEncoding characterEncoding, final File logFile) {
            this.recordHandler = recordHandler;

            try {
                if (dataSource.getClass() == DirectoryImporterDataSource.class && ((DirectoryImporterDataSource)dataSource).getRecordXPath() != null && !((DirectoryImporterDataSource)dataSource).getRecordXPath().isEmpty()) {

                    RecordSAXParser handler = new RecordSAXParser(((DirectoryImporterDataSource)dataSource).getRecordXPath(), new RecordSAXParser.RecordHandler() {
                        @Override
                        public void handleRecord(Element recordElement) {
                            try {
                                RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(recordElement, null, false, false);
                                //System.out.println("record.getId() = " + record.getId());
                                recordHandler.handleRecord(recordRepox);
                            } catch (Exception e) {
                                StringUtil.simpleLog("Error importing record from file: " + file.getName() + " ERROR: " + e.getMessage(), this.getClass(), logFile);
                                log.error(file.getName() + ": " + e.getMessage(), e);
                            }
                        }
                    });
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    XMLReader parser = saxParser.getXMLReader();
                    parser.setContentHandler(handler);

                    /*
                     * StringWriter stringWriter = new StringWriter();
                     * org.apache.commons.io.IOUtils.copy(new
                     * FileInputStream(file), stringWriter);
                     * 
                     * // replace invalid characters String result =
                     * stringWriter.toString(); result =
                     * result.replaceAll("&#55349;&#56491;", "&#119979;");
                     * result = result.replaceAll("&#55349;&#56651;",
                     * "&#120139;"); result =
                     * result.replaceAll("&#55349;&#56490;", "&#119978;");
                     * 
                     * stringWriter.close();
                     * 
                     * InputSource inSource = new InputSource(new
                     * StringReader(result));
                     */
                    InputSource inSource = new InputSource(new FileInputStream(file));
                    parser.parse(inSource);

                    /*
                     * //DOM SAXReader reader = new SAXReader(); Document
                     * document = reader.read(file);
                     * 
                     * if(((DataSourceDirectoryImporter)dataSource).getNamespaces
                     * () != null){ XPath xpath2 =
                     * DocumentHelper.createXPath(((DataSourceDirectoryImporter
                     * )dataSource).getRecordXPath());
                     * xpath2.setNamespaceContext(new
                     * SimpleNamespaceContext(((DataSourceDirectoryImporter
                     * )dataSource).getNamespaces())); recordIterator =
                     * xpath2.selectNodes(document).iterator(); } else
                     * recordIterator =
                     * document.selectNodes(((DataSourceDirectoryImporter
                     * )dataSource).getRecordXPath()).iterator();
                     */
                } else {
                    //                    StringUtil.simpleLog("Creating record iterator for file: " + file.getName(), this.getClass(), logFile, false);
                    Document document = new SAXReader().read(file);
                    //List<Element> records = new ArrayList<Element>();
                    //records.add(document.getRootElement());
                    //recordIterator = records.iterator();

                    RecordRepox recordRepox = dataSource.getRecordIdPolicy().createRecordRepox(document.getRootElement(), null, false, false);
                    //System.out.println("record.getId() = " + record.getId());
                    recordHandler.handleRecord(recordRepox);
                }
            } catch (Exception e) {
                StringUtil.simpleLog("Error parsing record(s) from file: " + file.getName() + " ERROR: " + e.getMessage(), this.getClass(), logFile);
                e.printStackTrace();
            }
        }
    }

}