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
    	Date startDate = null;
    	Date endDate = null;
    	Date compareDate = null;
    	assertFalse(DateUtils.isBetween(startDate, endDate, compareDate));
    }
    
    @Test
    public void testIsBetweenSame() {
    	Date startDate = new Date();
    	Date compareDate = new Date();
    	Date endDate = new Date();
    	assertTrue(DateUtils.isBetween(startDate, endDate, compareDate));
    }
    
    @Test
    public void testIsBetweenActive() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2015, 4, 18, 12, 0, 1);
    	Date startDate = cal.getTime();
    	Date compareDate = new Date();
    	Date endDate = null;
    	assertTrue(DateUtils.isBetween(startDate, endDate, compareDate));
    }
    
    @Test
    public void testIsBetween() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2015, 4, 18, 12, 0, 1);
    	Date startDate = cal.getTime();
    	cal.set(2015, 4, 21, 14, 13, 1);
    	Date compareDate = cal.getTime();
    	cal.set(2099, 1, 1, 1, 1, 1);
    	Date endDate = cal.getTime();
    	assertTrue(DateUtils.isBetween(startDate, endDate, compareDate));
    }
    
    @Test
    public void testIsBetweenBefore() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(2015, 4, 18, 12, 0, 1);
    	Date startDate = cal.getTime();
    	cal.set(2015, 4, 21, 14, 13, 1);
    	Date compareDate = cal.getTime();
    	cal.set(2015, 4, 20, 12, 0, 1);
    	Date endDate = cal.getTime();
    	assertFalse(DateUtils.isBetween(startDate, endDate, compareDate));
    }
}