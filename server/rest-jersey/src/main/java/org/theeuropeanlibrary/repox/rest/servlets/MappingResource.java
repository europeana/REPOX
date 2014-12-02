/* MappingResource.java - created on Nov 27, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.FilenameUtils;
import org.dom4j.DocumentException;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.HarvestOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.MappingOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.metadataSchemas.MetadataSchemaManager;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.metadataTransformation.MetadataTransformationManager;
import pt.utl.ist.metadataTransformation.TransformationsFileManager;
import pt.utl.ist.task.ScheduledTask;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;
import pt.utl.ist.util.exceptions.SameStylesheetTransformationException;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Mappings context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Nov 27, 2014
 */
@Path("/" + MappingOptionListContainer.MAPPINGS)
@Api(value = "/" + MappingOptionListContainer.MAPPINGS, description = "Rest api for mappings")
public class MappingResource {
    @Context
    UriInfo                              uriInfo;

    public DefaultDataManager            dataManager;
    public MetadataTransformationManager metadataTransformationManager;
    public MetadataSchemaManager         metadataSchemaManager;

    //For updating a mapping
    private String                       oldMappingId = "";

    /**
     * Initialize fields before serving.
     */
    public MappingResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
        metadataTransformationManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager();
        metadataSchemaManager = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataSchemaManager();
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     * @param metadataTransformationManager 
     * @param metadataSchemaManager 
     */
    public MappingResource(DefaultDataManager dataManager, MetadataTransformationManager metadataTransformationManager, MetadataSchemaManager metadataSchemaManager) {
        super();
        this.dataManager = dataManager;
        this.metadataTransformationManager = metadataTransformationManager;
        this.metadataSchemaManager = metadataSchemaManager;
    }

    /**
     * Retrieve all the available options for Mappings.
     * Relative path : /mappings
     * @return the list of the options available wrapped in a container.
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over mappings conext.", httpMethod = "OPTIONS", response = MappingOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
    })
    public MappingOptionListContainer getOptions() {
        MappingOptionListContainer mappingOptionListContainer = new MappingOptionListContainer(uriInfo.getBaseUri());
        return mappingOptionListContainer;
    }

    /**
     * Create a new mapping - XSL file through HTTP POST.
     * Relative path : /mappings
     * @param multiPart 
     * @return OK or Error Message
     * @throws AlreadyExistsException 
     * @throws InternalServerErrorException 
     * @throws MissingArgumentsException 
     * @throws DoesNotExistException 
     * @throws InvalidArgumentsException 
     */
    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes("multipart/mixed")
    @ApiOperation(value = "Create a new mapping - XSL file through HTTP POST.", httpMethod = "POST", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response createMapping(MultiPart multiPart) throws AlreadyExistsException, InternalServerErrorException, MissingArgumentsException, DoesNotExistException, InvalidArgumentsException {
        MetadataTransformation metadataTransformation = multiPart.getBodyParts().get(0).getEntityAs(MetadataTransformation.class);
        BodyPartEntity fileBodyPartEntity = (BodyPartEntity)multiPart.getBodyParts().get(1).getEntity();
        InputStream xsltInputStream = fileBodyPartEntity.getInputStream();

        if (metadataTransformation == null)
            throw new MissingArgumentsException("Missing value: " + "MetadataTransformation was null");
        else if (xsltInputStream == null)
            throw new MissingArgumentsException("Missing value: " + "xsltInputStream was null");

        if (metadataTransformation.getId() == null || metadataTransformation.getId().equals(""))
            throw new MissingArgumentsException("Missing value: " + "Id must not be empty");
        else if (metadataTransformation.getSourceSchemaId() == null || metadataTransformation.getSourceSchemaId().equals(""))
            throw new MissingArgumentsException("Missing value: " + "SourceSchemaId must not be empty");
        else if (metadataTransformation.getDestinationSchemaId() == null || metadataTransformation.getDestinationSchemaId().equals(""))
            throw new MissingArgumentsException("Missing value: " + "DestinationSchemaId must not be empty");
        else if (metadataTransformation.getStylesheet() == null || metadataTransformation.getStylesheet().equals(""))
            throw new MissingArgumentsException("Missing value: " + "Stylesheet must not be empty");
        else if (metadataTransformation.getSourceSchemaVersion() == null || metadataTransformation.getSourceSchemaVersion().equals(""))
            throw new MissingArgumentsException("Missing value: " + "SourceSchemaVersion must not be empty");
        else if (metadataTransformation.getDestSchemaVersion() == null || metadataTransformation.getDestSchemaVersion().equals(""))
            throw new MissingArgumentsException("Missing value: " + "DestSchemaVersion must not be empty");

        File xsltDir = metadataTransformationManager.getXsltDir();
        String extension = FilenameUtils.getExtension(metadataTransformation.getStylesheet());
        if (!extension.equals("xsl") && !extension.equals(""))
            throw new InvalidArgumentsException("Stylesheet name must be without extension or with .xsl");
        else if (extension.equals("xsl"))
        {
            metadataTransformation.setStylesheet(FilenameUtils.removeExtension(metadataTransformation.getStylesheet()));
        }
        if(oldMappingId != null && !oldMappingId.isEmpty()) //Validates only when called from the update method
        {
            MetadataTransformation mapping = getMapping(oldMappingId);
            TransformationsFileManager.deleteTransformationFiles(FilenameUtils.removeExtension(mapping.getStylesheet()), xsltDir, metadataTransformationManager.getXmapDir());
        }
        TransformationsFileManager.Response result = TransformationsFileManager.writeXslFile(metadataTransformation.getStylesheet() + ".xsl", xsltDir, xsltInputStream);

        if (result == TransformationsFileManager.Response.ERROR) {
            throw new InternalServerErrorException("Error in server : " + "Error creating Mapping: id \"" + metadataTransformation.getId() + "\" error during file saving.");
        }
        else if (result == TransformationsFileManager.Response.FILE_TOO_BIG) {
            throw new InternalServerErrorException("Error in server : " + "Error creating Mapping: id \"" + metadataTransformation.getId() + "\" xsd file is too big.");
        }
        else if (result == TransformationsFileManager.Response.XSL_ALREADY_EXISTS) {
            throw new AlreadyExistsException("Already exists: " + "Error creating Mapping: id \"" + metadataTransformation.getId() + "\" xslt filename already exists.");
        }

        String srcXsdLink = metadataSchemaManager.getSchemaXSD(metadataTransformation.getSourceSchemaId(), Double.valueOf(metadataTransformation.getSourceSchemaVersion()));
        if (srcXsdLink == null)
            throw new DoesNotExistException("Schema with id " + metadataTransformation.getSourceSchemaId() + " and version " + metadataTransformation.getSourceSchemaVersion() + " does NOT exist!");
        String destXsdLink = metadataSchemaManager.getSchemaXSD(metadataTransformation.getDestinationSchemaId(), Double.valueOf(metadataTransformation.getDestSchemaVersion()));
        if (destXsdLink == null)
            throw new DoesNotExistException("Schema with id " + metadataTransformation.getDestinationSchemaId() + " and version " + metadataTransformation.getDestSchemaVersion() + " does NOT exist!");
        String destNamespace = metadataSchemaManager.getMetadataSchema(metadataTransformation.getDestinationSchemaId()).getNamespace();

        metadataTransformation.setEditable(false);
        metadataTransformation.setSourceSchema(srcXsdLink);
        metadataTransformation.setDestSchema(destXsdLink);
        metadataTransformation.setDestNamespace(destNamespace);
        metadataTransformation.setMDRCompliant(true);
        //If a file was uploaded, then erase its old files
        metadataTransformation.setDeleteOldFiles(true);

        try {
            metadataTransformationManager.saveMetadataTransformation(metadataTransformation, oldMappingId);
        } catch (IOException | DocumentException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        } catch (SameStylesheetTransformationException e) {
            throw new AlreadyExistsException("Already exists: " + "Same Stylesheet");
        }

        return Response.status(201).entity(new Result("Mapping with id " + metadataTransformation.getId() + " created!")).build();
    }

    /**
     * Retrieve a mapping.
     * Relative path : /mappings/{mappingId}
     * @param mappingId 
     * @return XML, JSON: Mapping
     * @throws DoesNotExistException 
     */
    @GET
    @Path("/" + MappingOptionListContainer.MAPPINGID)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Retrieves the specified mapping.", httpMethod = "GET", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Dataset)"),
            @ApiResponse(code = 404, message = "DoesNotExistException")
    })
    public MetadataTransformation getMapping(@ApiParam(value = "Id of mapping", required = true) @PathParam("mappingId") String mappingId) throws DoesNotExistException {

        Map<String, List<MetadataTransformation>> metadataTransformationsMap = metadataTransformationManager.getMetadataTransformations();
        for (List<MetadataTransformation> metadataTransformations : metadataTransformationsMap.values())
        {
            for (MetadataTransformation metadataTransformation : metadataTransformations)
            {
                if (metadataTransformation.getId().toLowerCase().equals(mappingId.toLowerCase()))
                    return metadataTransformation;
            }
        }
        throw new DoesNotExistException("Mapping with id " + mappingId + " does NOT exist!");
    }

    /**
     * Delete a mapping.
     * Relative path : /mappings/{mappingId}  
     * @param mappingId 
     * @return OK or Error Message
     * @throws DoesNotExistException 
     * @throws InternalServerErrorException 
     */
    @DELETE
    @Path("/" + MappingOptionListContainer.MAPPINGID)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Delete a mapping.", httpMethod = "DELETE", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response deleteMapping(@ApiParam(value = "Id of mapping", required = true) @PathParam("mappingId") String mappingId) throws DoesNotExistException, InternalServerErrorException {

        try {
            if (!metadataTransformationManager.deleteMetadataTransformation(mappingId))
                throw new DoesNotExistException("Mapping with id " + mappingId + " does NOT exist!");
        } catch (IOException | DocumentException e) {
            throw new InternalServerErrorException("Error in server : " + e.getMessage());
        }

        return Response.status(200).entity(new Result("Mapping with id " + mappingId + " deleted!")).build();
    }

    /**
     * Update a mapping.
     * Relative path : /mappings/{mappingId}  
     * @param mappingId  
     * @param multiPart 
     * @return OK or Error Message
     * @throws MissingArgumentsException 
     * @throws InvalidArgumentsException 
     * @throws DoesNotExistException 
     * @throws AlreadyExistsException 
     * @throws InternalServerErrorException 
     */
    @PUT
    @Path("/" + MappingOptionListContainer.MAPPINGID)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes("multipart/mixed")
    @ApiOperation(value = "Update a mapping.", httpMethod = "PUT", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException")
    })
    public Response updateMapping(@ApiParam(value = "Id of mapping", required = true) @PathParam("mappingId") String mappingId,
            MultiPart multiPart) throws MissingArgumentsException, InternalServerErrorException, AlreadyExistsException, DoesNotExistException, InvalidArgumentsException {

        MetadataTransformation mapping;
        try {
            mapping = getMapping(mappingId);
        } catch (DoesNotExistException e) {
            throw new RuntimeException("Caused by DoesNotExistException", e);
        }
        oldMappingId = mapping.getId();

        createMapping(multiPart);
        oldMappingId = "";

        return Response.status(200).entity(new Result("Mapping with id " + mappingId + " updated!")).build();
    }

}
