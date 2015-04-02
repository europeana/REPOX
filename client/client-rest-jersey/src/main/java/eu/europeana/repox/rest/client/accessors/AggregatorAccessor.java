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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.theeuropeanlibrary.repox.rest.pathOptions.AggregatorOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.util.exceptions.DoesNotExistException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 2, 2015
 */
public class AggregatorAccessor {

  private String restUrl;
  private Client client = JerseyClientBuilder.newClient();
  private static final Logger LOGGER = LoggerFactory.getLogger(AggregatorAccessor.class);

  public AggregatorAccessor(String restUrl, String username, String password) {
    super();
    this.restUrl = restUrl;
    // TODO check if there is a / at the end
    
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
  }
  
  
  public static void main(String[] args) throws DoesNotExistException {
  AggregatorAccessor aa = new AggregatorAccessor("http://localhost:8080/repox/rest", "temporary", "temporary");
  Aggregator aggregator = aa.getAggregator("A0r");
//  
//  System.out.println(aggregator.getId());
  
  
 
  }

  /**
   * Retrieve an Aggregator by id.
   * @param aggregatorId
   * @return Aggregator
   * @throws DoesNotExistException 
   */
  public Aggregator getAggregator(String aggregatorId) throws DoesNotExistException {
    WebTarget target = client.target(restUrl + "/" + AggregatorOptionListContainer.AGGREGATORS + "/"+ aggregatorId);
    Response response = target.request(MediaType.APPLICATION_JSON).get();
    if(response.getStatus() == 404)
    {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getAggregator() failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    }
    Aggregator aggregator = response.readEntity(Aggregator.class);
    LOGGER.info("getAggregator() success!");

    return aggregator;
  }
  
//  public boolean

  public String getRestUrl() {
    return restUrl;
  }

  public void setRestUrl(String restUrl) {
    this.restUrl = restUrl;
  }

}
