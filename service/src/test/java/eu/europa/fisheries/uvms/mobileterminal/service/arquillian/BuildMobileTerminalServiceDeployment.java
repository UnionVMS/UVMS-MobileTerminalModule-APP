package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.mobileterminal.ConfigModel;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

@ArquillianSuiteDeployment
public abstract class BuildMobileTerminalServiceDeployment {

    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        // Import Maven runtime dependencies
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies().resolve().withTransitivity().asFile();

        // Embedding war package which contains the test class is needed
        // So that Arquillian can invoke test class through its servlet test runner
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");
        testWar.addPackages(true, "com.tocea.easycoverage.framework.api");
        testWar.addPackages(true, "eu.europa.fisheries.uvms.mobileterminal.service");
 //       testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.service");
 //       testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.service.exception");
 //       testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.dto");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal");


        testWar.addClass(TransactionalTests.class);
        testWar.addClass(ConfigService.class);
        testWar.addClass(ConfigServiceBean.class);
        testWar.addClass(ConfigModel.class);
        testWar.addClass(TransactionalTests.class);
        testWar.addClass(MobileTerminalMessageException.class);
        testWar.addClass(MobileTerminalConfigHelper.class);



        // för Mapped PollServiceBean
        testWar.addClass(MappedPollService.class);
        testWar.addClass(MappedPollServiceBean.class);
        testWar.addClass(PollRequestType.class);
        testWar.addClass(CreatePollResultDto.class);
        testWar.addClass(MobileTerminalServiceException.class);

/*
        testWar.addClass(MobileTerminalService.class);
        testWar.addClass(MobileTerminalServiceBean.class);
        testWar.addClass(PluginService.class);
        testWar.addClass(PluginServiceBean.class);
        testWar.addClass(PollDto.class);
        testWar.addClass(PollService.class);
        testWar.addClass(PollServiceBean.class);
        testWar.addClass(PollType.class);
*/

        // Empty beans for EE6 CDI
        testWar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        testWar.addAsLibraries(files);



        return testWar;
    }



    }

