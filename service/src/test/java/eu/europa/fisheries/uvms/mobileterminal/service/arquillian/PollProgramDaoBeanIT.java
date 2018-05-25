package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.poll.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.util.DateUtils;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.validation.ConstraintViolationException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by thofan on 2017-05-02.
 */

@RunWith(Arquillian.class)
public class PollProgramDaoBeanIT extends TransactionalTests {

    private Calendar cal = Calendar.getInstance();

    private int startYear = 1999;
    private int latestRunYear = 2017;

    private Random rnd = new Random();

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @EJB
    private TerminalDaoBean terminalDaoBean;

    @EJB
    private MobileTerminalPluginDaoBean testDaoBean;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram() throws PollDaoException {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        String guid = pollProgram.getGuid();
        PollProgram fetchedPollProgram = pollProgramDao.getPollProgramByGuid(guid);

//@formatter:off
        boolean ok = ((fetchedPollProgram != null) &&
                (fetchedPollProgram.getGuid() != null) &&
                (fetchedPollProgram.getGuid().equals(guid)));
//@formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram_updateUserConstraintViolation() throws PollDaoException {

        thrown.expect(ConstraintViolationException.class);
        thrown.expectMessage("Validation failed for classes [eu.europa.ec.fisheries.uvms.mobileterminal.service.entity" +
                ".poll.PollProgram] during persist time for groups [javax.validation.groups.Default, ]");

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        char[] updatedBy = new char[61];
        Arrays.fill(updatedBy, 'x');
        pollProgram.setUpdatedBy(new String(updatedBy));

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void createPollProgram_withNullWillFail() throws PollDaoException {
        thrown.expect(PollDaoException.class);
        thrown.expectMessage("[ create poll program ] attempt to create create event with null entity");

        pollProgramDao.createPollProgram(null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updatePollProgram() throws PollDaoException {

        // we want to be able to tamper with the dates for proper test  coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        // change the GUID
        String guid = UUID.randomUUID().toString();
        pollProgram.setGuid(guid);
        pollProgram.setUpdatedBy("update");
        // store
        pollProgramDao.updatePollProgram(pollProgram);
        em.flush();

        PollProgram fetchedPollProgram = pollProgramDao.getPollProgramByGuid(guid);

// @formatter:off
        boolean ok = ((fetchedPollProgram != null) &&
                (fetchedPollProgram.getGuid() != null) &&
                (fetchedPollProgram.getGuid().equals(guid)) &&
                (fetchedPollProgram.getUpdatedBy() != null) &&
                (fetchedPollProgram.getUpdatedBy().equals("update")));
// @formatter:on
        assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updatePollProgram_WithNonePersistedEntityWillFail() throws PollDaoException {

        thrown.expect(PollDaoException.class);
        thrown.expectMessage(" [ There is no such persisted entity to update ] ");

        // we want to be able to tamper with the dates for proper test  coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.updatePollProgram(pollProgram);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive() throws PollDaoException {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();
        List<PollProgram> pollsAlive = pollProgramDao.getProgramPollsAlive();
        boolean found = false;
        for (PollProgram pp : pollsAlive) {
            String tmpGuid = pp.getGuid();
            if (tmpGuid.equals(pollProgram.getGuid())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive_ShouldFailWithCurrentDateBiggerThenStopDate() throws PollDaoException {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();

        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, startYear - 1);
        Date stopDate = cal.getTime();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();
        List<PollProgram> pollsAlive = pollProgramDao.getProgramPollsAlive();
        boolean found = false;
        for (PollProgram pp : pollsAlive) {
            String tmpGuid = pp.getGuid();
            if (tmpGuid.equals(pollProgram.getGuid())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getProgramPollsAlive_ShouldFailWithPollStateArchived() throws PollDaoException {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        pollProgram.setPollState(PollStateEnum.ARCHIVED);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();
        List<PollProgram> pollsAlive = pollProgramDao.getProgramPollsAlive();
        boolean found = false;
        for (PollProgram pp : pollsAlive) {
            String tmpGuid = pp.getGuid();
            if (tmpGuid.equals(pollProgram.getGuid())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted() throws PollDaoException {

        Date now = DateUtils.getUTCNow();
        cal.setTime(now);

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        List<PollProgram> pollPrograms;

        pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();

        boolean found = false;
        for (PollProgram pp : pollPrograms) {
            String tmpGuid = pp.getGuid();
            if (tmpGuid.equals(pollProgram.getGuid())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        assertTrue(pollPrograms.size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenLatestRunBiggerThenNow() throws PollDaoException {

        Date now = DateUtils.getUTCNow();
        cal.setTime(now);

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();

        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear + 3);
        Date latestRun = cal.getTime();

        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        List<PollProgram> pollPrograms;

        pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();

        boolean found = false;
        for (PollProgram pp : pollPrograms) {
            String tmpGuid = pp.getGuid();
            if (tmpGuid.equals(pollProgram.getGuid())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
        assertFalse(pollPrograms.size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenStartDateBiggerThenNow() throws PollDaoException {

        Date now = DateUtils.getUTCNow();
        cal.setTime(now);

        // we want to be able to tamper with the dates for proper test coverage
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        Date startDate = cal.getTime();

        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        List<PollProgram> pollPrograms;

        pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();

        boolean found = false;
        for (PollProgram pp : pollPrograms) {
            String tmpGuid = pp.getGuid();
            if (tmpGuid.equals(pollProgram.getGuid())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
        assertFalse(pollPrograms.size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramRunningAndStarted_ShouldFailWhenPollStateIsNotStarted() throws PollDaoException {

        Date now = DateUtils.getUTCNow();
        cal.setTime(now);

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);
        pollProgram.setPollState(PollStateEnum.STOPPED);

        pollProgramDao.createPollProgram(pollProgram);
        em.flush();

        List<PollProgram> pollPrograms;

        pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();

        boolean found = false;
        for (PollProgram pp : pollPrograms) {
            String tmpGuid = pp.getGuid();
            if (tmpGuid.equals(pollProgram.getGuid())) {
                found = true;
                break;
            }
        }
        assertFalse(found);
        assertFalse(pollPrograms.size() > 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramByGuid() throws PollDaoException {
        // same as create since it uses the same methods to validate itself
        createPollProgram();
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollProgramByGuid_ShouldFailWithInvalidGuid() throws PollDaoException {

        thrown.expect(PollDaoException.class);
        thrown.expectMessage("No entity found getting by id");

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String mobileTerminalSerialNumber = createSerialNumber();
        PollProgram pollProgram = createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        String newGuid = UUID.randomUUID().toString();
        PollProgram fetchedPollProgram = pollProgramDao.getPollProgramByGuid(newGuid);

        assertNull(fetchedPollProgram);
    }

    private PollProgram createPollProgramHelper(String mobileTerminalSerialNo, Date startDate, Date stopDate, Date latestRun) {

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
        MobileTerminalPlugin mtp;
        List<MobileTerminalPlugin> plugs = null;

        try {
            plugs = testDaoBean.getPluginList();
        } catch (ConfigDaoException e) {
            fail(e.getMessage());
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
            fail(e.getMessage());
            return null;
        }
    }

    private Date getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, startYear);
        return cal.getTime();
    }

    private Date getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.getTime();
    }

    private Date getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return cal.getTime();
    }

    private String createSerialNumber() {
        return "SNU" + rnd.nextInt();
    }
}
