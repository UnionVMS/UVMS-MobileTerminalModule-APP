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

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;

@Stateless
public class MobileTerminalPluginDaoBean extends Dao {

	private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalPluginDaoBean.class);

	public List<MobileTerminalPlugin> getPluginList() throws ConfigDaoException {
		try {
            TypedQuery<MobileTerminalPlugin> query = em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_ALL, MobileTerminalPlugin.class);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting plugin list. ] " + e.getMessage());
            throw new ConfigDaoException("No entities found when retrieving all plugins");
        }
	}

	public MobileTerminalPlugin createMobileTerminalPlugin(MobileTerminalPlugin plugin) throws TerminalDaoException {
		try {
			em.persist(plugin);
			return plugin;
		} catch (Exception e) {
			throw new TerminalDaoException("[ create mobile terminal plugin ] ");
		}
	}

	public MobileTerminalPlugin getPluginByServiceName(String serviceName) throws NoEntityFoundException {
		try {
            TypedQuery<MobileTerminalPlugin> query = em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_BY_SERVICE_NAME, MobileTerminalPlugin.class);
            query.setParameter("serviceName", serviceName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting plugin by service name. ] {}", e.getMessage());
            throw new NoEntityFoundException("No entities found when retrieving mobile terminal plugin by service name");
        }
	}

	public MobileTerminalPlugin updateMobileTerminalPlugin(MobileTerminalPlugin entity) throws TerminalDaoException {
		if(entity == null || entity.getId() == null) {
			// It's a defensive decision to prevent clients from using merge excessively instead of persist.
			throw new TerminalDaoException(" [ There is no such MobileTerminalPlugin object to update ] ");
		}
		try {
			entity = em.merge(entity);
			em.flush();
			return entity;
		} catch (Exception e) {
			throw new TerminalDaoException(" [ Error occurred while trying to update MobileTerminalPlugin ] ");
		}
	}
}
