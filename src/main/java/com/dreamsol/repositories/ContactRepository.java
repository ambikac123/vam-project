package com.dreamsol.repositories;

import com.dreamsol.entites.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    boolean existsByEmail(String email);

    boolean existsByMobileNumber(long mobileNumber);

    boolean existsByEmployeeId(String employeeId);

    Optional<Contact> findById(Long id);

    Page<Contact> findByEmployeeIdContainingIgnoreCaseOrMobileNumberOrEmailContainingIgnoreCaseOrContactNameContainingIgnoreCaseOrCommunicationNameContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
            String employeeId, long mobileNumber, String email, String contactName, String communicationName,
            String createdBy, String updatedBy,
            Boolean status, LocalDateTime createdAt, LocalDateTime updatedAt, Pageable pageable);

    Page<Contact> findAll(Pageable pageable);
}
