/*
 * Copyright 2007-2015 The Europeana Foundation
 * 
 * Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved by the
 * European Commission; You may not use this work except in compliance with the Licence.
 * 
 * You may obtain a copy of the Licence at: http://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence
 * is distributed on an "AS IS" basis, without warranties or conditions of any kind, either express
 * or implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */
package eu.europeana.repox.rest.client.accessors;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.InternalServerErrorException;
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

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.util.exceptions.DoesNotExistException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 7, 2015
 */
public class ProvidersAccessorTest {
  private static URL restUrl;
  private static String username = "username";
  private static String password = "password";
  private static Client client;
  private static WebTarget webTarget;
  private static Builder builder;
  private static Response response;
  private static ProvidersAccessor pa;

  @BeforeClass
  public static void setUp() throws IOException {
    restUrl = new URL("http://examp.com/er");
    client = Mockito.mock(Client.class);
    webTarget = Mockito.mock(WebTarget.class);
    builder = Mockito.mock(Builder.class);
    response = Mockito.mock(Response.class);
    pa = new ProvidersAccessor(restUrl, username, password, client);

    Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);
    Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
    Mockito.when(webTarget.queryParam(Mockito.anyString(), Mockito.anyObject())).thenReturn(
        webTarget);
    Mockito.when(builder.get()).thenReturn(response);
    Mockito.when(builder.delete()).thenReturn(response);
    // Mockito.when(
    // builder.post(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
    // Mockito.any(Class.class))).thenReturn(response);
    // Mockito.when(
    // builder.put(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
    // Mockito.any(Class.class))).thenReturn(response);
  }

  // Tests for GetAggregator
  @Test
  public void testGetProvider() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(DataProvider.class)).thenReturn(new DataProvider());
    DataProvider dataProvider = pa.getProvider("Pr0");
    Assert.assertNotNull(dataProvider);
  }

  @Test(expected = DoesNotExistException.class)
  public void testGetProviderDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));

    // First call get exception
    DataProvider dataProvider = null;
    dataProvider = pa.getProvider("Pr0");
    Assert.assertNull(dataProvider);
  }

  // Tests for DeleteAggregator
  @Test
  public void testDeleteProvider() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    pa.deleteProvider("Pr0");
  }

  @Test(expected = DoesNotExistException.class)
  public void testDeleteAProviderInternalServerError() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    pa.deleteProvider("Pr0");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testDeleteProviderDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    pa.deleteProvider("Pr0");
  }

}
