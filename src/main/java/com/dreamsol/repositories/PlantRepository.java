package com.dreamsol.repositories;

import com.dreamsol.entites.Plant;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long>, JpaSpecificationExecutor<Plant> {
    Page<Plant> findByPlantNameContainingIgnoreCaseOrPlantBriefContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
            String plantName, String plantBrief, String createdBy, String updatedBy, boolean status,
            LocalDateTime createdAt, LocalDateTime updatedAt, Pageable pageable);

    Optional<Plant> findByPlantNameContainingIgnoreCase(String vehicleNumber);
    Optional<Plant> findByPlantNameIgnoreCase(String plantName);
}
