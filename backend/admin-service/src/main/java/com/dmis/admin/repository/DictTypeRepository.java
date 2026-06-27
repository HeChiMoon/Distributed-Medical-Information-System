package com.dmis.admin.repository;

import com.dmis.admin.model.SysDictType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DictTypeRepository extends JpaRepository<SysDictType, Long> {

    Optional<SysDictType> findByIdAndDeletedFalse(Long id);

    Page<SysDictType> findByDeletedFalse(Pageable pageable);

    Page<SysDictType> findByDeletedFalseAndDictNameContaining(String keyword, Pageable pageable);
}
