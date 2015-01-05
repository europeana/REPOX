/* clientTestClass.java - created on Dec 2, 2014, Copyright (c) 2011 The European Library, all rights reserved */
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.theeuropeanlibrary.repox.rest.pathOptions.RecordOptionListContainer;

import pt.utl.ist.util.XmlUtil;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Dec 2, 2014
 */
public class clientTestClass {

    /**
     * Creates a new instance of this class.
     */
    public clientTestClass() {

    }

    public static void main(String[] args) throws IOException
    {

        String datasetId = "a0660";
        String recordId = "MyRecord:ID";
        String record = "";
        ClientConfig config = new ClientConfig();
        //    packages("org.glassfish.jersey.examples.multipart");
        config.register(MultiPartFeature.class);
        Client client = ClientBuilder.newClient(config);
        WebTarget target = client.target("http://localhost:8080/repox/rest" + "/" + RecordOptionListContainer.RECORDS).queryParam("datasetId", datasetId).queryParam("recordId", recordId);
        
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");
        Element node = root.addElement("author")
                .addAttribute("name", "exampleName")
                .addText("Some Text");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlUtil.writePrettyPrint(baos, node);
        record = baos.toString();
        
        Response response = target.request().post(Entity.entity(record, MediaType.APPLICATION_XML), Response.class);
        System.out.println(response.getStatus());
        

        //        String mappingId = "SampleId3";
        //        ClientConfig config = new ClientConfig();
        ////        packages("org.glassfish.jersey.examples.multipart");
        //        config.register(MultiPartFeature.class);
        //        Client client = ClientBuilder.newClient(config);
        //        WebTarget target = client.target("http://localhost:8080/repox/rest" + "/" + MappingOptionListContainer.MAPPINGS + "/" + mappingId);
        //
        //        String id = "SampleId3";
        //        String description = "NONE";
        //        String srcSchemaId = "edm";
        //        String srcSchemaVersion = "1.0";
        //        String destSchemaId = "ese";
        //        String destSchemaVersion = "3.3";
        //        String xslFilename = "myXSLT2";
        //        boolean isXslVersion2 = true;
        //
        //        MetadataTransformation mtdTransformation = new MetadataTransformation();
        //        mtdTransformation.setId(id);
        //        mtdTransformation.setDescription(description);
        //        mtdTransformation.setSourceSchemaId(srcSchemaId);
        //        mtdTransformation.setDestinationSchemaId(destSchemaId);
        //        mtdTransformation.setStylesheet(xslFilename);
        //        mtdTransformation.setSourceSchemaVersion(srcSchemaVersion);
        //        mtdTransformation.setDestSchemaVersion(destSchemaVersion);
        //        mtdTransformation.setVersionTwo(isXslVersion2);
        //
        //        // MediaType of the body part will be derived from the file.
        //        File tempFile = new File("/Users/simontzanakis/example.xsl");
        ////        tempFile.deleteOnExit();
        //        final FileDataBodyPart filePart = new FileDataBodyPart("myFile", tempFile, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        ////        MetadataTransformation mtdTransformationFake = new MetadataTransformation();
        //        MultiPart multiPartEntity = new MultiPart();
        //        multiPartEntity.bodyPart(new BodyPart(mtdTransformation, MediaType.APPLICATION_XML_TYPE));
        //        multiPartEntity.bodyPart(filePart);
        //        
        //        Response response = target.request().put(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        //        
        //        System.out.println(response.getStatus());
    }

}
