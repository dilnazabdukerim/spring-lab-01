package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.entity.Account;
import kz.iitu.spring_lab_01.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("Account IDs are required");
        }

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException(
                    "Cannot transfer money to the same account"
            );
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }

        Account from = accountRepository.findById(fromId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Source account not found: " + fromId
                        ));

        Account to = accountRepository.findById(toId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Destination account not found: " + toId
                        ));

        from.debit(amount);
        to.credit(amount);

        accountRepository.save(from);
        accountRepository.save(to);
    }

    @Transactional(readOnly = true)
    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Account not found: " + id
                        ));
    }
    @Transactional
    public void transferWithFailure(
            Long fromId,
            Long toId,
            BigDecimal amount
    ) {
        Account from = accountRepository.findById(fromId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Source account not found"));

        Account to = accountRepository.findById(toId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Destination account not found"));

        from.debit(amount);

        accountRepository.save(from);

        throw new IllegalStateException("Simulated failure for rollback test");
    }
}