package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;


import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalModuleBaseRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalModuleMethod;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalUnmarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
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

    public void ping(EventMessage message) {
        TextMessage requestMessage = message.getJmsMessage();

        try {
            MobileTerminalModuleBaseRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(requestMessage, MobileTerminalModuleBaseRequest.class);
            if (baseRequest.getMethod() == MobileTerminalModuleMethod.PING) {

                Connection connection = connectionFactory.createConnection();
                try {
                    //TODO: Transacted false??
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    String pingResponse = MobileTerminalModuleResponseMapper.createPingResponse("pong");
                    TextMessage pingResponseMessage = session.createTextMessage(pingResponse);
                    pingResponseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                    pingResponseMessage.setJMSDestination(message.getJmsMessage().getJMSReplyTo());
                    getProducer(session, pingResponseMessage.getJMSDestination()).send(pingResponseMessage);
                } finally {
                    connection.close();
                }
            }
        } catch (MobileTerminalModelMapperException | MobileTerminalUnmarshallException | JMSException e) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to ping MobileTerminal: " + e.getMessage()));
        }
    }

    // TODO: This needs to be fixed, NON_PERSISTENT and timetolive is not ok.
    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(60000L);
        return producer;
    }
}
