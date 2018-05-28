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
package eu.europa.fisheries.uvms.mobileterminal.service.mapper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.EnumException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;

@RunWith(MockitoJUnitRunner.class)
public class ModelToEntityMapperTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMapNewTerminalEntity() {
    	
    }
    
    @Test
    public void testMapExistingTerminalEntity() {
    	
    }
    @Ignore
    @Test
    public void testMapComChannels() throws MobileTerminalModelMapperException, EnumException {
    	/*
    	List<ComChannelType> comchannels = null;
    	Integer dnid = 1220;
    	Integer memberId = 1209;
    	
    	Comchanneleventconnect histories = mapper.mapComChannels(comchannels);
    	assertNull(histories);
    	
    	Comchanneltype comchanneltype = new Comchanneltype();
		when(enumDao.getComchanneltypeById(ComChannelNameEnum.VMS)).thenReturn(comchanneltype);
		
		comchannels = new ArrayList<ComChannelType>();
		ComChannelType channel = new ComChannelType();
		channel.setChannelType(ChannelNameType.VMS);
		ComChannelAttribute channelIdDnid = new ComChannelAttribute();
		channelIdDnid.setType(ComChannelFieldType.DNID);
		channelIdDnid.setValue(String.valueOf(dnid));
		channel.getAttributes().add(channelIdDnid);
		ComChannelAttribute channelIdMemberNo = new ComChannelAttribute();
		channelIdMemberNo.setType(ComChannelFieldType.MEMBER_ID);
		channelIdMemberNo.setValue(String.valueOf(memberId));
		channel.getAttributes().add(channelIdMemberNo);
		comchannels.add(channel);
		
		histories = mapper.mapComChannels(comchannels);
		
		assertNotNull(histories);
    	assertTrue(MobileTerminalConstants.TRUE.equalsIgnoreCase(histories.getComchaneeventconActive()));
//    	assertEquals(1, histories.getComchannels().size());*/
    }
    @Ignore
    @Test
    public void testMapTerminalAttributes() throws EnumException {
    	/*
    	List<MobileTerminalAttribute> attributes = null;
    	EventCodeEnum eventType = EventCodeEnum.CREATE;
    	String softwareVersion = "1.0";
    	
    	TerminalEventType eventEntity = new TerminalEventType();
    	when(enumDao.getTerminaleventtypeById(eventType)).thenReturn(eventEntity);
    	
    	Terminaleventconnect histories = mapper.mapTerminalAttributes(attributes);
    	assertNull(histories);
    	
    	Terminaltypeconfiguration conf = new Terminaltypeconfiguration();
		when(enumDao.getTerminaltypeconfigurationById(TerminalConfEnum.SOFTWARE_VERSION)).thenReturn(conf);
		
    	attributes = new ArrayList<MobileTerminalAttribute>();
    	MobileTerminalAttribute attribute = new MobileTerminalAttribute();
    	attribute.setFieldType(MobileTerminalFieldType.SOFTWARE_VERSION);
		attribute.setValue(softwareVersion);
    	attributes.add(attribute);
    	
    	histories = mapper.mapTerminalAttributes(attributes);
    	assertNotNull(histories);*/
    	//assertEquals(1, histories.getTerminalvalues().size());
    	//assertEquals(softwareVersion, histories.getTerminalvalues().get(0).getValue());
    }
}