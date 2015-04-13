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

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
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
public class RecordsAccessor {
  private URL restUrl;
  private Client client = JerseyClientBuilder.newClient();
  private static final Logger LOGGER = LoggerFactory.getLogger(DatasetsAccessor.class);

  /**
   * Setup RecordsAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @throws MalformedURLException
   */
  public RecordsAccessor(URL restUrl, String username, String password)
      throws MalformedURLException {
    super();

    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("RecordsAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Only used for tests. Setup RecordsAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @param username
   * @param password
   * @param target
   * @throws MalformedURLException
   */
  RecordsAccessor(URL restUrl, String username, String password, Client client)
      throws MalformedURLException {
    super();
    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    this.client = client;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("RecordsAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Retrieve the record with the provided id.
   * @param id
   * @return String with the record in xml
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  public String getRecord(String id) throws InvalidArgumentsException, DoesNotExistException,
      InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + RecordOptionListContainer.RECORDS).queryParam("recordId", id);
    Response response = target.request(MediaType.APPLICATION_JSON).get();

    if (response.getStatus() == 400) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getRecord(..) failure! : " + errorMessage.getResult());
      throw new InvalidArgumentsException(errorMessage.getResult());
    } else if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getRecord(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    } else if (response.getStatus() == 500) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getRecord(..) failure! : " + errorMessage.getResult());
      throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("getRecord(..) success!");
    Result result = response.readEntity(Result.class);
    return result.getResult();
  }
  
  /**
   * Deletes (mark) or permanently erase a record.
   * @param id
   * @param type
   * @throws MissingArgumentsException
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  public void removeRecord(String id, String type) throws MissingArgumentsException, DoesNotExistException, InternalServerErrorException
  {
    WebTarget target = client.target(restUrl + "/" + RecordOptionListContainer.RECORDS).queryParam("recordId", id).queryParam("type", type);
    Response response = target.request(MediaType.APPLICATION_JSON).delete();
    
    switch (response.getStatus()) {
      case 404:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("removeRecord(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("removeRecord(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("removeRecord(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    Result result = response.readEntity(Result.class);
    LOGGER.info("removeRecord(..) success! : " + result.getResult());
  }

  public static void main(String[] args) throws MalformedURLException, InternalServerErrorException, InvalidArgumentsException, DoesNotExistException, UnsupportedEncodingException, FileNotFoundException, MissingArgumentsException {
    RecordsAccessor ra =
        new RecordsAccessor(new URL("http://localhost:8080/repox/rest"), "temporary", "temporary");
    
//    System.out.println(ra.getRecord("oai:the.european.library.a0660:urn:onb.ac.at:abo:http://data.onb.ac.at/ABO/%2BZ103526808"));
    
//    String s = ra.getRecord("oai:the.european.library.a0660:urn:onb.ac.at:abo:http://data.onb.ac.at/ABO/%252BZ103526808");
//    System.out.println(s);
    
    ra.removeRecord("oai:the.european.library.a0660:urn:onb.ac.at:abo:http://data.onb.ac.at/ABO/%252BZ103526808", RecordOptionListContainer.ERASE);
  }

}
