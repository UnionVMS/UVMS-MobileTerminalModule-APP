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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.message.consumer.bean;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.message.event.carrier.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(mappedName = MessageConstants.COMPONENT_EVENT_QUEUE, activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = MessageConstants.COMPONENT_EVENT_QUEUE_NAME),
        @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = MessageConstants.COMPONENT_EVENT_QUEUE),
        @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = MessageConstants.CONNECTION_FACTORY)
})
public class MessageConsumerBeanMock implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(MessageConsumerBeanMock.class);



    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;
        try {
            String txt = textMessage.getText();
            LOG.info(txt);
        } catch (JMSException e) {
            LOG.error("onMessage: ", e);
        }
    }

}