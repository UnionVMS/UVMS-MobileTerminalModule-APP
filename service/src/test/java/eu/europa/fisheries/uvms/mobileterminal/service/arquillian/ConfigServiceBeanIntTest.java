package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConfigServiceBeanIntTest extends TransactionalTests {
    /*

    @EJB
    private ConfigServiceBean configService;

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void testGetConfig() throws MobileTerminalException {
        List<ConfigList> rs =  configService.getConfig();
        assertNotNull(rs);
        assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TIME_SPAN.toString()));
        assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TYPE.toString()));
        assertTrue(configListContains(rs, MobileTerminalConfigType.TRANSPONDERS.toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetRegisteredMobileTerminalPlugins_fail() throws MobileTerminalException {

        thrown.expect(MobileTerminalException.class);
        // thrown.expectMessage("Failed to map to exchange get service list request");

        System.setProperty(MessageProducerBean.MESSAGE_PRODUCER_METHODS_FAIL, "true");
        configService.getRegisteredMobileTerminalPlugins();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPlugins() throws MobileTerminalException {
        List<PluginService> pluginList = Collections.singletonList(createPluginService());
        List<Plugin> plugins = configService.upsertPlugins(pluginList, "TEST");
        assertNotNull(plugins);
        assertTrue(pluginsContains(pluginList, "TEST_SERVICE"));
    }

    @Test
    @OperateOnDeployment("normal")
   public void testUpsertPluginsUpdate() throws MobileTerminalException {
        List<PluginService> pluginList = Collections.singletonList(createPluginService());
        List<Plugin> plugins = configService.upsertPlugins(pluginList, "TEST");
        assertNotNull(plugins);
        assertTrue(pluginsContains(pluginList, "TEST_SERVICE"));
        assertEquals(1, pluginList.size());
        //assertEquals(4, plugins.size());

        for(PluginService ps : pluginList) {
            ps.setLabelName("NEW_IRIDIUM_TEST_SERVICE");
        }

        assertEquals(1, pluginList.size());

        List<Plugin> updatedPlugins = configService.upsertPlugins(pluginList, "TEST");
        assertNotNull(updatedPlugins);
        assertEquals(1, updatedPlugins.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadServiceName() throws MobileTerminalException {

        thrown.expect(InputArgumentException.class);
        // thrown.expectMessage("No service name");

        List<PluginService> pluginList = new ArrayList<>();
        PluginService pluginService = createPluginService();
        pluginService.setServiceName("");
        pluginList.add(pluginService);

        configService.upsertPlugins(pluginList, "TEST");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadLabelName() throws MobileTerminalException {

        thrown.expect(InputArgumentException.class);
        // thrown.expectMessage("No plugin name");

        List<PluginService> pluginList = new ArrayList<>();
        PluginService pluginService = createPluginService();
        pluginService.setLabelName("");
        pluginList.add(pluginService);

        configService.upsertPlugins(pluginList, "TEST");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadSatelliteType() throws MobileTerminalException {

        thrown.expect(InputArgumentException.class);
        // thrown.expectMessage("No satellite type");

        List<PluginService> pluginList = new ArrayList<>();
        PluginService pluginService = createPluginService();
        pluginService.setSatelliteType("");
        pluginList.add(pluginService);

        configService.upsertPlugins(pluginList, "TEST");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetTerminalSystems() throws MobileTerminalException {
        MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();
        mobileTerminalPlugin.setName("TEST");
        mobileTerminalPlugin.setPluginSatelliteType("TEST");
        mobileTerminalPlugin.setDescription("TEST");
        mobileTerminalPlugin.setPluginSatelliteType(MobileTerminalTypeEnum.INMARSAT_C.toString());
        mobileTerminalPlugin.setPluginInactive(false);
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        List<TerminalSystemType> rs =  configService.getTerminalSystems();
        assertNotNull(rs);
        assertTrue(rs.size() > 0);
        assertTrue(terminalSystemsContains(rs, MobileTerminalTypeEnum.INMARSAT_C.toString()));
    }

    private PluginService createPluginService() {
        PluginService pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("IRIDIUM_TEST_SERVICE");
        pluginService.setSatelliteType("IRIDIUM");
        pluginService.setServiceName("TEST_SERVICE");
        return pluginService;
    }

    private boolean terminalSystemsContains(List<TerminalSystemType> list, String type) {
        for(TerminalSystemType item : list) {
            if(item.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean configListContains(List<ConfigList> configLists, String value) {
        for(ConfigList item : configLists) {
            if(value.equals(item.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean pluginsContains(List<PluginService> pluginList, String name) {
        for(PluginService item : pluginList) {
            if(item.getServiceName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    */
}
