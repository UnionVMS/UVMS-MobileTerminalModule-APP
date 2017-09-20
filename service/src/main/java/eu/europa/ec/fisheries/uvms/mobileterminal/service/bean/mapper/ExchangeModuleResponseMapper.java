package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ExchangeFault;
import eu.europa.ec.fisheries.schema.exchange.module.v1.*;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.uvms.exchange.model.constant.FaultCode;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeValidationException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

public class ExchangeModuleResponseMapper {

	private static Logger LOG = LoggerFactory.getLogger(ExchangeModuleResponseMapper.class);
	
    public static void validateResponse(final TextMessage response, final String correlationId) throws JMSException, ExchangeValidationException {

        if (response == null) {
            throw new ExchangeValidationException("Error when validating response in ResponseMapper: Response is Null");
        }

        if (response.getJMSCorrelationID() == null) {
            throw new ExchangeValidationException("No corelationId in response (Null) . Expected was: " + correlationId);
        }

        if (!correlationId.equalsIgnoreCase(response.getJMSCorrelationID())) {
            throw new ExchangeValidationException("Wrong corelationId in response. Expected was: " + correlationId + "But actual was: "
                    + response.getJMSCorrelationID());
        }

        try {
			final ExchangeFault fault = JAXBMarshaller.unmarshallTextMessage(response, ExchangeFault.class);
	        //TODO use fault
			throw new ExchangeValidationException(fault.getCode() + " - " + fault.getMessage());
		} catch (final ExchangeModelMarshallException e) {
			//everything went well
		}        
    }

    public static AcknowledgeType mapAcknowledgeTypeOK() {
    	final AcknowledgeType ackType = new AcknowledgeType();
    	ackType.setType(AcknowledgeTypeType.OK);
    	return ackType;
    }
    
    public static AcknowledgeType mapAcknowledgeTypeOK(final String messageId, final String message) {
        final AcknowledgeType ackType = new AcknowledgeType();
        ackType.setType(AcknowledgeTypeType.OK);
        ackType.setMessage(message);
        ackType.setMessageId(messageId);
        return ackType;
    }

    public static AcknowledgeType mapAcknowledgeTypeNOK(final String messageId, final String errorMessage) {
    	final AcknowledgeType ackType = new AcknowledgeType();
    	ackType.setMessage(errorMessage);
    	ackType.setMessageId(messageId);
    	ackType.setType(AcknowledgeTypeType.NOK);
    	return ackType;
    }
    
    public static String mapSetCommandResponse(final AcknowledgeType ackType) throws ExchangeModelMarshallException {
        final SetCommandResponse response = new SetCommandResponse();
        response.setResponse(ackType);
        return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
    
    public static String mapSendMovementToPluginResponse(final AcknowledgeType ackType) throws ExchangeModelMarshallException {
    	final SendMovementToPluginResponse response = new SendMovementToPluginResponse();
    	response.setResponse(ackType);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
	}
    
    public static String mapUpdateSettingResponse(final AcknowledgeType ackType) throws ExchangeModelMarshallException {
    	final UpdatePluginSettingResponse response = new UpdatePluginSettingResponse();
    	response.setResponse(ackType);
    	return JAXBMarshaller.marshallJaxBObjectToString(response);
    }
    
    public static ExchangeFault createFaultMessage(final FaultCode code, final String message) {
    	final ExchangeFault fault = new ExchangeFault();
    	fault.setCode(code.getCode());
    	fault.setMessage(message);
    	return fault;
    }

	public static String mapServiceListResponse(final List<ServiceResponseType> serviceList) throws ExchangeModelMarshallException {
		final GetServiceListResponse response = new GetServiceListResponse();
		response.getService().addAll(serviceList);
		return JAXBMarshaller.marshallJaxBObjectToString(response);
	}
	
	public static List<ServiceResponseType> mapServiceListResponse(final TextMessage response, final String correlationId) throws ExchangeModelMapperException {
		try {
			validateResponse(response, correlationId);
			final GetServiceListResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, GetServiceListResponse.class);
			return unmarshalledResponse.getService();
		} catch(JMSException | ExchangeValidationException e) {
			LOG.error("[ Error when mapping response to service types ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to service types ] " + e.getMessage());
		}
	}

	public static AcknowledgeType mapSetCommandResponse(final TextMessage response, final String correlationId) throws ExchangeModelMapperException {
		try {
			validateResponse(response, correlationId);
			final SetCommandResponse unmarshalledResponse = JAXBMarshaller.unmarshallTextMessage(response, SetCommandResponse.class);
			return unmarshalledResponse.getResponse();
			//TODO handle ExchangeValidationException - extract fault...
		} catch(JMSException | ExchangeModelMarshallException e) {
			LOG.error("[ Error when mapping response to service types ]");
			throw new ExchangeModelMapperException("[ Error when mapping response to service types ] " + e.getMessage());
		}
	}
}