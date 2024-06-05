package com.dreamsol.repositories;

import com.dreamsol.entites.DrivingLicence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrivingLicenceRepo extends JpaRepository<DrivingLicence,Long> {
    Optional<DrivingLicence> findByLicence(String licence);

    Optional<DrivingLicence> findByDriverMobile(Long driverMobile);

    Page<DrivingLicence> findByStatus(boolean status, Pageable pageable);

    Page<DrivingLicence> findByStatusAndUnitId(boolean bool, Long unitId, Pageable pageable);
}
