/*
 * Copyright 2007-2015 The Europeana Foundation
 * 
 * Licensed under the EUPL, Version 1.1 (the "License") and subsequent versions as approved by the
 * European Commission; You may not use this work except in compliance with the License.
 * 
 * You may obtain a copy of the License at: http://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" basis, without warranties or conditions of any kind, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
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

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

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
  public static void setUpBeforeClass() throws IOException {
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
    Mockito.when(
        builder.post(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
            Mockito.any(Class.class))).thenReturn(response);
    Mockito.when(
        builder.put(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
            Mockito.any(Class.class))).thenReturn(response);
  }

  // Tests for GetProvider
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
    pa.getProvider("Pr0");
  }

  // Tests for DeleteProvider
  @Test
  public void testDeleteProvider() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    pa.deleteProvider("Pr0");
  }

  @Test(expected = DoesNotExistException.class)
  public void testDeleteProviderDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    pa.deleteProvider("Pr0");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testDeleteProviderInternalServerError() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    pa.deleteProvider("Pr0");
  }

  // Tests for GetProviderList
  @Test
  public void testGetProviderList() throws InvalidArgumentsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    pa.getProviderList("A0r0", 0, 1);
    Mockito.when(response.readEntity(new GenericType<List<DataProvider>>() {})).thenReturn(
        new ArrayList<DataProvider>());
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testGetProviderListInvalidArguments() throws InvalidArgumentsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    pa.getProviderList("A0r0", 0, 1);
  }
  
  @Test(expected = DoesNotExistException.class)
  public void testGetProviderListDoesNotExist() throws InvalidArgumentsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    pa.getProviderList("A0r0", 0, 1);
  }

  // Tests for CreateProvider
  @Test
  public void testCreateProvider() throws InternalServerErrorException, InvalidArgumentsException,
      MissingArgumentsException, AlreadyExistsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(201);
    pa.createProvider("A0r0", "Id", "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testCreateProviderInvalidArguments() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    pa.createProvider("A0r0", "Id", "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = DoesNotExistException.class)
  public void testCreateProviderDoesNotExist() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    pa.createProvider("A0r0", "Id", "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = MissingArgumentsException.class)
  public void testCreateProviderMissingArguments() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    pa.createProvider("A0r0", "Id", "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = AlreadyExistsException.class)
  public void testCreateProviderAlreadyExists() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    pa.createProvider("A0r0", "Id", "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testCreateProviderInternalServerError() throws InternalServerErrorException,
      InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    pa.createProvider("A0r0", "Id", "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  // Tests for UpdateProvider
  @Test
  public void testUpdateProvider() throws InvalidArgumentsException, DoesNotExistException,
      MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(201);
    pa.updateProvider("P0r0", null, null, "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testUpdateProviderInvalidArguments() throws InvalidArgumentsException,
      DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    pa.updateProvider("P0r0", null, null, "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = DoesNotExistException.class)
  public void testUpdateProviderDoesNotExist() throws InvalidArgumentsException,
      DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    pa.updateProvider("P0r0", null, null, "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = MissingArgumentsException.class)
  public void testUpdateProviderMissingArguments() throws InvalidArgumentsException,
      DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    pa.updateProvider("P0r0", null, null, "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = AlreadyExistsException.class)
  public void testUpdateProviderAlreadyExists() throws InvalidArgumentsException,
      DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    pa.updateProvider("P0r0", null, null, "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testUpdateProviderInternalServerError() throws InvalidArgumentsException,
      DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    pa.updateProvider("P0r0", null, null, "ProviderName", "Greece", "GR", "NONE", "P0", "example.com",
        ProviderType.LIBRARY, "test@test.com");
  }
}
