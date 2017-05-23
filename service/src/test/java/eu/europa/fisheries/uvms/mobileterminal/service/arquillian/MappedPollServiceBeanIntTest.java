package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.ejb.EJB;
import javax.persistence.Query;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollMobileTerminal;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MappedPollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;

/**
 * Created by thofan on 2017-05-04.
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

        PollRequestType pollRequestType = helper_createPollRequestType();
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
            }else{
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


        try {
            List<PollDto> rs = mappedPollService.getRunningProgramPolls();
        } catch (MobileTerminalServiceException e) {
            e.printStackTrace();
        }


    }


    /*

    //@Test
    @OperateOnDeployment("normal")
    public void startProgramPoll()  {}

    //@Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll()  {}

    //@Test
    @OperateOnDeployment("normal")
    public void inactivateProgramPoll() {}

    //@Test
    @OperateOnDeployment("normal")
    public void getPollBySearchQuery()  {}

    //@Test
    @OperateOnDeployment("normal")
    public void getPollableChannels()  {}



*/


    private PollRequestType helper_createPollRequestType() {

        PollRequestType prt = new PollRequestType();
        prt.setComment("aComment" + UUID.randomUUID().toString());
        prt.setUserName("TEST");
        prt.setPollType(PollType.MANUAL_POLL);
        PollMobileTerminal pollMobileTerminal = helper_createPollMobileTerminal();
        prt.getMobileTerminals().add(pollMobileTerminal);


        PollAttribute pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.START_DATE);
        String startDate = DateUtils.getUTCNow().toString();
        pollAttribute.setValue(startDate);

        prt.getAttributes().add(pollAttribute);
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
        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setGuid(UUID.randomUUID().toString());
        channel.setMobileTerminal(mt);
        channel.getHistories();
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


}
