package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
    public void testGetPluginList() throws ConfigDaoException {

        List<MobileTerminalPlugin> mobileTerminalPluginList = mobileTerminalPluginDao.getPluginList();

        assertNotNull(mobileTerminalPluginList);

    }

    @Test
    public void testCreateMobileTerminalPlugin() throws TerminalDaoException {

        try {
            // Given
            MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

            // When
            MobileTerminalPlugin mobileTerminalPluginAfterCreation = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
            MobileTerminalPlugin mobileTerminalPluginReadFromDatabase = mobileTerminalPluginDao.getPluginByServiceName(mobileTerminalPlugin.getPluginServiceName());

            // Then
            assertNotNull(mobileTerminalPluginAfterCreation);
            assertNotNull(mobileTerminalPluginReadFromDatabase);
            assertThat(mobileTerminalPlugin, is(mobileTerminalPluginAfterCreation));
            assertThat(mobileTerminalPlugin.getPluginServiceName(), is(mobileTerminalPluginReadFromDatabase.getPluginServiceName()));

        } catch (TerminalDaoException e) {
            LOG.error("Test testCreateMobileTerminalPlugin failed with exception {}", e);
        }
    }

    @Test(expected = TerminalDaoException.class)
    public void testCreateMobileTerminalPlugin_persistEmptyEntityFailsWithTerminalDaoException() throws TerminalDaoException {

            MobileTerminalPlugin failingMobileTerminalPlugin = null;

            mobileTerminalPluginDao.createMobileTerminalPlugin(failingMobileTerminalPlugin);

            thrown.expect(TerminalDaoException.class);
            thrown.expectMessage("create mobile terminal plugin");
    }


    @Test
    public void testGetPluginByServiceName() throws TerminalDaoException {

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        mobileTerminalPlugin = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        // When
        MobileTerminalPlugin mobileTerminalPluginAfterGetter = mobileTerminalPluginDao.getPluginByServiceName(mobileTerminalPlugin.getPluginServiceName());

        // Then
        assertNotNull(mobileTerminalPluginAfterGetter);
        assertThat(mobileTerminalPluginAfterGetter.getPluginServiceName(), is("test_serviceName"));
    }

    @Test
    public void testUpdatePlugin() throws TerminalDaoException {

        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

        MobileTerminalPlugin mobileTerminalPluginAfterFirstUpdate = mobileTerminalPluginDao.updatePlugin(mobileTerminalPlugin);

        assertThat(mobileTerminalPlugin.getPluginServiceName(), is(mobileTerminalPluginAfterFirstUpdate.getPluginServiceName()));

        mobileTerminalPlugin.setPluginServiceName("change_name");

        MobileTerminalPlugin mobileTerminalPluginAfterSecondUpdate = mobileTerminalPluginDao.updatePlugin(mobileTerminalPlugin);

        assertNotNull(mobileTerminalPluginAfterFirstUpdate);
        assertThat(mobileTerminalPluginAfterSecondUpdate.getPluginServiceName(), is(mobileTerminalPlugin.getPluginServiceName()));
        assertThat(mobileTerminalPlugin.getPluginServiceName(), is("change_name"));
    }


    private MobileTerminalPlugin createMobileTerminalPluginHelper() {

        MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();

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