/* TestSoapServlet.java - created on 19 de Abr de 2013, Copyright (c) 2011 The European Library, all rights reserved */
package harvesterUI.server.sru;



import harvesterUI.shared.ProjectType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.dom4j.Element;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.configuration.EuropeanaRepoxContextUtil;
import pt.utl.ist.configuration.RepoxManager;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.repox.recordPackage.RecordRepox;
import pt.utl.ist.repox.recordPackage.RecordRepoxExternalId;
import pt.utl.ist.repox.util.PropertyUtil;
import pt.utl.ist.sru.SruRecordUpdateDataSource;

/**
 *
 *
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 19 de Abr de 2013
 */
public class SruRecordUpdateServlet extends HttpServlet {

    private RepoxManager repoxManager;

    MessageFactory messageFactory;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);

        Properties properties = PropertyUtil.loadGuiConfiguration("gui.properties");
        ProjectType projectType = ProjectType.valueOf(properties.getProperty("project.type"));

        if(projectType == ProjectType.LIGHT){
            ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
            this.repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        }else if(projectType == ProjectType.EUROPEANA){
            ConfigSingleton.setRepoxContextUtil(new EuropeanaRepoxContextUtil());
            this.repoxManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager();
        }
        try {
            messageFactory = MessageFactory.newInstance();
        } catch (SOAPException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    protected void doRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String dataSetId=req.getPathInfo();
            if(dataSetId.startsWith("/"))
                dataSetId=dataSetId.substring(1);

            DataSourceContainer dataSource = repoxManager.getDataManager().getDataSourceContainer(dataSetId);
            if(dataSource==null) {
                sendSenderFault("Unknown data set: "+dataSetId, resp);
                return;
            }
            if(!(dataSource.getDataSource() instanceof SruRecordUpdateDataSource)) {
                sendSenderFault("SRU record update not allowed for data set: "+dataSetId, resp);
                return;
            }

            InputStream inStream=req.getInputStream();

            SOAPMessage incomingMessage = messageFactory.createMessage(new MimeHeaders(), inStream);

            SOAPPart soappart = incomingMessage.getSOAPPart();
            SOAPEnvelope envelope = soappart.getEnvelope();
            SOAPHeader header = envelope.getHeader();
            SOAPBody body = envelope.getBody();

            SOAPElement requestEl=SoapUtil.getFirstChild(body, new QName("http://www.loc.gov/zing/srw/update/", "updateRequest"));
            if(requestEl==null) {
                sendSenderFault("Not a valid SRU Update Record Request", resp);
                return;
            }
            SOAPElement actionEl=SoapUtil.getFirstChild(requestEl, new QName("http://www.loc.gov/zing/srw/update/", "action"));
            SOAPElement recIdEl=SoapUtil.getFirstChild(requestEl, new QName("http://www.loc.gov/zing/srw/update/", "recordIdentifier"));
            SOAPElement recEl=SoapUtil.getFirstChild(requestEl, new QName("http://www.loc.gov/zing/srw/", "record"));

            Action action=actionEl==null ? null : Action.fromUri(actionEl.getTextContent());
            String recId= recIdEl==null ? null : recIdEl.getTextContent();

            if(action==null) {
                sendSenderFault("'action' is missing", resp);
                return;
            }
            if(recId==null) {
                sendSenderFault("'recordIdentifier' is missing", resp);
                return;
            }
            if(action!=Action.DELETE && recEl==null) {
                sendSenderFault("'record' is missing", resp);
                return;
            }

            SOAPElement xmlRec=null;
            if(action!=Action.DELETE) {
                SOAPElement recDataEl=null;
                if(recEl!=null)
                    recDataEl=SoapUtil.getFirstChild(recEl, new QName("http://www.loc.gov/zing/srw/", "recordData"));
                if(recDataEl!=null)
                    xmlRec=SoapUtil.getFirstElement(recDataEl);
                if(xmlRec==null) {
                    sendSenderFault("'record' is missing", resp);
                    return;
                }
            }

            RecordRepoxExternalId recRepox=null;
            switch (action) {
                case REPLACE:
                case CREATE:{
                    org.dom4j.Document dom4jDocument = SoapUtil.toDom4jDocument(incomingMessage);
                    Element bodyElement = dom4jDocument.getRootElement().element("Body");
                    Element requestElement = bodyElement.element("updateRequest");
                    Element sruRecElement = requestElement.element("record");
                    Element recElement = sruRecElement.element("recordData");
                    if(recElement==null || recElement.elements().isEmpty()) {
                        sendSenderFault("'record' is missing", resp);
                        return;
                    }
                    if(recElement.elements().size()>1) {
                        sendSenderFault("Invalid record xml", resp);
                        return;
                    }
                    recElement = (Element)recElement.elements().get(0);
                    recRepox=new RecordRepoxExternalId(recElement, recId, false);
                    break;
                }case DELETE:{
                    recRepox=new RecordRepoxExternalId(null, recId, true);
                    break;
                }
            }




            ArrayList<RecordRepox> recInList=new ArrayList<RecordRepox>(1);
            recInList.add(recRepox);
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getAccessPointsManager().processRecords(dataSource.getDataSource(), recInList,null);

            //update record's count
            ConfigSingleton.getRepoxContextUtil().getRepoxManager().getRecordCountManager().getRecordCount(dataSource.getDataSource().getId(), true);

            // update dataProviders.xml
            //ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().saveData();

            sendSruResponse(recId, OperationStatus.SUCCESS, resp);
        } catch (Exception e) {
            e.printStackTrace();
            sendReceiverFault(e, resp);
        }
    }


    private void sendSruResponse(String recordIdentifier, OperationStatus status, HttpServletResponse resp) throws ServletException {
        try {
            SOAPMessage outgoingMessage = messageFactory.createMessage();

            SOAPPart soappart = outgoingMessage.getSOAPPart();
            SOAPEnvelope envelope = soappart.getEnvelope();
            SOAPBody body = envelope.getBody();

            SOAPBodyElement updRespEl = body.addBodyElement(new QName("http://www.loc.gov/zing/srw/update/", "updateResponse"));
            updRespEl.addChildElement(new QName("http://www.loc.gov/zing/srw/", "version")).setTextContent("1.0");
            updRespEl.addChildElement(new QName("http://www.loc.gov/zing/srw/update/", "operationStatus")).setTextContent(status.toSruCode());
            updRespEl.addChildElement(new QName("http://www.loc.gov/zing/srw/update/", "recordIdentifier")).setTextContent(recordIdentifier);
            updRespEl.addChildElement(new QName("http://www.loc.gov/zing/srw/", "extraResponseData"));

            resp.setContentType("text/xml");
            ServletOutputStream os = resp.getOutputStream();
            outgoingMessage.writeTo(os);
            //os.flush();
            //os.close();
        } catch (SOAPException ex) {
            throw new ServletException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new ServletException(ex.getMessage(), ex);
        }
    }

    private void sendReceiverFault(Exception e, HttpServletResponse resp) throws ServletException {
        try {
            SOAPMessage outgoingMessage = messageFactory.createMessage();

            SOAPPart soappart = outgoingMessage.getSOAPPart();
            SOAPEnvelope envelope = soappart.getEnvelope();
            SOAPBody body = envelope.getBody();
            SOAPFault fault = body.addFault();

            fault.setFaultCode(new QName("http://www.w3.org/2003/05/soap-envelope", "Receiver"));
            fault.setFaultString(e.getClass().getName()+ (e.getMessage() == null ? "" : e.getMessage()), Locale.ENGLISH);

            resp.setContentType("text/xml");
            ServletOutputStream os = resp.getOutputStream();
            outgoingMessage.writeTo(os);
            //os.flush();
            //os.close();
        } catch (SOAPException ex) {
            throw new ServletException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new ServletException(ex.getMessage(), ex);
        }
    }

    private void sendSenderFault(String message, HttpServletResponse resp) throws ServletException {
        try {
            SOAPMessage outgoingMessage = messageFactory.createMessage();

            SOAPPart soappart = outgoingMessage.getSOAPPart();
            SOAPEnvelope envelope = soappart.getEnvelope();
            SOAPBody body = envelope.getBody();
            SOAPFault fault = body.addFault();

            fault.setFaultCode(new QName("http://www.w3.org/2003/05/soap-envelope", "Sender"));
            fault.setFaultString(message, Locale.ENGLISH);

            resp.setContentType("text/xml");
            ServletOutputStream os = resp.getOutputStream();
            outgoingMessage.writeTo(os);
            //os.flush();
            //os.close();
        } catch (SOAPException ex) {
            throw new ServletException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new ServletException(ex.getMessage(), ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doRequest(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doRequest(req, resp);
    }
}
