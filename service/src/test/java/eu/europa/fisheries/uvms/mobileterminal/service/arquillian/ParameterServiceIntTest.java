package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigHelper;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;

@RunWith(Arquillian.class)
public class ParameterServiceIntTest extends TransactionalTests {

    @EJB
    private ParameterService parameterService;

    @EJB
    private ConfigHelper configHelper;
    
    @Test
    @OperateOnDeployment("normal")
    public void testGetConfig() throws Exception {
    	parameterService.init(configHelper.getModuleName());
    	List<SettingType> allSettings = parameterService.getAllSettings();
    	Assert.assertNotNull(allSettings);
    	Assert.assertFalse(allSettings.isEmpty());    	
    }

}