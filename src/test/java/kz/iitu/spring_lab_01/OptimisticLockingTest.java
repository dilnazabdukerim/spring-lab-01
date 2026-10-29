package kz.iitu.spring_lab_01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kz.iitu.spring_lab_01.entity.Account;
import kz.iitu.spring_lab_01.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OptimisticLockingTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void staleUpdateShouldFail() {
        Long accountId = accountRepository
                .findByOwnerNameIgnoreCase("Alice")
                .orElseThrow()
                .getId();

        TransactionTemplate transaction =
                new TransactionTemplate(transactionManager);

        // Transaction 1: load the current version.
        Account staleAccount = transaction.execute(status ->
                entityManager.find(Account.class, accountId)
        );

        if (staleAccount == null) {
            throw new IllegalStateException("Account not found");
        }

        // Transaction 2: update and commit a newer version.
        transaction.executeWithoutResult(status -> {
            Account currentAccount =
                    entityManager.find(Account.class, accountId);

            currentAccount.setBalance(
                    currentAccount.getBalance().add(new BigDecimal("1.00"))
            );
            entityManager.flush();
        });

        // Transaction 3: try to save the stale version.
        staleAccount.setBalance(new BigDecimal("800.00"));

        assertThrows(
                RuntimeException.class,
                () -> transaction.executeWithoutResult(status -> {
                    entityManager.merge(staleAccount);
                    entityManager.flush();
                })
        );
    }
}