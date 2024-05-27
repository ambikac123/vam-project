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

    Page<Unit> findByUnitNameContainingIgnoreCaseOrUnitIpContainingIgnoreCaseOrUnitCityContainingIgnoreCaseOrPassAddressContainingIgnoreCaseOrPassDisclaimerContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
            String search, String search2, String search3, String search4, String search5, boolean parsedStatus,
            LocalDateTime parsedDateTime, LocalDateTime parsedDateTime2, Pageable pageable);

    Optional<Unit> findByUnitName(String unitName);
}
