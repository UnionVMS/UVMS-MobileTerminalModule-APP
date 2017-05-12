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
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.Dao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;

@Stateless
public class PollProgramDaoBean extends Dao implements PollProgramDao {

    final static Logger LOG = LoggerFactory.getLogger(PollProgramDaoBean.class);

    @Override
    public void createPollProgram(PollProgram pollProgram) throws PollDaoException {
        try {
            em.persist(pollProgram);
        } catch (EntityExistsException | IllegalArgumentException e) {
            LOG.error("[ Error when creating poll program. ] {}", e.getMessage());
            throw new PollDaoException("[ create poll program ] " + e.getMessage());
        }
    }

    @Override
    public PollProgram updatePollProgram(PollProgram pollProgram) throws PollDaoException {
        try {
            em.merge(pollProgram);
            em.flush();
            return pollProgram;
        } catch (EntityExistsException | IllegalArgumentException e) {
            LOG.error("[ Error when updating poll program. ] {}", e.getMessage());
            throw new PollDaoException("[ update poll program ] " + e.getMessage());
        }
    }

    @Override
    public List<PollProgram> getProgramPollsAlive() throws PollDaoException {
        try {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_ALIVE, PollProgram.class);
            query.setParameter("currentDate", DateUtils.getUTCNow());
            return query.getResultList();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting poll program alive. ] {}", e.getMessage());
            throw new PollDaoException("No entities found when retrieving getPollProgramAlive");
        }
    }

    @Override
    public List<PollProgram> getPollProgramRunningAndStarted() throws PollDaoException {
        try {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_RUNNING_AND_STARTED, PollProgram.class);
            query.setParameter("currentDate", DateUtils.getUTCNow());
            List<PollProgram> pollPrograms = query.getResultList();
            List<PollProgram> validPollPrograms = new ArrayList<PollProgram>();

            for (PollProgram pollProgram : pollPrograms) {
                Date lastRun = pollProgram.getLatestRun();
                Integer frequency = pollProgram.getFrequency();
                Date now = DateUtils.getUTCNow();

                boolean createPoll = lastRun == null || now.getTime() >= lastRun.getTime() + frequency * 1000;
                LOG.debug("createPoll:{} for guid '{}' (lastRun:{}, frequency:{}, now:{})", createPoll, pollProgram.getGuid(), lastRun, frequency, now);

                if (createPoll) {
                    pollProgram.setLatestRun(now);
                    validPollPrograms.add(pollProgram);
                }
            }

            return validPollPrograms;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting poll program running and started. ] {}", e.getMessage());
            throw new PollDaoException("No entities found when retrieving getPollProgramRunningAndStarted");
        }
    }

    @Override
    public PollProgram getPollProgramByGuid(String guid) throws PollDaoException {
        try {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_BY_ID, PollProgram.class);
            query.setParameter("guid", guid);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting poll program by id. ] {}", e.getMessage());
            throw new PollDaoException("No entity found getting by id");
        }
    }
}