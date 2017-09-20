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
package eu.europa.ec.fisheries.uvms.mobileterminal.constant;

import java.util.Date;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.inmarsatc.InmarsatCHistoryOceanRegion;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;

public class EqualsUtil {

	public static boolean compare(final String one, final String two) {
		if(one == null && two == null) return true;
		if(one == null) return false;
		return one.equalsIgnoreCase(two);
	}
	
	public static boolean compare(final Date one, final Date two) {
		return DateUtils.equalsDate(one, two);
	}
	
	public static boolean compare(final Integer one, final Integer two) {
		if(one == null && two == null) return true;
		if(one == null) return false;
		return one.equals(two);
	}
	
	public static boolean compare(final Boolean one, final Boolean two) {
		if(one == null && two == null) return true;
		if(one == null) return false;
		return one.equals(two);
	}
	
	public static int getHashCode(final Object obj) {
		if(obj == null) return 0;
		return obj.hashCode();
	}

	public static boolean equalsOceanRegions(final Set<InmarsatCHistoryOceanRegion> oceanRegionsOne, final Set<InmarsatCHistoryOceanRegion> oceanRegionsTwo) {
	    final boolean hasOceanRegionsOne = !CollectionUtils.isEmpty(oceanRegionsOne);
	    final boolean hasOceanRegionsTwo = !CollectionUtils.isEmpty(oceanRegionsTwo);

	    if (!hasOceanRegionsOne && !hasOceanRegionsTwo) {
	        // Neither has ocean regions
	        return true;
	    }
	    else if (hasOceanRegionsOne ^ hasOceanRegionsTwo) {
	        // One has and the other hasn't
	        return false;
	    }
	    else {
	        // True iff both sets of regions are equal
	        return CollectionUtils.isEqualCollection(oceanRegionsOne, oceanRegionsTwo);
	    }
	}

}