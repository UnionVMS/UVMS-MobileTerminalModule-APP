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

import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
//import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

//@Startup
@Singleton
public class MobileTerminalExecutorServiceBean {

      /*
    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalExecutorServiceBean.class);

    @EJB
    ConfigService configService;

    @EJB
    //@Inject
    PollService pollService;

    @Resource(lookup="java:/UvmsMobileTerminalExecutorService")
    private ManagedScheduledExecutorService executorService;

    @PostConstruct
    public void postConstruct() {
        LOG.info("PluginTimerBean init");
        PluginTimerTask pluginTimerTask = new PluginTimerTask(configService);
        executorService.scheduleWithFixedDelay(pluginTimerTask, 15, 15, TimeUnit.MINUTES);

        PollTimerTask pollTimerTask = new PollTimerTask(pollService);
        executorService.scheduleWithFixedDelay(pollTimerTask, 5, 300, TimeUnit.SECONDS);
    }
    */
}