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
package eu.europa.fisheries.uvms.mobileterminal.service.constant;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.EqualsUtil;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.inmarsatc.InmarsatCHistoryOceanRegion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TestEqualsUtil {

    private InmarsatCHistoryOceanRegion AOR_E = getOceanRegion(581, "EAST_ATLANTIC");
    private InmarsatCHistoryOceanRegion POR = getOceanRegion(582, "PACIFIC");
    private InmarsatCHistoryOceanRegion IOR = getOceanRegion(583, "INDIAN");
    private InmarsatCHistoryOceanRegion AOR_W = getOceanRegion(584, "WEST_ATLANTIC");

    @Test
    public void testEqualsOceanRegionsBothEmpty() {
        assertTrue("should be equal if both region sets are null", 
                EqualsUtil.equalsOceanRegions(null, null));
        assertTrue("should be equal if both region sets are empty", 
                EqualsUtil.equalsOceanRegions(new HashSet<InmarsatCHistoryOceanRegion>(), new HashSet<InmarsatCHistoryOceanRegion>()));
        assertTrue("should be equal if one is null and the other is empty",
                EqualsUtil.equalsOceanRegions(null, new HashSet<InmarsatCHistoryOceanRegion>()));
        assertTrue("should be equal if one is null and the other is empty",
                EqualsUtil.equalsOceanRegions(new HashSet<InmarsatCHistoryOceanRegion>(), null));        
    }

    @Test
    public void testEqualsOceanRegionsOneEmpty() {
        assertFalse("should not be equal if one is null and the other has regions",
                EqualsUtil.equalsOceanRegions(null, new HashSet<>(Arrays.asList(AOR_W))));
        assertFalse("should not be equal if one is null and the other has regions",
                EqualsUtil.equalsOceanRegions(new HashSet<>(Arrays.asList(AOR_W)), null));
    }

    @Test
    public void testEqualsOceanRegionsSameSet() {
        assertTrue("should be equal if ocean region sets are the same",
                EqualsUtil.equalsOceanRegions(new HashSet<>(Arrays.asList(AOR_W, POR)), new HashSet<>(Arrays.asList(POR, AOR_W))));
    }

    @Test
    public void testEqualsOceanRegionsDifferentSets() {
        assertFalse("should not be equal if different sets of ocean regions",
                EqualsUtil.equalsOceanRegions(new HashSet<>(Arrays.asList(AOR_W, IOR)), new HashSet<>(Arrays.asList(AOR_W, POR))));
    }

    @Test
    public void testCompareString() {
        assertTrue(EqualsUtil.compare("test", new String("test")));
        assertFalse(EqualsUtil.compare("test", new String("Test")));
        assertFalse(EqualsUtil.compare("", null));
    }

    @Test
    public void testCompareDate() {
        assertTrue(EqualsUtil.compare(parseDate("2000-01-01"), parseDate("2000-01-01")));
        assertFalse(EqualsUtil.compare(parseDate("2000-01-01"), parseDate("2010-01-01")));
        assertFalse(EqualsUtil.compare(parseDate("2000-01-01"), null));
    }

    @Test
    public void testCompareInteger() {
        assertTrue(EqualsUtil.compare(10, new Integer(10)));
        assertFalse(EqualsUtil.compare(10, new Integer(100)));
        assertFalse(EqualsUtil.compare(10, null));
    }

    @Test
    public void testCompareBoolean() {
        assertTrue(EqualsUtil.compare(true, true));
        assertFalse(EqualsUtil.compare(true, false));
        assertFalse(EqualsUtil.compare(true, null));
    }

    private InmarsatCHistoryOceanRegion getOceanRegion(Integer code, String name) {
        InmarsatCHistoryOceanRegion oceanRegion = new InmarsatCHistoryOceanRegion();
        oceanRegion.setCode(code);
        oceanRegion.setName(name);
        return oceanRegion;
    }

    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
