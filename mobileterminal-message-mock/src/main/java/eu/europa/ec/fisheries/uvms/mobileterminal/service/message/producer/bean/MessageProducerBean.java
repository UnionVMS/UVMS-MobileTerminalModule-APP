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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.message.producer.bean;

import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.message.producer.MessageProducer;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.util.UUID;

@Stateless
public class MessageProducerBean implements MessageProducer, ConfigMessageProducer {

    public static final String MESSAGE_PRODUCER_METHODS_FAIL = "MESSAGE_PRODUCER_METHODS_FAIL";

    private void shouldIFail() throws MobileTerminalMessageException {
        String fail = System.getProperty(MESSAGE_PRODUCER_METHODS_FAIL, "false");
        if(!"false".equals(fail.toLowerCase())) {
            throw new MobileTerminalMessageException("MESSAGE_PRODUCER_METHODS_FAIL == true");
        }
    }

    @PostConstruct
    public void init() {
    }

    @Override
    public String sendDataSourceMessage(String text, DataSourceQueue queue) throws MobileTerminalMessageException {
        shouldIFail();
        return UUID.randomUUID().toString();
    }

    @Override
    public String sendModuleMessage(String text, ModuleQueue queue) throws MobileTerminalMessageException {
        shouldIFail();
        return UUID.randomUUID().toString();
    }

    @Override
    public String sendConfigMessage(String text) {
        return UUID.randomUUID().toString();
    }
}
