package kz.iitu.spring_lab_01;

import kz.iitu.spring_lab_01.service.AuditService;
import kz.iitu.spring_lab_01.service.PropagationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PropagationServiceTest {

    @Autowired
    private PropagationService propagationService;

    @Autowired
    private AuditService auditService;

    @BeforeEach
    void cleanAuditRecords() {
        // Записи аудита проверяются относительно начального количества.
    }

    @Test
    void requiredAuditShouldRollbackWithOuterTransaction() {
        long before = auditService.countRecords();

        assertThrows(
                IllegalStateException.class,
                () -> propagationService.runRequiredExperiment()
        );

        assertEquals(before, auditService.countRecords());
    }

    @Test
    void requiresNewAuditShouldRemainAfterOuterRollback() {
        long before = auditService.countRecords();

        assertThrows(
                IllegalStateException.class,
                () -> propagationService.runRequiresNewExperiment()
        );

        assertEquals(before + 1, auditService.countRecords());
    }
}