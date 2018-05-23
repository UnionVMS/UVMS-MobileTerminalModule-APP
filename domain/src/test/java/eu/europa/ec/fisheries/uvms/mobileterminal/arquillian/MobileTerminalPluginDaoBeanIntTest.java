package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by roblar on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class MobileTerminalPluginDaoBeanIntTest extends TransactionalTests {

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginListWithNewPlugin() throws ConfigDaoException, TerminalDaoException {

        List<MobileTerminalPlugin> mobileTerminalPluginListBefore = mobileTerminalPluginDao.getPluginList();
        assertNotNull(mobileTerminalPluginListBefore);
        
        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        mobileTerminalPlugin = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        // When
        List<MobileTerminalPlugin> mobileTerminalPluginList = mobileTerminalPluginDao.getPluginList();

        // Then
        assertNotNull(mobileTerminalPlugin.getId());
        assertNotNull(mobileTerminalPluginList);
        assertEquals(mobileTerminalPluginListBefore.size() + 1, mobileTerminalPluginList.size());
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
        char[] name = new char[41];
        Arrays.fill(name, 'x');
        mobileTerminalPlugin.setName(new String(name));

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
        char[] description = new char[81];
        Arrays.fill(description, 'x');
        mobileTerminalPlugin.setDescription(new String(description));

        // When
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();

        // Then Exception thrown
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_serviceNameConstraintViolation() throws TerminalDaoException {

        thrown.expect(ConstraintViolationException.class);

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] serviceName = new char[501];
        Arrays.fill(serviceName, 'x');
        mobileTerminalPlugin.setPluginServiceName(new String(serviceName));

        // When
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();

        // Then Exception thrown
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_satelliteTypeConstraintViolation() throws TerminalDaoException {

        thrown.expect(ConstraintViolationException.class);

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] satelliteType = new char[51];
        Arrays.fill(satelliteType, 'x');
        mobileTerminalPlugin.setPluginSatelliteType(new String(satelliteType));

        // When
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();

        // Then Exception thrown
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_updateUserConstraintViolation() throws TerminalDaoException {

        thrown.expect(ConstraintViolationException.class);

        // Given
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] updatedBy = new char[61];
        Arrays.fill(updatedBy, 'x');
        mobileTerminalPlugin.setUpdatedBy(new String(updatedBy));

        // When
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();

        // Then Exception thrown
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginByServiceName() throws TerminalDaoException {

        // Given
        final String serviceName = "test_serviceName";
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        mobileTerminalPlugin = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        // When
        MobileTerminalPlugin mobileTerminalPluginAfterGetter = mobileTerminalPluginDao.getPluginByServiceName(serviceName);

        // Then
        assertNotNull(mobileTerminalPlugin.getId());
        assertNotNull(mobileTerminalPluginAfterGetter);
        assertEquals(serviceName, mobileTerminalPluginAfterGetter.getPluginServiceName());
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

        // Given
        final String newServiceName = "change_me";
        MobileTerminalPlugin created = createMobileTerminalPluginHelper();
        created = mobileTerminalPluginDao.createMobileTerminalPlugin(created);

        // When
        created.setPluginServiceName(newServiceName);
        MobileTerminalPlugin updated = mobileTerminalPluginDao.updateMobileTerminalPlugin(created);

        // Then
        assertNotNull(created);
        assertEquals(updated.getId(), created.getId());
        assertEquals(updated.getPluginServiceName(), created.getPluginServiceName());
        assertEquals(newServiceName, created.getPluginServiceName());
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
        String testName = UUID.randomUUID().toString();

        mobileTerminalPlugin.setName(testName);
        mobileTerminalPlugin.setDescription("test_description");
        mobileTerminalPlugin.setPluginServiceName("test_serviceName");
        mobileTerminalPlugin.setPluginSatelliteType("test_satelliteType");
        mobileTerminalPlugin.setPluginInactive(false);
        mobileTerminalPlugin.setUpdateTime(new Date());
        mobileTerminalPlugin.setUpdatedBy("test_user");

        return mobileTerminalPlugin;
    }
}
