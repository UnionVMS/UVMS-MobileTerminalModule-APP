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
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
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
    public void createPoll(Poll poll) throws PollDaoException {
        try {
            em.persist(poll);
        } catch (EntityExistsException | IllegalArgumentException e) {
            LOG.error("[ Error when creating poll. ] {}", e.getMessage());
            throw new PollDaoException("[ create poll ] " + e.getMessage());
        }
    }

    @Override
    public Poll getPoll(String pollId) throws PollDaoException {
        try {
            TypedQuery<Poll> query = em.createNamedQuery(MobileTerminalConstants.POLL_FIND_BY_ID, Poll.class);
            query.setParameter("polltrackId", pollId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting poll. ] {}", e.getMessage());
            throw new PollDaoException("No Poll entity found with TrackId " + pollId);
        }
    }

    @Override
    public List<Poll> getPollListByProgramPoll(Integer pollProgramId) throws PollDaoException {
        throw new PollDaoException("Not yet implemented");
    }

    @Override
    public void createPollProgram(PollProgram pollProgram) throws PollDaoException {
        try {
            em.persist(pollProgram);
        } catch (EntityExistsException | IllegalArgumentException e) {
            LOG.error("[ Error when creating poll program. ] {}", e.getMessage());
            throw new PollDaoException("[ create poll ] " + e.getMessage());
        }
    }

	@Override
	public Long getPollListSearchCount(String sql, List<PollSearchKeyValue> searchKeyValues, boolean isDynamic) {
		TypedQuery<Long> query = em.createQuery(sql, Long.class);
		
		for(PollSearchKeyValue keyValue : searchKeyValues) {
			String sqlReplaceToken = keyValue.getSearchField().getSqlReplaceToken();
			if(keyValue.getSearchField().getClazz().isAssignableFrom(MobileTerminalTypeEnum.class)){
				List<MobileTerminalTypeEnum> types = new ArrayList<>();
				for (String value : keyValue.getValues()) {
					MobileTerminalTypeEnum type = MobileTerminalTypeEnum.valueOf(value);
					types.add(type);
				}
				query.setParameter(sqlReplaceToken, types);
			} else if(keyValue.getSearchField().getClazz().isAssignableFrom(PollTypeEnum.class)){
				List<PollTypeEnum> types = new ArrayList<>();
				for (String value : keyValue.getValues()) {
					PollTypeEnum type = PollTypeEnum.valueOf(value);
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
	public List<Poll> getPollListSearchPaginated(Integer page, Integer listSize, String sql, List<PollSearchKeyValue> searchKeyValues, boolean isDynamic) throws PollDaoException {
		TypedQuery<Poll> query = em.createQuery(sql, Poll.class);
		
		for(PollSearchKeyValue keyValue : searchKeyValues) {
			String sqlReplaceToken = keyValue.getSearchField().getSqlReplaceToken();
			if(keyValue.getSearchField().getClazz().isAssignableFrom(MobileTerminalTypeEnum.class)){
				List<MobileTerminalTypeEnum> types = new ArrayList<>();
				for (String value : keyValue.getValues()) {
					MobileTerminalTypeEnum type = MobileTerminalTypeEnum.valueOf(value);
					types.add(type);
				}
				query.setParameter(sqlReplaceToken, types);
			} else if(keyValue.getSearchField().getClazz().isAssignableFrom(PollTypeEnum.class)){
				List<PollTypeEnum> types = new ArrayList<>();
				for (String value : keyValue.getValues()) {
					PollTypeEnum type = PollTypeEnum.valueOf(value);
					types.add(type);
				}
				query.setParameter(sqlReplaceToken, types);
			} else {
				query.setParameter(sqlReplaceToken, keyValue.getValues());
			}
		}
		
		query.setFirstResult(listSize * (page -1));
		query.setMaxResults(listSize);
		return query.getResultList();
	}

}