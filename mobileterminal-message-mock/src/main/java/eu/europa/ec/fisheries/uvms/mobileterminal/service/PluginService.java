package eu.europa.ec.fisheries.uvms.mobileterminal.service;

import javax.ejb.Local;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

@Local
public interface PluginService {

    public AcknowledgeTypeType sendPoll(PollResponseType poll, String username) throws MobileTerminalServiceException;

    public void processUpdatedDNIDList(String pluginName);
}