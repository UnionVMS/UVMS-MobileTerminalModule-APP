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

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.Dao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class PollProgramDaoBean extends Dao implements PollProgramDao {

    private final static Logger LOG = LoggerFactory.getLogger(PollProgramDaoBean.class);

    @Override
    public void createPollProgram(PollProgram pollProgram) throws PollDaoException {
        try {
            em.persist(pollProgram);
        } catch (EntityExistsException | IllegalArgumentException e) {
            throw new PollDaoException("Error when creating poll program ", e);
        }
    }

    @Override
    public PollProgram updatePollProgram(PollProgram pollProgram) throws PollDaoException {
        if(pollProgram == null || pollProgram.getId() == null) {
            // It's a defensive decision to prevent clients from using merge excessively instead of persist.
            throw new PollDaoException(" [ There is no such persisted entity to update ] ");
        }
        try {
            pollProgram = em.merge(pollProgram);
            em.flush();
            return pollProgram;
        } catch (EntityExistsException | IllegalArgumentException e) {
            throw new PollDaoException("Error when updating poll program " , e);
        }
    }

    @Override
    public List<PollProgram> getProgramPollsAlive() throws PollDaoException {
        try {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_ALIVE, PollProgram.class);
            query.setParameter("currentDate", DateUtils.getUTCNow());
            return query.getResultList();
        } catch (NoResultException e) {
            throw new PollDaoException("No entities found when retrieving getPollProgramAlive",e);
        }
    }

    @Override
    public List<PollProgram> getPollProgramRunningAndStarted() throws PollDaoException {
        try {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_RUNNING_AND_STARTED, PollProgram.class);
            query.setParameter("currentDate", DateUtils.getUTCNow());
            List<PollProgram> pollPrograms = query.getResultList();
            List<PollProgram> validPollPrograms = new ArrayList<>();

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
            throw new PollDaoException("No entities found when retrieving getPollProgramRunningAndStarted",e);
        }
    }

    @Override
    public PollProgram getPollProgramByGuid(String guid) throws PollDaoException {
        try {
            TypedQuery<PollProgram> query = em.createNamedQuery(MobileTerminalConstants.POLL_PROGRAM_FIND_BY_ID, PollProgram.class);
            query.setParameter("guid", guid);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new PollDaoException("No entity found getting by id",e);
        }
    }
}
