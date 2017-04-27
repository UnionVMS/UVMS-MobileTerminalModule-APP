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
package eu.europa.ec.fisheries.uvms.mobileterminal.dao;

import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;

@Local
public interface PollProgramDao {

    /**
     * Create poll program
     *
     * @param pollProgram
     * @throws PollDaoException
     */
    public void createPollProgram(PollProgram pollProgram) throws PollDaoException;
    
    /**
     * Update poll program
     * 
     * @param pollProgram
     * @return
     * @throws PollDaoException
     */
    public PollProgram updatePollProgram(PollProgram pollProgram) throws PollDaoException;
    
    /**
     * Get a list of alive poll programs
     * A poll program is alive when current date is before end date,
     * and the state is not archived
     *
     * @return
     * @throws PollDaoException
     */
    public List<PollProgram> getProgramPollsAlive() throws PollDaoException;

    /**
     * Get a list of running and started programs
     * A poll program is running when current date is after start date,
     * and the state is STARTED
     * 
     * @return
     * @throws PollDaoException
     */
    public List<PollProgram> getPollProgramRunningAndStarted() throws PollDaoException;
    
    /**
     * Get poll program by guid
     * @param guid
     * @return
     * @throws PollDaoException
     */
	public PollProgram getPollProgramByGuid(String guid) throws PollDaoException;
}