package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchField;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by roblar on 2017-05-03.
 */
@RunWith(Arquillian.class)
public class PollDaoBeanIntTest extends TransactionalTests {

    @EJB
    PollDao pollDao;

    final static Logger LOG = LoggerFactory.getLogger(PollDaoBeanIntTest.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePoll() throws PollDaoException {

        Poll poll = createPollHelper();
        pollDao.createPoll(poll);
        String pollId = poll.getId().toString();
        em.persist(poll);
        em.flush();

        Poll pollReadFromDatabase = em.find(Poll.class, Long.valueOf(pollId));

        assertNotNull(pollReadFromDatabase);
        assertEquals(poll.getId(), pollReadFromDatabase.getId());
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPoll_willFailDueToNamedQueryHasNotBeenCorrectlyImplemented() throws PollDaoException {

        Poll poll = createPollHelper();
        pollDao.createPoll(poll);
        String pollId = poll.getId().toString();
        em.flush();

        Poll pollReadFromDatabase = pollDao.getPoll(pollId);
        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("No query defined for that name [Poll.findById]");
    }

    @Test(expected = PollDaoException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListByProgramPoll_willFailWithPollDaoException() throws PollDaoException {

        Integer pollProgramId = 1;

        pollDao.getPollListByProgramPoll(pollProgramId);

        thrown.expect(PollDaoException.class);
        thrown.expectMessage("Not yet implemented");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePollProgram() throws PollDaoException {

        String uuid = UUID.randomUUID().toString();

        PollProgram pollProgram = new PollProgram();
        pollProgram.setGuid(uuid);

        pollDao.createPollProgram(pollProgram);
        Long pollProgramId = pollProgram.getId();

        PollProgram pollProgramReadFromDatabase = em.find(PollProgram.class, pollProgramId);

        assertNotNull(pollProgramReadFromDatabase);
        assertEquals(pollProgramId, pollProgramReadFromDatabase.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    //ToDo: Need to understand SQL phrases from PollSearchMapper to test this one. Work in progress.
    public void testGetPollListSearchCount() throws PollDaoException {

        String baseSql = "SELECT COUNT (DISTINCT p) FROM Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE";
        String countSqlConnectId = "SELECT COUNT (DISTINCT p) FROM Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE tc.connectValue IN (:connectionValue) "; // Fails with "Invalid path: 'tc.connectValue'".
        String countSqlPollId = "SELECT COUNT (DISTINCT p) FROM Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE p.guid IN (:guid) "; // Works ok together with PollSearchField.POLL_ID.
        String countSqlPollType = "SELECT COUNT (DISTINCT p) FROM Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE p.pollType IN (:pollType) "; // Fails with javax.ejb.EJBTransactionRolledbackException: No enum constant eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum.testKeyValue1
        String countSqlTerminalType = "SELECT COUNT (DISTINCT p) FROM Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE mt.mobileTerminalType IN (:mobileTerminalType) "; // Fails with java.lang.IllegalArgumentException: No enum constant eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum.testKeyValue1
        String countSqlUser = "SELECT DISTINCT p FROM Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE pb.creator IN (:creator) "; //Fails with java.lang.IllegalArgumentException: Type specified for TypedQuery [java.lang.Long] is incompatible with query return type [class eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll]


        /***
         * Godkända PollTypeEnum-värden:
         * PROGRAM_POLL(1),
         * SAMPLING_POLL(2),
         * MANUAL_POLL(3),
         * CONFIGURATION_POLL(4);
         */

        /***
         * Godkända MobileTerminalTypeEnum-värden:
         * INMARSAT_C, IRIDIUM;
         */

        //String keyValue1 = "testKeyValue1";
        String pollTypeEnumValue1 = "PROGRAM_POLL";
        String mobileTerminalTypeEnumValue1 = "INMARSAT_C";

        //String keyValue2 = "testKeyValue2";
        String pollTypeEnumValue2 = "SAMPLING_POLL";
        String mobileTerminalTypeEnumValue2 = "IRIDIUM";

        //List<String> listOfKeyValues = Arrays.asList(pollTypeEnumValue1, pollTypeEnumValue2);
        List<String> listOfKeyValues = Arrays.asList(mobileTerminalTypeEnumValue1, mobileTerminalTypeEnumValue2);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        //pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        //pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);
        //pollSearchKeyValue1.setSearchField(PollSearchField.POLL_TYPE);
        //pollSearchKeyValue1.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue1.setSearchField(PollSearchField.USER);
        pollSearchKeyValue1.setValues(listOfKeyValues);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        //pollSearchKeyValue2.setSearchField(PollSearchField.POLL_ID);
        //pollSearchKeyValue2.setSearchField(PollSearchField.CONNECT_ID);
        //pollSearchKeyValue2.setSearchField(PollSearchField.POLL_TYPE);
        //pollSearchKeyValue2.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue2.setSearchField(PollSearchField.USER);
        pollSearchKeyValue2.setValues(listOfKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        //boolean isDynamic = false;
        boolean isDynamic = true;

        Long number = pollDao.getPollListSearchCount(countSqlUser, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_failOnSqlReplaceToken() {

        String sql = "SELECT COUNT (DISTINCT p) FROM Poll p ";
        List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();
        boolean isDynamic = true;
        Long number = pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue, isDynamic);

    }
    //sqlReplaceToken

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_noSqlPhraseCausesException() {

        String sql = "";
        List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();
        boolean isDynamic = true;
        Long number = pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue, isDynamic);

        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("unexpected end of subtree []");
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_malformedSqlPhraseCausesException() {

        String sql = "SELECT * FROM Poll p";
        List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();
        boolean isDynamic = true;
        Long number = pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue, isDynamic);

        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("unexpected token: * near line");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated() {

    }

    private Poll createPollHelper() {

        String uuid = UUID.randomUUID().toString();

        Poll poll = new Poll();
        poll.setGuid(uuid);
        poll.setUpdateTime(new Date());
        poll.setUpdatedBy("testUser");

        return poll;
    }
}
