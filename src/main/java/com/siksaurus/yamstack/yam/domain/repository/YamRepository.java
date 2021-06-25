package com.siksaurus.yamstack.yam.domain.repository;

import com.siksaurus.yamstack.yam.domain.Yam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YamRepository extends JpaRepository<Yam, Long> {
    List<Yam> findByAccount_Email(String email);

    Page<Yam> findByAccount_Email(String email, Pageable pageable);
}
