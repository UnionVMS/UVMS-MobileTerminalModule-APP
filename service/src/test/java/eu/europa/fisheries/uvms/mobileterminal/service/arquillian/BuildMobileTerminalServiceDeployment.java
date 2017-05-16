package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollDomainModelBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.PollDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.AuditModuleRequestMapper;
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
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ArquillianSuiteDeployment
public abstract class BuildMobileTerminalServiceDeployment {

    final static Logger LOG = LoggerFactory.getLogger(BuildMobileTerminalServiceDeployment.class);


    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        // Import Maven runtime dependencies
        File[] files = Maven.configureResolver().workOffline().loadPomFromFile("pom.xml")
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
        testWar.addClass(ConfigService.class);
        testWar.addClass(ConfigServiceBean.class);
        testWar.addClass(MobileTerminalMessageException.class);
        testWar.addClass(MobileTerminalConfigHelper.class);



        // för Mapped PollServiceBean
        testWar.addClass(MappedPollService.class);
        testWar.addClass(MappedPollServiceBean.class);
        testWar.addClass(PollRequestType.class);
        testWar.addClass(CreatePollResultDto.class);
        testWar.addClass(MobileTerminalServiceException.class);
        testWar.addClass(MappedPollServiceBeanIntTest.class);
        testWar.addClass(PollService.class);
        testWar.addClass(PollServiceBean.class);
        testWar.addClass(MobileTerminalService.class);
        testWar.addClass(MobileTerminalServiceBean.class);
        testWar.addClass(PollService.class);
        testWar.addClass(PollServiceBean.class);
        testWar.addClass(PollToCommandRequestMapper.class);
        testWar.addClass(AuditModuleRequestMapper.class);

        testWar.addClass(PluginService.class);
        //testWar.deleteClass(PluginServiceBean.class);
        //testWar.addClass(PluginServiceMOCKBean.class);
        testWar.addClass(MobileTerminalModelMapperException.class);
        testWar.addClass(MobileTerminalModelException.class);
        testWar.addClass(MobileTerminalException.class);
        testWar.addClass(MobileTerminalUnmarshallException.class);
        testWar.addClass(ConfigServiceException.class);


        //    testWar.addClass(ConfigModelBean.class);
        //testWar.addClass(NoEntityFoundException.class);
        //testWar.addClass(TerminalDaoException.class);
        //testWar.addClass(MobileTerminalModelException.class);
        //testWar.addClass(PollDaoException.class);
/*
        testWar.addClass(MobileTerminalService.class);
        testWar.addClass(MobileTerminalServiceBean.class);
        testWar.addClass(PluginService.class);
        testWar.addClass(PluginServiceBean.class);
        testWar.addClass(PollDto.class);

        testWar.addClass(PollType.class);
*/

        // Empty beans for EE6 CDI
        testWar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        testWar.addAsLibraries(files);



        return testWar;
    }

    private static void printFiles(File[] files) {

        List<File> filesSorted = new ArrayList<>();
        for(File f : files){
            filesSorted.add(f);
        }

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

