package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.entity.Account;
import kz.iitu.spring_lab_01.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class IsolationService {

    private final AccountRepository accountRepository;

    public IsolationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(
            readOnly = true,
            isolation = Isolation.READ_COMMITTED
    )
    public BigDecimal readBalanceReadCommitted(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found"));

        return account.getBalance();
    }

    @Transactional(
            readOnly = true,
            isolation = Isolation.REPEATABLE_READ
    )
    public BigDecimal readBalanceRepeatableRead(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found"));

        return account.getBalance();
    }
}