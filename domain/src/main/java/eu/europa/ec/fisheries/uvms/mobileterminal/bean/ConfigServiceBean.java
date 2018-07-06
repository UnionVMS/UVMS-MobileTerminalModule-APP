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
package eu.europa.ec.fisheries.uvms.mobileterminal.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.uvms.mobileterminal.client.ExchangeRESTClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;

@Stateless
@LocalBean
public class ConfigServiceBean {

    private final static Logger LOG = LoggerFactory.getLogger(ConfigServiceBean.class);

	@EJB
	private ConfigModelBean configModel;

    public List<TerminalSystemType> getTerminalSystems() throws MobileTerminalException {
        LOG.debug("GET TERMINAL SYSTEM TRANSPONDERS INVOKED IN SERVICE LAYER");
		return configModel.getAllTerminalSystems();
    }

	public List<ConfigList> getConfig() {
		LOG.debug("Get configuration in service layer");
		return configModel.getConfigValues();
	}

	public List<Plugin> upsertPlugins(List<PluginService> plugins, String username) throws MobileTerminalException {
		return configModel.upsertPlugins(plugins);
	}

	public List<ServiceResponseType> getRegisteredMobileTerminalPlugins() {
		LOG.debug("Get registered service types");
		List<PluginType> pluginTypes = new ArrayList<>();
		pluginTypes.add(PluginType.SATELLITE_RECEIVER);
		GetServiceListRequest getServiceListRequest = new GetServiceListRequest();
		getServiceListRequest.getType().addAll(pluginTypes);
		ExchangeRESTClient exchangeRESTClient = new ExchangeRESTClient();
		GetServiceListResponse getServiceListResponse = exchangeRESTClient.getServiceList(getServiceListRequest);
		return getServiceListResponse.getService();
	}
}
