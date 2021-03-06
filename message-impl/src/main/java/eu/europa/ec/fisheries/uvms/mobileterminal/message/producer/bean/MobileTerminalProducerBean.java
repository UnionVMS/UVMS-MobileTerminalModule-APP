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

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MobileTerminalProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.*;

@Stateless
public class MobileTerminalProducerBean extends AbstractProducer implements MobileTerminalProducer, ConfigMessageProducer {

    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalProducerBean.class);

    private Queue responseQueue;
    private Queue auditQueue;
    private Queue exchangeQueue;
    private Queue configQueue;

    @PostConstruct
    public void init() {
        responseQueue = JMSUtils.lookupQueue(MessageConstants.COMPONENT_RESPONSE_QUEUE);
        auditQueue = JMSUtils.lookupQueue(MessageConstants.AUDIT_MODULE_QUEUE);
        exchangeQueue = JMSUtils.lookupQueue(MessageConstants.EXCHANGE_MODULE_QUEUE);
        configQueue = JMSUtils.lookupQueue(ConfigConstants.CONFIG_MESSAGE_IN_QUEUE);
    }

    /**
     * TODO : What is this doing??
     *
     * @param text
     * @param queue
     * @return
     * @throws MobileTerminalMessageException
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendDataSourceMessage(String text, DataSourceQueue queue) throws MobileTerminalMessageException {
        ConnectionFactory connectionFactory = JMSUtils.lookupConnectionFactory();
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);
            switch (queue) {
                case INTEGRATION:
                    break;
            }
            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending data source message. ] {}", e.getMessage());
            throw new MobileTerminalMessageException(e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(String text, ModuleQueue queue) throws MobileTerminalMessageException {
        try {
            return sendMessageToSpecificQueue(text, getQueueFromModuleQueue(queue), responseQueue, 0);
        } catch (Exception e) {
            LOG.error("[ Error when sending data source message. ] {}", e.getMessage());
            throw new MobileTerminalMessageException(e.getMessage());
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendResponseToRequestor(final TextMessage message, final String text) throws MessageException {
        sendResponseMessageToSender(message, text);
    }

    @Override
    public String sendConfigMessage(String text) throws ConfigMessageException {
        try {
            return sendModuleMessage(text, ModuleQueue.CONFIG);
        } catch (MobileTerminalMessageException e) {
            LOG.error("[ Error when sending config message. ] {}", e.getMessage());
            throw new ConfigMessageException(e.getMessage());
        }
    }

    private Queue getQueueFromModuleQueue(ModuleQueue queue) {
        Queue queueToReturn = null;
        switch (queue) {
            case AUDIT:
                queueToReturn = auditQueue;
                break;
            case EXCHANGE:
                queueToReturn = exchangeQueue;
                break;
            case CONFIG:
                queueToReturn = configQueue;
                break;
        }
        return queueToReturn;
    }

    @Override
    public String getDestinationName() {
        return eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants.QUEUE_MOBILE_TERMINAL_EVENT;
    }
}
