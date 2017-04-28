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
import javax.validation.ConstraintViolationException;
import java.util.Calendar;
import java.util.List;

@RunWith(Arquillian.class)
public class DNIDListDaoBeanTestIntTest extends TransactionalTests {

    private final static String PLUGIN_NAME = "TEST_PLUGIN_NAME";
    private final static String DN_ID = "TEST_DN_ID";
    private final static String USERNAME = "TEST";

    @EJB
    private DNIDListDao dnidListDao;


    @Test
    @OperateOnDeployment("normal")
    public void testGetDNIDListEmptyList() throws ConfigDaoException {
        List<DNIDList> plugins = dnidListDao.getDNIDList(PLUGIN_NAME + Calendar.getInstance().getTimeInMillis());
        Assert.assertNotNull(plugins);
        Assert.assertTrue(plugins.size() == 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetDNIDList() throws ConfigDaoException {
        DNIDList dnidList = createDnidList();
        dnidListDao.create(dnidList);
        em.flush();

        List<DNIDList> plugins = dnidListDao.getDNIDList(PLUGIN_NAME);
        Assert.assertNotNull(plugins);
        Assert.assertTrue(plugins.size() > 0);
        Assert.assertTrue(plugins.size() < 2);

    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreate() throws ConfigDaoException {
        DNIDList dnidList = createDnidList();
        dnidListDao.create(dnidList);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateWithBadDNID() throws ConfigDaoException {
        DNIDList dnidList = createDnidList();
        char[] dnId = new char[101];
        dnidList.setDNID(new String(dnId));
        try {
            dnidListDao.create(dnidList);
            em.flush();
        } catch(ConstraintViolationException ignore) {}
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateWithBadPluginName() throws ConfigDaoException {
        DNIDList dnidList = createDnidList();
        char[] pluginName = new char[501];
        dnidList.setPluginName(new String(pluginName));
        try {
            dnidListDao.create(dnidList);
            em.flush();
        } catch(ConstraintViolationException ignore) {}
    }

    //TODO: Implement negative tests when there exists constraints to actually test

    @Test
    @OperateOnDeployment("normal")
    public void testRemoveByPluginName() throws ConfigDaoException {
        DNIDList dnidList = createDnidList();
        dnidListDao.create(dnidList);
        em.flush();

        dnidListDao.removeByPluginName(PLUGIN_NAME);
        List<DNIDList> plugins = dnidListDao.getDNIDList(PLUGIN_NAME);
        Assert.assertNotNull(plugins);
        Assert.assertTrue(plugins.size() == 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetAllDNIDList() throws ConfigDaoException {
        List<DNIDList> before = dnidListDao.getAllDNIDList();
        Assert.assertNotNull(before);

        DNIDList dnidList = createDnidList();
        dnidListDao.create(dnidList);
        em.flush();

        List<DNIDList> after = dnidListDao.getAllDNIDList();
        Assert.assertNotNull(after);

        Assert.assertTrue(before.size() == (after.size() - 1));

    }

    private DNIDList createDnidList() {
        DNIDList dnidList = new DNIDList();
        dnidList.setDNID(DN_ID);
        dnidList.setPluginName(PLUGIN_NAME);
        dnidList.setUpdateTime(Calendar.getInstance().getTime());
        dnidList.setUpdateUser(USERNAME);
        return dnidList;
    }

}
