package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;

/**
 * Created by thofan on 2017-04-28.
 */

@RunWith(Arquillian.class)
public class TerminalDaoBeanIT extends TransactionalTests {

    final static Logger LOG = LoggerFactory.getLogger(TerminalDaoBeanIT.class);
    Random rnd = new Random();

    @EJB
    private TerminalDao terminalDaoBean;

    @EJB
    MobileTerminalPluginDao testDaoBean;


    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() {

        final String serialNo = createSerialNumber();
        final MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        try {
            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();
            final MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
            final boolean ok = ((fetchedBySerialNo != null) &&
                    (fetchedBySerialNo.getSerialNo() != null) &&
                    (fetchedBySerialNo.getSerialNo().equals(serialNo)));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (final TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }

    }


    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_VERIFY_THAT_SETGUID_DOES_NOT_WORK_AT_CREATE() {

        final String uuid = UUID.randomUUID().toString();
        final String serialNo = createSerialNumber();
        final MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        mobileTerminal.setGuid(uuid);
        try {
            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();
            final MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
            final boolean ok = ((fetchedBySerialNo != null) &&
                    (fetchedBySerialNo.getGuid() != null) &&
                    (!fetchedBySerialNo.getGuid().equals(uuid)));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (final TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }
    }



    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid() {

        final String serialNo = createSerialNumber();
        final MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        try {
            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();
            final MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
            final String fetchedGUID = fetchedBySerialNo.getGuid();
            final MobileTerminal fetchedByGUID = terminalDaoBean.getMobileTerminalByGuid(fetchedGUID);
// @formatter:off
            final boolean ok = ((fetchedBySerialNo != null) &&
                    (fetchedBySerialNo.getSerialNo() != null) &&
                    (fetchedBySerialNo.getGuid() != null) &&
                    (fetchedBySerialNo.getSerialNo().equals(serialNo)) &&
                    (fetchedByGUID != null)) &&
                    (fetchedByGUID.getGuid() != null) &&
                    (fetchedByGUID.getGuid().equals(fetchedBySerialNo.getGuid()));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (final TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid_NON_EXISTING_GUID() {

        final String aNonExistingGuid = UUID.randomUUID().toString();
        final String serialNo = createSerialNumber();
        final MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        try {
            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();
            final MobileTerminal fetchedByGUID = terminalDaoBean.getMobileTerminalByGuid(aNonExistingGuid);
// @formatter:off
            final boolean ok = (
                    (fetchedByGUID == null)
            );
// @formatter:on
            Assert.fail();
        } catch (final NoEntityFoundException e) {
            Assert.assertTrue(true);
        } catch (final TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo() {

        // this is the same as create since they both use getMobileTerminalBySerialNo to verify functionallity

        final String serialNo = createSerialNumber();
        final MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        try {
            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();
            final MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
            final boolean ok = ((fetchedBySerialNo != null) &&
                    (fetchedBySerialNo.getSerialNo() != null) &&
                    (fetchedBySerialNo.getSerialNo().equals(serialNo)));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (final TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery() {
        final String serialNo = createSerialNumber();
        final MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);

        final String updateUser = UUID.randomUUID().toString();
        mobileTerminal.setUpdatedBy(updateUser);
        store(mobileTerminal);

        final String sql = "SELECT m FROM MobileTerminal m WHERE m.updateuser = '" + updateUser + "'";

        try {
            final List<MobileTerminal>
                    rs = terminalDaoBean.getMobileTerminalsByQuery(sql);
// @formatter:off
            boolean ok = (
                    (rs != null) &&
                            (rs.size() > 0));

            boolean found = false;
            if (ok) {
                for (final MobileTerminal mt : rs) {
                    final String wrkUpdateUser = mt.getUpdatedBy();
                    if (wrkUpdateUser.equals(updateUser)) {
                        found = true;
                        break;
                    }
                }
            }
            ok = ok && found;
// @formatter:on
            Assert.assertTrue(ok);
        } catch (final TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() {

        final String serialNo = createSerialNumber();
        final MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo);
        try {
            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();

            mobileTerminal.setGuid("UPDATED");
            terminalDaoBean.updateMobileTerminal(mobileTerminal);
            em.flush();

            final MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
            final boolean ok = ((fetchedBySerialNo != null) &&
                    (fetchedBySerialNo.getGuid() != null) &&
                    (fetchedBySerialNo.getGuid().equals("UPDATED")));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (final TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }

    }


    private MobileTerminal createMobileTerminalHelper(final String serialNo) {

        final MobileTerminal mt = new MobileTerminal();

        MobileTerminalPlugin mtp = null;

        List<MobileTerminalPlugin> plugs = null;
        try {
            plugs = testDaoBean.getPluginList();
        } catch (final ConfigDaoException e) {
            e.printStackTrace();
        }


        mtp = plugs.get(0);
        mt.setSerialNo(serialNo);
        mt.setUpdateTime(new Date());
        mt.setUpdatedBy("TEST");
        mt.setSource(MobileTerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setInactivated(false);

        return mt;

    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();

    }

    private void store(final MobileTerminal mobileTerminal) {
        em.persist(mobileTerminal);
        em.flush();
    }

}
