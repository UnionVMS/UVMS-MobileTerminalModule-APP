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

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ComchannelNameResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.UpdatedDNIDListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.UpsertPluginListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.PingResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.HistoryMobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalFault;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalHistory;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalFaultException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalUnmarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalValidationException;

/**
 **/
public class MobileTerminalDataSourceResponseMapper {

    private static Logger LOG = LoggerFactory.getLogger(MobileTerminalDataSourceResponseMapper.class);

    /**
     * Validates a response
     *
     * @param response
     * @param correlationId
     * @throws MobileTerminalModelMapperException
     * @throws JMSException
     * @throws MobileTerminalValidationException
     * @throws MobileTerminalFaultException 
     */
    private static void validateResponse(final TextMessage response, final String correlationId) throws JMSException, MobileTerminalValidationException, MobileTerminalFaultException {

        if (response == null) {
            throw new MobileTerminalValidationException("Error when validating response in ResponseMapper: Response is Null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new MobileTerminalValidationException("No correlationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new MobileTerminalValidationException("Wrong correlationId in response. Expected was: " + correlationId + "But actual was: " + response.getJMSCorrelationID());
        }

        try {
            final MobileTerminalFault fault = JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalFault.class);
            throw new MobileTerminalFaultException("Fault found when validate response", fault);
        } catch (final MobileTerminalUnmarshallException e) {
            //everything is well
        }
    }

    /**
     *
     * @param response
     * @param messageId
     * @return
     * @throws MobileTerminalModelMapperException
     */
    public static MobileTerminalType mapToMobileTerminalFromResponse(final TextMessage response, final String messageId) throws MobileTerminalModelException {
        try {
            validateResponse(response, messageId);
            final MobileTerminalResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalResponse.class);
            return unmarshalledResponse.getMobilTerminal();
        } catch (MobileTerminalUnmarshallException | MobileTerminalValidationException | JMSException e) {
            LOG.error("[ Error when mapping response to mobile terminal. ] " + e.getMessage());
            throw new MobileTerminalModelMapperException(e.getMessage());
        }

    }

    public static boolean mapDNIDUpdatedMobileTerminalResponse(final TextMessage response, final String messageId) throws MobileTerminalModelException {
    	try {
            validateResponse(response, messageId);
            final MobileTerminalResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalResponse.class);
            return unmarshalledResponse.isDnidListUpdated();
        } catch (MobileTerminalUnmarshallException | MobileTerminalValidationException | JMSException e) {
            LOG.error("[ Error when mapping response to mobile terminal. DNIDList updated] " + e.getMessage());
            throw new MobileTerminalModelMapperException(e.getMessage());
        }
    }
    
    /**
     *
     * @param response
     * @param correlationId
     * @return
     * @throws MobileTerminalModelMapperException
     */
    public static MobileTerminalListResponse mapToMobileTerminalListFromResponse(final TextMessage response, final String correlationId) throws MobileTerminalModelException {
        try {
            validateResponse(response, correlationId);
            final MobileTerminalListResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, MobileTerminalListResponse.class);
            return mappedResponse;
        } catch (MobileTerminalUnmarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to mobile terminal list. ] {}", e.getMessage());
            throw new MobileTerminalModelMapperException("Error when returning mobile terminal list from response in ResponseMapper: " + e.getMessage());
        }
    }

    /**
     *
     * @param response
     * @param correlationId
     * @return
     * @throws MobileTerminalModelMapperException
     */
    public static List<TerminalSystemType> mapToTerminalSystemList(final TextMessage response, final String correlationId) throws MobileTerminalModelException {
        try {
            validateResponse(response, correlationId);
            final TerminalSystemListResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, TerminalSystemListResponse.class);
            return mappedResponse.getTerminalSystem();
        } catch (MobileTerminalUnmarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to terminal system list. ] {}", e.getMessage());
            throw new MobileTerminalModelMapperException("Error when returning mobile terminal list from response in ResponseMapper: " + e.getMessage());
        }
    }

	public static List<String> mapToChannelNames(final TextMessage response, final String correlationId) throws MobileTerminalModelException {
		try {
            validateResponse(response, correlationId);
            final ComchannelNameResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ComchannelNameResponse.class);
            return mappedResponse.getComchannelName();
        } catch (MobileTerminalUnmarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to comchannel list. ] {}", e.getMessage());
            throw new MobileTerminalModelMapperException("Error when returning comchannel names from response in ResponseMapper: " + e.getMessage());
        }
	}
    
    /**
     *
     * @param response
     * @param correlationId
     * @return
     * @throws MobileTerminalModelMapperException
     */
    public static List<MobileTerminalHistory> mapToHistoryList(final TextMessage response, final String correlationId) throws MobileTerminalModelException {
        try {
            validateResponse(response, correlationId);
            final HistoryMobileTerminalListResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, HistoryMobileTerminalListResponse.class);
            return mappedResponse.getHistory();
        } catch (MobileTerminalUnmarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to mobile terminal history. ] {}", e.getMessage());
            throw new MobileTerminalModelMapperException("Error when returning mobile terminal list from response in ResponseMapper: " + e.getMessage());
        }
    }

    public static List<ConfigList> mapToConfigList(final TextMessage response, final String correlationId) throws MobileTerminalModelException {
    	try {
    		validateResponse(response, correlationId);
    		final ConfigResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, ConfigResponse.class);
    		return mappedResponse.getConfig();
    	} catch (MobileTerminalUnmarshallException | JMSException e) {
    		LOG.error("[ Error when mapping response to config list. ] {}", e.getMessage());
    		throw new MobileTerminalModelMapperException("Error when returning mobile terminal list from response in ResponseMapper: " + e.getMessage());
    	}
	}
    
    public static List<Plugin> mapToPluginList(final TextMessage response, final String correlationId) throws MobileTerminalModelException {
    	try {
            validateResponse(response, correlationId);
            final UpsertPluginListResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, UpsertPluginListResponse.class);
            return mappedResponse.getPlugin();
        } catch (MobileTerminalUnmarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to plugin list. ] ");
            throw new MobileTerminalModelMapperException("Error when returning plugin list from response in ResponseMapper: " + e.getMessage());
        }
	}
    
    public static UpdatedDNIDListResponse mapToUpdatedDNIDList(final TextMessage response, final String correlationId) throws MobileTerminalModelException {
    	try {
            validateResponse(response, correlationId);
            final UpdatedDNIDListResponse mappedResponse = JAXBMarshaller.unmarshallTextMessage(response, UpdatedDNIDListResponse.class);
            return mappedResponse;
        } catch (MobileTerminalUnmarshallException | JMSException e) {
            LOG.error("[ Error when mapping response to updated DNID list response. ] ");
            throw new MobileTerminalModelMapperException("Error when returning updated DNID List from response in ResponseMapper: " + e.getMessage());
        }
    }
    
    /**
     * @param systemList
     * @return
     * @throws MobileTerminalModelMapperException
     */
    public static String createTerminalSystemListResponse(final List<TerminalSystemType> systemList) throws MobileTerminalModelException {
        final TerminalSystemListResponse response = new TerminalSystemListResponse();
        response.getTerminalSystem().addAll(systemList);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    /**
     *
     * @param message
     * @return
     */
    public static MobileTerminalFault createFaultMessage(final int code, final String message) {
        final MobileTerminalFault fault = new MobileTerminalFault();
        fault.setCode(code);
        fault.setMessage(message);
        return fault;
    }

    /**
     *
     * @param terminal
     * @return
     * @throws MobileTerminalModelMapperException
     */
    public static String createMobileTerminalResponse(final MobileTerminalType terminal) throws MobileTerminalModelException {
        final MobileTerminalResponse response = new MobileTerminalResponse();
        response.setMobilTerminal(terminal);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }

    public static String createPingResponse(final String responseMessage) throws MobileTerminalModelMapperException {
		final PingResponse pingResponse = new PingResponse();
		pingResponse.setResponse(responseMessage);
		return JAXBMarshaller.marshallJaxBObjectToString(pingResponse);
    }
}