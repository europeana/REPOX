/* ProvidersResource.java - created on Oct 24, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.dom4j.DocumentException;
import org.theeuropeanlibrary.repox.rest.pathOptions.ProviderOptionListContainer;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;

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
    UriInfo                   uriInfo;

    public DefaultDataManager dataManager;

    /**
     * Initialize fields before serving.
     */
    public ProvidersResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }

    /**
     * Creates a new instance by providing the DataManager.
     * @param dataManager
     */
    public ProvidersResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }

    /**
     * Retrieve all the available options for Providers
     * Relative path : /providers
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Get options over provider conext.", httpMethod = "OPTIONS", response = ProviderOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
          })
    public ProviderOptionListContainer getOptions() {
        ProviderOptionListContainer providerOptionListContainer = new ProviderOptionListContainer(uriInfo.getBaseUri());
        return providerOptionListContainer;
    }

    /**
     * Retrieve the provider with the provided id.
     * Relative path : /providers/{providerId}
     * @param providerId 
     * @return Provider information
     * @throws DoesNotExistException 
     */
    @GET
    @Path("/" + ProviderOptionListContainer.PROVIDERID)
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Get specific provider.", httpMethod = "GET", response = DataProvider.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Provider)"),
            @ApiResponse(code = 404, message = "DoesNotExistException")
          })
    public DataProvider getProvider(@ApiParam(value = "Id of provider", required = true) @PathParam("providerId") String providerId) throws DoesNotExistException {
        DataProvider provider = null;
        provider = dataManager.getDataProvider(providerId);
        if (provider == null)
            throw new DoesNotExistException("Provider does NOT exist!");

        return provider;
    }

    /**
     * Create an provider provided in the body of the post call.
     * Relative path : /providers
     * @param provider 
     * @return OK or Error Message
     * @throws MissingArgumentsException 
     * @throws AlreadyExistsException 
     * @throws InvalidArgumentsException 
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @ApiOperation(value = "Create a provider.", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
          })
    public Response createProvider(@ApiParam(value = "Provider id is not required", required = true) DataProvider provider) throws MissingArgumentsException, AlreadyExistsException, InvalidArgumentsException {
//        if(provider.getId() != null)
//            throw new InvalidArgumentsException("Invalid value: " + "Provider Id provided in body must be null");
//        
//        String name = provider.getName();
//        String country = provider.getCountry();
//        String description = provider.getDescription();
//        String nameCode = provider.getNameCode();
//        String homepage = provider.getHomePage();
//        String dataSetType = provider.getProviderType().toString();
//        String email = provider.getEmail();
//        
//        if(name == null || name.equals(""))
//            throw new InvalidArgumentsException("Invalid value: " + "Provider name must not be empty");
//        else if(country == null || country.equals(""))
//            throw new InvalidArgumentsException("Invalid value: " + "Provider country must not be empty");
//        else if(dataSetType == null || dataSetType.equals(""))
//            throw new InvalidArgumentsException("Invalid value: " + "Provider dataSetType must not be empty");
//        
//        DataProvider createdProvider = null;
//        if (provider.getName() != null && !provider.getName().isEmpty()) {
//            try {
//                dataManager.createDataProvider(aggregatorId, name, country, description, nameCode, homepage, dataSetType, email);
//            } catch (DocumentException | IOException e) {
//                throw new InternalServerErrorException("Error in server : " + e.getMessage());
//            } catch (InvalidArgumentsException e) { //This happens when the URL is invalid
//                throw new InvalidArgumentsException("Invalid value: " + e.getMessage());
//            } catch (AlreadyExistsException e) { //This basically happens if and provider already exists with both name and nameCode the same as the one provided 
//                throw new AlreadyExistsException("Provider with name \"" + e.getMessage() + "\" already exists!");
//            }
//        } else
//            throw new MissingArgumentsException("Missing argument name!");
//        return Response.created(null)
//                .entity(new Result("Aggregator with name = " + createdAggregator.getName() + " and id = " + createdAggregator.getId() + " created successfully"))
//                .build();
        return null;
    }
//
//    /**
//     * Delete an aggregator by specifying the Id.
//     * Relative path : /aggregators
//     * @param aggregatorId 
//     * @return OK or Error Message
//     * @throws DoesNotExistException 
//     */
//    @DELETE
//    @Path("/" + AggregatorOptionListContainer.AGGREGATORID)
//    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
//    @ApiOperation(value = "Delete an aggregator.", httpMethod = "DELETE")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
//            @ApiResponse(code = 404, message = "DoesNotExistException"),
//            @ApiResponse(code = 500, message = "InternalServerErrorException")
//          })
//    public Response deleteAggregator(@ApiParam(value = "Id of aggregator", required = true) @PathParam("aggregatorId") String aggregatorId) throws DoesNotExistException {
//
//        try {
//            dataManager.deleteAggregator(aggregatorId);
//        } catch (DocumentException | IOException e) {
//            throw new InternalServerErrorException("Error in server : " + e.getMessage());
//        } catch (ObjectNotFoundException e) {
//            throw new DoesNotExistException(
//                    "A resource of the aggregator or the aggregator itself with id \"" + e.getMessage() + "\" does NOT exist!");
//        }
//
//        return Response.status(200).entity(new Result("Aggregator with id " + aggregatorId + " deleted!")).build();
//    }
//
//    /**
//     * Update an aggregator by specifying the Id.
//     * The Id of the aggregator is provided as a path parameter and in request body there is an aggregator with the update that are requested(name, nameCode, homePage) the remaining fields of the Aggregator class provided can be null
//     * Relative path : /aggregators
//     * @param aggregatorId 
//     * @param aggregator 
//     * @return OK or Error Message
//     * @throws DoesNotExistException 
//     * @throws InvalidArgumentsException 
//     */
//    @PUT
//    @Path("/" + AggregatorOptionListContainer.AGGREGATORID)
//    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
//    @ApiOperation(value = "Update an aggregator.", httpMethod = "PUT")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
//            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
//            @ApiResponse(code = 404, message = "DoesNotExistException"),
//            @ApiResponse(code = 500, message = "InternalServerErrorException")
//          })
//    public Response updateAggregator(@ApiParam(value = "Id of aggregator", required = true) @PathParam("aggregatorId") String aggregatorId,@ApiParam(value = "Aggregator id is not required", required = true) Aggregator aggregator) throws DoesNotExistException,
//            InvalidArgumentsException {
//
//        String name = aggregator.getName();
//        String nameCode = aggregator.getNameCode();
//        String homePage = aggregator.getHomePage();
//
//        try {
//            dataManager.updateAggregator(aggregatorId, name, nameCode, homePage);
//        } catch (IOException e) {
//            throw new InternalServerErrorException("Error in server : " + e.getMessage());
//        } catch (ObjectNotFoundException e) {
//            throw new DoesNotExistException(
//                    "A resource of the aggregator or the aggregator itself with id \"" + e.getMessage() + "\" does NOT exist!");
//        } catch (InvalidArgumentsException e) { //This happens when the URL is invalid
//            throw new InvalidArgumentsException("Invalid value: " + e.getMessage());
//        }
//
//        return Response.status(200).entity(new Result("Aggregator with id " + aggregatorId + " updated!")).build();
//    }
//
//    /**
//     * Get a list of aggregators in the specified range.
//     * Offset not allowed negative. If number is negative then it returns all the items from offset until the total number of items.
//     * Relative path : /aggregators
//     * @param offset 
//     * @param number 
//     * @return the list of the number of aggregators requested
//     * @throws Exception 
//     * @throws InvalidArgumentsException 
//     */
//    @GET
//    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
//    @ApiOperation(value = "Get a list of aggregators.", httpMethod = "GET")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "OK (Response containing a list of aggregators)"),
//            @ApiResponse(code = 400, message = "InvalidArgumentsException")
//          })
//    public Response getAggregatorList(@ApiParam(value = "Index where to start from", required = true) @DefaultValue("0") @QueryParam("offset") int offset, @ApiParam(value = "Number of aggregators requested", required = true) @DefaultValue("-1") @QueryParam("number") int number) throws Exception, InvalidArgumentsException {
//        
//        if(offset < 0)
//            throw new InvalidArgumentsException("Offset negative values not allowed!");
//        
//        List<Aggregator> aggregatorsListSorted;
//        try {
//            aggregatorsListSorted = dataManager.getAggregatorsListSorted(offset, number);
//        } catch (IndexOutOfBoundsException e) {
//            throw new InvalidArgumentsException("Invalid argument : " + e.getMessage());
//        }
//
//        return  Response.status(200).entity(new GenericEntity<List<Aggregator>>(aggregatorsListSorted) {}).build();
//    }
//
////    @GET
////    @Produces({ MediaType.TEXT_PLAIN })
////    public String test() throws DoesNotExistException {
////        throw new DoesNotExistException("AAAAA");
////
////    }

}

