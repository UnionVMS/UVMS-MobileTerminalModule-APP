package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.validation.constraints.AssertTrue;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

        String uuid = UUID.randomUUID().toString();
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo, uuid);
        try {
            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();
            MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
            MobileTerminal fetchedByUUID = terminalDaoBean.getMobileTerminalByGuid(uuid);
// @formatter:off
            boolean ok = ((fetchedBySerialNo != null) &&
                    (fetchedByUUID != null) &&
                    (fetchedBySerialNo.getSerialNo() != null) &&
                    (fetchedByUUID.getSerialNo() != null) &&
                    (fetchedBySerialNo.getSerialNo().equals(fetchedByUUID.getSerialNo())));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }

    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid() {

        String uuid = UUID.randomUUID().toString();
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo, uuid);
        store(mobileTerminal);
        try {
            MobileTerminal fetchedByUUID = terminalDaoBean.getMobileTerminalByGuid(uuid);
// @formatter:off
            boolean ok = (
                    (fetchedByUUID != null) &&
                    (fetchedByUUID.getGuid()) != null) &&
                    (uuid.equals(fetchedByUUID.getGuid()));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo() {
        String uuid = UUID.randomUUID().toString();
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo, uuid);
        store(mobileTerminal);
        try {
            MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
            boolean ok = (
                    (fetchedBySerialNo != null) &&
                    (fetchedBySerialNo.getSerialNo() != null) &&
                    (serialNo.equals(fetchedBySerialNo.getSerialNo())));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }
    }


    // TODO enhance the testresult
    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery() {
        String uuid = UUID.randomUUID().toString();
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo, uuid);
        store(mobileTerminal);

        String sql = "select mt from movement.mobileterminal mt ";

        try {
            List<MobileTerminal>
                    rs = terminalDaoBean.getMobileTerminalsByQuery(sql);
// @formatter:off
            boolean ok = (
                    (rs != null) &&
                    (rs.size() > 0));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() {

        String uuid = UUID.randomUUID().toString();
        String serialNo = createSerialNumber();
        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo, uuid);
        try {
            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();

            mobileTerminal.setGuid("UPDATED");
            terminalDaoBean.updateMobileTerminal(mobileTerminal);
            em.flush();

            MobileTerminal fetchedBySerialNo = terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
// @formatter:off
            boolean ok = ((fetchedBySerialNo != null) &&
                    (fetchedBySerialNo.getSerialNo() != null) &&
                    (fetchedBySerialNo.getSerialNo().equals("UPDATED")));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(), e);
        }

    }


    private MobileTerminal createMobileTerminalHelper(String serialNo, String uuid) {

        MobileTerminal mt = new MobileTerminal();

        MobileTerminalPlugin mtp = null;

        List<MobileTerminalPlugin> plugs = null;
        try {
             plugs = testDaoBean.getPluginList();
        } catch (ConfigDaoException e) {
            e.printStackTrace();
        }


        mtp = plugs.get(0);
        mt.setGuid(uuid);
        mt.setSerialNo(serialNo);
        mt.setUpdateTime(new Date());
        mt.setUpdatedBy("TEST");
        mt.setSource(MobileTerminalSourceEnum.INTERNAL);
        mt.setPlugin(mtp );
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setArchived(false);
        mt.setInactivated(false);

        return mt;

    }

    private String createSerialNumber(){
        return "SNU" + rnd.nextInt();

    }

    private void store(MobileTerminal mobileTerminal) {
        em.persist(mobileTerminal);
        em.flush();
    }

}
