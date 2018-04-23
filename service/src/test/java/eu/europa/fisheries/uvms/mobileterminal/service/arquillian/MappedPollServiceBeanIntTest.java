package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.bean.MessageProducerBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MappedPollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import eu.europa.fisheries.uvms.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by thofan on 2017-05-04.
 *
 * TODO Since presence of REQUIRES_NEW are very limited in these classes, we don't test them right now, We will revisit
 */

@RunWith(Arquillian.class)
public class MappedPollServiceBeanIntTest extends TransactionalTests {

    @EJB
    private MappedPollService mappedPollService;

    @EJB
    private PollProgramDao pollProgramDao;

    @EJB
    private TestPollHelper testPollHelper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Calendar cal = Calendar.getInstance();

    @Test
    @OperateOnDeployment("normal")
    public void createPoll() throws MobileTerminalServiceException, ConfigDaoException, TerminalDaoException {

        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        PollRequestType pollRequestType = helper_createPollRequestType(PollType.MANUAL_POLL);

        // create a poll
        CreatePollResultDto createPollResultDto = mappedPollService.createPoll(pollRequestType, "TEST");
        em.flush();
        List<String> sendPolls = createPollResultDto.getSentPolls();
        String pollGuid = sendPolls.get(0);

        assertNotNull(pollGuid);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getRunningProgramPolls() {
        // This is already tested in PollProgramDaoBeanIT class.
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll() throws MobileTerminalServiceException, PollDaoException, ConfigDaoException, TerminalDaoException {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        PollProgram pollProgram = createPollProgramHelper(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        String guid = pollProgram.getGuid();

        PollDto startedProgramPoll = mappedPollService.startProgramPoll(guid, username);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValue();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll() throws MobileTerminalServiceException, PollDaoException, ConfigDaoException, TerminalDaoException {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        PollProgram pollProgram = createPollProgramHelper(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        String guid = pollProgram.getGuid();

        PollDto startedProgramPoll = mappedPollService.startProgramPoll(guid, username);
        assertNotNull(startedProgramPoll);

        PollDto stoppedProgramPoll = mappedPollService.stopProgramPoll(guid, username);
        assertNotNull(stoppedProgramPoll);
        List<PollValue> values = stoppedProgramPoll.getValue();
        boolean found = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "false");

        assertTrue(found);
    }

    @Test
    @OperateOnDeployment("normal")
    public void inactivateProgramPoll() throws MobileTerminalServiceException, PollDaoException, ConfigDaoException, TerminalDaoException {

        // we want to be able to tamper with the dates for proper test coverage
        Date startDate = getStartDate();
        Date latestRun = getLatestRunDate();
        Date stopDate = getStopDate();

        String username = "TEST";

        String connectId = UUID.randomUUID().toString();
        PollProgram pollProgram = createPollProgramHelper(connectId, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);
        String guid = pollProgram.getGuid();

        PollDto startedProgramPoll = mappedPollService.startProgramPoll(guid, username);
        assertNotNull(startedProgramPoll);

        List<PollValue> values = startedProgramPoll.getValue();
        boolean isRunning = validatePollKeyValue(values, PollKey.PROGRAM_RUNNING, "true");
        assertTrue(isRunning);

        PollDto inactivatedProgramPoll = mappedPollService.inactivateProgramPoll(guid, username);
        assertNotNull(inactivatedProgramPoll);

        List<PollValue> values1 = inactivatedProgramPoll.getValue();
        boolean isStopped = validatePollKeyValue(values1, PollKey.PROGRAM_RUNNING, "false");
        assertTrue(isStopped);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollBySearchQuery()  {

       // TODO: Find how to pass right PollListQuery object to service bean
    }

    @Test
    @OperateOnDeployment("normal")
    public void getPollableChannels()  {

        // TODO: Find how to pass right PollListQuery object to service bean
    }

    @Test
    @OperateOnDeployment("normal")
    public void startProgramPoll_ShouldFailWithNullAsPollId() throws MobileTerminalServiceException {

        thrown.expect(MobileTerminalServiceException.class);
        // thrown.expectMessage("No poll id given");

        mappedPollService.startProgramPoll(null, "TEST");
    }

    @Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll_ShouldFailWithNullAsPollId() throws MobileTerminalServiceException {

        thrown.expect(MobileTerminalServiceException.class);
        // thrown.expectMessage("No poll id given");

        mappedPollService.stopProgramPoll(null, "TEST");
    }

    private PollProgram createPollProgramHelper(String connectId, Date startDate, Date stopDate, Date latestRun) throws ConfigDaoException, TerminalDaoException {

        PollProgram pp = new PollProgram();
        // create a valid mobileTerminal
        MobileTerminal mobileTerminal = testPollHelper.createMobileTerminal(connectId);

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

    private PollRequestType helper_createPollRequestType(PollType pollType) throws ConfigDaoException, TerminalDaoException {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        String startDate = format.format(cal.getTime());
        cal.set(Calendar.YEAR, 2020);
        String endDate = format.format(cal.getTime());

        PollRequestType prt = new PollRequestType();
        prt.setComment("aComment" + UUID.randomUUID().toString());
        prt.setUserName("TEST");
        prt.setPollType(pollType);
        PollMobileTerminal pollMobileTerminal = helper_createPollMobileTerminal();
        prt.getMobileTerminals().add(pollMobileTerminal);

        PollAttribute psStart = new PollAttribute();
        PollAttribute psEnd = new PollAttribute();
        PollAttribute psFreq = new PollAttribute();

        psStart.setKey(PollAttributeType.START_DATE);
        psStart.setValue(startDate);
        prt.getAttributes().add(psStart);

        psEnd.setKey(PollAttributeType.END_DATE);
        psEnd.setValue(endDate);
        prt.getAttributes().add(psEnd);

        psFreq.setKey(PollAttributeType.FREQUENCY);
        psFreq.setValue("300000");
        prt.getAttributes().add(psFreq);

        return prt;
    }

    private PollMobileTerminal helper_createPollMobileTerminal() throws ConfigDaoException, TerminalDaoException {

        String connectId = UUID.randomUUID().toString();

        MobileTerminal mobileTerminal = testPollHelper.createMobileTerminal(connectId);
        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setConnectId(connectId);
        pmt.setMobileTerminalId(mobileTerminal.getGuid());

        Set<Channel> channels = mobileTerminal.getChannels();
        Channel channel = channels.iterator().next();
        String channelId = channel.getGuid();
        pmt.setComChannelId(channelId);
        return pmt;
    }

    private boolean validatePollKeyValue(List<PollValue> values, PollKey key, String value) {
        for(PollValue v : values) {
            PollKey pollKey = v.getKey();
            if(pollKey.equals(key) && v.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private Date getStartDate() {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startYear = 1999;
        cal.set(Calendar.YEAR, startYear);
        return cal.getTime();
    }

    private Date getLatestRunDate() {
        cal.set(Calendar.DAY_OF_MONTH, 20);
        int latestRunYear = 2017;
        cal.set(Calendar.YEAR, latestRunYear);
        return cal.getTime();
    }

    private Date getStopDate() {
        cal.set(Calendar.DAY_OF_MONTH, 28);
        cal.set(Calendar.YEAR, 2019);
        return cal.getTime();
    }
}
