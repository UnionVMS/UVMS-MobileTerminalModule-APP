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
package eu.europa.ec.fisheries.uvms.mobileterminal.service;

import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

/**
 **/
@Local
public interface ConfigService {


    /**
     * Get all defined terminal system transponders
     *
     * @return
     * @throws MobileTerminalServiceException
     */
    public List<TerminalSystemType> getTerminalSystems() throws MobileTerminalException;
	
	/**
	 * Get channel names
	 * @return
	 */
	public List<String> getChannelNames() throws MobileTerminalException;
	
	/**
	 * Get configuration
	 * 
	 * @return
	 * @throws MobileTerminalException
	 */
	public List<ConfigList> getConfig() throws MobileTerminalException;
	
	/**
	 * 
	 * @param pluginList
	 * @return
	 * @throws MobileTerminalException
	 */
	public List<Plugin> upsertPlugins(List<PluginService> pluginList, String username) throws MobileTerminalException;
	
	/**
	 * Get plugins (from exchange) matching MobileTerminal plugins
	 * @return
	 * @throws MobileTerminalException
	 */
	public List<ServiceResponseType> getRegisteredMobileTerminalPlugins();
}