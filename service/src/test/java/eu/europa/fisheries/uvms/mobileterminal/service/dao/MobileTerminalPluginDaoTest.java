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
package eu.europa.fisheries.uvms.mobileterminal.service.dao;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MobileTerminalPluginDaoTest {

	@Mock
	private EntityManager em;
	@Mock
	private TypedQuery<MobileTerminalPlugin> query;
	@Mock
	private MobileTerminalPlugin plugin;
	
	@InjectMocks
	private MobileTerminalPluginDaoBean testDaoBean;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPluginListEmpty() throws ConfigDaoException {
		List<MobileTerminalPlugin> result = new ArrayList<>();
		when(em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_ALL, MobileTerminalPlugin.class)).thenReturn(query);
		when(query.getResultList()).thenReturn(result);
		
		List<MobileTerminalPlugin> pluginList = testDaoBean.getPluginList();
		
		assertEquals(0, pluginList.size());
	}
	
	@Test
	public void testGetPluginListOne() throws ConfigDaoException {
		List<MobileTerminalPlugin> result = new ArrayList<>();
		result.add(plugin);
		when(em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_ALL, MobileTerminalPlugin.class)).thenReturn(query);
		when(query.getResultList()).thenReturn(result);
		
		List<MobileTerminalPlugin> pluginList = testDaoBean.getPluginList();
		
		assertEquals(result.size(), pluginList.size());
	}
	
	@Test
	public void testGetPluginListMany() throws ConfigDaoException {
		List<MobileTerminalPlugin> result = new ArrayList<>();
		result.add(plugin);
		result.add(plugin);
		result.add(plugin);
		result.add(plugin);
		result.add(plugin);
		when(em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_ALL, MobileTerminalPlugin.class)).thenReturn(query);
		when(query.getResultList()).thenReturn(result);
		
		List<MobileTerminalPlugin> pluginList = testDaoBean.getPluginList();
		
		assertEquals(result.size(), pluginList.size());
	}
	
	@Test(expected=ConfigDaoException.class)
	public void testGetPluginListResultListException() throws ConfigDaoException {
		List<MobileTerminalPlugin> result = new ArrayList<>();
		result.add(plugin);
		when(em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_ALL, MobileTerminalPlugin.class)).thenReturn(query);
		when(query.getResultList()).thenThrow(new QueryTimeoutException());
		
		testDaoBean.getPluginList();
	}
	
	@Test(expected=TerminalDaoException.class)
	public void testCreateMobileTerminalPluginNull() throws TerminalDaoException {
		doThrow(new IllegalArgumentException()).when(em).persist(null);
		testDaoBean.createMobileTerminalPlugin(null);
	}
	
	@Test
	public void testCreateMobileTerminalPluginVerify() throws TerminalDaoException {
		testDaoBean.createMobileTerminalPlugin(plugin);
		verify(em).persist(plugin);
	}
	
	@Test(expected=TerminalDaoException.class)
	public void testCreateMobileTerminalPluginException() throws TerminalDaoException {
		doThrow(new EntityExistsException()).when(em).persist(plugin);
		testDaoBean.createMobileTerminalPlugin(plugin);
	}
	
	@Test
	public void testGetPluginByServiceName() throws NoEntityFoundException {
		String serviceClassName = "ServiceClassName";
		when(plugin.getName()).thenReturn(serviceClassName);
		when(em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_BY_SERVICE_NAME, MobileTerminalPlugin.class)).thenReturn(query);
		when(query.getSingleResult()).thenReturn(plugin);
		
		MobileTerminalPlugin result = testDaoBean.getPluginByServiceName(serviceClassName);
		
		assertNotNull(result);
		assertEquals(serviceClassName, result.getName());
	}
	
	@Test(expected=NoEntityFoundException.class)
	public void testGetPluginByServiceNameException() throws NoEntityFoundException {
		String serviceClassName = "ServiceClassName";
		when(em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_BY_SERVICE_NAME, MobileTerminalPlugin.class)).thenReturn(query);
		when(query.getSingleResult()).thenThrow(new NoResultException());
		
		testDaoBean.getPluginByServiceName(serviceClassName);
	}
	
	@Test
	public void testUpdatePluginVerify() throws TerminalDaoException {
		testDaoBean.updateMobileTerminalPlugin(plugin);
		verify(em).merge(plugin);
		verify(em).flush();
	}
	
	@Test(expected=TerminalDaoException.class)
	public void testUpdatePluginException() throws TerminalDaoException {
		doThrow(new IllegalArgumentException()).when(em).merge(null);
		testDaoBean.updateMobileTerminalPlugin(null);
	}
}
