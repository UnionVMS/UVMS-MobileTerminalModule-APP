package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;


import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MobileTerminalProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.TextMessage;

@Stateless
@LocalBean
public class PingReceivedEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(PingReceivedEventBean.class);

    @EJB
    private MobileTerminalProducer messageProducer;

    @Inject
    @ErrorEvent
    private Event<EventMessage> errorEvent;

    public void ping(EventMessage message) {
        TextMessage receivedJmsMessage = message.getJmsMessage();
        try {
            String pingResponse = MobileTerminalModuleResponseMapper.createPingResponse("pong");
            messageProducer.sendResponseToRequestor(receivedJmsMessage, pingResponse);
        } catch (MobileTerminalModelMapperException | MessageException e) {
            LOG.error("Ping message went wrong", e);
            errorEvent.fire(new EventMessage(receivedJmsMessage, "Exception when trying to ping MobileTerminal: " + e.getMessage()));
            // Propagate error
            throw new EJBException(e);
        }
    }
}
