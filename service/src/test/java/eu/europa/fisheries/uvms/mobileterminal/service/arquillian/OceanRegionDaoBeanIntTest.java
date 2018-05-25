package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean.OceanRegionDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.OceanRegion;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class OceanRegionDaoBeanIntTest extends TransactionalTests {

    @EJB
    private OceanRegionDaoBean oceanRegionDao;

    @Test
    @OperateOnDeployment("normal")
    public void testGetOceanRegionList() throws ConfigDaoException {
        // Since we have at least 4 regions inserted by LIQUIBASE this should always work
        List<OceanRegion> oceanRegions = oceanRegionDao.getOceanRegionList();
        assertNotNull(oceanRegions);
        assertTrue(oceanRegions.size() > 3);
    }
}
