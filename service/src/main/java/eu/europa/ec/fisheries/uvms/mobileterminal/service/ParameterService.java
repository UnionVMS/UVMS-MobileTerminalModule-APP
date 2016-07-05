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

import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.ParameterKey;
import javax.ejb.Local;

@Local
public interface ParameterService {

    /**
     * Gets the String value of the parameter if it is a String
     * @param key
     * @return
     * @throws MobileTerminalModelException 
     */
    public String getStringValue(ParameterKey key) throws MobileTerminalModelException;

    /**
     * Gets the Boolean value of the parameter if it is a String in database and conforms to true/false
     * 
     * @param key
     * @return
     * @throws MobileTerminalModelException 
     */
    public Boolean getBooleanValue(ParameterKey key) throws MobileTerminalModelException;

}