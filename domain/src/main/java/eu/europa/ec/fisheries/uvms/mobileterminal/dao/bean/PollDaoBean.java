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
package eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.Dao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;

@Stateless
public class PollDaoBean extends Dao implements PollDao {

    final static Logger LOG = LoggerFactory.getLogger(PollDaoBean.class);

    @Override
    public void createPoll(final Poll poll) throws PollDaoException {
        try {
            em.persist(poll);
        } catch (EntityExistsException | IllegalArgumentException e) {
            LOG.error("[ Error when creating poll. ] {}", e.getMessage());
            throw new PollDaoException("[ create poll ] " + e.getMessage());
        }
    }

    @Override
    public Poll getPoll(final String pollId) throws PollDaoException {
        try {
        	//ToDo: The query string POLL_FIND_BY_ID is not implemented anywhere causing this method to always throw an exception.
			//ToDo: Either it needs to be implemented in e.g. a named query in the Poll entity or this query needs to use a different query string.
            final TypedQuery<Poll> query = em.createNamedQuery(MobileTerminalConstants.POLL_FIND_BY_ID, Poll.class);
            query.setParameter("polltrackId", pollId);
            return query.getSingleResult();
        } catch (final NoResultException e) {
            LOG.error("[ Error when getting poll. ] {}", e.getMessage());
            throw new PollDaoException("No Poll entity found with TrackId " + pollId);
        }
    }

    @Override
	//ToDo: This method is not implemented. Need to evaluate if the functionality is required or not.
    public List<Poll> getPollListByProgramPoll(final Integer pollProgramId) throws PollDaoException {
        throw new PollDaoException("Not yet implemented");
    }

    @Override
    public void createPollProgram(final PollProgram pollProgram) throws PollDaoException {
        try {
            em.persist(pollProgram);
        } catch (EntityExistsException | IllegalArgumentException e) {
            LOG.error("[ Error when creating poll program. ] {}", e.getMessage());
            throw new PollDaoException("[ create poll ] " + e.getMessage());
        }
    }

	@Override
	public Long getPollListSearchCount(final String sql, final List<PollSearchKeyValue> searchKeyValues, final boolean isDynamic) {
		final TypedQuery<Long> query = em.createQuery(sql, Long.class);
		
		for(final PollSearchKeyValue keyValue : searchKeyValues) {
			final String sqlReplaceToken = keyValue.getSearchField().getSqlReplaceToken();
			if(keyValue.getSearchField().getClazz().isAssignableFrom(MobileTerminalTypeEnum.class)){
				final List<MobileTerminalTypeEnum> types = new ArrayList<>();
				for (final String value : keyValue.getValues()) {
					final MobileTerminalTypeEnum type = MobileTerminalTypeEnum.valueOf(value);
					types.add(type);
				}
				query.setParameter(sqlReplaceToken, types);
			} else if(keyValue.getSearchField().getClazz().isAssignableFrom(PollTypeEnum.class)){
				final List<PollTypeEnum> types = new ArrayList<>();
				for (final String value : keyValue.getValues()) {
					final PollTypeEnum type = PollTypeEnum.valueOf(value);
					types.add(type);
				}
				query.setParameter(sqlReplaceToken, types);
			} else {
				query.setParameter(sqlReplaceToken, keyValue.getValues());
			}
		}
		
		return query.getSingleResult();
	}

	@Override
	public List<Poll> getPollListSearchPaginated(final Integer page, final Integer listSize, final String sql, final List<PollSearchKeyValue> searchKeyValues, final boolean isDynamic) throws PollDaoException {
		final TypedQuery<Poll> query = em.createQuery(sql, Poll.class);
		
		for(final PollSearchKeyValue keyValue : searchKeyValues) {
			final String sqlReplaceToken = keyValue.getSearchField().getSqlReplaceToken();
			if(keyValue.getSearchField().getClazz().isAssignableFrom(MobileTerminalTypeEnum.class)){
				final List<MobileTerminalTypeEnum> types = new ArrayList<>();
				for (final String value : keyValue.getValues()) {
					final MobileTerminalTypeEnum type = MobileTerminalTypeEnum.valueOf(value);
					types.add(type);
				}
				query.setParameter(sqlReplaceToken, types);
			} else if(keyValue.getSearchField().getClazz().isAssignableFrom(PollTypeEnum.class)){
				final List<PollTypeEnum> types = new ArrayList<>();
				for (final String value : keyValue.getValues()) {
					final PollTypeEnum type = PollTypeEnum.valueOf(value);
					types.add(type);
				}
				query.setParameter(sqlReplaceToken, types);
			} else {
				query.setParameter(sqlReplaceToken, keyValue.getValues());
			}
		}
		
		//ToDo: Need a validation check here to make sure that listSize * (page -1) >= 1.  
		query.setFirstResult(listSize * (page -1));
		query.setMaxResults(listSize);
		return query.getResultList();
	}

}