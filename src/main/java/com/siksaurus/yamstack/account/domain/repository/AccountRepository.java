package com.siksaurus.yamstack.account.domain.repository;

import com.siksaurus.yamstack.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByName(String name);
}
