package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.arquillian.TransactionalTests;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.exception.SearchMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.poll.PollSearchMapper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by roblar on 2017-05-04.
 */
@RunWith(Arquillian.class)
public class PollSearchMapperIntTest extends TransactionalTests {

    @Test
    @OperateOnDeployment("normal")
    public void testCreateSearchFields() throws SearchMapperException {

        final List<ListCriteria> listOfListCriteria = createListOfListCriteriaHelper(SearchKey.POLL_ID);

        final List<PollSearchKeyValue> pollSearchKeyValueList = PollSearchMapper.createSearchFields(listOfListCriteria);

        assertNotNull(pollSearchKeyValueList);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateCountSearchSql() throws SearchMapperException {

        // Note: Any value for isDynamic yields the same SQL phrases.
        final boolean isDynamic = true;

        final List<SearchKey> searchKeyList = Arrays.asList(SearchKey.CONNECT_ID, SearchKey.POLL_ID,
                SearchKey.POLL_TYPE, SearchKey.TERMINAL_TYPE, SearchKey.USER);

        for(final SearchKey searchKey : searchKeyList) {
            final List<ListCriteria> listOfListCriteria = createListOfListCriteriaHelper(searchKey);
            final List<PollSearchKeyValue> pollSearchKeyValueList = PollSearchMapper.createSearchFields(listOfListCriteria);

            final String selectSql = PollSearchMapper.createCountSearchSql(pollSearchKeyValueList, isDynamic);
            assertNotNull(selectSql);
            assertTrue(selectSql.contains("SELECT COUNT (DISTINCT p) FROM Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE"));

            switch(searchKey) {
                case CONNECT_ID:
                    assertTrue(selectSql.contains("tc.connectValue IN (:connectionValue) "));
                    continue;
                case POLL_ID:
                    assertTrue(selectSql.contains("p.guid IN (:guid) "));
                    continue;
                case POLL_TYPE:
                    assertTrue(selectSql.contains("p.pollType IN (:pollType) "));
                    continue;
                case TERMINAL_TYPE:
                    assertTrue(selectSql.contains("mt.mobileTerminalType IN (:mobileTerminalType) "));
                    continue;
                case USER:
                    assertTrue(selectSql.contains("pb.creator IN (:creator) "));
            }
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateSelectSearchSql() throws SearchMapperException {

        // Note: Any value for isDynamic yields the same SQL phrases.
        final boolean isDynamic = true;

        final List<SearchKey> searchKeyList = Arrays.asList(SearchKey.CONNECT_ID, SearchKey.POLL_ID,
                SearchKey.POLL_TYPE, SearchKey.TERMINAL_TYPE, SearchKey.USER);

        for(final SearchKey searchKey : searchKeyList) {
            final List<ListCriteria> listOfListCriteria = createListOfListCriteriaHelper(searchKey);
            final List<PollSearchKeyValue> pollSearchKeyValueList = PollSearchMapper.createSearchFields(listOfListCriteria);

            final String selectSql = PollSearchMapper.createSelectSearchSql(pollSearchKeyValueList, isDynamic);
            assertNotNull(selectSql);
            assertTrue(selectSql.contains("SELECT DISTINCT p FROM Poll p  INNER JOIN p.pollBase pb  INNER JOIN pb.mobileterminal mt  WHERE"));

            switch(searchKey) {
                case CONNECT_ID:
                    assertTrue(selectSql.contains("tc.connectValue IN (:connectionValue)"));
                    continue;
                case POLL_ID:
                    assertTrue(selectSql.contains("p.guid IN (:guid)"));
                    continue;
                case POLL_TYPE:
                    assertTrue(selectSql.contains("p.pollType IN (:pollType)"));
                    continue;
                case TERMINAL_TYPE:
                    assertTrue(selectSql.contains("mt.mobileTerminalType IN (:mobileTerminalType)"));
                    continue;
                case USER:
                    assertTrue(selectSql.contains("pb.creator IN (:creator)"));
            }
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreatePollableSearchSql() throws SearchMapperException {

        final String id1 = "test_id1";
        final String id2 = "test_id2";
        final List<String> idList = Arrays.asList(id1, id2);

        final String veryComplexSqlPhrase = PollSearchMapper.createPollableSearchSql(idList);

        assertNotNull(veryComplexSqlPhrase);
        assertEquals(veryComplexSqlPhrase, "SELECT DISTINCT c FROM Channel c INNER JOIN FETCH c.mobileTerminal mt INNER JOIN FETCH mt.mobileTerminalEvents me INNER JOIN FETCH me.pollChannel pc INNER JOIN FETCH mt.plugin p  INNER JOIN FETCH p.capabilities cap  WHERE  c.guid = pc.guid  AND me.active = true  AND mt.archived = '0' AND mt.inactivated = '0' AND p.pluginInactive = '0'  AND (cap.name = 'POLLABLE' AND UPPER(cap.value) = 'TRUE' )  AND (me.connectId is not null)  AND me.connectId IN :idList ORDER BY c.guid DESC ");
    }

    private List<ListCriteria> createListOfListCriteriaHelper(final SearchKey searchKey) {

        final ListCriteria listCriteria1 = new ListCriteria();
        listCriteria1.setKey(searchKey);
        listCriteria1.setValue("testListCriteriaValue1");

        final ListCriteria listCriteria2 = new ListCriteria();
        listCriteria2.setKey(searchKey);
        listCriteria2.setValue("testListCriteriaValue2");

        return Arrays.asList(listCriteria1, listCriteria2);
    }
}
