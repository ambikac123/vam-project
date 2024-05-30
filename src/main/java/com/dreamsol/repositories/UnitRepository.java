package com.dreamsol.repositories;

import com.dreamsol.entites.Unit;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    Page<Unit> findByUnitNameContainingIgnoreCaseOrUnitIpContainingIgnoreCaseOrUnitCityContainingIgnoreCaseOrPassAddressContainingIgnoreCaseOrPassDisclaimerContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
            String unitName, String unitIp, String passDisclaimer, String unitCity, String passAddress,
            String createdBy, String updatedBy, boolean parsedStatus,
            LocalDateTime parsedDateTime, LocalDateTime parsedDateTime2, Pageable pageable);

    Optional<Unit> findByUnitNameIgnoreCaseOrUnitIp(String unitName, String unitIp);

    Optional<Unit> findByUnitNameIgnoreCaseAndUnitIp(String unitName, String unitIp);

}
