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

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.theeuropeanlibrary.repox.rest.pathOptions.AggregatorOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.ProviderOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.util.exceptions.DoesNotExistException;

/**
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
  }

  /**
   * Retrieve the provider with the provided id.
   * 
   * @param aggregatorId
   * @return Aggregator
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
        client.target(restUrl + "/" + ProviderOptionListContainer.PROVIDERS + "/"
            + providerId);
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


  public static void main(String[] args) throws DoesNotExistException, MalformedURLException {
    ProvidersAccessor pa =
        new ProvidersAccessor(new URL("http://localhost:8080/repox/rest/"), "temporary",
            "temporary");
    LOGGER.debug("OK");
    DataProvider provider = pa.getProvider("Pr0");
    System.out.println(provider.getId());
    LOGGER.debug("OK");
  }

}
