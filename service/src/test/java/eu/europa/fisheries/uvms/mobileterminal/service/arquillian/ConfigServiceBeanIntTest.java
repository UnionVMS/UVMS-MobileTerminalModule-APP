package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConfigType;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

@RunWith(Arquillian.class)
public class ConfigServiceBeanIntTest extends TransactionalTests {

    @EJB
    private ConfigService configService;

    @Test
    @OperateOnDeployment("normal")
    public void testGetConfig() throws MobileTerminalException {
        List<ConfigList> rs =  configService.getConfig();
        Assert.assertNotNull(rs);
        Assert.assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TIME_SPAN.toString()));
        Assert.assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TYPE.toString()));
        Assert.assertTrue(configListContains(rs, MobileTerminalConfigType.TRANSPONDERS.toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    @Ignore
    public void testGetRegisteredMobileTerminalPlugins() throws MobileTerminalException {
        List<ServiceResponseType> registeredMobileTerminalPlugins =  configService.getRegisteredMobileTerminalPlugins();
        Assert.assertNotNull(registeredMobileTerminalPlugins);
        // TODO: How to test this properly
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPlugins() throws MobileTerminalException {
        List<PluginService> pluginList = new ArrayList<>();
        PluginService pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("IRIDIUM_TEST_SERVICE");
        pluginService.setSatelliteType("IRIDIUM");
        pluginService.setServiceName("TEST_SERVICE");
        pluginList.add(pluginService);
        List<Plugin> plugins = configService.upsertPlugins(pluginList, "TEST");
        Assert.assertNotNull(plugins);
        Assert.assertTrue(pluginsContains(pluginList, "TEST_SERVICE"));
    }


    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadServiceName() throws MobileTerminalException {
        List<PluginService> pluginList = new ArrayList<>();
        PluginService pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("IRIDIUM_TEST_SERVICE");
        pluginService.setSatelliteType("IRIDIUM");
        pluginService.setServiceName("");
        pluginList.add(pluginService);
        List<Plugin> plugins = configService.upsertPlugins(pluginList, "TEST");
        Assert.assertNotNull(plugins);
        Assert.assertFalse(pluginsContains(pluginList, ""));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetTerminalSystems() throws MobileTerminalException {
        List<TerminalSystemType> rs =  configService.getTerminalSystems();
        Assert.assertNotNull(rs);
        Assert.assertTrue(rs.size() > 1);
        Assert.assertTrue(terminalSystemsContains(rs, "IRIDIUM"));
        Assert.assertTrue(terminalSystemsContains(rs, "INMARSAT-C"));
    }


    private boolean terminalSystemsContains(List<TerminalSystemType> list, String name) {
        for(TerminalSystemType each : list) {
            if(each.getType().equals(name)) {
                return true;
            }
        }
        return false;
    }


    private boolean configListContains(List<ConfigList> configLists, String value) {
        for(ConfigList each : configLists) {
            if(value.equals(each.getName())) {
                return true;
            }
        }

        return false;
    }

    private boolean pluginsContains(List<PluginService> pluginList, String name) {

        for(PluginService each : pluginList) {
            if(each.getServiceName().equals(name)) {
                return true;
            }
        }

        return false;
    }

}
