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
package eu.europa.ec.fisheries.uvms.mobileterminal.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.ChannelDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.EnumException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPluginCapability;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.EnumMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.MobileTerminalEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollModelToEntityMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.poll.PollSearchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.*;

/**
 **/
@Stateless
@LocalBean
public class PollDomainModelBean  {
    final static Logger LOG = LoggerFactory.getLogger(PollDomainModelBean.class);

    @EJB
    PollDao pollDao;

    @EJB
    PollProgramDao pollProgramDao;

    @EJB
    TerminalDao terminalDao;

    @EJB
    ChannelDao channelDao;
    
    private MobileTerminalType mapPollableTerminalType(final MobileTerminalTypeEnum type, final String guid) throws MobileTerminalModelException {
        final MobileTerminal terminal = terminalDao.getMobileTerminalByGuid(guid);
        return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
	}

    private MobileTerminalType getPollableTerminalType(final String guid, final String channelGuid) throws MobileTerminalModelException {
        final MobileTerminal terminal = terminalDao.getMobileTerminalByGuid(guid);
        checkPollable(terminal);

        if(channelGuid != null && !channelGuid.isEmpty()) {
            for (final Channel channel : terminal.getChannels()) {
                if (channel.getGuid().equalsIgnoreCase(channelGuid)) {
                    if (!channel.getMobileTerminal().getGuid().equalsIgnoreCase(guid)) {
                        throw new MobileTerminalModelException("Channel " + channel.getGuid() + " can not be polled, because it is not part of terminal " + terminal.getGuid());
                    }
                    return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal, channel);
                }
            }

        }
		throw new MobileTerminalModelException("Could not find channel " + channelGuid + " based on");
    }
    
    private void checkPollable(final MobileTerminal terminal) throws MobileTerminalModelException {
    	if(terminal.getArchived()) {
    		throw new MobileTerminalModelException("Terminal is archived");
    	}
    	if(terminal.getInactivated()) {
    		throw new MobileTerminalModelException("Terminal is inactive");
    	}
    	if(terminal.getPlugin() != null && terminal.getPlugin().getPluginInactive()) {
    		throw new MobileTerminalModelException("Terminal connected to no longer active Plugin (LES)");
    	}
    }
    
    public List<PollResponseType> createPolls(final PollRequestType pollRequest, final String username) throws MobileTerminalModelException {
        if (pollRequest == null || pollRequest.getPollType() == null) {
            throw new InputArgumentException("No polls to create");
        }

        if (pollRequest.getComment() == null || pollRequest.getUserName() == null) {
            throw new InputArgumentException("Cannot create without comment and user");
        }
        
        if(pollRequest.getMobileTerminals() == null) {
        	throw new MobileTerminalModelException("No mobile terminals for " + pollRequest.getPollType());
        }

        List<PollResponseType> responseList;
        Map<Poll, MobileTerminalType> pollMobileTerminalTypeMap;
        switch (pollRequest.getPollType()) {
            case PROGRAM_POLL:
                final Map<PollProgram, MobileTerminalType> pollProgramMobileTerminalTypeMap = validateAndMapToProgramPolls(pollRequest, username);
                responseList = createPollPrograms(pollProgramMobileTerminalTypeMap, username);
                break;
            case CONFIGURATION_POLL:
            case MANUAL_POLL:
            case SAMPLING_POLL:
                pollMobileTerminalTypeMap = validateAndMapToPolls(pollRequest, username);
                responseList = createPolls(pollMobileTerminalTypeMap, pollRequest.getPollType());
                break;
            default:
                LOG.error("[ Could not decide poll type ] {}", pollRequest.getPollType());
                throw new MobileTerminalModelException("Could not decide Poll Type when creating polls");
        }
        return responseList;
    }

    private Map<PollProgram, MobileTerminalType> validateAndMapToProgramPolls(final PollRequestType pollRequest, final String username) throws MobileTerminalModelException {
        final Map<PollProgram, MobileTerminalType> map = new HashMap<>();

        for(final PollMobileTerminal pollTerminal : pollRequest.getMobileTerminals()) {
            final MobileTerminal mobileTerminalEntity = terminalDao.getMobileTerminalByGuid(pollTerminal.getMobileTerminalId());
            final String connectId = mobileTerminalEntity.getCurrentEvent().getConnectId();
            if (!pollTerminal.getConnectId().equals(connectId)) {
                throw new MobileTerminalModelException("Terminal " + mobileTerminalEntity.getGuid() + " can not be polled, because it is not linked to asset " + connectId);
            }
            final MobileTerminalType terminalType = getPollableTerminalType(pollTerminal.getMobileTerminalId(), pollTerminal.getComChannelId());
            final PollProgram pollProgram = PollModelToEntityMapper.mapToProgramPoll(mobileTerminalEntity, connectId, pollTerminal.getComChannelId(), pollRequest, username);
            map.put(pollProgram, terminalType);
        }
        return map;
    }

    private Map<Poll, MobileTerminalType> validateAndMapToPolls(final PollRequestType pollRequest, final String username) throws MobileTerminalModelException {
        final Map<Poll, MobileTerminalType> map = new HashMap<>();

        for(final PollMobileTerminal pollTerminal : pollRequest.getMobileTerminals()) {
            final MobileTerminal mobileTerminalEntity = terminalDao.getMobileTerminalByGuid(pollTerminal.getMobileTerminalId());
            final String connectId = mobileTerminalEntity.getCurrentEvent().getConnectId();
            if (!pollTerminal.getConnectId().equals(connectId)) {
                throw new MobileTerminalModelException("Terminal " + mobileTerminalEntity.getGuid() + " can not be polled, because it is not linked to asset " + connectId);
            }

            if(pollRequest.getPollType() != PollType.MANUAL_POLL){
                validateMobileTerminalPluginCapability(mobileTerminalEntity.getPlugin().getCapabilities(), pollRequest.getPollType(), mobileTerminalEntity.getPlugin().getPluginServiceName());
            }
            final MobileTerminalType terminalType = getPollableTerminalType(pollTerminal.getMobileTerminalId(), pollTerminal.getComChannelId());
            final Poll poll = PollModelToEntityMapper.mapToPoll(mobileTerminalEntity, connectId, pollTerminal.getComChannelId(), pollRequest, username);
            map.put(poll, terminalType);
        }
        return map;
    }

    private void validateMobileTerminalPluginCapability(final Set<MobileTerminalPluginCapability> capabilities, final PollType pollType, final String pluginServiceName) throws MobileTerminalModelException {
        PluginCapabilityType pluginCapabilityType = PluginCapabilityType.CONFIGURABLE;
        switch (pollType){
            case CONFIGURATION_POLL:
                pluginCapabilityType = PluginCapabilityType.CONFIGURABLE;
                break;
            case SAMPLING_POLL:
                pluginCapabilityType = PluginCapabilityType.SAMPLING;
                break;
            default:
                throw new MobileTerminalModelException("Cannot create " + pollType.name() +"  poll when plugin: " + pluginServiceName);
        }
        if (!validatePluginHasCapabilityConfigurable(capabilities, pluginCapabilityType)){
            throw new MobileTerminalModelException("Cannot create " + pollType.name() +"  poll when plugin: " + pluginServiceName + " has not capability " + pluginCapabilityType.name()+" set");
        }
    }

    private boolean validatePluginHasCapabilityConfigurable(final Set<MobileTerminalPluginCapability> capabilities, final PluginCapabilityType pluginCapability) {
        for (final MobileTerminalPluginCapability pluginCap : capabilities){
            if(pluginCapability.name().equalsIgnoreCase(pluginCap.getName())){
                return true;
            }
        }
        return false;
    }

    private List<PollResponseType> createPollPrograms(final Map<PollProgram, MobileTerminalType> map, final String username) throws MobileTerminalModelException {
        final List<PollResponseType> responseList = new ArrayList<>();
        final Iterator<Map.Entry<PollProgram, MobileTerminalType>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            final Map.Entry<PollProgram, MobileTerminalType> next = iterator.next();
            final PollProgram pollProgram = next.getKey();
            final MobileTerminalType mobileTerminalType = next.getValue();
            try {
                pollDao.createPollProgram(pollProgram);
                responseList.add(PollEntityToModelMapper.mapToPollResponseType(pollProgram, mobileTerminalType));
            } catch (final PollDaoException e) {
                LOG.error("[ Could not persist PollProgram ]");
                throw new MobileTerminalModelException("Could not persist PollProgam");
            } catch (final MobileTerminalModelMapperException e) {
                LOG.error("Could not map PollProgram to PollResponseType");
                throw e;
            }
        }
        return responseList;
    }

    private List<PollResponseType> createPolls(final Map<Poll, MobileTerminalType> map, final PollType pollType) throws MobileTerminalModelException {
        final List<PollResponseType> responseList = new ArrayList<>();
        final Iterator<Map.Entry<Poll, MobileTerminalType>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            final Map.Entry<Poll, MobileTerminalType> next = iterator.next();
            final Poll poll = next.getKey();
            final MobileTerminalType mobileTerminalType = next.getValue();
            try {
                pollDao.createPoll(poll);
                responseList.add(PollEntityToModelMapper.mapToPollResponseType(poll, mobileTerminalType, pollType));
            } catch (final PollDaoException e) {
                LOG.error("[ Could not persist Poll ]");
                throw new MobileTerminalModelException("Could not persist Poll");
            } catch (final MobileTerminalModelMapperException e) {
                LOG.error("Could not map Poll to PollResponseType");
                throw e;
            }
        }
        return responseList;

    }

    public PollListResponse getPollList(final PollListQuery query) throws MobileTerminalModelException {
        if (query == null) {
        	throw new InputArgumentException("Cannot get poll list because no query.");
        }

        if (query.getPagination() == null) {
        	throw new InputArgumentException("Cannot get poll list because no list pagination.");
        }

        if(query.getPollSearchCriteria() == null || query.getPollSearchCriteria().getCriterias() == null) {
        	throw new InputArgumentException("Cannot get poll list because criteria are null.");
        }
        final PollListResponse response = new PollListResponse();
        final List<PollResponseType> pollResponseList = new ArrayList<>();

        final Integer page = query.getPagination().getPage();
        final Integer listSize = query.getPagination().getListSize();
        final boolean isDynamic = query.getPollSearchCriteria().isIsDynamic();
        final List<PollSearchKeyValue> searchKeys = PollSearchMapper.createSearchFields(query.getPollSearchCriteria().getCriterias());
        
        final String countSql = PollSearchMapper.createCountSearchSql(searchKeys, isDynamic);
        final String sql = PollSearchMapper.createSelectSearchSql(searchKeys, isDynamic);

        final Long numberMatches = pollDao.getPollListSearchCount(countSql, searchKeys, isDynamic);
        final List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, searchKeys, isDynamic);
        
        for (final Poll poll : pollList) {
        	try {
        		final MobileTerminal mobileTerminalEntity = poll.getPollBase().getMobileTerminal();
        		final MobileTerminalType mobileTerminalType = mapPollableTerminalType(mobileTerminalEntity.getMobileTerminalType(), mobileTerminalEntity.getGuid());
        		final PollResponseType pollType = PollEntityToModelMapper.mapToPollResponseType(poll, mobileTerminalType, EnumMapper.getPollModelFromType(poll.getPollType()));
        		pollResponseList.add(pollType);
        	} catch (final EnumException e) {
        		LOG.error("[ Poll " + poll.getGuid() + "  couldn't map type ]");
        	}
        }

        int numberOfPages = (int) (numberMatches / listSize);
        if (numberMatches % listSize != 0) {
            numberOfPages += 1;
        }

        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(query.getPagination().getPage());
        response.getPollList().addAll(pollResponseList);
        return response;
    }
    
    public List<PollResponseType> getPollProgramList(final boolean onlyAlivePrograms) throws MobileTerminalModelException {
        final List<PollProgram> pollPrograms = pollProgramDao.getProgramPollsAlive();
        
        final List<PollResponseType> responseList = new ArrayList<>();
        for(final PollProgram pollProgram : pollPrograms) {
        	try {
        		final MobileTerminal terminal = pollProgram.getPollBase().getMobileTerminal();
        		final MobileTerminalType terminalType = mapPollableTerminalType(terminal.getMobileTerminalType(), terminal.getGuid());
        		responseList.add(PollEntityToModelMapper.mapToPollResponseType(pollProgram, terminalType));
        	} catch (final NoEntityFoundException e) {
        		LOG.error("[ Unvalid mobile terminal connected to poll program " + pollProgram.getGuid() + " ]");
        	    throw new MobileTerminalModelException("[ Unvalid mobile terminal connected to poll program " + pollProgram.getGuid() + " ]");
        	}
        }
        return responseList;
    }

    public List<PollResponseType> getPollProgramRunningAndStarted() throws MobileTerminalModelException {
        final List<PollProgram> pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();

        final List<PollResponseType> responseList = new ArrayList<>();
        for(final PollProgram pollProgram : pollPrograms) {
        	try {
        		final MobileTerminal terminal = pollProgram.getPollBase().getMobileTerminal();
        		final MobileTerminalType terminalType = mapPollableTerminalType(terminal.getMobileTerminalType(), terminal.getGuid());
        		responseList.add(PollEntityToModelMapper.mapToPollResponseType(pollProgram, terminalType));
        	} catch (final NoEntityFoundException e) {
        		LOG.error("[ Unvalid mobile terminal connected to poll program " + pollProgram.getGuid() + " ]");
        	    throw new MobileTerminalModelException("[ Unvalid mobile terminal connected to poll program " + pollProgram.getGuid() + " ]");
        	}
        }
        return responseList;
    }

    public PollResponseType setStatusPollProgram(final PollId id, final PollStatus state) throws MobileTerminalModelException {
        if (id == null || id.getGuid() == null || id.getGuid().isEmpty()) {
            throw new InputArgumentException("No poll id given");
        }
        if (state == null) {
            throw new InputArgumentException("No status to set");
        }
        
        try {
            final PollProgram program = pollProgramDao.getPollProgramByGuid(id.getGuid());
            final MobileTerminal terminal = program.getPollBase().getMobileTerminal();
            final MobileTerminalType terminalType = mapPollableTerminalType(terminal.getMobileTerminalType(), terminal.getGuid());
            
            switch (program.getPollState()) {
            case ARCHIVED:
                throw new MobileTerminalModelException("Can not change status of archived program poll, id: [ " + id.getGuid() + " ]");
            case STARTED:
            case STOPPED:
            }

            // TODO
            // check terminal/comchannel?

            program.setPollState(EnumMapper.getPollStateTypeFromModel(state));

            return PollEntityToModelMapper.mapToPollResponseType(program, terminalType);
        } catch (final EnumException e) {
            LOG.error("[ Error when setting poll program status. ] {}", e.getMessage());
            throw new MobileTerminalModelException(e.getMessage());
        } catch (final NoEntityFoundException e) {
        	LOG.error("[ Unvalid mobile terminal connected to poll program ]");
        	throw new MobileTerminalModelException("[ Unvalid mobile terminal connected to poll program ]");
        }
    }

    public ListResponseDto getMobileTerminalPollableList(final PollableQuery query) throws MobileTerminalModelException {
        if (query == null) {
            throw new InputArgumentException("No query");
        }

        if (query.getPagination() == null) {
            throw new InputArgumentException("No list pagination");
        }

        final ListResponseDto response = new ListResponseDto();
        final List<MobileTerminalType> mobileTerminalList = new ArrayList<MobileTerminalType>();

        final Integer page = query.getPagination().getPage();
        final Integer listSize = query.getPagination().getListSize();
        final int startIndex = (page-1)*listSize;
        int stopIndex = startIndex+listSize;
        LOG.debug("page: " + page + ", listSize: " + listSize + ", startIndex: " + startIndex);
        
        final List<String> idList = query.getConnectIdList();
        final long in = System.currentTimeMillis();

        //Long numberMatches = channelInmarsatCDao.getPollableListSearchCount(countSql, idList);
        final List<Channel> channels = channelDao.getPollableListSearch(idList);
        
        for (final Channel comchannel : channels) {
        	//TODO slim response from Pollable
        	final MobileTerminalType terminal = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(comchannel.getMobileTerminal(), comchannel);
            mobileTerminalList.add(terminal);
        }
        
        final int numberMatches = mobileTerminalList.size();
        
        int numberOfPages = numberMatches / listSize;
        if (numberMatches % listSize != 0) {
            numberOfPages += 1;
        }

        if((numberMatches-1) <= 0) {
        	response.setMobileTerminalList(mobileTerminalList);
        } else {
        	if(stopIndex >= numberMatches) {
            	stopIndex = numberMatches;
            }
        	LOG.debug("stopIndex: " + stopIndex);
            final List<MobileTerminalType> newList = new ArrayList<>(mobileTerminalList.subList(startIndex, stopIndex));
        	response.setMobileTerminalList(newList);
        }
        
        
        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(query.getPagination().getPage());
        
        final long out = System.currentTimeMillis();
        LOG.debug("Get pollable channels " + (out-in) + " ms");
        return response;

    }

}