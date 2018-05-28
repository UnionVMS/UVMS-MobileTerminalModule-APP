package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.ChannelDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.MobileTerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by roblar on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class ChannelDaoIntTest extends TransactionalTests {

    @Inject
    private ChannelDaoBean channelDao;

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @EJB
    private MobileTerminalDaoBean mobileTerminalDao;

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch() throws ConfigDaoException {

        //Given - need a string list of id's.
        String id1 = "test_id1";
        String id2 = "test_id2";
        List<String> idList = Arrays.asList(id1, id2);

        MobileTerminal mobileTerminal = createMobileTerminal(id1);
        MobileTerminal retrieved = mobileTerminalDao.createEntity(mobileTerminal);
        assertNotNull(retrieved);

        //When
        List<Channel> channels = channelDao.getPollableListSearch(idList);

        //Then
        assertNotNull(channels);
        assertEquals(1, channels.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch_emptyList() {

        //Given - empty id list
        List<String> emptyList = new ArrayList<>();

        //When
        List<Channel> channels = channelDao.getPollableListSearch(emptyList);

        //Then
        assertNotNull(channels);
        assertThat(channels.size(), is(0));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch_NULL() {

        //Given - null
        List<String> nullAsList = null;

        //When
        List<Channel> channels = channelDao.getPollableListSearch(nullAsList);

        //Then
        assertNotNull(channels);
        assertThat(channels.size(), is(0));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetActiveDNID() {

        //Given
        String pluginName = "test_getActiveDNID";

        //When
        List<String> dnidList = channelDao.getActiveDNID(pluginName);

        //Then
        assertNotNull(dnidList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetActiveDNID_emptyList() {

        //Given
        String pluginName = null;

        //When
        List<String> dnidList = channelDao.getActiveDNID(pluginName);

        //Then
        assertThat(dnidList.size(), is(0));
    }

    private MobileTerminal createMobileTerminal(String connectId) throws ConfigDaoException {

        String serialNo = UUID.randomUUID().toString();

        Channel channel = new Channel();
        channel.setArchived(false);
        channel.setGuid(serialNo);
        channel.getHistories();

        List<MobileTerminalPlugin> plugs = mobileTerminalPluginDao.getPluginList();
        MobileTerminalPlugin mtp = plugs.get(0);

        MobileTerminal mt = new MobileTerminal();
        mt.setSerialNo(serialNo);
        mt.setUpdateTime(new Date());
        mt.setUpdatedBy("TEST");
        mt.setSource(MobileTerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setInactivated(false);

        Set<MobileTerminalPluginCapability> capabilityList = new HashSet<>();
        MobileTerminalPluginCapability mtpc = new MobileTerminalPluginCapability();
        mtpc.setPlugin(mtp);
        mtpc.setName("test");
        mtpc.setValue("test");
        mtpc.setUpdatedBy("TEST_USER");
        mtpc.setUpdateTime(new Date());
        capabilityList.add(mtpc);

        mtp.getCapabilities().addAll(capabilityList);

        Set<MobileTerminalEvent> mobileTerminalEvents = new HashSet<>();
        MobileTerminalEvent mte = new MobileTerminalEvent();
        if(connectId != null && !connectId.trim().isEmpty())
            mte.setConnectId(connectId);
        mte.setActive(true);
        mte.setMobileTerminal(mt);

        String attributes = PollAttributeType.START_DATE.value() + "=" + DateUtils.getUTCNow().toString();
        attributes = attributes + ";";
        attributes = attributes + PollAttributeType.END_DATE.value() + "=" + DateUtils.getUTCNow().toString();
        mte.setAttributes(attributes);

        Channel pollChannel = new Channel();
        pollChannel.setArchived(false);
        pollChannel.setGuid(serialNo);
        pollChannel.setMobileTerminal(mt);
        pollChannel.getHistories();

        mte.setPollChannel(pollChannel);
        mobileTerminalEvents.add(mte);
        mt.getMobileTerminalEvents().addAll(mobileTerminalEvents);

        channel.setMobileTerminal(mt);

        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        mt.getChannels().addAll(channels);

        return mt;
    }
}
