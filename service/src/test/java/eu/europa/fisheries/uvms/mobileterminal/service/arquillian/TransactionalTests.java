package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class TransactionalTests extends BuildMobileTerminalServiceDeployment {

    @PersistenceContext
    EntityManager em;

    @Inject
    private UserTransaction userTransaction;

    @Before
    public void before() throws SystemException, NotSupportedException {
        userTransaction.begin();
    }

    @After
    public void after() throws SystemException {
        userTransaction.rollback();
    }
}
