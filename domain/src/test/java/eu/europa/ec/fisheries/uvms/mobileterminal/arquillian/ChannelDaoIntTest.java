package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.ChannelDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;

/**
 * Created by roblar on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class ChannelDaoIntTest extends TransactionalTests {

    @Inject
    ChannelDao channelDao;

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch() {

        //Given - need a string list of id's.
        final String id1 = "test_id1";
        final String id2 = "test_id2";

        final List<String> idList = Arrays.asList(id1, id2);

        //When
        final List<Channel> channels = channelDao.getPollableListSearch(idList);

        //Then
        assertNotNull(channels);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollableListSearch_emptyList() {

        //Given - empty id list
        final List<String> emptyList = new ArrayList<>();

        //When
        final List<Channel> channels = channelDao.getPollableListSearch(emptyList);

        //Then
        assertThat(channels.size(), is(0));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetActiveDNID() {

        //Given
        final String pluginName = "test_getActiveDNID";

        //When
        final List<String> dnidList = channelDao.getActiveDNID(pluginName);

        //Then
        assertNotNull(dnidList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetActiveDNID_emptyList() {

        //Given
        final String pluginName = null;

        //When
        final List<String> dnidList = channelDao.getActiveDNID(pluginName);

        //Then
        assertThat(dnidList.size(), is(0));
    }
}
