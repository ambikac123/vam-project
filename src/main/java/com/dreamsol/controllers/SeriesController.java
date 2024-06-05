package com.dreamsol.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        SeriesResponseDto seriesResponseDto = seriesService.createSeries(seriesRequestDto);
        return ResponseEntity.ok(seriesResponseDto);
    }

    @PutMapping("update-series/{id}")
    public ResponseEntity<SeriesResponseDto> updateSeries(@PathVariable Long id,
            @Valid @RequestBody SeriesRequestDto seriesRequestDto) {
        SeriesResponseDto seriesResponseDto = seriesService.updateSeries(id, seriesRequestDto);
        return ResponseEntity.ok(seriesResponseDto);
    }

    @GetMapping("get-series/{id}")
    public ResponseEntity<SeriesResponseDto> getSeriesById(@PathVariable Long id) {
        SeriesResponseDto seriesResponseDto = seriesService.getSeriesById(id);
        return ResponseEntity.ok(seriesResponseDto);
    }

    @GetMapping("get-all-series")
    public ResponseEntity<Page<SeriesResponseDto>> getAllSeries(
            @PageableDefault(size = 10, sort = "plantName", page = 0) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long untiId,
            @RequestParam(required = false) String seriesFor) {
        Page<SeriesResponseDto> seriesResponseDtos = seriesService.getSeries(pageable, status, untiId, seriesFor);
        return ResponseEntity.ok(seriesResponseDtos);
    }

    @DeleteMapping("delete-series/{id}")
    public ResponseEntity<Void> deleteSeries(@PathVariable Long id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.noContent().build();
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
