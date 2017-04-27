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
package eu.europa.ec.fisheries.uvms.mobileterminal.dao;

import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;

@Local
public interface MobileTerminalPluginDao {

	/**
	 * Get all mobile terminal plugins
	 * 
	 * @return
	 * @throws ConfigDaoException
	 */
	public List<MobileTerminalPlugin> getPluginList() throws ConfigDaoException;
	
	/**
	 * Persist mobile terminal plugin
	 * @param plugin
	 * @return
	 * @throws TerminalDaoException
	 */
	public MobileTerminalPlugin createMobileTerminalPlugin(MobileTerminalPlugin plugin) throws TerminalDaoException;

	/**
	 * 
	 * @param serviceName
	 * @return
	 * @throws NoEntityFoundException
	 */
	public MobileTerminalPlugin getPluginByServiceName(String serviceName) throws NoEntityFoundException;

	/**
	 * Merge and flush
	 * 
	 * @param entity
	 * @return
	 * @throws TerminalDaoException
	 */
	public MobileTerminalPlugin updatePlugin(MobileTerminalPlugin entity) throws TerminalDaoException;
}