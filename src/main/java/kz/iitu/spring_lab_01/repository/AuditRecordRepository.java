package kz.iitu.spring_lab_01.repository;

import kz.iitu.spring_lab_01.entity.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRecordRepository
        extends JpaRepository<AuditRecord, Long> {
}