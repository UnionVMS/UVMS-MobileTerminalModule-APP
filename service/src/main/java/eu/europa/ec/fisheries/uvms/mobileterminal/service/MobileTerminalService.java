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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAssignQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalHistory;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalStatus;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

@Local
public interface MobileTerminalService {

    /**
     * Create mobile terminal
     *
     * @param mobileTerminal
     * @param source
     * @param data
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType createMobileTerminal(MobileTerminalType mobileTerminal, MobileTerminalSource source, String username) throws MobileTerminalException;

    /**
     * Get a list of mobile terminals defined by query
     *
     * @param query
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalListResponse getMobileTerminalList(MobileTerminalListQuery query) throws MobileTerminalException;

    /**
     * Get a mobile terminal by guid
     *
     * @param guid
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType getMobileTerminalById(String guid) throws MobileTerminalException;

    /**
     * Get a mobile terminal by mobile terminal id type
     *
     * @param id
     * @param queue
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType getMobileTerminalById(MobileTerminalId id, DataSourceQueue queue) throws MobileTerminalException;

    /**
     *
     * Updates mobile terminal if it exists and creates a new mobile terminal if
     * it does not exist
     *
     * @param data
     * @param source
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType upsertMobileTerminal(MobileTerminalType data, MobileTerminalSource source, String username) throws MobileTerminalException;

    /**
     * Update mobile terminal
     *
     * @param data
     * @param comment
     * @param source
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType updateMobileTerminal(MobileTerminalType data, String comment, MobileTerminalSource source, String username)
            throws MobileTerminalException;

    /**
     * Assigns the selected mobile terminal from the selected carrier
     *
     * @param query
     * @param comment
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException;

    /**
     * Unassigns the selected mobile terminal from the selected carrier
     *
     * @param query
     * @param comment
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalException;

    /**
     * Set status of a mobile terminal
     *
     * @param terminalId
     * @param comment
     * @param status
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalType setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username)
            throws MobileTerminalException;

    /**
     * Get mobile terminal history list for one mobile terminal
     *
     * @param guid
     * @return
     */
    public List<MobileTerminalHistory> getMobileTerminalHistoryList(String guid) throws MobileTerminalException;

    /**
     * Get pollable mobile terminals matching query
     *
     * @param query
     * @return
     * @throws MobileTerminalServiceException
     */
    public MobileTerminalListResponse getPollableMobileTerminal(PollableQuery query) throws MobileTerminalException;

}