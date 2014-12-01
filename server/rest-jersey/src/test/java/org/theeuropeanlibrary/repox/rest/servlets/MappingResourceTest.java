/* MappingResourceTest.java - created on Nov 27, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dom4j.DocumentException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.pathOptions.MappingOptionListContainer;

import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.metadataSchemas.MetadataSchema;
import pt.utl.ist.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.metadataTransformation.TransformationsFileManager;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.SameStylesheetTransformationException;

/**
 * Mappings context path handling tests.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 27, 2014
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TransformationsFileManager.class)
public class MappingResourceTest extends JerseyTest {
    DefaultDataManager            dataManager;
    MetadataTransformationManager metadataTransformationManager;
    MetadataSchemaManager         metadataSchemaManager;

    public MappingResourceTest() throws Exception {
        super(new JerseyConfigMocked());
        dataManager = JerseyConfigMocked.getDataManager();
        metadataTransformationManager = JerseyConfigMocked.getMetadataTransformationManager();
        metadataSchemaManager = JerseyConfigMocked.getMetadataSchemaManager();

        mockStatic(TransformationsFileManager.class);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }

    @Before
    public void setUpBeforeMethod() throws Exception {
        //Reset mock before every test
        reset(dataManager);
        reset(metadataTransformationManager);
        reset(metadataSchemaManager);
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.MappingResource#getOptions()}.
     */
    @Test
    @Ignore
    public void testGetOptions() {
        int numberOfAvailableOptions = 1;
        WebTarget target = target("/" + MappingOptionListContainer.MAPPINGS);

        //Check xml options working
        Response response = target.request(MediaType.APPLICATION_XML).options();
        assertEquals(200, response.getStatus());
        //Check json options working
        response = target.request(MediaType.APPLICATION_JSON).options();
        assertEquals(200, response.getStatus());
        MappingOptionListContainer molc = response.readEntity(MappingOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, molc.getOptionList().size());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.MappingResource#createMapping(MultiPart)}.
     * @throws Exception 
     */
    @Test
      @Ignore
    public void testCreateMapping() throws Exception{
        WebTarget target = target("/" + MappingOptionListContainer.MAPPINGS);

        String id = "SampleId2";
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
        final FileDataBodyPart filePart = new FileDataBodyPart("myFile", File.createTempFile("example", "xsl"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        MetadataTransformation mtdTransformationFake = new MetadataTransformation();
        MultiPart multiPartEntity = new MultiPart();
        multiPartEntity.bodyPart(new BodyPart(mtdTransformationFake, MediaType.APPLICATION_XML_TYPE));
        multiPartEntity.bodyPart(filePart);

        //Missing arguments in sent MetadataTransformation
        //Missing Id
        Response response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(406, response.getStatus());
        mtdTransformationFake.setId(id);
        //Missing sourceSchemaId
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(406, response.getStatus());
        mtdTransformationFake.setSourceSchemaId(srcSchemaId);
        //Missing destinationSchemaId
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(406, response.getStatus());
        mtdTransformationFake.setDestinationSchemaId(destSchemaId);
        //Missing styleSheet
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(406, response.getStatus());
        mtdTransformationFake.setStylesheet(xslFilename);
        //Missing srcSchemaVersion
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(406, response.getStatus());
        mtdTransformationFake.setSourceSchemaVersion(srcSchemaVersion);
        //Missing destSchemaVersion
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(406, response.getStatus());
        mtdTransformationFake.setDestSchemaVersion("3.0");
        //Stylesheet with wrong suffix
        mtdTransformationFake.setStylesheet("example.txt");
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(400, response.getStatus());
        multiPartEntity.close();

        multiPartEntity = new MultiPart();
        multiPartEntity.bodyPart(new BodyPart(mtdTransformation, MediaType.APPLICATION_XML_TYPE));
        multiPartEntity.bodyPart(filePart);

        when(metadataTransformationManager.getXsltDir()).thenReturn(null);
        when(TransformationsFileManager.writeXslFile(anyString(), any(File.class), any(InputStream.class))).thenReturn(TransformationsFileManager.Response.ERROR)
                .thenReturn(TransformationsFileManager.Response.FILE_TOO_BIG).thenReturn(TransformationsFileManager.Response.XSL_ALREADY_EXISTS)
                .thenReturn(TransformationsFileManager.Response.SUCCESS);

        //WriteXsltFile internalServerError
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(500, response.getStatus());
        //WriteXsltFile internalServerError
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(500, response.getStatus());
        //Xslt file already exists
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(409, response.getStatus());

        when(metadataSchemaManager.getSchemaXSD(anyString(), anyDouble())).thenReturn(null).thenReturn("SomeSourceSchema").thenReturn(null).thenReturn("SomeDestinationSchema");
        //Xsd source schema non existent 
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(404, response.getStatus());
        //Xsd destination schema non existent 
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(404, response.getStatus());

        MetadataSchema metadataSchema = mock(MetadataSchema.class);
        when(metadataSchemaManager.getMetadataSchema(anyString())).thenReturn(metadataSchema);
        when(metadataSchema.getNamespace()).thenReturn("SomeNamespace");

        doThrow(new IOException()).doThrow(new SameStylesheetTransformationException("Another mapping has the same xslt file!")).doNothing().when(metadataTransformationManager)
                .saveMetadataTransformation(any(MetadataTransformation.class), anyString());

        //InternalServerError when saving metdataTransformation
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(500, response.getStatus());
        //Already existent mapping with same xslt fileName
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(409, response.getStatus());

        //Valid
        response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
        assertEquals(201, response.getStatus());

        multiPartEntity.close();
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.MappingResource#getMapping(String)}.
     * @throws Exception 
     */
    @Test
    @Ignore
    public void testGetMapping() throws Exception {
        String mappingId = "SampleId2";
        WebTarget target = target("/" + MappingOptionListContainer.MAPPINGS + "/" + mappingId);
        
        doReturn(new HashMap<String, List<MetadataTransformation>>()).when(metadataTransformationManager).getMetadataTransformations();
        
        //Non existent
        Response response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.MappingResource#deleteMapping(String)
     * @throws Exception 
     */
	@Test
//	@Ignore
	public void testDeleteMapping() throws Exception {
		String mappingId = "SampleId2";
        WebTarget target = target("/" + MappingOptionListContainer.MAPPINGS + "/" + mappingId);
        
        when(metadataTransformationManager.deleteMetadataTransformation(mappingId)).thenThrow(new IOException()).thenReturn(false).thenReturn(true);
        
        //InternalServerError
        Response response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(500, response.getStatus());
        //Non existent
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(404, response.getStatus());
        //Valid call
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(200, response.getStatus());
	}
}
