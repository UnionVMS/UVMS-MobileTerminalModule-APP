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
package eu.europa.ec.fisheries.uvms.mobileterminal.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.OceanRegionDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.OceanRegion;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PluginMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({PluginMapper.class})
public class ConfigModelTest {
	
	@Mock
	OceanRegionDao oceanRegionDao;
	
	@Mock
	MobileTerminalPluginDao mobileTerminalPluginDao;
	
	
	@Mock
	PluginService pluginType;
	
	@Mock
	MobileTerminalPlugin siriusone;

	@Mock
	MobileTerminalPlugin twostage;

	@InjectMocks
	ConfigModelBean testModelBean;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
        when(siriusone.getPluginSatelliteType()).thenReturn("IRIDIUM");
        when(twostage.getPluginSatelliteType()).thenReturn("INMARSAT_C");
	}
	
	@Test
	public void testGetAllTerminalSystemsEmpty() throws MobileTerminalModelException {
		final List<MobileTerminalPlugin> pluginList = new ArrayList<>();
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(pluginList);
		final List<OceanRegion> oceanRegionList = new ArrayList<>();
		when(oceanRegionDao.getOceanRegionList()).thenReturn(oceanRegionList);
		
		final List<TerminalSystemType> terminalSystems = testModelBean.getAllTerminalSystems();
		
		assertEquals(0, terminalSystems.size());
	}
	
	//@Test
	public void testGetAllTerminalSystems() throws MobileTerminalModelException {
		final List<MobileTerminalPlugin> pluginList = new ArrayList<>();
		pluginList.add(siriusone);
		pluginList.add(twostage);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(pluginList);
		final List<OceanRegion> oceanRegionList = new ArrayList<>();
		when(oceanRegionDao.getOceanRegionList()).thenReturn(oceanRegionList);
		
		final List<TerminalSystemType> terminalSystems = testModelBean.getAllTerminalSystems();
		
		assertEquals(2, terminalSystems.size());
		
		for(final TerminalSystemType system : terminalSystems) {
			assertNotNull(system.getType());
		}
	}

	@Test(expected=MobileTerminalModelException.class)
	public void testGetAllTerminalSystemsException() throws MobileTerminalModelException {
		when(mobileTerminalPluginDao.getPluginList()).thenThrow(new ConfigDaoException("No plugins"));
		
		final List<TerminalSystemType> terminalSystems = testModelBean.getAllTerminalSystems();
	}
	
	@Test
	public void testGetConfigValues() throws MobileTerminalModelException {
		final List<ConfigList> configValues = testModelBean.getConfigValues();
		assertNotNull(configValues);
		assertEquals(3, configValues.size());
		
		for(final ConfigList config : configValues) {
			assertNotNull(config.getName());
		}
	}
	
	@Test
	public void testUpsertPluginsEmptyInput() throws MobileTerminalModelException {
		final List<PluginService> pluginList = new ArrayList<>();
		final List<Plugin> plugins = testModelBean.upsertPlugins(pluginList);
		
		assertEquals(pluginList.size(), plugins.size());
	}
	
	@Test(expected=InputArgumentException.class)
	public void testUpsertPluginsNull() throws MobileTerminalModelException {
		final List<Plugin> plugins = testModelBean.upsertPlugins(null);
	}
	
	@Test(expected=InputArgumentException.class)
	public void testUpsertPluginsNonValidInput() throws MobileTerminalModelException {
		final List<PluginService> pluginList = new ArrayList<>();
		pluginList.add(pluginType);
		final List<Plugin> plugins = testModelBean.upsertPlugins(pluginList);
	}
	
	@Test(expected=TerminalDaoException.class)
	public void testUpdatePluginUpdateException() throws TerminalDaoException {
		final String serviceName = "serviceName";
		when(pluginType.getServiceName()).thenReturn(serviceName);
		when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenReturn(siriusone);
		
		mockStatic(PluginMapper.class);
		when(PluginMapper.equals(siriusone, pluginType)).thenReturn(false);
		
		when(mobileTerminalPluginDao.updatePlugin(any(MobileTerminalPlugin.class))).thenThrow(new TerminalDaoException("Couldn't update entity"));
		
		final MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);
	}
	
	@Test
	public void testUpdatePluginEquals() throws TerminalDaoException {
		final String serviceName = "serviceName";
		when(pluginType.getServiceName()).thenReturn(serviceName);
		when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenReturn(siriusone);
		mockStatic(PluginMapper.class);
		when(PluginMapper.equals(siriusone, pluginType)).thenReturn(true);
		
		final MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);
		
		assertNotNull(resEntity);
	}
	
	@Test
	public void testUpdatePluginUpdate() throws TerminalDaoException {
		final String serviceName = "serviceName";
		when(pluginType.getServiceName()).thenReturn(serviceName);
		when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenReturn(siriusone);
		mockStatic(PluginMapper.class);
		when(PluginMapper.equals(siriusone, pluginType)).thenReturn(false);
		mockStatic(PluginMapper.class);
		when(PluginMapper.mapModelToEntity(siriusone, pluginType)).thenReturn(siriusone);
		
		when(mobileTerminalPluginDao.updatePlugin(any(MobileTerminalPlugin.class))).thenReturn(siriusone);
		
		final MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);
		assertNotNull(resEntity);
	}
	
	@Test
	public void testUpdateNoPluginFound() throws TerminalDaoException {
		final String serviceName = "serviceName";
		when(pluginType.getServiceName()).thenReturn(serviceName);
		when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenThrow(new NoEntityFoundException("No plugin to update"));
		final MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);
		
		assertNull(resEntity);
	}
	
	@Test
	public void testUpsertPluginsCreate() throws MobileTerminalModelException {
		final String pluginLabelName = "serviceLabelName";
		final String pluginServiceName = "serviceName";
		final List<PluginService> pluginList = new ArrayList<>();
		when(pluginType.getLabelName()).thenReturn(pluginLabelName);
		when(pluginType.getServiceName()).thenReturn(pluginServiceName);
		pluginList.add(pluginType);
		
		when(mobileTerminalPluginDao.getPluginByServiceName(pluginServiceName)).thenThrow(new NoEntityFoundException("No plugin to update"));
		
		mockStatic(PluginMapper.class);
		when(PluginMapper.mapModelToEntity(any(PluginService.class))).thenReturn(siriusone);
		when(mobileTerminalPluginDao.createMobileTerminalPlugin(siriusone)).thenReturn(siriusone);
		
		final List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);
		
		final List<Plugin> plugins = testModelBean.upsertPlugins(pluginList);
		
		assertEquals(pluginList.size(), plugins.size());
	}
	
	@Test(expected=ConfigDaoException.class)
	public void testInactivatePluginsException() throws ConfigDaoException {
		when(mobileTerminalPluginDao.getPluginList()).thenThrow(new ConfigDaoException("No plugin list"));
		final Map<String, PluginService> map = new HashMap<>();
		testModelBean.inactivatePlugins(map);
	}
	
	@Test
	public void testInactivatePluginsNoPlugin() throws ConfigDaoException {
		final Map<String, PluginService> map = new HashMap<>();
		final List<Plugin> resEntityList = testModelBean.inactivatePlugins(map);
		
		assertNotNull(resEntityList);
		assertEquals(0, resEntityList.size());
	}
	
	@Test
	public void testInactivatePluginsInactive() throws ConfigDaoException {
		final String serviceName = "serviceName";
		final Map<String, PluginService> map = new HashMap<>();
		final List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(siriusone.getPluginServiceName()).thenReturn(serviceName);
		when(siriusone.getPluginInactive()).thenReturn(false);
		entityList.add(siriusone);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);
		
		final List<Plugin> resEntityList = testModelBean.inactivatePlugins(map);
		assertNotNull(resEntityList);
		assertEquals(1, resEntityList.size());
		for(final Plugin p : resEntityList) {
			assertFalse(p.isInactive());
		}
	}
	
	@Test
	public void testInactivePluginsExsist() throws ConfigDaoException {
		final String serviceName = "serviceName";
		final Map<String, PluginService> map = new HashMap<>();
		final List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(siriusone.getPluginServiceName()).thenReturn(serviceName);
		when(siriusone.getPluginInactive()).thenReturn(false);
		entityList.add(siriusone);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);
		map.put(serviceName, pluginType);
		
		final List<Plugin> resEntityList = testModelBean.inactivatePlugins(map);
		assertNotNull(resEntityList);
		assertEquals(0, resEntityList.size());
	}
	
	@Test
	public void testInactivePluginsAlreadyInactive() throws ConfigDaoException {
		final String serviceName = "serviceName";
		final Map<String, PluginService> map = new HashMap<>();
		final List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(siriusone.getPluginServiceName()).thenReturn(serviceName);
		when(siriusone.getPluginInactive()).thenReturn(true);
		entityList.add(siriusone);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);
		
		final List<Plugin> resEntityList = testModelBean.inactivatePlugins(map);
		assertNotNull(resEntityList);
		assertEquals(0, resEntityList.size());
	}
}