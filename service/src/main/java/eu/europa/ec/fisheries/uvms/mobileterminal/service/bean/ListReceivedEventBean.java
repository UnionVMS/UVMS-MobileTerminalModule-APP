package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalListRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.*;
import java.util.List;

@Stateless
@LocalBean
public class ListReceivedEventBean {

    final static Logger LOG = LoggerFactory.getLogger(ListReceivedEventBean.class);

    @EJB
    private MobileTerminalService mobileTerminalService;

    @Resource(lookup = MessageConstants.JAVA_MESSAGE_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    public void list(EventMessage message) {
        LOG.info("List Mobile terminals:{}",message);
        try {
            MobileTerminalListRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), MobileTerminalListRequest.class);

            MobileTerminalListResponse mobileTerminalListResponse = mobileTerminalService.getMobileTerminalList(request.getQuery());
            List<MobileTerminalType> mobileTerminalTypes = mobileTerminalListResponse.getMobileTerminal();

            Connection connection = connectionFactory.createConnection();
            try {
                //TODO: Transacted false??
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                String response = MobileTerminalModuleRequestMapper.mapGetMobileTerminalList(mobileTerminalTypes);
                TextMessage responseMessage = session.createTextMessage(response);
                responseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(responseMessage);
            } finally {
                connection.close();
            }
        } catch (MobileTerminalException | JMSException e) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to get list in MobileTerminal: " + e.getMessage()));
            // Propagate error
            throw new EJBException(e);
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
