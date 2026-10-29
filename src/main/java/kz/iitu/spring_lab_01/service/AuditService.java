package kz.iitu.spring_lab_01.service;

import kz.iitu.spring_lab_01.entity.AuditRecord;
import kz.iitu.spring_lab_01.repository.AuditRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditRecordRepository auditRecordRepository;

    public AuditService(AuditRecordRepository auditRecordRepository) {
        this.auditRecordRepository = auditRecordRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void recordRequired(String message) {
        auditRecordRepository.save(new AuditRecord(message));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordRequiresNew(String message) {
        auditRecordRepository.save(new AuditRecord(message));
    }

    @Transactional(readOnly = true)
    public long countRecords() {
        return auditRecordRepository.count();
    }
}