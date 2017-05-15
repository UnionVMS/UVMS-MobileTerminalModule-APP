package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.GetReceivedEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ListReceivedEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.PingReceivedEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalEventServiceBean;

/**
 * Created by roblar on 2017-05-15.
 */

@RunWith(Arquillian.class)
public class MobileTerminalEventServiceBeanIntTest extends TransactionalTests {

	/*
	@EJB
	MobileTerminalEventServiceBean mobileTerminalEventServiceBean;
	*/
	
	@Inject
	@GetReceivedEvent
	Event<EventMessage> getReceivedEvent;
	
	@Inject
	@PingReceivedEvent
	Event<EventMessage> pingReceivedEvent;
	
	@Inject
	@ListReceivedEvent
	Event<EventMessage> listReceivedEvent;
	
	@Test
	public void testTriggerGetReceivedEvent() {
		
	}
	
	@Test
	public void testTriggerListReceivedEvent() {
		
	}
	
	@Test
	public void testTriggerPingReceivedEvent() {
		
	}
}
