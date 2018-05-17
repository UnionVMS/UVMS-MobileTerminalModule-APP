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

import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.jms.*;

@Stateless
public class MessageProducerBean implements MessageProducer, ConfigMessageProducer {

	private Queue responseQueue;
	private Queue auditQueue;
	private Queue exchangeQueue;
	private Queue configQueue;
	private ConnectionFactory connectionFactory;

	private final static Logger LOG = LoggerFactory.getLogger(MessageProducerBean.class);

	@PostConstruct
	public void init() {
    	connectionFactory = JMSUtils.lookupConnectionFactory();
		responseQueue = JMSUtils.lookupQueue(MessageConstants.COMPONENT_RESPONSE_QUEUE);
		auditQueue = JMSUtils.lookupQueue(MessageConstants.AUDIT_MODULE_QUEUE);
		exchangeQueue = JMSUtils.lookupQueue(MessageConstants.EXCHANGE_MODULE_QUEUE);
		configQueue = JMSUtils.lookupQueue(ConfigConstants.CONFIG_MESSAGE_IN_QUEUE);
	}

	@Override
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String sendDataSourceMessage(String text, DataSourceQueue queue) throws MobileTerminalMessageException {

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
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String sendModuleMessage(String text, ModuleQueue queue) throws MobileTerminalMessageException {
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			final Session session = JMSUtils.connectToQueue(connection);

			TextMessage message = session.createTextMessage();
			message.setJMSReplyTo(responseQueue);
			message.setText(text);
			javax.jms.MessageProducer producer;

			switch (queue) {
			case AUDIT:
//				getProducer(session, auditQueue).send(message);
				producer = session.createProducer(auditQueue);
				producer.send(message);
				break;
			case EXCHANGE:
//				getProducer(session, exchangeQueue).send(message);
				producer = session.createProducer(exchangeQueue);
				producer.send(message);
				break;
			case CONFIG:
//				getProducer(session, configQueue).send(message);
				producer = session.createProducer(configQueue);
				producer.send(message);
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
	public String sendConfigMessage(String text) throws ConfigMessageException {
		try {
			return sendModuleMessage(text, ModuleQueue.CONFIG);
		} catch (MobileTerminalMessageException e) {
			LOG.error("[ Error when sending config message. ] {}", e.getMessage());
			throw new ConfigMessageException(e.getMessage());
		}
	}

//	private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
//		javax.jms.MessageProducer producer = session.createProducer(destination);
//		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//		producer.setTimeToLive(60000L);
//		return producer;
//	}
}
