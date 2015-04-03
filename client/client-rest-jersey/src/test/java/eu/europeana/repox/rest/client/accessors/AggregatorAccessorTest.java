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
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

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

    Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);
    Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
    Mockito.when(webTarget.queryParam(Mockito.anyString(), Mockito.anyObject())).thenReturn(
        webTarget);
    Mockito.when(builder.get()).thenReturn(response);
    Mockito.when(builder.delete()).thenReturn(response);
    Mockito.when(
        builder.post(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
            Mockito.any(Class.class))).thenReturn(response);
    Mockito.when(
        builder.put(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
            Mockito.any(Class.class))).thenReturn(response);
  }

  // Tests for GetAggregator
  @Test
  public void testGetAggregator() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(Aggregator.class)).thenReturn(new Aggregator());
    Aggregator aggregator = aa.getAggregator("A0r0");
    Assert.assertNotNull(aggregator);
  }

  @Test(expected = DoesNotExistException.class)
  public void testGetAggregatorDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));

    // First call get exception
    Aggregator aggregator = null;
    aggregator = aa.getAggregator("A0r0");
    Assert.assertNull(aggregator);
  }

  // Tests for DeleteAggregator
  @Test
  public void testDeleteAggregator() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    aa.deleteAggregator("A0r0");
  }

  @Test(expected = DoesNotExistException.class)
  public void testDeleteAggregatorInternalServerError() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    aa.deleteAggregator("A0r0");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testDeleteAggregatorDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    aa.deleteAggregator("A0r0");
  }

  // Tests for GetAggregatorList
  @Test
  public void testGetAggregatorList() throws InvalidArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(200);
    aa.getAggregatorList(0, 1);
    Mockito.when(response.readEntity(new GenericType<List<Aggregator>>() {})).thenReturn(
        new ArrayList<Aggregator>());
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testGetAggregatorListInvalidArguments() throws InvalidArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    aa.getAggregatorList(0, 1);
  }

  // Tests for CreateAggregator
  @Test
  public void testCreateAggregator() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(201);
    aa.createAggregator("Id", "Greece", "GR", "http://somepage.com");
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testCreateAggregatorInvalidArguments() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    aa.createAggregator("Id", "Greece", "GR", "http://somepage.com");
  }

  @Test(expected = MissingArgumentsException.class)
  public void testCreateAggregatorMissingArguments() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    aa.createAggregator("Id", "Greece", "GR", "http://somepage.com");
  }

  @Test(expected = AlreadyExistsException.class)
  public void testCreateAggregatorAlreadyExists() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    aa.createAggregator("Id", "Greece", "GR", "http://somepage.com");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testCreateAggregatorInternalServerError() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    aa.createAggregator("Id", "Greece", "GR", "http://somepage.com");
  }

  // Tests for UpdateAggregator
  @Test
  public void testUpdateAggregator() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(201);
    aa.updateAggregator("Id", "newId", "Greece", "GR", "http://somepage.com");
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testUpdateAggregatorInvalidArguments() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    aa.updateAggregator("Id", "newId", "Greece", "GR", "http://somepage.com");
  }

  @Test(expected = DoesNotExistException.class)
  public void testUpdateAggregatorAlreadyExists() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    aa.updateAggregator("Id", "newId", "Greece", "GR", "http://somepage.com");
  }

  @Test(expected = MissingArgumentsException.class)
  public void testUpdateAggregatorMissingArguments() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    aa.updateAggregator("Id", "newId", "Greece", "GR", "http://somepage.com");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testUpdateAggregatorInternalServerError() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    aa.updateAggregator("Id", "newId", "Greece", "GR", "http://somepage.com");
  }
}
