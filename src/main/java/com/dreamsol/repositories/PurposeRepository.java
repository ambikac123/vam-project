package com.dreamsol.repositories;

import com.dreamsol.entites.Purpose;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, Long> {
    Page<Purpose> findByPurposeForContainingIgnoreCaseOrPurposeBriefContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
            String purposeFor, String purposeBrief, String createdBy, String updatedBy, boolean status,
            LocalDateTime createdAt, LocalDateTime updatedAt, Pageable pageable);

    Optional<Purpose> findByPurposeForContainingIgnoreCase(String vehicleNumber);
}
