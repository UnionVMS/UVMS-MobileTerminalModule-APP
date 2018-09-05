package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAssignQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.bean.MessageProducerBeanMock;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
import eu.europa.fisheries.uvms.mobileterminal.service.arquillian.helper.TestPollHelper;

/**
 * Created by thofan on 2017-05-29.
 */


@RunWith(Arquillian.class)
public class MobileTerminalServiceIntTest extends TransactionalTests {

    // TODO we do test on those transactions that are wrong in construction

    @EJB
    private TestPollHelper testPollHelper;

    @EJB
    private MobileTerminalService mobileTerminalService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String USERNAME = "TEST_USERNAME";
    private static final String NEW_MOBILETERMINAL_TYPE = "IRIDIUM";
    private static final String TEST_COMMENT = "TEST_COMMENT";

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalByIdAndDataSourceQueue() throws MobileTerminalException {

        String createdMobileTerminalId;
        String fetchedMobileTerminalGuid;

        System.setProperty(MessageProducerBeanMock.MESSAGE_PRODUCER_METHODS_FAIL, "false");
        String connectId = UUID.randomUUID().toString();
        MobileTerminal createdMobileTerminal = testPollHelper.createMobileTerminal(connectId);
        createdMobileTerminalId = createdMobileTerminal.getGuid();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(createdMobileTerminalId);

        MobileTerminalType fetchedMobileTerminalType = mobileTerminalService.getMobileTerminalById(mobileTerminalId, DataSourceQueue.INTERNAL);
        assertNotNull(fetchedMobileTerminalType);

        fetchedMobileTerminalGuid = fetchedMobileTerminalType.getMobileTerminalId().getGuid();
        assertEquals(fetchedMobileTerminalGuid, createdMobileTerminalId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalById() throws MobileTerminalException {

        String createdMobileTerminalId;
        String fetchedMobileTerminalGuid;

        System.setProperty(MessageProducerBeanMock.MESSAGE_PRODUCER_METHODS_FAIL, "false");
        String connectId = UUID.randomUUID().toString();
        MobileTerminal createdMobileTerminal = testPollHelper.createMobileTerminal(connectId);
        createdMobileTerminalId = createdMobileTerminal.getGuid();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(createdMobileTerminalId);

        MobileTerminalType fetchedMobileTerminalType = mobileTerminalService.getMobileTerminalById(createdMobileTerminalId);
        assertNotNull(fetchedMobileTerminalType);

        fetchedMobileTerminalGuid = fetchedMobileTerminalType.getMobileTerminalId().getGuid();
        assertEquals(fetchedMobileTerminalGuid, createdMobileTerminalId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal() throws MobileTerminalException {

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal() throws MobileTerminalException {

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);

        MobileTerminalType updated = upsertMobileTerminalType(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getType());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal() throws MobileTerminalException {

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);

        MobileTerminalType updated = updateMobileTerminalType(created);

        assertNotNull(updated);
        assertEquals(NEW_MOBILETERMINAL_TYPE, updated.getType());
        assertEquals(MobileTerminalSource.INTERNAL, updated.getSource());
    }

    @Test
    @OperateOnDeployment("normal")
    public void assignMobileTerminal() throws MobileTerminalException {

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);

        MobileTerminalAssignQuery query = new MobileTerminalAssignQuery();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(created.getMobileTerminalId().getGuid());
        query.setMobileTerminalId(mobileTerminalId);
        String guid = UUID.randomUUID().toString();
        query.setConnectId(guid);

        MobileTerminalType mobileTerminalType = mobileTerminalService.assignMobileTerminal(query, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminalType);
    }

    @Test
    @OperateOnDeployment("normal")
    public void unAssignMobileTerminalFromCarrier() throws MobileTerminalException {

        MobileTerminalType created = createMobileTerminalType();
        created.setConnectId(UUID.randomUUID().toString());
        assertNotNull(created);

        MobileTerminalAssignQuery query = new MobileTerminalAssignQuery();
        MobileTerminalId mobileTerminalId = new MobileTerminalId();
        mobileTerminalId.setGuid(created.getMobileTerminalId().getGuid());
        query.setMobileTerminalId(mobileTerminalId);
        query.setConnectId(created.getConnectId());

        MobileTerminalType mobileTerminalType = mobileTerminalService.assignMobileTerminal(query, TEST_COMMENT, USERNAME);
        assertNotNull(mobileTerminalType);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_Plugin() throws MobileTerminalException {

        thrown.expect(EJBTransactionRolledbackException.class);
//        thrown.expectMessage("Cannot create Mobile terminal when plugin is not null");

        MobileTerminalType mobileTerminalType = testPollHelper.createBasicMobileTerminal();
        mobileTerminalType.setPlugin(null);
        mobileTerminalService.createMobileTerminal(mobileTerminalType, MobileTerminalSource.INTERNAL, USERNAME);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createMobileTerminal_WillFail_Null_SerialNumber() throws MobileTerminalException {

        thrown.expect(MobileTerminalModelException.class);
//        thrown.expectMessage("Cannot create mobile terminal without serial number");

        MobileTerminalType mobileTerminalType = testPollHelper.createBasicMobileTerminal();
        List<MobileTerminalAttribute> attributes = mobileTerminalType.getAttributes();
        for (MobileTerminalAttribute attribute : attributes) {
            if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(attribute.getType())) {
                attribute.setType(null);
                attribute.setValue(null);
                break;
            }
        }
        mobileTerminalService.createMobileTerminal(mobileTerminalType, MobileTerminalSource.INTERNAL, USERNAME);
    }

    @Test
    @OperateOnDeployment("normal")
    public void upsertMobileTerminal_WillFail_Null_TerminalId() throws MobileTerminalException {

        thrown.expect(InputArgumentException.class);
//        thrown.expectMessage("No Mobile terminalId in request");

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);

        created.setMobileTerminalId(null);

        upsertMobileTerminalType(created);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateMobileTerminal_WillFail_Null_TerminalId() throws MobileTerminalException {

        thrown.expect(InputArgumentException.class);
//        thrown.expectMessage("Non valid id of terminal to update");

        MobileTerminalType created = createMobileTerminalType();
        assertNotNull(created);

        created.setMobileTerminalId(null);

        updateMobileTerminalType(created);
    }

    private MobileTerminalType createMobileTerminalType() throws MobileTerminalException {
        MobileTerminalType mobileTerminalType = testPollHelper.createBasicMobileTerminal();
        return mobileTerminalService.createMobileTerminal(mobileTerminalType, MobileTerminalSource.INTERNAL, USERNAME);
    }

    private MobileTerminalType updateMobileTerminalType(MobileTerminalType created) throws MobileTerminalException {
        created.setType(NEW_MOBILETERMINAL_TYPE);
        return mobileTerminalService.updateMobileTerminal(created, TEST_COMMENT, MobileTerminalSource.INTERNAL, USERNAME);
    }

    private MobileTerminalType upsertMobileTerminalType(MobileTerminalType created) throws MobileTerminalException {
        created.setType(NEW_MOBILETERMINAL_TYPE);
        return mobileTerminalService.upsertMobileTerminal(created, MobileTerminalSource.INTERNAL, USERNAME);
    }
}
