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
            String search, String search2, String search3, String search4, boolean parsedStatus,
            LocalDateTime parsedDateTime, LocalDateTime parsedDateTime2, Pageable pageable);

    Optional<Department> findByDepartmentCode(String departmentCode);
}
