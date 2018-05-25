package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.message.producer.bean.MessageProducerBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.service.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.service.exception.MobileTerminalServiceException;
import eu.europa.fisheries.uvms.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class PollServiceBeanIntTest extends TransactionalTests {



    @EJB
    private PollServiceBean pollService;

    @EJB
    private TestPollHelper testPollHelper;

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createPoll() throws MobileTerminalServiceException, ConfigDaoException, TerminalDaoException {
        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");
        PollRequestType pollRequestType = testPollHelper.createPollRequestType();
        CreatePollResultDto createPollResultDto = pollService.createPoll(pollRequestType, "TEST");
        assertNotNull(createPollResultDto);
    }

    @Test
    public void createPollWithBrokenJMS_WillFail() throws ConfigDaoException, TerminalDaoException, MobileTerminalServiceException {

        thrown.expect(MobileTerminalServiceException.class);
        // thrown.expectMessage("MESSAGE_PRODUCER_METHODS_FAIL == true");

        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "true");
        PollRequestType pollRequestType = testPollHelper.createPollRequestType();
        pollService.createPoll(pollRequestType, "TEST");
    }

    @Test
    public void getRunningProgramPolls() throws PollDaoException, MobileTerminalServiceException, ConfigDaoException, TerminalDaoException {
        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        List<PollResponseType> runningProgramPolls = pollService.getRunningProgramPolls();
        assertNotNull(runningProgramPolls);
        assertEquals(1, runningProgramPolls.size());
    }

    @Test
    public void startProgramPoll() throws ConfigDaoException, TerminalDaoException, PollDaoException, MobileTerminalServiceException {

        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        PollResponseType responseType = pollService.startProgramPoll(pollProgram.getGuid(), pollProgram.getUpdatedBy());
        List<PollAttribute> attributes = responseType.getAttributes();

        boolean ok = false;
        for(PollAttribute attribute : attributes) {
            if(attribute.getKey().toString().equalsIgnoreCase(PollAttributeType.PROGRAM_RUNNING.toString())) {
                assertEquals(attribute.getValue(), "TRUE");
                ok = true;
            }
        }
        assertTrue(ok);
        assertNotNull(responseType);
    }

    @Test
    public void stopProgramPoll() throws ConfigDaoException, TerminalDaoException, PollDaoException, MobileTerminalServiceException {

        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        PollResponseType responseType = pollService.stopProgramPoll(pollProgram.getGuid(), pollProgram.getUpdatedBy());
        List<PollAttribute> attributes = responseType.getAttributes();

        boolean ok = false;
        for(PollAttribute attribute : attributes) {
            if(attribute.getKey().toString().equalsIgnoreCase(PollAttributeType.PROGRAM_RUNNING.toString())) {
                assertEquals(attribute.getValue(), "FALSE");
                ok = true;
            }
        }
        assertTrue(ok);
        assertNotNull(responseType);
    }

    @Test
    public void inactivateProgramPoll() throws PollDaoException, ConfigDaoException, TerminalDaoException, MobileTerminalServiceException {
        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        PollResponseType responseType = pollService.inactivateProgramPoll(pollProgram.getGuid(), pollProgram.getUpdatedBy());
        List<PollAttribute> attributes = responseType.getAttributes();

        boolean ok = false;
        for(PollAttribute attribute : attributes) {
            if(attribute.getKey().toString().equalsIgnoreCase(PollAttributeType.PROGRAM_RUNNING.toString())) {
                assertEquals(attribute.getValue(), "FALSE");
                ok = true;
            }
        }
        assertTrue(ok);
        assertNotNull(responseType);
    }

    @Test
    public void getPollProgramRunningAndStarted() throws MobileTerminalException {

        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");

        Date startDate = testPollHelper.getStartDate();
        Date latestRun = testPollHelper.getLatestRunDate();
        Date stopDate = testPollHelper.getStopDate();

        String mobileTerminalSerialNumber = testPollHelper.createSerialNumber();
        PollProgram pollProgram = testPollHelper.createPollProgramHelper(mobileTerminalSerialNumber, startDate, stopDate, latestRun);

        pollProgramDao.createPollProgram(pollProgram);

        List<PollResponseType> responseTypeList = pollService.timer();

        assertNotNull(responseTypeList);
        assertEquals(1, responseTypeList.size());

        List<PollAttribute> attributes = responseTypeList.get(0).getAttributes();
        assertNotNull(attributes);

        boolean ok = false;
        for(PollAttribute attribute : attributes) {
            if(attribute.getKey().toString().equalsIgnoreCase(PollAttributeType.PROGRAM_RUNNING.toString())) {
                assertEquals(attribute.getValue(), "TRUE");
                ok = true;
            }
        }
        assertTrue(ok);
    }


}
