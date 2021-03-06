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

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ComchannelNameRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemListRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.UpdatedDNIDListRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.UpdatedDNIDListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.UpsertPluginListRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.CreateMobileTerminalRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.GetMobileTerminalRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.HistoryMobileTerminalListRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalAssignRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalUnAssignRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.SetStatusMobileTerminalRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.UpdateMobileTerminalRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.UpsertMobileTerminalRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAssignQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalDataSourceMethod;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalStatus;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelValidationException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.validator.MobileTerminalDataSourceRequestValidator;

/**
 **/
public class MobileTerminalDataSourceRequestMapper {

    public static String mapGetMobileTerminal(MobileTerminalId mobileTerminalId) throws MobileTerminalModelMapperException {
        GetMobileTerminalRequest request = new GetMobileTerminalRequest();
        request.setMethod(MobileTerminalDataSourceMethod.GET);
        request.setId(mobileTerminalId);
        return JAXBMarshaller.marshallJaxBObjectToString(request);
    }
}