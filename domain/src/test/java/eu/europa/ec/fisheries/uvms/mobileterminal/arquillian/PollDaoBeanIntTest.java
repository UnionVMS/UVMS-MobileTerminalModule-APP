package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchField;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.poll.PollSearchMapper;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
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
    public void testGetPollListSearchCount_PollSearchField_POLL_ID() {

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.POLL_ID);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        boolean isDynamic = true;

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_SearchField_TERMINAL_TYPE() {

        /***
         * Ok MobileTerminalTypeEnum values:
         * INMARSAT_C, IRIDIUM;
         */

        String mobileTerminalTypeEnumInmarsatC = "INMARSAT_C";
        String mobileTerminalTypeEnumIridium = "IRIDIUM";

        List<String> listOfPollSearchKeyValues = Arrays.asList(mobileTerminalTypeEnumInmarsatC, mobileTerminalTypeEnumIridium);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        boolean isDynamic = true;

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_SearchField_POLL_TYPE() {

        /***
         * Ok PollTypeEnum values:
         * PROGRAM_POLL(1),
         * SAMPLING_POLL(2),
         * MANUAL_POLL(3),
         * CONFIGURATION_POLL(4);
         */
        String pollTypeEnumProgramPoll = "PROGRAM_POLL";
        String pollTypeEnumSamplingPoll = "SAMPLING_POLL";
        String pollTypeEnumManualPoll = "MANUAL_POLL";
        String pollTypeEnumConfigurationPoll = "CONFIGURATION_POLL";

        List<String> listOfPollSearchKeyValues = Arrays.asList(pollTypeEnumProgramPoll, pollTypeEnumSamplingPoll,
                pollTypeEnumManualPoll, pollTypeEnumConfigurationPoll);

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_TYPE);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.POLL_TYPE);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        boolean isDynamic = true;

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(number);
    }

    //@Test(expected = QuerySyntaxException.class)
    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_settingPollSearchField_CONNECT_ID_inPollSearchKeyValueWillBuildNoneWorkingSqlPhrase() {

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.CONNECT_ID);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        boolean isDynamic = true;

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

        //thrown.expect(QuerySyntaxException.class);
        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("Invalid path: 'tc.connectValue' [SELECT COUNT (DISTINCT p) FROM eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE tc.connectValue IN (:connectionValue) ]");
    }

    //@Test(expected = IllegalArgumentException.class)
    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_settingPollSearchField_USER_inPollSearchKeyValueWillCauseTypeMismatch() {

        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.USER);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.USER);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        boolean isDynamic = true;

        String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

        //thrown.expect(IllegalArgumentException.class);
        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("Type specified for TypedQuery [java.lang.Long] is incompatible with query return type [class eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll]");
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

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_settingPollSearchField_CONNECT_ID_inPollSearchKeyValueWillBuildNoneWorkingSqlPhrase() throws PollDaoException {

        boolean isDynamic = true;
        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.CONNECT_ID);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        Integer page = 1;
        Integer listSize = 2;

        String selectSearchSql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, selectSearchSql, listOfPollSearchKeyValue, isDynamic);

        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("Invalid path: 'tc.connectValue' [SELECT DISTINCT p FROM eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE tc.connectValue IN (:connectionValue)  AND tc.connectValue IN (:connectionValue) ]");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_POLL_ID() throws PollDaoException {

        boolean isDynamic = true;
        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.POLL_ID);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        Integer page = 1;
        Integer listSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_POLL_TYPE() throws PollDaoException {

        boolean isDynamic = true;
        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_TYPE);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.POLL_TYPE);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        Integer page = 1;
        Integer listSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_TERMINAL_TYPE() throws PollDaoException {

        boolean isDynamic = true;
        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.TERMINAL_TYPE);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.TERMINAL_TYPE);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        Integer page = 1;
        Integer listSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_USER() throws PollDaoException {

        boolean isDynamic = true;
        PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.USER);

        PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.USER);

        List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        Integer page = 1;
        Integer listSize = 2;

        String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(pollList);
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
