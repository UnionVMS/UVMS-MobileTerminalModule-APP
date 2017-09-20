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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.consumer.MessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;

@Stateless
public class ConfigServiceBean implements ConfigService {

    final static Logger LOG = LoggerFactory.getLogger(ConfigServiceBean.class);

    @EJB
    MessageProducer messageProducer;

	//@EJB(lookup = ServiceConstants.DB_ACCESS_CONFIG_MODEL)
	@EJB
	ConfigModelBean configModel;

    @EJB
    MessageConsumer reciever;

    @Override
    public List<TerminalSystemType> getTerminalSystems() throws MobileTerminalException {
        LOG.debug("GET TERMINAL SYSTEM TRANSPONDERS INVOKED IN SERVICE LAYER");
		final List<TerminalSystemType> systemList = configModel.getAllTerminalSystems();
        return systemList;
    }

	@Override
	public List<ConfigList> getConfig() throws MobileTerminalException {
		LOG.debug("Get configuration in service layer");
		final List<ConfigList> values = configModel.getConfigValues();
		return values;
	}

	@Override
	public List<Plugin> upsertPlugins(final List<PluginService> plugins, final String username) throws MobileTerminalException {
		final List<Plugin> plugin = configModel.upsertPlugins(plugins);
		return plugin;
	}

	@Override
	public List<ServiceResponseType> getRegisteredMobileTerminalPlugins() {
		LOG.debug("Get registered service types");
		try {
			final List<PluginType> pluginTypes = new ArrayList<>();
			pluginTypes.add(PluginType.SATELLITE_RECEIVER);
			final String data = ExchangeModuleRequestMapper.createGetServiceListRequest(pluginTypes);
			final String messageId = messageProducer.sendModuleMessage(data, ModuleQueue.EXCHANGE);
			final TextMessage response = reciever.getMessage(messageId, TextMessage.class);
			return ExchangeModuleResponseMapper.mapServiceListResponse(response, messageId);
		} catch (ExchangeModelMapperException | MobileTerminalMessageException e) {
			LOG.error("Failed to map to exchange get service list request");
			return null;
		}
	}
}