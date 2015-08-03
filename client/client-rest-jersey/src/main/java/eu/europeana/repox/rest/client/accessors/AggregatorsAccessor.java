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
import org.theeuropeanlibrary.repox.rest.pathOptions.AggregatorOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * Access functionality to the aggregators.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 2, 2015
 */
public class AggregatorsAccessor {
  private URL restUrl;
  private Client client = JerseyClientBuilder.newClient();
  private static final Logger LOGGER = LoggerFactory.getLogger(AggregatorsAccessor.class);

  /**
   * Setup AggregatorAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @param username
   * @param passwordS
   * @throws MalformedURLException
   */
  public AggregatorsAccessor(URL restUrl, String username, String password)
      throws MalformedURLException {
    super();

    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("AggregatorAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Only used for tests. Setup AggregatorAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @param username
   * @param password
   * @param target
   * @throws MalformedURLException
   */
  AggregatorsAccessor(URL restUrl, String username, String password, Client client)
      throws MalformedURLException {
    super();
    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    this.client = client;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("AggregatorAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Retrieve the aggregator with the provided id.
   * 
   * @param aggregatorId
   * @return Aggregator
   * @throws DoesNotExistException
   */
  public Aggregator getAggregator(String aggregatorId) throws DoesNotExistException {
    WebTarget target =
        client.target(restUrl + "/" + AggregatorOptionListContainer.AGGREGATORS + "/"
            + aggregatorId);
    Response response = target.request(MediaType.APPLICATION_JSON).get();
    if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getAggregator(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    }
    Aggregator aggregator = response.readEntity(Aggregator.class);
    LOGGER.info("getAggregator(..) success!");

    return aggregator;
  }

  /**
   * Delete an aggregator by specifying the Id.
   * 
   * @param aggregatorId
   * @throws DoesNotExistException
   */
  public void deleteAggregator(String aggregatorId) throws DoesNotExistException,
      InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + AggregatorOptionListContainer.AGGREGATORS + "/"
            + aggregatorId);
    Response response = target.request(MediaType.APPLICATION_JSON).delete();
    if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("deleteAggregator(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    } else if (response.getStatus() == 500) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("deleteAggregator(..) failure! : " + errorMessage.getResult());
      throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("deleteAggregator(..) success!");
  }

  /**
   * Get a list of aggregators in the specified range. Returned number can be smaller than the
   * requested. Offset not allowed negative. If number is negative then it returns all the items
   * from offset until the total number of items.
   * 
   * @param offset
   * @param number
   * @return the list of the number of aggregators requested
   * @throws InvalidArgumentsException
   */
  public List<Aggregator> getAggregatorList(int offset, int number)
      throws InvalidArgumentsException {
    WebTarget target =
        client.target(restUrl + "/" + AggregatorOptionListContainer.AGGREGATORS)
            .queryParam(AggregatorOptionListContainer.OFFSET, offset)
            .queryParam(AggregatorOptionListContainer.NUMBER, number);
    Response response = target.request(MediaType.APPLICATION_JSON).get();
    if (response.getStatus() == 400) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getAggregatorList(..) failure! : " + errorMessage.getResult());
      throw new InvalidArgumentsException(errorMessage.getResult());
    }
    List<Aggregator> subList = response.readEntity(new GenericType<List<Aggregator>>() {});
    LOGGER.info("getAggregatorList(..) success!");

    return subList;
  }

  /**
   * Create an aggregator.
   * 
   * @param id
   * @param name
   * @param nameCode
   * @param homepage
   * @throws InvalidArgumentsException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  public void createAggregator(String id, String name, String nameCode, String homepage)
      throws InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      InternalServerErrorException {
    WebTarget target = client.target(restUrl + "/" + AggregatorOptionListContainer.AGGREGATORS);
    Aggregator aggregator = new Aggregator(id, name, nameCode, homepage, null);
    Response response =
        target.request(MediaType.APPLICATION_JSON).post(
            Entity.entity(aggregator, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createAggregator(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createAggregator(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
     case 409:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createAggregator(..) failure! : " + errorMessage.getResult());
        throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createAggregator(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("createAggregator(..) success!");
  }

  /**
   * Update an aggregator by specifying the Id. Aggregator newId can be null if there is no need to
   * change the id.
   * 
   * @param id
   * @param newId
   * @param name
   * @param nameCode
   * @param homepage
   * @throws InvalidArgumentsException
   * @throws MissingArgumentsException
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  public void updateAggregator(String id, String newId, String name, String nameCode,
      String homepage) throws InvalidArgumentsException, MissingArgumentsException,
      DoesNotExistException, InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + AggregatorOptionListContainer.AGGREGATORS + "/" + id);
    Aggregator aggregator = new Aggregator(newId, name, nameCode, homepage, null);
    Response response =
        target.request(MediaType.APPLICATION_JSON).put(
            Entity.entity(aggregator, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateAggregator(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateAggregator(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateAggregator(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateAggregator(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("updateAggregator(..) success!");
  }

  public URL getRestUrl() {
    return restUrl;
  }

  public void setRestUrl(URL restUrl) {
    this.restUrl = restUrl;
  }
}
