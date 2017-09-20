package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchField;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.poll.PollSearchMapper;
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

        final Poll poll = createPollHelper();
        pollDao.createPoll(poll);
        final String pollId = poll.getId().toString();
        em.persist(poll);
        em.flush();

        final Poll pollReadFromDatabase = em.find(Poll.class, Long.valueOf(pollId));

        assertNotNull(pollReadFromDatabase);
        assertEquals(poll.getId(), pollReadFromDatabase.getId());
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPoll_willFailDueToNamedQueryHasNotBeenCorrectlyImplemented() throws PollDaoException {

        final Poll poll = createPollHelper();
        pollDao.createPoll(poll);
        final String pollId = poll.getId().toString();
        em.flush();

        final Poll pollReadFromDatabase = pollDao.getPoll(pollId);
        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("No query defined for that name [Poll.findById]");
    }

    @Test(expected = PollDaoException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListByProgramPoll_willFailWithPollDaoException() throws PollDaoException {

        final Integer pollProgramId = 1;

        pollDao.getPollListByProgramPoll(pollProgramId);

        thrown.expect(PollDaoException.class);
        thrown.expectMessage("Not yet implemented");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePollProgram() throws PollDaoException {

        final String uuid = UUID.randomUUID().toString();

        final PollProgram pollProgram = new PollProgram();
        pollProgram.setGuid(uuid);

        pollDao.createPollProgram(pollProgram);
        final Long pollProgramId = pollProgram.getId();

        final PollProgram pollProgramReadFromDatabase = em.find(PollProgram.class, pollProgramId);

        assertNotNull(pollProgramReadFromDatabase);
        assertEquals(pollProgramId, pollProgramReadFromDatabase.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_PollSearchField_POLL_ID() {

    	final String testValue1 = "testValue1";
    	final String testValue2 = "testValue2";
    	final List<String> listOfPollSearchKeyValues = Arrays.asList(testValue1, testValue2);
    	
        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues);
        
        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        final boolean isDynamic = true;

        final String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        final Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(number);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_SearchField_TERMINAL_TYPE() {

        /***
         * Ok MobileTerminalTypeEnum values:
         * INMARSAT_C, IRIDIUM;
         */

        final String mobileTerminalTypeEnumInmarsatC = "INMARSAT_C";
        final String mobileTerminalTypeEnumIridium = "IRIDIUM";

        final List<String> listOfPollSearchKeyValues = Arrays.asList(mobileTerminalTypeEnumInmarsatC, mobileTerminalTypeEnumIridium);

        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues);

        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        final boolean isDynamic = true;

        final String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        final Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

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
        final String pollTypeEnumProgramPoll = "PROGRAM_POLL";
        final String pollTypeEnumSamplingPoll = "SAMPLING_POLL";
        final String pollTypeEnumManualPoll = "MANUAL_POLL";
        final String pollTypeEnumConfigurationPoll = "CONFIGURATION_POLL";

        final List<String> listOfPollSearchKeyValues = Arrays.asList(pollTypeEnumProgramPoll, pollTypeEnumSamplingPoll,
                pollTypeEnumManualPoll, pollTypeEnumConfigurationPoll);

        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_TYPE);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.POLL_TYPE);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues);

        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        final boolean isDynamic = true;

        final String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        final Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(number);
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_settingPollSearchField_CONNECT_ID_inPollSearchKeyValueWillBuildNoneWorkingSqlPhrase() {

        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.CONNECT_ID);

        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        final boolean isDynamic = true;

        final String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        final Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("Invalid path: 'tc.connectValue' [SELECT COUNT (DISTINCT p) FROM eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE tc.connectValue IN (:connectionValue) ]");
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_settingPollSearchField_USER_inPollSearchKeyValueWillCauseTypeMismatch() {

        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.USER);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.USER);

        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
        final boolean isDynamic = true;

        final String countSearchSql = PollSearchMapper.createCountSearchSql(listOfPollSearchKeyValue, isDynamic);

        final Long number = pollDao.getPollListSearchCount(countSearchSql, listOfPollSearchKeyValue, isDynamic);

        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("Type specified for TypedQuery [java.lang.Long] is incompatible with query return type [class eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll]");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_failOnSqlReplaceToken() {

        final String sql = "SELECT COUNT (DISTINCT p) FROM Poll p ";
        final List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();
        final boolean isDynamic = true;
        final Long number = pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue, isDynamic);

    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_noSqlPhraseCausesException() {

        final String sql = "";
        final List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();
        final boolean isDynamic = true;
        final Long number = pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue, isDynamic);

        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("unexpected end of subtree []");
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchCount_malformedSqlPhraseCausesException() {

        final String sql = "SELECT * FROM Poll p";
        final List<PollSearchKeyValue> listOfPollSearchKeyValue = new ArrayList<>();
        final boolean isDynamic = true;
        final Long number = pollDao.getPollListSearchCount(sql, listOfPollSearchKeyValue, isDynamic);

        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("unexpected token: * near line");
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_settingPollSearchField_CONNECT_ID_inPollSearchKeyValueWillBuildNoneWorkingSqlPhrase() throws PollDaoException {

        final boolean isDynamic = true;
        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.CONNECT_ID);

        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        final Integer page = 1;
        final Integer listSize = 2;

        final String selectSearchSql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        final List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, selectSearchSql, listOfPollSearchKeyValue, isDynamic);

        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("Invalid path: 'tc.connectValue' [SELECT DISTINCT p FROM eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE tc.connectValue IN (:connectionValue)  AND tc.connectValue IN (:connectionValue) ]");
    }

    @Test(expected = EJBTransactionRolledbackException.class)
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_pageCountLessThanZeroThrowsException() throws PollDaoException {
    	
    	final boolean isDynamic = true;
    	
        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.CONNECT_ID);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.CONNECT_ID);

        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        final Integer page = 0;
        final Integer listSize = 1;
        
        final String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        final List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue, isDynamic);
        
        thrown.expect(EJBTransactionRolledbackException.class);
        thrown.expectMessage("Negative value (-1) passed to setFirstResult");  
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_POLL_ID() throws PollDaoException {

    	final Poll poll = createPollHelper();
    	pollDao.createPoll(poll);
        em.flush();
    	
        final boolean isDynamic = true;
        
        final String testValue1 = "testValue1";
    	final String testValue2 = "testValue2";
    	final List<String> pollSearchKeyValueList = Arrays.asList(testValue1, testValue2);
        
        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue1.setValues(pollSearchKeyValueList);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.POLL_ID);
        pollSearchKeyValue2.setValues(pollSearchKeyValueList);

        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        final Integer page = 1;
        final Integer listSize = 2;

        final String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        final List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_POLL_TYPE() throws PollDaoException {

        /***
         * Ok PollTypeEnum values:
         * PROGRAM_POLL(1),
         * SAMPLING_POLL(2),
         * MANUAL_POLL(3),
         * CONFIGURATION_POLL(4);
         */
        final String pollTypeEnumProgramPoll = "PROGRAM_POLL";
        final String pollTypeEnumSamplingPoll = "SAMPLING_POLL";
        final String pollTypeEnumManualPoll = "MANUAL_POLL";
        final String pollTypeEnumConfigurationPoll = "CONFIGURATION_POLL";

        final List<String> listOfPollSearchKeyValues = Arrays.asList(pollTypeEnumProgramPoll, pollTypeEnumSamplingPoll,
                pollTypeEnumManualPoll, pollTypeEnumConfigurationPoll);

        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.POLL_TYPE);
        pollSearchKeyValue1.setValues(listOfPollSearchKeyValues);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.POLL_TYPE);
        pollSearchKeyValue2.setValues(listOfPollSearchKeyValues);

        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);
    	
        final boolean isDynamic = true;
        
        final Integer page = 1;
        final Integer listSize = 2;

        final String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        final List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_TERMINAL_TYPE() throws PollDaoException {

    	/*
         * Ok MobileTerminalTypeEnum values:
         * INMARSAT_C, IRIDIUM;
         */
    	
    	final String mobileTerminalTypeEnumInmarsatC = "INMARSAT_C";
        final String mobileTerminalTypeEnumIridium = "IRIDIUM";

        final List<String> pollSearchKeyValuesList = Arrays.asList(mobileTerminalTypeEnumInmarsatC, mobileTerminalTypeEnumIridium);
    	
        final boolean isDynamic = true;
        
        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue1.setValues(pollSearchKeyValuesList);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.TERMINAL_TYPE);
        pollSearchKeyValue2.setValues(pollSearchKeyValuesList);

        final List<PollSearchKeyValue> listOfPollSearchKeyValues = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        final Integer page = 1;
        final Integer listSize = 2;

        final String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValues, isDynamic);

        final List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValues, isDynamic);

        assertNotNull(pollList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPollListSearchPaginated_PollSearchField_USER() throws PollDaoException {

    	final String testValue1 = "testValue1";
    	final String testValue2 = "testValue2";
    	final List<String> pollSearchKeyValueList = Arrays.asList(testValue1, testValue2);
    	
        final boolean isDynamic = true;
    	    	    	
        final PollSearchKeyValue pollSearchKeyValue1 = new PollSearchKeyValue();
        pollSearchKeyValue1.setSearchField(PollSearchField.USER);
        pollSearchKeyValue1.setValues(pollSearchKeyValueList);

        final PollSearchKeyValue pollSearchKeyValue2 = new PollSearchKeyValue();
        pollSearchKeyValue2.setSearchField(PollSearchField.USER);
        pollSearchKeyValue2.setValues(pollSearchKeyValueList);

        final List<PollSearchKeyValue> listOfPollSearchKeyValue = Arrays.asList(pollSearchKeyValue1, pollSearchKeyValue2);

        final Integer page = 2;
        final Integer listSize = 1;

        final String sql = PollSearchMapper.createSelectSearchSql(listOfPollSearchKeyValue, isDynamic);

        final List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, listOfPollSearchKeyValue, isDynamic);

        assertNotNull(pollList);
    }

    private Poll createPollHelper() {

        final String uuid = UUID.randomUUID().toString();

        final Poll poll = new Poll();
        poll.setGuid(uuid);
        poll.setUpdateTime(new Date());
        poll.setUpdatedBy("testUser");

        return poll;
    }
}
