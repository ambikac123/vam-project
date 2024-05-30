package com.dreamsol.repositories;

import com.dreamsol.entites.Series;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long>, JpaSpecificationExecutor<Series> {
    Page<Series> findBySeriesForContainingIgnoreCaseOrPrefixContainingIgnoreCaseOrSubPrefixContainingIgnoreCaseOrCreatedByContainingIgnoreCaseOrUpdatedByContainingIgnoreCaseOrStatusOrCreatedAtOrUpdatedAt(
            String seriesFor, String prefix, String subPrefix, String createdBy, String updatedBy, boolean status,
            LocalDateTime createdAt, LocalDateTime updatedAt, Pageable pageable);

    boolean existsBySeriesFor(String seriesFor);

    Optional<List<Series>> findBySeriesForIgnoreCaseAndPrefixIgnoreCase(String seriesFor, String prefix);

    boolean existsByPrefix(String prefix);
}
