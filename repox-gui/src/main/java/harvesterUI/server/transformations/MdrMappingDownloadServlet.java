//package harvesterUI.server.transformations;
//
//import harvesterUI.server.RepoxServiceImpl;
//import harvesterUI.server.projects.Light.LightSaveData;
//import org.dom4j.Document;
//import org.dom4j.io.SAXReader;
//import pt.utl.ist.repox.util.ConfigSingleton;
//import pt.utl.ist.repox.util.XmlUtil;
//
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.xml.transform.Result;
//import javax.xml.transform.Source;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import java.io.*;
//import java.net.URL;
//
///**
// * Created to REPOX.
// * User: Edmundo
// * Date: 27-05-2011
// * Time: 11:43
// */
//public class MdrMappingDownloadServlet extends HttpServlet {
//    public void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
//        try{
//            String identifier = (String)request.getParameter("fileName");
//
//            String uri = LightSaveData.decodeMDRId(identifier);
//            String statusUri = RepoxServiceImpl.getRepoxManager().getConfiguration().getMdrUrl() +
//                    "/services/provider/getTranslation?uri=" + TransformationsServiceImpl.forURL(uri) + "&mimetype="+TransformationsServiceImpl.forURL("application/xslt+xml");
//
//            SAXReader reader = new SAXReader();
//            Document document = reader.read(new URL(statusUri));
//
//            int length   = 0;
//            ServletOutputStream op = response.getOutputStream();
////            ServletContext context = getServletConfig().getServletContext();
//            String mimetype = "application/xslt+xml";
//
//            response.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
////            response.setContentLength( (int)xsltFile.length() );
//            response.setHeader( "Content-Disposition", "attachment; filename=\"" + identifier+".xsl" + "\"" );
//
//            XmlUtil.writePrettyPrint(op,document);
//
//            op.flush();
//            op.close();
//
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//}
//
//
