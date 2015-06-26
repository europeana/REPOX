/*
 * ProvidersResource.java - created on Oct 24, 2014, Copyright (c) 2011 The European Library, all
 * rights reserved
 */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.theeuropeanlibrary.repox.rest.pathOptions.ProviderOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.Countries;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Providers context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 24, 2014
 */

@Path("/" + ProviderOptionListContainer.PROVIDERS)
@Api(value = "/" + ProviderOptionListContainer.PROVIDERS, description = "Rest api for providers")
public class ProvidersResource {
  @Context
  UriInfo uriInfo;

  public DefaultDataManager dataManager;

  /**
   * Initialize fields before serving.
   */
  public ProvidersResource() {
    ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
    dataManager =
        ((DefaultDataManager) ConfigSingleton.getRepoxContextUtil().getRepoxManager()
            .getDataManager());
  }

  /**
   * Creates a new instance by providing the DataManager. (For Tests)
   * 
   * @param dataManager
   */
  public ProvidersResource(DefaultDataManager dataManager) {
    super();
    this.dataManager = dataManager;
  }

  /**
   * Retrieve all the available options for Providers. Relative path : /providers
   * 
   * @return the list of the options available wrapped in a container
   */
  @OPTIONS
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get options over providers conext.", httpMethod = "OPTIONS",
      response = ProviderOptionListContainer.class)
  @ApiResponses(value = {@ApiResponse(code = 200,
      message = "OK (Response containing a list of all available options)")})
  public ProviderOptionListContainer getOptions() {
    ProviderOptionListContainer providerOptionListContainer =
        new ProviderOptionListContainer(uriInfo.getBaseUri());
    return providerOptionListContainer;
  }

  /**
   * Retrieve all the available options for Providers(For browser visibility). Relative path :
   * /providers
   * 
   * @return the list of the options available wrapped in a container
   */
  @GET
  @Path("/" + ProviderOptionListContainer.OPTIONS)
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get options over providers conext.", httpMethod = "GET",
      response = ProviderOptionListContainer.class)
  @ApiResponses(value = {@ApiResponse(code = 200,
      message = "OK (Response containing a list of all available options)")})
  public ProviderOptionListContainer getGETOptions() {
    return getOptions();
  }

  /**
   * Retrieve the provider with the provided id. Relative path : /providers/{providerId}
   * 
   * @param providerId
   * @return Provider information
   * @throws DoesNotExistException
   */
  @GET
  @Path("/" + ProviderOptionListContainer.PROVIDERID)
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get specific provider.", httpMethod = "GET", response = DataProvider.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK (Response containing an Provider)"),
      @ApiResponse(code = 404, message = "DoesNotExistException")})
  public DataProvider getProvider(
      @ApiParam(value = "Id of provider", required = true) @PathParam("providerId") String providerId)
      throws DoesNotExistException {
    DataProvider provider = null;
    provider = dataManager.getDataProvider(providerId);
    if (provider == null)
      throw new DoesNotExistException("Provider with id " + providerId + " does NOT exist!");

    return provider;
  }

  /**
   * Create an provider provided in the body of the post call. Relative path : /providers
   * 
   * @param aggregatorId
   * @param provider
   * @return OK or Error Message
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   */
  @POST
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Create a provider.", httpMethod = "POST", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Created (Response containing a String message)"),
      @ApiResponse(code = 400, message = "InvalidArgumentsException"),
      @ApiResponse(code = 404, message = "DoesNotExistException"),
      @ApiResponse(code = 406, message = "MissingArgumentsException"),
      @ApiResponse(code = 409, message = "AlreadyExistsException"),
      @ApiResponse(code = 500, message = "InternalServerErrorException")})
  public Response createProvider(
      @ApiParam(value = "AggregatorId", required = true) @QueryParam("aggregatorId") String aggregatorId,
      @ApiParam(value = "Provider data", required = true) DataProvider provider)
      throws MissingArgumentsException, AlreadyExistsException, InvalidArgumentsException,
      DoesNotExistException {

    if (aggregatorId == null || aggregatorId.equals(""))
      throw new MissingArgumentsException("Missing argument aggregatorId!");

    String providerId = provider.getId();
    String name = provider.getName();
    String country = provider.getCountry();
    String countryCode = provider.getCountryCode();
    String description = provider.getDescription();
    String nameCode = provider.getNameCode();
    String homepage = provider.getHomepage();
    String providerType = null;
    if (provider.getProviderType() != null)
      providerType = provider.getProviderType().toString();
    String email = provider.getEmail();

    if (name == null || name.isEmpty())
      throw new MissingArgumentsException("Missing value: " + "Provider name must not be empty.");
    else if ((country == null || country.equals(""))
        && (countryCode == null || countryCode.equals("")))
      throw new MissingArgumentsException("Missing value: "
          + "Provider country or countryCode must not be empty.");
    else if (providerType == null || providerType.equals(""))
      throw new MissingArgumentsException("Missing value: "
          + "Provider dataSetType must not be empty.");

    Map<String, String> countries = Countries.getCountries();
    if (countryCode != null && !countryCode.equals("")) {
      boolean flag = false;
      for (Map.Entry<String, String> mapEntry : countries.entrySet())
        if (mapEntry.getKey().equalsIgnoreCase(countryCode)) {
          country = mapEntry.getValue();
          countryCode = mapEntry.getKey();
          flag = true;
          break;
        }
      if (!flag)
        throw new InvalidArgumentsException("Invalid argument: "
            + "Provider countryCode doesn't exist.");
    } else if (country != null && !country.equals("")) {
      boolean flag = false;
      for (Map.Entry<String, String> mapEntry : countries.entrySet())
        if (mapEntry.getValue().equalsIgnoreCase(country)) {
          country = mapEntry.getValue();
          countryCode = mapEntry.getKey();
          flag = true;
          break;
        }
      if (!flag)
        throw new InvalidArgumentsException("Invalid argument: "
            + "Provider country doesn't exist.");
    }

    DataProvider createdProvider = null;
    try {
      createdProvider =
          dataManager.createDataProvider(aggregatorId, providerId, name, country, countryCode,
              description, nameCode, homepage, providerType, email);
    } catch (IOException e) {
      throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
    } catch (InvalidArgumentsException e) { // This happens when the URL is invalid
      throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
    } catch (AlreadyExistsException e) { // This basically happens if and provider already exists
                                         // with the same Id
      throw new AlreadyExistsException("Already exists: " + e.getMessage());
    } catch (ObjectNotFoundException e) {
      throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
    }

    return Response
        .created(null)
        .entity(
            new Result("DataProvider with id = <" + createdProvider.getId() + "> and name = "
                + createdProvider.getName() + " created successfully")).build();
  }

  /**
   * Delete a provider by specifying the Id. Relative path : /providers
   * 
   * @param dataProviderId
   * @return OK or Error Message
   * @throws DoesNotExistException
   */
  @DELETE
  @Path("/" + ProviderOptionListContainer.PROVIDERID)
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Delete a provider.", httpMethod = "DELETE", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
      @ApiResponse(code = 404, message = "DoesNotExistException"),
      @ApiResponse(code = 500, message = "InternalServerErrorException")})
  public Response deleteProvider(
      @ApiParam(value = "Id of provider", required = true) @PathParam("providerId") String dataProviderId)
      throws DoesNotExistException {
    try {
      dataManager.deleteDataProvider(dataProviderId);
    } catch (IOException e) {
      throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
    } catch (ObjectNotFoundException e) {
      throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
    }

    return Response.status(200)
        .entity(new Result("Provider with id " + dataProviderId + " deleted!")).build();
  }

  /**
   * Update a provider by specifying the Id. Relative path : /providers
   * 
   * @param providerId
   * @param newAggregatorId
   * @param provider
   * @return OK or Error Message
   * @throws DoesNotExistException
   * @throws InvalidArgumentsException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   */
  @PUT
  @Path("/" + ProviderOptionListContainer.PROVIDERID)
  @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Update a provider.", httpMethod = "PUT", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
      @ApiResponse(code = 400, message = "InvalidArgumentsException"),
      @ApiResponse(code = 404, message = "DoesNotExistException"),
      @ApiResponse(code = 406, message = "MissingArgumentsException"),
      @ApiResponse(code = 409, message = "AlreadyExistsException"),
      @ApiResponse(code = 500, message = "InternalServerErrorException")})
  public Response updateProvider(
      @ApiParam(value = "Id of provider", required = true) @PathParam("providerId") String providerId,
      @ApiParam(value = "Aggregator Id", required = false) @QueryParam("newAggregatorId") String newAggregatorId,
      @ApiParam(value = "Provider data", required = true) DataProvider provider)
      throws DoesNotExistException, InvalidArgumentsException, MissingArgumentsException,
      AlreadyExistsException {

    String newProviderId = provider.getId();
    String name = provider.getName();
    String country = provider.getCountry();
    String countryCode = provider.getCountryCode();
    String description = provider.getDescription();
    String nameCode = provider.getNameCode();
    String homepage = provider.getHomepage();
    String providerType = null;
    if (provider.getProviderType() != null)
      providerType = provider.getProviderType().toString();
    String email = provider.getEmail();

    if (name == null || name.isEmpty())
      throw new MissingArgumentsException("Missing value: " + "Provider name must not be empty.");
    else if ((country == null || country.equals(""))
        && (countryCode == null || countryCode.equals("")))
      throw new MissingArgumentsException("Missing value: "
          + "Provider country or countryCode must not be empty.");
    else if (providerType == null || providerType.equals(""))
      throw new MissingArgumentsException("Missing value: "
          + "Provider dataSetType must not be empty.");

    Map<String, String> countries = Countries.getCountries();
    if (countryCode != null && !countryCode.equals("")) {
      boolean flag = false;
      for (Map.Entry<String, String> mapEntry : countries.entrySet())
        if (mapEntry.getKey().equalsIgnoreCase(countryCode)) {
          country = mapEntry.getValue();
          countryCode = mapEntry.getKey();
          flag = true;
          break;
        }
      if (!flag)
        throw new InvalidArgumentsException("Invalid argument: "
            + "Provider countryCode doesn't exist.");
    } else if (country != null && !country.equals("")) {
      boolean flag = false;
      for (Map.Entry<String, String> mapEntry : countries.entrySet())
        if (mapEntry.getValue().equalsIgnoreCase(country)) {
          country = mapEntry.getValue();
          countryCode = mapEntry.getKey();
          flag = true;
          break;
        }
      if (!flag)
        throw new InvalidArgumentsException("Invalid argument: "
            + "Provider country doesn't exist.");
    }

    try {
      dataManager.updateDataProvider(newAggregatorId, providerId, newProviderId, name, country,
          countryCode, description, nameCode, homepage, providerType, email);
    } catch (ObjectNotFoundException e) {
      throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
    } catch (IOException e) {
      throw new InternalServerErrorException("Error in server : " + e.getMessage());
    } catch (AlreadyExistsException e) {
      throw new AlreadyExistsException("Already exists: " + e.getMessage());
    }

    if (newProviderId != null && !newProviderId.isEmpty() && !providerId.equals(newProviderId))
      return Response
          .status(200)
          .entity(
              new Result("Provider with id " + providerId + " updated and has now id : "
                  + newProviderId + "!")).build();
    else
      return Response.status(200)
          .entity(new Result("Provider with id " + providerId + " updated!")).build();
  }

  /**
   * Get a list of provider in the specified range. Returned number can be smaller than the
   * requested. Offset not allowed negative. If number is negative then it returns all the items
   * from offset until the total number of items.
   * 
   * Relative path : /providers
   * 
   * @param aggregatorId
   * @param offset
   * @param number
   * @return the list of the number of providers requested
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
  @ApiOperation(value = "Get a list of providers.", httpMethod = "GET",
      response = DataProvider.class, responseContainer = "List")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "OK (Response containing a list of providers)"),
      @ApiResponse(code = 400, message = "InvalidArgumentsException"),
      @ApiResponse(code = 404, message = "DoesNotExistException")})
  public Response getProviderList(
      @ApiParam(value = "AggregatorId", required = true) @QueryParam("aggregatorId") String aggregatorId,
      @ApiParam(value = "Index where to start from(negative not allowed)", defaultValue = "0") @DefaultValue("0") @QueryParam("offset") int offset,
      @ApiParam(value = "Number of aggregators requested(-1 to get all)", defaultValue = "-1") @DefaultValue("-1") @QueryParam("number") int number)
      throws InvalidArgumentsException, DoesNotExistException {

    if (offset < 0)
      throw new InvalidArgumentsException("Offset negative values not allowed!");

    List<DataProvider> providersListSorted;

    try {
      providersListSorted = dataManager.getDataProvidersListSorted(aggregatorId, offset, number);
    } catch (ObjectNotFoundException e) {
      throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
    } catch (IndexOutOfBoundsException e) {
      throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
    }

    return Response.status(200)
        .entity(new GenericEntity<List<DataProvider>>(providersListSorted) {}).build();
  }
}
