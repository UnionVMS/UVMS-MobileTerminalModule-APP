package eu.europa.fisheries.uvms.mobileterminal.module.arquillian;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class MobileterminalModuleITest extends BuildMobileterminalModuleTestDeployment {

    @Test
    @OperateOnDeployment("mobileterminalmodule")
    public void validateEarModuleDeploymentTest()  {
    }

}
