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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.EnumException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollPayload;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PollModelToEntityMapper {
    private static Logger LOG = LoggerFactory.getLogger(PollModelToEntityMapper.class);

    private static PollBase createNewPollBase(MobileTerminal terminal, String terminalConnect, String channelGuid, PollRequestType requestType, String username) {
        PollBase pollBase = new PollBase();
        pollBase.setChannelGuid(channelGuid);
        pollBase.setMobileTerminal(terminal);
        pollBase.setTerminalConnect(terminalConnect);
        pollBase.setComment(requestType.getComment());
        pollBase.setUser(requestType.getUserName());
        
        pollBase.setUpdatedBy(username);
        pollBase.setUpdateTime(DateUtils.getNowDateUTC());
        return pollBase;
    }

    public static PollProgram mapToProgramPoll(MobileTerminal terminal, String terminalConnect, String channelGuid, PollRequestType requestType, String username)
            throws MobileTerminalModelMapperException {
        PollProgram poll = new PollProgram();
        PollBase pollBase = createNewPollBase(terminal, terminalConnect, channelGuid, requestType, username);
        poll.setPollBase(pollBase);
        poll.setPollState(PollStateEnum.STARTED);

        poll.setLatestRun(null);
        poll.setUpdatedBy(username);
        poll.setUpdateTime(DateUtils.getNowDateUTC());

        List<PollAttribute> attributes = requestType.getAttributes();
        if (attributes == null || attributes.isEmpty())
            throw new MobileTerminalModelMapperException("No attributes to map to program poll");
        for (PollAttribute attr : attributes) {
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
                throw new MobileTerminalModelMapperException("Poll attribute " + attr.getKey() + "  could not be parsed",e);
            }
        }
        return poll;
    }

    public static Poll mapToPoll(MobileTerminal comchannel, String connectId, String channelGuid, PollRequestType requestType, String username) throws MobileTerminalModelMapperException {
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
    
    private static Poll createPollBase(MobileTerminal comchannel, String terminalConnect, String channelGuid, PollRequestType requestType, String username) throws MobileTerminalModelMapperException {
        Poll poll = new Poll();
        PollBase pollBase = createNewPollBase(comchannel, terminalConnect, channelGuid, requestType, username);
        poll.setPollBase(pollBase);
        try {
        	poll.setPollType(EnumMapper.getPollTypeFromModel(requestType.getPollType()));
        } catch (EnumException e) {
        	throw new MobileTerminalModelMapperException("Couldn't map type of poll " , e);
        }

        poll.setUpdatedBy(username);
        poll.setUpdateTime(DateUtils.getNowDateUTC());

        return poll;
    }

    private static Poll mapToConfigurationPoll(MobileTerminal comchannel, String terminalConnect, String channelGuid, PollRequestType requestType, String usernmae)
            throws MobileTerminalModelMapperException {
    	Poll poll = createPollBase(comchannel, terminalConnect, channelGuid, requestType, usernmae);
        List<PollAttribute> attributes = requestType.getAttributes();
        if (attributes == null || attributes.isEmpty())
        	throw new MobileTerminalModelMapperException("No attributes to map to configuration poll");
        List<PollPayload> payloadList = new ArrayList<>();
        PollPayload payload = new PollPayload();
        for (PollAttribute attr : attributes) {
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
        		throw new MobileTerminalModelMapperException("Poll attribute  " + attr.getKey() + "  could not be parsed",e);
        	}
        }
        payload.setPoll(poll);
        payloadList.add(payload);
        poll.setPayloads(payloadList);
        return poll;
    }

    private static Poll mapToSamplingPoll(MobileTerminal comchannel, String terminalConnect, String channelGuid, PollRequestType requestType, String username)
            throws MobileTerminalModelMapperException {
    	Poll poll = createPollBase(comchannel, terminalConnect, channelGuid, requestType, username);
        List<PollAttribute> attributes = requestType.getAttributes();
        if (attributes == null || attributes.isEmpty())
        	throw new MobileTerminalModelMapperException("No attributes to map to sampling poll");
        List<PollPayload> payloadList = new ArrayList<>();
        PollPayload payload = new PollPayload();
        for (PollAttribute attr : attributes) {
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
        		throw new MobileTerminalModelMapperException("Poll attribute  " + attr.getKey() + "  could not be parsed",e);
        	}
        }
        payload.setPoll(poll);
        payloadList.add(payload);
        poll.setPayloads(payloadList);
        return poll;
    }
}
