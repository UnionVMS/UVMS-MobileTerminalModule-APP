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
package eu.europa.ec.fisheries.uvms.mobileterminal.message.consumer.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalModuleBaseRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalModuleMethod;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalUnmarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.GetReceivedEventBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.ListReceivedEventBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.PingReceivedEventBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
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
public class MobileTerminalMessageConsumerBean implements MessageListener {

    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalMessageConsumerBean.class);

    @EJB
    private GetReceivedEventBean getReceivedEventBean;

    @EJB
    private ListReceivedEventBean listReceivedEventBean;

    @EJB
    private PingReceivedEventBean pingReceivedEventBean;

    @Inject
    @ErrorEvent
    private Event<EventMessage> errorEvent;

    @Override
    public void onMessage(Message message) {
        //handleMobileTerminalMessages(message);
    }

    public void handleMobileTerminalMessages(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            MobileTerminalModuleBaseRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, MobileTerminalModuleBaseRequest.class);
            MobileTerminalModuleMethod mobTerminalMethod = request.getMethod();
            LOG.info("Message received in mobileterminal with method : [ {} ]", mobTerminalMethod);
            switch (mobTerminalMethod) {
                case GET_MOBILE_TERMINAL:
                    getReceivedEventBean.get(new EventMessage(textMessage));
                    break;
                case LIST_MOBILE_TERMINALS:
                    listReceivedEventBean.list(new EventMessage(textMessage));
                    break;
                case BATCH_LIST_MOBILE_TERMINALS:
                    listReceivedEventBean.listBatch(new EventMessage(textMessage));
                    break;
                case PING:
                    pingReceivedEventBean.ping(new EventMessage(textMessage));
                    break;
                default:
                    LOG.error("[ Unsupported request: {} ]", mobTerminalMethod);
                    break;
            }
        } catch (NullPointerException | MobileTerminalUnmarshallException e) {
            LOG.error("[ Error when receiving message in mobileterminal. ] {}", e.getMessage());
            errorEvent.fire(new EventMessage(textMessage, "Error when receivning message in mobileterminal: " + e.getMessage()));
        }
    }
}
