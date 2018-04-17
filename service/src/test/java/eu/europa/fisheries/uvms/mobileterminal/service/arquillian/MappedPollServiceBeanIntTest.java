package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.bean.MessageProducerBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MappedPollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by thofan on 2017-05-04.
 *
 * TODO Since presence of REQUIRES_NEW are very limited in these classes, we don't test them right now, We will revisit
 */

@RunWith(Arquillian.class)
public class MappedPollServiceBeanIntTest extends TransactionalTests {

    @EJB
    private MappedPollService mappedPollService;

    @EJB
    private TerminalDao terminalDaoBean;

    @EJB
    private MobileTerminalPluginDao testDaoBean;

    @Test
    @OperateOnDeployment("normal")
    public void createPoll() throws MobileTerminalServiceException {

        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        PollRequestType pollRequestType = helper_createPollRequestType(PollType.MANUAL_POLL);

        // create a poll
        CreatePollResultDto createPollResultDto = mappedPollService.createPoll(pollRequestType, "TEST");
        em.flush();
        List<String> sendPolls = createPollResultDto.getSentPolls();
        String pollGuid = sendPolls.get(0);

        Assert.assertNotNull(pollGuid);
    }

    private PollRequestType helper_createPollRequestType(PollType pollType) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
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

        MobileTerminalPlugin mtp;
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
        //Channel channel = helper_createChannel(mt);
        channel.setArchived(false);
        channel.setGuid(UUID.randomUUID().toString());
        channel.setMobileTerminal(mt);
        channel.getHistories();
        channel.getCurrentHistory();

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
