package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;

/**
 * Created by roblar on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class MobileTerminalPluginDaoBeanIntTest extends TransactionalTests {

    @EJB
    MobileTerminalPluginDao mobileTerminalPluginDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalPluginDaoBeanIntTest.class);

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginList() throws ConfigDaoException {

        final List<MobileTerminalPlugin> mobileTerminalPluginList = mobileTerminalPluginDao.getPluginList();

        assertNotNull(mobileTerminalPluginList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin() throws TerminalDaoException {

        try {
            // Given
            final MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

            // When
            final MobileTerminalPlugin mobileTerminalPluginAfterCreation = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
            final MobileTerminalPlugin mobileTerminalPluginReadFromDatabase = mobileTerminalPluginDao.getPluginByServiceName(mobileTerminalPlugin.getPluginServiceName());

            // Then
            assertNotNull(mobileTerminalPluginAfterCreation);
            assertNotNull(mobileTerminalPluginReadFromDatabase);
            assertThat(mobileTerminalPlugin, is(mobileTerminalPluginAfterCreation));
            assertThat(mobileTerminalPlugin.getPluginServiceName(), is(mobileTerminalPluginReadFromDatabase.getPluginServiceName()));

        } catch (final TerminalDaoException e) {
            LOG.error("Test testCreateMobileTerminalPlugin failed with exception {}", e);
        }
    }

    @Test(expected = TerminalDaoException.class)
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_persistNullEntityFailsWithTerminalDaoException() throws TerminalDaoException {

        final MobileTerminalPlugin failingMobileTerminalPlugin = null;

        mobileTerminalPluginDao.createMobileTerminalPlugin(failingMobileTerminalPlugin);

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage("create mobile terminal plugin");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginByServiceName() throws TerminalDaoException {

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        mobileTerminalPlugin = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        // When
        final MobileTerminalPlugin mobileTerminalPluginAfterGetter = mobileTerminalPluginDao.getPluginByServiceName(mobileTerminalPlugin.getPluginServiceName());

        // Then
        assertNotNull(mobileTerminalPluginAfterGetter);
        assertThat(mobileTerminalPluginAfterGetter.getPluginServiceName(), is("test_serviceName"));
    }

    @Test(expected = NoEntityFoundException.class)
    @OperateOnDeployment("normal")
    public void testGetPluginByServiceName_wrongServiceNameThrowsNoEntityFoundException() throws TerminalDaoException {

        final MobileTerminalPlugin mobileTerminalPluginAfterGetter = mobileTerminalPluginDao.getPluginByServiceName("thisServiceNameDoesNotExist");

        thrown.expect(NoEntityFoundException.class);
        thrown.expectMessage("No entities found when retrieving mobile terminal plugin by service name");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdatePlugin() throws TerminalDaoException {

        final MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

        final MobileTerminalPlugin mobileTerminalPluginAfterFirstUpdate = mobileTerminalPluginDao.updatePlugin(mobileTerminalPlugin);

        assertThat(mobileTerminalPlugin.getPluginServiceName(), is(mobileTerminalPluginAfterFirstUpdate.getPluginServiceName()));

        mobileTerminalPlugin.setPluginServiceName("change_name");

        final MobileTerminalPlugin mobileTerminalPluginAfterSecondUpdate = mobileTerminalPluginDao.updatePlugin(mobileTerminalPlugin);

        assertNotNull(mobileTerminalPluginAfterFirstUpdate);
        assertThat(mobileTerminalPluginAfterSecondUpdate.getPluginServiceName(), is(mobileTerminalPlugin.getPluginServiceName()));
        assertThat(mobileTerminalPlugin.getPluginServiceName(), is("change_name"));
    }

    @Test(expected = TerminalDaoException.class)
    @OperateOnDeployment("normal")
    public void testUpdatePlugin_persistNullEntityFailsWithTerminalDaoException() throws TerminalDaoException {

        final MobileTerminalPlugin mobileTerminalPlugin = null;

        final MobileTerminalPlugin mobileTerminalPluginAfterFirstUpdate = mobileTerminalPluginDao.updatePlugin(mobileTerminalPlugin);

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage("update mobile terminal plugin");
    }

    private MobileTerminalPlugin createMobileTerminalPluginHelper() {

        final MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();

        mobileTerminalPlugin.setName("test_name");
        mobileTerminalPlugin.setDescription("test_description");
        mobileTerminalPlugin.setPluginServiceName("test_serviceName");
        mobileTerminalPlugin.setPluginSatelliteType("test_satelliteType");
        mobileTerminalPlugin.setPluginInactive(false);
        mobileTerminalPlugin.setUpdateTime(new Date());
        mobileTerminalPlugin.setUpdatedBy("test_user");

        return mobileTerminalPlugin;
    }
}