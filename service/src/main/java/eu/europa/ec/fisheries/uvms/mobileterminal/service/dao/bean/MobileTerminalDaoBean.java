package eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.bean;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;

@Stateless
public class MobileTerminalDaoBean extends Dao {

    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalDaoBean.class);

    public MobileTerminal createEntity(MobileTerminal entity) throws ConfigDaoException {
        try {
            em.persist(entity);
            return entity;
        } catch (Exception e) {
            LOG.error("[ Error when creating MobileTerminal: " + entity + " ] " + e.getMessage());
            throw new ConfigDaoException("[ Error when creating MobileTerminal: " + entity + " ] ");
        }
    }

    public MobileTerminal getEntityById(Integer id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public MobileTerminal getEntityByGuid(String guid) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
