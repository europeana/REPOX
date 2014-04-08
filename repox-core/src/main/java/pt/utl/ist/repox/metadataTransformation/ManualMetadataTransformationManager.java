package pt.utl.ist.repox.metadataTransformation;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;
import org.dom4j.io.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ManualMetadataTransformationManager {
	private static final String REPOX_NAMESPACE = "rpx";
	private static final String NAMESPACE_PREFIX = "/" + REPOX_NAMESPACE + ":";
	private static final String NAMESPACE_URI_ATTRIBUTE = "xmlns:" + REPOX_NAMESPACE;
	private static final String NAMESPACE_REPLACE = "@NAMESPACE@";

//	private static final String templateRepoxNamespace = "xmlns:rpx='info:lc/xmlns/marcxchange-v1'";

	public static final String OAI_DC_NAMESPACES = "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
		+ " xmlns:oai_dc='http://www.openarchives.org/OAI/2.0/oai_dc/'"
		+ " xmlns:dc='http://purl.org/dc/elements/1.1/'"
		+ " xsi:schemaLocation='http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd'";
	public static final String OAI_DC_ROOT = "oai_dc:dc";

	public static final String TEL_NAMESPACES = "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
		+ " xmlns='http://krait.kb.nl/coop/tel/handbook/telterms.html'"
		+ " xmlns:dc='http://purl.org/dc/elements/1.1/'"
		+ " xmlns:tel='http://krait.kb.nl/coop/tel/handbook/telterms.html'";
	public static final String TEL_ROOT = "record";

	//TODO: confirm that xsl:stylesheet attribute xmlns:rpx = record attribute
	private static final String templateStart = "<?xml version='1.0' encoding='UTF-8'?>"
		+ " <xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'"
		+ " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " + NAMESPACE_REPLACE  + ">"
		+ " <xsl:output method='xml' indent='yes'/>"
		+ " <xsl:template match='/'>";
	private static final String templateFinish = "</xsl:template>" + "</xsl:stylesheet>";

	private Map<String, Set<String>> tagMappings;

	public Map<String, Set<String>> getTagMappings() {
		return tagMappings;
	}

	public void setTagMappings(Map<String, Set<String>> tagMappings) {
		this.tagMappings = tagMappings;
	}

	public ManualMetadataTransformationManager(Map<String, Set<String>> tagMappings) {
		super();
		this.tagMappings = tagMappings;
	}

/*
	<xsl:if test="[tag Xpath]">
		 <xsl:if test="$currentGroupMatch != 'true'"> -> usar hash do xpath do grupo para a variável para evitar problemas
		 	<xsl:variable name="currentGroupMatch" select="'true'" />
		 	[initialPrefix]
		 </xsl:if>
		 [currentPrefix]

		 <xsl:value-of select="."/>

		System.out.println("ROOT Element: " + sourceDocument.getRootElement().getName());

	</xsl:if>

	<xsl:if test="$currentGroupMatch == true">[finalSuffix]</xsl:if>

*/

	private String getNamespaceAware(String xpath, boolean useNamespace) {
		return (useNamespace ? xpath.replaceAll("/", NAMESPACE_PREFIX) : xpath);
	}

	public String getTransformationText(Document sourceDocument, String rootElement, String namespaces) throws IOException, DocumentException, NoSuchAlgorithmException {
		String transformationText = "";

		boolean useNamespace = (sourceDocument.getRootElement().getNamespace() != Namespace.NO_NAMESPACE);
		String namespace = (useNamespace ? sourceDocument.getRootElement().getNamespace().getText() : "");

		for (String currentTargetField : tagMappings.keySet()) {
			for (String currentMapping : tagMappings.get(currentTargetField)) {
				currentMapping = currentMapping.trim();
				String mappingToAdd = "";

				if(currentMapping.startsWith(TagGroup.GROUP_DELIMITER_START)) {
					TagGroup group = new TagGroup(currentMapping);

					int indexOfXpath = (group.getCommonXpath() != null ? group.getCommonXpath().trim().length() : 0);

					String existsValueXpath = "";
					for (int i = 0; i < group.getTags().size(); i++) {
						if(i > 0) {
							existsValueXpath = existsValueXpath.trim() + " or ";
						}

						String currentXpath = group.getTags().get(i).getXpath().trim();
						String currentXpathToAdd = (indexOfXpath > 0 ? getNamespaceAware(currentXpath.substring(indexOfXpath), useNamespace).substring(1)
																	: getNamespaceAware(currentXpath, useNamespace));
						existsValueXpath = existsValueXpath + currentXpathToAdd;
					}

					if(group.getInitialPrefix() != null && group.getInitialPrefix().length() > 0) {
						mappingToAdd += "<xsl:if test=\"" + existsValueXpath + "\">" + group.getInitialPrefix() + "</xsl:if>";
					}

					String allTests = "";

					for (int i = 0; i < group.getTags().size(); i++) {
						Tag currentTag = group.getTags().get(i);
						String selectXpath = (indexOfXpath > 0 ? getNamespaceAware(currentTag.getXpath().trim().substring(indexOfXpath), useNamespace).substring(1)
																: getNamespaceAware(currentTag.getXpath().trim(), useNamespace));
						mappingToAdd += "<xsl:if test=\"" + selectXpath + "\">"
							+ group.getTagPrefixes().get(i)
							+ "<xsl:text xml:space=\"preserve\"> </xsl:text>"
							+ "<xsl:value-of select=\"" + selectXpath + "\"/>"
							+ "</xsl:if>";

						allTests += (allTests.isEmpty() ? selectXpath : " or " + selectXpath);
					}

					if(group.getFinalSuffix() != null && group.getFinalSuffix().length() > 0) {
						mappingToAdd += "<xsl:if test=\"" + existsValueXpath.trim() + "\">" + group.getFinalSuffix() + "</xsl:if>";
					}

					mappingToAdd = (indexOfXpath > 0 ? "<xsl:for-each select=\"" + getNamespaceAware(group.getCommonXpath(), useNamespace) + "\">" : "")
									+ "<xsl:if test=\"" + allTests + "\">"
									+ "<" + currentTargetField + ">"
									+ mappingToAdd
									+ "</" + getEndTag(currentTargetField) + ">"
									+ "</xsl:if>"
									+ (indexOfXpath > 0 ? "</xsl:for-each>" : "")
									+ "\n\n";
				}
				else {
					currentMapping = getNamespaceAware(currentMapping, useNamespace);
					mappingToAdd = "<xsl:for-each select=\"" + currentMapping.trim() + "\">"
					+ "<" + currentTargetField + ">" + "<xsl:value-of select=\".\"/></" + getEndTag(currentTargetField) + ">"
					+ "</xsl:for-each>\n\n";
				}

				transformationText += mappingToAdd;
			}
		}

		String stylesheetHeader = (useNamespace ? templateStart.replaceFirst(NAMESPACE_REPLACE, NAMESPACE_URI_ATTRIBUTE + "='" + namespace + "'")
												: templateStart.replaceFirst(NAMESPACE_REPLACE, ""));

		String targetStart = "<" + rootElement + " " + namespaces + ">";
		String targetEnd = "</" + rootElement + ">";

		transformationText = stylesheetHeader + targetStart + "\n\n" + transformationText + "\n" + targetEnd + templateFinish;

		OutputFormat format = OutputFormat.createPrettyPrint();
		OutputStream outputStream = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(outputStream, format);
		writer.write(DocumentHelper.parseText(transformationText));

		return outputStream.toString();

//		return transformationText;
	}

	private String getEndTag(String currentTargetField) {
		int spaceCharIndex = currentTargetField.indexOf(" ");
		if(spaceCharIndex != -1) {
			return currentTargetField.substring(0, spaceCharIndex);
		}
		else {
			return currentTargetField;
		}
	}

	public String transform(File sourceFile, String rootElement, String namespaces)
			throws DocumentException, TransformerException, IOException, NoSuchAlgorithmException {
		Document sourceDocument = new SAXReader().read(sourceFile);
		String transformationText = getTransformationText(sourceDocument, rootElement, namespaces);

		Document transformationDocument = DocumentHelper.parseText(transformationText);

        boolean isVersion2 = transformationDocument.getRootElement().attribute("version").getText().equals("2.0");
        if(isVersion2){
            System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        }
        else{
            System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer(new DocumentSource(transformationDocument));

		DocumentSource source = new DocumentSource(sourceDocument);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);

        Document transformedDoc = result.getDocument();
        if(transformedDoc.getRootElement() != null
        		&& transformedDoc.getRootElement().getNamespaceForPrefix(REPOX_NAMESPACE) != null) {
        	Namespace repoxNamespace = transformedDoc.getRootElement().getNamespaceForPrefix(REPOX_NAMESPACE);
        	transformedDoc.getRootElement().remove(repoxNamespace);
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
		OutputStream outputStream = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(outputStream, format);
		writer.write(DocumentHelper.parseText(transformedDoc.asXML()));

		return outputStream.toString();
	}

	public static void main(String[] args) throws Exception {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(System.out, format);

		TreeMap<String, Set<String>> metadataMappings = new TreeMap<String, Set<String>>();
		Set<String> dcIdentifierMappings = new TreeSet<String>();
		dcIdentifierMappings.add("/record/lixo1[@lixoArg='3' and lixoArg2='2']");
		dcIdentifierMappings.add("/record/lixo2/lixo3");
		dcIdentifierMappings.add("/record/controlfield[@tag='001']");
		dcIdentifierMappings.add("/record/controlfield");
		Set<String> dcTitleMappings = new TreeSet<String>();
		dcTitleMappings.add("/record/lixo3[@lixoArg='asdasd' and lixoArg2='2']");
		dcTitleMappings.add("/record/lixo4/lixo5");
		dcTitleMappings.add("/record/lixo6");
		metadataMappings.put("dc:identifier", dcIdentifierMappings);
		metadataMappings.put("dc:title", dcTitleMappings);

		ManualMetadataTransformationManager manager = new ManualMetadataTransformationManager(metadataMappings);
		File sourceFile = new File("/home/dreis/repoxdata/kbrdir/kbr_1_806783.marcxchange.xml");
		String transformed = manager.transform(sourceFile, OAI_DC_ROOT, OAI_DC_NAMESPACES);

		System.out.println("Transformed: \n");
        writer.write(DocumentHelper.parseText(transformed));
	}

	/*
		public static void main(String[] args) throws Exception {
			TransformerFactory factory = TransformerFactory.newInstance();
	        Transformer transformer = factory.newTransformer(new StreamSource(new File("/home/dreis/repoxdata/configuration/xslt/unimarc2tel-notbn.xsl")));

	        DocumentSource source = new DocumentSource(new SAXReader().read(new File("/home/dreis/repoxdata/kbrdir/kbr_1_806783.marcxchange.xml")));
	        DocumentResult result = new DocumentResult();
	        transformer.transform( source, result );

	        // return the transformed document
	        Document transformedDoc = result.getDocument();
	        OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(transformedDoc);
		}
	*/
}
