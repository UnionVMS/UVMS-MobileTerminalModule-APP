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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollId;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollStatus;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollDomainModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.consumer.MessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PollTimerService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

@Stateless
public class PollServiceBean implements PollService {

    private final static Logger LOG = LoggerFactory.getLogger(PollServiceBean.class);

    @EJB
    private MessageProducer messageProducer;

    @EJB
    private MessageConsumer messageConsumer;

    @EJB
    private PluginService sendPollService;

    @EJB
    private PollTimerService timerService;

    @EJB
    private PollDomainModelBean pollModel;

    @Override
    public CreatePollResultDto createPoll(PollRequestType poll, String username) throws MobileTerminalServiceException {
        LOG.debug("CREATE POLL INVOKED IN SERVICE LAYER");
        try {
            List<PollResponseType> createdPolls = pollModel.createPolls(poll, username);

            boolean triggerTimer = false;
            List<String> unsentPolls = new ArrayList<>();
            List<String> sentPolls = new ArrayList<>();
            for (PollResponseType createdPoll : createdPolls) {
                triggerTimer = PollType.PROGRAM_POLL.equals(createdPoll.getPollType());
                try {
                    AcknowledgeTypeType ack = sendPollService.sendPoll(createdPoll, username);
                    switch (ack) {
                        case NOK:
                            unsentPolls.add(createdPoll.getPollId().getGuid());
                            break;
                        case OK:
                            sentPolls.add(createdPoll.getPollId().getGuid());
                            break;
                    }
                } catch (MobileTerminalServiceException e) {
                    LOG.error(e.getMessage());
                }

                try {
                    String auditData = AuditModuleRequestMapper.mapAuditLogPollCreated(createdPoll.getPollType(), createdPoll.getPollId().getGuid(), createdPoll.getComment(), username);
                    messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
                } catch (AuditModelMarshallException e) {
                    LOG.error("Failed to send audit log message! Poll with guid {} was created", createdPoll.getPollId().getGuid());
                }
            }

            if (triggerTimer) {
                timerService.timerTimeout();
            }

            CreatePollResultDto result = new CreatePollResultDto();
            result.setSentPolls(sentPolls);
            result.setUnsentPolls(unsentPolls);
            result.setUnsentPoll(!unsentPolls.isEmpty());
            return result;
        } catch (MobileTerminalModelException | MobileTerminalMessageException e) {
        	LOG.error("Failed to create poll",e);
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    @Override
    public List<PollResponseType> getRunningProgramPolls() throws MobileTerminalServiceException {
        LOG.debug("GET RUNNING PROGRAM POLLS INVOKED IN SERVICE LAYER");
        try {
            return pollModel.getPollProgramList();
        } catch (MobileTerminalModelException e) {
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    @Override
    public PollResponseType startProgramPoll(String pollId, String username) throws MobileTerminalServiceException {
        LOG.debug("START POLLING INVOKED IN SERVICE LAYER");
        try {
            PollId pollIdType = new PollId();
            pollIdType.setGuid(pollId);
            PollResponseType startedPoll = pollModel.setStatusPollProgram(pollIdType, PollStatus.STARTED);
            try {
                String auditData = AuditModuleRequestMapper.mapAuditLogProgramPollStarted(startedPoll.getPollId().getGuid(), username);
                messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
            } catch (AuditModelMarshallException e) {
                LOG.error("Failed to send audit log message! Poll with guid {} was started", startedPoll.getPollId().getGuid());
            }

            return startedPoll;
        } catch (MobileTerminalModelException | MobileTerminalMessageException e) {
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    @Override
    public PollResponseType stopProgramPoll(String pollId, String username) throws MobileTerminalServiceException {
        LOG.debug("STOP POLLING INVOKED IN SERVICE LAYER");
        try {
            PollId pollIdType = new PollId();
            pollIdType.setGuid(pollId);
            PollResponseType stoppedPoll = pollModel.setStatusPollProgram(pollIdType, PollStatus.STOPPED);
            try {
                String auditData = AuditModuleRequestMapper.mapAuditLogProgramPollStopped(stoppedPoll.getPollId().getGuid(), username);
                messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
            } catch (AuditModelMarshallException e) {
                LOG.error("Failed to send audit log message! Poll with guid {} was stopped", stoppedPoll.getPollId().getGuid());
            }

            return stoppedPoll;
        } catch (MobileTerminalModelException | MobileTerminalMessageException e) {
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    @Override
    public PollResponseType inactivateProgramPoll(String pollId, String username) throws MobileTerminalServiceException {
        LOG.debug("INACTIVATE PROGRAM POLL INVOKED IN SERVICE LAYER");
        try {
            PollId pollIdType = new PollId();
            pollIdType.setGuid(pollId);
            PollResponseType inactivatedPoll = pollModel.setStatusPollProgram(pollIdType, PollStatus.ARCHIVED);
            try {
                String auditData = AuditModuleRequestMapper.mapAuditLogProgramPollInactivated(inactivatedPoll.getPollId().getGuid(), username);
                messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
            } catch (AuditModelMarshallException e) {
                LOG.error("Failed to send audit log message! Poll with guid {} was inactivated", inactivatedPoll.getPollId().getGuid());
            }

            return inactivatedPoll;
        } catch (MobileTerminalModelException | MobileTerminalMessageException e) {
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    @Override
    public PollListResponse getPollBySearchCriteria(PollListQuery query) throws MobileTerminalServiceException {
        LOG.debug("GET POLL BY SEARCHCRITERIA INVOKED IN SERVICE LAYER");
        try {
            return pollModel.getPollList(query);
        } catch (MobileTerminalModelException e) {
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    @Override
    public List<PollResponseType> timer() throws MobileTerminalException {
        LOG.debug("TIMER TRIGGERED IN SERVICE LAYER");

        return pollModel.getPollProgramRunningAndStarted();
    }
}
