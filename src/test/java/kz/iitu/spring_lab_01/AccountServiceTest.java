package kz.iitu.spring_lab_01;

import kz.iitu.spring_lab_01.entity.Account;
import kz.iitu.spring_lab_01.repository.AccountRepository;
import kz.iitu.spring_lab_01.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Long aliceId;
    private Long bobId;

    @BeforeEach
    void setUp() {
        Account alice = accountRepository
                .findByOwnerNameIgnoreCase("Alice")
                .orElseThrow();

        Account bob = accountRepository
                .findByOwnerNameIgnoreCase("Bob")
                .orElseThrow();

        alice.setBalance(new BigDecimal("1000.00"));
        bob.setBalance(new BigDecimal("500.00"));

        accountRepository.saveAndFlush(alice);
        accountRepository.saveAndFlush(bob);

        aliceId = alice.getId();
        bobId = bob.getId();
    }

    @Test
    void transferShouldUpdateBothBalances() {
        accountService.transfer(
                aliceId,
                bobId,
                new BigDecimal("100.00")
        );

        assertEquals(
                0,
                new BigDecimal("900.00")
                        .compareTo(accountService.getAccount(aliceId).getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("600.00")
                        .compareTo(accountService.getAccount(bobId).getBalance())
        );
    }

    @Test
    void transferShouldFailWhenBalanceIsInsufficient() {
        assertThrows(
                IllegalStateException.class,
                () -> accountService.transfer(
                        aliceId,
                        bobId,
                        new BigDecimal("2000.00")
                )
        );

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(accountService.getAccount(aliceId).getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("500.00")
                        .compareTo(accountService.getAccount(bobId).getBalance())
        );
    }

    @Test
    void transferShouldRollbackWhenUnexpectedFailureOccurs() {
        assertThrows(
                IllegalStateException.class,
                () -> accountService.transferWithFailure(
                        aliceId,
                        bobId,
                        new BigDecimal("100.00")
                )
        );

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(accountService.getAccount(aliceId).getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("500.00")
                        .compareTo(accountService.getAccount(bobId).getBalance())
        );
    }
}