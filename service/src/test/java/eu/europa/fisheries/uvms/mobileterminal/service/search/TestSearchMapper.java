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
package eu.europa.fisheries.uvms.mobileterminal.service.search;

import eu.europa.ec.fisheries.uvms.mobileterminal.search.SearchField;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.SearchFieldHolder;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.SearchTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TestSearchMapper {

    private SearchKeyValue searchKeyValue;

    private SearchKeyValue searchKeyValueWildcard;

    @Before
    public void setup() {
        searchKeyValue = getSearchKeyValue(Arrays.asList("someValue", "someOtherValue"));
        searchKeyValueWildcard = getSearchKeyValue(Arrays.asList("someValue*", "someOtherValue"));
    }

    private SearchKeyValue getSearchKeyValue(List<String> values) {
        SearchKeyValue skv = new SearchKeyValue();
        SearchFieldHolder sfh = new SearchFieldHolder(SearchTable.IRIDIUM, SearchField.SATELLITE_NUMBER);
        skv.setSearchFieldHolder(sfh);
        skv.setValues(values);
        return skv;
    }

    @Test
    public void testCreateSelectSearchSql() {}

}