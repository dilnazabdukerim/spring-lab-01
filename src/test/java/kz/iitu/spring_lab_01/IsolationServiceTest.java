package kz.iitu.spring_lab_01;

import kz.iitu.spring_lab_01.entity.Account;
import kz.iitu.spring_lab_01.repository.AccountRepository;
import kz.iitu.spring_lab_01.service.IsolationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class IsolationServiceTest {

    @Autowired
    private IsolationService isolationService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldReadExistingAccountBalance() {
        Account alice = accountRepository
                .findByOwnerNameIgnoreCase("Alice")
                .orElseThrow();

        Long aliceId = alice.getId();
        BigDecimal expectedBalance = alice.getBalance();

        BigDecimal actualBalance = isolationService
                .readBalanceReadCommitted(aliceId);

        assertEquals(
                0,
                expectedBalance.compareTo(actualBalance)
        );
    }
}