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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.consumer.MessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.PollDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

@Stateless
public class MobileTerminalServiceBean implements MobileTerminalService {

    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalServiceBean.class);

    @EJB
    MessageProducer messageProducer;

    @EJB
    MessageConsumer reciever;

    @EJB
    PluginService pluginService;
    
    /**
     * {@inheritDoc}
     *
     * @param mobileTerminal
     * @throws MobileTerminalServiceException
     */
    @Override
    public MobileTerminalType createMobileTerminal(MobileTerminalType mobileTerminal, MobileTerminalSource source, String username) throws MobileTerminalException {
        LOG.debug("CREATE MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        String data = MobileTerminalDataSourceRequestMapper.mapGetCreateMobileTerminal(mobileTerminal, source, username);
        String messageId = messageProducer.sendDataSourceMessage(data, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);

        MobileTerminalType createdMobileTerminal = MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
        boolean dnidUpdated = MobileTerminalDataSourceResponseMapper.mapDNIDUpdatedMobileTerminalResponse(response, messageId);
        
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
        String data = MobileTerminalDataSourceRequestMapper.mapGetMobileTerminalList(query);
        String messageId = messageProducer.sendDataSourceMessage(data, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        return MobileTerminalDataSourceResponseMapper.mapToMobileTerminalListFromResponse(response, messageId);
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
        String data = MobileTerminalDataSourceRequestMapper.mapGetMobileTerminal(guid);
        String messageId = messageProducer.sendDataSourceMessage(data, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        return MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
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
        String sendData = MobileTerminalDataSourceRequestMapper.mapUpsertMobileTerminal(data, source, username);
        String messageId = messageProducer.sendDataSourceMessage(sendData, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        
        boolean dnidUpdated = MobileTerminalDataSourceResponseMapper.mapDNIDUpdatedMobileTerminalResponse(response, messageId);
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(data.getPlugin().getServiceName());
        }
        
        return MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
    }

    /**
     * {@inheritDoc}
     *
     * @param id
     * @throws MobileTerminalServiceException
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public MobileTerminalType getMobileTerminalById(MobileTerminalId id, DataSourceQueue queue) throws MobileTerminalException {
        LOG.debug("GET MOBILE TERMINAL BY ID ( FROM SPECIFIC QUEUE ) INVOKED IN SERVICE LAYER, QUEUE = ", queue.name());
        if (id == null) {
            throw new InputArgumentException("No id");
        }
        String data = MobileTerminalDataSourceRequestMapper.mapGetMobileTerminal(id);
        String messageId = messageProducer.sendDataSourceMessage(data, queue);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
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
        String data = MobileTerminalDataSourceRequestMapper.mapUpdateMobileTerminal(mobileTerminal, comment, source, username);
        String messageId = messageProducer.sendDataSourceMessage(data, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);

        MobileTerminalType updatedMobileTerminal = MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUpdated(updatedMobileTerminal.getMobileTerminalId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was updated", updatedMobileTerminal.getMobileTerminalId()
                    .getGuid());
        }
        
        boolean dnidUpdated = MobileTerminalDataSourceResponseMapper.mapDNIDUpdatedMobileTerminalResponse(response, messageId);
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(updatedMobileTerminal.getPlugin().getServiceName());
        }
        
        return updatedMobileTerminal;
    }

    @Override
    public MobileTerminalType assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException {
        LOG.debug("ASSIGN MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        String data = MobileTerminalDataSourceRequestMapper.mapGetAssignMobileTerminal(query, comment, username);
        String messageId = messageProducer.sendDataSourceMessage(data, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);

        MobileTerminalType assignedMobileTerminal = MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalAssigned(assignedMobileTerminal.getMobileTerminalId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was assigned", assignedMobileTerminal.getMobileTerminalId()
                    .getGuid());
        }

        return assignedMobileTerminal;
    }

    @Override
    public MobileTerminalType unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException {
        LOG.debug("UNASSIGN MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        String data = MobileTerminalDataSourceRequestMapper.mapGetUnAssignMobileTerminal(query, comment, username);
        String messageId = messageProducer.sendDataSourceMessage(data, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);

        MobileTerminalType unassignedMobileTerminal = MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUnassigned(unassignedMobileTerminal.getMobileTerminalId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was unassigned", unassignedMobileTerminal.getMobileTerminalId()
                    .getGuid());
        }

        return unassignedMobileTerminal;
    }

    @Override
    public MobileTerminalType setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username)
            throws MobileTerminalException {
        LOG.debug("SET STATUS OF MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        String data = MobileTerminalDataSourceRequestMapper.mapSetStatus(terminalId, comment, status, username);
        String messageId = messageProducer.sendDataSourceMessage(data, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);

        MobileTerminalType mobileTerminalWithNewStatus = MobileTerminalDataSourceResponseMapper.mapToMobileTerminalFromResponse(response, messageId);
        try {
            String auditData = null;
            switch (status) {
            case ACTIVE:
                auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalActivated(mobileTerminalWithNewStatus.getMobileTerminalId().getGuid(),comment, username);
                break;
            case INACTIVE:
                auditData = AuditModuleRequestMapper
                .mapAuditLogMobileTerminalInactivated(mobileTerminalWithNewStatus.getMobileTerminalId().getGuid(), comment, username);
                break;
            case ARCHIVE:
                auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalArchived(mobileTerminalWithNewStatus.getMobileTerminalId().getGuid(), comment,username);
                break;
            default:
                break;
            }
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was set to status {}", mobileTerminalWithNewStatus
                    .getMobileTerminalId().getGuid(), status);
        }

        boolean dnidUpdated = MobileTerminalDataSourceResponseMapper.mapDNIDUpdatedMobileTerminalResponse(response, messageId);
        if(dnidUpdated) {
        	pluginService.processUpdatedDNIDList(mobileTerminalWithNewStatus.getPlugin().getServiceName());
        }
        
        return mobileTerminalWithNewStatus;
    }

    @Override
    public List<MobileTerminalHistory> getMobileTerminalHistoryList(String guid) throws MobileTerminalException {
        LOG.debug("GET HISTORY OF MOBILE TERMINAL INVOKED IN SERVICE LAYER");
        String data = MobileTerminalDataSourceRequestMapper.mapGetHistory(guid);
        String messageId = messageProducer.sendDataSourceMessage(data, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        return MobileTerminalDataSourceResponseMapper.mapToHistoryList(response, messageId);
    }

    @Override
    public MobileTerminalListResponse getPollableMobileTerminal(PollableQuery query) throws MobileTerminalException {
        LOG.debug("Get pollable mobile terminals");
        String data = PollDataSourceRequestMapper.mapGetPollableRequest(query);
        String messageId = messageProducer.sendDataSourceMessage(data, DataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        return MobileTerminalDataSourceResponseMapper.mapToMobileTerminalListFromResponse(response, messageId);
    }
}