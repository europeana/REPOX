import java.io.File;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.theeuropeanlibrary.repox.rest.pathOptions.MappingOptionListContainer;

import pt.utl.ist.metadataTransformation.MetadataTransformation;

/* clientTestClass.java - created on Dec 2, 2014, Copyright (c) 2011 The European Library, all rights reserved */

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
    
    public static void main(String[] args)
    {

        String mappingId = "SampleId3";
        ClientConfig config = new ClientConfig();
//        packages("org.glassfish.jersey.examples.multipart");
        config.register(MultiPartFeature.class);
        Client client = ClientBuilder.newClient(config);
        WebTarget target = client.target("http://localhost:8080/repox/rest" + "/" + MappingOptionListContainer.MAPPINGS + "/" + mappingId);

        String id = "SampleId3";
        String description = "NONE";
        String srcSchemaId = "edm";
        String srcSchemaVersion = "1.0";
        String destSchemaId = "ese";
        String destSchemaVersion = "3.3";
        String xslFilename = "myXSLT2";
        boolean isXslVersion2 = true;

        MetadataTransformation mtdTransformation = new MetadataTransformation();
        mtdTransformation.setId(id);
        mtdTransformation.setDescription(description);
        mtdTransformation.setSourceSchemaId(srcSchemaId);
        mtdTransformation.setDestinationSchemaId(destSchemaId);
        mtdTransformation.setStylesheet(xslFilename);
        mtdTransformation.setSourceSchemaVersion(srcSchemaVersion);
        mtdTransformation.setDestSchemaVersion(destSchemaVersion);
        mtdTransformation.setVersionTwo(isXslVersion2);

        // MediaType of the body part will be derived from the file.
        File tempFile = new File("/Users/simontzanakis/example.xsl");
//        tempFile.deleteOnExit();
        final FileDataBodyPart filePart = new FileDataBodyPart("myFile", tempFile, MediaType.APPLICATION_OCTET_STREAM_TYPE);
//        MetadataTransformation mtdTransformationFake = new MetadataTransformation();
        MultiPart multiPartEntity = new MultiPart();
        multiPartEntity.bodyPart(new BodyPart(mtdTransformation, MediaType.APPLICATION_XML_TYPE));
        multiPartEntity.bodyPart(filePart);
        
        Response response = target.request().put(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        
        System.out.println(response.getStatus());
    }

}
