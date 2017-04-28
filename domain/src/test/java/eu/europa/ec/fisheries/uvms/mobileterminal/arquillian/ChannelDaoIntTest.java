package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.ChannelDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by roblar on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class ChannelDaoIntTest extends TransactionalTests {

    @Inject
    ChannelDao channelDao;

    @Test
    public void testGetPollableListSearch() {

        //Given - need a string list of id's.
        String id1 = "test_id1";
        String id2 = "test_id2";

        List<String> idList = Arrays.asList(id1, id2);

        //When
        //List<Channel> channels = channelDaoBean.getPollableListSearch(idList);
        List<Channel> channels = channelDao.getPollableListSearch(idList);


        //Then
        assertNotNull(channels);
    }

    @Test
    public void testGetActiveDNID() {

    }

    @Test
    public void getSQLActiveDNID() {

    }

}
