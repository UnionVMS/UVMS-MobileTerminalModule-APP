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

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HistoryMapperTest {

    private Date createDate;
    private Date inactivateDate;
    private Date modifyDate;

    @Before
    public void init() {
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 05, 18, 12, 00);
        createDate = cal.getTime();
        cal.set(2015, 05, 19, 12, 00);
        inactivateDate = cal.getTime();
        cal.set(2015, 05, 20, 12, 00);
        modifyDate = cal.getTime();
    }
    
    @Test
    public void testGetHistory() {
    }
/*
    @Test(expected = MobileTerminalModelMapperException.class)
    public void testGetHistoryException() throws MobileTerminalModelMapperException {
        Terminal terminal = null;
        HistoryMapper.getHistory(terminal);
    }

    @Test
    public void testGetEmptyHistory() throws MobileTerminalModelMapperException {
        Terminal terminal = new Terminal();
        terminal.setTerminalEvents(new HashSet<TerminalEvent>());
        List<MobileTerminalHistory> history = HistoryMapper.getHistory(terminal);
        assertEquals(0, history.size());
    }

    private TerminalEventType getEventEntity(EventCodeEnum eventCode) {
        TerminalEventType type = new TerminalEventType();
        type.setId(eventCode.getId());
        return type;
    }

    private TerminalSource getTerminalSourceEntity(TerminalSourceEnum source) {
        TerminalSource entity = new TerminalSource();
        entity.setId(source.getId());
        return entity;
    }

    private TerminalValueType getTerminalValueTypeEntity(String name) {
        TerminalValueType type = new TerminalValueType();
        type.setName(name);
        return type;
    }

    private TerminalEvent getModifyEvent(Terminal terminal) {
        TerminalEvent inactivate = new TerminalEvent();
        inactivate.setComment("modify");
        inactivate.setTerminal(terminal);
        inactivate.setTerminalEventType(getEventEntity(EventCodeEnum.MODIFY));
        inactivate.setUpdatedBy("test");
        inactivate.setUpdateTime(modifyDate);
        return inactivate;
    }

    private TerminalEvent getInactivateEvent(Terminal terminal) {
        TerminalEvent inactivate = new TerminalEvent();
        inactivate.setComment("inactivate");
        inactivate.setTerminal(terminal);
        inactivate.setTerminalEventType(getEventEntity(EventCodeEnum.INACTIVATE));
        inactivate.setUpdatedBy("test");
        inactivate.setUpdateTime(inactivateDate);
        return inactivate;
    }

    private TerminalEvent getCreateEvent(Terminal terminal) {
        TerminalEvent create = new TerminalEvent();
        create.setComment("create");
        create.setTerminal(terminal);
        create.setTerminalEventType(getEventEntity(EventCodeEnum.CREATE));
        create.setUpdatedBy("test");
        create.setUpdateTime(createDate);
        return create;
    }

    private TerminalType getTerminalType(String type) {
        TerminalType terminalType = new TerminalType();
        terminalType.setName(type);
        return terminalType;
    }

    private Terminal getTerminal() {
        Terminal terminal = new Terminal();
        terminal.setGuid("guid-1234");
        terminal.setTerminalType(getTerminalType("INMARSAT_C"));
        terminal.setTerminalSource(getTerminalSourceEntity(TerminalSourceEnum.INTERNAL));

        List<TerminalEvent> terminalevents = new ArrayList<>();
        TerminalEvent createEvent = getCreateEvent(terminal);
        terminalevents.add(createEvent);
        terminal.setTerminalEvents(new HashSet<>(terminalevents));

        List<TerminalValue> terminalvalues = new ArrayList<>();

        TerminalValue oneValue = new TerminalValue();
        oneValue.setActive(true);
        oneValue.setEndDate(null);
        oneValue.setStartDate(createDate);
        oneValue.setTerminal(terminal);
        oneValue.setTerminalValueType(getTerminalValueTypeEntity("ONE_VALUE"));
        oneValue.setUpdatedBy("test");
        oneValue.setUpdateTime(createDate);
        oneValue.setValue("one value");

        terminalvalues.add(oneValue);
        terminal.setTerminalValues(new HashSet<>(terminalvalues));

        terminal.setComChannels(new HashSet<Comchannel>());
        return terminal;
    }

    private Terminal getInactivedTerminal() {
        Terminal terminal = new Terminal();
        terminal.setGuid("guid-98765");
        terminal.setTerminalType(getTerminalType("INMARSAT_C"));
        terminal.setTerminalSource(getTerminalSourceEntity(TerminalSourceEnum.INTERNAL));

        List<TerminalEvent> terminalevents = new ArrayList<>();
        TerminalEvent createEvent = getCreateEvent(terminal);
        TerminalEvent inactivateEvent = getInactivateEvent(terminal);
        TerminalEvent modifyEvent = getModifyEvent(terminal);
        terminalevents.add(createEvent);
        terminalevents.add(inactivateEvent);
        terminalevents.add(modifyEvent);
        terminal.setTerminalEvents(new HashSet<>(terminalevents));

        List<TerminalValue> terminalvalues = new ArrayList<>();

        TerminalValue oneValue = new TerminalValue();
        oneValue.setActive(false);
        oneValue.setEndDate(modifyDate);
        oneValue.setStartDate(createDate);
        oneValue.setTerminal(terminal);
        oneValue.setTerminalValueType(getTerminalValueTypeEntity("ONE_VALUE"));
        oneValue.setUpdatedBy("test");
        oneValue.setUpdateTime(modifyDate);
        oneValue.setValue("one changed value");

        terminalvalues.add(oneValue);

        TerminalValue oneChangedValue = new TerminalValue();
        oneChangedValue.setActive(true);
        oneChangedValue.setEndDate(null);
        oneChangedValue.setStartDate(modifyDate);
        oneChangedValue.setTerminal(terminal);
        oneChangedValue.setTerminalValueType(getTerminalValueTypeEntity("ONE_VALUE"));
        oneChangedValue.setUpdatedBy("test");
        oneChangedValue.setUpdateTime(modifyDate);
        oneChangedValue.setValue("one changed value");

        terminal.setTerminalValues(new HashSet<>(terminalvalues));

        terminal.setComChannels(new HashSet<Comchannel>());
        return terminal;
    }

    @Test
    public void testGetTerminalHistory() throws MobileTerminalModelMapperException {
        Terminal terminal = getTerminal();

        List<MobileTerminalHistory> history = HistoryMapper.getHistory(terminal);
        assertEquals(1, history.size());
    }

    @Test
    public void testGetTerminalHistoryInactive() throws MobileTerminalModelMapperException {
        Terminal terminal = getInactivedTerminal();

        List<MobileTerminalHistory> history = HistoryMapper.getHistory(terminal);
        assertEquals(3, history.size());

        for (MobileTerminalHistory hist : history) {
            if (EventCode.CREATE.equals(hist.getEventCode())) {
                assertFalse(hist.getMobileTerminal().isInactive());
            } else if (EventCode.INACTIVATE.equals(hist.getEventCode())) {
                assertTrue(hist.getMobileTerminal().isInactive());
            } else {
                assertTrue(hist.getMobileTerminal().isInactive());
            }
        }
    }*/
}