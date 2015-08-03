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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.HarvestOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.task.ScheduledTask;
import pt.utl.ist.task.ScheduledTask.Frequency;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * Access functionality to the harvests.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 9, 2015
 */
public class HarvestAccessor {
  private URL restUrl;
  private Client client = JerseyClientBuilder.newClient();
  private static final Logger LOGGER = LoggerFactory.getLogger(HarvestAccessor.class);

  /**
   * Setup HarvestAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @throws MalformedURLException
   */
  public HarvestAccessor(URL restUrl, String username, String password)
      throws MalformedURLException {
    super();

    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("HarvestAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Only used for tests. Setup HarvestAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @param username
   * @param password
   * @param target
   * @throws MalformedURLException
   */
  HarvestAccessor(URL restUrl, String username, String password, Client client)
      throws MalformedURLException {
    super();
    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    this.client = client;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("HarvestAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Initiates a new harvest of the dataset with id.
   * 
   * @param id
   * @param type
   * @throws AlreadyExistsException
   * @throws DoesNotExistException
   */
  public void startHarvest(String id, String type) throws AlreadyExistsException,
      DoesNotExistException, InternalServerErrorException {
    WebTarget target =
        client.target(
            restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/"
                + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.START)
            .queryParam(HarvestOptionListContainer.TYPE, type);
    Response response =
        target.request(MediaType.APPLICATION_JSON).post(
            Entity.entity(null, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("startHarvest(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
     // case 409:
     //   errorMessage = response.readEntity(Result.class);
     //   LOGGER.warn("startHarvest(..) failure! : " + errorMessage.getResult());
     //   throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("startHarvest(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    Result errorMessage = response.readEntity(Result.class);
    LOGGER.info("startHarvest(..) success! : " + errorMessage.getResult());
  }

  /**
   * Cancels a harvesting ingest.
   * 
   * @param id
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  public void cancelHarvest(String id) throws DoesNotExistException, InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/"
            + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.CANCEL);

    Response response = target.request(MediaType.APPLICATION_JSON).delete();

    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("cancelHarvest(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("cancelHarvest(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    Result errorMessage = response.readEntity(Result.class);
    LOGGER.info("cancelHarvest(..) success! : " + errorMessage.getResult());
  }

  /**
   * Schedules an automatic harvesting.
   * 
   * @param id
   * @param firstDateTime
   * @param frequency
   * @param xmonths
   * @param incremental
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   */
  public void scheduleHarvest(String id, Calendar firstDateTime, Frequency frequency, int xmonths,
      boolean incremental) throws DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException {
    WebTarget target =
        client.target(
            restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/"
                + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.SCHEDULE)
            .queryParam(HarvestOptionListContainer.INCREMENTAL, incremental);

    ScheduledTask scheduledTask = new ScheduledTask();
    scheduledTask.setFirstRun(firstDateTime);
    scheduledTask.setFrequency(frequency);
    scheduledTask.setXmonths(new Integer(xmonths));

    Response response =
        target.request(MediaType.APPLICATION_JSON).post(
            Entity.entity(scheduledTask, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("scheduleHarvest(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("scheduleHarvest(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
     // case 409:
     //   errorMessage = response.readEntity(Result.class);
     //   LOGGER.warn("scheduleHarvest(..) failure! : " + errorMessage.getResult());
     //   throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("scheduleHarvest(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("scheduleHarvest(..) success!");
  }

  /**
   * Deletes an automatic harvesting.
   * 
   * @param id
   * @param taskId
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  public void deleteScheduledTask(String id, String taskId) throws DoesNotExistException,
      InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/"
            + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.SCHEDULES + "/"
            + taskId);

    Response response = target.request(MediaType.APPLICATION_JSON).delete();

    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("deleteScheduledTask(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("deleteScheduledTask(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    Result errorMessage = response.readEntity(Result.class);
    LOGGER.info("deleteScheduledTask(..) success! : " + errorMessage.getResult());
  }

  public List<ScheduledTask> getDatasetScheduledTasks(String id) throws DoesNotExistException,
      InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/"
            + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.SCHEDULES);
    Response response = target.request(MediaType.APPLICATION_JSON).get();

    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetScheduledTasks(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetScheduledTasks(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    List<ScheduledTask> scheduleTasksList =
        response.readEntity(new GenericType<List<ScheduledTask>>() {});
    LOGGER.info("getDatasetScheduledTasks(..) success!");
    return scheduleTasksList;
  }

  /**
   * Gets the status of a specific dataset harvesting.
   * 
   * @param id
   * @return String containing the status
   * @throws DoesNotExistException
   */
  public String getDatasetHarvestingStatus(String id) throws DoesNotExistException,
      InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/"
            + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.STATUS);
    Response response = target.request(MediaType.APPLICATION_JSON).get();

    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetHarvestingStatus(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetHarvestingStatus(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    Result result = response.readEntity(Result.class);
    LOGGER.info("getDatasetHarvestingStatus(..) success!");
    return result.getResult();
  }

  /**
   * Gets the logs of the last ingest.
   * 
   * @param id
   * @return String with xml of the log
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  public String getDatasetLastIngestLog(String id) throws DoesNotExistException,
      InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/" + id + "/"
            + HarvestOptionListContainer.HARVEST + "/" + HarvestOptionListContainer.LOG);
    Response response = target.request(MediaType.APPLICATION_JSON).get();

    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetLastIngestLog(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("getDatasetLastIngestLog(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    Result result = response.readEntity(Result.class);
    LOGGER.info("getDatasetLastIngestLog(..) success!");
    return result.getResult();
  }

  /**
   * Gets a list of currently executing dataset harvests.
   * @return list with the running tasks
   */
  public List<Task> getCurrentHarvestsList() {
    WebTarget target =
        client.target(restUrl + "/" + DatasetOptionListContainer.DATASETS + "/"
            + HarvestOptionListContainer.HARVESTS);
    Response response = target.request(MediaType.APPLICATION_JSON).get();

    List<Task> tasksList = response.readEntity(new GenericType<List<Task>>() {});
    LOGGER.info("getCurrentHarvestsList(..) success!");
    return tasksList;
  }

//  public static void main(String[] args) throws MalformedURLException, AlreadyExistsException,
//      DoesNotExistException, MissingArgumentsException {
//    HarvestAccessor ha =
//        new HarvestAccessor(new URL("http://localhost:8080/repox/rest"), "temporary", "temporary");
//    // ha.startHarvest("a0660", HarvestOptionListContainer.FULL);
//
//    // ha.cancelHarvest("a0660");
//    // Calendar date = Calendar.getInstance();
//    // date.set(Calendar.YEAR, 2015);
//    // date.set(Calendar.MONTH, Calendar.APRIL);
//    // date.set(Calendar.DAY_OF_MONTH, 10);
//    // date.set(Calendar.HOUR_OF_DAY, 13);
//    // date.set(Calendar.MINUTE, 22);
//    // ha.scheduleHarvest("a0660", date, Frequency.DAILY, 2, false);
//
//    // ha.deleteScheduledTask("a0660", "a0660_4");
//
//    // List<ScheduledTask> datasetScheduledTasks = ha.getDatasetScheduledTasks("a0660");
//    // System.out.println(ha.getDatasetHarvestingStatus("a0660"));
//    System.out.println(ha.getDatasetLastIngestLog("a0660"));
//    
////    System.out.println(ha.getCurrentHarvestsList().size());
//  }

}
