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
package eu.europa.ec.fisheries.uvms.mobileterminal.constant;

import eu.europa.ec.fisheries.uvms.mobileterminal.bean.MobileTerminalExistsException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.MobileTerminalDaoMappingException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoMappingException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.exception.ConnectMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.exception.SearchMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;

public class ErrorHandler {

	public static MobileTerminalFaultCode getFaultCode(final MobileTerminalModelException e) {
		if(e instanceof InputArgumentException) {
			return MobileTerminalFaultCode.DOMAIN_INPUT_ERROR;
		}
		if(e instanceof MobileTerminalDaoMappingException) {
			return MobileTerminalFaultCode.DOMAIN_MAPPING_ERROR;
		}
		if(e instanceof PollDaoException) {
			return MobileTerminalFaultCode.DOMAIN_DAO_ERROR;
		}
		if(e instanceof PollDaoMappingException) {
			return MobileTerminalFaultCode.DOMAIN_MAPPING_ERROR;
		}
		if(e instanceof TerminalDaoException) {
			return MobileTerminalFaultCode.DOMAIN_DAO_ERROR;
		}
		if(e instanceof ConfigDaoException) {
			return MobileTerminalFaultCode.DOMAIN_DAO_ERROR;
		}
		if(e instanceof ConnectMapperException) {
			return MobileTerminalFaultCode.DOMAIN_MAPPING_ERROR;
		}
		if(e instanceof SearchMapperException) {
			return MobileTerminalFaultCode.DOMAIN_INPUT_ERROR;
		}
        if (e instanceof MobileTerminalExistsException) {
            return MobileTerminalFaultCode.DOMAIN_TERMINAL_EXISTS_ERROR;
        }
		return MobileTerminalFaultCode.DOMAIN_ERROR;
	}
}