/* AggregatorsTest.java - created on Oct 9, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfigTesting;
import org.theeuropeanlibrary.repox.rest.pathOptions.AggregatorOptionListContainer;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.util.exceptions.AggregatorDoesNotExistException;

/**
 * Aggregators context path handling tests.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 9, 2014
 */
//@Ignore
public class AggregatorsResourceTest extends JerseyTest {

    public AggregatorsResourceTest() throws Exception {
        super(new JerseyConfigTesting());
    }
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUpBeforeMethod()  throws Exception{
        reset(JerseyConfigTesting.dataManager);
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.AggregatorsResource#getOptions()}.
     */
    @Test
//    @Ignore
    public void testGetOptions() {     
        int numberOfAvailableOptions = 1;
        WebTarget target = target("/" + AggregatorOptionListContainer.AGGREGATORS);
        
        Response response = target.request(MediaType.APPLICATION_XML).options();
        assertEquals(200, response.getStatus()); //Check xml working
        response = target.request(MediaType.APPLICATION_JSON).options();
        assertEquals(200, response.getStatus()); //Check json working 
        AggregatorOptionListContainer aolc = response.readEntity(AggregatorOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, aolc.getOptionList().size());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.AggregatorsResource#getAggregator(java.lang.String)}.
     */
    @Test
//    @Ignore
    public void testGetAggregator() {    
        String aggregatorId = "Austriar0";
        WebTarget target = target("/" + AggregatorOptionListContainer.AGGREGATORS + "/" + aggregatorId);
        
        Response response = target.request(MediaType.APPLICATION_XML).head();
        assertEquals(200, response.getStatus()); //Check xml working
        response = target.request(MediaType.APPLICATION_JSON).head(); 
        assertEquals(200, response.getStatus()); //Check json working
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());
        Aggregator aggregator = response.readEntity(Aggregator.class);
        response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        aggregator = response.readEntity(Aggregator.class);
        assertEquals(aggregatorId, aggregator.getId());
        
        //Make the call return an exception
        target = target("/" + AggregatorOptionListContainer.AGGREGATORS + "/" + "aFakeAggregatorId");
        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatus());
        response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(404, response.getStatus());
//        System.out.println(aggregator.getId());
    }
    
    
    
    
    
    
    

    /**
     * TEMPORARY TEST METHOD
     * @throws JAXBException
     * @throws AggregatorDoesNotExistException 
     * @throws MalformedURLException 
     */
    @Test
//    @Ignore
    public final void fastTesting() throws JAXBException, AggregatorDoesNotExistException, MalformedURLException {
        String aggregatorId = "Austriar0";
        
//        DefaultDataManager dataManager = mock(DefaultDataManager.class);
//        when(dataManager.getAggregator(aggregatorId)).thenReturn(new Aggregator(aggregatorId, "testName", "namecode", new URL("http://something"), null));
//        AggregatorsResource ar = new AggregatorsResource(dataManager);
        
//        Aggregator aggregator = ar.getAggregator(aggregatorId);
        
        when(JerseyConfigTesting.dataManager.getAggregator(aggregatorId)).thenReturn(new Aggregator(aggregatorId, "testName", "namecode", new URL("http://something"), null));
        reset(JerseyConfigTesting.dataManager);
        when(JerseyConfigTesting.dataManager.getAggregator(aggregatorId)).thenReturn(new Aggregator(aggregatorId, "testName", "namecode", new URL("http://else"), null));
        
        WebTarget target = target("/" + AggregatorOptionListContainer.AGGREGATORS + "/" + aggregatorId);
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        Aggregator aggregator = response.readEntity(Aggregator.class);
        assertEquals(aggregatorId, aggregator.getId());
        System.out.println(aggregator.getHomePage());
//        Response response = target("/aggregators").request(MediaType.TEXT_PLAIN).get();
//        System.out.println(response.getStatus());
//        String aggregator = response.readEntity(String.class);
//        System.out.println(aggregator);
        
        //        ArrayList<Option> arrayList = new ArrayList<Option>();
        //        arrayList.add(new Option("description", "syntax"));
        //        arrayList.add(new Option("description2", "syntax2"));
        //        OptionList optionList = new OptionList(arrayList);

//        URI uri = UriBuilder.fromPath("http://app/rest").build();
//        AggregatorOptionListContainer optionList = new AggregatorOptionListContainer(uri);
//
//        JAXBContext jc = JAXBContext.newInstance(AggregatorOptionListContainer.class);
//
//        Marshaller marshaller = jc.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); //pretty print XML
//        marshaller.marshal(optionList, System.out);
        
        
        
//        JAXBContext ctx;
//        try {
//            ctx = JAXBContext.newInstance(AggregatorOptionListContainer.class);
//            Marshaller marshaller = ctx.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//            marshaller.marshal(aolc, System.out);
//
//        } catch (JAXBException e) {
//            throw new RuntimeException("Caused by JAXBException", e);
//        }
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        System.out.println(gson.toJson(aolc));
//        
//        System.out.println(target("/aggregators/options").request(MediaType.APPLICATION_JSON).options().readEntity(String.class));
    }

}
