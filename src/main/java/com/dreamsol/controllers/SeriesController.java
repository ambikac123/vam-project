package com.dreamsol.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dreamsol.dtos.requestDtos.SeriesRequestDto;
import com.dreamsol.dtos.responseDtos.SeriesResponseDto;
import com.dreamsol.services.SeriesService;

import java.io.IOException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @PostMapping("/create-series")
    public ResponseEntity<SeriesResponseDto> createPlant(@Valid @RequestBody SeriesRequestDto seriesRequestDto) {
        return seriesService.createSeries(seriesRequestDto);
    }

    @PutMapping("update-series/{id}")
    public ResponseEntity<SeriesResponseDto> updateSeries(@PathVariable Long id,
            @Valid @RequestBody SeriesRequestDto seriesRequestDto) {
        return seriesService.updateSeries(id, seriesRequestDto);
    }

    @GetMapping("get-series/{id}")
    public ResponseEntity<SeriesResponseDto> getSeriesById(@PathVariable Long id) {
        return seriesService.getSeriesById(id);
    }

    @GetMapping("get-all-series")
    public ResponseEntity<?> getAllPurposes(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) String seriesFor) {
        return seriesService.getSeries(seriesFor, pageSize, page, sortBy, sortDirection, status, unitId);
    }

    @DeleteMapping("delete-series/{id}")
    public ResponseEntity<?> deleteSeries(@PathVariable Long id) {
        return seriesService.deleteSeries(id);
    }

    @GetMapping(value = "/download-excel-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelData() {
        return seriesService.downloadSeriesDataAsExcel();
    }

    @GetMapping(value = "/download-excel-sample", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadExcelSample() throws IOException {
        return seriesService.downloadSeriesExcelSample();
    }
}
