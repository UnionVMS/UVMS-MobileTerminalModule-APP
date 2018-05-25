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

import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.EnumException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.EnumMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class EnumMapperTest {
    
    //EVENT
    @Test
    public void testGetEventFromId() throws EnumException {
    	EventCodeEnum idEnum = EventCodeEnum.MODIFY;
    	Integer id = idEnum.getId();
    	EventCodeEnum res = EnumMapper.getEventTypeFromId(id);
    	assertEquals(idEnum, res);
    }
    /*
    @Test(expected=EnumException.class)
    public void testGetEventFromIdException() throws EnumException {
    	EventCodeEnum idEnum = EventCodeEnum.MODIFY;
    	Integer id = idEnum.getId();
    	Integer anotherId = 42;
    	EventCodeEnum res = EnumMapper.getEventTypeFromId(anotherId);
    }
    
    @Test(expected=EnumException.class)
    public void testGetEventFromEntityNoEntity() throws EnumException {
    	TerminalEventType entity = null;
    	EnumMapper.getEventTypeFromEntity(entity);
    }
    
    @Test(expected=EnumException.class)
    public void testGetEventFromEntityNoEntityId() throws EnumException {
    	TerminalEventType entity = new TerminalEventType();
    	EnumMapper.getEventTypeFromEntity(entity);
    }
    
    @Test
    public void testGetEventFromEntity() throws EnumException {
    	EventCodeEnum idEnum = EventCodeEnum.MODIFY;
    	TerminalEventType entity = new TerminalEventType();
    	entity.setId(idEnum.getId());
    	EventCodeEnum res = EnumMapper.getEventTypeFromEntity(entity);
    	assertEquals(idEnum, res);
    }
    
    @Test
    public void testGetEventModelFromType() throws EnumException {
    	EventCodeEnum type = EventCodeEnum.MODIFY;
    	EventCode systemEnum = EventCode.MODIFY;
    	EventCode result = EnumMapper.getEventModelFromType(type);
    	assertEquals(systemEnum, result);
    }
    
    @Test(expected=EnumException.class)
    public void testGetEventModelFromTypeNoType() throws EnumException {
    	EventCodeEnum type = null;
		EnumMapper.getEventModelFromType(type);
    }
    
    @Test
    public void testGetEventFromModel() throws EnumException {
    	EventCodeEnum typeEnum = EventCodeEnum.MODIFY;
    	EventCode systemEnum = EventCode.MODIFY;
    	EventCodeEnum result = EnumMapper.getEventTypeFromModel(systemEnum);
    	assertEquals(typeEnum, result);
    }
    //END: EVENT*/
}