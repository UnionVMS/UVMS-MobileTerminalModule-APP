package eu.europa.ec.fisheries.uvms.mobileterminal.service.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.model.exception.MobileTerminalModelException;

public class MobileTerminalExistsException extends MobileTerminalModelException {

    private static final long serialVersionUID = 1L;

    private MobileTerminalType mobileTerminal;

    public MobileTerminalExistsException(String message, MobileTerminalType mobileTerminal) {
        super(message);
        this.mobileTerminal = mobileTerminal;
    }

    /**
     * A skeleton of the mobile terminal that already exists.
     */
    public MobileTerminalType getMobileTerminal() {
        return this.mobileTerminal;
    }
}
