package pt.utl.ist.repox.metadataTransformation;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import pt.utl.ist.repox.util.ConfigSingleton;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class MetadataTransformation {
    private static final Logger log = Logger.getLogger(MetadataTransformation.class);

    private String id;
    private String description;
    private String sourceFormat;
    private String destinationFormat;
    private String stylesheet;
    private String destSchema;
    private String destNamespace;
    private String sourceSchema;
    private boolean bMDRCompliant;
    private boolean bEditable;
    private boolean versionTwo = false;
    private boolean bDeleteOldFiles = false;

    public void setSourceSchema(String schema) {
        sourceSchema = schema;
    }

    public String getSourceSchema() {
        return sourceSchema;
    }

    public void setMDRCompliant(boolean mdrCompliant) {
        bMDRCompliant = mdrCompliant;
    }

    public boolean isMDRCompliant() {
        return bMDRCompliant;
    }

    public boolean isVersionTwo() {
        return versionTwo;
    }

    public void setVersionTwo(boolean versionTwo) {
        this.versionTwo = versionTwo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceFormat() {
        return sourceFormat;
    }

    public void setSourceFormat(String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    public String getDestinationFormat() {
        return destinationFormat;
    }

    public void setDestinationFormat(String destinationFormat) {
        this.destinationFormat = destinationFormat;
    }

    public String getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public boolean isEditable() {
        return bEditable;
    }

    public void setEditable(boolean editable) {
        this.bEditable = editable;
    }

    public String getDestSchema() {
        return destSchema;
    }

    public void setDestSchema(String destSchema) {
        this.destSchema = destSchema;
    }

    public String getDestNamespace() {
        return destNamespace;
    }

    public void setDestNamespace(String destNamespace) {
        this.destNamespace = destNamespace;
    }

    public boolean isDeleteOldFiles() {
        return bDeleteOldFiles;
    }

    public void setDeleteOldFiles(boolean deleteOldFiles) {
        this.bDeleteOldFiles = deleteOldFiles;
    }

    public MetadataTransformation() {
        super();
    }


    public MetadataTransformation(String id, String description, String sourceFormat, String destinationFormat,
                                  String stylesheet, boolean editable, boolean isVersion2, String destSchema,
                                  String namespace) {
        super();
        this.id = id;
        this.description = description;
        this.sourceFormat = sourceFormat;
        this.destinationFormat = destinationFormat;
        this.stylesheet = stylesheet;
        this.bEditable = editable;
        this.versionTwo = isVersion2;
        this.destSchema = destSchema;
        this.destNamespace = namespace;
    }

    public String transform(String identifier, String xmlSourceString, String dataProviderName) throws DocumentException, TransformerException, NullPointerException {
        try{
            Transformer transformer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().loadStylesheet(this);

            Document sourceDocument = DocumentHelper.parseText(xmlSourceString);
            DocumentSource source = new DocumentSource(sourceDocument);
            DocumentResult result = new DocumentResult();

            transformer.clearParameters();
            transformer.setParameter("recordIdentifier", identifier);
            transformer.setParameter("dataProvider", dataProviderName);

            // Transform the source XML to System.out.
            transformer.transform(source, result);
            Document transformedDoc = result.getDocument();

            return transformedDoc.asXML();
        }catch (NullPointerException e){
            throw new NullPointerException();
        }
    }



    public static void main( final String [] args ) {
        /*
        try{
            List<Element> records = RecordSAXParser.parse(new File("C:\\Users\\GPedrosa\\Desktop\\outros2\\09428_Ag_DE_ELocal.xml"), "europeana:record");
            System.out.println("records = " + records.size());
        }catch(Exception e){
            e.printStackTrace();
        }
        */


        //String foo_xml = "C:\\Users\\GPedrosa\\Desktop\\REPOX\\XSL2\\xml.xml"; //input xml
        //String foo_xsl = "C:\\Users\\GPedrosa\\Desktop\\REPOX\\XSL2\\xsl-v2.xsl"; //input xsl

        //String foo_xml = "C:/Users/GPedrosa/Desktop/repoxTest/PM/PortugalMatematica-1.xml"; //input xml
        //String foo_xsl = "C:\\Users\\GPedrosa\\Desktop\\repoxTest\\xsl\\IstPmToNlm.xsl"; //input xsl

        String foo_xml2 = "D:\\Projectos\\repoxdata_new\\export\\bmfinancas\\bmfinancas12-1.xml"; //input xml
        String foo_xsl2 = "D:\\Projectos\\repoxdata_new\\configuration\\xslt\\winlib2ese.xsl"; //input xsl


        // String foo_xml = "C:\\Users\\GPedrosa\\Desktop\\EuDML\\repoxTest\\testXslt1\\OAIHandler.xml"; //input xml
        // String foo_xsl = "c:\\tel\\repoxdata\\configuration\\xslt\\unimarcFigVinhos2ese.xsl"; //input xsl


        try {
            System.out.println("1... T1 : XML: version1 : XSL: version1");
            //     myTransformer (foo_xml, foo_xsl, false);
            //System.out.println("2... T2: XML: version2 : XSL: version2");
            myTransformer (foo_xml2, foo_xsl2, false);
            //System.out.println("");
            //System.out.println("3... T2 : XML: version1 : XSL: version1");
            //myTransformerV2(foo_xml, foo_xsl);
            //System.out.println("4... T1 : XML: version2 : XSL: version2");
            //myTransformerV1(foo_xml2, foo_xsl2);
            //System.out.println("");
            //System.out.println("5... T1 : XML: version1 : XSL: version1");
            //myTransformerV1 (foo_xml, foo_xsl);
            //System.out.println("6... T2: XML: version2 : XSL: version2");
            //myTransformerV2 (foo_xml2, foo_xsl2);

        } catch (Exception ex) {
            System.out.println("ex = " + ex);
        }
    }


    private static void myTransformer(String sourceID, String xslID, boolean isVersion2)
            throws TransformerException, DocumentException {

        String xmlSourceString = "<resultado xmlns=\"http://www.openarchives.org/OAI/2.0/\" boletim=\"0\" hide=\"0\" pesquisa=\"2\" url=\"winlib.exe?pesq=2&amp;doc=14951&amp;exp=3\">\t<web-path>http://213.58.158.155/winlib</web-path>\t<pesq id=\"2\">Pesquisa Simples</pesq>\t<pesq id=\"3\">Pesquisa Orientada</pesq>\t<pesq id=\"6\">Fundo Histórico Ultramarino</pesq>\t<pesq id=\"98\">Biblioteca Nacional (»)</pesq>\t<pesq id=\"99\">Destaques</pesq>\t<doc bib=\"14951\" idtipo=\"11\" tipo=\"COLECÇÕES\">\t<campo name=\"Titulo\" ord=\"1\">\t\t<label>Título</label>\t\t<valores continuacao=\"1\">\t\t<valor>Reforma do Tribunal de Contas</valor>\t\t</valores>\t</campo>\t<campo name=\"DesigGenerica\" ord=\"2\">\t\t<label>Des. Gen.</label>\t\t<valores continuacao=\"1\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloSec\" ord=\"3\">\t\t<label>Título Sec.</label>\t\t<valores continuacao=\"1\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloPar\" ord=\"4\">\t\t<label>Título Par.</label>\t\t<valores continuacao=\"1\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"MencaoEdicao\" ord=\"13\">\t\t<label>Menção Ed.</label>\t\t<valores continuacao=\"1\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"DesigEspecifica\" ord=\"20\">\t\t<label>Paginação</label>\t\t<valores continuacao=\"1\">\t\t<valor>62 p.</valor>\t\t</valores>\t</campo>\t<campo name=\"OutTitulo\" ord=\"22\">\t\t<label>Título</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutDesigGenerica\" ord=\"23\">\t\t<label>Des. Gen.</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutTituloSec\" ord=\"24\">\t\t<label>Título Sec.</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutTituloPar\" ord=\"25\">\t\t<label>Título Par.</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutPriMenc\" ord=\"26\">\t\t<label>Pri. Menção</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutOutMenc\" ord=\"27\">\t\t<label>Out. Menções</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"Notas\" ord=\"31\">\t\t<label>Notas</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"Resumo\" ord=\"32\">\t\t<label>Resumo</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"DescritoresLivres\" ord=\"41\">\t\t<label>Descritores Livres</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloCapa\" ord=\"74\">\t\t<label>Título da capa</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloRostoComp\" ord=\"75\">\t\t<label>Título Rosto Comp.</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloLombada\" ord=\"76\">\t\t<label>Título Lombada</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloTraduzido\" ord=\"80\">\t\t<label>Título Traduzido</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloDesenvolvido\" ord=\"80\">\t\t<label>Título Desenvolvido</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloAbreviado\" ord=\"80\">\t\t<label>Título Abreviado</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloAdicionado\" ord=\"80\">\t\t<label>Título Adicionado</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"700\" ord=\"7\">\t\t<label>Autor</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"701\" ord=\"8\">\t\t<label>Co Autor</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"702\" ord=\"9\">\t\t<label>Resp. Sec.</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"710\" ord=\"10\">\t\t<label>Col. Autor</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"2907\">Portugal. Ministério das Finanças</valor>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"711\" ord=\"11\">\t\t<label>Co Col. Autor Pri.</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"712\" ord=\"12\">\t\t<label>Co Col. Autor Sec.</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo name=\"210_ed\" ord=\"16\">\t\t<label>Edição</label>\t\t<valores continuacao=\"1\">\t\t<valor>Lisboa: MF, 1989</valor>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"600\" ord=\"35\">\t\t<label>Nome de pessoa</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"601\" ord=\"36\">\t\t<label>Nome de colectividade</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"602\" ord=\"37\">\t\t<label>Nome de família</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"604\" ord=\"38\">\t\t<label>Autor/Título</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"605\" ord=\"39\">\t\t<label>Título</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"606\" ord=\"40\">\t\t<label>Descritores</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"1051\">SECTOR PÚBLICO</valor>\t\t<valor id=\"645\">LEGISLAÇÃO</valor>\t\t<valor id=\"614\">INSTITUIÇÃO FINANCEIRA</valor>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"607\" ord=\"42\">\t\t<label>Nome geográfico</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo name=\"URL\" ord=\"45\">\t\t<label>URLs</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"http://213.58.158.153/COL-MF-0017/1/\">Consulte esta obra na Biblioteca Digital</valor>\t\t</valores>\t</campo>\t<items>\t\t<label>Cota</label>\t\t<label>Sigla</label>\t\t<label>Código Barras</label>\t\t<label>Estado</label>\t\t<item id=\"19988\">\t\t\t<valor>COL/MF/00017</valor>\t\t\t<valor> </valor>\t\t\t<valor>300100011558</valor>\t\t\t<valor>Livre</valor>\t\t</item>\t\t<item id=\"19989\">\t\t\t<valor>COL/MF/00017/A</valor>\t\t\t<valor> </valor>\t\t\t<valor>300100011559</valor>\t\t\t<valor>Livre</valor>\t\t</item>\t\t<item id=\"19990\">\t\t\t<valor>COL/MF/00017/B</valor>\t\t\t<valor> </valor>\t\t\t<valor>300100011560</valor>\t\t\t<valor>Livre</valor>\t\t</item>\t</items>\t</doc></resultado>";
        Document sourceDocument = DocumentHelper.parseText(xmlSourceString);
        DocumentSource source = new DocumentSource(sourceDocument);


        if(isVersion2){
            System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        }
        else{
            System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
        }

        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();

        // Create a transformer for the stylesheet.
        Transformer transformer = tfactory.newTransformer(new StreamSource(new File(xslID)));

        // Transform the source XML to System.out.
        transformer.transform(source, new StreamResult(System.out));
    }
}
