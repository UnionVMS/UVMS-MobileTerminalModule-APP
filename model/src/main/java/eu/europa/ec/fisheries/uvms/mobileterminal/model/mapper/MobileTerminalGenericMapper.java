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
package eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;

public class MobileTerminalGenericMapper {
    private static Logger LOG = LoggerFactory.getLogger(MobileTerminalGenericMapper.class);

    public static String getComChannelTypeValue(final ComChannelType comchannel, final String type) throws MobileTerminalModelMapperException {
        for (final ComChannelAttribute comChannelAttr : comchannel.getAttributes()) {
            if (comChannelAttr.getType().equalsIgnoreCase(type)) {
                return comChannelAttr.getValue();
            }
        }
        throw new MobileTerminalModelMapperException("Could not get value for ComChannelIdType " + type);
    }

    public static String getPollAttributeTypeValue(final List<PollAttribute> attributes, final PollAttributeType key) throws MobileTerminalModelMapperException {
        for (final PollAttribute attribute : attributes) {
            if (attribute.getKey().equals(key)) {
                return attribute.getValue();
            }
        }
        throw new MobileTerminalModelMapperException("Could not map PollAttributeType key to value");
    }

    public static ComChannelAttribute createComChannelAttribute(final String value, final String type) {
        final ComChannelAttribute id = new ComChannelAttribute();
        id.setType(type);
        id.setValue(value);
        return id;
    }

    public static MobileTerminalAttribute createMobileTerminalAttribute(final String type, final String value) {
        final MobileTerminalAttribute attribute = new MobileTerminalAttribute();
        attribute.setType(type);
        attribute.setValue(value);
        return attribute;
    }
}