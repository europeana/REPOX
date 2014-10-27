/* ProvidersResourceTest.java - created on Oct 24, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dom4j.DocumentException;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigMocked;
import org.theeuropeanlibrary.repox.rest.pathOptions.ProviderOptionListContainer;

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

/**
 * Providers context path handling tests.
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 24, 2014
 */
public class ProvidersResourceTest extends JerseyTest {

    DefaultDataManager dataManager;

    public ProvidersResourceTest() throws Exception {
        super(new JerseyConfigMocked());
        dataManager = JerseyConfigMocked.getDataManager();
    }

    @Before
    public void setUpBeforeMethod() throws Exception {
        //Reset mock before every test
        reset(dataManager);
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.ProvidersResource#getOptions()}.
     */
    @Test
    @Ignore
    public void testGetOptions() {
        int numberOfAvailableOptions = 2;
        WebTarget target = target("/" + ProviderOptionListContainer.PROVIDERS);

        //Check xml options working
        Response response = target.request(MediaType.APPLICATION_XML).options();
        assertEquals(200, response.getStatus());
        //Check json options working
        response = target.request(MediaType.APPLICATION_JSON).options();
        assertEquals(200, response.getStatus());
        ProviderOptionListContainer polc = response.readEntity(ProviderOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, polc.getOptionList().size());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.ProvidersResource#getProvider(java.lang.String)}.
     * @throws MalformedURLException 
     */
    @Test
    @Ignore
    public void testGetProvider() throws MalformedURLException {
        String providerId = "SampleId";
        //Mocking
        when(dataManager.getDataProvider(providerId))
                .thenReturn(new DataProvider(providerId, "testName", "testCountry", "testDescription", null, "testNameCode", "testHomePage", ProviderType.LIBRARY));

        WebTarget target = target("/" + ProviderOptionListContainer.PROVIDERS + "/" + providerId);
        //Check xml head working
        Response response = target.request(MediaType.APPLICATION_XML).head();
        assertEquals(200, response.getStatus());
        //Check json head working
        response = target.request(MediaType.APPLICATION_JSON).head();
        assertEquals(200, response.getStatus());
        //Check get xml working with 200 status
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());
        DataProvider provider = response.readEntity(DataProvider.class);
        //Check get json working with 200 status
        response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        provider = response.readEntity(DataProvider.class);
        assertEquals(providerId, provider.getId());

        //Check Errors
        //Check get xml working with 404 status
        target = target("/" + ProviderOptionListContainer.PROVIDERS + "/" + "FakeAggregatorId");
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
        //Check get xml working with 404 status
        response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(404, response.getStatus());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.ProvidersResource#createProvider(String, DataProvider)}.
     * @throws AlreadyExistsException 
     * @throws InvalidArgumentsException 
     * @throws IOException 
     * @throws DocumentException 
     * @throws ObjectNotFoundException 
     */
    @Test
    @Ignore
    public void testCreateProvider() throws DocumentException, IOException, InvalidArgumentsException, AlreadyExistsException, ObjectNotFoundException {
        String aggregatorId = "SampleAggregatorId";
        WebTarget target = target("/" + ProviderOptionListContainer.PROVIDERS).queryParam("aggregatorId", aggregatorId);

        //Mocking
        DataProvider dataProvider = new DataProvider("SampleId", "SampleName", "SampleCounty", "SampleDescription", null, "SampleNameCode", "http://example.com", ProviderType.LIBRARY);
        when(
                dataManager.createDataProvider(aggregatorId, dataProvider.getId(), dataProvider.getName(), dataProvider.getCountry(), dataProvider.getDescription(), dataProvider.getNameCode(),
                        dataProvider.getHomePage(), dataProvider.getProviderType().toString(), dataProvider.getEmail())).thenReturn(dataProvider).thenThrow(new ObjectNotFoundException(aggregatorId))
                .thenThrow(new AlreadyExistsException("DataProvider " + dataProvider.getId() + " already exists!")).thenThrow(new InvalidArgumentsException("Invalid Argument URL"))
                .thenThrow(new IOException());

        //Valid request created
        Response response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(201, response.getStatus());
        //Aggregator not existent
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Already exists
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(409, response.getStatus());
        //Invalid URL
        response = target.request(MediaType.APPLICATION_XML).post(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());
        //Internal Server Error        
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Missing query parameter aggregatorId
        target = target("/" + ProviderOptionListContainer.PROVIDERS);
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing argument name
        dataProvider = new DataProvider("SampleId", null, "SampleCounty", "SampleDescription", null, "SampleNameCode", "http://example.com", ProviderType.LIBRARY);
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing argument country
        dataProvider = new DataProvider("SampleId", "SampleName", null, "SampleDescription", null, "SampleNameCode", "http://example.com", ProviderType.LIBRARY);
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing argument provider type
        dataProvider = new DataProvider("SampleId", "SampleName", "SampleCounty", "SampleDescription", null, "SampleNameCode", "http://example.com", null);
        response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());

    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.AggregatorsResource#deleteAggregator(String)}.
     * @throws Exception 
     * @throws ObjectNotFoundException 
     * @throws DocumentException 
     * @throws IOException 
     */
    @Test
    @Ignore
    public void testDeleteProvider() throws Exception, DocumentException, ObjectNotFoundException {
        String providerId = "SampleProviderId";
        WebTarget target = target("/" + ProviderOptionListContainer.PROVIDERS + "/" + providerId);

        //Mocking
        doNothing().doThrow(new IOException()).doThrow(new ObjectNotFoundException(providerId)).when(dataManager)
                .deleteDataProvider(providerId);

        //Valid call
        Response response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(200, response.getStatus());
        //Two internal server error exceptions
        response = target.request(MediaType.APPLICATION_XML).delete();
        assertEquals(500, response.getStatus());
        //Resource does NOT exist
        response = target.request(MediaType.APPLICATION_JSON).delete();
        assertEquals(404, response.getStatus());
    }

    /**
     * 
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.ProvidersResource#updateProvider(String, String, DataProvider)}.
     * @throws Exception
     */
    @Test
//    @Ignore
    public void testUpdateProvider() throws Exception {
        String providerId = "SampleProviderId";
        DataProvider dataProvider = new DataProvider("NewSampleId", "SampleName", "SampleCounty", "SampleDescription", null, "SampleNameCode", "http://example.com", ProviderType.LIBRARY);
        WebTarget target = target("/" + ProviderOptionListContainer.PROVIDERS + "/" + providerId);

        //Mocking
        when(dataManager.updateDataProvider(null, providerId, dataProvider.getId(), dataProvider.getName(), dataProvider.getCountry(), dataProvider.getDescription(), dataProvider.getNameCode(),
                        dataProvider.getHomePage(), dataProvider.getProviderType().toString(), dataProvider.getEmail()))
                .thenReturn(dataProvider).thenThrow(new IOException()).thenThrow(new ObjectNotFoundException(providerId))
                .thenThrow(new InvalidArgumentsException());

        //Valid call
        Response response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(200, response.getStatus());
        //Two internal server error exception
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(500, response.getStatus());
        //Resource does NOT exist
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(404, response.getStatus());
        //Invalid URL
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(dataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(400, response.getStatus());
        //Missing name
        DataProvider missingArgDataProvider = new DataProvider("NewSampleId", null, "SampleCounty", "SampleDescription", null, "SampleNameCode", "http://example.com", ProviderType.LIBRARY);
        response = target.request(MediaType.APPLICATION_XML).put(Entity.entity(missingArgDataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing country
        missingArgDataProvider = new DataProvider("NewSampleId", "SampleName", null, "SampleDescription", null, "SampleNameCode", "http://example.com", ProviderType.LIBRARY);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(missingArgDataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
        //Missing providerType
        missingArgDataProvider = new DataProvider("NewSampleId", "SampleName", "SampleCounty", "SampleDescription", null, "SampleNameCode", "http://example.com", null);
        response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(missingArgDataProvider, MediaType.APPLICATION_XML), Response.class);
        assertEquals(406, response.getStatus());
    }
}
