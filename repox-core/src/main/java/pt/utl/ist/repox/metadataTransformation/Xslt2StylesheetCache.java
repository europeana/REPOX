/* Xslt2StylesheetCache.java - created on 17 de Fev de 2011, Copyright (c) 2011 The European Library, all rights reserved */
package pt.utl.ist.repox.metadataTransformation;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.HashMap;

/** Caches all XSLT stylesheets as Templates. It reloads the templates when the stylesheets' source file changes
 *
 * @author Nuno Freire (nfreire@gmail.com)
 * @date 17 de Fev de 2011
 */
public class Xslt2StylesheetCache {

    private HashMap<File, TransformationFile> transformationFileHashMap;
    private TransformerFactory transformerFactory;

    /**
     * Creates a new instance of this class.
     */
    public Xslt2StylesheetCache() {
        transformationFileHashMap=new HashMap<File, TransformationFile>();
        transformerFactory = new net.sf.saxon.TransformerFactoryImpl();
    }

    /** Creates a new Transformer for the input stylesheet file.
     * @param stylesheetFile the stylesheet
     * @return the transformer
     * @throws TransformerException
     */
    public Transformer createTransformer(File stylesheetFile) throws TransformerException {
        return getTransformationTemplate(stylesheetFile).newTransformer();
    }

    public Templates getTransformationTemplate(File stylesheetFile) throws TransformerException{
        Templates templates;
        synchronized (transformationFileHashMap) {
            TransformationFile transformationFile = transformationFileHashMap.get(stylesheetFile);
            if(transformationFile==null || transformationFile.isOutdated(stylesheetFile.lastModified()) ||
                    !transformationFile.subFilesUpToDate()) {
                templates=transformerFactory.newTemplates(new StreamSource(stylesheetFile));
                transformationFile = new TransformationFile(stylesheetFile,templates,stylesheetFile.lastModified());
                transformationFileHashMap.put(stylesheetFile,transformationFile);
            }else
                templates = transformationFile.getTemplate();
        }
        return templates;
    }

}
