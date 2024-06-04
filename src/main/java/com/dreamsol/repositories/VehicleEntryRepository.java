package com.dreamsol.repositories;

import com.dreamsol.entites.VehicleEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleEntryRepository extends JpaRepository<VehicleEntry, Long> {


   // Page<VehicleEntry> findByLocationFromContainingIgnoreCase(String locationFrom, Pageable pageable);

    Page<VehicleEntry> findAll(Specification<VehicleEntry> spec, Pageable pageable);
}
