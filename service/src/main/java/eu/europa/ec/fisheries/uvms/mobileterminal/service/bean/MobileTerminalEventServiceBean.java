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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalListRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.GetMobileTerminalRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalModuleBaseRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalModuleMethod;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalFault;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalUnmarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.EventService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

import java.util.List;

@Stateless
public class MobileTerminalEventServiceBean implements EventService {

    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalEventServiceBean.class);

    @EJB
    MobileTerminalService service;

    @Resource(lookup = MessageConstants.CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    private Connection connection = null;
    private Session session = null;

    @EJB
    ParameterService parameters;

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void get(@Observes @GetReceivedEvent EventMessage message) {
        TextMessage requestMessage = message.getJmsMessage();
        try {
            MobileTerminalType mobileTerminal = getMobileTerminal(message);

            connectToQueue();

            String response = MobileTerminalModuleRequestMapper.createMobileTerminalResponse(mobileTerminal);
            TextMessage responseMessage = session.createTextMessage(response);
            responseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
            getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(responseMessage);
        } catch (MobileTerminalModelMapperException | JMSException e) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to get a MobileTerminal: " + e.getMessage()));
        } finally {
            disconnectQueue();
        }
    }

    @EJB
    MobileTerminalService mobileTerminalService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void list(@Observes @ListReceivedEvent EventMessage message) {
        LOG.info("List Mobile terminals");
        TextMessage requestMessage = message.getJmsMessage();

        try {
            MobileTerminalModuleBaseRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(requestMessage, MobileTerminalModuleBaseRequest.class);
            if (baseRequest.getMethod() == MobileTerminalModuleMethod.LIST_MOBILE_TERMINALS) {
                MobileTerminalListRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), MobileTerminalListRequest.class);

                MobileTerminalListResponse mobileTerminalListResponse = mobileTerminalService.getMobileTerminalList(request.getQuery());
                List<MobileTerminalType> mobileTerminalTypes = mobileTerminalListResponse.getMobileTerminal();

                connectToQueue();

                String response = MobileTerminalModuleRequestMapper.mapGetMobileTerminalList(mobileTerminalTypes);
                TextMessage responseMessage = session.createTextMessage(response);
                responseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(responseMessage);
            }
        } catch (MobileTerminalException | JMSException e) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to get list in MobileTerminal: " + e.getMessage()));
        } finally {
            disconnectQueue();
        }

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void ping(@Observes @PingReceivedEvent EventMessage message) {
        TextMessage requestMessage = message.getJmsMessage();

        try {
            MobileTerminalModuleBaseRequest baseRequest = JAXBMarshaller.unmarshallTextMessage(requestMessage, MobileTerminalModuleBaseRequest.class);
            if (baseRequest.getMethod() == MobileTerminalModuleMethod.PING) {
                connectToQueue();

                String pingResponse = MobileTerminalModuleResponseMapper.createPingResponse("pong");
                TextMessage pingResponseMessage = session.createTextMessage(pingResponse);
                pingResponseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                pingResponseMessage.setJMSDestination(message.getJmsMessage().getJMSReplyTo());
                getProducer(session, pingResponseMessage.getJMSDestination()).send(pingResponseMessage);
                return;
            }
        } catch (MobileTerminalModelMapperException | MobileTerminalUnmarshallException | JMSException e) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to ping MobileTerminal: " + e.getMessage()));
        } finally {
            disconnectQueue();
        }
    }

    private MobileTerminalType getMobileTerminal(EventMessage message) {
        GetMobileTerminalRequest request = null;
        MobileTerminalType mobTerm = null;
        DataSourceQueue dataSource = null;

        try {
            request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
        } catch (MobileTerminalUnmarshallException ex) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Error when mapping message: " + ex.getMessage()));
        }

        try {
            dataSource = decideDataflow();
        } catch (Exception ex) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when deciding Dataflow for : " + dataSource.name() + " Error message: " + ex.getMessage()));
        }

        try {
            LOG.debug("Got message to MobileTerminalModule, Executing Get MobileTerminal from datasource {}", dataSource.name());
            mobTerm = service.getMobileTerminalById(request.getId(), dataSource);
            if (!dataSource.equals(DataSourceQueue.INTERNAL)) {
                service.upsertMobileTerminal(mobTerm, MobileTerminalSource.NATIONAL, dataSource.name());
            }
        } catch (MobileTerminalException ex) {
            mobTerm = null;
        }

        if (mobTerm == null) {
            LOG.debug("Trying to retrieve MobileTerminal from datasource: {0} as second option", DataSourceQueue.INTERNAL.name());
            try {
                request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
                mobTerm = service.getMobileTerminalById(request.getId(), DataSourceQueue.INTERNAL);
            } catch (MobileTerminalException ex) {
                errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when getting vessel from source : " + dataSource.name() + " Error message: " + ex.getMessage()));
            }
        }

        return mobTerm;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void returnError(@Observes @ErrorEvent EventMessage message) {
        try {
        	connectToQueue();
            LOG.debug("Sending error message back from Mobile Terminal module to recipient om JMS Queue with correlationID: {} ", message.getJmsMessage().getJMSMessageID());

            MobileTerminalFault request = new MobileTerminalFault();
            request.setMessage(message.getErrorMessage());

            String data = JAXBMarshaller.marshallJaxBObjectToString(request);

            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSCorrelationID());
            getProducer(session, message.getJmsMessage().getJMSReplyTo()).send(response);

        } catch (MobileTerminalModelMapperException | JMSException ex) {
            LOG.error("Error when returning Error message to recipient", ex.getMessage());
        } finally {
            disconnectQueue();
        }
    }

    private DataSourceQueue decideDataflow() throws MobileTerminalServiceException {
        try {

            Boolean national = parameters.getBooleanValue(ParameterKey.USE_NATIONAL.getKey());

            LOG.debug("Settings for dataflow are: NATIONAL: {}", national.toString());

            if (national) {
                return DataSourceQueue.INTEGRATION;
            }

            return DataSourceQueue.INTERNAL;

        } catch (ConfigServiceException ex) {
        	LOG.error("[ Error when deciding data flow. ] {}", ex.getMessage());
            throw new MobileTerminalServiceException(ex.getMessage());
        }

    }

    private void connectToQueue() throws JMSException {
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
    }

    private void disconnectQueue() {
        try {
            if (connection != null) {
                connection.stop();
                connection.close();
            }
        } catch (JMSException e) {
            // do nothing
        }
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(60000L);
        return producer;
    }

}