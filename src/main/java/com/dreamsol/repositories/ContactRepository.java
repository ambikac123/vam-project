package com.dreamsol.repositories;

import com.dreamsol.entites.Contact;
import com.dreamsol.entites.Department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    boolean existsByEmail(String email);

    boolean existsByMobileNumber(long mobileNumber);

    boolean existsByEmployeeId(String employeeId);

    Optional<Contact> findById(Long id);

    Page<Contact> findAll(Pageable pageable);

    Page<Contact> findByStatusAndUnitIdAndDepartment(boolean bool, Long unitId, Department department,
            Pageable pageable);

    Page<Contact> findByUnitIdAndDepartment(Long unitId, Department department, Pageable pageable);

    Optional<Contact> findByEmployeeIdAndStatusTrue(String employeeId);
}
