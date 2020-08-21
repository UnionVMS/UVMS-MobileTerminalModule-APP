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
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class TerminalDaoBean extends Dao implements TerminalDao {

    private final static Logger LOG = LoggerFactory.getLogger(TerminalDaoBean.class);

	@Override
	public MobileTerminal getMobileTerminalByGuid(String guid) throws NoEntityFoundException {
		try {
            TypedQuery<MobileTerminal> query = em.createNamedQuery(MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_GUID, MobileTerminal.class);
            query.setParameter("guid", guid);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoEntityFoundException("No entity found with guid " + guid,e);
        }
	}

    @Override
    public MobileTerminal getMobileTerminalBySerialNo(String serialNo) throws NoEntityFoundException {
        try {
            TypedQuery<MobileTerminal> query = em.createNamedQuery(MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_SERIAL_NO, MobileTerminal.class);
            query.setParameter("serialNo", serialNo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoEntityFoundException("No entity found with serial no " + serialNo,e);
        }
    }

    @Override
    public void createMobileTerminal(MobileTerminal terminal) throws TerminalDaoException {
        try {
            em.persist(terminal);
            em.flush();
        } catch (Exception e) {
            throw new TerminalDaoException("Error when creating." , e);
        }
    }

    @Override
    public void updateMobileTerminal(MobileTerminal terminal) throws TerminalDaoException {
        if(terminal == null || terminal.getId() == null) {
            // It's a defensive decision to prevent clients from using merge excessively instead of persist.
            throw new TerminalDaoException(" [ There is no such persisted entity to update ] ");
        }
        try {
            em.merge(terminal);
            em.flush();
        } catch (Exception e) {
            throw new TerminalDaoException("Error when updating.",e);
        }
    }

    @Override
    public List<MobileTerminal> getMobileTerminalsByQuery(String sql) {
        Session session = em.unwrap(Session.class);
        Query query = session.createQuery(sql);
        return query.list();
    }
}
