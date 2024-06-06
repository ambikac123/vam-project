package com.dreamsol.repositories;

import com.dreamsol.entites.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserTypeRepository extends JpaRepository<UserType,Long> {
    Optional<UserType> findByUserTypeNameOrUserTypeCodeAndStatusTrue(String userTypeName, String userTypeCode);

    Optional<UserType> findByIdAndStatusTrue(Long id);

    Page<UserType> findByUserTypeNameContainingIgnoreCaseOrUserTypeCodeContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrCreatedAtOrUpdatedAtOrUnitIdOrStatus(String search, String search1, String search2, String search3, LocalDateTime localDateTime, LocalDateTime localDateTime1, long unitId, boolean status, Pageable pageable);

    Optional<UserType> findByUserTypeNameOrUserTypeCode(String userTypeName, String userTypeCode);

    Optional<UserType> findByUserTypeNameAndStatusTrue(String userTypeName);
}
