package com.dreamsol.repositories;

import com.dreamsol.entites.VehicleEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleEntryRepository extends JpaRepository<VehicleEntry, Long> {

    Page<VehicleEntry> findByStatusAndUnitId(boolean bool, Long unitId, Pageable pageable);

    Page<VehicleEntry> findByStatus(boolean bool, Pageable pageable);

    Page<VehicleEntry> findByUnitId(Long unitId, Pageable pageable);
}
