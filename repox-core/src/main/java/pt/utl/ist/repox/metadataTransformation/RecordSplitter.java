package pt.utl.ist.repox.metadataTransformation;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import pt.utl.ist.repox.util.XmlUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class RecordSplitter {

	public static void splitXmlFolder(File xmlDir, String xpath, String xpathId, File outputDir) throws IOException, DocumentException {
		if(!xmlDir.isDirectory()) {
			return;
		}

		File[] folderFiles = xmlDir.listFiles();

		for (File file : folderFiles) {
			if(file.isFile() && file.getName().endsWith(".xml")) {
				splitXmlObjects(file, xpath, xpathId, outputDir);
			}
			else if(file.isDirectory()) {
				splitXmlFolder(file, xpath, xpathId, outputDir);
			}
		}
	}

	public static void splitXmlObjects(File xmlFile, String xpath, String xpathId, File outputDir) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		Document xmlSource = reader.read(xmlFile);
		XPath recordXPath = DocumentHelper.createXPath(xpath);
		List objectNodes = recordXPath.selectNodes(xmlSource.getRootElement());
		for (Object currentObject : objectNodes) {
			Document currentDocument = DocumentHelper.createDocument();
			Element currentElement = (Element) currentObject;
			currentDocument.setRootElement((Element) currentElement.detach());

			String filename = null;
			if(xpathId != null) {
				filename = currentDocument.selectSingleNode(xpathId).getText();
			}

			if(filename == null || filename.trim().isEmpty()) {
				filename = UUID.randomUUID().toString();
			}

			outputDir.mkdir();
			File file = new File(outputDir, filename + ".xml");
			OutputStream outputStream = new FileOutputStream(file);

			XmlUtil.writePrettyPrint(outputStream, currentDocument);
		}
	}

	public static void main(String[] args) throws Exception {
		RecordSplitter.splitXmlFolder(new File("/home/dreis/apagame"), "/raiz/objecto", "/objecto/@id", new File("/home/dreis/apagame2"));
	}
}

