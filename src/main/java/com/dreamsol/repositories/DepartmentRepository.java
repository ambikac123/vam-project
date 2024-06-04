package com.dreamsol.repositories;

import com.dreamsol.entites.Department;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

        Page<Department> findByStatusAndUnitIdAndDepartmentName(boolean status, Long unitId, String departmentName,
                        Pageable pageable);

        Page<Department> findByUnitIdAndDepartmentName(Long unitId, String departmentName, Pageable pageable);

        Optional<Department> findByDepartmentCode(String departmentCode);

        Optional<Department> findByDepartmentNameIgnoreCase(String departmentName);

        Optional<Department> findByDepartmentNameIgnoreCaseOrDepartmentCodeIgnoreCase(String departmentName,
                        String departmentCode);

        Optional<Department> findByDepartmentNameIgnoreCaseAndDepartmentCodeIgnoreCase(String departmentName,
                        String departmentCode);

}
