package com.siksaurus.yamstack.account.domain.repository;

import com.siksaurus.yamstack.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findById(String id);
    Optional<Account> findByName(String name);
}
