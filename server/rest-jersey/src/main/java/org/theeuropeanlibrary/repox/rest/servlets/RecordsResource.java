/* RecordsResource.java - created on Dec 5, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.ParseException;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.HarvestOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.RecordOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.InvalidInputException;
import pt.utl.ist.util.Urn;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import com.google.gson.Gson;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Records context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Dec 5, 2014
 */
@Path("/" + RecordOptionListContainer.RECORDS)
@Api(value = "/" + RecordOptionListContainer.RECORDS, description = "Rest api for records context")
public class RecordsResource {
    @Context
    UriInfo                   uriInfo;

    public DefaultDataManager dataManager;
    public Urn                urn;        //For mocking tests

    /**
     * Initialize fields before serving.
     */
    public RecordsResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     * @param urn 
     */
    public RecordsResource(DefaultDataManager dataManager, Urn urn) {
        super();
        this.dataManager = dataManager;
        this.urn = urn;
    }

    /**
     * Retrieve all the available options for Records.
     * Relative path : /records
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over record conext.", httpMethod = "OPTIONS", response = RecordOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
    })
    public RecordOptionListContainer getOptions() {
        RecordOptionListContainer recordOptionListContainer = new RecordOptionListContainer(uriInfo.getBaseUri());
        return recordOptionListContainer;
    }

    /**
     * Retrieve all the available options for Records.
     * Relative path : /records/options
     * @return the list of the options available wrapped in a container
     */
    @GET
    @Path("/" + RecordOptionListContainer.OPTIONS)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over record conext.", httpMethod = "GET", response = RecordOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
    })
    public RecordOptionListContainer getGETOptions() {
        return getOptions();
    }

    /**
     * Retrieve the record with the provided id.
     * Relative path : /records
     * @param recordId 
     * @return OK or Error Message 
     * @throws DoesNotExistException 
     * @throws InvalidArgumentsException 
     * @throws IOException 
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get specific record.", httpMethod = "GET", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a Record)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response getRecord(@QueryParam("recordId") String recordId) throws DoesNotExistException, InvalidArgumentsException, IOException {
        Urn recordUrn = null;
        try {
            if (this.urn != null) //For mocking tests
                recordUrn = this.urn;
            else
                recordUrn = new Urn(recordId);
        } catch (InvalidInputException e) {
            throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
        }

        Node record = null;
        try {
            record = dataManager.getRecord(recordUrn);
        } catch (IOException | DocumentException | SQLException e) {
            throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
        } catch (ObjectNotFoundException e) {
            throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (record != null)
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(baos, "UTF-8");
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(outputStreamWriter, format);
            writer.write(record);
            writer.close();
        }
        else
            throw new DoesNotExistException("Does NOT exist: " + "Record with id " + recordId + " NOT found!");

        return Response.status(200).entity(baos.toString("UTF-8")).build();
    }

    /**
     * Deletes (mark) or permanently erase a record.
     * Relative path : /records  
     * @param recordId 
     * @param type 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     * @throws MissingArgumentsException 
     * @throws InvalidArgumentsException 
     */
    @DELETE
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Deletes (mark) or permanently erase a record.", httpMethod = "DELETE", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response removeRecord(@ApiParam(value = "Id of record", required = true) @QueryParam("recordId") String recordId,
            @ApiParam(value = "Delete(mark) or erase(permanent)") @DefaultValue(RecordOptionListContainer.DELETE) @QueryParam("type") String type) throws DoesNotExistException,
            MissingArgumentsException, InvalidArgumentsException {

        if (recordId == null || recordId.equals(""))
            throw new MissingArgumentsException("Missing value: " + "RecordId type missing!");
        if (type == null || type.equals("") || (!type.equals(RecordOptionListContainer.DELETE) && !type.equals(RecordOptionListContainer.ERASE)))
            throw new MissingArgumentsException("Missing value: " + "Query parameter type not valid!");

        if (type.equals(RecordOptionListContainer.DELETE))
        {
            try {
                dataManager.deleteRecord(recordId);
            } catch (DocumentException | SQLException | IOException e) {
                throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
            } catch (ObjectNotFoundException e) {
                throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
            } catch (InvalidInputException e) {
                throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
            }
            return Response.status(200).entity(new Result("Record with id: " + recordId + " deleted!")).build();
        }
        else //Erase
        {
            try {
                dataManager.eraseRecord(recordId);
            } catch (DocumentException | SQLException | IOException e) {
                throw new InternalServerErrorException("Internal Server Error : " + e.getMessage());
            } catch (ObjectNotFoundException e) {
                throw new DoesNotExistException("Does NOT exist: " + e.getMessage());
            } catch (InvalidInputException e) {
                throw new InvalidArgumentsException("Invalid argument: " + e.getMessage());
            }
            return Response.status(200).entity(new Result("Record with id: " + recordId + " erased!")).build();
        }
    }
}
