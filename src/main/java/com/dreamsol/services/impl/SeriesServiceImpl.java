package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.SeriesRequestDto;
import com.dreamsol.dtos.responseDtos.DropDownDto;
import com.dreamsol.dtos.responseDtos.SeriesResponseDto;
import com.dreamsol.entites.Series;
import com.dreamsol.repositories.SeriesRepository;
import com.dreamsol.securities.JwtUtil;
import com.dreamsol.services.SeriesService;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {

    private final SeriesRepository seriesRepository;
    private final ExcelUtility excelUtility;
    private final JwtUtil jwtUtil;

    private int num;

    @Override
    public ResponseEntity<SeriesResponseDto> createSeries(SeriesRequestDto seriesRequestDto) {
        num = 0;
        Series series = DtoUtilities.seriesRequestDtoToSeries(seriesRequestDto);
        Optional<List<Series>> dbSeries = seriesRepository
                .findBySeriesForIgnoreCaseAndSubPrefixIgnoreCase(seriesRequestDto.getSeriesFor(),
                        seriesRequestDto.getSubPrefix());
        if (dbSeries.isPresent() && dbSeries.get().size() > 0) {
            dbSeries.get().stream()
                    .forEach(numSeries -> num = numSeries.getNumberSeries() > num ? numSeries.getNumberSeries() : num);
            series.setNumberSeries(num + 1);
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            series.setCreatedBy(jwtUtil.getCurrentLoginUser());
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            series.setStatus(true);
            Series savedSeries = seriesRepository.save(series);
            return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(savedSeries));
        } else {
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            series.setCreatedBy(jwtUtil.getCurrentLoginUser());
            series.setStatus(true);
            Series savedSeries = seriesRepository.save(series);
            return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(savedSeries));
        }
    }

    @Override
    public ResponseEntity<SeriesResponseDto> updateSeries(Long id, SeriesRequestDto seriesRequestDto) {
        num = 0;
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        Optional<List<Series>> dbSeries = seriesRepository
                .findBySeriesForIgnoreCaseAndSubPrefixIgnoreCase(seriesRequestDto.getSeriesFor(),
                        series.getSubPrefix());
        if (dbSeries.isPresent()) {
            dbSeries.get().stream()
                    .forEach(numSeries -> num = numSeries.getNumberSeries() > num ? numSeries.getNumberSeries() : num);
            series.setNumberSeries(num + 1);
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            Series savedSeries = seriesRepository.save(series);
            return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(savedSeries));
        } else {
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            Series savedSeries = seriesRepository.save(series);
            return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(savedSeries));
        }
    }

    @Override
    public ResponseEntity<SeriesResponseDto> getSeriesById(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        return ResponseEntity.ok().body(DtoUtilities.seriesToSeriesResponseDto(series));
    }

    @Override
    public ResponseEntity<?> getSeries(String purposeFor, int pageSize, int page, String sortBy, String sortDirection,
            String status,
            Long unitId) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("Asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
        Boolean statusBoolean = status != null ? Boolean.parseBoolean(status) : null;

        Page<Series> seriesPage = seriesRepository.findByStatusAndUnitIdAndseriesName(statusBoolean, unitId,
                purposeFor, pageRequest);

        Page<SeriesResponseDto> seriesResponseDtos = seriesPage.map(DtoUtilities::seriesToSeriesResponseDto);
        return ResponseEntity.ok(seriesResponseDtos);
    }

    @Override
    public ResponseEntity<?> deleteSeries(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        if (!series.isStatus()) {
            throw new ResourceNotFoundException("Series", "Id", id);
        } else {
            series.setStatus(false);
            series.setUpdatedAt(LocalDateTime.now());
            series.setUpdatedBy(jwtUtil.getCurrentLoginUser());
            seriesRepository.save(series);
            return ResponseEntity.ok().body("Series Deleted Successfully");
        }
    }

    @Override
    public ResponseEntity<?> downloadSeriesDataAsExcel() {
        try {
            List<Series> seriesList = seriesRepository.findAll();
            if (seriesList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No series available!");
            }

            List<SeriesResponseDto> seriesResDtoList = seriesList.stream()
                    .map(DtoUtilities::seriesToSeriesResponseDto)
                    .collect(Collectors.toList());

            String fileName = "series_excel_data.xlsx";
            String sheetName = fileName.substring(0, fileName.indexOf('.'));
            Resource resource = excelUtility.downloadDataAsExcel(seriesResDtoList, sheetName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error! " + e);
        }
    }

    @Override
    public ResponseEntity<?> downloadSeriesExcelSample() throws IOException {
        String fileName = "series_excel_sample.xlsx";
        String sheetName = fileName.substring(0, fileName.indexOf('.'));
        Resource resource = excelUtility.downloadExcelSample(SeriesRequestDto.class, sheetName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
    }

    public ResponseEntity<?> getDropDown() {
        List<Series> series = seriesRepository.findAll();
        return ResponseEntity.ok(series.stream().map(this::seriesToDropDownRes).collect(Collectors.toList()));
    }

    private DropDownDto seriesToDropDownRes(Series series) {
        DropDownDto dto = new DropDownDto();
        dto.setId(series.getId());
        dto.setName(series.getSeriesFor());
        return dto;
    }
}
