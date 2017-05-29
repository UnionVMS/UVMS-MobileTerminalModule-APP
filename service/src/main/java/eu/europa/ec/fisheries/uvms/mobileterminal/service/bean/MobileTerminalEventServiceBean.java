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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalFault;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.EventService;

@Stateless
public class MobileTerminalEventServiceBean implements EventService {

    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalEventServiceBean.class);

    @Resource(lookup = MessageConstants.JAVA_MESSAGE_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    @Override
    public void returnError(@Observes @ErrorEvent EventMessage message) {
        try {
            Connection connection = connectionFactory.createConnection();
            try {
                //TODO: Transacted false??
                LOG.debug("Sending error message back from Mobile Terminal module to recipient om JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                MobileTerminalFault request = new MobileTerminalFault();
                request.setMessage(message.getErrorMessage());
                String data = JAXBMarshaller.marshallJaxBObjectToString(request);

                TextMessage response = session.createTextMessage(data);
                response.setJMSCorrelationID(message.getJmsMessage().getJMSCorrelationID());
                getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(response);
            } finally {
                connection.close();
            }

        } catch (MobileTerminalModelMapperException | JMSException ex) {
            LOG.error("Error when returning Error message to recipient", ex.getMessage());
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