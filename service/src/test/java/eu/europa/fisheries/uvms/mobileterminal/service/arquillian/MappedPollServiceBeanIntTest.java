package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.EJB;
import javax.persistence.Query;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MappedPollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;

/**
 * Created by thofan on 2017-05-04.
 *
 * TODO Since presence of REQUIRES_NEW are very limitied in these classes, we dont test them right now, We will revisit
 */

@RunWith(Arquillian.class)
public class MappedPollServiceBeanIntTest extends TransactionalTests {


    private static final String FIND_BY_GUID = "Poll.findByPollGUID";

    @EJB
    MappedPollService mappedPollService;

    @EJB
    private TerminalDao terminalDaoBean;

    @EJB
    MobileTerminalPluginDao testDaoBean;


    @Test
    @OperateOnDeployment("normal")
    public void createPoll() {

        PollRequestType pollRequestType = helper_createPollRequestType(PollType.MANUAL_POLL);
        try {

            // create a poll
            CreatePollResultDto createPollResultDto = mappedPollService.createPoll(pollRequestType, "TEST");
            em.flush();
            List<String> sendPolls = createPollResultDto.getSentPolls();
            String pollGuid = sendPolls.get(0);

            // try to find it
            Query qry = em.createNamedQuery(FIND_BY_GUID);
            qry.setParameter("guid", pollGuid);

            List<Poll> rs = qry.getResultList();
            if (rs.size() > 0) {
                // verify that we got the correct one
                Poll fetchedPoll = rs.get(0);
                String fetchedGUID = fetchedPoll.getGuid();
                Assert.assertTrue(pollGuid.equals(fetchedGUID));
            } else {
                Assert.fail();
            }
        } catch (MobileTerminalServiceException e) {
            Assert.fail();
        }
    }


    @Test
    @Ignore
    @OperateOnDeployment("normal")
    public void getRunningProgramPolls() {
        // THIS will be enhanced when i understand how to start a PollProgram
        try {
            List<PollDto> rs = mappedPollService.getRunningProgramPolls();
            Assert.assertTrue(rs != null);
        } catch (MobileTerminalServiceException e) {
            Assert.fail();
        }
    }


    @Test
    @Ignore
    @OperateOnDeployment("normal")
    public void startProgramPoll() {

        // TODO THIS will be enhanced when i understand how to start a PollProgram
        // TODO create a poll so we have something to start
        PollRequestType pollRequestType = helper_createPollRequestType(PollType.PROGRAM_POLL);
        try {

            // create a poll
            CreatePollResultDto createPollResultDto = mappedPollService.createPoll(pollRequestType, "TEST");
            em.flush();
            List<String> sendPolls = createPollResultDto.getSentPolls();
            String pollGuid = sendPolls.get(0);

            mappedPollService.startProgramPoll(pollGuid, "TEST");



        } catch (MobileTerminalServiceException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollableChannels() {

        ListPagination listPagination = new ListPagination();

        PollableQuery query = new PollableQuery();
        query.setPagination(listPagination);

        query.getConnectIdList().add("BOGUS_JUST_TO_TEST");
        query.getPagination().setPage(1);
        query.getPagination().setListSize(20);
        try {
            PollChannelListDto pollChannelListDto =  mappedPollService.getPollableChannels(query);
        } catch (MobileTerminalException e) {
            e.printStackTrace();
        }
    }


    private PollRequestType helper_createPollRequestType(PollType pollType) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        SimpleDateFormat format = new SimpleDateFormat();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        String startDate = format.format(cal.getTime());
        cal.set(Calendar.YEAR, 2020);
        String endDate = format.format(cal.getTime());


        PollRequestType prt = new PollRequestType();
        prt.setComment("aComment" + UUID.randomUUID().toString());
        prt.setUserName("TEST");
        prt.setPollType(pollType);
        PollMobileTerminal pollMobileTerminal = helper_createPollMobileTerminal();
        prt.getMobileTerminals().add(pollMobileTerminal);


        PollAttribute psStart = new PollAttribute();
        PollAttribute psEnd = new PollAttribute();
        PollAttribute psFreq = new PollAttribute();

        psStart.setKey(PollAttributeType.START_DATE);
        psStart.setValue(startDate);
        prt.getAttributes().add(psStart);

        psEnd.setKey(PollAttributeType.END_DATE);
        psEnd.setValue(endDate);
        prt.getAttributes().add(psEnd);

        psFreq.setKey(PollAttributeType.FREQUENCY);
        psFreq.setValue("300000");
        prt.getAttributes().add(psFreq);

        return prt;
    }

    private PollMobileTerminal helper_createPollMobileTerminal() {

        String connectId = UUID.randomUUID().toString();

        MobileTerminal mobileTerminal = helper_createMobileTerminal(connectId);
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setConnectId(connectId);
        pmt.setMobileTerminalId(mobileTerminal.getGuid());

        String channelId = "";
        Set<Channel> channels = mobileTerminal.getChannels();
        for (Channel ch : channels) {
            channelId = ch.getGuid();
            break;
        }
        pmt.setComChannelId(channelId);
        return pmt;
    }

    private MobileTerminal helper_createMobileTerminal(String connectId) {


        String serialNo = UUID.randomUUID().toString();
        MobileTerminal mt = new MobileTerminal();

        MobileTerminalPlugin mtp = null;
        List<MobileTerminalPlugin> plugs = null;
        try {
            plugs = testDaoBean.getPluginList();
        } catch (ConfigDaoException e) {
            e.printStackTrace();
        }
        mtp = plugs.get(0);
        mt.setSerialNo(serialNo);
        mt.setUpdateTime(new Date());
        mt.setUpdatedBy("TEST");
        mt.setSource(MobileTerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setInactivated(false);


        Set<MobileTerminalEvent> events = new HashSet<>();
        MobileTerminalEvent event = new MobileTerminalEvent();
        event.setConnectId(connectId);
        event.setActive(true);
        event.setMobileTerminal(mt);


        String attributes = PollAttributeType.START_DATE.value() + "=" + DateUtils.getUTCNow().toString();
        attributes = attributes + ";";
        attributes = attributes + PollAttributeType.END_DATE.value() + "=" + DateUtils.getUTCNow().toString();
        event.setAttributes(attributes);


        events.add(event);
        mt.setMobileTerminalEvents(events);


        Set<Channel> channels = new HashSet<>();
        //Channel channel = new Channel();
        Channel channel = helper_createChannel(mt);
//        channel.setArchived(false);
//        channel.setGuid(UUID.randomUUID().toString());
//        channel.setMobileTerminal(mt);
//        channel.getHistories();
//        channel.getCurrentHistory();

        channels.add(channel);
        mt.setChannels(channels);

        try {
            terminalDaoBean.createMobileTerminal(mt);
            em.flush();
            return mt;
        } catch (TerminalDaoException e) {
            e.printStackTrace();
            return null;
        }


    }


    private Channel helper_createChannel(MobileTerminal mt){

        Channel channel = new Channel();

        channel.setArchived(false);
        channel.setMobileTerminal(mt);
        channel.getHistories();
        channel.getCurrentHistory();

        em.persist(channel);
        em.flush();


        ChannelHistory channelHistory = new ChannelHistory();
        channelHistory.setChannel(channel);
        channelHistory.setActive(true);

        em.persist(channelHistory);

        em.flush();

        return channel;

    }


}
