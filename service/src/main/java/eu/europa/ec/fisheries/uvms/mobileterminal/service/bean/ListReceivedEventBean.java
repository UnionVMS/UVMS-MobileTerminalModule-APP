package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalBatchListElement;
import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalBatchListRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalListRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.MobileTerminalProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
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
import java.util.ArrayList;
import java.util.List;

@Stateless
@LocalBean
public class ListReceivedEventBean {

    final static Logger LOG = LoggerFactory.getLogger(ListReceivedEventBean.class);

    @EJB
    private MobileTerminalService mobileTerminalService;

    @EJB
    private MobileTerminalProducer messageProducer;

    @Inject
    @ErrorEvent
    private Event<EventMessage> errorEvent;

    public void list(EventMessage message) {
        TextMessage jmsMessage = message.getJmsMessage();
        try {
            MobileTerminalListRequest request = JAXBMarshaller.unmarshallTextMessage(jmsMessage, MobileTerminalListRequest.class);
            MobileTerminalListResponse mobileTerminalListResponse = mobileTerminalService.getMobileTerminalList(request.getQuery());
            List<MobileTerminalType> mobileTerminalTypes = mobileTerminalListResponse.getMobileTerminal();
            String response = MobileTerminalModuleRequestMapper.mapGetMobileTerminalList(mobileTerminalTypes);
            messageProducer.sendResponseToRequestor(jmsMessage, response);
            LOG.info("Response sent back to requestor : [ {} ]", jmsMessage.getJMSReplyTo());
        } catch (MobileTerminalException | MessageException | JMSException e) {
            errorEvent.fire(new EventMessage(jmsMessage, "Exception when trying to get list in MobileTerminal: " + e.getMessage()));
            throw new EJBException(e);
        }
    }

    public void listBatch(EventMessage message) {
        TextMessage jmsMessage = message.getJmsMessage();
        try {
            MobileTerminalBatchListRequest batchListRequest = JAXBMarshaller.unmarshallTextMessage(jmsMessage, MobileTerminalBatchListRequest.class);
            List<MobileTerminalBatchListElement> respList = new ArrayList<>();
            for (MobileTerminalListQuery mobileTerminalListQuery : batchListRequest.getQueryList()) {
                MobileTerminalListResponse mobileTerminalListResponse = mobileTerminalService.getMobileTerminalList(mobileTerminalListQuery);
                MobileTerminalBatchListElement batchListElement = new MobileTerminalBatchListElement();
                batchListElement.getMobileTerminal().addAll(mobileTerminalListResponse.getMobileTerminal());
                respList.add(batchListElement);
            }
            String response = MobileTerminalModuleRequestMapper.mapToMobileTerminalListBatchResponse(respList);
            messageProducer.sendResponseToRequestor(jmsMessage, response);
            LOG.info("Response sent back to requestor : [ {} ]", jmsMessage.getJMSReplyTo());
        } catch (MobileTerminalException | MessageException |JMSException e) {
            errorEvent.fire(new EventMessage(jmsMessage, "Exception when trying to get list in MobileTerminal: " + e.getMessage()));
            // Propagate error
            throw new EJBException(e);
        }
    }
}
