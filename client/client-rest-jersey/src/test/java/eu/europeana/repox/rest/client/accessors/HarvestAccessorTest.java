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
import java.util.Calendar;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.theeuropeanlibrary.repox.rest.pathOptions.HarvestOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.task.ScheduledTask.Frequency;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 9, 2015
 */
public class HarvestAccessorTest {
  private static URL restUrl;
  private static String username = "username";
  private static String password = "password";
  private static Client client;
  private static WebTarget webTarget;
  private static Builder builder;
  private static Response response;
  private static HarvestAccessor ha;

  @BeforeClass
  public static void setUpBeforeClass() throws IOException {
    restUrl = new URL("http://examp.com/er");
    client = Mockito.mock(Client.class);
    webTarget = Mockito.mock(WebTarget.class);
    builder = Mockito.mock(Builder.class);
    response = Mockito.mock(Response.class);
    ha = new HarvestAccessor(restUrl, username, password, client);

    Mockito.when(client.target(Mockito.anyString())).thenReturn(webTarget);
    Mockito.when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
    Mockito.when(webTarget.queryParam(Mockito.anyString(), Mockito.anyObject())).thenReturn(
        webTarget);
    Mockito.when(builder.get()).thenReturn(response);
    Mockito.when(builder.delete()).thenReturn(response);
    Mockito.when(
        builder.post(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
            Mockito.any(Class.class))).thenReturn(response);
    // Mockito.when(
    // builder.put(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
    // Mockito.any(Class.class))).thenReturn(response);
  }

  // Tests for StartHarvest
  @Test
  public void testStartHarvest() throws AlreadyExistsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Success!"));
    ha.startHarvest("ds0", HarvestOptionListContainer.SAMPLE);
  }

  @Test(expected = DoesNotExistException.class)
  public void testStartHarvestDoesNotExist() throws DoesNotExistException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ha.startHarvest("ds0", HarvestOptionListContainer.SAMPLE);
  }

  @Test(expected = AlreadyExistsException.class)
  public void testStartHarvestAlreadyExists() throws AlreadyExistsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    ha.startHarvest("ds0", HarvestOptionListContainer.SAMPLE);
  }

  @Test(expected = InternalServerErrorException.class)
  public void testStartHarvestInternalServerError() throws AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ha.startHarvest("ds0", HarvestOptionListContainer.SAMPLE);
  }

  // Tests for CancelHarvest
  @Test
  public void testCancelHarvest() throws AlreadyExistsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Success!"));
    ha.cancelHarvest("ds0");
  }

  @Test(expected = DoesNotExistException.class)
  public void testCancelHarvestDoesNotExist() throws DoesNotExistException, AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ha.cancelHarvest("ds0");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testCancelHarvestInternalServerError() throws AlreadyExistsException,
      DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ha.cancelHarvest("ds0");
  }

  // Tests for ScheduleHarvest
  @Test
  public void testScheduleHarvest() throws AlreadyExistsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Success!"));
    ha.startHarvest("ds0", HarvestOptionListContainer.SAMPLE);
  }

  @Test(expected = DoesNotExistException.class)
  public void testScheduleHarvestDoesNotExist() throws DoesNotExistException,
      AlreadyExistsException, MissingArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ha.scheduleHarvest("ds0", Calendar.getInstance(), Frequency.WEEKLY, 10, false);
  }

  @Test(expected = MissingArgumentsException.class)
  public void testCreateDatasetOaiMissingArguments() throws InternalServerErrorException,
      InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    Mockito.when(response.getStatus()).thenReturn(406);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Missing argument!"));
    ha.scheduleHarvest("ds0", Calendar.getInstance(), Frequency.WEEKLY, 10, false);
  }

  @Test(expected = AlreadyExistsException.class)
  public void testScheduleHarvestAlreadyExists() throws AlreadyExistsException,
      DoesNotExistException, MissingArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    ha.scheduleHarvest("ds0", Calendar.getInstance(), Frequency.WEEKLY, 10, false);
  }

  @Test(expected = InternalServerErrorException.class)
  public void testScheduleHarvestInternalServerError() throws AlreadyExistsException,
      DoesNotExistException, MissingArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ha.scheduleHarvest("ds0", Calendar.getInstance(), Frequency.WEEKLY, 10, false);
  }

  // Tests for deleteScheduledTask
  @Test
  public void testDeleteScheduledTask() throws AlreadyExistsException, DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Success!"));
    ha.deleteScheduledTask("ds0", "ds0_2");
  }

  @Test(expected = DoesNotExistException.class)
  public void testDeleteScheduledTaskDoesNotExist() throws DoesNotExistException,
      AlreadyExistsException, MissingArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ha.deleteScheduledTask("ds0", "ds0_2");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testDeleteScheduledTaskInternalServerError() throws AlreadyExistsException,
      DoesNotExistException, MissingArgumentsException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ha.deleteScheduledTask("ds0", "ds0_2");
  }

  // Tests for GetDatasetScheduledTasks
  @Test
  public void testGetDatasetScheduledTasks() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Success!"));
    ha.getDatasetScheduledTasks("ds0");
  }

  @Test(expected = DoesNotExistException.class)
  public void testGetDatasetScheduledTasksDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ha.getDatasetScheduledTasks("ds0");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testGetDatasetScheduledTasksInternalServerError() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ha.getDatasetScheduledTasks("ds0");
  }

  // Tests for GetDatasetHarvestingStatus
  @Test
  public void testGetDatasetHarvestingStatus() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Success!"));
    ha.getDatasetHarvestingStatus("ds0");
  }

  @Test(expected = DoesNotExistException.class)
  public void testGetDatasetHarvestingStatusDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ha.getDatasetHarvestingStatus("ds0");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testGetDatasetHarvestingStatusInternalServerError() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ha.getDatasetHarvestingStatus("ds0");
  }

  // Tests for GetDatasetLastIngestLog
  @Test
  public void testGetDatasetLastIngestLog() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(200);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Success!"));
    ha.getDatasetLastIngestLog("ds0");
  }

  @Test(expected = DoesNotExistException.class)
  public void testGetDatasetLastIngestLogDoesNotExist() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(404);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Does not exist!"));
    ha.getDatasetLastIngestLog("ds0");
  }

  @Test(expected = InternalServerErrorException.class)
  public void testGetDatasetLastIngestLogInternalServerError() throws DoesNotExistException {
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ha.getDatasetLastIngestLog("ds0");
  }
  
//Tests for GetCurrentHarvestsList
 @Test
 public void testGetCurrentHarvestsList(){
   Mockito.when(response.getStatus()).thenReturn(200);
   Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Success!"));
   ha.getCurrentHarvestsList();
 }


}
