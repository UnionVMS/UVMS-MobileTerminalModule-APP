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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalTypeComparator;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.consumer.MessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
@LocalBean
public class MobileTerminalServiceBean {

    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalServiceBean.class);

    @EJB
    private MessageProducer messageProducer;

    @EJB
    private MessageConsumer messageConsumer;

    @EJB
    private PluginServiceBean pluginService;


    @EJB
    private ConfigServiceBean configModel;

    @EJB
    private PollDomainModelBean pollModel;

    @EJB
    private TerminalDaoBean terminalDao;

    @EJB
    private MobileTerminalPluginDaoBean pluginDao;


    /**
     * {@inheritDoc}
     *
     * @param mobileTerminal
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType createMobileTerminal(MobileTerminalType mobileTerminal, MobileTerminalSource source, String username) throws MobileTerminalException {
        LOG.debug("CREATE MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        mobileTerminal.setSource(source);
        MobileTerminalType createdMobileTerminal = createMobileTerminal(mobileTerminal, username);
        boolean dnidUpdated = configModel.checkDNIDListChange(createdMobileTerminal.getPlugin().getServiceName());
        
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalCreated(createdMobileTerminal.getMobileTerminalId().getGuid(), username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was created", createdMobileTerminal.getMobileTerminalId()
                    .getGuid());
        }
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(createdMobileTerminal.getPlugin().getServiceName());
        }
        
        return createdMobileTerminal;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalListResponse getMobileTerminalList(MobileTerminalListQuery query) throws MobileTerminalException {
        LOG.debug("GET MOBILE TERMINAL LIST INVOKED IN SERVICE LAYER");
        ListResponseDto listResponse = getTerminalListByQuery(query);
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(listResponse.getCurrentPage());
        response.setTotalNumberOfPages(listResponse.getTotalNumberOfPages());
        response.getMobileTerminal().addAll(listResponse.getMobileTerminalList());
        return response;
    }

    /**
     * {@inheritDoc}
     *
     * @param guid
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType getMobileTerminalById(String guid) throws MobileTerminalException {
        LOG.debug("GET MOBILE TERMINAL BY ID INVOKED IN SERVICE LAYER");
        if (guid == null) {
            throw new InputArgumentException("No id");
        }
        MobileTerminalId id = new MobileTerminalId();
        id.setGuid(guid);
        MobileTerminalType terminalGet = getMobileTerminalById(id);
        return terminalGet;
    }

    /**
     *
     * {@inheritDoc}
     *
     * @param data
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType upsertMobileTerminal(MobileTerminalType data, MobileTerminalSource source, String username) throws MobileTerminalException {
        LOG.debug("UPSERT MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        if (data == null) {
            throw new InputArgumentException("No Mobile terminal to update [ NULL ]");
        }
        data.setSource(source);
        MobileTerminalType terminalUpserted = upsertMobileTerminal(data, username);
        
        boolean dnidUpdated = configModel.checkDNIDListChange(terminalUpserted.getPlugin().getServiceName());
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(data.getPlugin().getServiceName());
        }
        
        return terminalUpserted;
    }

    /**
     * {@inheritDoc}
     *
     * @param id
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType getMobileTerminalById(MobileTerminalId id, DataSourceQueue queue) throws MobileTerminalException {
        LOG.debug("GET MOBILE TERMINAL BY ID ( FROM SPECIFIC QUEUE ) INVOKED IN SERVICE LAYER, QUEUE = ", queue.name());
        if (id == null) {
            throw new InputArgumentException("No id");
        }
        if (queue != null && queue.equals(DataSourceQueue.INTERNAL)) {
            return getMobileTerminalById(id.getGuid());
        }
        String data = MobileTerminalDataSourceRequestMapper.mapGetMobileTerminal(id);
        String messageId = messageProducer.sendDataSourceMessage(data, queue);
        TextMessage response = messageConsumer.getMessage(messageId, TextMessage.class);
        return MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
    }

    /**
     * {@inheritDoc}
     *
     * @param mobileTerminal
     * @param source
     */
    public MobileTerminalType updateMobileTerminal(MobileTerminalType mobileTerminal, String comment, MobileTerminalSource source, String username)
            throws MobileTerminalException {
        LOG.debug("UPDATE MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        mobileTerminal.setSource(source);
        MobileTerminalType terminalUpdate = updateMobileTerminal(mobileTerminal, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUpdated(terminalUpdate.getMobileTerminalId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was updated", terminalUpdate.getMobileTerminalId()
                    .getGuid());
        }
        
        boolean dnidUpdated = configModel.checkDNIDListChange(terminalUpdate.getPlugin().getServiceName());
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(terminalUpdate.getPlugin().getServiceName());
        }
        
        return terminalUpdate;
    }

    public MobileTerminalType assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException {
        LOG.debug("ASSIGN MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        MobileTerminalType terminalAssign = assignMobileTerminalToCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalAssigned(terminalAssign.getMobileTerminalId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was assigned", terminalAssign.getMobileTerminalId()
                    .getGuid());
        }

        return terminalAssign;
    }

    public MobileTerminalType unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException {
        LOG.debug("UNASSIGN MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        MobileTerminalType terminalUnAssign = unAssignMobileTerminalFromCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUnassigned(terminalUnAssign.getMobileTerminalId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was unassigned", terminalUnAssign.getMobileTerminalId()
                    .getGuid());
        }

        return terminalUnAssign;
    }

    public MobileTerminalType setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username)
            throws MobileTerminalException {
        LOG.debug("SET STATUS OF MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        MobileTerminalType terminalStatus = setStatusMobileTerminal(terminalId, comment, status, username);
        try {
            String auditData = null;
            switch (status) {
            case ACTIVE:
                auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalActivated(terminalStatus.getMobileTerminalId().getGuid(),comment, username);
                break;
            case INACTIVE:
                auditData = AuditModuleRequestMapper
                .mapAuditLogMobileTerminalInactivated(terminalStatus.getMobileTerminalId().getGuid(), comment, username);
                break;
            case ARCHIVE:
                auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalArchived(terminalStatus.getMobileTerminalId().getGuid(), comment,username);
                break;
            default:
                break;
            }
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was set to status {}", terminalStatus
                    .getMobileTerminalId().getGuid(), status);
        }

        boolean dnidUpdated = configModel.checkDNIDListChange(terminalStatus.getPlugin().getServiceName());
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(terminalStatus.getPlugin().getServiceName());
        }
        
        return terminalStatus;
    }

    public MobileTerminalHistory getMobileTerminalHistoryList(String guid) throws MobileTerminalException {
        LOG.debug("GET HISTORY OF MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        MobileTerminalId terminalId = new MobileTerminalId();
        terminalId.setGuid(guid);
        MobileTerminalHistory historyList = getMobileTerminalHistoryList(terminalId);
        return historyList;
    }

    public MobileTerminalListResponse getPollableMobileTerminal(PollableQuery query) throws MobileTerminalException {
        LOG.debug("Get pollable mobile terminals");
        ListResponseDto listResponse = pollModel.getMobileTerminalPollableList(query);
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(listResponse.getCurrentPage());
        response.setTotalNumberOfPages(listResponse.getTotalNumberOfPages());
        response.getMobileTerminal().addAll(listResponse.getMobileTerminalList());
        return response;
    }

    /***************************************************************************************************************************/

    public MobileTerminal getMobileTerminalEntityById(MobileTerminalId id) throws eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException, NoEntityFoundException {
        if(id == null || id.getGuid() == null || id.getGuid().isEmpty()) throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("Non valid id");
        return terminalDao.getMobileTerminalByGuid(id.getGuid());
    }

    public MobileTerminal getMobileTerminalEntityBySerialNo(String serialNo) throws eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException, NoEntityFoundException {
        if(serialNo == null || serialNo.isEmpty()) throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("Non valid serial no");
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
        } catch (eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException | NoEntityFoundException e) {
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
        } catch (eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException | NoEntityFoundException e) {
            //Terminal does not exist, ok to create a new one
        }
    }

    public MobileTerminalType getMobileTerminalById(MobileTerminalId id) throws MobileTerminalModelException {
        if (id == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No id to fetch");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(id);
        return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
    }

    public MobileTerminalType updateMobileTerminal(MobileTerminalType model, String comment, String username) throws MobileTerminalModelException {
        if (model == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No terminal to update");
        }
        if (model.getMobileTerminalId() == null || model.getMobileTerminalId().getGuid() == null || model.getMobileTerminalId().getGuid().isEmpty()) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("Non valid id of terminal to update");
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

    public MobileTerminalType assignMobileTerminalToCarrier(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalModelException {
        if (query == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("RequestQuery is null");
        }
        if (query.getMobileTerminalId() == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No Mobile terminalId in request");
        }
        if (query.getConnectId() == null || query.getConnectId().isEmpty()) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No connect id in request");
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
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("RequestQuery is null");
        }
        if (query.getMobileTerminalId() == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No Mobile terminalId in request");
        }
        if (query.getConnectId() == null || query.getConnectId().isEmpty()) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No connect id in requesst");
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
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("RequestQuery is null");
        }
        if (mobileTerminal.getMobileTerminalId() == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No Mobile terminalId in request");
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

    /*
    public MobileTerminalType setStatusMobileTerminal______2(MobileTerminalId id, String comment, MobileTerminalStatus status, String username) throws MobileTerminalModelException {
        if (id == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No Mobile Terminal");
        }
        if (status == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No terminal status to set");
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
    */

    public MobileTerminalHistory getMobileTerminalHistoryList(MobileTerminalId id) throws MobileTerminalModelException {
        if (id == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No Mobile Terminal");
        }

        MobileTerminal terminal = getMobileTerminalEntityById(id);

        return HistoryMapper.getHistory(terminal);
    }

    public ListResponseDto getTerminalListByQuery(MobileTerminalListQuery query) throws MobileTerminalModelException {
        if (query == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No list query");
        }
        if (query.getPagination() == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No list pagination");
        }
        if (query.getMobileTerminalSearchCriteria() == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No list criteria");
        }
        if (query.getMobileTerminalSearchCriteria().getCriterias() == null) {
            throw new eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException("No list criteria");
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
