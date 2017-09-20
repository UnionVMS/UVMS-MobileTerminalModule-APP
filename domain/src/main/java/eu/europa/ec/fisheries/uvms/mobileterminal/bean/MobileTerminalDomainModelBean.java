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

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalTypeComparator;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.HistoryMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.MobileTerminalEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.MobileTerminalModelToEntityMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.SearchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
@LocalBean
public class MobileTerminalDomainModelBean  {

    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalDomainModelBean.class);

    @EJB
    TerminalDao terminalDao;
    
    @EJB
    ConfigModelBean config;

    @EJB
    MobileTerminalPluginDao pluginDao;
    
    public MobileTerminal getMobileTerminalEntityById(final MobileTerminalId id) throws InputArgumentException, NoEntityFoundException {
    	if(id == null || id.getGuid() == null || id.getGuid().isEmpty()) throw new InputArgumentException("Non valid id");
    	return terminalDao.getMobileTerminalByGuid(id.getGuid());
    }

    public MobileTerminal getMobileTerminalEntityBySerialNo(final String serialNo) throws InputArgumentException, NoEntityFoundException {
        if(serialNo == null || serialNo.isEmpty()) throw new InputArgumentException("Non valid serial no");
        return terminalDao.getMobileTerminalBySerialNo(serialNo);
    }
    
    public MobileTerminalType createMobileTerminal(final MobileTerminalType mobileTerminal, final String username) throws MobileTerminalModelException {
        try {
            assertTerminalNotExists(mobileTerminal);
            final String serialNumber = assertTerminalHasNeededData(mobileTerminal);

            final MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(mobileTerminal.getPlugin().getServiceName());

            final MobileTerminal terminal = MobileTerminalModelToEntityMapper.mapNewMobileTerminalEntity(mobileTerminal, serialNumber, plugin, username);
            terminalDao.createMobileTerminal(terminal);
            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
        } catch (final TerminalDaoException e) {
            throw new MobileTerminalModelException("Error when persisting terminal " + mobileTerminal.getMobileTerminalId());
        } catch (final MobileTerminalModelException e) {
            LOG.error("Error in model when creating mobile terminal: {}", e.getMessage());
            throw e;
        }
    }

    private String assertTerminalHasNeededData(final MobileTerminalType mobileTerminal) throws MobileTerminalModelException {
        String serialNumber = null;
        for (final MobileTerminalAttribute attribute : mobileTerminal.getAttributes()) {
            if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(attribute.getType()) &&
                    attribute.getValue() != null && !attribute.getValue().isEmpty()) {
                serialNumber = attribute.getValue();
                break;
            }
        }

        if (serialNumber == null) {
            throw new MobileTerminalModelException("Cannot create mobile terminal without serial number");
        }
        if(mobileTerminal.getPlugin() == null){
            throw new MobileTerminalModelException("Cannot create Mobile terminal when plugin is not null");
        }

        return serialNumber;
    }

    private void assertTerminalNotExists(final MobileTerminalType mobileTerminal) throws MobileTerminalModelException {
        try {
        	final MobileTerminal terminal = getMobileTerminalEntityById(mobileTerminal.getMobileTerminalId());
            throw new MobileTerminalModelException("Mobile terminal already exists in database for id: " + mobileTerminal.getMobileTerminalId());
        } catch (InputArgumentException | NoEntityFoundException e) {
            //Terminal does not exist, ok to create a new one
        }
        try {
            for (final MobileTerminalAttribute attribute : mobileTerminal.getAttributes()) {
                if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(attribute.getType())) {
                    final MobileTerminal terminal = getMobileTerminalEntityBySerialNo(attribute.getValue());
                    if (!terminal.getArchived()) {
                        throw new MobileTerminalModelException("Mobile terminal already exists in database for serial number: " + attribute.getValue());
                    }
                }
            }
        } catch (InputArgumentException | NoEntityFoundException e) {
            //Terminal does not exist, ok to create a new one
        }
    }

    public MobileTerminalType getMobileTerminalById(final MobileTerminalId id) throws MobileTerminalModelException {
        if (id == null) {
            throw new InputArgumentException("No id to fetch");
        }

        final MobileTerminal terminal = getMobileTerminalEntityById(id);
        return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
    }
    
    public MobileTerminalType updateMobileTerminal(final MobileTerminalType model, final String comment, final String username) throws MobileTerminalModelException {
        if (model == null) {
            throw new InputArgumentException("No terminal to update");
        }
        if (model.getMobileTerminalId() == null || model.getMobileTerminalId().getGuid() == null || model.getMobileTerminalId().getGuid().isEmpty()) {
            throw new InputArgumentException("Non valid id of terminal to update");
        }

        final MobileTerminal terminal = getMobileTerminalEntityById(model.getMobileTerminalId());
        MobileTerminalPlugin updatedPlugin = null;

        if(model.getPlugin() != null && model.getPlugin().getLabelName() != null && terminal.getPlugin() != null) {
        	if(!model.getPlugin().getLabelName().equalsIgnoreCase(terminal.getPlugin().getName())) {
        		updatedPlugin = pluginDao.getPluginByServiceName(model.getPlugin().getServiceName());
        		terminal.setPlugin(updatedPlugin);
        	}
        }

        if (updatedPlugin == null) {
            updatedPlugin = terminal.getPlugin();
        }

        final String serialNumber = assertTerminalHasNeededData(model);

        //TODO check type
        if(terminal.getMobileTerminalType() != null) {
            final MobileTerminal updatedTerminal = MobileTerminalModelToEntityMapper.mapMobileTerminalEntity(terminal, model, serialNumber, updatedPlugin, username, comment, EventCodeEnum.MODIFY);
            terminalDao.updateMobileTerminal(updatedTerminal);
            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(updatedTerminal);

        }
        throw new MobileTerminalModelException("Update - Not supported mobile terminal type");
    }

    public MobileTerminalType assignMobileTerminalToCarrier(final MobileTerminalAssignQuery query, final String comment,final String username) throws MobileTerminalModelException {
        if (query == null) {
            throw new InputArgumentException("RequestQuery is null");
        }
        if (query.getMobileTerminalId() == null) {
            throw new InputArgumentException("No Mobile terminalId in request");
        }
        if (query.getConnectId() == null || query.getConnectId().isEmpty()) {
            throw new InputArgumentException("No connect id in requesst");
        }

        final MobileTerminalId mobTermId = query.getMobileTerminalId();
        final String connectId = query.getConnectId();
        
        final MobileTerminal terminal = getMobileTerminalEntityById(mobTermId);
        final String currentConnectId = terminal.getCurrentEvent().getConnectId();
        if (currentConnectId == null || currentConnectId.isEmpty()) {
            final MobileTerminalEvent current = terminal.getCurrentEvent();
            current.setActive(false);
            final MobileTerminalEvent event = new MobileTerminalEvent();
            event.setActive(true);
            event.setPollChannel(current.getPollChannel());
            event.setDefaultChannel(current.getDefaultChannel());
            event.setUpdateTime(DateUtils.getNowDateUTC());
            event.setConfigChannel(current.getConfigChannel());
            event.setAttributes(current.getAttributes());
            event.setComment(comment);
            event.setConnectId(connectId);
            event.setMobileTerminal(terminal);
            event.setUpdatedBy(username);
            event.setEventCodeType(EventCodeEnum.LINK);
            terminal.getMobileTerminalEvents().add(event);
            terminalDao.updateMobileTerminal(terminal);

            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
        }

        throw new MobileTerminalModelException("Terminal " + mobTermId + " is already linked to an asset with guid " + currentConnectId);
    }

    public MobileTerminalType unAssignMobileTerminalFromCarrier(final MobileTerminalAssignQuery query, final String comment,final String username) throws MobileTerminalModelException {
        if (query == null) {
            throw new InputArgumentException("RequestQuery is null");
        }
        if (query.getMobileTerminalId() == null) {
            throw new InputArgumentException("No Mobile terminalId in request");
        }
        if (query.getConnectId() == null || query.getConnectId().isEmpty()) {
            throw new InputArgumentException("No connect id in requesst");
        }

        final MobileTerminalId mobTermId = query.getMobileTerminalId();
        final String connectId = query.getConnectId();

        final MobileTerminal terminal = getMobileTerminalEntityById(mobTermId);
        final String currentConnectId = terminal.getCurrentEvent().getConnectId();
        if (currentConnectId != null && currentConnectId.equals(connectId)) {
            final MobileTerminalEvent current = terminal.getCurrentEvent();
            current.setActive(false);
            final MobileTerminalEvent event = new MobileTerminalEvent();
            event.setActive(true);
            event.setPollChannel(current.getPollChannel());
            event.setDefaultChannel(current.getDefaultChannel());
            event.setUpdateTime(DateUtils.getNowDateUTC());
            event.setConfigChannel(current.getConfigChannel());
            event.setAttributes(current.getAttributes());
            event.setComment(comment);
            event.setConnectId(null);
            event.setMobileTerminal(terminal);
            event.setUpdatedBy(username);
            event.setEventCodeType(EventCodeEnum.UNLINK);
            terminal.getMobileTerminalEvents().add(event);
            terminalDao.updateMobileTerminal(terminal);

            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
        }

        throw new MobileTerminalModelException("Terminal " + mobTermId + " is not linked to an asset with guid " + connectId);
    }

    public MobileTerminalType upsertMobileTerminal(final MobileTerminalType mobileTerminal,final String username) throws InputArgumentException, MobileTerminalModelException {

        if (mobileTerminal == null) {
            throw new InputArgumentException("RequestQuery is null");
        }
        if (mobileTerminal.getMobileTerminalId() == null) {
            throw new InputArgumentException("No Mobile terminalId in request");
        }

        try {
            final MobileTerminalType terminal = updateMobileTerminal(mobileTerminal, "Upserted by external module", username); //TODO comment?
            return terminal;
        } catch (NumberFormatException | MobileTerminalModelException e) {
            LOG.error("[ Error when upserting mobile terminal: Mobile terminal update failed trying to insert. ] {} {}", e.getMessage(), e.getStackTrace());
            if (e instanceof MobileTerminalExistsException) {
                throw e;
            }
        }

        return createMobileTerminal(mobileTerminal, username);
    }

    public MobileTerminalType setStatusMobileTerminal(final MobileTerminalId id, final String comment, final MobileTerminalStatus status, final String username) throws MobileTerminalModelException {
        if (id == null) {
            throw new InputArgumentException("No Mobile Terminal");
        }
        if (status == null) {
            throw new InputArgumentException("No terminal status to set");
        }

        final MobileTerminal terminal = getMobileTerminalEntityById(id);

        final MobileTerminalEvent current = terminal.getCurrentEvent();
        current.setActive(false);

        final MobileTerminalEvent event = new MobileTerminalEvent();
        event.setActive(true);
        event.setPollChannel(current.getPollChannel());
        event.setDefaultChannel(current.getDefaultChannel());
        event.setUpdateTime(DateUtils.getNowDateUTC());
        event.setConfigChannel(current.getConfigChannel());
        event.setAttributes(current.getAttributes());
        event.setComment(comment);
        event.setConnectId(current.getConnectId());
        event.setMobileTerminal(terminal);
        event.setUpdatedBy(username);
        switch (status) {
            case ACTIVE:
                event.setEventCodeType(EventCodeEnum.ACTIVATE);
                terminal.setInactivated(false);
                break;
            case INACTIVE:
                event.setEventCodeType(EventCodeEnum.INACTIVATE);
                terminal.setInactivated(true);
                break;
            case ARCHIVE:
                event.setEventCodeType(EventCodeEnum.ARCHIVE);
                terminal.setArchived(true);
                break;
            default:
                LOG.error("[ Non valid status to set ] {}", status);
                throw new MobileTerminalModelException("Non valid status to set");
        }

        terminal.getMobileTerminalEvents().add(event);
        terminalDao.updateMobileTerminal(terminal);
        
        return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
    }

    public MobileTerminalHistory getMobileTerminalHistoryList(final MobileTerminalId id) throws MobileTerminalModelException {
        if (id == null) {
            throw new InputArgumentException("No Mobile Terminal");
        }

        final MobileTerminal terminal = getMobileTerminalEntityById(id);

        return HistoryMapper.getHistory(terminal);
    }

    public ListResponseDto getTerminalListByQuery(final MobileTerminalListQuery query) throws MobileTerminalModelException {
        if (query == null) {
            throw new InputArgumentException("No list query");
        }
        if (query.getPagination() == null) {
            throw new InputArgumentException("No list pagination");
        }
        if (query.getMobileTerminalSearchCriteria() == null) {
            throw new InputArgumentException("No list criteria");
        }
        if (query.getMobileTerminalSearchCriteria().getCriterias() == null) {
            throw new InputArgumentException("No list criteria");
        }


        final ListResponseDto response = new ListResponseDto();
        final List<MobileTerminalType> mobileTerminalList = new ArrayList<MobileTerminalType>();

        final Integer page = query.getPagination().getPage();
        final Integer listSize = query.getPagination().getListSize();
        final int startIndex = (page-1)*listSize;
        int stopIndex = startIndex+listSize;
        LOG.debug("page: " + page + ", listSize: " + listSize + ", startIndex: " + startIndex);
        
        final boolean isDynamic = query.getMobileTerminalSearchCriteria().isIsDynamic() == null ? true : query.getMobileTerminalSearchCriteria().isIsDynamic();

        final List<ListCriteria> criterias = query.getMobileTerminalSearchCriteria().getCriterias();

        final String searchSql = SearchMapper.createSelectSearchSql(criterias, isDynamic);

        final List<MobileTerminal> terminals = terminalDao.getMobileTerminalsByQuery(searchSql);

        for (final MobileTerminal terminal : terminals) {
            final MobileTerminalType terminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
            mobileTerminalList.add(terminalType);
        }

        Collections.sort(mobileTerminalList, new MobileTerminalTypeComparator());
        final int totalMatches = mobileTerminalList.size();
        LOG.debug("totalMatches: " + totalMatches);

        int numberOfPages =  totalMatches / listSize;
        if (totalMatches % listSize != 0) {
            numberOfPages += 1;
        }

        response.setMobileTerminalList(mobileTerminalList);
        if((totalMatches-1) <= 0) {
        } else {
        	if(stopIndex >= totalMatches) {
            	stopIndex = totalMatches;
            }
        	LOG.debug("stopIndex: " + stopIndex);
        	response.setMobileTerminalList(new ArrayList<>(mobileTerminalList.subList(startIndex, stopIndex)));
        }
        
        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(page);

        
        return response;
    }
}