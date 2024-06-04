package com.dreamsol.repositories;

import com.dreamsol.entites.Plant;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long>, JpaSpecificationExecutor<Plant> {

    Page<Plant> findByStatusAndUnitId(Pageable pageable, boolean status, Long unitId);

    Page<Plant> findByUnitId(Pageable pageable, Long unitId);

    Optional<Plant> findByPlantNameContainingIgnoreCase(String vehicleNumber);

    Optional<Plant> findByPlantNameIgnoreCase(String plantName);
}
