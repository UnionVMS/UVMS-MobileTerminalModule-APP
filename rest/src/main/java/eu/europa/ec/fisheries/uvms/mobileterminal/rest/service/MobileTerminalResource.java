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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAssignQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalStatus;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.error.ResponseCode;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

/**
 **/
@Path("/mobileterminal")
@Stateless
public class MobileTerminalResource {

    final static Logger LOG = LoggerFactory.getLogger(ResponseCode.class);

    @EJB
    MobileTerminalService mobileTerminalService;

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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public ResponseDto createMobileTerminal(final MobileTerminalType mobileterminal) {
        LOG.info("Create mobile terminal invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.createMobileTerminal(mobileterminal, MobileTerminalSource.INTERNAL, request.getRemoteUser()), ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when creating mobile terminal ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto getMobileTerminalById(@PathParam("id") final String mobileterminalId) {
        LOG.info("Get mobile terminal by id invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.getMobileTerminalById(mobileterminalId), ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when creating mobile terminal ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public ResponseDto updateMobileTerminal(@QueryParam("comment") final String comment, final MobileTerminalType mobileterminal) {
        LOG.info("Update mobile terminal by id invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.updateMobileTerminal(mobileterminal, comment, MobileTerminalSource.INTERNAL, request.getRemoteUser()),
                    ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when updating mobile terminal ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto getMobileTerminalList(final MobileTerminalListQuery query) {
        LOG.info("Get mobile terminal list invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.getMobileTerminalList(query), ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when getting mobile terminal list ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/assign")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public ResponseDto assignMobileTerminal(@QueryParam("comment") final String comment, final MobileTerminalAssignQuery query) {
        LOG.info("Assign mobile terminal invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.assignMobileTerminal(query, comment, request.getRemoteUser()), ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when assigning mobile terminal ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/unassign")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public ResponseDto unAssignMobileTerminal(@QueryParam("comment") final String comment, final MobileTerminalAssignQuery query) {
        LOG.info("Unassign mobile terminal invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.unAssignMobileTerminal(query, comment, request.getRemoteUser()), ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when unassigning mobile terminal ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/status/activate")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public ResponseDto setStatusActive(@QueryParam("comment") final String comment, final MobileTerminalId terminalId) {
        LOG.info("Set mobile terminal status active invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.setStatusMobileTerminal(terminalId, comment, MobileTerminalStatus.ACTIVE, request.getRemoteUser()), ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when activating mobile terminal ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/status/inactivate")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public ResponseDto setStatusInactive(@QueryParam("comment") final String comment, final MobileTerminalId terminalId) {
        LOG.info("Set mobile terminal status inactive invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.setStatusMobileTerminal(terminalId, comment, MobileTerminalStatus.INACTIVE, request.getRemoteUser()), ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when inactivating mobile terminal ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/status/remove")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public ResponseDto setStatusRemoved(@QueryParam("comment") final String comment, final MobileTerminalId terminalId) {
        LOG.info("Set mobile terminal status removed invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.setStatusMobileTerminal(terminalId, comment, MobileTerminalStatus.ARCHIVE, request.getRemoteUser()), ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when removing mobile terminal ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
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
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/history/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto getMobileTerminalHistoryListByMobileTerminalId(@PathParam("id") final String guid) {
        LOG.info("Get mobile terminal history by mobile terminal id invoked in rest layer.");
        try {
            return new ResponseDto(mobileTerminalService.getMobileTerminalHistoryList(guid), ResponseCode.OK);
        } catch (final Exception ex) {
            LOG.error("[ Error when getting mobile terminal history by terminalId ] {}", ex.getStackTrace());
            return ErrorHandler.getFault(ex);
        }
    }


}