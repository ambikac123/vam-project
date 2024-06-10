package com.dreamsol.repositories;

import com.dreamsol.dtos.responseDtos.PurposeCountDto;
import com.dreamsol.entites.Purpose;
import com.dreamsol.entites.VehicleEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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

    @Query("SELECT COUNT(v) FROM VehicleEntry v WHERE v.status = true")
    Long countTotalEntries();

    @Query("SELECT COUNT(v) FROM VehicleEntry v WHERE v.status = true AND v.purpose.status = true")
    Long countInEntries();

    @Query("SELECT COUNT(v) FROM VehicleEntry v WHERE v.status = true AND v.purpose.status = false")
    Long countOutEntries();

    @Query("SELECT v.purpose.purposeFor AS purpose, COUNT(v.purpose) AS count " +
            "FROM VehicleEntry v " +
            "WHERE v.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY v.purpose.purposeFor")
    List<PurposeCountDto> findPurposesWithinDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}