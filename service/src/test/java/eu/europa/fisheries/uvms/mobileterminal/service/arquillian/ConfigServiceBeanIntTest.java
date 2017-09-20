package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import eu.europa.ec.fisheries.uvms.mobileterminal.message.producer.bean.MessageProducerBean;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConfigType;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;

@RunWith(Arquillian.class)
public class ConfigServiceBeanIntTest extends TransactionalTests {

    @EJB
    private ConfigService configService;

    @EJB
    private MobileTerminalPluginDao mobileTerminalPluginDao;

    @Test
    @OperateOnDeployment("normal")
    public void testGetConfig() throws MobileTerminalException {
        final List<ConfigList> rs =  configService.getConfig();
        Assert.assertNotNull(rs);
        Assert.assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TIME_SPAN.toString()));
        Assert.assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TYPE.toString()));
        Assert.assertTrue(configListContains(rs, MobileTerminalConfigType.TRANSPONDERS.toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetRegisteredMobileTerminalPlugins_fail() {
        //TODO: Rewrite the error handling of getRegisteredMobileTerminalPlugins and extend this test.
        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "true");
        final List<ServiceResponseType> registeredMobileTerminalPlugins =  configService.getRegisteredMobileTerminalPlugins();
        Assert.assertNull(registeredMobileTerminalPlugins);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPlugins() throws MobileTerminalException {
        final List<PluginService> pluginList = new ArrayList<>();
        final PluginService pluginService = createPluginService();
        pluginList.add(pluginService);
        final List<Plugin> plugins = configService.upsertPlugins(pluginList, "TEST");
        Assert.assertNotNull(plugins);
        Assert.assertTrue(pluginsContains(pluginList, "TEST_SERVICE"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadServiceName() {
        final List<PluginService> pluginList = new ArrayList<>();
        final PluginService pluginService = createPluginService();
        pluginService.setServiceName("");
        pluginList.add(pluginService);
        try {
            configService.upsertPlugins(pluginList, "TEST");
        } catch (final MobileTerminalException e) {
            if(!(e instanceof InputArgumentException)) {
                Assert.fail("Should be InputArgumentException");
            }
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadLabelName() {
        final List<PluginService> pluginList = new ArrayList<>();
        final PluginService pluginService = createPluginService();
        pluginService.setLabelName("");
        pluginList.add(pluginService);
        try {
            configService.upsertPlugins(pluginList, "TEST");
        } catch (final MobileTerminalException e) {
            if(!(e instanceof InputArgumentException)) {
                Assert.fail("Should be InputArgumentException");
            }
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetTerminalSystems() throws MobileTerminalException {
        final MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();
        mobileTerminalPlugin.setName("TEST");
        mobileTerminalPlugin.setPluginSatelliteType("TEST");
        mobileTerminalPlugin.setDescription("TEST");
        mobileTerminalPlugin.setPluginSatelliteType(MobileTerminalTypeEnum.INMARSAT_C.toString());
        mobileTerminalPlugin.setPluginInactive(false);
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        final List<TerminalSystemType> rs =  configService.getTerminalSystems();
        Assert.assertNotNull(rs);
        Assert.assertTrue(rs.size() > 0);
        Assert.assertTrue(terminalSystemsContains(rs, MobileTerminalTypeEnum.INMARSAT_C.toString()));
    }

    private PluginService createPluginService() {
        final PluginService pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("IRIDIUM_TEST_SERVICE");
        pluginService.setSatelliteType("IRIDIUM");
        pluginService.setServiceName("TEST_SERVICE");
        return pluginService;
    }


    private boolean terminalSystemsContains(final List<TerminalSystemType> list, final String type) {
        for(final TerminalSystemType each : list) {
            if(each.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean configListContains(final List<ConfigList> configLists, final String value) {
        for(final ConfigList each : configLists) {
            if(value.equals(each.getName())) {
                return true;
            }
        }

        return false;
    }

    private boolean pluginsContains(final List<PluginService> pluginList, final String name) {

        for(final PluginService each : pluginList) {
            if(each.getServiceName().equals(name)) {
                return true;
            }
        }

        return false;
    }

}
