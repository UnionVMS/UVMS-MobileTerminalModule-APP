package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.List;

@RunWith(Arquillian.class)
public class ConfigServiceBeanIntTest extends TransactionalTests {




    @EJB
    private ConfigService configService;

    @Test
    @OperateOnDeployment("normal")
    public void testMe() throws MobileTerminalException {
        List<ConfigList> rs =  configService.getConfig();
        if(rs != null){
            for(ConfigList configList : rs){
                String name = configList.getName();
                List<String> values = configList.getValue();
            }
        }
    }


}
