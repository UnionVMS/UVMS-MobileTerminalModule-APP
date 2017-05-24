package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.bean.MessageProducerBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import eu.europa.fisheries.uvms.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class PollServiceBeanIntTest extends TransactionalTests {

    @EJB
    private PollService pollService;

    @EJB
    private TestPollHelper testPollHelper;

    @Test
    public void createPoll() throws MobileTerminalServiceException, ConfigDaoException, TerminalDaoException {
        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");
        PollRequestType pollRequestType = testPollHelper.createPollRequestType();
        CreatePollResultDto createPollResultDto = pollService.createPoll(pollRequestType, "TEST");
        Assert.assertNotNull(createPollResultDto);
    }

    @Test
    public void createPollWithBrokenJMS() throws MobileTerminalServiceException, ConfigDaoException, TerminalDaoException {
        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "true");
        PollRequestType pollRequestType = testPollHelper.createPollRequestType();
        try {
            pollService.createPoll(pollRequestType, "TEST");
            Assert.fail();
        } catch (MobileTerminalServiceException ignore) {
        }
    }





}
