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
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.DNIDList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class DNIDListDaoBean {

	@PersistenceContext
	private EntityManager em;

	private final static Logger LOG = LoggerFactory.getLogger(DNIDListDaoBean.class);

	public List<DNIDList> getAllDNIDList() throws ConfigDaoException {
		try {
            TypedQuery<DNIDList> query = em.createNamedQuery(MobileTerminalConstants.DNID_LIST, DNIDList.class);
            return query.getResultList();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting dnid list. ] {}", e.getMessage());
            throw new ConfigDaoException("No entities found when retrieving dnid list");
        }
	}

	public List<DNIDList> getDNIDList(String pluginName) throws ConfigDaoException {
		try {
            TypedQuery<DNIDList> query = em.createNamedQuery(MobileTerminalConstants.DNID_LIST_BY_PLUGIN, DNIDList.class);
            query.setParameter("pluginName", pluginName);
			return query.getResultList();
		} catch (NoResultException e) {
            LOG.error("[ Error when getting dnid list by plugin. ] {}", e.getMessage());
            throw new ConfigDaoException("No entities found when retrieving dnid list by plugin");
        }
	}

	public void removeByPluginName(String pluginName) throws ConfigDaoException {
		List<DNIDList> dnidList = getDNIDList(pluginName);
		for(DNIDList entity : dnidList) {
			em.remove(entity);
		}
	}

	public DNIDList create(DNIDList entity) throws ConfigDaoException {
		try {
            em.persist(entity);
            return entity;
        } catch (Exception e) {
            LOG.error("[ Error when creating dnid list entity. ]", e.getMessage());
            throw new ConfigDaoException("[ dnid list entity ] " + e.getMessage());
        }
	}
}