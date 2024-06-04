package com.dreamsol.repositories;

import com.dreamsol.entites.Series;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    Page<Series> findBySeriesForAndUnitIdAndStatus(Pageable pageable, String seriesFor, Long unitId, boolean status);

    Page<Series> findBySeriesForAndUnitId(Pageable pageable, String seriesFor, Long unitId);

    boolean existsBySeriesFor(String seriesFor);

    Optional<List<Series>> findBySeriesForIgnoreCaseAndSubPrefixIgnoreCase(String seriesFor, String subPrefix);

    boolean existsByPrefix(String prefix);
}
