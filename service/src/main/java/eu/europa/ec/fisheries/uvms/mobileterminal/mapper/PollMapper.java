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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.AttributeDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceMapperException;

/**
 **/
public class PollMapper {

    final static Logger LOG = LoggerFactory.getLogger(PollMapper.class);

    public static List<PollDto> mapPolls(final List<PollResponseType> pollResponses) throws MobileTerminalServiceMapperException {
        final List<PollDto> dtoList = new ArrayList<>();
        for (final PollResponseType response : pollResponses) {
            dtoList.add(mapPoll(response));
        }
        return dtoList;
    }
    
    public static PollDto mapPoll(final PollResponseType response) throws MobileTerminalServiceMapperException {
        checkInputParams(response.getMobileTerminal());
        return createPollDto(response);
    }

    private static void checkInputParams(final MobileTerminalType terminal) throws MobileTerminalServiceMapperException {
        if (terminal == null) {
            throw new MobileTerminalServiceMapperException("MobileTerminal is null");
        }
    }

    private static PollDto createPollDto(final PollResponseType response) {
        final MobileTerminalType terminal = response.getMobileTerminal();
        final List<PollAttribute> attributes = response.getAttributes();

        final PollDto dto = new PollDto();
        dto.addValue(PollKey.CONNECTION_ID, response.getMobileTerminal().getConnectId());
        dto.addValue(PollKey.TRANSPONDER, terminal.getType());
        dto.addValue(PollKey.POLL_ID, response.getPollId().getGuid());
        dto.addValue(PollKey.POLL_TYPE, response.getPollType().name());
        dto.addValue(PollKey.POLL_COMMENT, response.getComment());
        
        final String startDate = getPollAttribute(PollAttributeType.START_DATE, attributes);
        if (startDate != null) {
            dto.addValue(PollKey.START_DATE, startDate);
        }
        final String endDate = getPollAttribute(PollAttributeType.END_DATE, attributes);
        if (endDate != null) {
            dto.addValue(PollKey.END_DATE, endDate);
        }
        final String frequency = getPollAttribute(PollAttributeType.FREQUENCY, attributes);
        if (frequency != null) {
            dto.addValue(PollKey.FREQUENCY, frequency);
        }
        final String programRunning = getPollAttribute(PollAttributeType.PROGRAM_RUNNING, attributes);
        if (programRunning != null) {
            dto.addValue(PollKey.PROGRAM_RUNNING, programRunning);
        }

        final String creator = getPollAttribute(PollAttributeType.USER, attributes);
        if(creator != null) {
        	dto.addValue(PollKey.USER, creator);
        }

        return dto;
    }

    public static String getPollAttribute(final PollAttributeType type, final List<PollAttribute> attributes) {
        for (final PollAttribute attribute : attributes) {
            if (attribute.getKey().equals(type)) {
                return attribute.getValue();
            }
        }
        return null;
    }

    public static PollChannelDto mapPollChannel(final MobileTerminalType mobileTerminal) throws MobileTerminalServiceMapperException {
    	checkInputParams(mobileTerminal);
    	
        final PollChannelDto pollChannel = new PollChannelDto();
        
        // TODO exception handling
        pollChannel.setComChannelId(mobileTerminal.getChannels().get(0).getGuid());
        pollChannel.setMobileTerminalId(mobileTerminal.getMobileTerminalId().getGuid());
        pollChannel.setMobileTerminalType(mobileTerminal.getType());
        pollChannel.setConnectId(mobileTerminal.getConnectId());
        
        final List<AttributeDto> attributes = new ArrayList<>();
        for(final MobileTerminalAttribute attr : mobileTerminal.getAttributes()) {
        	final AttributeDto dto = new AttributeDto();
        	dto.setType(attr.getType());
        	dto.setValue(attr.getValue());
        	attributes.add(dto);
        }
        
        //Only first channel
        if(mobileTerminal.getChannels().get(0) != null) {
        	for(final ComChannelAttribute channel : mobileTerminal.getChannels().get(0).getAttributes()) {
        		final AttributeDto cDto = new AttributeDto();
        		cDto.setType(channel.getType());
        		cDto.setValue(channel.getValue());
        		attributes.add(cDto);
        	}
        }
        
        pollChannel.setMobileTerminalAttributes(attributes);
        return pollChannel;
    }
}