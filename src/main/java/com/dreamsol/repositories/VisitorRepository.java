package com.dreamsol.repositories;

import com.dreamsol.entites.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

        @Query("SELECT v FROM Visitor v JOIN v.purpose p JOIN v.department d JOIN v.user u WHERE " +
                        "(:userId IS NULL OR u.id = :userId) AND " +
                        "(:purposeId IS NULL OR p.id = :purposeId) AND " +
                        "(:departmentId IS NULL OR d.id = :departmentId) AND " +
                        "(:unitId IS NULL OR v.unitId = :unitId) AND " +
                        "(:status IS NULL OR v.status = :status)")
        Page<Visitor> findByEmployeeIdAndPurposeIdAndDepartmentIdAndUnitIdAndStatus(
                        @Param("userId") Long userId,
                        @Param("purposeId") Long purposeId,
                        @Param("departmentId") Long departmentId,
                        @Param("unitId") Long unitId,
                        @Param("status") Boolean status,
                        Pageable pageable);

        @Query("SELECT v FROM Visitor v JOIN v.purpose p JOIN v.department d JOIN v.user u WHERE " +
                        "(:userId IS NULL OR u.id = :userId) AND " +
                        "(:purposeId IS NULL OR p.id = :purposeId) AND " +
                        "(:departmentId IS NULL OR d.id = :departmentId) AND " +
                        "(:unitId IS NULL OR v.unitId = :unitId) AND " +
                        "(:status IS NULL OR v.status = :status)")
        List<Visitor> findByEmployeeIdAndPurposeIdAndDepartmentIdAndUnitIdAndStatus(
                        @Param("userId") Long userId,
                        @Param("purposeId") Long purposeId,
                        @Param("departmentId") Long departmentId,
                        @Param("unitId") Long unitId,
                        @Param("status") Boolean status);

        // @Query("SELECT COUNT(v) FROM Visitor v JOIN v.purpose p JOIN v.department d
        // JOIN v.user u WHERE " +
        // "(:userId IS NULL OR u.id = :userId) AND " +
        // "(:purposeId IS NULL OR p.id = :purposeId) AND " +
        // "(:departmentId IS NULL OR d.id = :departmentId) AND " +
        // "(:unitId IS NULL OR v.unitId = :unitId) AND " +
        // "(:status IS NULL OR v.status = :status) AND " +
        // "((:fromDate IS NULL OR :toDate IS NULL) OR (v.createdAT BETWEEN :fromDate
        // AND :toDate))")
        // Long countVisitors(
        // @Param("userId") Long userId,
        // @Param("purposeId") Long purposeId,
        // @Param("departmentId") Long departmentId,
        // @Param("unitId") Long unitId,
        // @Param("status") Boolean status,
        // @Param("fromDate") LocalDateTime fromDate,
        // @Param("toDate") LocalDateTime toDate);

}
