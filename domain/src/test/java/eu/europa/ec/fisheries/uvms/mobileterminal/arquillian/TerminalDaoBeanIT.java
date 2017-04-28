package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.TerminalDaoBean;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.validation.constraints.AssertTrue;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by thofan on 2017-04-28.
 */

@RunWith(Arquillian.class)
public class TerminalDaoBeanIT  extends TransactionalTests {


    final static Logger LOG = LoggerFactory.getLogger(TerminalDaoBeanIT.class);


    Random rnd = new Random();

    @EJB
    private TerminalDao terminalDaoBean;


    //@Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() {

        String uuid = UUID.randomUUID().toString();
        String serialNo = "SN" + rnd.nextLong();

        MobileTerminal mobileTerminal = createMobileTerminalHelper(serialNo, uuid);
        try {
            terminalDaoBean.createMobileTerminal(mobileTerminal);
            em.flush();
            MobileTerminal fetchedBySerialNo =  terminalDaoBean.getMobileTerminalBySerialNo(serialNo);
            MobileTerminal fetchedByUUID =  terminalDaoBean.getMobileTerminalByGuid(uuid);
            Assert.assertTrue(true);
        } catch (TerminalDaoException e) {
            Assert.fail();
           LOG.error(e.toString(),e);
        }

    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByGuid() {
        String uuid = "MOBILE_TERMINAL_GUID_16";

        try {
            MobileTerminal fetchedByUUID =  terminalDaoBean.getMobileTerminalByGuid(uuid);
            Assert.assertTrue(fetchedByUUID != null);
        } catch (TerminalDaoException e) {
            Assert.fail();
            LOG.error(e.toString(),e);
        }
    }

    //@Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalBySerialNo() {
        double longitude = 9.140626D;
        double latitude = 57.683805D;
    }


    //@Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalsByQuery() {
        double longitude = 9.140626D;
        double latitude = 57.683805D;
    }


    //@Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() {
        double longitude = 9.140626D;
        double latitude = 57.683805D;
    }



    private MobileTerminal createMobileTerminalHelper(String serialNo,String uuid){

        MobileTerminal mt = new MobileTerminal();

        mt.setGuid(uuid);
        mt.setSerialNo(serialNo);
        mt.setArchived(false);
        mt.setInactivated(false);
        mt.setSource(MobileTerminalSourceEnum.INTERNAL);
        mt.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mt.setUpdatedBy("TEST");
        mt.setUpdateTime(new Date());

        return mt;

    }

}
