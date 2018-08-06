package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollToCommandRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalUnmarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MappedPollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.MobileTerminalService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.ConfigServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MappedPollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalConfigHelper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.timer.MobileTerminalExecutorServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.timer.PluginTimerTask;
import eu.europa.ec.fisheries.uvms.mobileterminal.timer.PollTimerTask;
import eu.europa.fisheries.uvms.mobileterminal.service.arquillian.helper.TestPollHelper;

@ArquillianSuiteDeployment
public abstract class BuildMobileTerminalServiceDeployment {

    private final static Logger LOG = LoggerFactory.getLogger(BuildMobileTerminalServiceDeployment.class);

    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        // Import Maven runtime dependencies
        File[] files = Maven.configureResolver().loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
        printFiles(files);

        // Embedding war package which contains the test class is needed
        // So that Arquillian can invoke test class through its servlet test runner
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");
        testWar.addPackages(true, "com.tocea.easycoverage.framework.api");
        testWar.addPackages(true, "eu.europa.fisheries.uvms.mobileterminal.service");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.dto");
        //testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception");

        testWar.addClass(TransactionalTests.class);
        testWar.addClass(TestPollHelper.class);
        testWar.addClass(ConfigService.class);
        testWar.addClass(ConfigServiceBeanMock.class);
        testWar.addClass(MobileTerminalMessageException.class);
        testWar.addClass(MobileTerminalConfigHelper.class);
//        testWar.addClass(eu.europa.ec.fisheries.uvms.config.service.ParameterService.class);
//        testWar.addClass(eu.europa.ec.fisheries.uvms.config.service.ParameterServiceBean.class);
//        testWar.addClass(eu.europa.ec.fisheries.schema.config.types.v1.SettingType.class);
//        testWar.addClass(eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException.class);
//        testWar.addClass(eu.europa.ec.fisheries.uvms.config.model.exception.InputArgumentException.class);
//        testWar.addClass(eu.europa.ec.fisheries.uvms.config.service.entity.Parameter.class);

        // för Mapped PollServiceBean
        testWar.addClass(MappedPollService.class);
        testWar.addClass(MappedPollServiceBean.class);
        testWar.addClass(PollRequestType.class);
        testWar.addClass(CreatePollResultDto.class);
        testWar.addClass(MobileTerminalServiceException.class);
        testWar.addClass(PollService.class);
        testWar.addClass(PollServiceBean.class);
        testWar.addClass(MobileTerminalService.class);
        testWar.addClass(MobileTerminalServiceBean.class);
        testWar.addClass(PollService.class);
        testWar.addClass(PollServiceBean.class);
        testWar.addClass(PollToCommandRequestMapper.class);
        testWar.addClass(PollMapper.class);
        testWar.addClass(AuditModuleRequestMapper.class);

        testWar.addClass(PluginService.class);
        testWar.addClass(MobileTerminalModelMapperException.class);
        testWar.addClass(MobileTerminalModelException.class);
        testWar.addClass(MobileTerminalException.class);
        testWar.addClass(MobileTerminalUnmarshallException.class);
        testWar.addClass(ConfigServiceException.class);

        // Empty beans for EE6 CDI
        testWar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");

        testWar.addAsLibraries(files);
        return testWar;
    }

    private static void printFiles(File[] files) {

        List<File> filesSorted = new ArrayList<>();
        Collections.addAll(filesSorted, files);

        Collections.sort(filesSorted, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        LOG.info("FROM POM - begin");
        for(File f : filesSorted){
            LOG.info("       --->>>   "   +   f.getName());
        }
        LOG.info("FROM POM - end");
    }
}
