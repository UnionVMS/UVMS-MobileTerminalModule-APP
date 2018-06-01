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

import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.error.ResponseCode;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/mobileterminal")
@Stateless
@Consumes(value = { MediaType.APPLICATION_JSON })
@Produces(value = { MediaType.APPLICATION_JSON })
public class MobileTerminalResource {

    private final static Logger LOG = LoggerFactory.getLogger(ResponseCode.class);

    @EJB
    private MobileTerminalServiceBean mobileTerminalService;

    @Context
    private HttpServletRequest request;

    /**
     *
     * @responseMessage 200 Mobile Terminal successfully created
     * @responseMessage 500 Error when creating mobile Terminal
     *
     * @summary Creates a mobile terminal
     *
     */
    @POST
    @Path("/")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response createMobileTerminal(MobileTerminalType type) {
        LOG.info("Create mobile terminal invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = mobileTerminalService.createMobileTerminal(type, MobileTerminalSource.INTERNAL, request.getRemoteUser());
            return Response.ok(mobileTerminalType).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when creating mobile terminal ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }

    /**
     *
     * @responseMessage 200 Mobile Terminal successfully retrieved
     * @responseMessage 500 Error when retrieveing Mobile Terminal by id
     *
     * @summary Gets a mobile terminal by its id
     *
     */
    @GET
    @Path("/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalById(@PathParam("id") String id) {
        LOG.info("Get mobile terminal by id invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = mobileTerminalService.getMobileTerminalById(id);
            return Response.ok(mobileTerminalType).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when creating mobile terminal ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }

    /**
     *
     * @responseMessage 200 Mobile Terminal successfully updated
     * @responseMessage 500 Error when updating Mobile Terminal
     *
     * @summary Updates a mobile terminal with new values
     *
     */
    @PUT
    @Path("/")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response updateMobileTerminal(@QueryParam("comment") String comment, MobileTerminalType type) {
        LOG.info("Update mobile terminal by id invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = mobileTerminalService.updateMobileTerminal(type, comment, MobileTerminalSource.INTERNAL, request.getRemoteUser());
            return Response.ok(mobileTerminalType).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when updating mobile terminal ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }

    /**
     *
     * @responseMessage 200 Mobile Terminal list successfully retrieved
     * @responseMessage 500 Error when getting mobile terminal list
     *
     * @summary Gets a list fo mobile terminals based on a query
     *
     */
    @POST
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalList(MobileTerminalListQuery query) {
        LOG.info("Get mobile terminal list invoked in rest layer.");
        try {
            MobileTerminalListResponse response = mobileTerminalService.getMobileTerminalList(query);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting mobile terminal list ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }

    /**
     *
     * @responseMessage 200 Mobile Terminal list successfully assigned to a
     *                  carrier
     * @responseMessage 500 Error when assigning the mobile terminal to a
     *                  carrier
     *
     * @summary Assigns a mobile terminal to a carrier
     *
     */
    @POST
    @Path("/assign")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response assignMobileTerminal(@QueryParam("comment") String comment, MobileTerminalAssignQuery query) {
        LOG.info("Assign mobile terminal invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = mobileTerminalService.assignMobileTerminal(query, comment, request.getRemoteUser());
            return Response.ok(mobileTerminalType).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when assigning mobile terminal ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }

    /**
     *
     * @responseMessage 200 Mobile successfully unassigned from the carrier
     * @responseMessage 500 Error when unassigning the mobile terminal from the
     *                  carrier
     *
     * @summary Unassigns a mobile terminal from a carrier
     *
     */
    @POST
    @Path("/unassign")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response unAssignMobileTerminal(@QueryParam("comment") String comment, MobileTerminalAssignQuery query) {
        LOG.info("Unassign mobile terminal invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = mobileTerminalService.unAssignMobileTerminal(query, comment, request.getRemoteUser());
            return Response.ok(mobileTerminalType).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when unassigning mobile terminal ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }

    /**
     *
     * @responseMessage 200 Mobile successfully activated
     * @responseMessage 500 Error when activating the mobile terminal
     *
     * @summary Activates a mobile terminal
     *
     */
    @PUT
    @Path("/status/activate")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response setStatusActive(@QueryParam("comment") String comment, MobileTerminalId terminalId) {
        LOG.info("Set mobile terminal status active invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = mobileTerminalService.setStatusMobileTerminal(terminalId, comment, MobileTerminalStatus.ACTIVE, request.getRemoteUser());
            return Response.ok(mobileTerminalType).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when activating mobile terminal ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }

    /**
     *
     * @responseMessage 200 Mobile successfully inactivated
     * @responseMessage 500 Error when inactivating the mobile terminal
     *
     * @summary Inactivates a mobile terminal
     *
     */
    @PUT
    @Path("/status/inactivate")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response setStatusInactive(@QueryParam("comment") String comment, MobileTerminalId terminalId) {
        LOG.info("Set mobile terminal status inactive invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = mobileTerminalService.setStatusMobileTerminal(terminalId, comment, MobileTerminalStatus.INACTIVE, request.getRemoteUser());
            return Response.ok(mobileTerminalType).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when inactivating mobile terminal ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }

    /**
     *
     * @responseMessage 200 Mobile successfully removed
     * @responseMessage 500 Error when removing the mobile terminal
     *
     * @summary Removes a mobile terminal. It is archived but not readable
     *
     */
    @PUT
    @Path("/status/remove")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public Response setStatusRemoved(@QueryParam("comment") String comment, MobileTerminalId terminalId) {
        LOG.info("Set mobile terminal status removed invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = mobileTerminalService.setStatusMobileTerminal(terminalId, comment, MobileTerminalStatus.ARCHIVE, request.getRemoteUser());
            return Response.ok(mobileTerminalType).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when removing mobile terminal ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }

    /**
     *
     * @responseMessage 200 Mobile terminal history successfully retrieved
     * @responseMessage 500 Error when retrieving the mobile terminals history
     *
     * @summary Gets all historical events connected to the specified mobiel
     *          terminal
     *
     */
    @GET
    @Path("/history/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalHistoryListByMobileTerminalId(@PathParam("id") String guid) {
        LOG.info("Get mobile terminal history by mobile terminal id invoked in rest layer.");
        try {
            MobileTerminalHistory mobileTerminalHistory = mobileTerminalService.getMobileTerminalHistoryList(guid);
            return Response.ok(mobileTerminalHistory).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting mobile terminal history by terminalId ] {}", ex.getStackTrace());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .header("MDC", MDC.get("requestId"))
                    .build();
        }
    }
}
