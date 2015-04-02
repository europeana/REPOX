/*
 * Copyright 2007-2015 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved 
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 *  
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under 
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of 
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under 
 *  the Licence.
 */
package eu.europeana.repox.rest.client.accessors;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.util.exceptions.DoesNotExistException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 2, 2015
 */
public class AggregatorAccessorTest {
  private static URL restUrl;
  private static String username = "username";
  private static String password = "password";
  private static Client client;
  private static WebTarget webTarget;
  private static Builder builder;
  private static Response response;
  private static AggregatorAccessor aa;

  @BeforeClass
  public static void setUp() throws IOException {
    restUrl = new URL("http://examp.com/er");
    client = Mockito.mock(Client.class);
    webTarget = Mockito.mock(WebTarget.class);
    builder = Mockito.mock(Builder.class);
    response = Mockito.mock(Response.class);
    aa = new AggregatorAccessor(restUrl, username, password, client);
  }
  
  @Test
  public void testGetAggregator() {
    Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);
    Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
    Mockito.when(builder.get()).thenReturn(response);
    Mockito.when(response.getStatus()).thenReturn(404).thenReturn(200);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    Mockito.when(response.readEntity(Aggregator.class)).thenReturn(new Aggregator());

    //First call get exception
    Aggregator aggregator = null;
    try {
      aggregator = aa.getAggregator("A0r0");
    } catch (DoesNotExistException e) {
    }
    Assert.assertNull(aggregator);
    
    aggregator = null;
    try {
      aggregator = aa.getAggregator("A0r0");
    } catch (DoesNotExistException e1) {
    }
    Assert.assertNotNull(aggregator);
  }

}
