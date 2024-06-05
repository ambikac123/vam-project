package com.dreamsol.services.impl;

import com.dreamsol.dtos.requestDtos.SeriesRequestDto;
import com.dreamsol.dtos.responseDtos.SeriesResponseDto;
import com.dreamsol.entites.Series;
import com.dreamsol.repositories.SeriesRepository;
import com.dreamsol.services.SeriesService;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.utility.DtoUtilities;
import com.dreamsol.utility.ExcelUtility;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private int num;

    @Override
    public SeriesResponseDto createSeries(SeriesRequestDto seriesRequestDto) {
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
            Series savedSeries = seriesRepository.save(series);
            return DtoUtilities.seriesToSeriesResponseDto(savedSeries);
        } else {
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            Series savedSeries = seriesRepository.save(series);
            return DtoUtilities.seriesToSeriesResponseDto(savedSeries);
        }
    }

    @Override
    public SeriesResponseDto updateSeries(Long id, SeriesRequestDto seriesRequestDto) {
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
            Series savedSeries = seriesRepository.save(series);
            return DtoUtilities.seriesToSeriesResponseDto(savedSeries);
        } else {
            series.setPrefix(series.getSeriesFor().substring(0, 3).toUpperCase());
            Series savedSeries = seriesRepository.save(series);
            return DtoUtilities.seriesToSeriesResponseDto(savedSeries);
        }
    }

    @Override
    public SeriesResponseDto getSeriesById(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        return DtoUtilities.seriesToSeriesResponseDto(series);
    }

    @Override
    public Page<SeriesResponseDto> getSeries(Pageable pageable, String status, Long unitId, String seriesFor) {

        boolean bool = false;
        if (status != null) {
            try {
                bool = Boolean.parseBoolean(status);
            } catch (Exception e) {
                return seriesRepository.findBySeriesForAndUnitId(pageable, seriesFor, unitId)
                        .map(DtoUtilities::seriesToSeriesResponseDto);
            }
            return seriesRepository.findBySeriesForAndUnitIdAndStatus(pageable, seriesFor, unitId, bool)
                    .map(DtoUtilities::seriesToSeriesResponseDto);
        }

        return seriesRepository.findAll(pageable).map(DtoUtilities::seriesToSeriesResponseDto);

    }

    @Override
    public void deleteSeries(Long id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Series", "Id", id));
        if (!series.isStatus()) {
            throw new ResourceNotFoundException("Series", "Id", id);
        } else {
            series.setStatus(false);
            series.setUpdatedAt(LocalDateTime.now());
            seriesRepository.save(series);
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
}
