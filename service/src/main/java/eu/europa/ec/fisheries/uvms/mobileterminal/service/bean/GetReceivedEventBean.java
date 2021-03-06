package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.GetMobileTerminalRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MobileTerminalProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalUnmarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

@Stateless
@LocalBean
public class GetReceivedEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(GetReceivedEventBean.class);

    // TODO: NOOOOOOOOOO, Config Module is locally deployed in MobileTerminal...
    @EJB
    private ParameterService parameters;

    @EJB
    private MobileTerminalService service;

    @EJB
    private MobileTerminalProducer messageProducer;

    @Inject
    @ErrorEvent
    private Event<EventMessage> errorEvent;

    public void get(EventMessage message) {
        TextMessage jmsMessage = message.getJmsMessage();
        try {
            MobileTerminalType mobileTerminal = getMobileTerminal(message);
            String response = MobileTerminalModuleRequestMapper.createMobileTerminalResponse(mobileTerminal);
            messageProducer.sendResponseToRequestor(jmsMessage, response);
            LOG.info("Response sent back to requestor : [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (MobileTerminalModelMapperException | JMSException | MessageException e) {
            errorEvent.fire(new EventMessage(jmsMessage, "Exception when trying to get a MobileTerminal: " + e.getMessage()));
            // Propagate error
            throw new EJBException(e);
        }
    }

    // TODO: Go through this logic and error handling
    private MobileTerminalType getMobileTerminal(EventMessage message) {
        GetMobileTerminalRequest request = null;
        MobileTerminalType mobTerm;
        DataSourceQueue dataSource = null;
        try {
            request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
        } catch (MobileTerminalUnmarshallException ex) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Error when mapping message: " + ex.getMessage()));
        }
        try {
            dataSource = decideDataflow();
        } catch (Exception ex) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when deciding Dataflow for : " + dataSource.name() + " Error message: " + ex.getMessage()));
        }
        try {
            LOG.debug("Got message to MobileTerminalModule, Executing Get MobileTerminal from datasource {}", dataSource.name());
            mobTerm = service.getMobileTerminalById(request.getId(), dataSource);
            if (!dataSource.equals(DataSourceQueue.INTERNAL)) {
                service.upsertMobileTerminal(mobTerm, MobileTerminalSource.NATIONAL, dataSource.name());
            }
        } catch (MobileTerminalException ex) {
            mobTerm = null;
        }
        if (mobTerm == null) {
            LOG.debug("Trying to retrieve MobileTerminal from datasource: {0} as second option", DataSourceQueue.INTERNAL.name());
            try {
                request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
                mobTerm = service.getMobileTerminalById(request.getId(), DataSourceQueue.INTERNAL);
            } catch (MobileTerminalException ex) {
                errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when getting vessel from source : " + dataSource.name() + " Error message: " + ex.getMessage()));
            }
        }
        return mobTerm;
    }

    private DataSourceQueue decideDataflow() throws MobileTerminalServiceException {
        try {
            Boolean national = parameters.getBooleanValue(ParameterKey.USE_NATIONAL.getKey());
            LOG.debug("Settings for dataflow are: NATIONAL: {}", national.toString());
            if (national) {
                return DataSourceQueue.INTEGRATION;
            }
            return DataSourceQueue.INTERNAL;
        } catch (ConfigServiceException ex) {
            LOG.error("[ Error when deciding data flow. ] {}", ex.getMessage());
            throw new MobileTerminalServiceException(ex.getMessage());
        }
    }
}
