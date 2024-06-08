package com.dreamsol.repositories;

import com.dreamsol.entites.VehicleEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleEntryRepository extends JpaRepository<VehicleEntry, Long> {

    @Query("SELECT ve FROM VehicleEntry ve " +
            "JOIN ve.plant p " +
            "JOIN ve.purpose ps " +
            "WHERE (:status IS NULL OR ve.status = :status) " +
            "AND (:unitId IS NULL OR ve.unitId = :unitId) " +
            "AND (:plantId IS NULL OR p.id = :plantId) " +
            "AND (:purposeId IS NULL OR ps.id = :purposeId)")
    Page<VehicleEntry> findByParameters(
            @Param("status") Boolean status,
            @Param("unitId") Long unitId,
            @Param("plantId") Long plantId,
            @Param("purposeId") Long purposeId,
            Pageable pageable);
}
