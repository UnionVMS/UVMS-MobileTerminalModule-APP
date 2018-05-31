package eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean;

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MobileTerminalDaoBean  {
    @PersistenceContext
    private EntityManager em;


    public MobileTerminal createEntity(MobileTerminal entity)  {
            em.persist(entity);
            return entity;
    }

    public MobileTerminal getEntityById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public MobileTerminal getEntityByGuid(String guid) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
