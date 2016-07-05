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
package eu.europa.ec.fisheries.uvms.mobileterminal.service;

import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

@Local
public interface MappedPollService {

    /**
     * Create poll
     * 
     * @param pollRequest
     * @return
     * @throws MobileTerminalServiceException
     */
    public CreatePollResultDto createPoll(PollRequestType pollRequest, String username) throws MobileTerminalServiceException;

    /**
     * Get running program polls
     * 
     * @return
     * @throws MobileTerminalServiceException
     */
    public List<PollDto> getRunningProgramPolls() throws MobileTerminalServiceException;

    /**
     * Start program poll
     * 
     * @param pollId
     * @return
     * @throws MobileTerminalServiceException
     */
    public PollDto startProgramPoll(String pollId, String username) throws MobileTerminalServiceException;

    /**
     * Stop program poll
     * 
     * @param pollId
     * @return
     * @throws MobileTerminalServiceException
     */
    public PollDto stopProgramPoll(String pollId, String username) throws MobileTerminalServiceException;

    /**
     * Inactivate program poll
     * 
     * @param pollId
     * @return
     * @throws MobileTerminalServiceException
     */
    public PollDto inactivateProgramPoll(String pollId, String username) throws MobileTerminalServiceException;

    /**
     * Get poll by search criteria
     * 
     * @param criteria
     * @return
     * @throws MobileTerminalServiceException
     */
    public PollChannelListDto getPollBySearchQuery(PollListQuery query) throws MobileTerminalServiceException;

    /**
     * Get pollable channels
     * 
     * @param query
     * 
     * @return
     * @throws MobileTerminalServiceException
     */
    public PollChannelListDto getPollableChannels(PollableQuery query) throws MobileTerminalException;

}