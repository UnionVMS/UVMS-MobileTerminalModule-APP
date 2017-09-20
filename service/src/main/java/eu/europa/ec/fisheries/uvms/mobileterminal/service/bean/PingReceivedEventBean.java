package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;


import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.*;

@Stateless
@LocalBean
public class PingReceivedEventBean {

    final static Logger LOG = LoggerFactory.getLogger(PingReceivedEventBean.class);

    @Resource(lookup = MessageConstants.JAVA_MESSAGE_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    public void ping(final EventMessage message) {
        try {
            final Connection connection = connectionFactory.createConnection();
            try {
                //TODO: Transacted false??
                final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final String pingResponse = MobileTerminalModuleResponseMapper.createPingResponse("pong");
                final TextMessage pingResponseMessage = session.createTextMessage(pingResponse);
                pingResponseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                pingResponseMessage.setJMSDestination(message.getJmsMessage().getJMSReplyTo());
                getProducer(session, pingResponseMessage.getJMSDestination()).send(pingResponseMessage);
            } finally {
                connection.close();
            }
        } catch (MobileTerminalModelMapperException | JMSException e) {
            LOG.error("Ping message went wrong", e);
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to ping MobileTerminal: " + e.getMessage()));
            // Propagate error
            throw new EJBException(e);
        }
    }

    // TODO: This needs to be fixed, NON_PERSISTENT and timetolive is not ok.
    private javax.jms.MessageProducer getProducer(final Session session, final Destination destination) throws JMSException {
        final javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(60000L);
        return producer;
    }
}
