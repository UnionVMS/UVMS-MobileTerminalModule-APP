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
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.Dao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.OceanRegionDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.OceanRegion;

@Stateless
public class OceanRegionDaoBean extends Dao implements OceanRegionDao {

	private final static Logger LOG = LoggerFactory.getLogger(OceanRegionDaoBean.class);

	@Override
	public List<OceanRegion> getOceanRegionList() throws ConfigDaoException {
		try {
            TypedQuery<OceanRegion> query = em.createNamedQuery(MobileTerminalConstants.OCEAN_REGIONS, OceanRegion.class);
            return query.getResultList();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting ocean regions. ] {}", e.getMessage());
            throw new ConfigDaoException("No entities found when retrieving ocean regions");
        }
	}
}
