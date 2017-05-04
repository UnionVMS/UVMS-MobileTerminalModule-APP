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
package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.EnumException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.ChannelHistory;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;

public class MobileTerminalEntityToModelMapper {
    private static Logger LOG = LoggerFactory.getLogger(MobileTerminalEntityToModelMapper.class);

    public static MobileTerminalType mapToMobileTerminalType(MobileTerminal entity, Channel channel) throws MobileTerminalModelMapperException {
        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        return mapToMobileTerminalType(entity, channels);
    }

    public static MobileTerminalType mapToMobileTerminalType(MobileTerminal entity, Set<Channel> channels) throws MobileTerminalModelMapperException {
        MobileTerminalType type = mapToMobileTerminalType(entity);

        type.getChannels().clear();
        type.getChannels().addAll(mapChannels(entity.getChannels(), entity.getCurrentEvent()));

        return type;
    }

    public static MobileTerminalType mapToMobileTerminalType(MobileTerminal entity) throws MobileTerminalModelMapperException {
        if (entity == null) {
            throw new MobileTerminalModelMapperException("No mobile terminal entity to map");
        }

        MobileTerminalEvent currentEvent = entity.getCurrentEvent();
        if (currentEvent == null) {
            throw new MobileTerminalModelMapperException("No mobile terminal event entity to map");
        }

        MobileTerminalType model = new MobileTerminalType();
        model.setMobileTerminalId(mapToMobileTerminalId(entity.getGuid()));

        Plugin plugin = PluginMapper.mapEntityToModel(entity.getPlugin());
        model.setPlugin(plugin);

        try {
            model.setSource(mapToMobileTerminalSource(entity.getSource()));
        } catch (EnumException e) {
            LOG.error("[ Error when setting mobile terminal source. ] {}", e.getMessage());
        }

        model.setConnectId(currentEvent.getConnectId());

        model.setType(entity.getMobileTerminalType().name());
        model.setInactive(entity.getInactivated());
        model.setArchived(entity.getArchived());
        model.setId(entity.getId().intValue());

        model.getChannels().addAll(mapChannels(entity.getChannels(), currentEvent));

        model.getAttributes().addAll(AttributeMapper.mapAttributeStringToTerminalAttribute(currentEvent.getAttributes()));

        return model;
    }

    private static List<ComChannelType> mapChannels(Set<Channel> channels, MobileTerminalEvent currentEvent) {
        if (channels == null || channels.isEmpty()) {
            return new ArrayList<>();
        }
        List<ComChannelType> channelList = new ArrayList<>();
        for (Channel channel : channels) {
            if (channel.getArchived() != null && channel.getArchived()) {
                continue;
            }
            ChannelHistory current = channel.getCurrentHistory();
            if (current != null) {
                ComChannelType comChannel = new ComChannelType();
                comChannel.setName(current.getName());
                comChannel.setGuid(channel.getGuid());

                comChannel.getAttributes().addAll(AttributeMapper.mapAttributeStringToComChannelAttribute(current.getAttributes()));

                ComChannelCapability pollCapability = new ComChannelCapability();
                pollCapability.setType(MobileTerminalConstants.CAPABILITY_POLLABLE);

                Channel wrkPollChannel = currentEvent.getPollChannel();
                if(wrkPollChannel != null) {
                    pollCapability.setValue(currentEvent.getPollChannel().equals(channel));
                }
                else{
                    pollCapability.setValue(false);
                }
                comChannel.getCapabilities().add(pollCapability);

                ComChannelCapability configCapability = new ComChannelCapability();
                configCapability.setType(MobileTerminalConstants.CAPABILITY_CONFIGURABLE);

                Channel wrkConfigChannel = currentEvent.getConfigChannel();
                if (wrkConfigChannel != null) {
                    configCapability.setValue(currentEvent.getConfigChannel().equals(channel));
                } else {
                    configCapability.setValue(false);
                }

                comChannel.getCapabilities().add(configCapability);

                ComChannelCapability defaultCapability = new ComChannelCapability();
                defaultCapability.setType(MobileTerminalConstants.CAPABILITY_DEFAULT_REPORTING);

                Channel wrkDefaultChannel = currentEvent.getDefaultChannel();
                if(wrkDefaultChannel != null) {
                    defaultCapability.setValue(currentEvent.getDefaultChannel().equals(channel));
                }
                else{
                    defaultCapability.setValue(false);
                }
                comChannel.getCapabilities().add(defaultCapability);

                channelList.add(comChannel);
            }
        }

        return channelList;
    }

    private static MobileTerminalSource mapToMobileTerminalSource(MobileTerminalSourceEnum mobtermSourceId) throws EnumException {
        if (mobtermSourceId != null) {
            switch (mobtermSourceId) {
                case INTERNAL:
                    return MobileTerminalSource.INTERNAL;
                case NATIONAL:
                    return MobileTerminalSource.NATIONAL;
            }
        }
        throw new EnumException("Couldn't map enum");
    }

    private static MobileTerminalId mapToMobileTerminalId(String mobtermGuid) throws MobileTerminalModelMapperException {
        if (mobtermGuid == null || mobtermGuid.isEmpty()) {
            throw new MobileTerminalModelMapperException("No GUID found");
        }
        MobileTerminalId terminalId = new MobileTerminalId();
        terminalId.setGuid(mobtermGuid);
        return terminalId;
    }

}