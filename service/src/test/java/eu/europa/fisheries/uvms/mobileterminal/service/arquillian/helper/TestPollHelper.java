package eu.europa.fisheries.uvms.mobileterminal.service.arquillian.helper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.poll.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.PluginMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.util.DateUtils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.*;

@Stateless
@LocalBean
public class TestPollHelper {

    private Calendar cal = Calendar.getInstance();
    private Random rnd = new Random();

    @EJB
    private TerminalDaoBean terminalDao;

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

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

    private PollMobileTerminal createPollMobileTerminal() throws ConfigDaoException, TerminalDaoException {

        String connectId = UUID.randomUUID().toString();

        MobileTerminal mobileTerminal = createMobileTerminal(connectId);
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setConnectId(connectId);
        pmt.setMobileTerminalId(mobileTerminal.getGuid());

        Iterator iterator = mobileTerminal.getChannels().iterator();
        Channel channel = (Channel) iterator.next();
        pmt.setComChannelId(channel.getGuid());
        return pmt;
    }

    public MobileTerminal createMobileTerminal(String connectId) throws TerminalDaoException, ConfigDaoException {

        String serialNo = UUID.randomUUID().toString();

        MobileTerminal mt = new MobileTerminal();
        MobileTerminalPlugin mtp;
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

    public MobileTerminalType createBasicMobileTerminal() throws TerminalDaoException {
        MobileTerminalType mobileTerminal = new MobileTerminalType();
        mobileTerminal.setSource(MobileTerminalSource.INTERNAL);
        mobileTerminal.setType("INMARSAT_C");
        List<MobileTerminalAttribute> attributes = mobileTerminal.getAttributes();
        addAttribute(attributes, MobileTerminalConstants.SERIAL_NUMBER, generateARandomStringWithMaxLength(10));
        addAttribute(attributes, "SATELLITE_NUMBER", "S" + generateARandomStringWithMaxLength(4));
        addAttribute(attributes, "ANTENNA", "A");
        addAttribute(attributes, "TRANSCEIVER_TYPE", "A");
        addAttribute(attributes, "SOFTWARE_VERSION", "A");

        List<ComChannelType> channels = mobileTerminal.getChannels();
        ComChannelType comChannelType = new ComChannelType();
        channels.add(comChannelType);
        comChannelType.setGuid(UUID.randomUUID().toString());
        comChannelType.setName("VMS");

        addChannelAttribute(comChannelType, "FREQUENCY_GRACE_PERIOD", "54000");
        addChannelAttribute(comChannelType, "MEMBER_NUMBER", "100");
        addChannelAttribute(comChannelType, "FREQUENCY_EXPECTED", "7200");
        addChannelAttribute(comChannelType, "FREQUENCY_IN_PORT", "10800");
        addChannelAttribute(comChannelType, "LES_DESCRIPTION", "Thrane&Thrane");
        addChannelAttribute(comChannelType, "DNID", "1" + generateARandomStringWithMaxLength(3));
        addChannelAttribute(comChannelType, "INSTALLED_BY", "Mike Great");

        addChannelCapability(comChannelType, "POLLABLE", true);
        addChannelCapability(comChannelType, "CONFIGURABLE", true);
        addChannelCapability(comChannelType, "DEFAULT_REPORTING", true);

        Plugin plugin = new Plugin();
        plugin.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        plugin.setLabelName("Thrane&Thrane");
        plugin.setSatelliteType("INMARSAT_C");
        plugin.setInactive(false);

        MobileTerminalPlugin mobileTerminalPlugin = PluginMapper.mapModelToEntity((createPluginService()));
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        mobileTerminal.setPlugin(plugin);

        return mobileTerminal;
    }

    private PluginService createPluginService() {
        PluginService pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("Thrane&Thrane");
        pluginService.setSatelliteType("INMARSAT_C");
        pluginService.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        return pluginService;
    }

    private String generateARandomStringWithMaxLength(int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int val = new Random().nextInt(10);
            builder.append(String.valueOf(val));
        }
        return builder.toString();
    }

    private void addChannelCapability(ComChannelType comChannelType, String type, boolean value) {
        ComChannelCapability channelCapability = new ComChannelCapability();

        channelCapability.setType(type);
        channelCapability.setValue(value);
        comChannelType.getCapabilities().add(channelCapability);
    }

    private void addChannelAttribute(ComChannelType comChannelType, String type, String value) {
        ComChannelAttribute channelAttribute = new ComChannelAttribute();
        channelAttribute.setType(type);
        channelAttribute.setValue(value);
        comChannelType.getAttributes().add(channelAttribute);
    }

    private void addAttribute(List<MobileTerminalAttribute> attributes, String type, String value) {
        MobileTerminalAttribute serialNumberMobileTerminalAttribute = new MobileTerminalAttribute();
        serialNumberMobileTerminalAttribute.setType(type);
        serialNumberMobileTerminalAttribute.setValue(value);
        attributes.add(serialNumberMobileTerminalAttribute);
    }

    public PollProgram createPollProgramHelper(String mobileTerminalSerialNo, Date startDate, Date stopDate, Date latestRun)
            throws ConfigDaoException, TerminalDaoException {

        PollProgram pp = new PollProgram();
        // create a valid mobileTerminal
        MobileTerminal mobileTerminal = createMobileTerminal(mobileTerminalSerialNo);

        PollBase pb = new PollBase();
        String channelGuid = UUID.randomUUID().toString();
        String terminalConnect = UUID.randomUUID().toString();
        pb.setChannelGuid(channelGuid);
        pb.setMobileTerminal(mobileTerminal);
        pb.setTerminalConnect(terminalConnect);
        pp.setFrequency(1);
        pp.setLatestRun(latestRun);
        pp.setPollBase(pb);
        pp.setPollState(PollStateEnum.STARTED);
        pp.setStartDate(startDate);
        pp.setStopDate(stopDate);
        pp.setUpdateTime(latestRun);
        pp.setUpdatedBy("TEST");

        return pp;
    }

    public Date getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startYear = 1999;
        cal.set(Calendar.YEAR, startYear);
        return cal.getTime();
    }

    public Date getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        int latestRunYear = 2017;
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.getTime();
    }

    public Date getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return cal.getTime();
    }

    public String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
