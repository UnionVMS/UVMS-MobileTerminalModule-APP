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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilTest {

    @Test
    public void testIsBetweenNoDate() {
    	final Date startDate = null;
    	final Date endDate = null;
    	final Date compareDate = null;
    	assertFalse(DateUtils.isBetween(startDate, endDate, compareDate));
    }
    
    @Test
    public void testIsBetweenSame() {
    	final Date startDate = new Date();
    	final Date compareDate = new Date();
    	final Date endDate = new Date();
    	assertTrue(DateUtils.isBetween(startDate, endDate, compareDate));
    }
    
    @Test
    public void testIsBetweenActive() {
    	final Calendar cal = Calendar.getInstance();
    	cal.set(2015, 4, 18, 12, 0, 1);
    	final Date startDate = cal.getTime();
    	final Date compareDate = new Date();
    	final Date endDate = null;
    	assertTrue(DateUtils.isBetween(startDate, endDate, compareDate));
    }
    
    @Test
    public void testIsBetween() {
    	final Calendar cal = Calendar.getInstance();
    	cal.set(2015, 4, 18, 12, 0, 1);
    	final Date startDate = cal.getTime();
    	cal.set(2015, 4, 21, 14, 13, 1);
    	final Date compareDate = cal.getTime();
    	cal.set(2099, 1, 1, 1, 1, 1);
    	final Date endDate = cal.getTime();
    	assertTrue(DateUtils.isBetween(startDate, endDate, compareDate));
    }
    
    @Test
    public void testIsBetweenBefore() {
    	final Calendar cal = Calendar.getInstance();
    	cal.set(2015, 4, 18, 12, 0, 1);
    	final Date startDate = cal.getTime();
    	cal.set(2015, 4, 21, 14, 13, 1);
    	final Date compareDate = cal.getTime();
    	cal.set(2015, 4, 20, 12, 0, 1);
    	final Date endDate = cal.getTime();
    	assertFalse(DateUtils.isBetween(startDate, endDate, compareDate));
    }
}