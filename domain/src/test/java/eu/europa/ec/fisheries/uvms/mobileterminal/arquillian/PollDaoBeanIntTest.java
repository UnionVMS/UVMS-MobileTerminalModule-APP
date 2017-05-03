package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollDao;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

/**
 * Created by roblar on 2017-05-03.
 */
@RunWith(Arquillian.class)
public class PollDaoBeanIntTest extends TransactionalTests {

    @EJB
    PollDao pollDao;

    final static Logger LOG = LoggerFactory.getLogger(PollDaoBeanIntTest.class);

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll() {

    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPoll() {

    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListByProgramPoll() {

    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePollProgram() {

    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount() {

    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated() {

    }
}
