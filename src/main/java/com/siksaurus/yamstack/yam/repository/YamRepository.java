package com.siksaurus.yamstack.yam.repository;

import com.siksaurus.yamstack.yam.domain.Yam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YamRepository extends JpaRepository<Yam, Integer> {
}
