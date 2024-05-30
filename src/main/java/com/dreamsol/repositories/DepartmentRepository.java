package com.dreamsol.repositories;

import com.dreamsol.entites.Department;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Page<Department> findByDepartmentNameContainingIgnoreCaseOrDepartmentCodeContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
            String departmentName, String departmentCode, String createdBy, String updatedBy, boolean parsedStatus,
            LocalDateTime parsedDateTime, LocalDateTime parsedDateTime2, Pageable pageable);

    Optional<Department> findByDepartmentCode(String departmentCode);

    Optional<Department> findByDepartmentNameIgnoreCaseOrDepartmentCodeIgnoreCase(String departmentName,
            String departmentCode);

    Optional<Department> findByDepartmentNameIgnoreCaseAndDepartmentCodeIgnoreCase(String departmentName,
            String departmentCode);

}
