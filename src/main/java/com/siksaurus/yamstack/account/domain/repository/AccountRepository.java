package com.siksaurus.yamstack.account.domain.repository;

import com.siksaurus.yamstack.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
