package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.DNIDListDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.DNIDList;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Calendar;
import java.util.List;

@RunWith(Arquillian.class)
public class DNIDListDaoBeanTestIntTest extends TransactionalTests {

    @EJB
    private DNIDListDao dnidListDao;


    @Test
    @OperateOnDeployment("normal")
    public void testCreate() throws ConfigDaoException {
        DNIDList dnidList = new DNIDList();
        dnidList.setDNID("testDNID");
        dnidList.setPluginName("TEST PLUGIN NAME");
        dnidList.setUpdateTime(Calendar.getInstance().getTime());
        dnidList.setUpdateUser("TEST");
        dnidListDao.create(dnidList);
        em.flush();

        List<DNIDList> plugins = dnidListDao.getDNIDList("TEST PLUGIN NAME");
        Assert.assertNotNull(plugins);
        Assert.assertTrue(plugins.size() > 0);
        Assert.assertTrue(plugins.size() < 2);

    }

}
