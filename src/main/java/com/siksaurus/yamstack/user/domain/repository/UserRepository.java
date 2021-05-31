package com.siksaurus.yamstack.user.domain.repository;

import com.siksaurus.yamstack.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
