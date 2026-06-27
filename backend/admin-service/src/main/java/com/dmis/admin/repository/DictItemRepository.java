package com.dmis.admin.repository;

import com.dmis.admin.model.SysDictItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DictItemRepository extends JpaRepository<SysDictItem, Long> {

    Optional<SysDictItem> findByIdAndDeletedFalse(Long id);

    Page<SysDictItem> findByDeletedFalseAndDictTypeId(Long dictTypeId, Pageable pageable);
}
