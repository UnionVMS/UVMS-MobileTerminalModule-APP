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

import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.ConfigServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalPollTimerServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class MobileTerminalExecutorServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalExecutorServiceBean.class);

    @EJB
    private ConfigServiceBean configService;

    @EJB
    private MobileTerminalPollTimerServiceBean timerService;

    private PluginTimerTask pluginTimerTask;

    @PostConstruct
    public void initPlugins() {
        try {
            if(pluginTimerTask == null) {
                pluginTimerTask = new PluginTimerTask(configService);
            }
            pluginTimerTask.run();
        } catch (Exception e) {
            LOG.error("Error when initializing PluginTimerTask", e);
        }
    }
    
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void initPluginTimer() {
        try {
            if(pluginTimerTask == null) {
                pluginTimerTask = new PluginTimerTask(configService);
            }
            LOG.info("PluginTimerTask initialized.");
            pluginTimerTask.run();
        } catch (Exception e) {
            LOG.error("[ Error when initializing PluginTimerTask. ] {}", e.getMessage());
        }
    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void initPollTimer() {
        try {
            LOG.info("PollTimerTask initialized.");
            timerService.timerTimeout();
        } catch (Exception e) {
            LOG.error("[ Error when initializing PollTimerTask. ] {}", e.getMessage());
        }
    }
}
