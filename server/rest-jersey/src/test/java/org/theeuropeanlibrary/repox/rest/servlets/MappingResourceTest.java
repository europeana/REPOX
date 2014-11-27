/* MappingResourceTest.java - created on Nov 27, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;

import java.io.File;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.pathOptions.MappingOptionListContainer;

import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.task.TaskManager;

/**
 * Mappings context path handling tests.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 27, 2014
 */
public class MappingResourceTest extends JerseyTest {
    DefaultDataManager dataManager;
    TaskManager        taskManager;

    public MappingResourceTest() throws Exception {
        super(new JerseyConfigMocked());
        dataManager = JerseyConfigMocked.getDataManager();
        taskManager = JerseyConfigMocked.getTaskManager();
    }
    
    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }

    @Before
    public void setUpBeforeMethod() throws Exception {
        //Reset mock before every test
        reset(dataManager);
        reset(taskManager);
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

//    @Test
//    //  @Ignore
//    public void testUploadFile() throws Exception {
//
//        
//        WebTarget target = target("/" + MappingOptionListContainer.MAPPINGS);
//
//        // MediaType of the body part will be derived from the file.
//        final FileDataBodyPart filePart = new FileDataBodyPart("myFile", new File("/tmp/ex.txt"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
//        
//        final MultiPart multiPartEntity = new MultiPart();
//        //TODO Add transformation data in first bodypart.
//        multiPartEntity.bodyPart(new BodyPart().entity("hello"));
//        multiPartEntity.bodyPart(filePart);
//
//        Response response = target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")), Response.class);
//        assertEquals(200, response.getStatus());
//    }

}
