/* AggregatorsTest.java - created on Oct 9, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.theeuropeanlibrary.repox.rest.configuration.JerseyConfig;
import org.theeuropeanlibrary.repox.rest.pathOptions.AggregatorOptionListContainer;

/**
 * Aggregators context path handling tests.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 9, 2014
 */
public class AggregatorsTest extends JerseyTest {

    public AggregatorsTest() throws Exception {
        super(new JerseyConfig());
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

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.Aggregators#getOptions()}.
     */
    @Test
    @Ignore
    public void testGetOptions() {
        int numberOfAvailableOptions = 1;
        Response response = target("/aggregators").request(MediaType.APPLICATION_XML).options();
        assertEquals(200, response.getStatus());
        AggregatorOptionListContainer aolc = response.readEntity(AggregatorOptionListContainer.class);
        //Check the number of options provided
        assertEquals(numberOfAvailableOptions, aolc.getOptionList().size());
    }

    /**
     * Test method for {@link org.theeuropeanlibrary.repox.rest.servlets.Aggregators#getAggregator(java.lang.String)}.
     */
    @Test
//    @Ignore
    public final void testGetAggregator() {
        String aggregatorId = "Austriar0";
//        final String responseMsg = target("/aggregators/" + aggregatorId).request().get(String.class);
//        assertEquals("Aggregator" + aggregatorId, responseMsg);
//        System.out.println("Aggregator" + responseMsg);
        
        Response response = target("/aggregators/" + aggregatorId).request(MediaType.APPLICATION_XML).get();
        System.out.println(response.getStatus());
        String aggregator = response.readEntity(String.class);
        System.out.println(aggregator);
    }
    
    
    
    
    
    
    

    /**
     * TEMPORARY TEST METHOD
     * @throws JAXBException
     */
    @Test
    @Ignore
    public final void fastTesting() throws JAXBException {
        
        
        Response response = target("/aggregators").request(MediaType.TEXT_PLAIN).get();
        System.out.println(response.getStatus());
        String aggregator = response.readEntity(String.class);
        System.out.println(aggregator);
        
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
