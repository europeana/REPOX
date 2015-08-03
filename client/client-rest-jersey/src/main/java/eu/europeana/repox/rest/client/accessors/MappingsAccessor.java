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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.theeuropeanlibrary.repox.rest.pathOptions.MappingOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 13, 2015
 */
public class MappingsAccessor {
  private URL restUrl;
  private Client client = JerseyClientBuilder.newClient();
  private static final Logger LOGGER = LoggerFactory.getLogger(MappingsAccessor.class);

  /**
   * Setup MappingsAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @throws MalformedURLException
   */
  public MappingsAccessor(URL restUrl, String username, String password)
      throws MalformedURLException {
    super();

    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("DatasetsAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Only used for tests. Setup MappingsAccessor with the target Url, username and password
   * 
   * @param restUrl
   * @param username
   * @param password
   * @param target
   * @throws MalformedURLException
   */
  MappingsAccessor(URL restUrl, String username, String password, Client client)
      throws MalformedURLException {
    super();
    this.restUrl =
        restUrl.toString().charAt(restUrl.toString().length() - 1) == '/' ? new URL(restUrl
            .toString().substring(0, restUrl.toString().length() - 1)) : restUrl;
    this.client = client;
    HttpAuthenticationFeature authfeature = HttpAuthenticationFeature.basic(username, password);
    client.register(authfeature);
    LOGGER.info("MappingsAccessor created with target url: {}", this.restUrl);
  }

  /**
   * Retrieve a mapping.
   * 
   * @param id
   * @return
   * @throws DoesNotExistException
   */
  public MetadataTransformation getMapping(String id) throws DoesNotExistException {
    WebTarget target =
        client.target(restUrl + "/" + MappingOptionListContainer.MAPPINGS + "/" + id);

    Response response = target.request(MediaType.APPLICATION_JSON).get();
    if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("getMapping(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    }
    MetadataTransformation metadataTransformation =
        response.readEntity(MetadataTransformation.class);
    LOGGER.info("getMapping(..) success!");
    return metadataTransformation;
  }

  /**
   * Delete a mapping.
   * 
   * @param id
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  public void deleteMapping(String id) throws DoesNotExistException, InternalServerErrorException {
    WebTarget target =
        client.target(restUrl + "/" + MappingOptionListContainer.MAPPINGS + "/" + id);
    Response response = target.request(MediaType.APPLICATION_JSON).delete();
    if (response.getStatus() == 404) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("deleteMapping(..) failure! : " + errorMessage.getResult());
      throw new DoesNotExistException(errorMessage.getResult());
    } else if (response.getStatus() == 500) {
      Result errorMessage = response.readEntity(Result.class);
      LOGGER.warn("deleteMapping(..) failure! : " + errorMessage.getResult());
      throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("deleteMapping(..) success!");
  }

  /**
   * Create a new mapping - XSL file through HTTP POST.
   * @param metadataTransformation
   * @param file
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  public void createMapping(MetadataTransformation metadataTransformation, File file)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException {
    WebTarget target = client.register(MultiPartFeature.class).target(restUrl + "/" + MappingOptionListContainer.MAPPINGS);

    // MediaType of the body part will be derived from the file.
    final FileDataBodyPart filePart =
        new FileDataBodyPart(file.getName(), file, MediaType.APPLICATION_OCTET_STREAM_TYPE);
    MultiPart multiPartEntity = new MultiPart();
    multiPartEntity.bodyPart(new BodyPart(metadataTransformation, MediaType.APPLICATION_XML_TYPE));
    multiPartEntity.bodyPart(filePart);

    Response response =
        target.request().post(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")),
            Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createMapping(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createMapping(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createMapping(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
     // case 409:
     //   errorMessage = response.readEntity(Result.class);
     //   LOGGER.warn("createMapping(..) failure! : " + errorMessage.getResult());
     //   throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("createMapping(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("createMapping(..) success!");
  }
  
  /**
   * Update a mapping.
   * @param id
   * @param metadataTransformation
   * @param file
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  public void updateMapping(String id, MetadataTransformation metadataTransformation, File file)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException {
    WebTarget target = client.register(MultiPartFeature.class).target(restUrl + "/" + MappingOptionListContainer.MAPPINGS + "/" + id);

    // MediaType of the body part will be derived from the file.
    final FileDataBodyPart filePart =
        new FileDataBodyPart(file.getName(), file, MediaType.APPLICATION_OCTET_STREAM_TYPE);
    MultiPart multiPartEntity = new MultiPart();
    multiPartEntity.bodyPart(new BodyPart(metadataTransformation, MediaType.APPLICATION_XML_TYPE));
    multiPartEntity.bodyPart(filePart);

    Response response =
        target.request().put(Entity.entity(multiPartEntity, new MediaType("multipart", "mixed")),
            Response.class);

    switch (response.getStatus()) {
      case 400:
        Result errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateMapping(..) failure! : " + errorMessage.getResult());
        throw new InvalidArgumentsException(errorMessage.getResult());
      case 404:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateMapping(..) failure! : " + errorMessage.getResult());
        throw new DoesNotExistException(errorMessage.getResult());
      case 406:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateMapping(..) failure! : " + errorMessage.getResult());
        throw new MissingArgumentsException(errorMessage.getResult());
     // case 409:
     //   errorMessage = response.readEntity(Result.class);
    //    LOGGER.warn("updateMapping(..) failure! : " + errorMessage.getResult());
     //   throw new AlreadyExistsException(errorMessage.getResult());
      case 500:
        errorMessage = response.readEntity(Result.class);
        LOGGER.warn("updateMapping(..) failure! : " + errorMessage.getResult());
        throw new InternalServerErrorException(errorMessage.getResult());
    }
    LOGGER.info("updateMapping(..) success!");
  }

//  public static void main(String[] args) throws MalformedURLException, DoesNotExistException, InternalServerErrorException, InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException {
//    MappingsAccessor ma =
//        new MappingsAccessor(new URL("http://localhost:8080/repox/rest"), "temporary", "temporary");
//
//    // System.out.println(ma.getMapping("Example").getDestinationSchemaId());
////    ma.deleteMapping("Example");
//    
//    String id = "SampleId2";
//    String description = "NONE";
//    String srcSchemaId = "edm";
//    String srcSchemaVersion = "1.0";
//    String destSchemaId = "lido";
//    String destSchemaVersion = "1";
//    String xslFilename = "myXSLT3";
//    boolean isXslVersion2 = true;
//
//    MetadataTransformation mtdTransformation = new MetadataTransformation();
//    mtdTransformation.setId(id);
//    mtdTransformation.setDescription(description);
//    mtdTransformation.setSourceSchemaId(srcSchemaId);
//    mtdTransformation.setDestinationSchemaId(destSchemaId);
//    mtdTransformation.setStylesheet(xslFilename);
//    mtdTransformation.setSourceSchemaVersion(srcSchemaVersion);
//    mtdTransformation.setDestSchemaVersion(destSchemaVersion);
//    mtdTransformation.setVersionTwo(isXslVersion2);
//    
//    File xlst = new File("/tmp/example.xsl");
//    ma.updateMapping("SampleId2", mtdTransformation, xlst);
//
//  }

}
