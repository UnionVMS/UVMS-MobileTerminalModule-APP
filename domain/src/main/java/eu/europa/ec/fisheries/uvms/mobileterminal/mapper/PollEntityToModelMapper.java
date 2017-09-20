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
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollId;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;

public class PollEntityToModelMapper {
    private static Logger LOG = LoggerFactory.getLogger(PollEntityToModelMapper.class);

    private static PollResponseType mapToPollResponseType(final PollBase pollBase, final MobileTerminalType mobileTerminalType) throws MobileTerminalModelMapperException {
        final PollResponseType response = new PollResponseType();
        response.setComment(pollBase.getComment());
        response.setUserName(pollBase.getUser());
        // TODO created time?
        // response.setCreatedTime();
        response.setMobileTerminal(mobileTerminalType);
        response.getAttributes().add(createPollAttribute(PollAttributeType.USER, pollBase.getUser()));
        return response;
    }

    public static PollResponseType mapToPollResponseType(final PollProgram program, final MobileTerminalType mobileTerminalType) throws MobileTerminalModelMapperException {
        final PollResponseType response = mapToPollResponseType(program.getPollBase(), mobileTerminalType);
        response.setPollType(PollType.PROGRAM_POLL);
        final PollId pollId = new PollId();
        pollId.setGuid(program.getGuid());
        response.setPollId(pollId);

        response.getAttributes().addAll(getProgramPollAttributes(program));
        return response;
    }

    public static PollResponseType mapToPollResponseType(final Poll poll, final MobileTerminalType mobileTerminalType, final PollType pollType) throws MobileTerminalModelMapperException {
        final PollResponseType response = mapToPollResponseType(poll.getPollBase(), mobileTerminalType);
        response.setPollType(pollType);
        final PollId pollId = new PollId();
        pollId.setGuid(poll.getGuid());
        response.setPollId(pollId);
        
        return response;
    }

    private static List<PollAttribute> getProgramPollAttributes(final PollProgram program) {
        final List<PollAttribute> attributes = new ArrayList<PollAttribute>();
        attributes.add(createPollAttribute(PollAttributeType.FREQUENCY, program.getFrequency().toString()));
        attributes.add(createPollAttribute(PollAttributeType.START_DATE, DateUtils.parseUTCDateTimeToString(program.getStartDate())));
        attributes.add(createPollAttribute(PollAttributeType.END_DATE, DateUtils.parseUTCDateTimeToString(program.getStopDate())));

        switch (program.getPollState()) {
        case STARTED:
        	attributes.add(createPollAttribute(PollAttributeType.PROGRAM_RUNNING, MobileTerminalConstants.TRUE));
            break;
        case STOPPED:
        case ARCHIVED:
        	attributes.add(createPollAttribute(PollAttributeType.PROGRAM_RUNNING, MobileTerminalConstants.FALSE));
            break;
        }
        return attributes;
    }

    private static PollAttribute createPollAttribute(final PollAttributeType key, final String value) {
        final PollAttribute attrib = new PollAttribute();
        attrib.setKey(key);
        attrib.setValue(value);
        return attrib;
    }
}