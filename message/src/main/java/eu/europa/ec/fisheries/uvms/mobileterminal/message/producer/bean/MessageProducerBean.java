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
package eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.bean;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.jms.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.config.constants.ConfigConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;

@Stateless
public class MessageProducerBean implements MessageProducer, ConfigMessageProducer {

    @Resource(lookup = MessageConstants.QUEUE_DATASOURCE_INTEGRATION)
    private Queue integrationQueue;

    @Resource(lookup = MessageConstants.COMPONENT_RESPONSE_QUEUE)
    private Queue responseQueue;

    @Resource(lookup = MessageConstants.AUDIT_MODULE_QUEUE)
    private Queue auditQueue;

    @Resource(lookup = MessageConstants.EXCHANGE_MODULE_QUEUE)
    private Queue exchangeQueue;

    @Resource(lookup = ConfigConstants.CONFIG_MESSAGE_IN_QUEUE)
    private Queue configQueue;

    final static Logger LOG = LoggerFactory.getLogger(MessageProducerBean.class);

    private static final int CONFIG_TTL = 30000;

    @EJB
    JMSConnectorBean connector;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendDataSourceMessage(String text, DataSourceQueue queue) throws MobileTerminalMessageException {
        try {
            Session session = connector.getNewSession();

            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);

            switch (queue) {
            case INTEGRATION:
                getProducer(session, integrationQueue).send(message);
                break;
            }

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending data source message. ] {}", e.getMessage());
            throw new MobileTerminalMessageException(e.getMessage());
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(String text, ModuleQueue queue) throws MobileTerminalMessageException {
        try {
            Session session = connector.getNewSession();

            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);

            switch (queue) {
            case AUDIT:
                getProducer(session, auditQueue).send(message);
                break;
            case EXCHANGE:
                getProducer(session, exchangeQueue).send(message);
                break;
            case CONFIG:
                getProducer(session, configQueue).send(message);
            	break;
            default:
                break;
            }

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending data source message. ] {}", e.getMessage());
            throw new MobileTerminalMessageException(e.getMessage());
        }
    }

	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendConfigMessage(String text) throws ConfigMessageException {
        try {
            return sendModuleMessage(text, ModuleQueue.CONFIG);
        } catch (MobileTerminalMessageException e) {
            LOG.error("[ Error when sending config message. ] {}", e.getMessage());
            throw new ConfigMessageException(e.getMessage());
        }
    }

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(60000L);
        return producer;
    }
}