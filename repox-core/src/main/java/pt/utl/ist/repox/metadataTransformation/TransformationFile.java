package pt.utl.ist.repox.metadataTransformation;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.xml.transform.Templates;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created to project REPOX.
 * User: Edmundo
 * Date: 03/09/12
 * Time: 13:28
 */
public class TransformationFile {

    private List<TransformationSubFile> transformationSubFiles;
    private File stylesheet;

    private Templates template;
    private Long timestamp;

    public TransformationFile(File stylesheet, Templates template, Long timestamp) {
        this.stylesheet = stylesheet;
        this.template = template;
        this.timestamp = timestamp;
        transformationSubFiles = new ArrayList<TransformationSubFile>();
        addSubFiles();
    }

    public void addSubFiles(){
        // todo: process imports inside of imports
        try {
            SAXReader reader = new SAXReader();

            Document document = reader.read(stylesheet);
            document.getRootElement().add(new Namespace("xsl","http://www.w3.org/1999/XSL/Transform"));

            List<Node> list = document.getRootElement().selectNodes("xsl:import");

            for(Node node: list) {
                String fileName = node.valueOf("@href");
                File xslFile = new File(stylesheet.getParent(),fileName);
                transformationSubFiles.add(new TransformationSubFile(xslFile.lastModified(),xslFile.getName()));
            }
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public boolean isOutdated(Long currentStylesheetDate) {
        return !currentStylesheetDate.equals(getTimestamp());
    }

    public boolean subFilesUpToDate() {
        for(TransformationSubFile subFile : transformationSubFiles){
            File currentFile = new File(stylesheet.getParent(),subFile.getFileName());
            if(subFile.getTimestamp() != currentFile.lastModified()){
//                System.out.println("Sub Files OUTDATED");
                return false;
            }
        }
//        System.out.println("Sub Files Up to Date");
        return true;
    }

    public Templates getTemplate() {
        return template;
    }

    public void setTemplate(Templates template) {
        this.template = template;
    }

    public List<TransformationSubFile> getTransformationSubFiles() {
        return transformationSubFiles;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
