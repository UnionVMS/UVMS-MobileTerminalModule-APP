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
package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;

@RunWith(MockitoJUnitRunner.class)
public class EntityToModelMapperTest {

    private static final boolean ENABLED = false;

    @Ignore
    @Test
    public void testMapToTerminalSystemModelEmpty() throws MobileTerminalModelMapperException {
        /*
         TerminalSystemType systemType = TerminalSystemType.INMARSAT_C;
         TerminalType system = new TerminalType();
         system.setId(TerminalTypeEnum.INMARSAT_C.getId());
		
         TerminalSystem resSystem = EntityToModelMapper.mapToTerminalType(system);
         assertEquals(systemType, resSystem.getSystemType());
         assertEquals(0, resSystem.getConfiguration().size());*/
    }

    @Ignore
    @Test
    public void testMapToTerminalSystemModel() throws MobileTerminalModelMapperException {
        /*
         TerminalSystemType systemType = TerminalSystemType.INMARSAT_C;
         TerminalType system = new TerminalType();
         system.setId(TerminalTypeEnum.INMARSAT_C.getId());
		
         List<Terminaltypeconfiguration> configurationList = new ArrayList<Terminaltypeconfiguration>();
         TerminalConfEnum confEnum = TerminalConfEnum.SATELLITE_NUMBER;
         Terminaltypeconfiguration conf = new Terminaltypeconfiguration();
         conf.setTermtypconfId(confEnum.getId());
         configurationList.add(conf);
         //system.setTerminaltypeconfigurations(configurationList);
		
         TerminalSystem resSystem = EntityToModelMapper.mapToTerminalType(system);
         assertEquals(systemType, resSystem.getSystemType());
         assertEquals(1, resSystem.getConfiguration().size());*/
    }

    @Ignore
    @Test(expected = MobileTerminalModelMapperException.class)
    public void testMapToMobileTerminalTypeNoEntity() throws MobileTerminalModelMapperException {
    	/*
        Terminal entity = null;
        EntityToModelMapper.mapToMobileTerminalType(entity);*/
    }

    @Ignore
    @Test
    public void testMapToMobileTerminalType() throws MobileTerminalModelMapperException {
    	/*
        Terminal entity = new Terminal();
        TerminalSourceEnum sourceEnum = TerminalSourceEnum.INTERNAL;
        MobileTerminalSource resSource = MobileTerminalSource.INTERNAL;
        TerminalSource terminalsource = new TerminalSource();
        terminalsource.setId(sourceEnum.getId());
        entity.setTerminalSource(terminalsource);
        boolean resActive = true;*/
        /*
         if(resActive) {
         entity.setTermActive(MobileTerminalConstants.TRUE);
         } else {
         entity.setTermActive(MobileTerminalConstants.FALSE);
         }*/
/*
        MobileTerminalType mobileTerminal = EntityToModelMapper.mapToMobileTerminalType(entity);
        assertEquals(resSource, mobileTerminal.getSource());

        if (resActive) {
            assertFalse(mobileTerminal.isInactive());
        } else {
            assertTrue(mobileTerminal.isInactive());
        }
*/
    }
/*
    private ComchannelValue getComchannelvalue(Comchannel comchannel, String type, String value) {
        ComchannelValue comchannelvalue = new ComchannelValue();
        comchannelvalue.setComchannel(comchannel);
        ComchannelValueType typeentity = new ComchannelValueType();
		//TODO fix terminal type
		//typeentity.setTerminalType(terminalType);
		comchannelvalue.setComchannelValueType(typeentity);
		comchannelvalue.setValue(value);
		return comchannelvalue;
	}*/
	/*
	private Comchanneleventconnect getComchannelhistory(ComChannelNameEnum vmsEnum, Integer dnid, Integer memberNo, Integer sortOrder, Date startDate, Date stopDate) {
		Comchanneleventconnect history = new Comchanneleventconnect();
		history.setComchaneeventconActive(MobileTerminalConstants.TRUE);
		List<Comchannel> comchannels = new ArrayList<Comchannel>();
		Comchannel comchannel = new Comchannel();
		Comchanneltype comchanneltype = new Comchanneltype();
		comchanneltype.setComchantypId(vmsEnum.getId());
		//comchannel.setComchanneltype(comchanneltype);
		List<ComchannelValue> comchannelvalues = new ArrayList<>();
		comchannelvalues.add(getComchannelvalue(comchannel, ComChannelAttrEnum.DNID, String.valueOf(dnid)));
		comchannelvalues.add(getComchannelvalue(comchannel, ComChannelAttrEnum.MEMBER_ID, String.valueOf(memberNo)));
		comchannelvalues.add(getComchannelvalue(comchannel, ComChannelAttrEnum.START_DATE, String.valueOf(startDate)));
		comchannelvalues.add(getComchannelvalue(comchannel, ComChannelAttrEnum.END_DATE, String.valueOf(stopDate)));
		comchannel.setComchannelvalues(comchannelvalues);
		//comchannel.setComchanSortnum(sortOrder);
		comchannels.add(comchannel);
//		history.setComchannels(comchannels);
		return history;
	}*/
	@Ignore
	@Test(expected=MobileTerminalModelMapperException.class)
	public void testMapToChannelsNoHistory() throws MobileTerminalModelMapperException {
//		Comchanneleventconnect chistories = null;
//		EntityToModelMapper.mapToChannels(chistories);
    }

    @Ignore
    @Test(expected = MobileTerminalModelMapperException.class)
    public void testMapToChannelsNoComChannels() throws MobileTerminalModelMapperException {
//		Comchanneleventconnect history = new Comchanneleventconnect();
//		history.setComchaneeventconActive(MobileTerminalConstants.TRUE);
//		EntityToModelMapper.mapToChannels(history);
    }

    @Ignore
    @Test
    public void testMapToChannels() throws MobileTerminalModelMapperException {
        /*
         ComChannelNameEnum vmsEnum = ComChannelNameEnum.VMS;
         ChannelNameType vmsNameType = ChannelNameType.VMS;
         Integer dnid = 12345;
         Integer memberNo = 98989;
         Integer sortOrder = 1;
         Date startDate = new Date();
         Date stopDate = null;
         Comchanneleventconnect history = getComchannelhistory(vmsEnum, dnid, memberNo, sortOrder, startDate, stopDate);
         List<ComChannelType> channels = EntityToModelMapper.mapToChannels(history);
         assertEquals(1, channels.size());
		
         ComChannelType channel = channels.get(0);
         assertEquals(vmsNameType, channel.getChannelType());
         assertEquals(sortOrder.intValue(), channel.getOrder().intValue());*/
    }
    /*
     private Terminaleventconnect getTerminalhistory(TerminalConfEnum conf, String value) {
     Terminaleventconnect history = new Terminaleventconnect();
     List<TerminalValue> values = new ArrayList<TerminalValue>();
     values.add(getTerminalvalue(conf, value));
     //history.setTerminalvalues(values);
     return history;
     }
	
	
     private TerminalValue getTerminalvalue(TerminalConfEnum conf, String value) {
     TerminalValue termValue = new TerminalValue();
     Terminaltypeconfiguration valueConfiguration = new Terminaltypeconfiguration();
     valueConfiguration.setTermtypconfId(conf.getId());
     //termValue.setTermvalTermtypconf(valueConfiguration);
     termValue.setValue(value);
     return termValue;
     }
     @Ignore
     @Test(expected=MobileTerminalModelMapperException.class)
     public void testMapToAttributesNoHistory() throws MobileTerminalModelMapperException {
     Terminaleventconnect thistories = null;
     EntityToModelMapper.mapToAttributes(thistories);
     }
     @Ignore
     @Test(expected=MobileTerminalModelMapperException.class)
     public void testMapToAttributesNoComChannels() throws MobileTerminalModelMapperException {
     Terminaleventconnect history = new Terminaleventconnect();
     //history.setTermhistActive(MobileTerminalConstants.TRUE);
     EntityToModelMapper.mapToAttributes(history);
     }
     @Ignore
     @Test
     public void testMapToAttributes() throws MobileTerminalModelMapperException {
     TerminalConfEnum software = TerminalConfEnum.SOFTWARE_VERSION;
     String softwareVersion = "ABC99";
     Terminaleventconnect history = getTerminalhistory(software, softwareVersion);
		
     List<MobileTerminalAttribute> attributes = EntityToModelMapper.mapToAttributes(history);
     assertEquals(1, attributes.size());
		
     TerminalConfEnum antenna = TerminalConfEnum.ANTENNA;
     String antennaValue = "009";
     //history.getTerminalvalues().add(getTerminalvalue(antenna, antennaValue));

     attributes = EntityToModelMapper.mapToAttributes(history);
     assertEquals(2, attributes.size());
     }*/

    @Ignore
    @Test
    public void testMapToMobileTerminalId() {
        /*
         Integer internalId = 12345;
         String serialNumber = "12345";
         TerminalSystemType systemType = TerminalSystemType.INMARSAT_C;
		
         TerminalTypeEnum typeEnum = TerminalTypeEnum.INMARSAT_C;
         TerminalType type = new TerminalType();
         type.setId(typeEnum.getId());*/
        /*
         MobileTerminalId resId = EntityToModelMapper.mapToMobileTerminalId(type, serialNumber, internalId);
		
         assertEquals(systemType, resId.getSystemType());
         assertEquals(2, resId.getIdList().size());*/
    }
}