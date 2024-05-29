package com.dreamsol.repositories;

import com.dreamsol.entites.VehicleLicence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleLicenceRepo extends JpaRepository<VehicleLicence, Long> {

    boolean existsByVehicleNumber(String vehicleNumber);

    Page<VehicleLicence> findByVehicleOwnerContainingIgnoreCase(String vehicleOwner, Pageable pageable);
}
