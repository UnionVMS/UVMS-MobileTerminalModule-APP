package eu.europa.ec.fisheries.uvms.mobileterminal.arquillian;


import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class TransactionalTests extends BuildMobileTerminalDeployment {

    @Inject
    private UserTransaction userTransaction;

    @PersistenceContext(unitName = "mobileTerminalPU")
    protected EntityManager em;

    @Before
    public void before() throws SystemException, NotSupportedException {
        userTransaction.begin();
    }

    @After
    public void after() throws SystemException {
        userTransaction.rollback();
    }
}
