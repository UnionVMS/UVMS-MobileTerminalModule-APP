package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;


import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.OceanRegionDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.OceanRegion;

@RunWith(Arquillian.class)
public class OceanRegionDaoBeanIntTest extends TransactionalTests {

    @EJB
    private OceanRegionDao oceanRegionDao;


    @Test
    @OperateOnDeployment("normal")
    public void testGetOceanRegionList() throws ConfigDaoException {
        // Since we have at least 4 regions inserted by LIQUIBASE this should always work
        List<OceanRegion> oceanRegions = oceanRegionDao.getOceanRegionList();
        Assert.assertNotNull(oceanRegions);
        Assert.assertTrue(oceanRegions.size() > 3);
    }

}
