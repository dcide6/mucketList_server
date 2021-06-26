package com.siksaurus.yamstack.yam.domain.repository;

import com.siksaurus.yamstack.yam.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
