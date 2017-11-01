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

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.consumer.MessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;

@Stateless
public class MobileTerminalMessageConsumer implements MessageConsumer, ConfigMessageConsumer {

    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalMessageConsumer.class);

    private ConnectionFactory connectionFactory;

    private Queue responseMobileTerminalQueue;

    @PostConstruct
    private void init() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        responseMobileTerminalQueue = JMSUtils.lookupQueue(MessageConstants.COMPONENT_RESPONSE_QUEUE);
    }

    @Override
    public <T> T getMessage(String correlationId, Class type) throws MobileTerminalMessageException {

        Message message = null;
    	Connection connection=null;

        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            
            message = session.createTextMessage(createResponse());
            message.setJMSCorrelationID(correlationId);
            message.setJMSMessageID(correlationId);

        } catch (ExchangeModelMarshallException | JMSException   e) {
        	LOG.warn("Problem getMessage",e);
        } finally{
        	JMSUtils.disconnectQueue(connection);
        }

        return (T) message;


    }

    private String createResponse() throws ExchangeModelMarshallException {

        AcknowledgeType acknowledgeType = new AcknowledgeType();
        acknowledgeType.setType(AcknowledgeTypeType.OK);
        acknowledgeType.setMessage("MESSAGE");
        return ExchangeModuleResponseMapper.mapSetCommandResponse(acknowledgeType);
    }

    @Override
    public <T> T getConfigMessage(String correlationId, Class type) throws ConfigMessageException {
        try {
            return getMessage(correlationId, type);
        }
        catch (MobileTerminalMessageException e) {
            LOG.error("[ Error when getting config message. ] {}", e.getMessage());
            throw new ConfigMessageException(e.getMessage());
        }
    }

}