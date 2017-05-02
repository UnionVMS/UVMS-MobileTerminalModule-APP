package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.DateUtils;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.*;

/**
 * Created by thofan on 2017-05-02.
 */

@RunWith(Arquillian.class)
public class PollProgramDaoBeanIT extends TransactionalTests {

    Random rnd = new Random();

    @EJB
    PollProgramDao pollProgramDao;


    @EJB
    private TerminalDao terminalDaoBean;

    @EJB
    MobileTerminalPluginDao testDaoBean;


    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram() {

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber);
        try {
            pollProgramDao.createPollProgram(pollProgram);
            String guid = pollProgram.getGuid();
            PollProgram fetchedPollProgram = pollProgramDao.getPollProgramByGuid(guid);

// @formatter:off
            boolean ok = ((fetchedPollProgram != null) &&
                    (fetchedPollProgram.getGuid() != null) &&
                    (fetchedPollProgram.getGuid().equals(guid)));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (PollDaoException e) {
            Assert.fail();
        }
    }

    //@Test
    @OperateOnDeployment("normal")
    public void updatePollProgram() {


    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive() {

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber);

        try {
            pollProgramDao.createPollProgram(pollProgram);
            em.flush();
            List<PollProgram> rs = pollProgramDao.getProgramPollsAlive();
            boolean found = false;
            for(PollProgram pp : rs){
                String tmpGuid = pp.getGuid();
                if(tmpGuid.equals(pollProgram.getGuid())){
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        } catch (PollDaoException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted() {

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber);

        try {
            pollProgramDao.createPollProgram(pollProgram);
            em.flush();


            List<PollProgram> rs = pollProgramDao.getPollProgramRunningAndStarted();
            boolean found = false;
            for(PollProgram pp : rs){
                String tmpGuid = pp.getGuid();
                if(tmpGuid.equals(pollProgram.getGuid())){
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        } catch (PollDaoException e) {
            e.printStackTrace();
            Assert.fail();
        }



    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramByGuid() {
        // same as create since it uses the same methods to validate itself
        createPollProgram();
    }


    private PollProgram createPollProgramHelper(String mobileTerminalSerialNo) {

        Date aDate = new Date();

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, 2001);
        Date startDate = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, 2050);
        Date latestRun = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2099);
        Date stopDate = cal.getTime();




        PollProgram pp = new PollProgram();
        // create a valid mobileTerminal
        MobileTerminal mobileTerminal = createMobileTerminalHelper(mobileTerminalSerialNo);

        PollBase pb = new PollBase();
        String channelGuid = UUID.randomUUID().toString();
        String terminalConnect = UUID.randomUUID().toString();
        pb.setChannelGuid(channelGuid);
        pb.setMobileTerminal(mobileTerminal);
        pb.setTerminalConnect(terminalConnect);
        pp.setFrequency(1);
        pp.setLatestRun(latestRun);
        pp.setPollBase(pb);
        pp.setPollState(PollStateEnum.STARTED);
        pp.setStartDate(startDate);
        pp.setStopDate(stopDate);
        pp.setUpdateTime(latestRun);
        pp.setUpdatedBy("TEST");

        return pp;
    }


    private MobileTerminal createMobileTerminalHelper(String serialNo) {

        MobileTerminal mt = new MobileTerminal();

        MobileTerminalPlugin mtp = null;

        List<MobileTerminalPlugin> plugs = null;
        try {
            plugs = testDaoBean.getPluginList();
        } catch (ConfigDaoException e) {
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


        try {
            terminalDaoBean.createMobileTerminal(mt);
            return mt;
        } catch (TerminalDaoException e) {
            e.printStackTrace();
            return null;
        }


    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();

    }


}
