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

import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.SimpleFileExtractStrategy;
import pt.utl.ist.marc.CharacterEncoding;
import pt.utl.ist.marc.FolderFileRetrieveStrategy;
import pt.utl.ist.marc.iso2709.shared.Iso2709Variant;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 7, 2015
 */
public class DatasetsAccessorTest {
  private static URL restUrl;
  private static String username = "username";
  private static String password = "password";
  private static Client client;
  private static WebTarget webTarget;
  private static Builder builder;
  private static Response response;
  private static DatasetsAccessor da;

  @BeforeClass
  public static void setUp() throws IOException {
    restUrl = new URL("http://examp.com/er");
    client = Mockito.mock(Client.class);
    webTarget = Mockito.mock(WebTarget.class);
    builder = Mockito.mock(Builder.class);
    response = Mockito.mock(Response.class);
    da = new DatasetsAccessor(restUrl, username, password, client);

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

  // Tests for GetDataset
  @Test
  public void testGetDataset() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(DataSourceContainer.class)).thenReturn(
        new DefaultDataSourceContainer());
    DataSourceContainer dataset = da.getDataset("D0r0");
    Assert.assertNotNull(dataset);
  }

  @Test(expected = DoesNotExistException.class)
  public void testGetProviderDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    da.getDataset("D0r0");
  }

  // Tests for DeleteDataset
  @Test
  public void testDeleteDataset() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    da.deleteDataset("d0r0");
  }

  @Test(expected = DoesNotExistException.class)
  public void testDeleteDatasetDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    da.deleteDataset("d0r0");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testDeleteDatasetInternalServerError() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    da.deleteDataset("d0r0");
  }

  // Tests for GetDatasetList
  @Test
  public void testGetDatasetList() throws InvalidArgumentsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    da.getDatasetList("P0r0", 0, 1);
    Mockito.when(response.readEntity(new GenericType<List<DataSourceContainer>>() {})).thenReturn(
        new ArrayList<DataSourceContainer>());
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testGetDatasetListInvalidArguments() throws InvalidArgumentsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.getDatasetList("P0r0", 0, 1);
  }

  @Test(expected = DoesNotExistException.class)
  public void testGetDatasetDoesNotExist() throws InvalidArgumentsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.getDatasetList("P0r0", 0, 1);
  }

  // Tests for CreateDatasetOai
  @Test
  public void testCreateDatasetOai() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(201);
    da.createDatasetOai("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "http://example.com/handler", "abo",
        "/tmp/export/a0661", new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testCreateDatasetOaiInvalidArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.createDatasetOai("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "http://example.com/handler", "abo",
        "/tmp/export/a0661", new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = DoesNotExistException.class)
  public void testCreateDatasetOaiDoesNotExist() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.createDatasetOai("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "http://example.com/handler", "abo",
        "/tmp/export/a0661", new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = MissingArgumentsException.class)
  public void testCreateDatasetOaiMissingArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.createDatasetOai("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "http://example.com/handler", "abo",
        "/tmp/export/a0661", new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = AlreadyExistsException.class)
  public void testCreateDatasetOaiAlreadyExists() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    da.createDatasetOai("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "http://example.com/handler", "abo",
        "/tmp/export/a0661", new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = InternalServerErrorException.class)
  public void testCreateDatasetOaiInternalServerError() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    da.createDatasetOai("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "http://example.com/handler", "abo",
        "/tmp/export/a0661", new IdProvidedRecordIdPolicy(), null);
  }

  // Tests for CreateDatasetFile
  @Test
  public void testCreateDatasetFile() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(201);
    da.createDatasetFile("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testCreateDatasetFileInvalidArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.createDatasetFile("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = DoesNotExistException.class)
  public void testCreateDatasetFileDoesNotExist() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.createDatasetFile("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = MissingArgumentsException.class)
  public void testCreateDatasetFileMissingArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.createDatasetFile("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = AlreadyExistsException.class)
  public void testCreateDatasetFileAlreadyExists() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    da.createDatasetFile("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = InternalServerErrorException.class)
  public void testCreateDatasetFileInternalServerError() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    da.createDatasetFile("P0r0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  // Tests for UpdateDatasetOai
  @Test
  public void testUpdateDatasetOai() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(201);
    da.updateDatasetOai("ds0", null, "ExampleOAI", "a0660", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null,
        "http://oai.onb.ac.at/repox2/OAIHandler", "abo", "/tmp/export3/a0660",
        new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testUpdateDatasetOaiInvalidArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.updateDatasetOai("ds0", null, "ExampleOAI", "a0660", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null,
        "http://oai.onb.ac.at/repox2/OAIHandler", "abo", "/tmp/export3/a0660",
        new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = DoesNotExistException.class)
  public void testUpdateDatasetOaiDoesNotExist() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.updateDatasetOai("ds0", null, "ExampleOAI", "a0660", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null,
        "http://oai.onb.ac.at/repox2/OAIHandler", "abo", "/tmp/export3/a0660",
        new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = MissingArgumentsException.class)
  public void testUpdateDatasetOaiMissingArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.updateDatasetOai("ds0", null, "ExampleOAI", "a0660", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null,
        "http://oai.onb.ac.at/repox2/OAIHandler", "abo", "/tmp/export3/a0660",
        new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = AlreadyExistsException.class)
  public void testUpdateDatasetOaiAlreadyExists() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    da.updateDatasetOai("ds0", null, "ExampleOAI", "a0660", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null,
        "http://oai.onb.ac.at/repox2/OAIHandler", "abo", "/tmp/export3/a0660",
        new IdProvidedRecordIdPolicy(), null);
  }

  @Test(expected = InternalServerErrorException.class)
  public void testUpdateDatasetOaiInternalServerError() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    da.updateDatasetOai("ds0", null, "ExampleOAI", "a0660", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null,
        "http://oai.onb.ac.at/repox2/OAIHandler", "abo", "/tmp/export3/a0660",
        new IdProvidedRecordIdPolicy(), null);
  }

  // Tests for UpdateDatasetFile
  @Test
  public void testUpdateDatasetFile() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(201);
    da.updateDatasetFile("ds0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testUpdateDatasetFileInvalidArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(400);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.updateDatasetFile("ds0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = DoesNotExistException.class)
  public void testUpdateDatasetFileDoesNotExist() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.updateDatasetFile("ds0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = MissingArgumentsException.class)
  public void testUpdateDatasetFileMissingArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Invalid argument!"));
    da.updateDatasetFile("ds0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = AlreadyExistsException.class)
  public void testUpdateDatasetFileAlreadyExists() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    da.updateDatasetFile("ds0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }

  @Test(expected = InternalServerErrorException.class)
  public void testUpdateDatasetFileInternalServerError() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    da.updateDatasetFile("ds0", null, "ExampleOAI", "nameCode", true,
        "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd", "NONE",
        "http://www.europeana.eu/schemas/ese/", "ese", null, "/tmp/export/a0661",
        new IdProvidedRecordIdPolicy(), new SimpleFileExtractStrategy(),
        new FolderFileRetrieveStrategy(), CharacterEncoding.UTF_8, Iso2709Variant.STANDARD,
        "/sample/dir", "SamplerecordXPath", null);
  }
}
