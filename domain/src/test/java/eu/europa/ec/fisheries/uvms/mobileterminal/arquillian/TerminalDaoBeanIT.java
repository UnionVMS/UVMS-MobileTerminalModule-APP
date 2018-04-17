package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by thofan on 2017-04-28.
 */

@RunWith(Arquillian.class)
public class TerminalDaoBeanIT extends TransactionalTests {

    private static final Logger LOG = LoggerFactory.getLogger(TerminalDaoBeanIT.class);

    private Random rnd = new Random();

    @EJB
    private TerminalDao terminalDaoBean;

    @EJB
    private MobileTerminalPluginDao testDaoBean;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() throws TerminalDaoException {

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();

        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
        boolean ok = ((fetchedBySerialNo != null) &&
                (fetchedBySerialNo.getSerialNo() != null) &&
                (fetchedBySerialNo.getSerialNo().equals(serialNo)));
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFailWithUpdateUserConstraintViolation() throws TerminalDaoException {

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage("[ Error when creating. ]");

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        char[] chars = new char[61];
        Arrays.fill(chars, 'x');
        mobileTerminal.setUpdatedBy(new String(chars));

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo() throws TerminalDaoException {

        // this is the same as create since they both use getMobileTerminalBySerialNo to verify functionality
        createMobileTerminal();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo_NON_EXISTING_SERIAL_NO() throws NoEntityFoundException {

        thrown.expect(NoEntityFoundException.class);
        thrown.expectMessage("No entity found with serial no does_not_exist");
        terminalDaoBean.getMobileTerminalBySerialNo("does_not_exist");
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_VERIFY_THAT_SETGUID_DOES_NOT_WORK_AT_CREATE() throws TerminalDaoException {

        String uuid = UUID.randomUUID().toString();
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        mobileTerminal.setGuid(uuid);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
        boolean ok = ((fetchedBySerialNo != null) &&
                (fetchedBySerialNo.getGuid() != null) &&
                (!fetchedBySerialNo.getGuid().equals(uuid)));
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid() throws TerminalDaoException {

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
        String fetchedGUID = fetchedBySerialNo.getGuid();
        assertNotNull(fetchedGUID);
        MobileTerminal fetchedByGUID = terminalDaoBean.getMobileTerminalByGuid(fetchedGUID);
// @formatter:off
        boolean ok = (
            (fetchedBySerialNo.getSerialNo() != null) &&
            (fetchedBySerialNo.getGuid() != null) &&
            (fetchedBySerialNo.getSerialNo().equals(serialNo)) &&
            (fetchedByGUID != null)) &&
            (fetchedByGUID.getGuid() != null) &&
            (fetchedByGUID.getGuid().equals(fetchedBySerialNo.getGuid())
        );
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid_NON_EXISTING_GUID() throws TerminalDaoException {

        thrown.expect(NoEntityFoundException.class);

        String aNonExistingGuid = UUID.randomUUID().toString();
        terminalDaoBean.getMobileTerminalByGuid(aNonExistingGuid);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery() throws TerminalDaoException {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        String updateUser = UUID.randomUUID().toString();
        mobileTerminal.setUpdatedBy(updateUser);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();

        String sql = "SELECT m FROM MobileTerminal m WHERE m.updateuser = '" + updateUser + "'";

        List<MobileTerminal> mobileTerminals = terminalDaoBean.getMobileTerminalsByQuery(sql);
// @formatter:off
        boolean ok = (
                (mobileTerminals != null) &&
                (mobileTerminals.size() > 0));
// @formatter:on
        boolean found = false;
        if (ok) {
            for (MobileTerminal mt : mobileTerminals) {
                String wrkUpdateUser = mt.getUpdatedBy();
                if (wrkUpdateUser.equals(updateUser)) {
                    found = true;
                    break;
                }
            }
        }
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery_ShouldFailWithInvalidSqlQuery() throws TerminalDaoException {
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();

        String sql = "SELECT m FROM MobileTerminal m WHERE m.updateuser = 'test'"; // lower cases

        List<MobileTerminal> mobileTerminals = terminalDaoBean.getMobileTerminalsByQuery(sql);
// @formatter:off
        boolean nullOrEmpty = (
                (mobileTerminals == null) ||
                (mobileTerminals.size() <= 0));
// @formatter:on

        assertTrue(nullOrEmpty);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() throws TerminalDaoException {

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();

        mobileTerminal.setGuid("UPDATED");
        terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();

        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
        boolean ok = ((fetchedBySerialNo != null) &&
                (fetchedBySerialNo.getGuid() != null) &&
                (fetchedBySerialNo.getGuid().equals("UPDATED")));
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFailWithGuidConstraintViolation() throws TerminalDaoException {

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage("[ Error when updating. ]");

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        terminalDaoBean.createMobileTerminal(mobileTerminal);
        em.flush();
        MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);

        String uuid = UUID.randomUUID().toString() + "length-violation";
        fetchedBySerialNo.setGuid(uuid);

        terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFailWithNoPersistedEntity() throws TerminalDaoException {

        thrown.expect(TerminalDaoException.class);
        thrown.expectMessage(" [ There is no such persisted entity to update ] ");

        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        terminalDaoBean.updateMobileTerminal(mobileTerminal);
        em.flush();
    }

    private MobileTerminal createMobileTerminalHelper(String serialNo) {

        MobileTerminal mt = new MobileTerminal();
        MobileTerminalPlugin mtp;
        List<MobileTerminalPlugin> plugs;

        try {
            plugs = testDaoBean.getPluginList();
            mtp = plugs.get(0);
            mt.setSerialNo(serialNo);
            mt.setUpdateTime(new Date());
            mt.setUpdatedBy("TEST");
            mt.setSource(MobileTerminalSourceEnum.INTERNAL);
            mt.setPlugin(mtp);
            mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
            mt.setArchived(false);
            mt.setInactivated(false);
        } catch (ConfigDaoException e) {
            LOG.error("There was an error while retrieving pluginList: {}", e.getMessage());
        }
        return mt;
    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
