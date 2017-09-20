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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.AuditObjectTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.AuditOperationEnum;

public class AuditModuleRequestMapper {

    final static Logger LOG = LoggerFactory.getLogger(AuditModuleRequestMapper.class);

    public static String mapAuditLogMobileTerminalCreated(final String guid, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.CREATE.getValue(), guid, AuditOperationEnum.CREATE.getValue(), username);
    }

    public static String mapAuditLogMobileTerminalUpdated(final String guid, final String comment, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.UPDATE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalAssigned(final String guid, final String comment, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.LINKED.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalUnassigned(final String guid, final String comment, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.UNLINKED.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalActivated(final String guid, final String comment, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.ACTIVATE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalInactivated(final String guid, final String comment, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.INACTIVATE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogMobileTerminalArchived(final String guid, final String comment, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.MOBILE_TERMINAL.getValue(), AuditOperationEnum.ARCHIVE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogPollCreated(final PollType pollType, final String guid, final String comment, final String username) throws AuditModelMarshallException {
        AuditObjectTypeEnum pollTypeEnum;
        switch (pollType){
            case PROGRAM_POLL:
                pollTypeEnum = AuditObjectTypeEnum.PROGRAM_POLL;
                break;
            default:
                pollTypeEnum = AuditObjectTypeEnum.POLL;
                break;
        }
        return mapToAuditLog(pollTypeEnum.getValue(), AuditOperationEnum.CREATE.getValue(), guid, comment, username);
    }

    public static String mapAuditLogPollInactivated(final String guid, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.POLL.getValue(), AuditOperationEnum.INACTIVATE.getValue(), guid, AuditOperationEnum.INACTIVATE.getValue(), username);
    }

    public static String mapAuditLogPollStarted(final String guid, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.POLL.getValue(), AuditOperationEnum.START.getValue(), guid, AuditOperationEnum.START.getValue(), username);
    }

    public static String mapAuditLogPollStopped(final String guid, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.POLL.getValue(), AuditOperationEnum.STOP.getValue(), guid, AuditOperationEnum.STOP.getValue(), username);
    }

    private static String mapToAuditLog(final String objectType, final String operation, final String affectedObject, final String comment, final String username) throws AuditModelMarshallException {
        return AuditLogMapper.mapToAuditLog(objectType, operation, affectedObject, comment, username);
    }

    public static String mapAuditLogProgramPollStarted(final String guid, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.PROGRAM_POLL.getValue(), AuditOperationEnum.START.getValue(), guid, AuditOperationEnum.START.getValue(), username);
    }

    public static String mapAuditLogProgramPollStopped(final String guid, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.PROGRAM_POLL.getValue(), AuditOperationEnum.STOP.getValue(), guid, AuditOperationEnum.STOP.getValue(), username);
    }


    public static String mapAuditLogProgramPollInactivated(final String guid, final String username) throws AuditModelMarshallException {
        return mapToAuditLog(AuditObjectTypeEnum.PROGRAM_POLL.getValue(), AuditOperationEnum.INACTIVATE.getValue(), guid, AuditOperationEnum.INACTIVATE.getValue(), username);
    }
}