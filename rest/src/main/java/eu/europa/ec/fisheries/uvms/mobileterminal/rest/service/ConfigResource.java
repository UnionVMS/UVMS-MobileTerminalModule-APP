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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.ServiceToPluginMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.dto.MobileTerminalConfig;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.dto.MobileTerminalDeviceConfig;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.error.ResponseCode;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

/**
 * @apiDescription Handles all Polls*/
@Path("/config")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class ConfigResource {

    final static Logger LOG = LoggerFactory.getLogger(ConfigResource.class);

    @EJB
    ConfigService configService;
    
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
   @Consumes(value = {MediaType.APPLICATION_JSON})
   @Produces(value = {MediaType.APPLICATION_JSON})
   @Path("/transponders")
	public ResponseDto<List<MobileTerminalDeviceConfig>> getConfigTransponders() {
       try {
           LOG.info("Get config transponders invoked in rest layer.");
           List<TerminalSystemType> list = configService.getTerminalSystems();
           return new ResponseDto(MobileTerminalConfig.mapConfigTransponders(list), ResponseCode.OK);
       } catch (Exception ex) {
           LOG.error("[ Error when getting configTransponders ] {}", ex.getStackTrace());
           return ErrorHandler.getFault(ex);
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
   @Consumes(value = {MediaType.APPLICATION_JSON})
   @Produces(value = {MediaType.APPLICATION_JSON})
   @Path("/searchfields")
   public ResponseDto<List<String>> getConfigSearchFields() {
       LOG.info("Get config search fields invoked in rest layer.");
       try {
           return new ResponseDto(SearchKey.values(), ResponseCode.OK);
       } catch (Exception ex) {
           LOG.error("[ Error when getting config search fields ] {}", ex.getStackTrace());
           return ErrorHandler.getFault(ex);
       }
   }
    
    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/")
    public ResponseDto getConfiguration() {
        try {
        	List<ConfigList> config = configService.getConfig();
            return new ResponseDto(MobileTerminalConfig.mapConfigList(config), ResponseCode.OK);
        } catch (Exception ex) {
            return ErrorHandler.getFault(ex);
        }
    }

    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/updatePlugins")
    public ResponseDto updatePlugins() {
        try {
            try {
                List<ServiceResponseType> serviceTypes = configService.getRegisteredMobileTerminalPlugins();
                LOG.debug("get services from exchange registry");
                List<Plugin> plugins = new ArrayList<>();
                if(serviceTypes != null) {
                    plugins = configService.upsertPlugins(ServiceToPluginMapper.mapToPluginList(serviceTypes), "PluginTimerBean");
                    LOG.debug("upserted plugins");
                }
                return new ResponseDto(plugins, ResponseCode.OK);
            } catch (MobileTerminalException e) {
                return ErrorHandler.getFault(e);
            }
        } catch (Exception ex) {
            return ErrorHandler.getFault(ex);
        }
    }
}