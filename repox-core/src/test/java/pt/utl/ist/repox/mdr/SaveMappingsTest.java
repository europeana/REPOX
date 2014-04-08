package pt.utl.ist.repox.mdr;

import junit.framework.Assert;
import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.repox.task.exception.IllegalFileFormatException;
import pt.utl.ist.repox.util.ConfigSingleton;
import pt.utl.ist.repox.util.RepoxContextUtilDefault;
import pt.utl.ist.util.exceptions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class SaveMappingsTest {
    private final String TRANSFORM_ID_1 = "TRANS_TEST_1";
    private final String TRANSFORM_ID_2 = "TRANS_TEST_2";
    private final String TRANSFORM_1_DESC = "TRANS_TEST_1_DESC";
    private final String TRANSFORM_2_DESC = "TRANS_TEST_2_DESC";
    private final String TRANSFORM_1_SRC = "ESE";
    private final String TRANSFORM_1_SRC_SCHEMA = "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd";
    private final String TRANSFORM_2_SRC = "ESE2";
    private final String TRANSFORM_2_SRC_SCHEMA = "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd";
    private final String TRANSFORM_1_DEST = "OAI_DC";
    private final String TRANSFORM_2_DEST = "OAI_DC2";
    private final String TRANSFORM_1_XSL = "src/test/resources/xslImportTest/new.xsl";
    private final String TRANSFORM_2_XSL = "src/test/resources/xslImportTest/new2.xsl";
    private final String TRANSFORM_SCHEMA = "rheherh";
    private final String TRANSFORM_NAMESPACE = "rheherhd32";


    MetadataTransformationManager metadataTransformationManager;

    @Before
    public void setUp() {
        ConfigSingleton.setRepoxContextUtil(new RepoxContextUtilDefault());
        metadataTransformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManagerTest().getMetadataTransformationManager();
    }

    @After
    public void tearDown() throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalFileFormatException, SQLException, ParseException, ObjectNotFoundException {
        metadataTransformationManager.deleteMetadataTransformation(TRANSFORM_ID_1);
        metadataTransformationManager.deleteMetadataTransformation(TRANSFORM_ID_2);
    }

    @Test
    public void singleSaveTest() {
        try {
            MetadataTransformation metadataTransformation = new MetadataTransformation(TRANSFORM_ID_1,
                    TRANSFORM_1_DESC, TRANSFORM_1_SRC, TRANSFORM_1_DEST, TRANSFORM_1_XSL,
                    false, true, TRANSFORM_SCHEMA, TRANSFORM_NAMESPACE);
            metadataTransformation.setSourceSchema(TRANSFORM_1_SRC_SCHEMA);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(metadataTransformation, TRANSFORM_ID_1);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (AlreadyExistsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SameStylesheetTransformationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void sameIdTest() {
        try {
            MetadataTransformation metadataTransformation1 = new MetadataTransformation(TRANSFORM_ID_1,
                    TRANSFORM_1_DESC,TRANSFORM_1_SRC,TRANSFORM_1_DEST,TRANSFORM_1_XSL,
                    false,true,TRANSFORM_SCHEMA,TRANSFORM_NAMESPACE);
            metadataTransformation1.setSourceSchema(TRANSFORM_1_SRC_SCHEMA);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(metadataTransformation1,TRANSFORM_ID_1);

            MetadataTransformation metadataTransformation2 = new MetadataTransformation(TRANSFORM_ID_1,
                    TRANSFORM_1_DESC,TRANSFORM_1_SRC,TRANSFORM_1_DEST,TRANSFORM_1_XSL,
                    false,true,TRANSFORM_SCHEMA,TRANSFORM_NAMESPACE);
            metadataTransformation2.setSourceSchema(TRANSFORM_1_SRC_SCHEMA);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(metadataTransformation2,"");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (AlreadyExistsException e) {
            Assert.assertTrue(true);
        } catch (SameStylesheetTransformationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void sameSrcAndDestFormatTest() {
        try {
            MetadataTransformation metadataTransformation1 = new MetadataTransformation(TRANSFORM_ID_1,
                    TRANSFORM_1_DESC,TRANSFORM_1_SRC,TRANSFORM_1_DEST,TRANSFORM_1_XSL,
            false,true,TRANSFORM_SCHEMA,TRANSFORM_NAMESPACE);
            metadataTransformation1.setSourceSchema(TRANSFORM_1_SRC_SCHEMA);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(metadataTransformation1,TRANSFORM_ID_1);

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(metadataTransformation1,TRANSFORM_ID_1);


            MetadataTransformation metadataTransformation2 = new MetadataTransformation(TRANSFORM_ID_2,
                    TRANSFORM_2_DESC,TRANSFORM_1_SRC,TRANSFORM_1_DEST,TRANSFORM_2_XSL,
                    false,true,TRANSFORM_SCHEMA,TRANSFORM_NAMESPACE);
            metadataTransformation2.setSourceSchema(TRANSFORM_1_SRC_SCHEMA);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(metadataTransformation1,TRANSFORM_ID_1);

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(metadataTransformation2,"");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (AlreadyExistsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SameStylesheetTransformationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
