package eu.europa.fisheries.uvms.mobileterminal.service.arquillian.helper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.*;

@Stateless
@LocalBean
public class TestPollHelper {


    @EJB
    private TerminalDao terminalDao;

    @EJB
    private MobileTerminalPluginDao mobileTerminalPluginDao;


    public PollRequestType createPollRequestType() throws ConfigDaoException, TerminalDaoException {

        PollRequestType prt = new PollRequestType();
        prt.setComment("aComment" + UUID.randomUUID().toString());
        prt.setUserName("TEST");
        prt.setPollType(PollType.MANUAL_POLL);
        PollMobileTerminal pollMobileTerminal = createPollMobileTerminal();
        prt.getMobileTerminals().add(pollMobileTerminal);


        PollAttribute pollAttribute = new PollAttribute();
        pollAttribute.setKey(PollAttributeType.START_DATE);
        String startDate = DateUtils.getUTCNow().toString();
        pollAttribute.setValue(startDate);

        prt.getAttributes().add(pollAttribute);
        return prt;
    }

    public PollMobileTerminal createPollMobileTerminal() throws ConfigDaoException, TerminalDaoException {

        String connectId = UUID.randomUUID().toString();

        MobileTerminal mobileTerminal = createMobileTerminal(connectId);
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

    public MobileTerminal createMobileTerminal(String connectId) throws TerminalDaoException, ConfigDaoException {


        String serialNo = UUID.randomUUID().toString();
        MobileTerminal mt = new MobileTerminal();

        MobileTerminalPlugin mtp = null;
        List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();
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
        terminalDao.createMobileTerminal(mt);
        return mt;

    }


}
