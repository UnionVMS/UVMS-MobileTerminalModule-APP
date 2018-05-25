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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constant.MobileTerminalTypeComparator;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.HistoryMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalModelToEntityMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.SearchMapper;
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
public class MobileTerminalDomainModelBean {

    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalDomainModelBean.class);

    @EJB
    private TerminalDaoBean terminalDao;

    @EJB
    private MobileTerminalPluginDaoBean pluginDao;
    
    public MobileTerminal getMobileTerminalEntityById(MobileTerminalId id) throws InputArgumentException, NoEntityFoundException {
    	if(id == null || id.getGuid() == null || id.getGuid().isEmpty()) throw new InputArgumentException("Non valid id");
    	return terminalDao.getMobileTerminalByGuid(id.getGuid());
    }

    public MobileTerminal getMobileTerminalEntityBySerialNo(String serialNo) throws InputArgumentException, NoEntityFoundException {
        if(serialNo == null || serialNo.isEmpty()) throw new InputArgumentException("Non valid serial no");
        return terminalDao.getMobileTerminalBySerialNo(serialNo);
    }
    
    public MobileTerminalType createMobileTerminal(MobileTerminalType mobileTerminal, String username) throws MobileTerminalModelException {
        try {
            assertTerminalNotExists(mobileTerminal);
            String serialNumber = assertTerminalHasNeededData(mobileTerminal);

            MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(mobileTerminal.getPlugin().getServiceName());

            MobileTerminal terminal = MobileTerminalModelToEntityMapper.mapNewMobileTerminalEntity(mobileTerminal, serialNumber, plugin, username);
            terminalDao.createMobileTerminal(terminal);
            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
        } catch (TerminalDaoException e) {
            throw new MobileTerminalModelException("Error when persisting terminal " + mobileTerminal.getMobileTerminalId());
        } catch (MobileTerminalModelException e) {
            LOG.error("Error in model when creating mobile terminal: {}", e.getMessage());
            throw e;
        }
    }

    private String assertTerminalHasNeededData(MobileTerminalType mobileTerminal) throws MobileTerminalModelException {
        String serialNumber = null;
        for (MobileTerminalAttribute attribute : mobileTerminal.getAttributes()) {
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

    private void assertTerminalNotExists(MobileTerminalType mobileTerminal) throws MobileTerminalModelException {
        try {
        	MobileTerminal terminal = getMobileTerminalEntityById(mobileTerminal.getMobileTerminalId());
            throw new MobileTerminalModelException("Mobile terminal already exists in database for id: " + mobileTerminal.getMobileTerminalId());
        } catch (InputArgumentException | NoEntityFoundException e) {
            //Terminal does not exist, ok to create a new one
        }
        try {
            for (MobileTerminalAttribute attribute : mobileTerminal.getAttributes()) {
                if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(attribute.getType())) {
                    MobileTerminal terminal = getMobileTerminalEntityBySerialNo(attribute.getValue());
                    if (!terminal.getArchived()) {
                        throw new MobileTerminalModelException("Mobile terminal already exists in database for serial number: " + attribute.getValue());
                    }
                }
            }
        } catch (InputArgumentException | NoEntityFoundException e) {
            //Terminal does not exist, ok to create a new one
        }
    }

    public MobileTerminalType getMobileTerminalById(MobileTerminalId id) throws MobileTerminalModelException {
        if (id == null) {
            throw new InputArgumentException("No id to fetch");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(id);
        return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
    }
    
    public MobileTerminalType updateMobileTerminal(MobileTerminalType model, String comment, String username) throws MobileTerminalModelException {
        if (model == null) {
            throw new InputArgumentException("No terminal to update");
        }
        if (model.getMobileTerminalId() == null || model.getMobileTerminalId().getGuid() == null || model.getMobileTerminalId().getGuid().isEmpty()) {
            throw new InputArgumentException("Non valid id of terminal to update");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(model.getMobileTerminalId());
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

        String serialNumber = assertTerminalHasNeededData(model);

        //TODO check type
        if(terminal.getMobileTerminalType() != null) {
            MobileTerminal updatedTerminal = MobileTerminalModelToEntityMapper.mapMobileTerminalEntity(terminal, model, serialNumber, updatedPlugin, username, comment, EventCodeEnum.MODIFY);
            terminalDao.updateMobileTerminal(updatedTerminal);
            return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(updatedTerminal);

        }
        throw new MobileTerminalModelException("Update - Not supported mobile terminal type");
    }

    public MobileTerminalType assignMobileTerminalToCarrier(MobileTerminalAssignQuery query, String comment,String username) throws MobileTerminalModelException {
        if (query == null) {
            throw new InputArgumentException("RequestQuery is null");
        }
        if (query.getMobileTerminalId() == null) {
            throw new InputArgumentException("No Mobile terminalId in request");
        }
        if (query.getConnectId() == null || query.getConnectId().isEmpty()) {
            throw new InputArgumentException("No connect id in request");
        }

        MobileTerminalId mobTermId = query.getMobileTerminalId();
        String connectId = query.getConnectId();
        
        MobileTerminal terminal = getMobileTerminalEntityById(mobTermId);
        String currentConnectId = terminal.getCurrentEvent().getConnectId();
        if (currentConnectId == null || currentConnectId.isEmpty()) {
            MobileTerminalEvent current = terminal.getCurrentEvent();
            current.setActive(false);
            MobileTerminalEvent event = new MobileTerminalEvent();
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

    public MobileTerminalType unAssignMobileTerminalFromCarrier(MobileTerminalAssignQuery query, String comment,String username) throws MobileTerminalModelException {
        if (query == null) {
            throw new InputArgumentException("RequestQuery is null");
        }
        if (query.getMobileTerminalId() == null) {
            throw new InputArgumentException("No Mobile terminalId in request");
        }
        if (query.getConnectId() == null || query.getConnectId().isEmpty()) {
            throw new InputArgumentException("No connect id in requesst");
        }

        MobileTerminalId mobTermId = query.getMobileTerminalId();
        String connectId = query.getConnectId();

        MobileTerminal terminal = getMobileTerminalEntityById(mobTermId);
        String currentConnectId = terminal.getCurrentEvent().getConnectId();
        if (currentConnectId != null && currentConnectId.equals(connectId)) {
            MobileTerminalEvent current = terminal.getCurrentEvent();
            current.setActive(false);
            MobileTerminalEvent event = new MobileTerminalEvent();
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

    public MobileTerminalType upsertMobileTerminal(MobileTerminalType mobileTerminal,String username) throws MobileTerminalModelException {

        if (mobileTerminal == null) {
            throw new InputArgumentException("RequestQuery is null");
        }
        if (mobileTerminal.getMobileTerminalId() == null) {
            throw new InputArgumentException("No Mobile terminalId in request");
        }

        try {
            return updateMobileTerminal(mobileTerminal, "Upserted by external module", username);
        } catch (NumberFormatException | MobileTerminalModelException e) {
            LOG.error("[ Error when upserting mobile terminal: Mobile terminal update failed trying to insert. ] {} {}", e.getMessage(), e.getStackTrace());
            if (e instanceof MobileTerminalExistsException) {
                throw e;
            }
        }
        return createMobileTerminal(mobileTerminal, username);
    }

    public MobileTerminalType setStatusMobileTerminal(MobileTerminalId id, String comment, MobileTerminalStatus status, String username) throws MobileTerminalModelException {
        if (id == null) {
            throw new InputArgumentException("No Mobile Terminal");
        }
        if (status == null) {
            throw new InputArgumentException("No terminal status to set");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(id);

        MobileTerminalEvent current = terminal.getCurrentEvent();
        current.setActive(false);

        MobileTerminalEvent event = new MobileTerminalEvent();
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

    public MobileTerminalHistory getMobileTerminalHistoryList(MobileTerminalId id) throws MobileTerminalModelException {
        if (id == null) {
            throw new InputArgumentException("No Mobile Terminal");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(id);

        return HistoryMapper.getHistory(terminal);
    }

    public ListResponseDto getTerminalListByQuery(MobileTerminalListQuery query) throws MobileTerminalModelException {
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


        ListResponseDto response = new ListResponseDto();
        List<MobileTerminalType> mobileTerminalList = new ArrayList<>();

        Integer page = query.getPagination().getPage();
        Integer listSize = query.getPagination().getListSize();
        int startIndex = (page-1)*listSize;
        int stopIndex = startIndex+listSize;
        LOG.debug("page: " + page + ", listSize: " + listSize + ", startIndex: " + startIndex);
        
        boolean isDynamic = query.getMobileTerminalSearchCriteria().isIsDynamic() == null ? true : query.getMobileTerminalSearchCriteria().isIsDynamic();

        List<ListCriteria> criterias = query.getMobileTerminalSearchCriteria().getCriterias();

        String searchSql = SearchMapper.createSelectSearchSql(criterias, isDynamic);

        List<MobileTerminal> terminals = terminalDao.getMobileTerminalsByQuery(searchSql);

        for (MobileTerminal terminal : terminals) {
            MobileTerminalType terminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
            mobileTerminalList.add(terminalType);
        }

        Collections.sort(mobileTerminalList, new MobileTerminalTypeComparator());
        int totalMatches = mobileTerminalList.size();
        LOG.debug("totalMatches: " + totalMatches);

        int numberOfPages =  totalMatches / listSize;
        if (totalMatches % listSize != 0) {
            numberOfPages += 1;
        }
        response.setMobileTerminalList(mobileTerminalList);

        if ((totalMatches - 1) > 0) {
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
