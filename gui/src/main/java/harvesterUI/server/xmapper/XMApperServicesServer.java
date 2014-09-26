package harvesterUI.server.xmapper;

import harvesterUI.server.util.Util;
import harvesterUI.shared.ServerSideException;
import harvesterUI.shared.mdr.TransformationUI;
import harvesterUI.shared.servletResponseStates.ResponseState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FilenameUtils;
import org.xml.sax.InputSource;

import pt.ist.mdr.mapping.ui.client.model.MappingScriptProxy;
import pt.ist.mdr.mapping.ui.server.Mapping2UIAdapter;
import pt.ist.mdr.mapping.ui.server.UI2MappingAdapter;
import pt.ist.mdr.model.utils.ModelUtils;
import pt.ist.mdr.schema.xml.XMLSchema;
import pt.ist.mdr.schema.xml.support.xsd.XSDSupport;
import pt.ist.mdr.system.services.admin.AdministrationService;
import pt.ist.mdr.system.services.admin.Service;
import pt.ist.xml.mapping.compiler.XSLTCompiler;
import pt.ist.xml.mapping.impl.MappingFactoryImpl;
import pt.ist.xml.mapping.impl.MappingModelImpl;
import pt.ist.xml.mapping.io.MappingResolver;
import pt.ist.xml.mapping.io.XMLMappingReader;
import pt.ist.xml.mapping.io.XMLMappingWriter;
import pt.ist.xml.mapping.spec.MappingFactory;
import pt.ist.xml.mapping.spec.MappingScript;
import pt.ist.xml.mapping.toolset.ToolsetManagerImpl;
import pt.ist.xml.mapping.toolset.function.XsltFunction;
import pt.ist.xml.mapping.toolset.library.XsltToolsetLibrary;
import pt.ist.xslt.io.XsltWriter;
import pt.ist.xslt.version1.XsltStylesheet;
import pt.utl.ist.repox.configuration.ConfigSingleton;
import pt.utl.ist.repox.metadataTransformation.MetadataTransformation;
import pt.utl.ist.repox.util.exceptions.AlreadyExistsException;
import pt.utl.ist.repox.util.exceptions.SameStylesheetTransformationException;

/**
 * Created to REPOX.
 * User: Higgs
 * Date: 02-10-2012
 * Time: 18:26
 */
public class XMApperServicesServer {

    protected static AdministrationService _service = Service.getService();
    protected static String XMAP_END = ".xmap";
    protected static String XSL_END = ".xsl"; //TODO

    public XMApperServicesServer() {
        _service = Service.getService();
    }

    /****************************************************/
    /*             Class's Protected Methods    	    */
    /****************************************************/

    protected static XMLSchema getSchema(String url) throws IOException {
        return ModelUtils.loadModel(new InputSource(url), XMLSchema.class, null);
    }

    protected static XMLSchema getXSDSchema(URL url) throws IOException {
        return new XSDSupport().parse(null, url, null);
    }

    protected static MappingScript getMapping(XMLSchema source, XMLSchema target, String url) throws IOException {

        MappingScript mapping = new XMLMappingReader(new MappingFactoryImpl(ModelUtils.getRegistry())).parse(new InputSource(url));
        mapping.setSource(source);
        mapping.setTarget(target);
        new MappingResolver(ModelUtils.getRegistry()).resolveTemplate(mapping);
        return mapping;
    }

    protected static MappingScript getEmptyMapping(XMLSchema source, XMLSchema target, String newMapID) throws IOException {
        MappingScript mapping;
        if(newMapID != null)
            mapping = new MappingModelImpl(newMapID, source, target);
        else {
            newMapID =   "http://"+source.getID()+target.getID()+UUID.randomUUID().toString(); //Not incremental but semi-random...
            mapping = new MappingModelImpl(newMapID, source, target);
        }

        mapping.setSource(source);
        mapping.setTarget(target);
        new MappingResolver(ModelUtils.getRegistry()).resolveTemplate(mapping);
        return mapping;
    }

    protected static MappingScript getMappingFromXSLT(XMLSchema source, XMLSchema target, String xslt_url){
        //TODO
        return null;
    }

    protected static boolean deleteDir(File dir) {
        // First delete all folder contents
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    protected static ResponseState validateTransformation(String id, String xslFilePath, String oldTransId) throws ServerSideException{
        try {

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    checkTransformationValidity(id, xslFilePath+XSL_END, oldTransId);

            return ResponseState.SUCCESS;
        } catch (SameStylesheetTransformationException e) {
            return ResponseState.MAPPING_SAME_XSL;
        }catch (AlreadyExistsException e) {
            return ResponseState.ALREADY_EXISTS;
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    protected static ResponseState saveTransformationMDR(TransformationUI transformationUI, String oldTransId) throws ServerSideException{
        try {
            String xslFilePath = FilenameUtils.removeExtension(transformationUI.getXslFilePath().toLowerCase())+XSL_END;
            //System.out.println("SERVER - XSL path: " + xslFilePath);

            MetadataTransformation mtdTransformation = new MetadataTransformation(transformationUI.getIdentifier(),
                    transformationUI.getDescription(),transformationUI.getSrcFormat(),
                    transformationUI.getDestFormat(),xslFilePath,
                    transformationUI.isEditable(),transformationUI.getIsXslVersion2(),transformationUI.getDestSchema(),transformationUI.getDestMetadataNamespace());
            mtdTransformation.setSourceSchema(transformationUI.getSourceSchema());
            mtdTransformation.setMDRCompliant(transformationUI.isMDRCompliant());

            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().
                    saveMetadataTransformation(mtdTransformation,oldTransId);
            //System.out.println("SERVER - Transformation saved on the MDR");
            return ResponseState.SUCCESS;
        } catch (SameStylesheetTransformationException e) {
            return ResponseState.MAPPING_SAME_XSL;
        }catch (AlreadyExistsException e) {
            return ResponseState.ALREADY_EXISTS;
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerSideException(Util.stackTraceToString(e));
        }
    }

    protected static void createXmapFile(String fileName, MappingScript script)
            throws IOException{
        File xmapDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getXmapDir();
        if(!xmapDir.exists())
            xmapDir.mkdirs();

        File xmapFile = new File(xmapDir, fileName.toLowerCase()+XMAP_END);
        StreamResult tmpResult = new StreamResult(new FileOutputStream(xmapFile));

        new XMLMappingWriter().write(script, tmpResult);
        tmpResult.getOutputStream().close();
        //System.out.println("SERVER - XMAP created");
    }

    protected static void createXslFile(String fileName, MappingScript script) throws IOException{
        File xsltDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getXsltDir();
        if(!xsltDir.exists())
            xsltDir.mkdirs();

        File xslFile = new File(xsltDir, fileName.toLowerCase()+XSL_END);
        StreamResult tmpResult = new StreamResult(new FileOutputStream(xslFile));
        XsltStylesheet xslt = new XSLTCompiler(
                new ToolsetManagerImpl<XsltFunction>(XsltToolsetLibrary.getToolsets())).
                compile(script);

        new XsltWriter().write(xslt, tmpResult);
        tmpResult.getOutputStream().close();

        /* DUMMY FILE CREATION CODE
        File xsltDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getXsltDir();
        if(!xsltDir.exists())
            xsltDir.mkdirs();

        tmpFile = new File(xsltDir, fileName.toLowerCase()+XSL_END);
        FileWriter fstream = new FileWriter(tmpFile);
        BufferedWriter outFile = new BufferedWriter(fstream);
        outFile.write("<This is a dummy xslt>");
        outFile.close();*/

        //System.out.println("SERVER - XSL created");
    }

    /****************************************************/
    /*         Class's Public Service Methods           */
    /****************************************************/

    // Server-provided tests
    public static MappingScriptProxy getTestEmptyMappingModel() {
        try {
            URL url = new URL("http://www.europeana.eu/schemas/edm/EDM.xsd");
            XMLSchema source = getXSDSchema(url);
            url = new URL("http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
            XMLSchema target = getXSDSchema(url);

            MappingScript mapping = getEmptyMapping(source, target, null);
            Mapping2UIAdapter adapter = new Mapping2UIAdapter();
            return adapter.adapt(mapping);
        }
        catch(IOException e) {
            return null;
        }
    }
    /*Tests end*/

    public static MappingScriptProxy getEmptyUserMappingModel(String source, String dest) {
        try {//usar getSchema??
            URL url = new URL(source);
            XMLSchema sxml = getXSDSchema(url);
            url = new URL(dest);
            XMLSchema dxml = getXSDSchema(url);

            MappingScript mapping = getEmptyMapping(sxml, dxml, null);
            Mapping2UIAdapter adapter = new Mapping2UIAdapter();
            return adapter.adapt(mapping);
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer generateScript(String uri) {
        System.out.println("SERVER - mapping URI: "+uri);
        return 42;
    }

    public static ResponseState saveMapping(MappingScriptProxy mappings, TransformationUI transformationUI, String oldTransId) {
//        System.out.println("SERVER - Save Mapping: "+mappings.getID());
//        System.out.println("SERVER - Target ID: "+mappings.getTargetModel().getID());
//        System.out.println("SERVER - Source ID: "+mappings.getSourceModel().getID());
        ResponseState response;

        try {
            //00 validate transformation
            response = validateTransformation(transformationUI.getIdentifier(),
                    FilenameUtils.removeExtension(transformationUI.getXslFilePath()), oldTransId);
            if(response != ResponseState.SUCCESS)
                return response;
//            System.out.println("SERVER - The transformation is valid");

            //Setup stuff for file writing
            URL tmpUrl = new URL(transformationUI.getSourceSchema());
            XMLSchema source = getXSDSchema(tmpUrl);
            tmpUrl = new URL(transformationUI.getDestSchema());
            XMLSchema target = getXSDSchema(tmpUrl);
            MappingFactory factory = new MappingFactoryImpl(ModelUtils.getRegistry());
            MappingScript script = new UI2MappingAdapter(factory, source, target).adapt(mappings);

            //  1st STEP - create the xmap file
            String fileName = FilenameUtils.removeExtension(transformationUI.getXslFilePath());
            createXmapFile(fileName, script);

            // 2nd STEP - Create the xsl file
            createXslFile(fileName, script);

            // 3rd STEP - Register transformation in the mdr
            response = saveTransformationMDR(transformationUI, oldTransId);
            //System.out.println("SERVER - EDITABLE: "+transformationUI.isEditable());
        }
        catch(Exception e){
            e.printStackTrace();
            response = ResponseState.ERROR;
        }

        return response;
    }
    /*public static MappingScriptProxy getTestExistingMappingModel(String resourcesPath) {
        try {
            String filePath = resourcesPath+TEST_SOURCE;
            System.out.println("SERVER INFO - source path(SERVER): "+filePath);
            XMLSchema source = getDestSchema(filePath);
            filePath = resourcesPath+TEST_TARGET;
            XMLSchema target = getDestSchema(filePath);
            //
            filePath = resourcesPath+TEST_MAP;
            MappingScript mapping = getMapping(source, target, filePath);
            return new Mapping2UIAdapter().adapt(mapping);
        }
        catch(IOException e) {
            return null;
        }
    }*/

    /* public static MappingScriptProxy getEmptyMappingModel(ServletContext context, OpenMappingForm form){
        String baseURL = context.getServletContextName(); //TO DO currently returns "XMApper"
        String mapID = "http://"+baseURL+"/"+UUID.randomUUID().toString();

        if (form != null) {
            if(form.getMapID() != null)
                mapID = form.getMapID();
            if(form.isFromURL()) {
                try {
                    URL url = new URL(form.getSource());
                    XMLSchema source = getXSDSchema(url);
                    url = new URL(form.getTarget());
                    XMLSchema target = getXSDSchema(url);

                    MappingScript mapping = getEmptyMapping(source, target, mapID);
                    return new Mapping2UIAdapter().adapt(mapping);
                }
                catch(IOException e) {
                    return null;
                }
            }
            else {
                try {
                    String resourcesPath = context.getRealPath(RESOURCES_ENDPATH);
                    String filePath = resourcesPath+TEMP_FOLDER+File.separator+"0"+FilenameUtils.getName(form.getSource());	// #0 - source schema
                    //System.out.println("SERVER INFO - source path(USER): "+filePath);
                    XMLSchema source = getDestSchema(filePath);
                    filePath = resourcesPath+TEMP_FOLDER+File.separator+"1"+FilenameUtils.getName(form.getTarget());			// #1 - target schema
                    XMLSchema target = getDestSchema(filePath);

                    MappingScript mapping = getEmptyMapping(source, target, mapID);

                    //Delete tmp folder
                    deleteDir(new File(resourcesPath+TEMP_FOLDER));
                    return new Mapping2UIAdapter().adapt(mapping);
                }
                catch(IOException e) {
                    return null;
                }
            }
        }
        return null;
    }*/

    public static MappingScriptProxy getExistingMappingModel(TransformationUI transformationUI) {
        try {
            File xmapDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getXmapDir();
            String mapFilePath = xmapDir.getPath() + File.separator + FilenameUtils.removeExtension(transformationUI.getXslFilePath()) + ".xmap";

            URL tmp = new URL(transformationUI.getSourceSchema());
            XMLSchema source = getXSDSchema(tmp);
            tmp = new URL(transformationUI.getDestSchema());
            XMLSchema target = getXSDSchema(tmp);
            MappingScript mapping = getMapping(source, target, mapFilePath);
            return new Mapping2UIAdapter().adapt(mapping);
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

