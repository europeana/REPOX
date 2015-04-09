/*
 * Copyright 2007-2015 The Europeana Foundation
 *
 *  Licensed under the EUPL, Version 1.1 (the "License") and subsequent versions as approved 
 *  by the European Commission;
 *  You may not use this work except in compliance with the License.
 *  
 *  You may obtain a copy of the License at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under 
 *  the License is distributed on an "AS IS" basis, without warranties or conditions of 
 *  any kind, either express or implied.
 *  See the License for the specific language governing permissions and limitations under 
 *  the License.
 */
package eu.europeana.repox.rest.client.accessors;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.theeuropeanlibrary.repox.rest.pathOptions.HarvestOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
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
//    Mockito.when(builder.get()).thenReturn(response);
//    Mockito.when(builder.delete()).thenReturn(response);
    Mockito.when(
        builder.post(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
            Mockito.any(Class.class))).thenReturn(response);
//    Mockito.when(
//        builder.put(Entity.entity(Mockito.any(Class.class), MediaType.APPLICATION_JSON),
//            Mockito.any(Class.class))).thenReturn(response);
  }
  
  // Tests for StartHarvest
  @Test
  public void testStartHarvest() throws AlreadyExistsException, DoesNotExistException{
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
  public void testStartHarvestAlreadyExists() throws AlreadyExistsException, DoesNotExistException{
    Mockito.when(response.getStatus()).thenReturn(409);
    Mockito.when(response.readEntity(Result.class)).thenReturn(new Result("Already exist!"));
    ha.startHarvest("ds0", HarvestOptionListContainer.SAMPLE);
  }

  @Test(expected = InternalServerErrorException.class)
  public void testStartHarvestInternalServerError() throws AlreadyExistsException, DoesNotExistException{
    Mockito.when(response.getStatus()).thenReturn(500);
    Mockito.when(response.readEntity(Result.class))
        .thenReturn(new Result("Internal Server Error!"));
    ha.startHarvest("ds0", HarvestOptionListContainer.SAMPLE);
  }
}
