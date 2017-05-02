package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;


import eu.europa.ec.fisheries.uvms.mobileterminal.dao.OceanRegionDao;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.OceanRegion;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

@RunWith(Arquillian.class)
public class OceanRegionDaoBeanIntTest extends TransactionalTests {

    @EJB
    private OceanRegionDao oceanRegionDao;


    @Test
    @OperateOnDeployment("normal")
    public void testGetOceanRegionList() throws ConfigDaoException {
        List<OceanRegion> oceanRegions = oceanRegionDao.getOceanRegionList();
        Assert.assertNotNull(oceanRegions);
        Assert.assertTrue(oceanRegions.size() > 0);
    }

}
