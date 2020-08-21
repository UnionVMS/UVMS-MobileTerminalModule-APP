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

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalFault;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MobileTerminalProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

@Stateless
public class MobileTerminalEventServiceBean implements EventService {

    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalEventServiceBean.class);

    @EJB
    private MobileTerminalProducer messageProducer;

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    @Override
    public void returnError(@Observes @ErrorEvent EventMessage message) {
        try {
            TextMessage receivedJmsMessage = message.getJmsMessage();
            LOG.debug("Sending error message back from Mobile Terminal module to recipient om JMS Queue with correlationID: {} ",
                    receivedJmsMessage.getJMSMessageID());
            MobileTerminalFault request = new MobileTerminalFault();
            request.setMessage(message.getErrorMessage());
            String data = JAXBMarshaller.marshallJaxBObjectToString(request);
            messageProducer.sendResponseToRequestor(receivedJmsMessage, data);
        } catch (MobileTerminalModelMapperException | JMSException | MessageException ex) {
            LOG.error("Error when returning Error message to recipient", ex);
        }
    }
}
