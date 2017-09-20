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
package eu.europa.ec.fisheries.uvms.mobileterminal.timer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.ServiceToPluginMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;

public class PluginTimerTask implements Runnable{

    ConfigService configService;
    final static Logger LOG = LoggerFactory.getLogger(PluginTimerTask.class);

    public PluginTimerTask(final ConfigService configService){
        this.configService = configService;
    }

    @Override
    public void run() {
        try {
            final List<ServiceResponseType> serviceTypes = configService.getRegisteredMobileTerminalPlugins();
            LOG.debug("get services from exchange registry");
            if(serviceTypes != null) {
                configService.upsertPlugins(ServiceToPluginMapper.mapToPluginList(serviceTypes), "PluginTimerBean");
                LOG.debug("upserted plugins");
            }
        } catch (final MobileTerminalException e) {
            LOG.info("Couldn't update plugins... ", e.getMessage());
        }

    }
}