package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropagationService {

    private final AuditService auditService;
    private final AccountRepository accountRepository;

    public PropagationService(
            AuditService auditService,
            AccountRepository accountRepository
    ) {
        this.auditService = auditService;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void runRequiredExperiment() {
        accountRepository.count();

        auditService.recordRequired("Audit with REQUIRED");

        throw new IllegalStateException(
                "Simulated outer transaction failure"
        );
    }

    @Transactional
    public void runRequiresNewExperiment() {
        accountRepository.count();

        auditService.recordRequiresNew("Audit with REQUIRES_NEW");

        throw new IllegalStateException(
                "Simulated outer transaction failure"
        );
    }
}