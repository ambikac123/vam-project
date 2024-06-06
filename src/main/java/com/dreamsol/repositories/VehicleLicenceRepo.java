package com.dreamsol.repositories;

import com.dreamsol.entites.VehicleLicence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleLicenceRepo extends JpaRepository<VehicleLicence, Long> {

    boolean existsByVehicleNumber(String vehicleNumber);

    Optional<VehicleLicence> findByVehicleNumber(String vehicleNumber);

    Page<VehicleLicence> findByStatus(boolean b, Pageable pageable);
}
