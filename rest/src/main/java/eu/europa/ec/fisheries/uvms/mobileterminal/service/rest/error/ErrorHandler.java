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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.rest.error;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalFault;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.model.exception.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.service.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.service.exception.MobileTerminalServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.service.exception.MobileTerminalServiceMapperException;

public class ErrorHandler {
    
    public static ResponseDto getFault(Exception ex) {
    	if(ex instanceof MobileTerminalServiceException) {
    		if(ex instanceof InputArgumentException) {
    			return new ResponseDto<>(ex.getMessage(), ResponseCode.INPUT_ERROR);
            }
    		
            if(ex instanceof MobileTerminalServiceMapperException) {
            	return new ResponseDto<>(ex.getMessage(), ResponseCode.MAPPING_ERROR);
            }
            return new ResponseDto<>(ex.getMessage(), ResponseCode.SERVICE_ERROR);
    	}
    	
    	if(ex instanceof MobileTerminalModelException) {
    		if(ex instanceof MobileTerminalModelValidationException) {
    			return new ResponseDto<>(ex.getMessage(), ResponseCode.INPUT_ERROR);
    		}
    		
    		if(ex instanceof MobileTerminalModelMapperException) {
    			//MobileTerminalValidationException
        		//MobileTerminalUnmarshallException
    			return new ResponseDto<>(ex.getMessage(), ResponseCode.MAPPING_ERROR);
    		}
    		
    		if(ex instanceof MobileTerminalFaultException) {
        		return extractFault((MobileTerminalFaultException)ex);
        	}
    		
    		return new ResponseDto<>(ex.getMessage(), ResponseCode.MODEL_ERROR);
    	}

    	if(ex instanceof MobileTerminalException) {
    		return new ResponseDto<>(ex.getMessage(), ResponseCode.MOBILE_TERMINAL_ERROR);
    	}
        return new ResponseDto<>(ex.getMessage(), ResponseCode.UNDEFINED_ERROR);
    }

    private static ResponseDto extractFault(MobileTerminalFaultException ex) {
        MobileTerminalFault fault = ex.getMobileTerminalFault();
        if (fault == null) {
            return new ResponseDto<>(ex.getMessage(), ResponseCode.DOMAIN_ERROR);
        }

        MobileTerminalType terminal = fault.getTerminal();
        if (terminal == null) {
            return new ResponseDto<>(fault.getMessage(), fault.getCode());
        }
        return new ResponseDto<>(terminal, fault.getCode());
    }
}
