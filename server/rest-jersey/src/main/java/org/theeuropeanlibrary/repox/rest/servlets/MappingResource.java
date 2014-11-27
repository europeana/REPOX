/* MappingResource.java - created on Nov 27, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.theeuropeanlibrary.repox.rest.pathOptions.MappingOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.MessageType;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.metadataTransformation.TransformationsFileManager;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
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
    UriInfo                   uriInfo;

    public DefaultDataManager dataManager;

    /**
     * Initialize fields before serving.
     */
    public MappingResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     */
    public MappingResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
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
    
    
//    /**
//     * Test retrieve a text file.
//     * Relative path : /mappings
//     * @return message.
//     * @throws IOException 
//     */
//    @POST
//    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
//    @Consumes("multipart/mixed")
////    @ApiOperation(value = "Get options over mappings conext.", httpMethod = "OPTIONS", response = MappingOptionListContainer.class)
////    @ApiResponses(value = {
////            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)")
////    })
//    public Response uploadFile(MultiPart multiPart) throws IOException {
//
//        System.out.println(multiPart.getBodyParts().get(0).getEntityAs(String.class));
//        
//        System.out.println(multiPart.getBodyParts().get(1).getMediaType());
//        BodyPartEntity bodyPartEntity = (BodyPartEntity)multiPart.getBodyParts().get(1).getEntity();
//        
//        StringBuilder sb = new StringBuilder(); 
//        InputStream stream = bodyPartEntity.getInputStream(); 
//        InputStreamReader reader = new InputStreamReader(stream); 
//        char[] buffer = new char[2048]; 
//        while (true) { 
//            int n = reader.read(buffer); 
//            if (n < 0) { 
//                break; 
//            } 
//            sb.append(buffer, 0, n); 
//        } 
//        
//        System.out.println(sb.toString());
//        
////        File xsltDir = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().getXsltDir();
////        TransformationsFileManager.Response result = TransformationsFileManager.writeXslFile(xslFilename + ".xsl", xsltDir, xsdFile);
////
////        if (result == TransformationsFileManager.Response.ERROR) {
////            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: id \"" + id + "\" error during file saving.");
////            return;
////        }
////        else if (result == TransformationsFileManager.Response.FILE_TOO_BIG) {
////            createErrorMessage(out, MessageType.OTHER, "Error creating Mapping: id \"" + id + "\" xsd file is too big.");
////            return;
////        }
////        else if (result == TransformationsFileManager.Response.XSL_ALREADY_EXISTS) {
////            createErrorMessage(out, MessageType.ALREADY_EXISTS, "Error creating Mapping: id \"" + id + "\" xsd filename already exists.");
////            return;
////        }
////
////        String srcXsdLink = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
////                .getMetadataSchemaManager().getSchemaXSD(srcSchemaId, Double.valueOf(srcSchemaVersion));
////
////        String destXsdLink = ConfigSingleton.getRepoxContextUtil().getRepoxManager()
////                .getMetadataSchemaManager().getSchemaXSD(destSchemaId, Double.valueOf(destSchemaVersion));
////
////        MetadataTransformation mtdTransformation = new MetadataTransformation(id,
////                description, srcSchemaId, destSchemaId, xslFilename + ".xsl",
////                false, Boolean.valueOf(isXslVersion2), destXsdLink, "");
////        mtdTransformation.setSourceSchema(srcXsdLink);
////        mtdTransformation.setMDRCompliant(true);
//
//        //If a file was uploaded, then erase its old files
//        mtdTransformation.setDeleteOldFiles(true);
//        
////        ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().saveMetadataTransformation(mtdTransformation, "");
//        
//        return Response.status(200).entity(new Result("its ok")).build();
//    }
    
}
