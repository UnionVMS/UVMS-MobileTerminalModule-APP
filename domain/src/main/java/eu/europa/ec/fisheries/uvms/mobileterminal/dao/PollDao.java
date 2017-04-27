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
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;

@Local
public interface PollDao {

    /**
     * Create poll
     *
     * @param poll
     * @throws PollDaoException
     */
    public void createPoll(Poll poll) throws PollDaoException;

    /**
     * Create pollProgram
     *
     * @param poll
     * @throws PollDaoException
     */
    public void createPollProgram(PollProgram poll) throws PollDaoException;

    /**
     * Get poll by internal id
     *
     * @param pollId
     * @return
     * @throws PollDaoException
     */
    public Poll getPoll(String pollId) throws PollDaoException;

    /**
     * Get all polls connected to program poll with given internal id
     *
     * @param pollProgramId
     * @return
     * @throws PollDaoException
     */
    public List<Poll> getPollListByProgramPoll(Integer pollProgramId) throws PollDaoException;

    /**
     * Get count of polls from sql with search criterias
     * 
     * @param sql
     * @param searchFields
     * @param isDynamic
     * @return
     */
    public Long getPollListSearchCount(String sql, List<PollSearchKeyValue> searchFields, boolean isDynamic);
    
    /**
     * Get polls paginated from sql with search criterias
     * @return
     * @throws PollDaoException
     */
    public List<Poll> getPollListSearchPaginated(Integer page, Integer listSize, String sql, List<PollSearchKeyValue> searchFields, boolean isDynamic) throws PollDaoException;
}