package com.dreamsol.repositories;

import com.dreamsol.entites.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c " +
            "JOIN c.department d " +
            "WHERE (:status IS NULL OR c.status = :status) AND " +
            "(:unitId IS NULL OR c.unitId = :unitId) AND " +
            "(:departmentId IS NULL OR d.id = :departmentId)")
    Page<Contact> findByFilters(@Param("status") Boolean status,
            @Param("unitId") Long unitId,
            @Param("departmentId") Long departmentId,
            Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(long mobileNumber);

    boolean existsByEmployeeId(String employeeId);

    Optional<Contact> findById(Long id);

    Page<Contact> findAll(Pageable pageable);

    Optional<Contact> findByEmployeeIdIgnoreCase(String employeeId);
}
