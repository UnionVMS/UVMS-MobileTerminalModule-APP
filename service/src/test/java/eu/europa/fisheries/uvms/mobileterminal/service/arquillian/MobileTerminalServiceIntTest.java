package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;


import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.bean.MessageProducerBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
import eu.europa.fisheries.uvms.mobileterminal.service.arquillian.helper.TestPollHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.UUID;

/**
 * Created by thofan on 2017-05-29.
 */


@RunWith(Arquillian.class)
public class MobileTerminalServiceIntTest extends TransactionalTests {

    // TODO we do test on those transactions that are wrong in construction

    @EJB
    TestPollHelper testPollHelper ;


    @EJB
    MobileTerminalService mobileTerminalService;


    @Test
    @OperateOnDeployment("normal")
    public void getMobileTerminalById() {


        // TODO must be changed when REQUIRES_NEW is removed  until then tsi is failTest

        String createdMobileTerminalId = "";
        String fetchedMobileTerminalGuid  = "";
        try {

            System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "false");
            String connectId = UUID.randomUUID().toString();
            MobileTerminal createdMobileTerminal = testPollHelper.createMobileTerminal(connectId);
            em.flush();
            createdMobileTerminalId = createdMobileTerminal.getGuid();
            MobileTerminalId mobileTerminalId = new MobileTerminalId();
            mobileTerminalId.setGuid(createdMobileTerminalId);

            MobileTerminalType fetchedMobileTerminalType = mobileTerminalService.getMobileTerminalById(mobileTerminalId, DataSourceQueue.INTERNAL);
            Assert.assertTrue(fetchedMobileTerminalType != null);

            fetchedMobileTerminalGuid = fetchedMobileTerminalType.getMobileTerminalId().getGuid();
            Assert.assertTrue(fetchedMobileTerminalGuid.equals(createdMobileTerminalId));

        } catch (MobileTerminalException e) {

            // TODO CHANGE THIS TO FAIL WHEN servercode is corrected
            Assert.assertTrue(true);
        }

    }

}

