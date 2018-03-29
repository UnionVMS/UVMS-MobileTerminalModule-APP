package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

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
    private MobileTerminalPluginDao mobileTerminalPluginDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginList() throws ConfigDaoException {

        List<MobileTerminalPlugin> mobileTerminalPluginList = mobileTerminalPluginDao.getPluginList();

        assertNotNull(mobileTerminalPluginList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin() throws TerminalDaoException {

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

        // When
        MobileTerminalPlugin mobileTerminalPluginAfterCreation = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        MobileTerminalPlugin mobileTerminalPluginReadFromDatabase = mobileTerminalPluginDao.getPluginByServiceName(mobileTerminalPlugin.getPluginServiceName());

        // Then
        assertNotNull(mobileTerminalPluginAfterCreation);
        assertNotNull(mobileTerminalPluginReadFromDatabase);
        assertSame(mobileTerminalPlugin, mobileTerminalPluginAfterCreation);
        assertEquals(mobileTerminalPlugin.getPluginServiceName(), mobileTerminalPluginReadFromDatabase.getPluginServiceName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_persistNullEntityFailsWithTerminalDaoException() throws TerminalDaoException {

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage("create mobile terminal plugin");

        mobileTerminalPluginDao.createMobileTerminalPlugin(null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_nameConstraintViolation() throws TerminalDaoException {

        thrown.expect(ConstraintViolationException.class);

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] chars = new char[101];
        Arrays.fill(chars, 'x');
        mobileTerminalPlugin.setName(new String(chars)); // 101 chars

        // When
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();

        // Then Exception thrown
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_descriptionConstraintViolation() throws TerminalDaoException {

        thrown.expect(ConstraintViolationException.class);

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] chars = new char[81];
        Arrays.fill(chars, 'x');
        mobileTerminalPlugin.setDescription(new String(chars)); // 81 chars

        // When
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();

        // Then Exception thrown
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginByServiceName() throws TerminalDaoException {

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        mobileTerminalPlugin = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        // When
        MobileTerminalPlugin mobileTerminalPluginAfterGetter = mobileTerminalPluginDao.getPluginByServiceName(mobileTerminalPlugin.getPluginServiceName());

        // Then
        assertNotNull(mobileTerminalPluginAfterGetter);
        assertEquals("test_serviceName", mobileTerminalPluginAfterGetter.getPluginServiceName());
    }

    @Test(expected = NoEntityFoundException.class)
    @OperateOnDeployment("normal")
    public void testGetPluginByServiceName_wrongServiceNameThrowsNoEntityFoundException() throws TerminalDaoException {

        thrown.expect(NoEntityFoundException.class);
        thrown.expectMessage("No entities found when retrieving mobile terminal plugin by service name");

        mobileTerminalPluginDao.getPluginByServiceName("thisServiceNameDoesNotExist");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdatePlugin() throws TerminalDaoException {

        MobileTerminalPlugin created = createMobileTerminalPluginHelper();

        created = mobileTerminalPluginDao.createMobileTerminalPlugin(created);
        created.setPluginServiceName("change_name");

        MobileTerminalPlugin updated = mobileTerminalPluginDao.updateMobileTerminalPlugin(created);

        assertNotNull(created);
        assertEquals(updated.getId(), created.getId());
        assertEquals(updated.getPluginServiceName(), created.getPluginServiceName());
        assertEquals("change_name", created.getPluginServiceName());
    }

    @Test(expected = TerminalDaoException.class)
    @OperateOnDeployment("normal")
    public void testUpdatePlugin_updateInsteadOfPersistFailsWithTerminalDaoException() throws TerminalDaoException {

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage(" [ There is no such MobileTerminalPlugin object to update ] ");

        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

        mobileTerminalPluginDao.updateMobileTerminalPlugin(mobileTerminalPlugin);
    }

    @Test(expected = TerminalDaoException.class)
    @OperateOnDeployment("normal")
    public void testUpdatePlugin_persistNullEntityFailsWithTerminalDaoException() throws TerminalDaoException {

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage(" [ There is no such MobileTerminalPlugin object to update ] ");

        mobileTerminalPluginDao.updateMobileTerminalPlugin(null);
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
