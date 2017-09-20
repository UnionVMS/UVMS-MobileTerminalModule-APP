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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.uvms.config.model.exception.ModelMarshallException;
import eu.europa.ec.fisheries.uvms.config.model.mapper.ModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollToCommandRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.consumer.MessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

@Stateless
public class PluginServiceBean implements PluginService {

    final static Logger LOG = LoggerFactory.getLogger(PluginServiceBean.class);

    public static final String EXCHANGE_MODULE_NAME = "exchange";
    public static final String DELIMETER = ".";
    public static final String INTERNAL_DELIMETER = ",";
    public static final String SETTING_KEY_DNID_LIST = "DNIDS";

    @EJB
    MessageProducer messageProducer;

    @EJB
    MessageConsumer reciever;

//    @EJB(lookup = ServiceConstants.DB_ACCESS_CONFIG_MODEL)
    @EJB
    ConfigModelBean configModel;

    @Override
    public AcknowledgeTypeType sendPoll(final PollResponseType poll, final String username) throws MobileTerminalServiceException {
        try {
            final PollType pollType = PollToCommandRequestMapper.mapToPollType(poll);
            final String pluginServiceName = poll.getMobileTerminal().getPlugin().getServiceName();
            final String exchangeData = ExchangeModuleRequestMapper.createSetCommandSendPollRequest(pluginServiceName, pollType, username, null);
            final String messageId = messageProducer.sendModuleMessage(exchangeData, ModuleQueue.EXCHANGE);
            final TextMessage response = reciever.getMessage(messageId, TextMessage.class);
            final AcknowledgeType ack = ExchangeModuleResponseMapper.mapSetCommandResponse(response, messageId);
            LOG.debug("Poll: " + poll.getPollId().getGuid() + " sent to exchange. Response: " + ack.getType());
            return ack.getType();
        } catch (ExchangeModelMapperException | MobileTerminalMessageException | MobileTerminalModelMapperException e) {
            LOG.error("Failed to send poll command! Poll with guid {} was created", poll.getPollId().getGuid());
            throw new MobileTerminalServiceException("Failed to send poll command. Poll with guid " + poll.getPollId().getGuid() + " was not sent");
        }
    }
    
    

    @Override
    public void processUpdatedDNIDList(final String pluginName) {
        try {
            final List<String> dnidList = configModel.updatedDNIDList(pluginName);

            final String settingKey = pluginName + DELIMETER + SETTING_KEY_DNID_LIST;
            final StringBuffer buffer = new StringBuffer();
            for (final String dnid : dnidList) {
                buffer.append(dnid + INTERNAL_DELIMETER);
            }
            final String settingValue = buffer.toString();

            try {
                sendUpdatedDNIDListToConfig(pluginName, settingKey, settingValue);
            } catch (ModelMarshallException | MobileTerminalMessageException e) {
                LOG.debug("Couldn't send to config module. Sending to exchange module.");
                sendUpdatedDNIDListToExchange(pluginName, SETTING_KEY_DNID_LIST, settingValue);
            }
        } catch (final MobileTerminalModelException ex) {
            LOG.error("Couldn't get updated DNID List");
        }
    }

    private void sendUpdatedDNIDListToConfig(final String pluginName, final String settingKey, final String settingValue) throws ModelMarshallException, MobileTerminalMessageException {
        final SettingType setting = new SettingType();
        setting.setKey(settingKey);
        setting.setModule(EXCHANGE_MODULE_NAME);
        setting.setDescription("DNID list for all active mobile terminals. Plugin use it to know which channels it should be listening to");
        setting.setGlobal(false);
        setting.setValue(settingValue);

        final String setSettingRequest = ModuleRequestMapper.toSetSettingRequest(EXCHANGE_MODULE_NAME, setting, "UVMS");
        final String messageId = messageProducer.sendModuleMessage(setSettingRequest, ModuleQueue.CONFIG);
        final TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        LOG.info("UpdatedDNIDList sent to config module");
    }

    private void sendUpdatedDNIDListToExchange(final String pluginName, final String settingKey, final String settingValue) {
        try {
            final String request = ExchangeModuleRequestMapper.createUpdatePluginSettingRequest(pluginName, settingKey, settingValue);
            final String messageId = messageProducer.sendModuleMessage(request, ModuleQueue.EXCHANGE);
            final TextMessage response = reciever.getMessage(messageId, TextMessage.class);
            LOG.info("UpdatedDNIDList sent to exchange module {} {}",pluginName,settingKey);
        } catch (ExchangeModelMarshallException | MobileTerminalMessageException e) {
            LOG.error("Failed to send updated DNID list {} {} {}",pluginName,settingKey,e);
        }
    }

}