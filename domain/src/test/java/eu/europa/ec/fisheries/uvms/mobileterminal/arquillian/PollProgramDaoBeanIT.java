package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.ejb.EJB;
import javax.persistence.Query;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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

/**
 * Created by thofan on 2017-05-02.
 */

@RunWith(Arquillian.class)
public class PollProgramDaoBeanIT extends TransactionalTests {

    Calendar cal = Calendar.getInstance();

    int startYear = 1999;
    int endYear = 2019;
    int latestRunYear = 2017;


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

        // we want to be able totamper with the dates for proper testcoverage
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, startYear);
        final Date startDate = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear);
        final Date latestRun = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, endYear);
        final Date stopDate = cal.getTime();


        final String mobileTerminalSerialNumber = createSerialNumber();
        final PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        try {
            pollProgramDao.createPollProgram(pollProgram);
            final String guid = pollProgram.getGuid();
            final PollProgram fetchedPollProgram = pollProgramDao.getPollProgramByGuid(guid);

//@formatter:off
            final boolean ok = ((fetchedPollProgram != null) &&
                    (fetchedPollProgram.getGuid() != null) &&
                    (fetchedPollProgram.getGuid().equals(guid)));
// @formatter:on
            Assert.assertTrue(ok);
        } catch (final PollDaoException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updatePollProgram() {

        // we want to be able to tamper with the dates for proper testcoverage
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, startYear);
        final Date startDate = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear);
        final Date latestRun = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, endYear);
        final Date stopDate = cal.getTime();

        final String mobileTerminalSerialNumber = createSerialNumber();
        final PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        try {
            pollProgramDao.createPollProgram(pollProgram);
            em.flush();

            // change the GUID
            final String guid = UUID.randomUUID().toString();
            pollProgram.setGuid(guid);
            pollProgram.setUpdatedBy("update");
            // store
            pollProgramDao.updatePollProgram(pollProgram);
            em.flush();

            final PollProgram fetchedPollProgram = pollProgramDao.getPollProgramByGuid(guid);

// @formatter:off
            final boolean ok = ((fetchedPollProgram != null) &&
                    (fetchedPollProgram.getGuid() != null) &&
                    (fetchedPollProgram.getGuid().equals(guid)) &&
                    (fetchedPollProgram.getUpdatedBy() != null) &&
                    (fetchedPollProgram.getUpdatedBy().equals("update")));
// @formatter:on
            Assert.assertTrue(ok);


        } catch (final PollDaoException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive() {

        // we want to be able to tamper with the dates for proper testcoverage
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, startYear);
        final Date startDate = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear);
        final Date latestRun = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, endYear);
        final Date stopDate = cal.getTime();

        final String mobileTerminalSerialNumber = createSerialNumber();
        final PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        try {
            pollProgramDao.createPollProgram(pollProgram);
            em.flush();
            final List<PollProgram> rs = pollProgramDao.getProgramPollsAlive();
            boolean found = false;
            for (final PollProgram pp : rs) {
                final String tmpGuid = pp.getGuid();
                if (tmpGuid.equals(pollProgram.getGuid())) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        } catch (final PollDaoException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted() {

        final Date now = DateUtils.getUTCNow();
        cal.setTime(now);

        // we want to be able to tamper with the dates for proper testcoverage
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, startYear);
        final Date startDate = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear);
        final Date latestRun = cal.getTime();


        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, endYear);
        final Date stopDate = cal.getTime();

        final String mobileTerminalSerialNumber = createSerialNumber();
        final PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        try {
            pollProgramDao.createPollProgram(pollProgram);
            em.flush();


            final boolean useInlineQuery = true;
            List<PollProgram> rs = null;
            if (useInlineQuery) {
                // this works  OBS nessesserary since date formatting is unclear
                final Query qry = em.createQuery("SELECT p FROM PollProgram p  WHERE p.startDate < :prm AND  p.pollState = eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum.STARTED");
                qry.setParameter("prm", now);
                rs = qry.getResultList();

            } else {
                // this is the correct retrieval BUT above is equivalent . . .
                rs = pollProgramDao.getPollProgramRunningAndStarted();
            }

            boolean found = false;
            for (final PollProgram pp : rs) {
                final String tmpGuid = pp.getGuid();
                if (tmpGuid.equals(pollProgram.getGuid())) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
            Assert.assertTrue(rs.size() > 0);
        } catch (final PollDaoException e) {
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


    private PollProgram createPollProgramHelper(final String mobileTerminalSerialNo, final Date startDate, final Date stopDate, final Date latestRun) {

        final PollProgram pp = new PollProgram();
        // create a valid mobileTerminal
        final MobileTerminal mobileTerminal = createMobileTerminalHelper(mobileTerminalSerialNo);

        final PollBase pb = new PollBase();
        final String channelGuid = UUID.randomUUID().toString();
        final String terminalConnect = UUID.randomUUID().toString();
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


        try {
            terminalDaoBean.createMobileTerminal(mt);
            return mt;
        } catch (final TerminalDaoException e) {
            e.printStackTrace();
            return null;
        }


    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();

    }


}
