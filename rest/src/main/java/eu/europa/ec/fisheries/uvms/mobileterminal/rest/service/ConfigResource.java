/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.mobileterminal.rest.service;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.ConfigServiceBean;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @apiDescription Handles all Polls
 */
@Path("/config")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class ConfigResource {

    private final static Logger LOG = LoggerFactory.getLogger(ConfigResource.class);

    @EJB
    private ConfigServiceBean configService;
    
    /**
    *
    * @responseMessage 200 Config transponders successfully retreived
    * @responseMessage 500 Error when retrieveing the config values for
    * transponders
    *
    * @summary Get all available config values for Mobile Terminals
    *
    */
   @GET
   @Path("/transponders")
	public Response getConfigTransponders() {
       try {
           LOG.info("Get config transponders invoked in rest layer.");
           List<TerminalSystemType> list = configService.getTerminalSystems();
           return Response.ok(list).header("MDC", MDC.get("requestId")).build();
       } catch (Exception ex) {
           LOG.error("[ Error when getting configTransponders ] {}", ex.getStackTrace());
           return Response
                   .status(Response.Status.BAD_REQUEST)
                   .entity(ex.getMessage())
                   .header("MDC", MDC.get("requestId"))
                   .build();
       }
   }

   /**
    *
    * @responseMessage 200 Config search fields successfully retreived
    * @responseMessage 500 Error when retrieveing the config search fields for
    * transponders
    *
    * @summary Gets all available search fields for Mobile Terminal
    *
    */
   @GET
   @Path("/searchfields")
   public Response getConfigSearchFields() {
       LOG.info("Get config search fields invoked in rest layer.");
       try {
           return Response.ok(SearchKey.values()).header("MDC", MDC.get("requestId")).build();
       } catch (Exception ex) {
           LOG.error("[ Error when getting config search fields ] {}", ex.getStackTrace());
           return Response
                   .status(Response.Status.BAD_REQUEST)
                   .entity(ex.getMessage())
                   .header("MDC", MDC.get("requestId"))
                   .build();
       }
   }

    @GET
    @Path("/")
    public Response getConfiguration() {
        try {
        	List<ConfigList> config = configService.getConfig();
        	return Response.ok(config).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }
}
