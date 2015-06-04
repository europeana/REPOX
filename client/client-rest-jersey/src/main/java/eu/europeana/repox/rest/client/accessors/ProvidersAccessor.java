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
import org.theeuropeanlibrary.repox.rest.pathOptions.ProviderOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * Access functionality to the providers.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 7, 2015
 */
public class ProvidersAccessor {

  private URL restUrl;
  private Client client = JerseyClientBuilder.newClient();
  private static final Logger LOGGER = LoggerFactory.getLogger(ProvidersAccessor.class);

  /**
   * Setup ProvidersAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @throws MalformedURLException
   */
  public ProvidersAccessor(URL restUrl, String username, String password)
      throws MalformedURLException {
    super();

    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("ProvidersAccessor created with target url: {}", this.restUrl);
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
  ProvidersAccessor(URL restUrl, String username, String password, Client client)
      throws MalformedURLException {
    super();
    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    this.client = client;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("ProvidersAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Retrieve the provider with the provided id.
   * 
   * @param aggregatorId
   * @return DataProvider
   * @throws DoesNotExistException
   */
  public DataProvider getProvider(String providerId) throws DoesNotExistException {
    WebTarget target =
        client.target(restUrl + "/" + ProviderOptionListContainer.PROVIDERS + "/" + providerId);
    Response response = target.request(MediaType.APPLICATION_JSON).get();
    if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getProvider(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    }
    DataProvider provider = response.readEntity(DataProvider.class);
    LOGGER.info("getProvider(..) success!");

    return provider;
  }

  /**
   * Delete an provider by specifying the Id.
   * 
   * @param aggregatorId
   * @throws DoesNotExistException
   */
  public void deleteProvider(String providerId) throws DoesNotExistException,
      InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + ProviderOptionListContainer.PROVIDERS + "/" + providerId);
    Response response = target.request(MediaType.APPLICATION_JSON).delete();
    if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("deleteProvider(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    } else if (response.getStatus() == 500) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("deleteProvider(..) failure! : " + errorMessage.getResult());
      throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("deleteProvider(..) success!");
  }

  /**
   * Get a list of provider in the specified range. Returned number can be smaller than the
   * requested. Offset not allowed negative. If number is negative then it returns all the items
   * from offset until the total number of items.
   * 
   * @param offset
   * @param number
   * @return the list of the number of providers requested
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException 
   */
  public List<DataProvider> getProviderList(String aggregatorId, int offset, int number)
      throws InvalidArgumentsException, DoesNotExistException {
    WebTarget target =
        client.target(restUrl + "/" + ProviderOptionListContainer.PROVIDERS)
            .queryParam(ProviderOptionListContainer.AGGREGATORID, aggregatorId)
            .queryParam(ProviderOptionListContainer.OFFSET, offset)
            .queryParam(ProviderOptionListContainer.NUMBER, number);
    Response response = target.request(MediaType.APPLICATION_JSON).get();
    if (response.getStatus() == 400) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getProviderList(..) failure! : " + errorMessage.getResult());
      throw new InvalidArgumentsException(errorMessage.getResult());
    }
    else if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getProviderList(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    }
    List<DataProvider> subList = response.readEntity(new GenericType<List<DataProvider>>() {});
    LOGGER.info("getProviderList(..) success!");

    return subList;
  }

  /**
   * Create a provider.
   * 
   * @param aggregatorId
   * @param id Should be null, it is generated internally
   * @param name
   * @param country
   * @param countryCode
   * @param description
   * @param nameCode
   * @param homepage
   * @param providerType
   * @param email
   * @throws InvalidArgumentsException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   * @throws DoesNotExistException
   */
  public void createProvider(String aggregatorId, String id, String name, String country, String countryCode, 
      String description, String nameCode, String homepage, ProviderType providerType, String email)
      throws InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      InternalServerErrorException, DoesNotExistException {
    WebTarget target =
        client.target(restUrl + "/" + ProviderOptionListContainer.PROVIDERS).queryParam(
            ProviderOptionListContainer.AGGREGATORID, aggregatorId);
    // dataProvider.setCountry("AUSTRIA");


    DataProvider provider =
        new DataProvider(id, name, null, description, null, nameCode, homepage,
            providerType, email);
    provider.setCountry(country);
    provider.setCountryCode(countryCode);
    Response response =
        target.request(MediaType.APPLICATION_JSON).post(
            Entity.entity(provider, MediaType.APPLICATION_JSON), Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createProvider(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createProvider(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createProvider(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
      case 409:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createProvider(..) failure! : " + errorMessage.getResult());
        throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createProvider(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("createProvider(..) success!");
  }
  
  /**
   * Update a provider by specifying the Id. Provider newId can be null if there is no need to
   * change the id.
   * 
   * @param id
   * @param newId Used to change the internal id, otherwise null
   * @param newAggregatorId Used to move the provider to another aggregator, otherwise null
   * @param name
   * @param country
   * @param description
   * @param nameCode
   * @param homepage
   * @param providerType
   * @param email
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException 
   */
  public void updateProvider(String id, String newId, String newAggregatorId, String name, String country, String countryCode,
      String description, String nameCode, String homepage, ProviderType providerType, String email) throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException, AlreadyExistsException{
    WebTarget target =
        client.target(restUrl + "/" + ProviderOptionListContainer.PROVIDERS + "/" + id).queryParam(
            ProviderOptionListContainer.NEWAGGREGATORID, newAggregatorId);
    
    DataProvider provider =
        new DataProvider(newId, name, null, description, null, nameCode, homepage,
            providerType, email);
    provider.setCountry(country);
    provider.setCountryCode(countryCode);
    Response response =
        target.request(MediaType.APPLICATION_JSON).put(
            Entity.entity(provider, MediaType.APPLICATION_JSON), Response.class);
    
    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateProvider(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateProvider(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateProvider(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
      case 409:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateProvider(..) failure! : " + errorMessage.getResult());
        throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateProvider(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("updateProvider(..) success!");
  }
}
