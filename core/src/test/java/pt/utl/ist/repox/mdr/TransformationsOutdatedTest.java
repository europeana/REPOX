package pt.utl.ist.repox.mdr;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.metadataTransformation.Xslt2StylesheetCache;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public class TransformationsOutdatedTest {
    private final String TRANSFORM_ID_1 = "TRANS_TEST_1";

    private final String TEST_FOLDER = "src/test/resources/xslImportTest";

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws IOException, ObjectNotFoundException {

    }

    @Test
    public void noFileOutdatedTest() {
        try {
            Xslt2StylesheetCache xslt2StylesheetCache = new Xslt2StylesheetCache();
            File mainFile = new File(TEST_FOLDER,"marc21bguc2ese.xsl");

            Templates result1 = xslt2StylesheetCache.getTransformationTemplate(mainFile);

            mainFile = new File(TEST_FOLDER,"marc21bguc2ese.xsl");

            Templates result2 = xslt2StylesheetCache.getTransformationTemplate(mainFile);

            Assert.assertTrue(result1.equals(result2));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fileOutdatedTest() {
        try {
            Xslt2StylesheetCache xslt2StylesheetCache = new Xslt2StylesheetCache();
            File mainFile = new File(TEST_FOLDER,"marc21bguc2ese.xsl");

            Templates result1 = xslt2StylesheetCache.getTransformationTemplate(mainFile);

            mainFile = new File(TEST_FOLDER,"marc21bguc2ese.xsl");
            Long lastModifiedOriginal = mainFile.lastModified();
            mainFile.setLastModified(1346698004);

            Templates result2 = xslt2StylesheetCache.getTransformationTemplate(mainFile);

            // return to previous last modified date
            mainFile.setLastModified(lastModifiedOriginal);

            Assert.assertTrue(!result1.equals(result2));
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void subFileOutdatedTest() {
        try {
            Xslt2StylesheetCache xslt2StylesheetCache = new Xslt2StylesheetCache();
            File mainFile = new File(TEST_FOLDER,"marc21bguc2ese.xsl");

            Templates result1 = xslt2StylesheetCache.getTransformationTemplate(mainFile);

            mainFile = new File(TEST_FOLDER,"marc21bguc2ese.xsl");

            File subFile = new File(TEST_FOLDER,"new2.xsl");
            Long lastModifiedOriginal = subFile.lastModified();
            subFile.setLastModified(1346698004);

            Templates result2 = xslt2StylesheetCache.getTransformationTemplate(mainFile);

            // return to previous last modified date
            subFile.setLastModified(lastModifiedOriginal);

            Assert.assertTrue(!result1.equals(result2));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

}
