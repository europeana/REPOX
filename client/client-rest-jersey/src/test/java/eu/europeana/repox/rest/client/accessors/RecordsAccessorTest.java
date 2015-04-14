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

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.theeuropeanlibrary.repox.rest.pathOptions.RecordOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 13, 2015
 */
public class RecordsAccessorTest {
  private static URL restUrl;
  private static String username = "username";
  private static String password = "password";
  private static Client client;
  private static WebTarget webTarget;
  private static Builder builder;
  private static Response response;
  private static RecordsAccessor ra;

  @BeforeClass
  public static void setUpBeforeClass() throws IOException {
    restUrl = new URL("http://examp.com/er");
    client = Mockito.mock(Client.class);
    webTarget = Mockito.mock(WebTarget.class);
    builder = Mockito.mock(Builder.class);
    response = Mockito.mock(Response.class);
    ra = new RecordsAccessor(restUrl, username, password, client);

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
        builder.post(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_XML),
            Mockito.any(Class.class))).thenReturn(response);
  }

  // Tests for GetRecord
  @Test
  public void testGetRecord() throws DoesNotExistException, InternalServerErrorException,
      InvalidArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(Result.class)).thenReturn(
        new Result("<root>xml record</root>"));
    String record = ra.getRecord("recordId");
    Assert.assertNotNull(record);
  }

  @Test(expected = DoesNotExistException.class)
  public void testGetRecordDoesNotExist() throws DoesNotExistException,
      InternalServerErrorException, InvalidArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ra.getRecord("recordId");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testGetRecordInternalServerError() throws DoesNotExistException,
      InternalServerErrorException, InvalidArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ra.getRecord("recordId");
  }

  // Tests for RemoveRecord
  @Test
  public void testRemoveRecord() throws InternalServerErrorException, InvalidArgumentsException,
      DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(200);
    ra.removeRecord("recordId", RecordOptionListContainer.DELETE);
  }

  @Test(expected = DoesNotExistException.class)
  public void testRemoveRecordDoesNotExist() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ra.removeRecord("recordId", RecordOptionListContainer.DELETE);
  }

  @Test(expected = MissingArgumentsException.class)
  public void testRemoveRecordMissingArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Missing argument!"));
    ra.removeRecord("recordId", RecordOptionListContainer.DELETE);
  }

  @Test(expected = InternalServerErrorException.class)
  public void testRemoveRecordInternalServerError() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ra.removeRecord("recordId", RecordOptionListContainer.DELETE);
  }

  // Tests for CreateRecord
  @Test
  public void testCreateRecord() throws InternalServerErrorException, DoesNotExistException,
      MissingArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(201);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Success!"));
    ra.createRecord("ds0", "rd0", "<root>example</root>");
  }

  @Test(expected = DoesNotExistException.class)
  public void testCreateRecordDoesNotExist() throws InternalServerErrorException,
      DoesNotExistException, MissingArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ra.createRecord("ds0", "rd0", "<root>example</root>");
  }

  @Test(expected = MissingArgumentsException.class)
  public void testCreateRecordMissingArguments() throws InternalServerErrorException,
      DoesNotExistException, MissingArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Missing argument!"));
    ra.createRecord("ds0", "rd0", "<root>example</root>");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testCreateRecordInternalServerError() throws InternalServerErrorException,
      DoesNotExistException, MissingArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ra.createRecord("ds0", "rd0", "<root>example</root>");
  }

}
