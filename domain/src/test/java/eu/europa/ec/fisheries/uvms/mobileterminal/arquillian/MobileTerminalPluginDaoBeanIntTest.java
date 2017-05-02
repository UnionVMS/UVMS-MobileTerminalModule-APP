package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by roblar on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class MobileTerminalPluginDaoBeanIntTest extends TransactionalTests {

    @Inject
    MobileTerminalPluginDao mobileTerminalPluginDao;

    final static Logger LOG = LoggerFactory.getLogger(MobileTerminalPluginDaoBeanIntTest.class);


    @Test
    public void testGetPluginList() throws ConfigDaoException {

        List<MobileTerminalPlugin> mobileTerminalPluginList = mobileTerminalPluginDao.getPluginList();

        assertNotNull(mobileTerminalPluginList);

    }

    @Test
    public void testCreateMobileTerminalPlugin() throws TerminalDaoException {

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

        // When
        MobileTerminalPlugin mobileTerminalPluginAfterCreation = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);


        // Then
        assertNotNull(mobileTerminalPluginAfterCreation);
        assertThat(mobileTerminalPlugin, is(mobileTerminalPluginAfterCreation));
    }

    @Test
    public void testGetPluginByServiceName() {

    }

    @Test
    public void testUpdatePlugin() {

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