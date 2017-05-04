package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MappedPollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

/**
 * Created by thofan on 2017-05-04.
 */

@RunWith(Arquillian.class)
public class MappedPollServiceBeanIntTest extends TransactionalTests {


    @EJB
    MappedPollService mappedPollService;



    @Test
    @OperateOnDeployment("normal")
    public void createPoll() {

        PollRequestType pollRequestType = createPollRequestTypeHelper();

        try {
            CreatePollResultDto createPollResultDto = mappedPollService.createPoll(pollRequestType, "TEST");





        } catch (MobileTerminalServiceException e) {
            e.printStackTrace();
        }


    }

    /*

    //@Test
    @OperateOnDeployment("normal")
    public void getRunningProgramPolls() {}

    //@Test
    @OperateOnDeployment("normal")
    public void startProgramPoll()  {}

    //@Test
    @OperateOnDeployment("normal")
    public void stopProgramPoll()  {}

    //@Test
    @OperateOnDeployment("normal")
    public void inactivateProgramPoll() {}

    //@Test
    @OperateOnDeployment("normal")
    public void getPollBySearchQuery()  {}

    //@Test
    @OperateOnDeployment("normal")
    public void getPollableChannels()  {}



*/



    private PollRequestType createPollRequestTypeHelper() {

        PollRequestType prt = new PollRequestType();
        prt.setComment("a comment");
        prt.setUserName("a username");
        prt.setPollType(PollType.SAMPLING_POLL);
        return prt;
    }


}
