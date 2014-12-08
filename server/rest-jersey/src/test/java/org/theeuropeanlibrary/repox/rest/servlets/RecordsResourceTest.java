/* RecordsResourceTest.java - created on Dec 5, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.pathOptions.RecordOptionListContainer;

import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.util.InvalidInputException;
import pt.utl.ist.util.Urn;
import pt.utl.ist.util.XmlUtil;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

/**
 * Records context path handling tests.
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Dec 5, 2014
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Urn.class)
public class RecordsResourceTest extends JerseyTest {
    DefaultDataManager dataManager;

    public RecordsResourceTest() throws Exception {
        super(new JerseyConfigMocked());
        dataManager = JerseyConfigMocked.getDataManager();
    }

    @Before
    public void setUpBeforeMethod() throws Exception {
        //Reset mock before every test
        reset(dataManager);
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.RecordsResource#getOptions()}.
     */
    @Test
//    @Ignore
    public void testGetOptions() {
        int numberOfAvailableOptions = 5;
        WebTarget target = target("/" + RecordOptionListContainer.RECORDS);

        //Check xml options working
        Response response = target.request(MediaType.APPLICATION_XML).options();
        assertEquals(200, response.getStatus());
        //Check json options working
        response = target.request(MediaType.APPLICATION_JSON).options();
        assertEquals(200, response.getStatus());
        RecordOptionListContainer rolc = response.readEntity(RecordOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, rolc.getOptionList().size());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.RecordsResource#getRecord(String)}.
     * @throws Exception 
     */
    @Test
//    @Ignore
    public void testGetRecord() throws Exception {
        String recordId = "recordIdSample";
        WebTarget target = target("/" + RecordOptionListContainer.RECORDS).queryParam("recordId", recordId);

        //Mocking
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");
        Element node = root.addElement("author")
                .addAttribute("name", "exampleName")
                .addText("Some Text");
        Urn anyUrn = any(Urn.class);
        when(dataManager.getRecord(anyUrn)).thenThrow(new IOException()).thenThrow(new ObjectNotFoundException(recordId)).thenReturn(null).thenReturn(node);

        //Internal Server Error
        Response response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(500, response.getStatus());
        //Record not existent
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
        //Record not existent
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
        //Valid Call
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.RecordsResource#removeRecord(String, String)}.
     * @throws Exception 
     */
    @Test
//        @Ignore
    public void testRemoveRecord() throws Exception {
        String recordId = "recordIdSample";
        //Deleting
        //Record not existent
        WebTarget target = target("/" + RecordOptionListContainer.RECORDS).queryParam("recordId", recordId).queryParam("type", RecordOptionListContainer.DELETE);
        when(dataManager.deleteRecord(recordId)).thenThrow(new IOException()).thenThrow(new ObjectNotFoundException("Record with id: " + recordId + " does NOT exist!")).thenThrow(new InvalidInputException("Invalid")).thenReturn(MessageType.OK);

        //Internal Server Error
        Response response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(500, response.getStatus());
        //Record not existent
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(404, response.getStatus());
        //Invalid argument
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(400, response.getStatus());
        //Valid Call
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(200, response.getStatus());

        //Erasing
        target = target("/" + RecordOptionListContainer.RECORDS).queryParam("recordId", recordId).queryParam("type", RecordOptionListContainer.ERASE);
        when(dataManager.eraseRecord(recordId)).thenThrow(new IOException()).thenThrow(new ObjectNotFoundException("Record with id: " + recordId + " does NOT exist!")).thenThrow(new InvalidInputException("Invalid")).thenReturn(MessageType.OK);

        //Internal Server Error
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(500, response.getStatus());
        //Record not existent
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(404, response.getStatus());
        //Invalid argument
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(400, response.getStatus());
        //Valid Call
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(200, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.RecordsResource#createRecord(String, String, String)}.
     * @throws Exception 
     */
    @Test
//    @Ignore
    public void testCreateRecord() throws Exception {
        String recordId = "recordIdSample";
        String datasetId = "datasetIdSample";
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");
        Element node = root.addElement("author")
                .addAttribute("name", "exampleName")
                .addText("Some Text");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlUtil.writePrettyPrint(baos, node);
        baos.toString();
        
        when(dataManager.saveRecord(anyString(), anyString(), anyString())).thenThrow(new IOException()).thenThrow(new ObjectNotFoundException("Not found!")).thenReturn(MessageType.OK);
        
        //Empty datasetId
        WebTarget target = target("/" + RecordOptionListContainer.RECORDS).queryParam("datasetId", "").queryParam("recordId", recordId);
        Response response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(baos.toString(), MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());

        //Empty recordId
        target = target("/" + RecordOptionListContainer.RECORDS).queryParam("datasetId", datasetId).queryParam("recordId", "");
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(baos.toString(), MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        
        //Empty record data
        target = target("/" + RecordOptionListContainer.RECORDS).queryParam("datasetId", datasetId).queryParam("recordId", recordId);
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity("", MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        
        //Internal Server error
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(baos.toString(), MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Not found
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(baos.toString(), MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Valid call
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(baos.toString(), MediaType.APPLICATION_XML), Response.class);
        assertEquals(201, response.getStatus());
    }
}
