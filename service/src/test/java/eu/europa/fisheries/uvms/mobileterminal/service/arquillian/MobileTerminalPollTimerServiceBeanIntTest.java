package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.ejb.EJB;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.fisheries.uvms.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PollTimerService;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;

/**
 * Created by roblar on 2017-05-16.
 */

@RunWith(Arquillian.class)
public class MobileTerminalPollTimerServiceBeanIntTest extends TransactionalTests {
	
    @EJB
	//@Inject
    //MobileTerminalPollTimerServiceBean mobileTerminalPollTimerServiceBean;
    PollTimerService pollTimerService;

	@EJB
	MobileTerminalPluginDao testDaoBean;

	@EJB
	private TerminalDao terminalDaoBean;

	@EJB
	private MobileTerminalPluginDao mobileTerminalPluginDao;

    /*
	@Test
    @OperateOnDeployment("normal")
	public void testTimerTimeout() {
		
		
		
	}
	*/
	@EJB
    TestPollHelper testPollHelper;

	@Test
    @OperateOnDeployment("normal")
	public void testTimerTimeout_pollProgramHasNotExpired() throws ParseException, PollDaoException, ConfigDaoException, TerminalDaoException {

		//MobileTerminal mobileTerminal = helper_createMobileTerminal("test");

		String connectId = UUID.randomUUID().toString();
		MobileTerminal mobileTerminal = testPollHelper.createMobileTerminal(connectId);


		//List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();



		String pollProgamGuid = UUID.randomUUID().toString();
		String channelGuid = UUID.randomUUID().toString();
		//String mobileTerminalGuid = UUID.randomUUID().toString();
		String comChannelGuid = UUID.randomUUID().toString();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
		Date now = DateUtils.getUTCNow();
		Date veryFarOut = simpleDateFormat.parse("01/01/9999");

		/*
		MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();
		em.persist(mobileTerminalPlugin);
		em.flush();
		*/


		// Fails if MobileTerminalEvent does no exist.

		/*
		MobileTerminalEvent mobileTerminalEvent = new MobileTerminalEvent();

		MobileTerminal mobileTerminal = new MobileTerminal();
		mobileTerminal.setGuid(mobileTerminalGuid);
		mobileTerminal.setPlugin(mobileTerminalPlugin);
		mobileTerminal.setSource(MobileTerminalSourceEnum.NATIONAL);
		mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
		mobileTerminal.setSerialNo("1");

		mobileTerminalEvent.setMobileTerminal(mobileTerminal);
		Set<MobileTerminalEvent> mobileTerminalEvents = new HashSet<>();
		mobileTerminalEvents.add(mobileTerminalEvent);

		mobileTerminal.setMobileTerminalEvents(mobileTerminalEvents);

		em.persist(mobileTerminal);
		em.persist(mobileTerminalEvent);
		em.flush();
		*/

		PollBase pollBase = new PollBase();
		pollBase.setChannelGuid(channelGuid);
		pollBase.setMobileTerminal(mobileTerminal);
		pollBase.setTerminalConnect("testTerminalConnectId");
		em.persist(pollBase);
		//em.persist(mobileTerminal);
		em.flush();


	    PollProgram pollProgram = new PollProgram();
	    //pollProgram.setId(42L);
	    pollProgram.setGuid(pollProgamGuid);

	    // Test archiving
	    pollProgram.setStartDate(now);
	    pollProgram.setPollBase(pollBase);
	    //pollProgram.setStartDate(veryFarOut);
	    //pollProgram.setStopDate(now);
		pollProgram.setStopDate(veryFarOut);

		pollProgram.setPollState(PollStateEnum.STARTED);
		pollProgram.setFrequency(42);
		//pollProgram.setPollState(PollStateEnum.ARCHIVED);

		//em.merge(pollProgram);
        em.persist(pollProgram);
        em.flush();
	    
	    //Long pollProgramId = pollProgram.getId();

	    //assertNotNull(pollProgramReadFromDatabase);
	    //assertEquals(pollProgramId, pollProgramReadFromDatabase.getId());

        // Fails at PollDataSourceRequestMapper.java, method mapCreatePollRequest(PollResponseType pollProgram) at row 44 if ComChannels not created.
		MobileTerminalType mobileTerminalType = new MobileTerminalType();

		ComChannelType comChannelType = new ComChannelType();
		comChannelType.setGuid(comChannelGuid);
		comChannelType.setName("testComChannelName");
		List<ComChannelType> comChannelTypeList = new ArrayList<ComChannelType>();
		comChannelTypeList.add(comChannelType);

		mobileTerminalType.getChannels().add(comChannelType);

		//ComChannelAttribute comChannelAttribute = new ComChannelAttribute();
		//comChannelAttribute.setValue("testComChannelAttributeValue");
		//comChannelAttribute.setType("testComChannelAttributeType");

		//mobileTerminalType.setArchived(true);
        //em.persist(mobileTerminalType);
        em.flush();


		pollTimerService.timerTimeout();
		
		//PollProgram pollProgramReadFromDatabase = em.find(PollProgram.class, pollProgramId);
		
		//PollProgram pollProgramFromDatabase = em.find(PollProgram.class, pollProgram.getGuid());
		
		//assertNotNull(pollProgramReadFromDatabase);
		//assertThat(pollProgramReadFromDatabase.getPollState(), is(PollStateEnum.ARCHIVED));
		//assertThat(pollProgramReadFromDatabase.getPollState(), is(PollStateEnum.STARTED));
		
		//PollResponseType thisPollShouldBeInactiveIfPollProgramHasExpired = new PollResponseType();
		//thisPollShouldBeInactiveIfPollProgramHasExpired.setPollType(PollType.);
		
		//assertTrue(now.getTime() > timedOut.getTime());
		//assertTrue(timedOut.after(now));
		
	}
	
	@Test
    @OperateOnDeployment("normal")
	public void testTimerTimeout_failPollProgramHasExpired() throws ConfigDaoException, TerminalDaoException, ParseException {

		String connectId = UUID.randomUUID().toString();
		MobileTerminal mobileTerminal = testPollHelper.createMobileTerminal(connectId);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Date now = simpleDateFormat.parse("01/01/9999");
		Date veryFarOut = DateUtils.getUTCNow();

		pollTimerService.timerTimeout();

		
	}

	@Test
    @OperateOnDeployment("normal")
	public void testTimerTimeout_pollProgramNotExpired_requiresCreatePollResultDtoNotNull() {
		
		//CreatePollResultDto
		
	}

	private MobileTerminal helper_createMobileTerminal(String connectId) {

		String serialNo = UUID.randomUUID().toString();
		MobileTerminal mobileTerminal = new MobileTerminal();

		MobileTerminalPlugin mobileTerminalPlugin = null;
		List<MobileTerminalPlugin> mobileTerminalPluginList = null;
		try {
			mobileTerminalPluginList = testDaoBean.getPluginList();
		} catch (ConfigDaoException e) {
			e.printStackTrace();
		}
		mobileTerminalPlugin = mobileTerminalPluginList.get(0);
		mobileTerminal.setSerialNo(serialNo);
		mobileTerminal.setUpdateTime(new Date());
		mobileTerminal.setUpdatedBy("TEST");
		mobileTerminal.setSource(MobileTerminalSourceEnum.INTERNAL);
		mobileTerminal.setPlugin(mobileTerminalPlugin);
		mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
		mobileTerminal.setArchived(false);
		mobileTerminal.setInactivated(false);


		Set<MobileTerminalEvent> events = new HashSet<>();
		MobileTerminalEvent event = new MobileTerminalEvent();
		event.setConnectId(connectId);
		event.setActive(true);
		event.setMobileTerminal(mobileTerminal);


		String attributes = PollAttributeType.START_DATE.value() + "=" + DateUtils.getUTCNow().toString();
		attributes = attributes + ";";
		attributes = attributes + PollAttributeType.END_DATE.value() + "=" + DateUtils.getUTCNow().toString();
		event.setAttributes(attributes);


		events.add(event);
		mobileTerminal.setMobileTerminalEvents(events);


		Set<Channel> channels = new HashSet<>();
		Channel channel = new Channel();
		channel.setArchived(false);
		channel.setGuid(UUID.randomUUID().toString());
		channel.setMobileTerminal(mobileTerminal);
		channel.getHistories();
		channels.add(channel);
		mobileTerminal.setChannels(channels);

		try {
			terminalDaoBean.createMobileTerminal(mobileTerminal);
			em.flush();
			return mobileTerminal;
		} catch (TerminalDaoException e) {
			e.printStackTrace();
			return null;
		}
	}
}