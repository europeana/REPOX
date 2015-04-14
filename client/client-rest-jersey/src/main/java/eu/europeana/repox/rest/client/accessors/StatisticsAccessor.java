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

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;
import org.theeuropeanlibrary.repox.rest.pathOptions.StatisticsOptionListContainer;

/**
 * Statistics access.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 13, 2015
 */
public class StatisticsAccessor {
  private URL restUrl;
  private Client client = JerseyClientBuilder.newClient();
  private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsAccessor.class);

  /**
   * Setup StatisticsAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @throws MalformedURLException
   */
  public StatisticsAccessor(URL restUrl, String username, String password)
      throws MalformedURLException {
    super();

    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("StatisticsAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Only used for tests. Setup StatisticsAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @param username
   * @param password
   * @param target
   * @throws MalformedURLException
   */
  StatisticsAccessor(URL restUrl, String username, String password, Client client)
      throws MalformedURLException {
    super();
    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    this.client = client;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("StatisticsAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Retrieve the statistics. 
   * @return String with the statistics
   */
  public String getStatistics() {
    WebTarget target = client.target(restUrl + "/" + StatisticsOptionListContainer.STATISTICS);
    Response response = target.request(MediaType.APPLICATION_JSON).get();
    if (response.getStatus() == 500) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getStatistics(..) failure! : " + errorMessage.getResult());
      throw new InternalServerErrorException(errorMessage.getResult());
    }
    Result result = response.readEntity(Result.class);
    LOGGER.info("getStatistics(..) success!");
    return result.getResult();
  }
  
//  public static void main(String[] args) throws MalformedURLException {
//    StatisticsAccessor sa = new StatisticsAccessor(new URL("http://localhost:8080/repox/rest"), "temporary", "temporary");
//
//    System.out.println(sa.getStatistics());
//  }

}
