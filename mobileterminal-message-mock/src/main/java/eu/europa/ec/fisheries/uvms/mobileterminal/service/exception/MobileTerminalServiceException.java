package eu.europa.ec.fisheries.uvms.mobileterminal.service.exception;

import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;

/**
 **/
public class MobileTerminalServiceException extends MobileTerminalException {
    private static final long serialVersionUID = 1L;

    public MobileTerminalServiceException(String message) {
        super(message);
    }

}