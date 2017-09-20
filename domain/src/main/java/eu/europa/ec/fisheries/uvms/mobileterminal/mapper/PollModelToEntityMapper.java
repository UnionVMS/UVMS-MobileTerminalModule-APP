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
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.EnumException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollPayload;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;

public class PollModelToEntityMapper {
    private static Logger LOG = LoggerFactory.getLogger(PollModelToEntityMapper.class);

    private static PollBase createNewPollBase(final MobileTerminal terminal, final String terminalConnect, final String channelGuid, final PollRequestType requestType, final String username) {
        final PollBase pollBase = new PollBase();
        pollBase.setChannelGuid(channelGuid);
        pollBase.setMobileTerminal(terminal);
        pollBase.setTerminalConnect(terminalConnect);
        pollBase.setComment(requestType.getComment());
        pollBase.setUser(requestType.getUserName());
        
        pollBase.setUpdatedBy(username);
        pollBase.setUpdateTime(DateUtils.getNowDateUTC());
        return pollBase;
    }

    public static PollProgram mapToProgramPoll(final MobileTerminal terminal, final String terminalConnect, final String channelGuid, final PollRequestType requestType, final String username)
            throws MobileTerminalModelMapperException {
        final PollProgram poll = new PollProgram();
        final PollBase pollBase = createNewPollBase(terminal, terminalConnect, channelGuid, requestType, username);
        poll.setPollBase(pollBase);
        poll.setPollState(PollStateEnum.STARTED);

        poll.setLatestRun(null);
        poll.setUpdatedBy(username);
        poll.setUpdateTime(DateUtils.getNowDateUTC());

        final List<PollAttribute> attributes = requestType.getAttributes();
        if (attributes == null || attributes.isEmpty())
            throw new MobileTerminalModelMapperException("No attributes to map to program poll");
        for (final PollAttribute attr : attributes) {
            try {
                switch (attr.getKey()) {
                case FREQUENCY:
                    poll.setFrequency(Integer.parseInt(attr.getValue()));
                    break;
                case START_DATE:
                    poll.setStartDate(eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils.parseToUTCDateTime(attr.getValue()));
                    break;
                case END_DATE:
                    poll.setStopDate(eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils.parseToUTCDateTime(attr.getValue()));
                    break;
                default:
                    LOG.debug("ProgramPoll with attr [ " + attr.getKey() + " ] is non valid to map");
                }
            } catch (UnsupportedOperationException | IllegalArgumentException e) {
                throw new MobileTerminalModelMapperException("Poll attribute [ " + attr.getKey() + " ] could not be parsed");
            }
        }

        return poll;
    }

    public static Poll mapToPoll(final MobileTerminal comchannel, final String connectId, final String channelGuid, final PollRequestType requestType, final String username) throws MobileTerminalModelMapperException {
    	switch (requestType.getPollType()) {
        case CONFIGURATION_POLL:
        	return mapToConfigurationPoll(comchannel, connectId, channelGuid, requestType, username);
        case SAMPLING_POLL:
        	return mapToSamplingPoll(comchannel, connectId, channelGuid, requestType, username);
        case MANUAL_POLL:
        	return createPollBase(comchannel, connectId, channelGuid, requestType, username);
        default:
        	throw new MobileTerminalModelMapperException("Non valid poll type");
    	}
    }
    
    private static Poll createPollBase(final MobileTerminal comchannel, final String terminalConnect, final String channelGuid, final PollRequestType requestType, final String username) throws MobileTerminalModelMapperException {
        final Poll poll = new Poll();
        final PollBase pollBase = createNewPollBase(comchannel, terminalConnect, channelGuid, requestType, username);
        poll.setPollBase(pollBase);
        try {
        	poll.setPollType(EnumMapper.getPollTypeFromModel(requestType.getPollType()));
        } catch (final EnumException e) {
        	throw new MobileTerminalModelMapperException("Couldn't map type of poll " + e.getMessage());
        }

        poll.setUpdatedBy(username);
        poll.setUpdateTime(DateUtils.getNowDateUTC());

        return poll;
    }

    private static Poll mapToConfigurationPoll(final MobileTerminal comchannel, final String terminalConnect, final String channelGuid, final PollRequestType requestType, final String usernmae)
            throws MobileTerminalModelMapperException {
    	final Poll poll = createPollBase(comchannel, terminalConnect, channelGuid, requestType, usernmae);
        final List<PollAttribute> attributes = requestType.getAttributes();
        if (attributes == null || attributes.isEmpty())
        	throw new MobileTerminalModelMapperException("No attributes to map to configuration poll");
        final List<PollPayload> payloadList = new ArrayList<>();
        final PollPayload payload = new PollPayload();
        for (final PollAttribute attr : attributes) {
        	try {
        		switch (attr.getKey()) {
                case REPORT_FREQUENCY:
                	payload.setReportingFrequency(Integer.parseInt(attr.getValue()));
                    break;
                case GRACE_PERIOD:
                	payload.setGracePeriod(Integer.parseInt(attr.getValue()));
                    break;
                case IN_PORT_GRACE:
                	payload.setInPortGrace(Integer.parseInt(attr.getValue()));
                    break;
                case DNID:
                	payload.setNewDnid(attr.getValue());
                    break;
                case MEMBER_NUMBER:
                	payload.setNewMemberNumber(attr.getValue());
                    break;
        		}
        	} catch (UnsupportedOperationException | IllegalArgumentException e) {
        		throw new MobileTerminalModelMapperException("Poll attribute [ " + attr.getKey() + " ] could not be parsed");
        	}
        }
        payload.setPoll(poll);
        payloadList.add(payload);
        poll.setPayloads(payloadList);
        return poll;
    }

    private static Poll mapToSamplingPoll(final MobileTerminal comchannel, final String terminalConnect, final String channelGuid, final PollRequestType requestType, final String username)
            throws MobileTerminalModelMapperException {
    	final Poll poll = createPollBase(comchannel, terminalConnect, channelGuid, requestType, username);
        final List<PollAttribute> attributes = requestType.getAttributes();
        if (attributes == null || attributes.isEmpty())
        	throw new MobileTerminalModelMapperException("No attributes to map to sampling poll");
        final List<PollPayload> payloadList = new ArrayList<>();
        final PollPayload payload = new PollPayload();
        for (final PollAttribute attr : attributes) {
        	try {
        		switch (attr.getKey()) {
        		case START_DATE:
        			payload.setStartDate(eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils.parseToUTCDateTime(attr.getValue()));
                	break;
        		case END_DATE:
        			payload.setStopDate(eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils.parseToUTCDateTime(attr.getValue()));
                    break;
        		}
        	} catch (UnsupportedOperationException | IllegalArgumentException e) {
        		throw new MobileTerminalModelMapperException("Poll attribute [ " + attr.getKey() + " ] could not be parsed");
        	}
        }
        payload.setPoll(poll);
        payloadList.add(payload);
        poll.setPayloads(payloadList);
        return poll;
    }
}