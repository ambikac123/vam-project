package com.dreamsol.repositories;

import com.dreamsol.entites.Purpose;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, Long> {

    Page<Purpose> findByPurposeForAndUnitIdAndStatus(Pageable pageable, String purposeFor, Long unitId, boolean status);

    Page<Purpose> findByPurposeForAndUnitId(Pageable pageable, String purposeFor, Long unitId);

    Optional<Purpose> findByPurposeForContainingIgnoreCase(String purposeFor);
}
