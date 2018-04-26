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
import javax.ejb.Stateless;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAssignQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalHistory;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalStatus;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.MobileTerminalDomainModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollDomainModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.consumer.MessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

@Stateless
public class MobileTerminalServiceBean implements MobileTerminalService {

    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalServiceBean.class);

    @EJB
    private MessageProducer messageProducer;

    @EJB
    private MessageConsumer messageConsumer;

    @EJB
    private PluginService pluginService;

    @EJB
    private MobileTerminalDomainModelBean mobileTerminalModel;

    @EJB
    private ConfigModelBean configModel;

    @EJB
    private PollDomainModelBean pollModel;
    
    /**
     * {@inheritDoc}
     *
     * @param mobileTerminal
     * @throws MobileTerminalServiceException
     */
    @Override
    public MobileTerminalType createMobileTerminal(MobileTerminalType mobileTerminal, MobileTerminalSource source, String username) throws MobileTerminalException {
        LOG.debug("CREATE MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        mobileTerminal.setSource(source);
        MobileTerminalType createdMobileTerminal = mobileTerminalModel.createMobileTerminal(mobileTerminal, username);
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
    @Override
    public MobileTerminalListResponse getMobileTerminalList(MobileTerminalListQuery query) throws MobileTerminalException {
        LOG.debug("GET MOBILE TERMINAL LIST INVOKED IN SERVICE LAYER");
        ListResponseDto listResponse = mobileTerminalModel.getTerminalListByQuery(query);
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
    @Override
    public MobileTerminalType getMobileTerminalById(String guid) throws MobileTerminalException {
        LOG.debug("GET MOBILE TERMINAL BY ID INVOKED IN SERVICE LAYER");
        if (guid == null) {
            throw new InputArgumentException("No id");
        }
        MobileTerminalId id = new MobileTerminalId();
        id.setGuid(guid);
        MobileTerminalType terminalGet = mobileTerminalModel.getMobileTerminalById(id);
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
    @Override
    public MobileTerminalType upsertMobileTerminal(MobileTerminalType data, MobileTerminalSource source, String username) throws MobileTerminalException {
        LOG.debug("UPSERT MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        if (data == null) {
            throw new InputArgumentException("No Mobile terminal to update [ NULL ]");
        }
        data.setSource(source);
        MobileTerminalType terminalUpserted = mobileTerminalModel.upsertMobileTerminal(data, username);
        
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
    @Override
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
    @Override
    public MobileTerminalType updateMobileTerminal(MobileTerminalType mobileTerminal, String comment, MobileTerminalSource source, String username)
            throws MobileTerminalException {
        LOG.debug("UPDATE MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        mobileTerminal.setSource(source);
        MobileTerminalType terminalUpdate = mobileTerminalModel.updateMobileTerminal(mobileTerminal, comment, username);
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

    @Override
    public MobileTerminalType assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException {
        LOG.debug("ASSIGN MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        MobileTerminalType terminalAssign = mobileTerminalModel.assignMobileTerminalToCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalAssigned(terminalAssign.getMobileTerminalId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was assigned", terminalAssign.getMobileTerminalId()
                    .getGuid());
        }

        return terminalAssign;
    }

    @Override
    public MobileTerminalType unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException {
        LOG.debug("UNASSIGN MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        MobileTerminalType terminalUnAssign = mobileTerminalModel.unAssignMobileTerminalFromCarrier(query, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUnassigned(terminalUnAssign.getMobileTerminalId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was unassigned", terminalUnAssign.getMobileTerminalId()
                    .getGuid());
        }

        return terminalUnAssign;
    }

    @Override
    public MobileTerminalType setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username)
            throws MobileTerminalException {
        LOG.debug("SET STATUS OF MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        MobileTerminalType terminalStatus = mobileTerminalModel.setStatusMobileTerminal(terminalId, comment, status, username);
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

    @Override
    public MobileTerminalHistory getMobileTerminalHistoryList(String guid) throws MobileTerminalException {
        LOG.debug("GET HISTORY OF MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        MobileTerminalId terminalId = new MobileTerminalId();
        terminalId.setGuid(guid);
        MobileTerminalHistory historyList = mobileTerminalModel.getMobileTerminalHistoryList(terminalId);
        return historyList;
    }

    @Override
    public MobileTerminalListResponse getPollableMobileTerminal(PollableQuery query) throws MobileTerminalException {
        LOG.debug("Get pollable mobile terminals");
        ListResponseDto listResponse = pollModel.getMobileTerminalPollableList(query);
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(listResponse.getCurrentPage());
        response.setTotalNumberOfPages(listResponse.getTotalNumberOfPages());
        response.getMobileTerminal().addAll(listResponse.getMobileTerminalList());
        return response;
    }
}
