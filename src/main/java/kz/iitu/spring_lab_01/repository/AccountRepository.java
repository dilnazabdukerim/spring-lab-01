package kz.iitu.spring_lab_01.repository;

import kz.iitu.spring_lab_01.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByOwnerNameIgnoreCase(String ownerName);
}