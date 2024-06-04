package com.dreamsol.repositories;

import com.dreamsol.entites.Unit;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    Page<Unit> findByStatus(Pageable pageable, boolean bool);

    Optional<Unit> findByUnitNameIgnoreCaseOrUnitIp(String unitName, String unitIp);

    Optional<Unit> findByUnitNameIgnoreCaseAndUnitIp(String unitName, String unitIp);

}
