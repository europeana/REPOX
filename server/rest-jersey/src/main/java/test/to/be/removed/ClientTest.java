/* ClientTest.java - created on Oct 14, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package test.to.be.removed;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 14, 2014
 */
public class ClientTest {

    /**
     * Creates a new instance of this class.
     */
    public ClientTest() {
    }
    
    public static void main(String[] args)
    {
        Client client1 = ClientBuilder.newClient();
        Response response1 = client1.target("http://localhost:8080/repox-server-rest-jersey/rest/aggregators").request(MediaType.TEXT_PLAIN).get();
        System.out.println(response1.getStatus());
        String aggregator1 = response1.readEntity(String.class);
        System.out.println(aggregator1);
        
        
//        String aggregatorId = "Austriar";
//        Client client = ClientBuilder.newClient();
//        Response response = client.target("http://localhost:8080/repox-server-rest-jersey/client/aggregators/" + aggregatorId).request(MediaType.APPLICATION_XML).get();
//        System.out.println(response.getStatus());
//        String aggregator = response.readEntity(String.class);
//        System.out.println(aggregator);
    }

}
