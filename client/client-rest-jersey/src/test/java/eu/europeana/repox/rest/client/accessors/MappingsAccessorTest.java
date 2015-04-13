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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 13, 2015
 */
public class MappingsAccessorTest {
  private static URL restUrl;
  private static String username = "username";
  private static String password = "password";
  private static Client client;
  private static WebTarget webTarget;
  private static Builder builder;
  private static Response response;
  private static MappingsAccessor ma;

  @BeforeClass
  public static void setUpBeforeClass() throws IOException {
    restUrl = new URL("http://examp.com/er");
    client = Mockito.mock(Client.class);
    webTarget = Mockito.mock(WebTarget.class);
    builder = Mockito.mock(Builder.class);
    response = Mockito.mock(Response.class);
    ma = new MappingsAccessor(restUrl, username, password, client);

    Mockito.when(client.register(MultiPartFeature.class)).thenReturn(client);
    Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);
    Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
    Mockito.when(webTarget.request()).thenReturn(builder);
    Mockito.when(webTarget.queryParam(Mockito.anyString(), Mockito.anyObject())).thenReturn(
        webTarget);
    Mockito.when(builder.get()).thenReturn(response);
    Mockito.when(builder.delete()).thenReturn(response);
    Mockito.when(
        builder.post(Entity.entity(Mockito.any(Class.class), new MediaType("multipart", "mixed")),
            Mockito.any(Class.class))).thenReturn(response);
  }

  // Tests for GetMapping
  @Test
  public void testGetMapping() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(MetadataTransformation.class)).thenReturn(
        new MetadataTransformation());

    MetadataTransformation mapping = ma.getMapping("map0");
    Assert.assertNotNull(mapping);
  }

  @Test(expected = DoesNotExistException.class)
  public void testGetMappingDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ma.getMapping("map0");
  }

  // Tests for DeleteMapping
  @Test
  public void testDeleteMapping() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    ma.deleteMapping("map0");
  }

  @Test(expected = DoesNotExistException.class)
  public void testDeleteMappingDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ma.deleteMapping("map0");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testDeleteMappingInternalServerError() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ma.deleteMapping("map0");
  }

  // Tests for CreateMapping
  @Test
  public void testCreateMapping() throws InternalServerErrorException, InvalidArgumentsException, DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(201);
    ma.createMapping(new MetadataTransformation(), new File("/tmp/example.xsl"));
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testCreateMappingInvalidArguments() throws InternalServerErrorException, InvalidArgumentsException, DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    ma.createMapping(new MetadataTransformation(), new File("/tmp/example.xsl"));
  }

  @Test(expected = DoesNotExistException.class)
  public void testCreateMappingDoesNotExist() throws InternalServerErrorException, InvalidArgumentsException, DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ma.createMapping(new MetadataTransformation(), new File("/tmp/example.xsl"));
  }

  @Test(expected = MissingArgumentsException.class)
  public void testCreateMappingMissingArguments() throws InternalServerErrorException, InvalidArgumentsException, DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Missing argument!"));
    ma.createMapping(new MetadataTransformation(), new File("/tmp/example.xsl"));
  }

  @Test(expected = AlreadyExistsException.class)
  public void testCreateMappingAlreadyExists() throws InternalServerErrorException, InvalidArgumentsException, DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    ma.createMapping(new MetadataTransformation(), new File("/tmp/example.xsl"));
  }

  @Test(expected = InternalServerErrorException.class)
  public void testCreateMappingInternalServerError() throws InternalServerErrorException, InvalidArgumentsException, DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ma.createMapping(new MetadataTransformation(), new File("/tmp/example.xsl"));
  }
}
