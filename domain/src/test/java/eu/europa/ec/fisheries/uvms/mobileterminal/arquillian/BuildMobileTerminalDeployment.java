package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;

import java.io.File;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.PollDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.poll.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.poll.PollSearchMapper;

/**
 * Created by andreasw on 2017-02-13.
 */
@ArquillianSuiteDeployment
public abstract class BuildMobileTerminalDeployment {

    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        // Import Maven runtime dependencies
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies().resolve().withTransitivity().asFile();

        // Embedding war package which contains the test class is needed
        // So that Arquillian can invoke test class through its servlet test runner
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.constant");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.entity");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.dao");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.mapper");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.exception");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.model.exception");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.util");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.dto");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal.search");
        testWar.addPackages(true, "com.tocea.easycoverage.framework.api");
        
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.model");
        
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.arquillian");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.mobileterminal.arquillian.bean");
        
        testWar.addPackages(true, "eu.europa.ec.fisheries.schema");
        testWar.addClass(TransactionalTests.class);
        testWar.addClass(TerminalDao.class);
        testWar.addClass(TerminalDaoBean.class);
        testWar.addClass(MobileTerminal.class);
        testWar.addClass(MobileTerminalTypeEnum.class);
        testWar.addClass(MobileTerminalPluginDao.class);
        testWar.addClass(MobileTerminalPluginDaoBean.class);
     
        testWar.addClass(PollDaoBean.class);
        testWar.addClass(PollSearchKeyValue.class);
        testWar.addClass(PollSearchMapper.class);

        testWar.addClass(PollProgramDao.class);
        testWar.addClass(PollProgram.class);

        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");
        // Empty beans for EE6 CDI
        testWar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        testWar.addAsLibraries(files);

        return testWar;
    }

}
